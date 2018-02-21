package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.logging.Logger;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidDistributionParameterException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelNotReadableException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.simulation.SampleGenerator;
import de.wwu.criticalsystems.libhpng.simulation.Simulator;



public class Testing {
	
	private HPnGModel model;
	private Integer minNumberOfRuns;
	private Logger logger;
	private SimpleNode root;
	private Double correctnessIndifferenceLevel;
	private Double powerIndifferenceLevel;
	private Double type1Error;
	private Double type2Error;
	private Double guess;
	private Boolean printRunResults;
	private Boolean checkLowerThan;
	private Boolean invertPropertyAndThreshold;
	private Integer maxNumberOfRuns;
	private Double currentTime;
	private MarkingPlot currentPlot;
	private Double maxTime;
	//private  ArrayList<MarkingPlot> plots = new ArrayList<MarkingPlot>();
	private Simulator simulator;
	Boolean fixedNumber;
	private Integer fixedNumberOfRuns;
	private Integer testRuns;
	
	private Integer fulfilled;
	private Integer notFulfilled;
	private Integer noResult;
	private Integer minRun;
	private Integer maxRun;
	private Integer totalRuns;

	
	
	public Testing (HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double correctnessIndifferenceLevel, Double powerIndifferenceLevel, Double guess, Double type1Error, Double type2Error, Boolean checkLowerThan, Boolean invertPropertyAndThreshold, Boolean printRunResults, Integer maxNumberOfRuns, Double currentTime, MarkingPlot currentPlot, Double maxTime, Simulator simulator, Boolean fixedNumber, Integer fixedNumberOfRuns, Integer testRuns) throws InvalidPropertyException, ModelNotReadableException, NullPointerException{
		
		this.model = model;
		this.minNumberOfRuns = minNumberOfRuns;
		this.logger = logger;
		this.root = root;
		this.correctnessIndifferenceLevel = correctnessIndifferenceLevel;
		this.powerIndifferenceLevel = powerIndifferenceLevel;
		this.type1Error = type1Error;
		this.type2Error = type2Error;
		this.printRunResults = printRunResults;
		this.checkLowerThan = checkLowerThan;
		this.invertPropertyAndThreshold = invertPropertyAndThreshold;
		this.maxNumberOfRuns = maxNumberOfRuns;
		this.currentTime = currentTime;
		this.currentPlot = currentPlot;
		this.maxTime = maxTime;
		this.simulator = simulator;
		this.fixedNumber = fixedNumber;
		this.fixedNumberOfRuns = fixedNumberOfRuns;
		this.guess = guess;
		this.testRuns = testRuns;
		
	
	}
	
	public void performTesting(Byte algorithmID, String algorithmName) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		
		
		fulfilled = 0;
		notFulfilled = 0;
		maxRun = 0;
		minRun = 0;
		noResult = 0;
		totalRuns = 0;
		int run = 0;
				
		HypothesisTester tester = null;
		SampleGenerator generator;
		
		
		Double fulfilledPercentage;
		Double notFulfilledPercentage;
		Double noResultPercentage;
		Integer averageRuns;
		
		
		switch (algorithmID){
		
			case 0:
				tester = new SequentialProbabilityRatioTester(model, minNumberOfRuns, logger, root, correctnessIndifferenceLevel, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold);
				break;
				
			case 1:
				tester = new GaussCIHypothesisTester(model, minNumberOfRuns, logger, root, powerIndifferenceLevel, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold);
				break;
				
			case 2:
				tester = new ChowRobbinsHypothesisTester(model, minNumberOfRuns, logger, root, powerIndifferenceLevel, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold);
				break;
				
			case 3:
				tester = new AzumaHypothesisTester(model, minNumberOfRuns, logger, root, guess, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold);
				break;
				
			case 4:
				tester = new ChernoffCIHypothesisTester(model, minNumberOfRuns, logger, root, powerIndifferenceLevel, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold);
				break;
			
			case 5:
				tester = new GaussSSPHypothesisTester(model, minNumberOfRuns, logger, root, correctnessIndifferenceLevel, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold);
				break;
				
			case 6:
				tester = new DarlingHypothesisTester(model, minNumberOfRuns, logger, root, guess, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold);
				break;
		}
		
		
		
