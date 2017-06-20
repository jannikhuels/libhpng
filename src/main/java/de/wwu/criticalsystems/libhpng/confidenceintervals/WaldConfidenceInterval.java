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
	
	
	public Integer calculateMeanAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		if (currentRun == 1){
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot))						
			fulfilled++;
				
		numberOfRuns++;	
		
		mean = fulfilled.doubleValue() / numberOfRuns.doubleValue();
		currentHalfIntervalWidth = z * Math.sqrt(mean * ( 1.0 - mean) / numberOfRuns.doubleValue());
		
		return numberOfRuns;
	
		}
	
}