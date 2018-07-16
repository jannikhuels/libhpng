package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import umontreal.ssj.probdist.NormalDist;

public class GaussSSPHypothesisTester extends HypothesisTester{
	
	
	public GaussSSPHypothesisTester(HPnGModel model, Double time, Double boundary, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double correctnessIndifferenceLevel, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{

		super(model, time, boundary, minNumberOfRuns, logger, root, checkLowerThan, invertPropertyAndThreshold);
		
		NormalDist normalDist = new NormalDist();		
		Double inverseType1Error = normalDist.inverseF(type1Error);
		Double inverseOneMinusType1Error = normalDist.inverseF(1 - type1Error);
		
		Integer calcNumberOfRuns1 = (int) Math.ceil(Math.pow(inverseOneMinusType1Error / correctnessIndifferenceLevel, 2.0) * (boundary - correctnessIndifferenceLevel) * (1 - boundary + correctnessIndifferenceLevel));
		Integer calcNumberOfRuns2 = (int) Math.ceil(Math.pow(inverseType1Error / correctnessIndifferenceLevel, 2.0) * (boundary + correctnessIndifferenceLevel) * (1 - boundary - correctnessIndifferenceLevel));
		calcNumberOfRuns =  Math.max(calcNumberOfRuns1, calcNumberOfRuns2);

	}
	
		
	private Integer calcNumberOfRuns;

	
	@Override
	public Boolean doTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		checkPropertyForCurrentRun(currentRun,plot);
		
			
		if(numberOfRuns >= calcNumberOfRuns && numberOfRuns < (calcNumberOfRuns + 1)){
			
			Double zN = fulfilled.doubleValue() - calcNumberOfRuns.doubleValue() * boundary;
			
			//accept H+1 hypothesis
			if (zN >= 0.0){
				
				resultAchieved = true;				
				propertyFulfilled = !checkLowerThan;
				
			//accept H-1 hypothesis	
			} else {
				
				resultAchieved = true;
				propertyFulfilled = checkLowerThan;

			}
		}
		
		return resultAchieved;
	}
	

}
