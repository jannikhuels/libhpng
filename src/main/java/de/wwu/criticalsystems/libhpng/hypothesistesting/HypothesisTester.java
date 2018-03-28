package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;

public abstract class HypothesisTester {
	
	
	public HypothesisTester(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Boolean checkLowerThan, Boolean invertPropertyAndThreshold) throws InvalidPropertyException{

		checker = new PropertyChecker(root, model);
		checker.setLogger(logger);	

		this.checkLowerThan = checkLowerThan;
		this.invertPropertyAndThreshold = invertPropertyAndThreshold;
		this.minNumberOfRuns = minNumberOfRuns;	


		boundary = this.checker.getProbBound(root);		
		if (boundary < 0.0 || boundary > 1.0){
			if (logger != null)
				logger.severe("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
			throw new InvalidPropertyException("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
		}
		if (invertPropertyAndThreshold)
			boundary = 1 - boundary;
			
	}
	
	
	public abstract Boolean doTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException;
	
		
	public Boolean getResultAchieved() {
		return resultAchieved;
	}
		
	public Boolean getPropertyFulfilled() {
		return propertyFulfilled;
	}

	public Boolean getTerminate(){
		return terminate;
	}
	
	public PropertyChecker getChecker() {
		return checker;
	}


	protected Boolean resultAchieved = false;
	protected Boolean propertyFulfilled;
	protected Boolean checkLowerThan;
	protected Boolean invertPropertyAndThreshold;
	protected PropertyChecker checker;
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
	
	protected void checkPropertyForCurrentRun(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		if (currentRun == 1){			
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		
		if (checker.checkProperty(plot) != invertPropertyAndThreshold) 						
			fulfilled++;
		
		numberOfRuns++;
	}
	
}
