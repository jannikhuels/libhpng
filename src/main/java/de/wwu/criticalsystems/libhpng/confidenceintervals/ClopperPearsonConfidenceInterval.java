package de.wwu.criticalsystems.libhpng.confidenceintervals;

import umontreal.ssj.probdist.BetaDist;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public class ClopperPearsonConfidenceInterval extends ConfidenceInterval{
	
	public ClopperPearsonConfidenceInterval(Double confidenceLevel) throws InvalidPropertyException {
		
		super();
	
		alphaHalf = (1.0 - confidenceLevel)/2.0;
	}


	private Double beta_low;
	private Double beta_upp;
	private Double alphaHalf;
	private Double x;
	private Double n;
	
	
	public Integer calculateMidpointAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		checkPropertyForCurrentRun(checker, currentRun, plot);
		
		x = fulfilled.doubleValue();
		n = numberOfRuns.doubleValue();
		
		if (fulfilled == 0)
			beta_low = 0.0;
		else
			beta_low = BetaDist.inverseF(x, n - x+ 1.0, alphaHalf);
		
		if (fulfilled.equals(numberOfRuns))
			beta_upp = 1.0;
		else
			beta_upp  = BetaDist.inverseF(x + 1, n - x, 1.0 - alphaHalf);
		
		
		currentHalfIntervalWidth = 0.5* (beta_upp - beta_low);
		midpoint = beta_low + currentHalfIntervalWidth;
			
		return numberOfRuns;
	
		}
	
}