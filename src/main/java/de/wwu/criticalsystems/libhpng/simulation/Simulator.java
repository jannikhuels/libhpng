package de.wwu.criticalsystems.libhpng.simulation;

import java.util.Random;
import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.SimulationEvent.SimulationEventType;

public class Simulator {
	
	public Simulator(HPnGModel model, Double maxTime) {
		this.model = model;
		this.maxTime = maxTime;
	}
	
	public Logger getLogger() {
		return logger;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	
	private SimulationEvent event;
	private HPnGModel model;
	private Double maxTime;
	private Logger logger;

	
	public Double getAndCompleteNextEvent(Double currentTime, MarkingPlot currentPlot, Boolean printRunResults){
		
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
				} else if (((ImmediateTransition)transition).getPriority() == event.getPriority()){
					event.getRelatedObjects().add(transition);
				} else
					break; //if immediate transition with higher priority found, transition loop can be exited here
			} else if (transition.getClass().equals(DeterministicTransition.class)){
			
				if (event.getEventType() == SimulationEventType.immediate_transition)
					break;
				
				timeOfCurrentEvent = currentTime + ((DeterministicTransition)transition).getFiringTime() - ((DeterministicTransition)transition).getClock();
				
				if (timeOfCurrentEvent < event.getOccurenceTime() || (timeOfCurrentEvent == event.getOccurenceTime() && ((DeterministicTransition)transition).getPriority() > event.getPriority())){
					event.setEventType(SimulationEventType.deterministic_transition);
					event.setFirstEventItem(transition, ((DeterministicTransition)transition).getPriority());
					event.setOccurenceTime(timeOfCurrentEvent);
				} else if (timeOfCurrentEvent == event.getOccurenceTime() && ((DeterministicTransition)transition).getPriority() == event.getPriority()){
					event.getRelatedObjects().add(transition);				
				}
			} else if (transition.getClass().equals(GeneralTransition.class)){
				
				if (event.getEventType() == SimulationEventType.immediate_transition)
					break;
				
				timeOfCurrentEvent = currentTime + ((GeneralTransition)transition).getDiscreteFiringTime() - ((GeneralTransition)transition).getEnablingTime();
				
				if (timeOfCurrentEvent < event.getOccurenceTime() || (timeOfCurrentEvent == event.getOccurenceTime() && !(event.getEventType() == SimulationEventType.deterministic_transition) && ((GeneralTransition)transition).getPriority() > event.getPriority())){
					event.setEventType(SimulationEventType.general_transition);
					event.setFirstEventItem(transition, ((GeneralTransition)transition).getPriority());
					event.setOccurenceTime(timeOfCurrentEvent);
				} else if (timeOfCurrentEvent == event.getOccurenceTime() && ((GeneralTransition)transition).getPriority() == event.getPriority()){
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
					Double timeDelta=-1.0;
					
					if (place.getDrift() == 0.0){
						if (((GuardArc)arc).getConditionFulfilled().equals(((GuardArc)arc).getInhibitor()) && arc.getWeight().equals(place.getFluidLevel())){
							timeDelta = 0.0;
						}						
					} else 
						timeDelta = (arc.getWeight() - place.getFluidLevel()) / place.getDrift();
				
					if (timeDelta == 0.0 && place.getDrift() < 0.0)
						((GuardArc)arc).setConditionFulfilled(false);				
									
					
					if (timeDelta == 0.0 && !((GuardArc)arc).getConditionFulfilled().equals(((GuardArc)arc).getInhibitor()))
						continue;
					
					if (timeDelta == 0.0 && place.getDrift() < 0.0 && ((GuardArc)arc).getInhibitor())
						continue;
					
					if (timeDelta < 0.0 
							|| (timeDelta == 0.0) && ((place.getDrift() > 0.0 && ((GuardArc)arc).getConditionFulfilled()) || (place.getDrift() < 0.0 && !((GuardArc)arc).getConditionFulfilled()))) 
						continue;
					
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
							
					} else if (timeOfCurrentEvent == event.getOccurenceTime()) {
						
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
				
				} else if (!arc.getClass().equals(GuardArc.class))
					break;
			}
			
