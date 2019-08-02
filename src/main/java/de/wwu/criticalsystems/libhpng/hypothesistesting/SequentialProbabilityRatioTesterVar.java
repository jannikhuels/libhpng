package de.wwu.criticalsystems.libhpng.hypothesistesting;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.model.HPnGModelVar;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;

import java.util.logging.Logger;

public class SequentialProbabilityRatioTesterVar extends HypothesisTesterVar{

	public SequentialProbabilityRatioTesterVar(HPnGModelVar model, Double time, Double boundary, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double correctnessIndifferenceLevel, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{
		
		super(model, time, boundary, minNumberOfRuns, logger, root, checkLowerThan, invertPropertyAndThreshold);
				
		A = (1.0 - type1Error) / type1Error;
		B = type1Error / (1.0 - type1Error);
		
		pminus1 = Math.max(0.0, boundary - correctnessIndifferenceLevel);
		pplus1= Math.min(1.0, boundary + correctnessIndifferenceLevel);		

	}
		

	private Double pplus1;
	private Double pminus1;
	private Double A;
	private Double B;
	private Double ratio;
	private Double x;
	private Double n;
	
	
	@Override
	public Boolean doTesting(Integer currentRun, MarkingPlotVar plot) throws InvalidPropertyException{
		
		checkPropertyForCurrentRun(currentRun, plot);		
		
		if (numberOfRuns >= minNumberOfRuns){
			
			x = fulfilled.doubleValue();
			n = numberOfRuns.doubleValue();
			ratio = (Math.pow(pminus1, x)*Math.pow(1.0 - pminus1, n - x)) / (Math.pow(pplus1, x)*Math.pow(1.0 - pplus1, n - x));			
				
			//accept H+1 hypothesis
			if (ratio < B){
				resultAchieved = true;				
				propertyFulfilled = !checkLowerThan;
			
				
			//accept H-1 hypothesis
			} else if (ratio > A){
				resultAchieved = true;
				propertyFulfilled = checkLowerThan;
			}	
					
		}				
		
		return resultAchieved;
	}

}
