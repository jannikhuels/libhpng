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


	private Integer fulfilled;
	private Double z;
	
	
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
		
		currentHalfIntervalWidth = z * Math.sqrt(mean * ( 1.0 - mean) / n);
		midpoint = mean;
		
		return numberOfRuns;
	
		}
	
}