			//check continuous places for borders
			for (Place p: model.getPlaces()){				
				if (p.getClass().equals(ContinuousPlace.class)){
					
					ContinuousPlace place = (ContinuousPlace)p;					
					Double timeDelta;
					if (place.getDrift() < 0.0 && !place.getLowerBoundaryReached())
						timeDelta = Math.abs(place.getFluidLevel() / place.getDrift());
					else if (place.getDrift() > 0.0 && !place.getUpperBoundaryInfinity() && !place.getUpperBoundaryReached())
						timeDelta = (place.getUpperBoundary() - place.getFluidLevel())/ place.getDrift();
					else
						continue;
				
					timeOfCurrentEvent = currentTime + timeDelta;
					
					if (timeOfCurrentEvent < event.getOccurenceTime()){
						
						event.setEventType(SimulationEventType.place_boundary);							
						event.setFirstEventItem(place, 0);
						event.setOccurenceTime(timeOfCurrentEvent);
							
					} else if (timeOfCurrentEvent == event.getOccurenceTime()) {
						
						if (event.getEventType() == SimulationEventType.no_event || event.getEventType() == SimulationEventType.general_transition || event.getEventType() == SimulationEventType.guard_arcs_deterministic || event.getEventType() == SimulationEventType.guard_arcs_continuous){							
							event.setEventType(SimulationEventType.place_boundary);
							event.setFirstEventItem(place, 0);							
						} else if (event.getEventType() == SimulationEventType.place_boundary){
							event.getRelatedObjects().add(place);
						}							
					}			
				}					
			}	
		}
		
		//complete event and update model marking
		if (maxTime < event.getOccurenceTime() || event.getEventType() == SimulationEventType.no_event){
			if (maxTime- currentTime > 0.0)
				model.advanceMarking(maxTime- currentTime);
			
			model.updateEnabling();
			model.updateFluidRates();
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
	
	
	private void completeEvent(Boolean printRunResults, MarkingPlot currentPlot){
	
		if (event.getEventType() == SimulationEventType.immediate_transition || event.getEventType() == SimulationEventType.deterministic_transition || event.getEventType() == SimulationEventType.general_transition) {
			
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
	
		} else if (event.getEventType() == SimulationEventType.guard_arcs_immediate || event.getEventType() == SimulationEventType.guard_arcs_continuous || event.getEventType() == SimulationEventType.guard_arcs_deterministic){
			
			//guard arc condition
			for (Object object: event.getRelatedObjects()){
				Boolean fulfilled = ((GuardArc)object).checkCondition();
				
				if (printRunResults && fulfilled && !((GuardArc)object).getInhibitor()) 
					System.out.println(event.getOccurenceTime() + " seconds: test arc " + ((GuardArc)object).getId() + " has its condition fulfilled for transition " + ((GuardArc)object).getConnectedTransition().getId());
				else if (printRunResults && !fulfilled && !((GuardArc)object).getInhibitor())
					System.out.println(event.getOccurenceTime() + " seconds: test arc " + ((GuardArc)object).getId() + " has its condition stopped being fulfilled for transition " + ((GuardArc)object).getConnectedTransition().getId());
				else if (printRunResults && fulfilled && ((GuardArc)object).getInhibitor()) 
					System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + ((GuardArc)object).getId() + " has its condition fulfilled for transition " + ((GuardArc)object).getConnectedTransition().getId());
				else if (printRunResults && !fulfilled && ((GuardArc)object).getInhibitor())
					System.out.println(event.getOccurenceTime() + " seconds: inhibitor arc " + ((GuardArc)object).getId() + " has its condition stopped being fulfilled for transition " + ((GuardArc)object).getConnectedTransition().getId());									
			}			
		} else if (event.getEventType() == SimulationEventType.place_boundary){
			
			//place boundary reached
			for (Object object: event.getRelatedObjects()){				
				
				if (((ContinuousPlace)object).checkLowerBoundary()){
					((ContinuousPlace)object).checkUpperBoundary();
					if (printRunResults)
						System.out.println(event.getOccurenceTime() + " seconds: continuous place " + ((ContinuousPlace)object).getId() + " is empty");
				} else {
					((ContinuousPlace)object).checkUpperBoundary();
					if (printRunResults)
						System.out.println(event.getOccurenceTime() + " seconds: continuous place " + ((ContinuousPlace)object).getId() + " has reached its upper boundary");
				}					
			}	
		}

		//update model status
		model.updateEnabling();
		model.updateFluidRates();
		
		//plot status
		currentPlot.saveAll(event.getOccurenceTime());		
	}
	
	
	private Transition conflictResolutionByTransitionWeight(){
		
		Double sum = 0.0;
		Double probability = 0.0;
		Double winner =  new Random().nextDouble();
		
		if (event.getEventType() == SimulationEventType.immediate_transition){
			for (Object object : event.getRelatedObjects())
				sum += ((ImmediateTransition)object).getWeight();
			
			for (Object object : event.getRelatedObjects()){
				probability += ((ImmediateTransition)object).getWeight() / sum;
				if (winner < probability)
					return ((Transition)object);
			}	
		} else if (event.getEventType() == SimulationEventType.deterministic_transition){
			for (Object object : event.getRelatedObjects())
				sum += ((DeterministicTransition)object).getWeight();
			
			for (Object object : event.getRelatedObjects()){
				probability += ((DeterministicTransition)object).getWeight() / sum;
				if (winner < probability)
					return ((Transition)object);
			}
		} else if (event.getEventType() == SimulationEventType.general_transition){
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