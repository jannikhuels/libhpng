package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import umontreal.ssj.probdist.NormalDist;

public class ChowRobbinsHypothesisTester extends HypothesisTester {
	

	public ChowRobbinsHypothesisTester(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double powerIndifferenceLevel, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{

		super(model, minNumberOfRuns, logger, root, checkLowerThan, invertPropertyAndThreshold);
		
		
		NormalDist normalDist = new NormalDist();		
		inverseType1Error = normalDist.inverseF(type1Error);
		Double inverseType2Error = normalDist.inverseF(type2Error);
	
		epsilon = powerIndifferenceLevel / (1.0 + (inverseType2Error / inverseType1Error));	
		requiredWidth = 2.0 * epsilon;
	}
	
	
	private Double lowerBoundary;
	private Double upperBoundary;
	private Double currentWidth;
	private Double mean;
	private Double n;
	private Double epsilon;
	private Double requiredWidth;
	private Double inverseType1Error;
	

	
	
	@Override
	public Boolean doTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
	
		checkPropertyForCurrentRun(currentRun,plot);
		
		
		if(numberOfRuns >= minNumberOfRuns){
					
			n = numberOfRuns.doubleValue();
			mean = fulfilled.doubleValue() / n;
			 
			currentWidth = 2.0 * inverseType1Error * Math.sqrt((mean * (1.0 - mean)) / (n));
			requiredWidth = 2.0 * epsilon;
			
			if (requiredWidth >= Math.abs(currentWidth) ){
	
				lowerBoundary = mean - epsilon;
				upperBoundary = mean + epsilon;
				
				//accept H+1 hypothesis
				if (lowerBoundary > boundary){
					
					resultAchieved = true;				
					propertyFulfilled = !checkLowerThan;
				
				//accept H-1 Hypothesis	
				}else if (upperBoundary < boundary){
					
					resultAchieved = true;
					propertyFulfilled = checkLowerThan;
				}				
			}			
		}
		
		return resultAchieved;		
		
	}
	
}
