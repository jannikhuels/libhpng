package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "fluidTransition" )
@XmlSeeAlso({Transition.class})
public class ContinuousTransition extends Transition{

	public ContinuousTransition(){}
	
	public ContinuousTransition(String id, Boolean enabled, Double fluidRate) {
		super(id, enabled);
		this.fluidRate = fluidRate;
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

	private Double fluidRate;
	private Double currentFluid;
}
