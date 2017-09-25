package de.wwu.criticalsystems.libhpng.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;

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
		this.currentFluidLevel = new Double (placeToCopy.getCurrentFluidLevel());
		this.exactFluidLevel = this.currentFluidLevel;
		this.upperBoundary = new Double (placeToCopy.getUpperBoundary());
		this.upperBoundaryInfinity = new Boolean (placeToCopy.getUpperBoundaryInfinity());
		this.upperBoundaryReached = new Boolean (placeToCopy.getUpperBoundaryReached());
		this.lowerBoundaryReached = new Boolean (placeToCopy.lowerBoundaryReached);
		this.drift = new Double (placeToCopy.getDrift());
		this.exactDrift = this.drift;
		this.changeOfExactDrift = new Double(placeToCopy.getChangeOfExactDrift());
		this.quanta = new Double(placeToCopy.getQuanta());
		this.lastUpdate = new Double(placeToCopy.getLastUpdate());
	}

	public Double getOriginalFluidLevel() {
		return originalFluidLevel;
	}	
	@XmlAttribute (name = "level")
	public void setOriginalFluidLevel(Double fluidLevel) {
		this.originalFluidLevel = fluidLevel;
	}
	
	public Double getCurrentFluidLevel() {
		return currentFluidLevel;
	}	
	public void setCurrentFluidLevel(Double fluidLevel) {
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
	
	public Double getQuanta() {
		return quanta;
	}

	public void setQuanta(Double quanta) {
		this.quanta = quanta;
	}
	
	public Double getTimeToNextInternalTransition() {
		return timeToNextInternalTransition;
	}

	public void setTimeToNextInternalTransition(Double timeToNextInternalTransition) {
		this.timeToNextInternalTransition = timeToNextInternalTransition;
	}


	public Double getExactFluidLevel() {
		return exactFluidLevel;
	}

	public void setExactFluidLevel(Double exactFluidLevel, Double simulationTime) {
		this.exactFluidLevel = exactFluidLevel;
		this.setLastUpdate(simulationTime);
	}

	public Double getExactDrift() {
		return exactDrift;
	}

	public void setExactDrift(Double exactDrift) {
		this.exactDrift = exactDrift;		
	}

	public Double getChangeOfExactDrift() {
		return changeOfExactDrift;
	}
	
	public void setChangeOfExactDrift(Double changeOfDrift) {
		this.changeOfExactDrift = changeOfDrift;
	}	
	
	public Double getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Double lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	public void resetFluidLevel() {
		this.currentFluidLevel = this.originalFluidLevel;
		this.exactFluidLevel = this.originalFluidLevel;
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
	
	
	
	
	public void computeTimeToNextInternalTransition(ArrayList<Arc> arcs) {	
		
		if (upperBoundaryReached || lowerBoundaryReached){
			
			Double inFlux = 0.0;					
			Double outFlux = 0.0;
			Double changeOfInFlux = 0.0;
			Double changeOfOutFlux = 0.0;
			
			for (Arc arc: arcs){
				if (arc.getConnectedPlace().getId().equals(this.getId()) && !arc.getClass().equals(GuardArc.class)){
					if (arc.getConnectedTransition().getEnabled()) {
						
						if (((ContinuousArc)arc).getDirection() == ContinuousArcType.input){
							
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

				
			if (upperBoundaryReached && changeOfOutFlux > changeOfInFlux){
				
				timeToNextInternalTransition = (inFlux - outFlux) / (changeOfOutFlux - changeOfInFlux);
				return;
				
			} else if (lowerBoundaryReached && changeOfInFlux > changeOfOutFlux){
				
				timeToNextInternalTransition = (outFlux - inFlux) / (changeOfInFlux - changeOfOutFlux);
				return;
				
			}
		} 
		
		if (changeOfExactDrift != 0.0)		
			timeToNextInternalTransition = Math.sqrt(Math.abs((2 * quanta)/changeOfExactDrift));
		else		
			timeToNextInternalTransition = Double.POSITIVE_INFINITY;		
	}
		

	public void computeTimeToNextInternalTransitionFromExternal(ArrayList<Arc> arcs) {			
		
		if (upperBoundaryReached || lowerBoundaryReached){
			
			Double inFlux = 0.0;					
			Double outFlux = 0.0;
			Double changeOfInFlux = 0.0;
			Double changeOfOutFlux = 0.0;
			
			for (Arc arc: arcs){
				if (arc.getConnectedPlace().getId().equals(this.getId()) && !arc.getClass().equals(GuardArc.class)){
					if (arc.getConnectedTransition().getEnabled()) {
						
						if (((ContinuousArc)arc).getDirection() == ContinuousArcType.input){
							
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

				
			if (upperBoundaryReached && changeOfOutFlux > changeOfInFlux){
				
				timeToNextInternalTransition = (inFlux - outFlux) / (changeOfOutFlux - changeOfInFlux);
				return;
				
			} else if (lowerBoundaryReached && changeOfInFlux > changeOfOutFlux){
				
				timeToNextInternalTransition = (outFlux - inFlux) / (changeOfInFlux - changeOfOutFlux);
				return;
				
			}
		} 
		
		
		if (changeOfExactDrift != 0.0){
			
			Double termB = Math.pow((drift - exactDrift) / changeOfExactDrift, 2.0);
			Double termC = Math.sqrt(termB - 2.0*(currentFluidLevel - exactFluidLevel - quanta)/changeOfExactDrift);
			Double termD = Math.sqrt(termB - 2.0*(currentFluidLevel - exactFluidLevel + quanta)/changeOfExactDrift);
			Double termE = -1.0 * (drift - exactDrift) / changeOfExactDrift;
					
			Double s1 = termE + termC;
			Double s2 = termE - termC;
			Double s3 = termE + termD;
			Double s4 = termE - termD;
			
			if (s1 <= 0.0 || Double.isNaN(s1))
				s1 = Double.POSITIVE_INFINITY;
			
			if (s2 <= 0.0 || Double.isNaN(s2))
				s2 = Double.POSITIVE_INFINITY;
			
			if (s3 <= 0.0 || Double.isNaN(s3))
				s3 = Double.POSITIVE_INFINITY;
			
			if (s4 <= 0.0 || Double.isNaN(s4))
				s4 = Double.POSITIVE_INFINITY;
						
			timeToNextInternalTransition =  Math.min(Math.min(s1,  s2), Math.min(s3, s4));
		} else
			timeToNextInternalTransition = Double.POSITIVE_INFINITY; 
		
	}
	
	
	public void performInternalTransition(Double timePoint, Double previousDrift, Double previousChangeOfDrift, ArrayList<Arc> arcs){
		
		Double timeSinceLastInternalTransition = timePoint - lastUpdate;				
		Double fluid = exactFluidLevel;			
		fluid += previousDrift * timeSinceLastInternalTransition + previousChangeOfDrift/2.0 * Math.pow(timeSinceLastInternalTransition, 2.0);
		
		BigDecimal level = new BigDecimal(fluid);
		level = level.setScale(8,BigDecimal.ROUND_HALF_UP);
		if (level.doubleValue() <= 0.0 ) 
			fluid = 0.0;
		else if (!upperBoundaryInfinity && level.doubleValue() == upperBoundary)
			fluid = upperBoundary;
		else
			fluid = level.doubleValue();	
		
		setExactFluidLevel(fluid, timePoint);
		
		
		currentFluidLevel = exactFluidLevel;		
		drift = exactDrift;			

		
		computeTimeToNextInternalTransition(arcs);
				
	}
	

	private Double currentFluidLevel; 
	private Double originalFluidLevel;
	private Double upperBoundary;
	private Double drift = 0.0;	
	private Boolean upperBoundaryInfinity;
	private Boolean upperBoundaryReached;
	private Boolean lowerBoundaryReached;	
	private Double exactFluidLevel;//x
	private Double exactDrift = 0.0; // u
	private Double changeOfExactDrift = 0.0; //mu
	//TODO
	private Double quanta = 5.0;
	private Double timeToNextInternalTransition;
	private Double lastUpdate = 0.0;
		
	
	
	
}