		int n = 0;
		while(n < testRuns){

			generator = new SampleGenerator();
			generator.initializeRandomStream();
			if (logger != null){
				if (!invertPropertyAndThreshold){
					if (!checkLowerThan)
						logger.info("Simulation started for a 'P>x' property with " + algorithmName);
					else
						logger.info("Simulation started for a 'P<x' property with " + algorithmName);
				} else {
					if (!checkLowerThan)
						logger.info("Simulation started for a 'P<=x' property with " + algorithmName);
					else
						logger.info("Simulation started for a 'P>=x' property with " + algorithmName);
				}
			}
			
		
			if (!printRunResults)
				System.out.println("Running simulation...");
		
			
			if (fixedNumber){				
				
				for (run = 0; run < fixedNumberOfRuns; run++){
					
					if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
					
					currentTime = 0.0;
					try {
						model.resetMarking();
						generator.sampleGeneralTransitions(model, logger);
					} catch (InvalidDistributionParameterException e) {
						throw new ModelNotReadableException(e.getLocalizedMessage());				
					}
				
					currentPlot = new MarkingPlot(maxTime);
					//plots.add(currentPlot);
					currentPlot.initialize(model);
					
					//simulation
					while (currentTime <= maxTime)
						currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
					
					tester.doTesting(run + 1, currentPlot);
									
					if (printRunResults){
						System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
						model.printCurrentMarking(false, true);	
					}
				}
				
				if (tester.getResultAchieved()){
					if (tester.getPropertyFulfilled()){
						System.out.println(fixedNumberOfRuns + " runs simulated. The property is fulfilled");
						fulfilled ++;
						calcMaxRun(run);
						calcMinRun(run);
						totalRuns += run;
					} else {
						System.out.println(fixedNumberOfRuns + " runs simulated. The property is NOT fulfilled");
						notFulfilled ++;
						calcMaxRun(run);
						calcMinRun(run);
						totalRuns += run;
					}
					
				} else {
					if (tester.getTerminate())
						System.out.println("The number of " + fixedNumberOfRuns + " runs has been achieved without a decision on the property result.");
					else
						System.out.println(fixedNumberOfRuns + " runs simulated. Not enough runs to make a decision on the property result.");
					
					noResult++;
				}
				
				if (logger != null){
					logger.info("Simulation finished after " + fixedNumberOfRuns + " runs");
					if (tester.getResultAchieved()){
						if (tester.getPropertyFulfilled())
							logger.info("The property is fulfilled");
						else
							logger.info("The property is not fulfilled");
					} else
						logger.info("Not enough runs executed to make a decision on the property");
				}
			}else{

				run = 0;
				while (!tester.getResultAchieved() && run < maxNumberOfRuns && !tester.getTerminate()){
					
					if (printRunResults)
						System.out.println("Starting simulation run no." + (run+1));
					
					currentTime = 0.0;
					try {
						model.resetMarking();
						generator.sampleGeneralTransitions(model, logger);
					} catch (InvalidDistributionParameterException e) {
						throw new ModelNotReadableException(e.getLocalizedMessage());				
					}
				
					currentPlot = new MarkingPlot(maxTime);
					//plots.add(currentPlot);
					currentPlot.initialize(model);
					
					//simulation
					while (currentTime <= maxTime)
						currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);		
					
					tester.doTesting(run + 1, currentPlot);
					
					if (printRunResults){
						System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
						model.printCurrentMarking(false, true);	
					}
					
					run++;		
				}
				if (tester.getResultAchieved()){
					
					if (tester.getPropertyFulfilled()){
						System.out.println(run + " runs needed. The property is fulfilled");
						fulfilled ++;
						calcMaxRun(run);
						calcMinRun(run);
						totalRuns += run;

					}else{
						System.out.println(run + " runs needed. The property is NOT fulfilled");
						notFulfilled ++;
						calcMaxRun(run);
						calcMinRun(run);
						totalRuns += run;

						}
				}else{
					System.out.println("The maximum number of " + run + " runs has been achieved without a decision on the property result.");
					noResult++;
				}
				
				if (logger != null){
					logger.info("Simulation finished after " + run + " runs");
					if (tester.getResultAchieved()){
						if (tester.getPropertyFulfilled())
							logger.info("The property is fulfilled");
						else
							logger.info("The property is not fulfilled");
					} else
						logger.info("Not enough runs executed to make a decision on the property");
				}
				
				
				
			}
			tester.resetResults();
			n++;
		}
		fulfilledPercentage = calcPercentage(fulfilled);
		notFulfilledPercentage = calcPercentage(notFulfilled);
		noResultPercentage = calcPercentage(noResult);
		averageRuns = totalRuns/(fulfilled+notFulfilled + noResult);
		
		System.out.println("\n" + "Property is fulfilled in: " + fulfilledPercentage + "% (" + fulfilled + ") of " + testRuns +" tests"  );
		System.out.println("Property is not fulfilled in: " + notFulfilledPercentage + "% (" + notFulfilled + ") of " + testRuns +" tests"  );
		System.out.println("No result could be calculated in: " + noResultPercentage + "% (" + noResult + ") of " + testRuns +" tests" + "\n");
		System.out.println("Minimal number of simulation runs needed: " + minRun) ;
		System.out.println("Maximal number of simulation runs needed: " + maxRun);
		if(fulfilled > 0 || notFulfilled > 0)
			System.out.println("Average number of simulation runs needed: " + averageRuns + "\n");
		else 
			System.out.println("There where no successfull runs" + "\n");		
		
		//plots.clear();
		
	}	
		
		
	
	private void calcMinRun(int currRun){
		
		if(minRun == 0)
			minRun = currRun;
		
		if(minRun > currRun)
			minRun = currRun;
	}
	
	private void calcMaxRun(int currRun){
		
		if(maxRun < currRun)
			{maxRun = currRun;}
	}
	
	
	private Double calcPercentage(Integer calc){
		
		Double percentage = calc.doubleValue() / testRuns.doubleValue();
		percentage = percentage*100;
		return percentage;
		
	}
}
