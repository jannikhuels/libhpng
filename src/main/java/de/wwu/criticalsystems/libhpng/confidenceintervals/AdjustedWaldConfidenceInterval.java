package de.wwu.criticalsystems.libhpng.confidenceintervals;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public class AdjustedWaldConfidenceInterval extends ConfidenceInterval{
	
	public AdjustedWaldConfidenceInterval() throws InvalidPropertyException {
		
		super();
	}


	private Integer fulfilled;
	private Double lower;
	private Double upper;
	
	
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
		
		if (fulfilled == 0)
			lower = 0.0;
		else
			lower = (X + 4.0/(2.0*n) - 2.0*Math.sqrt((mean*(1.0-mean) + 4.0/(4*n))/n))/(1.0 + 4.0/n); 
		
		if (fulfilled == numberOfRuns)
			upper = 1.0;
		else
			upper  = (X+ 4.0/(2.0*n) + 2.0*Math.sqrt((mean*(1.0-mean) + 4.0/(4*n))/n))/(1.0 + 4.0/n); 
		
		currentHalfIntervalWidth = 0.5* (upper - lower);
		midpoint = (X + 2.0) / (n + 4.0);
				
		return numberOfRuns;
	
		}
	
}