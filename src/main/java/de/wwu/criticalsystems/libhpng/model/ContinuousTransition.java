package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "continuousTransition" )
@XmlSeeAlso({Transition.class})
public class ContinuousTransition extends Transition{

	public ContinuousTransition(){}
	
	public ContinuousTransition(String id, Boolean enabled, Double fluidRate) {
		super(id, enabled);
		this.fluidRate = fluidRate;
	}

	public ContinuousTransition(ContinuousTransition currentTransitionToCopy) {
		super(new String (currentTransitionToCopy.getId()), new Boolean (currentTransitionToCopy.getEnabled()));
		this.fluidRate = new Double (currentTransitionToCopy.getFluidRate());		
		this.currentFluid = new Double (currentTransitionToCopy.getCurrentFluid());		
	}

	public Double getFluidRate() {
		return fluidRate;
	}
	@XmlAttribute (name = "rate")
	public void setFluidRate(Double fluidRate) {
		this.fluidRate = fluidRate;
	}

	public Double getCurrentFluid() {
		return currentFluid;
	}
	public void setCurrentFluid(Double currentFluid) {
		this.currentFluid = currentFluid;
	}
	
	public Double getCurrentChangeOfFluid() {
		return currentChangeOfFluid;
	}
	public void setCurrentChangeOfFluid(Double currentChangeOfFluid) {
		this.currentChangeOfFluid = currentChangeOfFluid;
	}

	private Double fluidRate;
	private Double currentFluid;
	private Double currentChangeOfFluid = 0.0;
}
