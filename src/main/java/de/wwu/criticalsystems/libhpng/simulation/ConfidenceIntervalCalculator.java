package de.wwu.criticalsystems.libhpng.simulation;

import java.util.logging.Logger;
import umontreal.ssj.probdist.StudentDist;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;

public class ConfidenceIntervalCalculator {
	
	public ConfidenceIntervalCalculator(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double confidenceLevel, Double halfIntervalWidth) throws InvalidPropertyException {
		
		this.minNumberOfRuns = minNumberOfRuns;
		checker = new PropertyChecker(root, model);
		checker.setLogger(logger);
		
		this.confidenceLevel = confidenceLevel;
		this.halfIntervalWidth = halfIntervalWidth;
	}

	public Integer getNumberOfRuns() {
		return numberOfRuns;
	}
	
	public Integer getMinNumberOfRuns() {
		return minNumberOfRuns;
	}
	
	public Double getSsquare() {
		return ssquare;
	}

	public Double getMean() {
		return mean;
	}	

	public Double getT() {
		return t;
	}

	private Integer numberOfRuns;
	private Integer minNumberOfRuns;
	private Integer fulfilled;
	private Double ssquare;
	private Double mean;
	private Double t;
	private PropertyChecker checker; 
	private Double confidenceLevel;
	private Double halfIntervalWidth;
	
	public void calculateSSquareForProperty(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException {
		
		if (currentRun == 1){
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot))						
			fulfilled++;
				
		numberOfRuns++;	
		mean = fulfilled.doubleValue() / numberOfRuns.doubleValue();
		
		if (numberOfRuns == 1)
			ssquare = 0.0;
		else
			ssquare = (fulfilled.doubleValue()*(numberOfRuns.doubleValue() - fulfilled.doubleValue()))/(numberOfRuns.doubleValue()*(numberOfRuns.doubleValue() - 1.0));		
	}
	

	public void findTDistribution(){
		
		if (numberOfRuns < 2)
			t = 0.0;
		else {				
			Double alphaHalf = (1.0 - confidenceLevel)/2.0;
			t = StudentDist.inverseF(numberOfRuns - 1, 1.0 - alphaHalf);
		}
	}
	
	
	public Boolean checkBound(){
		if (ssquare == null || ssquare == 0.0 || numberOfRuns < minNumberOfRuns) 
			return false;
		Double bound = Math.pow(t, 2.0)*ssquare / Math.pow(halfIntervalWidth, 2.0);
		return (bound <= numberOfRuns);
	}
	
	
	public Double getLowerBorder(){
		return Math.max(0.0,(mean - t * Math.sqrt(ssquare/numberOfRuns)));
	}
	
	
	public Double getUpperBorder(){
		return Math.min(1.0,(mean + t * Math.sqrt(ssquare/numberOfRuns)));
	}
}