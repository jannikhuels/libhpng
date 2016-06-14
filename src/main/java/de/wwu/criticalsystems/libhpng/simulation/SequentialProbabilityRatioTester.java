package de.wwu.criticalsystems.libhpng.simulation;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.PropertyError;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;

public class SequentialProbabilityRatioTester {
	
	public SequentialProbabilityRatioTester(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double confidenceParameter, Double type1Error, Double type2Error, Boolean lower) throws PropertyError{
		
		checker = new PropertyChecker(root, model);
		checker.setLogger(logger);	
		this.lower = lower;
		this.minNumberOfRuns = minNumberOfRuns;
		
		boundary = checker.getProbBoundary(root);		
		if (boundary < 0.0 || boundary > 1.0){
			if (logger != null)
				logger.severe("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
			throw new PropertyError("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
		}
		
		p0 = Math.min(1.0, boundary + confidenceParameter);
		p1 = Math.max(0.0, boundary - confidenceParameter);
		//p0 = boundary + confidenceParameter;
		//p1 = boundary - confidenceParameter;
		A = (1.0 - type2Error) / type1Error;
		B = type2Error / (1.0 - type1Error);
	}
	
	
	public Boolean getResultAchieved() {
		return resultAchieved;
	}
	
	
	public Boolean getPropertyFulfilled() {
		return propertyFulfilled;
	}
	
	
	public Integer getNumberOfRuns() {
		return numberOfRuns;
	}


	public Integer getMinNumberOfRuns() {
		return minNumberOfRuns;
	}



	private Boolean resultAchieved = false;
	private Boolean propertyFulfilled;
	private Double p0;
	private Double p1;
	private Double A;
	private Double B;
	private PropertyChecker checker;
	private Boolean lower;
	private Double boundary;
	private Integer numberOfRuns;
	private Integer minNumberOfRuns;
	private Integer fulfilled;
	
	
	public Boolean doRatioTest(Integer currentRun, MarkingPlot plot) throws PropertyError{
		
		if (currentRun == 1){
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot))						
			fulfilled++;
		numberOfRuns++;
		
		if (numberOfRuns >= minNumberOfRuns){
			
			Double ratio = (Math.pow(p1, fulfilled)*Math.pow(1.0 - p1, numberOfRuns - fulfilled)) / (Math.pow(p0, fulfilled)*Math.pow(1.0 - p0, numberOfRuns - fulfilled));
			
			if (ratio < B){
				resultAchieved = true;
				propertyFulfilled = true;
			} else if (ratio > A){
				resultAchieved = true;
				propertyFulfilled = false;
			}	
			
			if (resultAchieved && lower)
				propertyFulfilled = !propertyFulfilled;
			
		}				
		
		return resultAchieved;
	}

}
