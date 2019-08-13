package de.wwu.criticalsystems.libhpng.confidenceintervals;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;
import de.wwu.criticalsystems.libhpng.simulation.PropertyCheckerVar;

public abstract class ConfidenceIntervalVar {

	public ConfidenceIntervalVar() throws InvalidPropertyException { }

	
	public abstract Integer calculateMidpointAndHalfIntervalWidthForProperty(PropertyCheckerVar checker, Integer currentRun, MarkingPlotVar plot) throws InvalidPropertyException;
	
	
	public Integer getNumberOfRuns() {
		return numberOfRuns;
	}
	
	public Double getMidpoint() {
		return midpoint;
	}	

	public Double getCurrentHalfIntervalWidth() {
		return currentHalfIntervalWidth;
	}
	
	
	protected Integer numberOfRuns;
	protected Double midpoint;
	protected Double currentHalfIntervalWidth;
	protected Integer fulfilled;
	
	
	protected void checkPropertyForCurrentRun(PropertyCheckerVar checker, Integer currentRun, MarkingPlotVar plot) throws InvalidPropertyException{
			
		if (currentRun == 1){
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot))						
			fulfilled++;
				
		numberOfRuns++;	
	}
}
