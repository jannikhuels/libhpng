package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import umontreal.ssj.probdist.NormalDist;

public class GaussCIHypothesisTester extends HypothesisTester{

	
	public GaussCIHypothesisTester(HPnGModel model, Double time, Double boundary, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double powerIndifferenceLevel, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{

		super(model, time, boundary, minNumberOfRuns, logger, root, checkLowerThan, invertPropertyAndThreshold);
		
		NormalDist normalDist = new NormalDist();		
		Double inverseOneMinusType1Error = normalDist.inverseF(1 - type1Error);
		Double inverseType2Error = normalDist.inverseF(type2Error);
		
		Integer calcNumberOfRuns1 = (int) Math.ceil((Math.pow(( (inverseOneMinusType1Error * Math.sqrt (boundary * (1.0 - boundary)) - inverseType2Error * Math.sqrt( (boundary + powerIndifferenceLevel) * (1.0 - boundary - powerIndifferenceLevel)) ) / powerIndifferenceLevel ), 2.0)));
		Integer calcNumberOfRuns2 = (int) Math.ceil((Math.pow(( (inverseOneMinusType1Error * Math.sqrt (boundary * (1.0 - boundary)) - inverseType2Error * Math.sqrt( (boundary - powerIndifferenceLevel) * (1.0 - boundary + powerIndifferenceLevel)) ) / powerIndifferenceLevel ), 2.0)));			
		calcNumberOfRuns =  Math.max(calcNumberOfRuns1, calcNumberOfRuns2);
		
		lowerBoundary = normalDist.inverseF(type1Error) * Math.sqrt(calcNumberOfRuns * boundary * (1.0 - boundary));
		upperBoundary = -lowerBoundary;
		
	}
	

	private Double lowerBoundary;
	private Double upperBoundary;
	private Integer calcNumberOfRuns;
	
	
	@Override
	public Boolean doTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		checkPropertyForCurrentRun(currentRun,plot);	
		
			
		if(numberOfRuns >= calcNumberOfRuns && numberOfRuns < (calcNumberOfRuns + 1)){
			
			Double zN = fulfilled.doubleValue() - calcNumberOfRuns.doubleValue() * boundary;
			
			//accept H+1 hypothesis
			if (zN > upperBoundary){
				
				resultAchieved = true;				
				propertyFulfilled = !checkLowerThan;
				
			//accept H-1 hypothesis	
			}else if (zN < lowerBoundary){
				
				resultAchieved = true;
				propertyFulfilled = checkLowerThan;
				
			}else {
				terminate = true;
			}
		}
		
		return resultAchieved;
	}
	

}
