package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "fluidTransition" )
public class ContinuousTransition extends Transition{

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

	private Double fluidRate;

}
