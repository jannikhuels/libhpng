package de.wwu.criticalsystems.libhpng.model;

import java.math.BigDecimal;

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
	

	public ContinuousPlace(ContinuousPlace placeToCopy) {
		super(new String(placeToCopy.getId()));
		this.originalFluidLevel = new Double(placeToCopy.getOriginalFluidLevel());
		this.currentFluidLevel = new Double (placeToCopy.getFluidLevel());
		this.upperBoundary = new Double (placeToCopy.getUpperBoundary());
		this.upperBoundaryInfinity = new Boolean (placeToCopy.getUpperBoundaryInfinity());
		this.upperBoundaryReached = new Boolean (placeToCopy.getUpperBoundaryReached());
		this.lowerBoundaryReached = new Boolean (placeToCopy.lowerBoundaryReached);
		this.drift = new Double (placeToCopy.getDrift());
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
	
	public Double getDeltaQ() {
		return deltaQ;
	}

	public void setDeltaQ(Double deltaQ) {
		this.deltaQ = deltaQ;
	}

	private Double currentFluidLevel;
	private Double originalFluidLevel;
	private Double upperBoundary;
	private Double drift;
	private Boolean upperBoundaryInfinity;
	private Boolean upperBoundaryReached;
	private Boolean lowerBoundaryReached;	
	private Double deltaQ;
	private Double lastTimePoint = 0.0;
	private Double q;
	private Double timeDelta;
	private Double u;

	public void resetFluidLevel() {
		this.currentFluidLevel = this.originalFluidLevel;
	}
	
	public Boolean checkUpperBoundary(){
		
		BigDecimal level = new BigDecimal(currentFluidLevel);
		level = level.setScale(14,BigDecimal.ROUND_UP);
		
		if (!upperBoundaryInfinity && level.doubleValue() >= upperBoundary)
			upperBoundaryReached = true;
		else 
			upperBoundaryReached = false;
		return upperBoundaryReached;
	}
	
	public Boolean checkLowerBoundary(){	
		
		BigDecimal level = new BigDecimal(currentFluidLevel);
		level = level.setScale(14,BigDecimal.ROUND_DOWN);
					
		if (level.doubleValue() <= 0.0)
			lowerBoundaryReached = true;
		else
			lowerBoundaryReached = false;
		return lowerBoundaryReached;
	}
	
/*	public Double calculateNewFluidLevelAndDrift(Double timePoint){ //timeDelta seit letzter Berechnung
		
		
		
		if (timePoint == 0.0){
			if (deltaQ == null)
				System.out.println("DELTA Q!");
			
			q = originalFluidLevel;
			
			//TODO: allgemein
			drift = -1 * q;
		}
		
		timeDelta = timePoint - lastTimePoint;
				
		u = currentFluidLevel + drift*timeDelta;


		//q(t) = u + drift * (t-timepoint);
		
		//TODO: allgemein
		//drift (t) = - q(t) = - u - drift * (t-timepoint);

		//TODO: allgemein
		//x (t) = u - u * (t-timepoint) - 1/2 * drift * (t-timepoint)^2;
		
		currentFluidLevel = u;
		
		lastTimePoint = timePoint;
		return currentFluidLevel;
	}
	
	*/
//	public Double getNextTimePoint(){
//		
//		Double next;
//		Double tempq;
//		Double tempx;
//		
//		
//		//assume q>=x
//		Double deltaT1 = - (drift + u)/ drift + Math.sqrt(Math.pow(drift+u,2.0)/Math.pow(drift,2.0) + 2*deltaQ / drift);	
//		
////		tempq = u + drift * deltaT1;
////		//TODO: allgemein
////		tempx = u - u * deltaT1 - 1/2 * drift * Math.pow(deltaT1,2.0);
////		if (!(tempq >= tempx && tempq - tempx == deltaQ))
////			deltaT1 = 0.0;
//				
//		Double deltaT2 = - (drift + u)/ drift - Math.sqrt(Math.pow(drift+u,2.0)/Math.pow(drift,2.0) + 2*deltaQ / drift);
//
//		
//		//assume x>=q
//		Double deltaT3 = - (drift + u)/ drift + Math.sqrt(Math.pow(drift+u,2.0)/Math.pow(drift,2.0) - 2*deltaQ / drift);
//
//		
//		
//		Double deltaT4 = - (drift + u)/ drift - Math.sqrt(Math.pow(drift+u,2.0)/Math.pow(drift,2.0) - 2*deltaQ / drift);
//		
//		
//		return next;
//	}

	
	
}