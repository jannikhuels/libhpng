package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "continuousPlace" )
public class ContinuousPlace extends Place{

	
	public ContinuousPlace(){}
	
	public ContinuousPlace(String id, Double fluidLevel, Double upperBoundary,
			Double drift, Boolean upperBoundaryInfinity) {
		super(id);
		this.fluidLevel = fluidLevel;
		this.upperBoundary = upperBoundary;
		this.drift = drift;
		this.upperBoundaryInfinity = upperBoundaryInfinity;
	}
	
	public Double getFluidLevel() {
		return fluidLevel;
	}
	
	@XmlAttribute (name = "level")
	public void setFluidLevel(Double fluidLevel) {
		this.fluidLevel = fluidLevel;
	}
	public Double getUpperBoundary() {
		return upperBoundary;
	}
	
	@XmlAttribute (name = "capacity")
	public void setUpperBoundary(Double upperBoundary) {
		this.upperBoundary = upperBoundary;
	}
	public Double getDrift() {
		return drift;
	}

	public void setDrift(Double drift) {
		this.drift = drift;
	}
	public Boolean getUpperBoundaryInfinity() {
		return upperBoundaryInfinity;
	}	
	public void setUpperBoundaryInfinity(Boolean upperBoundaryInfinity) {
		this.upperBoundaryInfinity = upperBoundaryInfinity;
	}

	private Double fluidLevel;
	private Double upperBoundary;
	private Double drift;
	private Boolean upperBoundaryInfinity;
	
}
