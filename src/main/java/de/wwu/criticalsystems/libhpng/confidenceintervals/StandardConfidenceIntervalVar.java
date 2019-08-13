package de.wwu.criticalsystems.libhpng.confidenceintervals;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;
import de.wwu.criticalsystems.libhpng.simulation.PropertyCheckerVar;
import umontreal.ssj.probdist.StudentDist;

public class StandardConfidenceIntervalVar extends ConfidenceIntervalVar {

	public StandardConfidenceIntervalVar(Integer minNumberOfRuns, Double confidenceLevel, Double halfIntervalWidth) throws InvalidPropertyException {
		
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

	
	private Integer minNumberOfRuns;
	private Double ssquare;
	private Double t;
	private Double confidenceLevel;
	private Double halfIntervalWidth;
	private Double x;
	private Double n;
	private Double mean;
	

	public Integer calculateMidpointAndHalfIntervalWidthForProperty(PropertyCheckerVar checker, Integer currentRun, MarkingPlotVar plot) throws InvalidPropertyException {
		

		checkPropertyForCurrentRun(checker, currentRun, plot);
		
		
		x = fulfilled.doubleValue();
		n = numberOfRuns.doubleValue();
		mean = fulfilled.doubleValue() / n;
		
		if (numberOfRuns == 1)
			ssquare = 0.0;
		else
			ssquare = (x*(n - x))/(n*(n - 1.0));
		

		if (numberOfRuns < 2)
			t = 0.0;
		else {				
			Double alphaHalf = (1.0 - confidenceLevel)/2.0;
			t = StudentDist.inverseF(numberOfRuns - 1, 1.0 - alphaHalf);
		}
		
		currentHalfIntervalWidth = t * Math.sqrt(ssquare / n);
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