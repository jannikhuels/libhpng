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


	private Integer fulfilled;
	private Double z;
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
			lower = (X + (z*z)/(2.0*n) - z*Math.sqrt((mean*(1.0-mean) + (z*z)/(4.0*n))/n))/(1.0 + (z*z)/n); 
		
		if (fulfilled == numberOfRuns)
			upper = 1.0;
		else
			upper  = (X + (z*z)/(2.0*n) + z*Math.sqrt((mean*(1.0-mean) + (z*z)/(4.0*n))/n))/(1.0 + (z*z)/n); 
		
		currentHalfIntervalWidth = 0.5* (upper - lower);
		midpoint =  mean*(n / (n + (z*z))) + 0.5*((z*z)/(n + (z*z)));
			
		return numberOfRuns;
	
		}
	
}