package de.wwu.criticalsystems.libhpng.confidenceintervals;

import java.util.logging.Logger;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public class ConfidenceIntervalCalculator {
	
	public ConfidenceIntervalCalculator(Byte intervalID, HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double confidenceLevel, Double halfIntervalWidth) throws InvalidPropertyException {
		
		this.minNumberOfRuns = minNumberOfRuns;
		this.halfIntervalWidth = halfIntervalWidth;
		this.intervalID = intervalID;
		
		checker = new PropertyChecker(root, model);
		checker.setLogger(logger);

		
		switch (intervalID){
		
			case 0:
				interval = new StandardConfidenceInterval(minNumberOfRuns, confidenceLevel, halfIntervalWidth);
				break;
				
			case 1:
				interval = new WaldConfidenceInterval(confidenceLevel);
				break;

			case 2:
				interval = new ClopperPearsonConfidenceInterval (confidenceLevel);
				break;
		}
		
		
	}
	
	
	
	public Double getMean() {
		return mean;
	}

	private Integer numberOfRuns=0;
	private Integer minNumberOfRuns;
	private Double mean;
	private PropertyChecker checker;
	private Double halfIntervalWidth;
	private Double currentHalfIntervalWidth;
	private Byte intervalID;
	private ConfidenceInterval interval;
	
	
	public void calculateConfidenceInterval(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		numberOfRuns = interval.calculateMeanAndHalfIntervalWidthForProperty(checker, currentRun, plot);
		mean = interval.getMean();
		currentHalfIntervalWidth = interval.getCurrentHalfIntervalWidth();

		
	}
	
	
	public Boolean checkBound(){
		
		if (numberOfRuns < minNumberOfRuns)
			return false;
		
		
		if (intervalID == 0)
			return ((StandardConfidenceInterval)interval).checkBound();
		
		
		return (currentHalfIntervalWidth  <= halfIntervalWidth);
	}
	
	
	public Double getLowerBorder(){
		return Math.max(0.0,(mean - currentHalfIntervalWidth));
	}
	
	
	public Double getUpperBorder(){
		return Math.min(1.0,(mean + currentHalfIntervalWidth));
	}
	
}
