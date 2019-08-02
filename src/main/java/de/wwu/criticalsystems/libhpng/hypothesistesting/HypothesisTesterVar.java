package de.wwu.criticalsystems.libhpng.hypothesistesting;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.model.HPnGModelVar;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;
import de.wwu.criticalsystems.libhpng.simulation.PropertyCheckerVar;

import java.util.logging.Logger;

public abstract class HypothesisTesterVar {


	public HypothesisTesterVar(HPnGModelVar model, Double time, Double boundary, Integer minNumberOfRuns, Logger logger, SimpleNode root, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{

		checker = new PropertyCheckerVar(root, model, time);
		checker.setLogger(logger);	

		this.checkLowerThan = checkLowerThan;
		this.invertPropertyAndThreshold = invertPropertyAndThreshold;
		this.minNumberOfRuns = minNumberOfRuns;	


		this.boundary = boundary;	
		if (boundary < 0.0 || boundary > 1.0){
			if (logger != null)
				logger.severe("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
			throw new InvalidPropertyException("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
		}
		if (invertPropertyAndThreshold)
			this.boundary = 1 - boundary;
			
	}
	
	
	public abstract Boolean doTesting(Integer currentRun, MarkingPlotVar plot) throws InvalidPropertyException;
	
		
	public Boolean getResultAchieved() {
		return resultAchieved;
	}
		
	public Boolean getPropertyFulfilled() {
		return propertyFulfilled;
	}

	public Boolean getTerminate(){
		return terminate;
	}
	
	public PropertyCheckerVar getChecker() {
		return checker;
	}


	protected Boolean resultAchieved = false;
	protected Boolean propertyFulfilled;
	protected Boolean checkLowerThan;
	protected Boolean invertPropertyAndThreshold;
	protected PropertyCheckerVar checker;
	protected Double boundary;
	protected Integer numberOfRuns;
	protected Integer minNumberOfRuns;
	protected Integer calcNumberOfRuns;
	protected Integer fulfilled;
	protected Boolean terminate = false;
	
	
	public void resetResults() {
		resultAchieved = false;
		propertyFulfilled = false;
		numberOfRuns = 0;
		terminate = false;
	}
	
	protected void checkPropertyForCurrentRun(Integer currentRun, MarkingPlotVar plot) throws InvalidPropertyException{
		
		if (currentRun == 1){			
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		
		if (checker.checkProperty(plot) != invertPropertyAndThreshold) 						
			fulfilled++;
		
		numberOfRuns++;
	}
	
}
