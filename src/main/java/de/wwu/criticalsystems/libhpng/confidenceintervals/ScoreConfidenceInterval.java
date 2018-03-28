package de.wwu.criticalsystems.libhpng.confidenceintervals;

import umontreal.ssj.probdist.NormalDist;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public class ScoreConfidenceInterval extends ConfidenceInterval{
	
	public ScoreConfidenceInterval(Double confidenceLevel) throws InvalidPropertyException {
		
		super();
	
		Double alphaHalf = (1.0 - confidenceLevel)/2.0;
		z = NormalDist.inverseF01(1.0 - alphaHalf);
		
	}
	
	
	public Integer calculateMidpointAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		checkPropertyForCurrentRun(checker, currentRun, plot);
		
		
		x = fulfilled.doubleValue();
		n = numberOfRuns.doubleValue();
		mean = x / n;
		
		if (fulfilled == 0)
			lowerBoundary = 0.0;
		else
			lowerBoundary = (mean + (z*z)/(2.0*n) - z*Math.sqrt((mean*(1.0-mean) + (z*z)/(4.0*n))/n))/(1.0 + (z*z)/n); 
		
		if (fulfilled == numberOfRuns)
			upperBoundary = 1.0;
		else
			upperBoundary  = (mean + (z*z)/(2.0*n) + z*Math.sqrt((mean*(1.0-mean) + (z*z)/(4.0*n))/n))/(1.0 + (z*z)/n); 
		
		currentHalfIntervalWidth = 0.5* (upperBoundary - lowerBoundary);
		midpoint =  mean*(n / (n + (z*z))) + 0.5*((z*z)/(n + (z*z)));
			
		return numberOfRuns;	
	}


	private Double lowerBoundary;
	private Double upperBoundary;
	private Double x;
	private Double n;
	private Double mean;
	private Double z;
	
}