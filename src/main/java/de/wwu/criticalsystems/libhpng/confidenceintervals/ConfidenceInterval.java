package de.wwu.criticalsystems.libhpng.confidenceintervals;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public abstract class ConfidenceInterval {

	public ConfidenceInterval() throws InvalidPropertyException { }

	public Integer getNumberOfRuns() {
		return numberOfRuns;
	}
	
	public Double getMean() {
		return mean;
	}	

	public Double getCurrentHalfIntervalWidth() {
		return currentHalfIntervalWidth;
	}

	protected Integer numberOfRuns;
	protected Double mean;
	protected Double currentHalfIntervalWidth;
	
	
	public abstract Integer calculateMeanAndHalfIntervalWidthForProperty(PropertyChecker checker, Integer currentRun, MarkingPlot plot) throws InvalidPropertyException;
}
