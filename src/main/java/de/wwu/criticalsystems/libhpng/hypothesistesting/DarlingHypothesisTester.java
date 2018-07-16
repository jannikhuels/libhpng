package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;

public class DarlingHypothesisTester extends HypothesisTester{
	
	
	public DarlingHypothesisTester(HPnGModel model, Double time, Double boundary, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double guess, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{
		
		super(model, time, boundary, minNumberOfRuns, logger, root, checkLowerThan, invertPropertyAndThreshold);
		
		Double x = Math.log(type1Error);
		Double y = Math.log(guess);
		
		a = Math.exp(0.4913 - 0.0715 * x + 0.0988 * y - 0.00089 * Math.pow(x, 2.0) + 0.00639 * Math.pow(y, 2.0) - 0.00361 * x * y);
		k = Math.pow((type1Error * (a - 1.0))/Math.sqrt(2.0), (-1.0 / (a - 1.0))) -1.0;

	}
	
	
	private Double zN;
	private Double a;
	private Double k;
	private Double n;
	private Double upperBoundary;
	private Double lowerBoundary;
	
	
	@Override
	public Boolean doTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		
		checkPropertyForCurrentRun(currentRun,plot);

		if(numberOfRuns >= minNumberOfRuns){
			
			n = numberOfRuns.doubleValue();
			zN = fulfilled.doubleValue() - n * boundary;
			
			upperBoundary = Math.sqrt(a * (n + 1.0) * Math.log(n + k));
			lowerBoundary = -upperBoundary;

			
			//accept H+1 hypothesis
			if (zN > upperBoundary){
				
				resultAchieved = true;				
				propertyFulfilled = !checkLowerThan;
				
			//accept H-1 hypothesis	
			}else if (zN < lowerBoundary){
				
				resultAchieved = true;
				propertyFulfilled = checkLowerThan;

			}
		}
		
		return resultAchieved;
	}

}
