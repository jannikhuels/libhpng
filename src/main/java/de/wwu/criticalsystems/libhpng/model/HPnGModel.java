package de.wwu.criticalsystems.libhpng.model;

import java.math.BigDecimal;
import java.util.ArrayList;
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
		}
		

		for(Transition currentTransitionToCopy : modelToCopy.getTransitions()) {
			
			if (currentTransitionToCopy.getClass().equals(DynamicContinuousTransition.class))
				transitions.add(new DynamicContinuousTransition((DynamicContinuousTransition)currentTransitionToCopy, transitions));
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
		updateFluidRates();
		setDynamicContinuousTransitionsBack();
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
	
		
	private void setDynamicContinuousTransitionsBack() {
		for(Transition transition: transitions){
			if(transition.getClass().equals(DynamicContinuousTransition.class))
				((DynamicContinuousTransition)transition).setAdapted(false);
		}		
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
				
					if (((DiscreteArc)arc).getDirection() == DiscreteArcType.input)
						continue;
					
					if (((DiscretePlace)arc.getConnectedPlace()).getNumberOfTokens() < arc.getWeight())
						enabled = false;
				}		
			}
			
			transition.setEnabled(enabled);
			if (enabled && transition.getClass().equals(GeneralTransition.class))
				((GeneralTransition)transition).enableByPolicy(reset);
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
	
	
	public void updateFluidRates(){		
		
		setDynamicContinuousTransitionsBack();
		//set supposed fluid rates
		for(Transition transition: transitions){			
			if (transition.getClass().equals(ContinuousTransition.class))			
				((ContinuousTransition)transition).setCurrentFluid(((ContinuousTransition)transition).getFluidRate());
		}
		updateDynamicRates();
		
		//check borders
		Double inFlux;
		Double outFlux;
		Boolean change = true;
		
		while (change){
		
			change = false;
		
			for (Place place: places){			
				if (place.getClass().equals(ContinuousPlace.class)){
					inFlux = 0.0;
					outFlux = 0.0;
					for (Arc arc: arcs){
						if (arc.getConnectedPlace().getId().equals(place.getId()) && !arc.getClass().equals(GuardArc.class)){
							if (arc.getConnectedTransition().getEnabled()) {
								
								if (((ContinuousArc)arc).getDirection() == ContinuousArcType.input){
									if (arc.getConnectedTransition().getClass().equals(ContinuousTransition.class))
										inFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid();
									else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class))
										inFlux += ((DynamicContinuousTransition)arc.getConnectedTransition()).getCurrentFluid();
														
								} else {									
									if (arc.getConnectedTransition().getClass().equals(ContinuousTransition.class))
										outFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid();
									else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class))
										outFlux += ((DynamicContinuousTransition)arc.getConnectedTransition()).getCurrentFluid();							
								}
							}
						}
					}
	
					((ContinuousPlace)place).setDrift(inFlux - outFlux);
					
					
					if(!((ContinuousPlace)place).getUpperBoundaryInfinity() && (inFlux > outFlux) && (Math.ceil(((ContinuousPlace)place).getFluidLevel()*1000000)/1000000 >=((ContinuousPlace)place).getUpperBoundary())){
						//if upper boundary reached
						rateAdaption((ContinuousPlace)place, outFlux, ContinuousArcType.input);
						updateDynamicRates();
						((ContinuousPlace)place).setDrift(0.0);
						change = true;
					
					} else if ((outFlux > inFlux) && (Math.floor(((ContinuousPlace)place).getFluidLevel()*1000000)/1000000 <= 0.0)){
						//lower boundary reached
						rateAdaption((ContinuousPlace)place, inFlux, ContinuousArcType.output);
						updateDynamicRates();
						((ContinuousPlace)place).setDrift(0.0);
						change = true;
					}
					
				} else
					continue;		
			}
		}
		
	}
	
	
	public void advanceMarking(Double timeDelta){
				
		for (Place place: places){
			if (place.getClass().equals(ContinuousPlace.class)){
				Double fluid = ((ContinuousPlace)place).getFluidLevel();
				fluid += ((ContinuousPlace)place).getDrift() * timeDelta;
				BigDecimal level = new BigDecimal(fluid);
				level = level.setScale(8,BigDecimal.ROUND_HALF_UP);
				if (level.doubleValue() <= 0.0 ) 
					fluid = 0.0;
				else if (!((ContinuousPlace)place).getUpperBoundaryInfinity() && level.doubleValue() == ((ContinuousPlace)place).getUpperBoundary())
					fluid = ((ContinuousPlace)place).getUpperBoundary();
				else
					fluid = level.doubleValue();
				
				((ContinuousPlace)place).setFluidLevel(fluid);
			}
		}
		
		for (Transition transition: transitions){
			if (transition.getClass().equals(DeterministicTransition.class) && transition.getEnabled()){
				((DeterministicTransition)transition).setClock(((DeterministicTransition)transition).getClock() + timeDelta);
			} else if  (transition.getClass().equals(GeneralTransition.class) && transition.getEnabled()){
				((GeneralTransition)transition).setEnablingTime(((GeneralTransition)transition).getEnablingTime() + timeDelta);				
			}
		}	
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
				System.out.print("    " + place.getId() + ": " + ((ContinuousPlace)place).getFluidLevel());
			 else 
				System.out.print("    " + place.getId() + ": " + ((DiscretePlace)place).getNumberOfTokens());
		}
		System.out.println();
		System.out.println();
	}

	
	private void rateAdaption(ContinuousPlace place, double flux, ContinuousArcType direction){
		
		Integer arcIndex = 0;
		Integer arcIndex2;
		Integer currentPriority;
		Double fluxRequired;
		Double sum = 0.0;
		Double sharedFlux;
		ArrayList<ContinuousArc> priorityArcs = new ArrayList<ContinuousArc>();
		
		updateDynamicRates();
		place.setDrift(flux);		
		while(arcIndex < arcs.size() && flux > 0.0){	
			if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) && arcs.get(arcIndex).getClass().equals(ContinuousArc.class)
					&& ((ContinuousArc)arcs.get(arcIndex)).getDirection().equals(direction)){
							
				currentPriority = ((ContinuousArc)arcs.get(arcIndex)).getPriority();
				fluxRequired = 0.0;
				priorityArcs.clear();
				
				//sum up required flux for current priority
				for (arcIndex2 = arcIndex; arcIndex2 < arcs.size(); arcIndex2++){
					if (arcs.get(arcIndex2).getClass().equals(ContinuousArc.class)){
						ContinuousArc currentArc = (ContinuousArc)arcs.get(arcIndex2);
						if (currentArc.getConnectedPlace().getId().equals(place.getId()) && currentArc.getConnectedTransition().getEnabled() 
								&& currentArc.getPriority() == currentPriority && ((ContinuousArc)arcs.get(arcIndex)).getDirection().equals(direction)){
							
							if (currentArc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
								fluxRequired += ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid();
								sum += (((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid()*currentArc.getShare());
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
					arcIndex = arcIndex2;
					continue;
				}				
				
				//share remaining flux 
				if (priorityArcs.size() == 1 && priorityArcs.get(0).getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
					((DynamicContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setCurrentFluid(flux);
					((DynamicContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setAdapted(true);
				}
					
				
				else if (priorityArcs.size() == 1)
					((ContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setCurrentFluid(flux);
				
				else {
					
					for (ContinuousArc currentArc: priorityArcs){
						
						if (currentArc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
							sharedFlux = flux * currentArc.getShare() * ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() / sum;
							
							if (sharedFlux > ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid()){
								flux -= ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid();
								sum -= ((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getShare();
							} else {
								((DynamicContinuousTransition)currentArc.getConnectedTransition()).setCurrentFluid(sharedFlux);					
								((DynamicContinuousTransition)currentArc.getConnectedTransition()).setAdapted(true);
							}							
							
						} else {
							sharedFlux = flux * currentArc.getShare() * ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate() / sum;
							
							if (sharedFlux > ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate()){
								flux -= ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate();
								sum -= ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate() * currentArc.getShare();
							} else
								((ContinuousTransition)currentArc.getConnectedTransition()).setCurrentFluid(sharedFlux);							
						}

					}
				}
				flux = 0.0;
				arcIndex = arcIndex2;
				
			} else
				arcIndex++;
		}
		
		while(arcIndex < arcs.size()){	
			if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) 
					&& arcs.get(arcIndex).getClass().equals(ContinuousArc.class) && ((ContinuousArc)arcs.get(arcIndex)).getDirection().equals(direction)){
				if (arcs.get(arcIndex).getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)){
					((DynamicContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setCurrentFluid(0.0);
					((DynamicContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setAdapted(true);
				}					
				else 
					((ContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setCurrentFluid(0.0);	
			}
			arcIndex++;
		}		
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
			} else
				((DiscretePlace)place).resetNumberOfTokens();
		}
	}
	
	private void updateDynamicRates(){
		Double fluid;
		
		for(Transition transition: transitions){			
			if (transition.getClass().equals(DynamicContinuousTransition.class) && !((DynamicContinuousTransition)transition).getAdapted()){
				fluid = 0.0;
				
				for (int i = 0; i < ((DynamicContinuousTransition)transition).getDependencies().size(); i++){
					if (((DynamicContinuousTransition)transition).getDependencies().get(i).getTransition().getEnabled()){
						fluid+= ((DynamicContinuousTransition)transition).getDependencies().get(i).getTransition().getCurrentFluid() 
								* ((DynamicContinuousTransition)transition).getDependencies().get(i).getCoefficient();
								
					}
				}				
				((DynamicContinuousTransition)transition).setCurrentFluid(fluid);				
			}
		}
	}
}