package de.wwu.criticalsystems.libhpng.confidenceintervals;

import umontreal.ssj.probdist.NormalDist;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public class WaldConfidenceInterval extends ConfidenceInterval{
	
	public WaldConfidenceInterval(Double confidenceLevel) throws InvalidPropertyException {
		
		super();
	
		Double alphaHalf = (1.0 - confidenceLevel)/2.0;
		z = NormalDist.inverseF01(1.0 - alphaHalf);
		
	}


	private Double z;
	private Double n;
	private Double mean;
	
	public Integer calculateMidpointAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		checkPropertyForCurrentRun(checker, currentRun, plot);
		
		
		n = numberOfRuns.doubleValue();
		mean = fulfilled.doubleValue() / n;
		
		currentHalfIntervalWidth = z * Math.sqrt(mean * ( 1.0 - mean) / n);
		midpoint = mean;
		
		return numberOfRuns;
	
		}
	
}