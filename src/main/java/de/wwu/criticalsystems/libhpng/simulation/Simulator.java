package de.wwu.criticalsystems.libhpng.simulation;

import java.util.Random;
import java.util.logging.Logger;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.SimulationEvent.SimulationEventType;

public class Simulator {
	
	
	public Simulator(HPnGModel model, Double maxTime) {
		this.model = model;
		this.maxTime = maxTime;
	}
	
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	
	protected SimulationEvent event;
	protected HPnGModel model;
	protected Double maxTime;
	protected Logger logger;

	
	public Double getAndCompleteNextEvent(Double currentTime, MarkingPlot currentPlot, Boolean printRunResults) throws InvalidRandomVariateGeneratorException, InvalidPropertyException{
		
		getNextEvent(currentTime);
		
		
		//complete event and update model marking
		if (maxTime < event.getOccurenceTime() || event.getEventType().equals(SimulationEventType.no_event)){
			if (maxTime- currentTime > 0.0)
				model.advanceMarking(maxTime- currentTime);
			
			model.updateEnabling(false);
			model.updateFluidRates(maxTime);
			currentPlot.saveAll(maxTime);
			
			
		} else {
			if (event.getOccurenceTime() - currentTime > 0.0)
				model.advanceMarking(event.getOccurenceTime() - currentTime);
				
			completeEvent(printRunResults, currentPlot);
			
			if (printRunResults) 
				model.printCurrentMarking(false, false);
		}
		
	
		return event.getOccurenceTime(); 		
	}
	
	
	protected void completeEvent(Boolean printRunResults, MarkingPlot currentPlot) throws InvalidRandomVariateGeneratorException{
	
		if (event.getEventType().equals(SimulationEventType.immediate_transition) || event.getEventType().equals(SimulationEventType.deterministic_transition) || event.getEventType().equals(SimulationEventType.general_transition)) {
			
			//transition firing
			Transition transition = conflictResolutionByTransitionWeight();		
			transition.fireTransition();
			model.checkGuardArcsForDiscretePlaces();
			transition.setEnabled(false);
			
			if (event.getEventType().equals(SimulationEventType.general_transition)){
				if (printRunResults) System.out.println(event.getOccurenceTime() + " seconds: General transition " + transition.getId() + " is fired for the " + ((GeneralTransition)transition).getFirings() + ". time");
			} else if (event.getEventType().equals(SimulationEventType.immediate_transition)){
				if (printRunResults) System.out.println(event.getOccurenceTime() + " seconds: Immediate transition " + transition.getId() + " is fired");
			} else if (event.getEventType().equals(SimulationEventType.deterministic_transition)){
				if (printRunResults) System.out.println(event.getOccurenceTime() + " seconds: Deterministic transition " + transition.getId() + " is fired");
			}
	
		} else if (event.getEventType().equals(SimulationEventType.guard_arcs_immediate) || event.getEventType().equals(SimulationEventType.guard_arcs_continuous) || event.getEventType().equals(SimulationEventType.guard_arcs_deterministic)){
			
			//guard arc condition
			GuardArc arc;
			for (Object object: event.getRelatedObjects()){
				
				arc = (GuardArc)object;
				Boolean fulfilled = arc.checkCondition();
				
				if (printRunResults && fulfilled && !arc.getInhibitor()) 
					System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
				else if (printRunResults && !fulfilled && !arc.getInhibitor())
					System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());
				else if (printRunResults && fulfilled && arc.getInhibitor()) 
					System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
				else if (printRunResults && !fulfilled && arc.getInhibitor())
					System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());									
			}			
		} else if (event.getEventType().equals(SimulationEventType.place_boundary)){
			
			//place boundary reached
			ContinuousPlace place;
			for (Object object: event.getRelatedObjects()){				
				
				place = (ContinuousPlace)object;
				place.setExactFluidLevel(place.getCurrentFluidLevel(), event.getOccurenceTime());
					
				if (place.checkLowerBoundary()){
					place.checkUpperBoundary();
					if (printRunResults)
						System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " is empty");
				} else {
					place.checkUpperBoundary();
					if (printRunResults)
						System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " has reached its upper boundary");
				}	
			}	
		} else if (event.getEventType().equals(SimulationEventType.place_internaltransition)){

			ContinuousPlace place;
			
			//internal transition
			for (Object object: event.getRelatedObjects()){
				
				place = ((ContinuousPlace)object);
				
				Boolean lowerBoundary = place.getLowerBoundaryReached();
				Boolean upperBoundary = false;
				if (!place.getUpperBoundaryInfinity())
					upperBoundary = place.getUpperBoundaryReached();
				
							
				place.performInternalTransition(event.getOccurenceTime(), place.getExactDrift(), place.getChangeOfExactDrift(), model.getArcs(), false);	
				
				if (printRunResults)
					System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " has performed an internal transition");
				
				
				if (!lowerBoundary && place.checkLowerBoundary()){
					place.checkUpperBoundary();
					if (printRunResults)
						System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " is empty");
				} else if (!upperBoundary && place.checkUpperBoundary()) {
					if (printRunResults)
						System.out.println(event.getOccurenceTime() + " seconds: continuous place " + place.getId() + " has reached its upper boundary");
				}
				
	 			
				for (Arc arc: model.getArcs()){

					if (arc.getClass().equals(GuardArc.class) && arc.getConnectedPlace().equals(place)){
					//guard arc starting from fluid place
		 
						if (!((GuardArc)arc).getConditionFulfilled().equals(((GuardArc)arc).checkCondition())){
							Boolean fulfilled = ((GuardArc)arc).getConditionFulfilled();
							
							if (printRunResults && fulfilled && !((GuardArc)arc).getInhibitor()) 
								System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
							else if (printRunResults && !fulfilled && !((GuardArc)arc).getInhibitor())
								System.out.println(event.getOccurenceTime() + " seconds: test arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());
							else if (printRunResults && fulfilled && ((GuardArc)arc).getInhibitor()) 
								System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition fulfilled for transition " + arc.getConnectedTransition().getId());
							else if (printRunResults && !fulfilled && ((GuardArc)arc).getInhibitor())
								System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + arc.getId() + " has its condition stopped being fulfilled for transition " + arc.getConnectedTransition().getId());						
				
						}
					}					
				}
				
				
			}
		}

		//update model status
		model.updateEnabling(false);
		model.updateFluidRates(event.getOccurenceTime());
		if (event.getEventType() != SimulationEventType.place_internaltransition)
			model.computeInternalTransitionsAtEvent();
		
		//plot status
		currentPlot.saveAll(event.getOccurenceTime());		
	}
	
	
	protected void getNextEvent(Double currentTime){
		
		Double timeOfCurrentEvent;		
		event = new SimulationEvent(maxTime);
		
		//check transition events first
		for(Transition transition: model.getTransitions()){
			if (!transition.getEnabled())
				continue;
			
			if (transition.getClass().equals(ImmediateTransition.class)){
				
				if (((ImmediateTransition)transition).getPriority() > event.getPriority()){
					event.setEventType(SimulationEventType.immediate_transition);
					event.setFirstEventItem(transition, ((ImmediateTransition)transition).getPriority());
					event.setOccurenceTime(currentTime);
				} else if (((ImmediateTransition)transition).getPriority().equals(event.getPriority())){
					event.getRelatedObjects().add(transition);
				} else
					break; //if immediate transition with higher priority found, transition loop can be exited here
			} else if (transition.getClass().equals(DeterministicTransition.class)){
			
				if (event.getEventType().equals(SimulationEventType.immediate_transition))
					break;
				
				timeOfCurrentEvent = currentTime + ((DeterministicTransition)transition).getFiringTime() - ((DeterministicTransition)transition).getClock();
				
				if (timeOfCurrentEvent < event.getOccurenceTime() || (timeOfCurrentEvent.equals(event.getOccurenceTime()) && ((DeterministicTransition)transition).getPriority() > event.getPriority())){
					event.setEventType(SimulationEventType.deterministic_transition);
					event.setFirstEventItem(transition, ((DeterministicTransition)transition).getPriority());
					event.setOccurenceTime(timeOfCurrentEvent);
				} else if (timeOfCurrentEvent.equals(event.getOccurenceTime()) && ((DeterministicTransition)transition).getPriority().equals(event.getPriority())){
					event.getRelatedObjects().add(transition);				
				}
			} else if (transition.getClass().equals(GeneralTransition.class)){
				
				if (event.getEventType().equals(SimulationEventType.immediate_transition))
					break;
				
				timeOfCurrentEvent = currentTime + ((GeneralTransition)transition).getDiscreteFiringTime() - ((GeneralTransition)transition).getEnablingTime();
				
				if (timeOfCurrentEvent < event.getOccurenceTime() || (timeOfCurrentEvent.equals(event.getOccurenceTime()) && !(event.getEventType().equals(SimulationEventType.deterministic_transition)) && ((GeneralTransition)transition).getPriority() > event.getPriority())){
					event.setEventType(SimulationEventType.general_transition);
					event.setFirstEventItem(transition, ((GeneralTransition)transition).getPriority());
					event.setOccurenceTime(timeOfCurrentEvent);
				} else if (timeOfCurrentEvent.equals(event.getOccurenceTime()) && ((GeneralTransition)transition).getPriority().equals(event.getPriority())){
					event.getRelatedObjects().add(transition);				
				}
			} else 
				break; //continuous or continuous dynamic transition
		}		
		
		
		if (!event.getEventType().equals(SimulationEventType.immediate_transition)){
			
			//if no immediate transition, check guard arcs next
			for (Arc arc: model.getArcs()){
		
				
				if (arc.getClass().equals(GuardArc.class) && arc.getConnectedPlace().getClass().equals(ContinuousPlace.class)){
				//guard arc starting from fluid place
					 
					ContinuousPlace place = ((ContinuousPlace)arc.getConnectedPlace()); 
					Double timeDelta = -1.0;
					Double timeDeltaQ = -1.0;
					

					timeDelta = place.getTimeTilExactFluidLevelHitsBoundary(arc.getWeight(), currentTime);
					timeDeltaQ = place.getTimeTilCurrentFluidLevelHitsBoundary(arc.getWeight());
					
					if (((timeDelta >= 0.0 && timeDelta < Double.POSITIVE_INFINITY || (timeDeltaQ >= 0.0 && timeDeltaQ < Double.POSITIVE_INFINITY))) && !timeDelta.equals(timeDeltaQ) && (timeDelta < place.getTimeToNextInternalTransition() || timeDeltaQ < place.getTimeToNextInternalTransition())){
						
						if (timeDelta < 0.0)
							timeDelta = Double.POSITIVE_INFINITY;
						
						if (timeDeltaQ < 0.0)
							timeDelta = Double.POSITIVE_INFINITY;
						
						timeOfCurrentEvent = currentTime + Math.min(timeDelta, timeDeltaQ);

						if (timeOfCurrentEvent < event.getOccurenceTime()){
							
							event.setEventType(SimulationEventType.place_internaltransition);							
							event.setFirstEventItem(place, 0);
							event.setOccurenceTime(timeOfCurrentEvent);
								
						} else if (timeOfCurrentEvent.equals(event.getOccurenceTime())) {
							
							if (event.getEventType().equals(SimulationEventType.no_event)){							
								event.setEventType(SimulationEventType.place_internaltransition);
								event.setFirstEventItem(place, 0);							
							} else if (event.getEventType().equals(SimulationEventType.place_internaltransition)){
								event.getRelatedObjects().add(place);
							}							
						}	
						
						
					} else {
					
						if (timeDelta < 0.0 || timeDelta.equals(Double.POSITIVE_INFINITY))
							continue;
						
						if (timeDelta == 0.0){
													
							if(place.getDrift() >= 0.0 && ((GuardArc)arc).getConditionFulfilled().equals(true))
								continue;
							
							if (place.getDrift() < 0.0 && ((GuardArc)arc).getConditionFulfilled().equals(false))							
								continue;						
	
							((GuardArc)arc).setConditionFulfilled(true);
								
							
						}
						
		
						
						timeOfCurrentEvent = currentTime + timeDelta;
						
						if (timeOfCurrentEvent < event.getOccurenceTime()){
											
							if (arc.getConnectedTransition().getClass().equals(ImmediateTransition.class))
								event.setEventType(SimulationEventType.guard_arcs_immediate);
							else if (arc.getConnectedTransition().getClass().equals(ContinuousTransition.class))
								event.setEventType(SimulationEventType.guard_arcs_continuous);
							else
								event.setEventType(SimulationEventType.guard_arcs_deterministic);
								
							event.setFirstEventItem(arc, 0);
							event.setOccurenceTime(timeOfCurrentEvent);
								
						} else if (timeOfCurrentEvent.equals(event.getOccurenceTime())) {
							
							if ((!event.getEventType().equals(SimulationEventType.guard_arcs_immediate) && arc.getConnectedTransition().getClass().equals(ImmediateTransition.class))){
							
								//guard arc for immediate transition replaces other guard arcs
								event.setEventType(SimulationEventType.guard_arcs_immediate);
								event.setFirstEventItem(arc, 0);
								
							} else if (event.getEventType().equals(SimulationEventType.guard_arcs_deterministic) && arc.getConnectedTransition().getClass().equals(ContinuousTransition.class)){ 
								
								//guard arc for continuous transition replaces guard arcs for deterministic/general transitions
								event.setEventType(SimulationEventType.guard_arcs_continuous);
								event.setFirstEventItem(arc, 0);
								
							} else if ((event.getEventType().equals(SimulationEventType.guard_arcs_immediate) && arc.getConnectedTransition().getClass().equals(ImmediateTransition.class))
									|| (event.getEventType().equals(SimulationEventType.guard_arcs_continuous) && arc.getConnectedTransition().getClass().equals(ContinuousTransition.class)) 
									|| (event.getEventType().equals(SimulationEventType.guard_arcs_deterministic) && (arc.getConnectedTransition().getClass().equals(DeterministicTransition.class) || arc.getConnectedTransition().getClass().equals(GeneralTransition.class)))){
								
								//otherwise, if same kind of transitions, add to list
								event.getRelatedObjects().add(arc);
							}
						}
						
					}
				
				} else if (!arc.getClass().equals(GuardArc.class))
					break;
			}
			
			//check continuous places for borders
			for (Place p: model.getPlaces()){				
				if (p.getClass().equals(ContinuousPlace.class)){
					
					ContinuousPlace place = (ContinuousPlace)p;	
					Double timeDelta = -1.0;
					Double timeDeltaQ = -1.0;						
		
			
					if (place.getExactDrift() < 0.0 && !place.getLowerBoundaryReached())
						timeDelta = place.getTimeTilExactFluidLevelHitsBoundary(0.0, currentTime);
					else if (place.getExactDrift() > 0.0 && !place.getUpperBoundaryInfinity() && !place.getUpperBoundaryReached())
						timeDelta = place.getTimeTilExactFluidLevelHitsBoundary(place.getUpperBoundary(), currentTime);
					
					if (place.getDrift() < 0.0 && !place.getLowerBoundaryReached())
						timeDeltaQ = place.getTimeTilCurrentFluidLevelHitsBoundary(0.0);
					else if (place.getDrift() > 0.0 && !place.getUpperBoundaryInfinity() && !place.getUpperBoundaryReached())
						timeDeltaQ = place.getTimeTilCurrentFluidLevelHitsBoundary(place.getUpperBoundary());
					
					

					
					if (((timeDelta >= 0.0 && timeDelta < Double.POSITIVE_INFINITY) || (timeDeltaQ >= 0.0 && timeDeltaQ < Double.POSITIVE_INFINITY)) && !timeDelta.equals(timeDeltaQ) && (timeDelta < place.getTimeToNextInternalTransition() || timeDeltaQ < place.getTimeToNextInternalTransition())){
						
						if (timeDelta < 0.0)
							timeDelta = Double.POSITIVE_INFINITY;
						
						if (timeDeltaQ < 0.0)
							timeDeltaQ = Double.POSITIVE_INFINITY;
						
						timeOfCurrentEvent = currentTime + Math.min(timeDelta, timeDeltaQ);

						if (timeOfCurrentEvent < event.getOccurenceTime()){
							
							event.setEventType(SimulationEventType.place_boundary);							
							event.setFirstEventItem(place, 0);
							event.setOccurenceTime(timeOfCurrentEvent);
								
						} else if (timeOfCurrentEvent.equals(event.getOccurenceTime())) {
							
							if (event.getEventType().equals(SimulationEventType.no_event) || event.getEventType().equals(SimulationEventType.general_transition) || event.getEventType().equals(SimulationEventType.guard_arcs_deterministic) || event.getEventType().equals(SimulationEventType.guard_arcs_continuous)){													
								event.setEventType(SimulationEventType.place_boundary);
								event.setFirstEventItem(place, 0);							
							} else if (event.getEventType().equals(SimulationEventType.place_boundary)){
								event.getRelatedObjects().add(place);
							}							
						}	
						
						
					} else if (timeDelta >= 0.0 && timeDelta <= place.getTimeToNextInternalTransition() && !timeDelta.equals(Double.POSITIVE_INFINITY)){	
						
						timeOfCurrentEvent = currentTime + timeDelta;		
						
						if (timeOfCurrentEvent < event.getOccurenceTime()){
							
							event.setEventType(SimulationEventType.place_boundary);							
							event.setFirstEventItem(place, 0);
							event.setOccurenceTime(timeOfCurrentEvent);
								
						} else if (timeOfCurrentEvent.equals(event.getOccurenceTime())) {
							
							if (event.getEventType().equals(SimulationEventType.no_event) || event.getEventType().equals(SimulationEventType.general_transition) || event.getEventType().equals(SimulationEventType.guard_arcs_deterministic) || event.getEventType().equals(SimulationEventType.guard_arcs_continuous)){							
								event.setEventType(SimulationEventType.place_boundary);
								event.setFirstEventItem(place, 0);							
							} else if (event.getEventType().equals(SimulationEventType.place_boundary)){
								event.getRelatedObjects().add(place);
							}							
						}	
						
						if (timeDelta == 0.0 && place.getCurrentFluidLevel() == 0.0)
							place.checkLowerBoundary();
						else if (timeDelta == 0.0)
							place.checkUpperBoundary();
						
					} else {	
						
						timeOfCurrentEvent = currentTime + place.getTimeToNextInternalTransition();

						if (timeOfCurrentEvent < event.getOccurenceTime() && timeOfCurrentEvent >= currentTime){
							
							event.setEventType(SimulationEventType.place_internaltransition);							
							event.setFirstEventItem(place, 0);
							event.setOccurenceTime(timeOfCurrentEvent);
								
						} else if (timeOfCurrentEvent.equals(event.getOccurenceTime()) && timeOfCurrentEvent >= currentTime) {
							
							if (event.getEventType().equals(SimulationEventType.no_event)){							
								event.setEventType(SimulationEventType.place_internaltransition);
								event.setFirstEventItem(place, 0);							
							} else if (event.getEventType().equals(SimulationEventType.place_internaltransition)){
								event.getRelatedObjects().add(place);
							}							
						}	
						
					}
				}					
			}	
		}
	}
	
	private Transition conflictResolutionByTransitionWeight(){
		
		Double sum = 0.0;
		Double probability = 0.0;
		Double winner =  new Random().nextDouble();
		
		if (event.getEventType().equals(SimulationEventType.immediate_transition)){
			for (Object object : event.getRelatedObjects())
				sum += ((ImmediateTransition)object).getWeight();
			
			for (Object object : event.getRelatedObjects()){
				probability += ((ImmediateTransition)object).getWeight() / sum;
				if (winner < probability)
					return ((Transition)object);
			}	
		} else if (event.getEventType().equals(SimulationEventType.deterministic_transition)){
			for (Object object : event.getRelatedObjects())
				sum += ((DeterministicTransition)object).getWeight();
			
			for (Object object : event.getRelatedObjects()){
				probability += ((DeterministicTransition)object).getWeight() / sum;
				if (winner < probability)
					return ((Transition)object);
			}
		} else if (event.getEventType().equals(SimulationEventType.general_transition)){
			for (Object object : event.getRelatedObjects())
				sum += ((GeneralTransition)object).getWeight();
			
			for (Object object : event.getRelatedObjects()){
				probability += ((GeneralTransition)object).getWeight() / sum;
				if (winner < probability)
					return ((Transition)object);
			}
		}
		
		return null;
	}	
}