package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "continuousPlace" )
public class ContinuousPlace extends Place{
	
	public ContinuousPlace(){}
	
	public ContinuousPlace(String id, Double originalFluidLevel, Double upperBoundary,
			Double drift, Boolean upperBoundaryInfinity) {
		super(id);
		this.originalFluidLevel = originalFluidLevel;
		this.upperBoundary = upperBoundary;
		this.drift = drift;
		this.upperBoundaryInfinity = upperBoundaryInfinity;
	}	
	
	public Double getOriginalFluidLevel() {
		return originalFluidLevel;
	}	
	@XmlAttribute (name = "level")
	public void setOriginalFluidLevel(Double fluidLevel) {
		this.originalFluidLevel = fluidLevel;
	}
	
	public Double getFluidLevel() {
		return currentFluidLevel;
	}	
	public void setFluidLevel(Double fluidLevel) {
		this.currentFluidLevel = fluidLevel;
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
	@XmlAttribute (name = "infiniteCapacity")
	public void setUpperBoundaryInfinity(Boolean upperBoundaryInfinity) {
		this.upperBoundaryInfinity = upperBoundaryInfinity;
	}
	
	public Boolean getUpperBoundaryReached() {
		return upperBoundaryReached;
	}
	public void setUpperBoundaryReached(Boolean upperBoundaryReached) {
		this.upperBoundaryReached = upperBoundaryReached;
	}

	public Boolean getLowerBoundaryReached() {
		return lowerBoundaryReached;
	}
	public void setLowerBoundaryReached(Boolean lowerBoundaryReached) {
		this.lowerBoundaryReached = lowerBoundaryReached;
	}

	private Double currentFluidLevel;
	private Double originalFluidLevel;
	private Double upperBoundary;
	private Double drift;
	private Boolean upperBoundaryInfinity;
	private Boolean upperBoundaryReached;
	private Boolean lowerBoundaryReached;	

	public void resetFluidLevel() {
		this.currentFluidLevel = this.originalFluidLevel;
	}
	
	public Boolean checkUpperBoundary(){							
		if (!upperBoundaryInfinity && currentFluidLevel >= upperBoundary)
			upperBoundaryReached = true;
		else 
			upperBoundaryReached = false;
		return upperBoundaryReached;
	}
	
	public Boolean checkLowerBoundary(){		
		lowerBoundaryReached = false;					
		if (Math.floor(currentFluidLevel*1000000)/1000000 <= 0.0)
			lowerBoundaryReached = true;
		else
			lowerBoundaryReached = false;
		return lowerBoundaryReached;
	}
}