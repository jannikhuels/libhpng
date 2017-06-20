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


	private Integer fulfilled;
	private Double beta_low;
	private Double beta_upp;
	private Double alphaHalf;
	
	
	public Integer calculateMeanAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		if (currentRun == 1){
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot))						
			fulfilled++;
				
		numberOfRuns++;	
		
		if (fulfilled == 0)
			beta_low = 0.0;
		else
			beta_low = BetaDist.inverseF(fulfilled, numberOfRuns - fulfilled + 1, alphaHalf);
		
		if (fulfilled == numberOfRuns)
			beta_upp = 1.0;
		else
			beta_upp  = BetaDist.inverseF(fulfilled + 1, numberOfRuns - fulfilled, 1.0 - alphaHalf);
		
		
		currentHalfIntervalWidth = 0.5* (beta_upp - beta_low);
		mean = beta_low + currentHalfIntervalWidth;
			
		return numberOfRuns;
	
		}
	
}