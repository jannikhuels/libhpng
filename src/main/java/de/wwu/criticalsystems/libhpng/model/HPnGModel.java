package de.wwu.criticalsystems.libhpng.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidModelConnectionException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;
import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;


@XmlRootElement(name = "HPnG")
public class HPnGModel {
	
	public HPnGModel(){}
	
	public HPnGModel(HPnGModel modelToCopy, Logger logger) throws ModelCopyingFailedException, InvalidModelConnectionException{
				
		for(Place currentPlaceToCopy : modelToCopy.getPlaces()) {
			
			if (currentPlaceToCopy.getClass().equals(DiscretePlace.class))
				places.add(new DiscretePlace((DiscretePlace)currentPlaceToCopy));
			else if (currentPlaceToCopy.getClass().equals(ContinuousPlace.class))
				places.add(new ContinuousPlace((ContinuousPlace)currentPlaceToCopy));
		}
		
		
		for(Transition currentTransitionToCopy : modelToCopy.getTransitions()) {
			
			if (currentTransitionToCopy.getClass().equals(DeterministicTransition.class))
				transitions.add(new DeterministicTransition((DeterministicTransition)currentTransitionToCopy));
			else if (currentTransitionToCopy.getClass().equals(ImmediateTransition.class))
				transitions.add(new ImmediateTransition((ImmediateTransition)currentTransitionToCopy));
			else if (currentTransitionToCopy.getClass().equals(GeneralTransition.class))
				transitions.add(new GeneralTransition((GeneralTransition)currentTransitionToCopy));
			else if (currentTransitionToCopy.getClass().equals(ContinuousTransition.class))
				transitions.add(new ContinuousTransition((ContinuousTransition)currentTransitionToCopy));
			else if (currentTransitionToCopy.getClass().equals(DynamicContinuousTransition.class))
				transitions.add(new DynamicContinuousTransition((DynamicContinuousTransition)currentTransitionToCopy));
		}
		
		

		for(Arc currentArcToCopy : modelToCopy.getArcs()) {
			
			if (currentArcToCopy.getClass().equals(DiscreteArc.class))
				arcs.add(new DiscreteArc((DiscreteArc)currentArcToCopy));
			else if (currentArcToCopy.getClass().equals(ContinuousArc.class))
				arcs.add(new ContinuousArc((ContinuousArc)currentArcToCopy));
			else if (currentArcToCopy.getClass().equals(GuardArc.class))
				arcs.add(new GuardArc((GuardArc)currentArcToCopy));
		}
		
		//initialize model
		setConnectedPlacesAndTransitions(logger);
		sortLists(logger);
				
	}

	
	public ArrayList<Place> getPlaces() {
		return places;
	}
	
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}
	
	public ArrayList<Arc> getArcs() {
		return arcs;
	}
	
	@XmlElementWrapper(name="places")
	@XmlElements({
	    @XmlElement(name="discretePlace", type=DiscretePlace.class),
	    @XmlElement(name="continuousPlace", type=ContinuousPlace.class),
	})
	private ArrayList <Place> places = new ArrayList<Place>();
	
	@XmlElementWrapper(name="transitions")
	@XmlElements({
	    @XmlElement(name="deterministicTransition", type=DeterministicTransition.class),
	    @XmlElement(name="continuousTransition", type=ContinuousTransition.class),
	    @XmlElement(name="generalTransition", type=GeneralTransition.class),
	    @XmlElement(name="immediateTransition", type=ImmediateTransition.class),
	    @XmlElement(name="dynamicContinuousTransition", type=DynamicContinuousTransition.class),
	})
	private ArrayList <Transition> transitions = new ArrayList<Transition>();
	
	@XmlElementWrapper(name="arcs")
	@XmlElements({
	    @XmlElement(name="continuousArc", type=ContinuousArc.class),
	    @XmlElement(name="discreteArc", type=DiscreteArc.class),
	    @XmlElement(name="guardArc", type=GuardArc.class),
	})
	private ArrayList <Arc> arcs = new ArrayList<Arc>();
	
	
	//resets all marking-related properties of the model
	public void resetMarking(){
		
		setClockValuesToZero();		
		setOriginalFluidLevelsAndTokens();
		checkAllGuardArcs();
		try {
			updateEnabling(true);
		} catch (InvalidRandomVariateGeneratorException e) {}
		
		updateFluidRates(0.0);
		setDynamicContinuousTransitionsBack();
		computeInitialInternalTransitions();
	}
	
	
	public void setConnectedPlacesAndTransitions(Logger logger) throws InvalidModelConnectionException{

		for(Arc arc: arcs){
			Boolean fromNodeFound = false;
			Boolean toNodeFound = false;	
			
			//search for place id
		    for(Place place: places){
		    	
		        if(!fromNodeFound && place.getId().equals(arc.getFromNode())){
		        	
		        	fromNodeFound = true;
		        	arc.setConnectedPlace(place);		        			        	
		        	if(arc.getClass().equals(ContinuousArc.class)){
		        		ContinuousArcType dir = ContinuousArcType.output;
		        		((ContinuousArc)arc).setDirection(dir);
		        	} else if (arc.getClass().equals(DiscreteArc.class)){
		        		DiscreteArcType dir = DiscreteArcType.output;
		        		((DiscreteArc)arc).setDirection(dir);		        	
		        	}
		        	break;
		        		
		        } else if (!toNodeFound && place.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = true;
		        	arc.setConnectedPlace(place);
		        	if(arc.getClass().equals(ContinuousArc.class)){
		        		ContinuousArcType dir = ContinuousArcType.input;
		        		((ContinuousArc)arc).setDirection(dir);
		        	} else if (arc.getClass().equals(DiscreteArc.class)){
		        		DiscreteArcType dir = DiscreteArcType.input;
		        		((DiscreteArc)arc).setDirection(dir);		        	
		        	}
		        	break;
		        }		        
		    }
		       
		    //search for transition id
	        for(Transition transition: transitions){
		        if(!fromNodeFound && transition.getId().equals(arc.getFromNode())){
		        	
		        	fromNodeFound = true;
		        	arc.setConnectedTransition(transition);	
		        	transition.getConnectedArcs().add(arc);
		        	break;
		        		
		        } else if (!toNodeFound && transition.getId().equals(arc.getToNode())){
		        	
		        	toNodeFound = true;
		        	arc.setConnectedTransition(transition);
		        	transition.getConnectedArcs().add(arc);
		        	break;
		        }	        	
		    }	
	        
	        if (!fromNodeFound){
	          	if (logger != null) logger.severe("Model error: 'fromNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        	throw new InvalidModelConnectionException("'fromNode' for arc '" + arc.getId() + "' could not be matched to any place or transition");
	        }
	        if (!toNodeFound){
	        	if (logger != null) logger.severe("Model error: 'toNode' for arc '" + arc.getId() + "' could not be matched to any place or transition");
	        	throw new InvalidModelConnectionException("'toNode' for arc " + arc.getId() + " could not be matched to any place or transition");
	        }
	        
	      }
		  if (logger != null) logger.info("Model object connections were set successfully.");

	}
	
	public void sortLists(Logger logger){
		
		Collections.sort(places, new PlaceComparator());
		Collections.sort(transitions, new TransitionComparator());
		Collections.sort(arcs, new ArcComparator());
		
		for(Transition transition: transitions)
			Collections.sort(transition.getConnectedArcs(),new ArcComparatorForTransitions());
		
		if (logger != null) logger.info("Model lists sorted successfully.");
	}
	
		
	//updates enabling status for all transitions, but does not include a new check of guard arc conditions 
	public void updateEnabling(Boolean reset) throws InvalidRandomVariateGeneratorException{		
		
		for(Transition transition: transitions){			
			Boolean enabled = true;
			
			for (Arc arc: transition.getConnectedArcs()){
					
				if (arc.getClass().equals(GuardArc.class)){
					if((!((GuardArc)arc).getInhibitor() && !((GuardArc)arc).getConditionFulfilled()) || (((GuardArc)arc).getInhibitor() && ((GuardArc)arc).getConditionFulfilled()))
						enabled = false;
					continue;
				}
					
				if (!transition.getClass().equals(ContinuousTransition.class) && !transition.getClass().equals(DynamicContinuousTransition.class)){		
				
					if (((DiscreteArc)arc).getDirection().equals(DiscreteArcType.input))
						continue;
					
					if (((DiscretePlace)arc.getConnectedPlace()).getNumberOfTokens() < arc.getWeight())
						enabled = false;
				}		
			}

			if (!reset && enabled && transition.getClass().equals(GeneralTransition.class) && !transition.getEnabled())
				((GeneralTransition)transition).enableByPolicy(reset);
			
			
			transition.setEnabled(enabled);
		}
	}
	
	
	public void checkAllGuardArcs(){
		for (Arc arc: arcs){
			if (arc.getClass().equals(GuardArc.class)){
				((GuardArc)arc).checkCondition();
			} else
				break;
		}
	}
	
	
	public void checkGuardArcsForDiscretePlaces(){
		for (Arc arc: arcs){
			if (arc.getClass().equals(GuardArc.class) && arc.getConnectedPlace().getClass().equals(DiscretePlace.class)){
				((GuardArc)arc).checkCondition();
			} else if (!arc.getClass().equals(GuardArc.class))
				break;
		}
	}
	
	
	public void updateFluidRates(Double timePoint){		
		
		
		//check borders
		Double inFlux;
		Double outFlux;
		Double changeOfInFlux;
		Double changeOfOutFlux;
		Boolean change = true;
		ContinuousPlace p;		
		Double oldDrift;
		Double oldChangeOfDrift;
				
		setDynamicContinuousTransitionsBack();
		//set supposed fluid rates
		for(Transition transition: transitions){			
			if (transition.getClass().equals(ContinuousTransition.class)){		
				((ContinuousTransition)transition).setCurrentFluid(((ContinuousTransition)transition).getFluidRate());
				((ContinuousTransition)transition).setCurrentChangeOfFluid(0.0);
			}
		}
		updateDynamicRates();
				
		
		while (change){
		
			change = false;
		
			for (Place place: places){			
				if (place.getClass().equals(ContinuousPlace.class)){
					
					inFlux = 0.0;					
					outFlux = 0.0;
					changeOfInFlux = 0.0;
					changeOfOutFlux = 0.0;
					
					p = (ContinuousPlace)place;
					
					oldDrift = p.getExactDrift();
					oldChangeOfDrift = p.getChangeOfExactDrift();
					
					for (Arc arc: arcs){
						if (arc.getConnectedPlace().getId().equals(place.getId()) && !arc.getClass().equals(GuardArc.class)){
							if (arc.getConnectedTransition().getEnabled()) {
								
								if (((ContinuousArc)arc).getDirection().equals(ContinuousArcType.input)){
									
									if (arc.getConnectedTransition().getClass().equals(ContinuousTransition.class)){
										inFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid() * arc.getWeight();								
									} else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
										inFlux += ((DynamicContinuousTransition)arc.getConnectedTransition()).getCurrentFluid() * arc.getWeight();
										changeOfInFlux += ((DynamicContinuousTransition)arc.getConnectedTransition()).getCurrentChangeOfFluid() * arc.getWeight();
									}
														
								} else {									
									if (arc.getConnectedTransition().getClass().equals(ContinuousTransition.class)){
										outFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid() * arc.getWeight();
									} else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){	
										outFlux += ((DynamicContinuousTransition)arc.getConnectedTransition()).getCurrentFluid() * arc.getWeight();
										changeOfOutFlux += ((DynamicContinuousTransition)arc.getConnectedTransition()).getCurrentChangeOfFluid() * arc.getWeight();
									}
								}
							}
						}
					}

					p.setExactDrift(inFlux - outFlux);					
					p.setChangeOfExactDrift(changeOfInFlux - changeOfOutFlux);
					
					
					if((inFlux > outFlux) && p.getCurrentFluidLevel() >= p.getUpperBoundary()){
						//if upper boundary reached
						Double timeToNextPriority = rateAdaption((ContinuousPlace)place, outFlux, ContinuousArcType.input, changeOfOutFlux);
						p.setExactDrift(0.0);
						p.setChangeOfExactDrift(0.0);
						
						if (timeToNextPriority < Double.POSITIVE_INFINITY)
							p.setTimeToNextInternalTransition(Math.min(p.getTimeToNextInternalTransition(), timeToNextPriority));
						
						change = true;
					
					} else if ((outFlux > inFlux) && p.getCurrentFluidLevel() <= 0.0){
						//lower boundary reached
						Double timeToNextPriority = rateAdaption((ContinuousPlace)place, inFlux, ContinuousArcType.output, changeOfInFlux);
						
						p.setExactDrift(0.0);
						p.setChangeOfExactDrift(0.0);

						if (timeToNextPriority < Double.POSITIVE_INFINITY)
							p.setTimeToNextInternalTransition(Math.min(p.getTimeToNextInternalTransition(), timeToNextPriority));
						
						change = true;
						
					} else 
						updateDynamicRates();
						
					
					if (oldDrift != null && oldChangeOfDrift != null && (Math.abs(p.getExactDrift() - oldDrift) >= 0.000001 || Math.abs(p.getChangeOfExactDrift() - oldChangeOfDrift) >= 0.000001)){
						p.performInternalTransition(timePoint, oldDrift, oldChangeOfDrift, arcs);
						//System.out.println(timePoint + " seconds: continuous place " + place.getId() + " has performed an internal transition --1");
					}
										
				} else
					continue;		
			}
		}
		
	}
	

	public void advanceMarking(Double timeDelta){
		
		ContinuousPlace p;
					
		for (Place place: places){
			if (place.getClass().equals(ContinuousPlace.class)){
				
				p = ((ContinuousPlace)place);
				Double fluid = p.getCurrentFluidLevel();
				fluid += p.getDrift() * timeDelta;
				
				BigDecimal level = new BigDecimal(fluid);
				level = level.setScale(8,BigDecimal.ROUND_HALF_UP);
				if (level.doubleValue() <= 0.0 )
					fluid = 0.0;
				else if (!p.getUpperBoundaryInfinity() && p.getUpperBoundary().equals(level.doubleValue()))
					fluid = p.getUpperBoundary();					
				else
					fluid = level.doubleValue();
				p.setCurrentFluidLevel(fluid);
				//p.checkUpperBoundary();
				//p.checkLowerBoundary();
				
			}
		}
		
		for (Transition transition: transitions){
			if (transition.getClass().equals(DeterministicTransition.class) && transition.getEnabled()){
				((DeterministicTransition)transition).setClock(((DeterministicTransition)transition).getClock() + timeDelta);
			} else if  (transition.getClass().equals(GeneralTransition.class) && transition.getEnabled()){
				((GeneralTransition)transition).setEnablingTime(((GeneralTransition)transition).getEnablingTime() + timeDelta);				
			}
		}	
		
		updateDynamicRates();
		checkAllGuardArcs();
	}	
	
	public void printCurrentMarking(Boolean initial, Boolean last){
		
		if (initial)
			System.out.print("Initial marking:");
		else if (last)
			System.out.print("Final marking:  ");
		else
			System.out.print("Current marking:");
		
		
		for (Place place: places){
			if (place.getClass().equals(ContinuousPlace.class))
				System.out.print("    " + place.getId() + ": " + ((ContinuousPlace)place).getCurrentFluidLevel());
			 else 
				System.out.print("    " + place.getId() + ": " + ((DiscretePlace)place).getNumberOfTokens());
		}
		System.out.println();
		System.out.println();
	}
	
		
	public int[][] getArrayOfNumberOfComponents(){
		
		int[][] numbers = new int[3][5];
		Arrays.fill(numbers[0], 0);
		Arrays.fill(numbers[1], 0);
		Arrays.fill(numbers[2], 0);
		
		int counter1 = 0;
		int counter2 = 0;
		int counter3 = 0;
		int counter4 = 0;
		int counter5 = 0;
		
		
		for (Place place: places){				
			if (place.getClass().equals(DiscretePlace.class))
				counter1++;
			else if (place.getClass().equals(ContinuousPlace.class))
				counter2++;			
		}
		numbers[0][0] = counter1;
		numbers[0][1] = counter2;	
		
		counter1 = 0;
		counter2 = 0;
		
		
		for (Transition transition: transitions){
			if (transition.getClass().equals(GeneralTransition.class))
				counter1++;
			else if (transition.getClass().equals(ImmediateTransition.class))
				counter2++;
			else if (transition.getClass().equals(DeterministicTransition.class))
				counter3++;
			else if (transition.getClass().equals(ContinuousTransition.class))
				counter4++;
			else if (transition.getClass().equals(DynamicContinuousTransition.class))
				counter5++;		
		}
		numbers[1][0] = counter1;
		numbers[1][1] = counter2;
		numbers[1][2] = counter3;
		numbers[1][3] = counter4;
		numbers[1][4] = counter5;
		
		counter1 = 0;
		counter2 = 0;
		counter3 = 0;

				
		for (Arc arc: arcs){
			if (arc.getClass().equals(DiscreteArc.class))
				counter1++;
			else if (arc.getClass().equals(ContinuousArc.class))
				counter2++;
			else if (arc.getClass().equals(GuardArc.class))
				counter3++;
		}
		numbers[2][0] = counter1;
		numbers[2][1] = counter2;
		numbers[2][2] = counter3;		
		
		return numbers;		
	}

	

	public void computeInternalTransitionsAtEvent() {
		
		for(Place place: places){		
			
			if (place.getClass().equals(ContinuousPlace.class))				
				((ContinuousPlace)place).computeTimeToNextInternalTransitionFromExternal(arcs);	
						
		}
		
	}
	
	
	private void setDynamicContinuousTransitionsBack() {
		for(Transition transition: transitions){
			if(transition.getClass().equals(DynamicContinuousTransition.class))
				((DynamicContinuousTransition)transition).setAdapted(false);
		}		
	}
	
	
	private Double rateAdaption(ContinuousPlace place, Double flux, ContinuousArcType direction, Double changeOfFlux){
		
		Integer arcIndex = 0;
		Integer arcIndex2;
		Integer currentPriority = 0;
		Double fluxRequired = 0.0;
		Double changeOfFluxRequired = 0.0;
		Double sum = 0.0;
		Double accumulatedFluxRequired = 0.0;
		Double accumulatedChangeOfFluxRequired  = 0.0;

		Double sharedFluid;
		Double changeOfSharedFluid;
		ArrayList<ContinuousArc> priorityArcs = new ArrayList<ContinuousArc>();
		Integer highestPriority;
		Integer lowestPriority;
		Boolean enoughForCurrentPriority = false;
		
		

		int i = 0;
		while (!arcs.get(i).getClass().equals(ContinuousArc.class))
			i++;
		highestPriority = ((ContinuousArc)arcs.get(i)).getPriority();
		lowestPriority = highestPriority;
		while(arcIndex < arcs.size() && flux > 0.0){	
			if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) && arcs.get(arcIndex).getClass().equals(ContinuousArc.class)
					&& ((ContinuousArc)arcs.get(arcIndex)).getDirection().equals(direction)){
							
				currentPriority = ((ContinuousArc)arcs.get(arcIndex)).getPriority();
				enoughForCurrentPriority = false;
				fluxRequired = 0.0;
				changeOfFluxRequired = 0.0;
				priorityArcs.clear();
				
				//sum up required flux for current priority
				for (arcIndex2 = arcIndex; arcIndex2 < arcs.size(); arcIndex2++){
					if (arcs.get(arcIndex2).getClass().equals(ContinuousArc.class)){
						ContinuousArc currentArc = (ContinuousArc)arcs.get(arcIndex2);
						
						if (currentArc.getConnectedPlace().getId().equals(place.getId()) && currentArc.getConnectedTransition().getEnabled() 
								&& currentArc.getPriority().equals(currentPriority) && currentArc.getDirection().equals(direction)){
							
							if (currentArc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
								
								fluxRequired += ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getWeight();
								changeOfFluxRequired += ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getWeight();
								sum += ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getShare() * currentArc.getWeight();
								//changeOfSum += (((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentChangeOfFluid()*currentArc.getShare()) * currentArc.getWeight();
								((DynamicContinuousTransition)currentArc.getConnectedTransition()).setAdapted(false);
							} else {
								fluxRequired += ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate();
								sum += (((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate()*currentArc.getShare());
							}
							
							priorityArcs.add(currentArc);
						
						} else if (currentArc.getPriority() < currentPriority)
							break;
					} else
						break;
				}
				

				//if enough flux for current priority, subtract
				if (fluxRequired < flux){
					flux =- fluxRequired;
					changeOfFlux =- changeOfFluxRequired;
					arcIndex = arcIndex2;
					enoughForCurrentPriority = true;
					accumulatedFluxRequired += fluxRequired;
					accumulatedChangeOfFluxRequired += changeOfFluxRequired;
					continue;
				}				
				
				
				//otherwise: share remaining flux 
				if (priorityArcs.size() == 1 && priorityArcs.get(0).getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
					
					((DynamicContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setCurrentFluid(flux / priorityArcs.get(0).getWeight());
					((DynamicContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setCurrentChangeOfFluid(changeOfFlux / priorityArcs.get(0).getWeight());
					((DynamicContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setAdapted(true);
				} else if (priorityArcs.size() == 1)
					((ContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setCurrentFluid(flux / priorityArcs.get(0).getWeight());
				
				else {
					
					for (ContinuousArc currentArc: priorityArcs){
						
						if (currentArc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
																		
							if (currentArc.getShare() / sum < 1 / flux){
								sharedFluid = ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getShare() * flux / sum ;
								changeOfSharedFluid =  ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getShare() * flux / sum;
								((DynamicContinuousTransition)currentArc.getConnectedTransition()).setCurrentFluid(sharedFluid);			
								((DynamicContinuousTransition)currentArc.getConnectedTransition()).setCurrentChangeOfFluid(changeOfSharedFluid);
								((DynamicContinuousTransition)currentArc.getConnectedTransition()).setAdapted(true);
							} else {
											
								flux -= ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getWeight();
								changeOfFlux -= ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getWeight();
								sum -= ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid()  * currentArc.getWeight() * currentArc.getShare();	
							}
					
								
						} else {
							sharedFluid = flux * currentArc.getShare() * ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate() / sum;
							
							if (currentArc.getShare() / sum < 1 / flux){								
								sharedFluid = ((ContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getShare() * flux / sum ;
								changeOfSharedFluid =  ((ContinuousTransition)currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getShare() * flux / sum;
								((ContinuousTransition)currentArc.getConnectedTransition()).setCurrentFluid(sharedFluid);			
								((ContinuousTransition)currentArc.getConnectedTransition()).setCurrentChangeOfFluid(changeOfSharedFluid);								
							} else {
											
								flux -= ((ContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getWeight();
								changeOfFlux -= ((ContinuousTransition)currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getWeight();
								sum -= ((ContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid()  * currentArc.getWeight() * currentArc.getShare();	
							}						
						}

					}
				}
				flux = 0.0;
				arcIndex = arcIndex2;
				
			} else
				arcIndex++;
		}

		if (enoughForCurrentPriority)
			currentPriority -= 1;
		

		//set lower priority arcs to zero
		while(arcIndex < arcs.size()){	
			if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) && arcs.get(arcIndex).getClass().equals(ContinuousArc.class) && ((ContinuousArc)arcs.get(arcIndex)).getDirection().equals(direction)){
								
				if (arcs.get(arcIndex).getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
					((DynamicContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setCurrentFluid(0.0);
					((DynamicContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setCurrentChangeOfFluid(0.0);
					((DynamicContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setAdapted(true);					
				} else { 
					((ContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setCurrentFluid(0.0);	
					((ContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setCurrentChangeOfFluid(0.0);
				}
				lowestPriority = ((ContinuousArc)arcs.get(arcIndex)).getPriority();
			}
			arcIndex++;
		}

		
		
		if (highestPriority.equals(lowestPriority))
			return Double.POSITIVE_INFINITY;
		
		Double timeToNextPriority = Double.POSITIVE_INFINITY;
	
		timeToNextPriority = computeTimeToNextPriority(flux, changeOfFlux, accumulatedFluxRequired, accumulatedChangeOfFluxRequired); //accumulated = priorities that got full flux
		timeToNextPriority = Math.min(timeToNextPriority, computeTimeToNextPriority(flux, changeOfFlux, accumulatedFluxRequired + fluxRequired, accumulatedChangeOfFluxRequired + changeOfFluxRequired));
	
			
		return timeToNextPriority;
				
	}	
	

	

	private void setClockValuesToZero(){
			
		for(Transition transition: transitions){
			if (transition.getClass().equals(DeterministicTransition.class)){
				((DeterministicTransition)transition).setClock(0.0);
			} else if (transition.getClass().equals(GeneralTransition.class)){
				((GeneralTransition)transition).setEnablingTime(0.0);
				((GeneralTransition)transition).setFiringsToZero();
			}
		}
	}
	
	
	private void setOriginalFluidLevelsAndTokens(){
		for (Place place: places){
			if (place.getClass().equals(ContinuousPlace.class)){				
				((ContinuousPlace)place).resetFluidLevel();
				((ContinuousPlace)place).checkLowerBoundary();
				((ContinuousPlace)place).checkUpperBoundary();
				((ContinuousPlace)place).setLastUpdate(0.0);;
			} else
				((DiscretePlace)place).resetNumberOfTokens();
		}
	}
	
	
	private void updateDynamicRates(){
			
		for(Transition transition: transitions){			
			if (transition.getClass().equals(DynamicContinuousTransition.class) && !((DynamicContinuousTransition)transition).getAdapted())				
				((DynamicContinuousTransition)transition).computeCurrentFluidAndCurrentChangeOfFluid(this.places);						
				
		}
	}
	
	
	private void computeInitialInternalTransitions(){
		
		for(Place place: places){
			
			if (place.getClass().equals(ContinuousPlace.class))
				((ContinuousPlace)place).computeTimeToNextInternalTransition(arcs);	
						
		}
	}
	
	
	private Double computeTimeToNextPriority(Double flux, Double changeOfFlux, Double accumulatedFluxRequired, Double accumulatedChangeOfFluxRequired) {
	
		Double t = (accumulatedChangeOfFluxRequired - changeOfFlux) / (flux - accumulatedFluxRequired);
		
		if (t > 0.0)
			return t;
		
		return Double.POSITIVE_INFINITY;
	}
}