package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;

public class AzumaHypothesisTester extends HypothesisTester{
	
	
	public AzumaHypothesisTester(HPnGModel model, Double time, Double boundary, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double guess, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{
		
		super(model, time, boundary, minNumberOfRuns, logger, root, checkLowerThan, invertPropertyAndThreshold);
		
		a = (0.25 - 0.144* Math.pow(type1Error, 0.15)) * Math.sqrt((guess / 0.0243) ); 
		b = 0.75;
		k = Math.pow((Math.log(type1Error) / (8.0 * (Math.pow(a, 2.0)) * (2.0 - 3.0 * b))), (1.0 / (2.0 * b - 1.0)));
		
	}
	

	private Double zN;
	private Double a;
	private Double b;
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
			
			upperBoundary = a * (Math.pow((k + n), b));
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
