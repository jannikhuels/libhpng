package de.wwu.criticalsystems.libhpng.confidenceintervals;

import umontreal.ssj.probdist.StudentDist;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public class StandardConfidenceInterval extends ConfidenceInterval {
	
	public StandardConfidenceInterval(Integer minNumberOfRuns, Double confidenceLevel, Double halfIntervalWidth) throws InvalidPropertyException {
		
		super(); 
		
		this.minNumberOfRuns = minNumberOfRuns;		
		this.confidenceLevel = confidenceLevel;
		this.halfIntervalWidth = halfIntervalWidth;
	}

	public Integer getNumberOfRuns() {
		return numberOfRuns;
	}
	
	public Double getMidpoint() {
		return midpoint;
	}	
	
	public Double getCurrentHalfIntervalWidth() {
		return currentHalfIntervalWidth;
	}

	private Integer numberOfRuns;
	private Integer minNumberOfRuns;
	private Integer fulfilled;
	private Double midpoint;
	private Double ssquare;
	private Double t;
	private Double confidenceLevel;
	private Double halfIntervalWidth;
	private Double currentHalfIntervalWidth;
	
	

	public Integer calculateMidpointAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		
		if (currentRun == 1){
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot))						
			fulfilled++;
				
		numberOfRuns++;	
		
		Double X = fulfilled.doubleValue();
		Double n = numberOfRuns.doubleValue();
		Double mean = X / n;
		
		if (numberOfRuns == 1)
			ssquare = 0.0;
		else
			ssquare = (X*(n - X))/(n*(n - 1.0));
		

		if (numberOfRuns < 2)
			t = 0.0;
		else {				
			Double alphaHalf = (1.0 - confidenceLevel)/2.0;
			t = StudentDist.inverseF(numberOfRuns - 1, 1.0 - alphaHalf);
		}
		
		currentHalfIntervalWidth = t * Math.sqrt(ssquare/numberOfRuns);
		midpoint = mean;
		
		return numberOfRuns;
	}
	

	
	public Boolean checkBound(){
		if (ssquare == null || (ssquare == 0.0 && numberOfRuns < minNumberOfRuns) || numberOfRuns < minNumberOfRuns) 
			return false;
		Double bound = Math.pow(t, 2.0)*ssquare / Math.pow(halfIntervalWidth, 2.0);
		return (bound <= numberOfRuns);
	}

	
}