package de.wwu.criticalsystems.libhpng.simulation;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import umontreal.ssj.probdist.NormalDist;

public class GaussCIHypothesisTester {
	
	private Boolean resultAchieved = false;
	private Boolean propertyFulfilled;
	private PropertyChecker checker;
	private Boolean greaterThanHypothesis;
	private Double boundary;
	private Integer numberOfRuns;
	private Integer minNumberOfRuns;
	private Integer fulfilled;
	
	private Double Zn;
	private Integer fixedNumberOfRuns;
	private Integer calcNumberOfRuns1;
	private Integer calcNumberOfRuns2;
	private Double lowerBoundary;
	private Double upperBoundary;
	private NormalDist normalDist;
	private Boolean terminate;
	
	public GaussCIHypothesisTester(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double halfWidthOfIndifferenceRegion, Double guess, Double type1Error, Double type2Error, Boolean notEqual, Boolean greaterThanHypothesis) throws InvalidPropertyException{

		checker = new PropertyChecker(root, model);
		checker.setLogger(logger);	

		this.greaterThanHypothesis = greaterThanHypothesis;
		this.minNumberOfRuns = minNumberOfRuns;
		
		normalDist = new NormalDist();
		
		boundary = checker.getProbBoundary(root);		
		if (boundary < 0.0 || boundary > 1.0){
			if (logger != null)
				logger.severe("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
			throw new InvalidPropertyException("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
		}
		
		calcNumberOfRuns1 = (int) (Math.pow(( (normalDist.inverseF(1 - type1Error) * Math.sqrt (boundary * (1 - boundary)) - normalDist.inverseF(type2Error) * Math.sqrt( (boundary + guess) * (1 - boundary - guess)) ) / guess ), 2));
		calcNumberOfRuns2 = (int) (Math.pow(( (normalDist.inverseF(1 - type1Error) * Math.sqrt (boundary * (1 - boundary)) - normalDist.inverseF(type2Error) * Math.sqrt( (boundary - guess) * (1 - boundary - guess)) ) / guess ), 2));
		
		fixedNumberOfRuns =  Math.max(calcNumberOfRuns1, calcNumberOfRuns2);
		terminate = false;
		
		lowerBoundary = normalDist.inverseF(type1Error) * Math.sqrt(fixedNumberOfRuns * boundary * (1 - boundary));
		upperBoundary = -lowerBoundary;

		if (!notEqual){
			if (logger != null)
				logger.severe("Property Error: Gauss_CI hypothesis testing doesn't allow  tests for '>=' or '<='");
			throw new InvalidPropertyException("Property Error: Gauss-CI hypothesis testing doesn`t allow  tests for '>=' or '<='");
		}
		
	}
	
	public Boolean GCITesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		if (currentRun == 1){			
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot)){						
			fulfilled++;
		}numberOfRuns++;
		
		
		if(numberOfRuns.equals(fixedNumberOfRuns)){
			Zn = fulfilled - fixedNumberOfRuns * boundary;
			
			//accept H+1 hypothesis
			if (Zn > upperBoundary){
				resultAchieved = true;
				if(!greaterThanHypothesis){
					propertyFulfilled = false;
				}else propertyFulfilled = true;
			//accept H-1 hypothesis	
			}else if (Zn < lowerBoundary){
				resultAchieved = true;
				if (greaterThanHypothesis){
					propertyFulfilled = false;
				}else propertyFulfilled = true;
			}else if (lowerBoundary < Zn && upperBoundary > Zn){
				terminate = true;
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


	public Integer getFixedNumberOfRuns() {
		return fixedNumberOfRuns;
	}
	
	public Boolean getTerminate(){
		return terminate;
	}
}
