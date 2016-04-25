package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;


@XmlRootElement(name = "HPnG")
public class HPnGModel {

	public ArrayList<Place> getPlaces() {
		return places;
	}

	public ArrayList<Transition> getTransitions() {
		return transitions;
	}
	/*
	public void setTransitions(ArrayList<Transition> transitions) {
		this.transitions = transitions;
	}*/

	public ArrayList<Arc> getArcs() {
		return arcs;
	}
	/*
	public void setArcs(ArrayList<Arc> arcs) {
		this.arcs = arcs;
	}*/
	
	@XmlElementWrapper(name="places")
	@XmlElements({
	    @XmlElement(name="discretePlace", type=DiscretePlace.class),
	    @XmlElement(name="continuousPlace", type=ContinuousPlace.class),
	})
	private ArrayList <Place> places = new ArrayList<Place>();
	
	@XmlElementWrapper(name="transitions")
	@XmlElements({
	    @XmlElement(name="deterministicTransition", type=DeterministicTransition.class),
	    @XmlElement(name="fluidTransition", type=ContinuousTransition.class),
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
	
		
	//updates enabling status for all transitions, but does not include a new check of guard arc conditions 
	public void updateEnabling(){		
		
		for(Transition transition: transitions){			
			Boolean enabled = true;
			
			for (Arc arc: transition.getConnectedArcs()){
					
				if (arc.getClass().equals(GuardArc.class)){
					if(!((GuardArc)arc).getConditionFulfilled())
						enabled = false;
					continue;
				}
					
				if (!transition.getClass().equals(ContinuousTransition.class) && !transition.getClass().equals(DynamicContinuousTransition.class)){		
				
					if (((DiscreteArc)arc).getDirection() == DiscreteArcType.output)
						continue;
					
					if (((DiscretePlace)arc.getConnectedPlace()).getNumberOfTokens() < arc.getWeight())
						enabled = false;
				}				
				transition.setEnabled(enabled);			
			}	
		}
	}
	
	
	public void checkGuardArcs(){
		for (Arc arc: arcs){
			if (arc.getClass().equals(GuardArc.class)){
				((GuardArc)arc).checkCondition();
			} else
				break;
		}
	}
	
	
	public void updateFluidRates(){		
		
		//set supposed fluid rates
		for(Transition transition: transitions){			
			if (transition.getClass().equals(ContinuousTransition.class))			
				((ContinuousTransition)transition).setCurrentFluid(((ContinuousTransition)transition).getFluidRate());
		}
	
		//check borders
		Double inFlux = new Double(0.0);
		Double outFlux= new Double(0.0);
		for (Place place: places){			
			if (place.getClass().equals(ContinuousPlace.class)){
				
				getInFluxAndOutFlux((ContinuousPlace)place, inFlux, outFlux);
				
				//if upper boundary reached
				if(!((ContinuousPlace)place).getUpperBoundaryInfinity() && inFlux > outFlux && ((ContinuousPlace)place).getFluidLevel() == ((ContinuousPlace)place).getUpperBoundary()){
					rateAdaption((ContinuousPlace)place, outFlux);
				
				} else if (outFlux > inFlux && ((ContinuousPlace)place).getFluidLevel() == 0.0){
					//lower boundary reached
					rateAdaption((ContinuousPlace)place, inFlux);					
				}
				
			} else
				continue;		
		}
		
		//set dynamic fluid rates
		for(Transition transition: transitions){			
			if (transition.getClass().equals(DynamicContinuousTransition.class)){
				
				((DynamicContinuousTransition)transition).setCurrentFluid(0.0);
				
				for (int i = 0; i < ((DynamicContinuousTransition)transition).getDependencies().size(); i++){
					((DynamicContinuousTransition)transition).setCurrentFluid(
							((DynamicContinuousTransition)transition).getDependencies().get(i).getTransition().getCurrentFluid() 
							* ((DynamicContinuousTransition)transition).getDependencies().get(i).getCoefficient());
				}
			}
		}
	}
	
	
	public void advanceMarking(Double timeDelta){
		
		for (Place place: places){
			if (place.getClass().equals(ContinuousPlace.class)){
				Double fluid = ((ContinuousPlace)place).getFluidLevel();
				fluid += ((ContinuousPlace)place).getDrift() * timeDelta;
				((ContinuousPlace)place).setFluidLevel(fluid);
			}
		}
		
		for (Transition transition: transitions){
			if (transition.getClass().equals(DeterministicTransition.class) && transition.getEnabled()){
				((DeterministicTransition)transition).setClock(((DeterministicTransition)transition).getClock() + timeDelta);
			} else if  (transition.getClass().equals(GeneralTransition.class) && transition.getEnabled()){
				//TODO: eventuell später anpassen (one shot und mehr shot transitions)
				((GeneralTransition)transition).setEnablingTime(((GeneralTransition)transition).getEnablingTime() + timeDelta);
				
			}
		}
	}
	
	private void getInFluxAndOutFlux(ContinuousPlace place, Double inFlux, Double outFlux){
		
		inFlux = 0.0;
		outFlux = 0.0;
		
		for (Arc arc: arcs){
			if (arc.getConnectedPlace().getId().equals(place.getId()) && !arc.getClass().equals(GuardArc.class)){
				if (arc.getConnectedTransition().getEnabled()) {
					if (((ContinuousArc)arc).getDirection() == ContinuousArcType.input)
						inFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid();
					else
						outFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid();
				}
			}
		}
		
		((ContinuousPlace)place).setDrift(inFlux-outFlux);
	}
	
	
	private void rateAdaption(ContinuousPlace place, double flux){
		
		Integer arcIndex = 0;
		Integer arcIndex2;
		Integer currentPriority;
		Double fluxRequired;
		Double sum = 0.0;
		Double sharedFlux;
		ArrayList<ContinuousArc> priorityArcs = new ArrayList<ContinuousArc>();
		
		while(arcIndex < arcs.size() && flux > 0.0){	
			if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) && arcs.get(arcIndex).getClass().equals(ContinuousArc.class)){
							
				currentPriority = ((ContinuousArc)arcs.get(arcIndex)).getPriority();
				fluxRequired = 0.0;
				priorityArcs.clear();
				
				//sum up required flux for current priority
				for (arcIndex2 = arcIndex; arcIndex2 < arcs.size(); arcIndex2++){
					if (arcs.get(arcIndex2).getClass().equals(ContinuousArc.class)){
						ContinuousArc currentArc = (ContinuousArc)arcs.get(arcIndex2);
						if (currentArc.getConnectedPlace().getId().equals(place.getId()) && currentArc.getConnectedTransition().getEnabled() && currentArc.getPriority() == currentPriority){
							fluxRequired += ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate();
							sum += (((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate()*currentArc.getShare());
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
				if (priorityArcs.size() == 1){
					((ContinuousTransition)priorityArcs.get(0).getConnectedTransition()).setCurrentFluid(flux);
				} else {
					for (ContinuousArc currentArc: priorityArcs){
						sharedFlux = flux * currentArc.getShare() * ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate() / sum;
						
						if (sharedFlux > ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate()){
							flux -= ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate();
							sum -= ((ContinuousTransition)currentArc.getConnectedTransition()).getFluidRate() * currentArc.getShare();
						} else {
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
			if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) && arcs.get(arcIndex).getClass().equals(ContinuousArc.class)){
				((ContinuousTransition)arcs.get(arcIndex).getConnectedTransition()).setCurrentFluid(0.0);
			}			
			arcIndex++;
		}
	}	
	
	public void fireTransition(Transition transition){
		
		DiscretePlace place;
		
		for (Arc arc: transition.getConnectedArcs()){
			
			if (arc.getClass().equals(DiscreteArc.class)){
				place = (DiscretePlace)arc.getConnectedPlace();
				
				if (((DiscreteArc)arc).getDirection() == DiscreteArcType.input) 
					//input for place = output for transition -> add tokens
					place.setNumberOfTokens(place.getNumberOfTokens() + arc.getWeight().intValue());
				else //output for place = input for transition -> reduce tokens
					place.setNumberOfTokens(place.getNumberOfTokens() - arc.getWeight().intValue());
				
			}
		}
		
		if (transition.getClass().equals(DeterministicTransition.class)){
			((DeterministicTransition)transition).setClock(0.0);
		} //TODO: else if (GeneralTransition) ?
	}	
}