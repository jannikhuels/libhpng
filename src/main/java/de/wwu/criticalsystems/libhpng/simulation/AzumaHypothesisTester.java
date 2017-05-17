package de.wwu.criticalsystems.libhpng.simulation;

import java.util.logging.Logger;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;

public class AzumaHypothesisTester {
	
	private Boolean resultAchieved = false;
	private Boolean propertyFulfilled;
	private PropertyChecker checker;
	private Boolean greaterThanHypothesis;
	private Double boundary;
	private Integer numberOfRuns;
	private Integer minNumberOfRuns;
	private Integer fulfilled;
	
	//TODO: Naming Variable
	private Double Zn;
	private Double Azuma_a;
	private Double Azuma_b;
	private Double Azuma_k;
	private Double Azuma_function;
	
	
	public AzumaHypothesisTester(HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double halfWidthOfIndifferenceRegion, Double guess, Double type1Error, Double type2Error, Boolean notEqual, Boolean greaterThanHypothesis) throws InvalidPropertyException{
		
		checker = new PropertyChecker(root, model);
		checker.setLogger(logger);	

		//true for P>theta property
		this.greaterThanHypothesis = greaterThanHypothesis;
		this.minNumberOfRuns = minNumberOfRuns;

		Azuma_a=(0.25 - 0.144* Math.pow(type1Error, 0.15)) * Math.sqrt( (guess / 0.0243) );
		Azuma_b = 0.75;
		Azuma_k= Math.pow(((Math.log(type1Error)) / (8 * (Math.pow(Azuma_a, 2)) * (2 - 3 * Azuma_b))), ((1) / (2 * Azuma_b - 1)));

		boundary = checker.getProbBoundary(root);		
		if (boundary < 0.0 || boundary > 1.0){
			if (logger != null)
				logger.severe("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
			throw new InvalidPropertyException("Property Error: the boundary node of the property root must be between 0.0 and 1.0");
		}
		
		if (!notEqual){
			if (logger != null)
				logger.severe("Property Error: Azuma hypothesis testing doesn't allow  tests for '>=' or '<='");
			throw new InvalidPropertyException("Property Error: Azuma hypothesis testing doesn`t allow  tests for '>=' or '<='");
		}	
	}
	
	public Boolean azumaTesting(Integer currentRun, MarkingPlot plot) throws InvalidPropertyException{
		
		
		if (currentRun == 1){
			numberOfRuns = 0;
			fulfilled = 0;
		}
					
		if (checker.checkProperty(plot)){						
			fulfilled++;
		}numberOfRuns++;

		if(numberOfRuns >= minNumberOfRuns){
			Zn = fulfilled - numberOfRuns*boundary;
			Azuma_function = Azuma_a* (Math.pow((Azuma_k + numberOfRuns), Azuma_b));

			if(Zn > -Azuma_function && Zn < Azuma_function){
				return resultAchieved;
				
				//accept H+1 hypothesis
				}else if(Zn >= Azuma_function){
					resultAchieved = true;
					if (!greaterThanHypothesis){
						propertyFulfilled = false;
						}else propertyFulfilled = true;
					
				//accept H-1 hypothesis	
				}else if (Zn <= -Azuma_function){
					resultAchieved = true;
					if (greaterThanHypothesis){
						propertyFulfilled = false;
						}else propertyFulfilled = true;
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
	public Double getAzuma_function(){
		return Azuma_function;
	}
}
