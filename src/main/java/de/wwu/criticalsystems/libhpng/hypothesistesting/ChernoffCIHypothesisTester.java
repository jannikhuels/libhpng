package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;

public class ChernoffCIHypothesisTester extends HypothesisTester{
	

	public ChernoffCIHypothesisTester(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double powerIndifferenceLevel, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{

		super(model, minNumberOfRuns, logger, root, checkLowerThan, invertPropertyAndThreshold);

		calcNumberOfRuns = (2.0 * Math.sqrt(Math.log(type2Error)*Math.log(type1Error)) - Math.log(type1Error * type2Error)) / (2.0 * Math.pow(powerIndifferenceLevel, 2.0));		
		epsilon = Math.sqrt((1.0 / (2.0 * calcNumberOfRuns)) * Math.log(2.0 / type1Error));
		calcNumberOfRuns = Math.ceil(calcNumberOfRuns);
		
	}
	
	
	private Double epsilon;
	private Double calcNumberOfRuns;

	
	@Override
	public Boolean doTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		
		checkPropertyForCurrentRun(currentRun,plot);	
		
			
		if(numberOfRuns.doubleValue() >= calcNumberOfRuns && numberOfRuns.doubleValue() < (calcNumberOfRuns + 1.0)){
			
			Double mean = fulfilled.doubleValue() / numberOfRuns.doubleValue();
			
			//reject H0 hypothesis
			if (Math.abs(mean - boundary) > epsilon){
				
				//accept H+1 hypothesis
				if (mean > boundary){
					
					resultAchieved = true;				
					propertyFulfilled = !checkLowerThan;
					
				} else {
					
					resultAchieved = true;
					propertyFulfilled = checkLowerThan;					
				}					
				
			} else {
				terminate = true;
			}			
			
		}
		
		return resultAchieved;
	}
	

}
