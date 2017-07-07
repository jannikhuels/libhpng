package de.wwu.criticalsystems.libhpng.confidenceintervals;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public class AdjustedWaldConfidenceInterval extends ConfidenceInterval{
	
	public AdjustedWaldConfidenceInterval() throws InvalidPropertyException {
		
		super();
	}


	private Double lowerBoundary;
	private Double upperBoundary;
	private Double x;
	private Double n;
	
	
	public Integer calculateMidpointAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		checkPropertyForCurrentRun(checker, currentRun, plot);
		
		x = fulfilled.doubleValue();
		n = numberOfRuns.doubleValue();
		
		Double mean = x / n;
		
		if (fulfilled == 0)
			lowerBoundary = 0.0;
		else
			lowerBoundary = (x + 4.0/(2.0*n) - 2.0*Math.sqrt((mean*(1.0-mean) + 4.0/(4*n))/n))/(1.0 + 4.0/n); 
		
		if (fulfilled == numberOfRuns)
			upperBoundary = 1.0;
		else
			upperBoundary  = (x+ 4.0/(2.0*n) + 2.0*Math.sqrt((mean*(1.0-mean) + 4.0/(4*n))/n))/(1.0 + 4.0/n); 
		
		currentHalfIntervalWidth = 0.5* (upperBoundary - lowerBoundary);
		midpoint = (x + 2.0) / (n + 4.0);
				
		return numberOfRuns;
	
		}
	
}