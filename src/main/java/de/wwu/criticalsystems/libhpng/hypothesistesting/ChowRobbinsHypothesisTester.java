package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;
import umontreal.ssj.probdist.NormalDist;

public class ChowRobbinsHypothesisTester {
	
	private Boolean resultAchieved = false;
	private Boolean propertyFulfilled;
	private PropertyChecker checker;
	private Boolean greaterThanHypothesis;
	private Double boundary;
	private Integer numberOfRuns;
	private Integer minNumberOfRuns;
	private Integer fulfilled;
	
	//TODO: Naming Variable
	private Double lowerBoundary;
	private Double upperBoundary;
	private Double neededCIwidth;
	private Double currCIwidth;
	private Double fulfilledPercentage;
	private Double CIhalfWidth;
	//private Double guess;
	private NormalDist normalDist;
	private Double phiType1Error;
	private Double phiType2Error;
	private Boolean terminate;
	
	public ChowRobbinsHypothesisTester(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double halfWidthOfIndifferenceRegion, Double guess, Double type1Error, Double type2Error, Boolean notEqual, Boolean greaterThanHypothesis) throws InvalidPropertyException{

		checker = new PropertyChecker(root, model);
		checker.setLogger(logger);

		this.greaterThanHypothesis = greaterThanHypothesis;
		this.minNumberOfRuns = minNumberOfRuns;
	//	this.guess = guess;
		
		normalDist = new NormalDist();
		phiType1Error = normalDist.inverseF(type1Error);
		phiType2Error = normalDist.inverseF(type2Error);
		
		boundary = checker.getProbBoundary(root);		
		if (boundary < 0.0 || boundary > 1.0){
			if (logger != null)
				logger.severe("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
			throw new InvalidPropertyException("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
		}
		
		neededCIwidth = 2 * ( (guess) / (1 + (phiType2Error / phiType1Error)));
		terminate = false;
		CIhalfWidth = (guess) / (1 + (phiType2Error / phiType1Error));
		
		if (!notEqual){
			if (logger != null)
				logger.severe("Property Error: Gauss_CI hypothesis testing doesn't allow  tests for '>=' or '<='");
			throw new InvalidPropertyException("Property Error: Gauss-CI hypothesis testing doesn`t allow  tests for '>=' or '<='");
		}
		
	}
	
	public Boolean CRTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		if (currentRun == 1){			
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot)){						
			fulfilled++;
		}numberOfRuns++;
		
		if(numberOfRuns >= minNumberOfRuns){
			
			fulfilledPercentage = ((double) fulfilled) / numberOfRuns;
			 
			currCIwidth = 2 * phiType1Error * Math.sqrt((fulfilledPercentage * (1-fulfilledPercentage)) / (numberOfRuns));
			
			if(neededCIwidth >= Math.abs(currCIwidth) ){

			lowerBoundary = fulfilledPercentage - CIhalfWidth;
			upperBoundary = fulfilledPercentage + CIhalfWidth;
			
			//accept H+1 hypothesis
			if (lowerBoundary > boundary){
				resultAchieved = true;
				if(!greaterThanHypothesis){
					propertyFulfilled = false;
					}else propertyFulfilled = true;
			
			//accept H-1 Hypothesis	
			}else if (upperBoundary < boundary){
				resultAchieved = true;
				if (greaterThanHypothesis){
					propertyFulfilled = false;
					}else propertyFulfilled = true;
				}else if (lowerBoundary < boundary && boundary <upperBoundary){
					terminate = true;
				}
			}
		}
		return resultAchieved;
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
	
	//TODO: remove
	public Boolean getTerminate(){
		return terminate;
	}
	


}
