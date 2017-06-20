package de.wwu.criticalsystems.libhpng.hypothesistesting;

import java.util.ArrayList;
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

public class HypothesisTesting {
	
	private HPnGModel model;
	private Integer minNumberOfRuns;
	private Logger logger;
	private SimpleNode root;
	private Double halfWidthOfIndifferenceRegion;
	private Double type1Error;
	private Double type2Error;
	private Double guess;
	private Boolean notEqual;
	private Boolean nullHypothesisLowerEqual;
	private Boolean printRunResults;
	private Integer maxNumberOfRuns;
	private Double currentTime;
	private MarkingPlot currentPlot;
	private Double maxTime;
	private  ArrayList<MarkingPlot> plots;
	private Simulator simulator;
	private Integer fixedNumberOfRuns;
	private Integer testRuns;
	
	private Integer fulfilled;
	private Integer notFulfilled;
	private Integer noResult;
	private Integer minRun;
	private Integer maxRun;
	private Integer totalRuns;

	public HypothesisTesting (HPnGModel model, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double halfWidthOfIndifferenceRegion, Double guess, Double type1Error, Double type2Error, Boolean notEqual, Boolean nullHypothesisLowerEqual, Boolean printRunResults, Integer maxNumberOfRuns, Double currentTime, MarkingPlot currentPlot, Double maxTime, ArrayList<MarkingPlot> plots, Simulator simulator, Integer fixedNumberOfRuns, Integer testRuns) throws InvalidPropertyException, ModelNotReadableException, NullPointerException{
		
		this.model = model;
		this.minNumberOfRuns = minNumberOfRuns;
		this.logger = logger;
		this.root = root;
		this.halfWidthOfIndifferenceRegion = halfWidthOfIndifferenceRegion;
		this.type1Error = type1Error;
		this.type2Error = type2Error;
		this.notEqual = notEqual;
		this.nullHypothesisLowerEqual = nullHypothesisLowerEqual;
		this.printRunResults = printRunResults;
		this.maxNumberOfRuns = maxNumberOfRuns;
		this.currentTime = currentTime;
		this.currentPlot = currentPlot;
		this.maxTime = maxTime;
		this.plots = plots;
		this.simulator = simulator;
		this.fixedNumberOfRuns = fixedNumberOfRuns;
		this.guess = guess;
		this.testRuns = testRuns;
		
	
	}
	
	public void performTesting(Byte algorithmID) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		fulfilled = 0;
		notFulfilled = 0;
		maxRun = 0;
		minRun = 0;
		noResult = 0;
		totalRuns = 0;
		
		if (fixedNumberOfRuns == 0){
			if (algorithmID == 0){
				int n = 0;
				while(n < testRuns){
					simulateAndTestSPR(notEqual, nullHypothesisLowerEqual); 
					n++;
				}
				System.out.println("\n" + "property is fulfilled in: " + calcPercentage(fulfilled) + "% (" + fulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("property is not fulfilled in: " + calcPercentage(notFulfilled) + "% (" + notFulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("no result could be calculated in: " + calcPercentage(noResult) + "% (" + noResult + ") of " + testRuns +" runs" + "\n");
				System.out.println("minimal number of runs needed: " + minRun + "\n");
				System.out.println("maximal number of runs needed: " + maxRun + "\n");
				if(fulfilled > 0 || notFulfilled > 0){
					System.out.println("average number of runs needed: " + totalRuns/(fulfilled+notFulfilled) + "\n");
					}else {System.out.println("there where no successfull runs" + "\n");}
				
			}else if (algorithmID == 1){
				int n = 0;
				while(n < testRuns){
					simulateAndTestGaussCI(notEqual, nullHypothesisLowerEqual); 
					n++;
				}
				System.out.println("\n" + "property is fulfilled in: " + calcPercentage(fulfilled) + "% (" + fulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("property is not fulfilled in: " + calcPercentage(notFulfilled) + "% (" + notFulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("no result could be calculated in: " + calcPercentage(noResult) + "% (" + noResult + ") of " + testRuns +" runs" + "\n");
				System.out.println("minimal number of runs needed: " + minRun + "\n");
				System.out.println("maximal number of runs needed: " + maxRun + "\n");
				if(fulfilled > 0 || notFulfilled > 0){
					System.out.println("average number of runs needed: " + totalRuns/(fulfilled+notFulfilled) + "\n");
					}else {System.out.println("there where no successfull runs" + "\n");}
				
			}else if (algorithmID == 2){
				int n = 0;
				while(n < testRuns){
					simulateAndTestCR(notEqual, nullHypothesisLowerEqual); 
					n++;
				}
				System.out.println("\n" + "property is fulfilled in: " + calcPercentage(fulfilled) + "% (" + fulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("property is not fulfilled in: " + calcPercentage(notFulfilled) + "% (" + notFulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("no result could be calculated in: " + calcPercentage(noResult) + "% (" + noResult + ") of " + testRuns +" runs" + "\n");
				System.out.println("minimal number of runs needed: " + minRun + "\n");
				System.out.println("maximal number of runs needed: " + maxRun + "\n");
				
				if(fulfilled > 0 || notFulfilled > 0){
					System.out.println("average number of runs needed: " + totalRuns/(fulfilled+notFulfilled) + "\n");
					}else {System.out.println("there where no successfull runs" + "\n");}
				
			}else if (algorithmID == 3){
				int n = 0;
				while(n < testRuns){
					simulateAndTestAzuma(notEqual, nullHypothesisLowerEqual); 
					n++;
				}
				System.out.println("\n" + "property is fulfilled in: " + calcPercentage(fulfilled) + "% (" + fulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("property is not fulfilled in: " + calcPercentage(notFulfilled) + "% (" + notFulfilled + ") of " + testRuns +" runs" + "\n");
				System.out.println("no result could be calculated in: " + calcPercentage(noResult) + "% (" + noResult + ") of " + testRuns +" runs" + "\n");
				System.out.println("minimal number of runs needed: " + minRun + "\n");
				System.out.println("maximal number of runs needed: " + maxRun + "\n");
				
				if(fulfilled > 0 || notFulfilled > 0){
					System.out.println("average number of runs needed: " + totalRuns/(fulfilled+notFulfilled) + "\n");
					}else {System.out.println("there where no successfull runs" + "\n");}
			
			}else {
				if (algorithmID == 0){
					simulateAndTestSPRWithFixedNumberOfRuns(notEqual, nullHypothesisLowerEqual);
				}else if (algorithmID == 1){
					simulateAndTestGaussCIWithFixedNumberOfRuns(notEqual, nullHypothesisLowerEqual);
				}else if (algorithmID == 2){
					simulateAndTestCRWithFixedNumberOfRuns(notEqual, nullHypothesisLowerEqual);
				}else if (algorithmID == 3){
					simulateAndTestAzumaWithFixedNumberOfRuns(notEqual, nullHypothesisLowerEqual);}
			}
		}
	}
	
	private void simulateAndTestGaussCI (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
		if (logger != null){
				if (nullHypothesisLowerEqual){
					logger.info("Simulation started for a 'P>x' property with Gauss Confidence Interval test");
				}else {
					logger.info("Simulation started for a 'P<x' property with Gauss Confidence Interval test");}		
		}
		
		GaussCIHypothesisTester tester = new GaussCIHypothesisTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		int run = 0;
		while (!tester.getResultAchieved() && run < maxNumberOfRuns && !tester.getTerminate()){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
			 
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);		
			
			tester.GCITesting(run + 1, currentPlot);
			
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
				maxRun(run);
				minRun(run);
				totalRuns += run;

			}else{
				System.out.println(run + " runs needed. The property is NOT fulfilled");
				notFulfilled++;
				maxRun(run);
				minRun(run);
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
	
	private void simulateAndTestCR (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
		if (logger != null){
				if (nullHypothesisLowerEqual){
					logger.info("Simulation started for a 'P>x' property with Chow-Robbins test");
				}else {
					logger.info("Simulation started for a 'P<x' property with Chow-Robbins test");}		
		}
		
		ChowRobbinsHypothesisTester tester = new ChowRobbinsHypothesisTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		int run = 0;
		while (!tester.getResultAchieved() && run < maxNumberOfRuns && !tester.getTerminate()){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
		
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);		
			
			tester.CRTesting(run + 1, currentPlot);
			
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
				maxRun(run);
				minRun(run);
				totalRuns += run;

			}else{
				System.out.println(run + " runs needed. The property is NOT fulfilled");
				notFulfilled ++;
				maxRun(run);
				minRun(run);
				totalRuns += run;
				}
		} else{
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
	
	private void simulateAndTestAzuma (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
		if (logger != null){
			if (nullHypothesisLowerEqual){
				logger.info("Simulation started for a 'P>x' property with Azuma test");
			}else {
				logger.info("Simulation started for a 'P<x' property with Azuma test");}		
	}		
		
		AzumaHypothesisTester tester = new AzumaHypothesisTester (model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		int run = 0;
		while (!tester.getResultAchieved() && run < maxNumberOfRuns){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
		
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);		
			
			tester.azumaTesting(run + 1, currentPlot);
			
			if (printRunResults){
				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
				model.printCurrentMarking(false, true);	
			}
			
			run++;		
		}
		if (tester.getResultAchieved()){
			
			System.out.print("Azuma funktion: " + tester.getAzuma_function());
			
			if (tester.getPropertyFulfilled()){
				System.out.println(run + " runs needed. The property is fulfilled");
				fulfilled ++;
				maxRun(run);
				minRun(run);
				totalRuns += run;

			}else{
				System.out.println(run + " runs needed. The property is NOT fulfilled");
				notFulfilled ++;
				maxRun(run);
				minRun(run);
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
	
	private void simulateAndTestSPR (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
		if (logger != null){
			if (notEqual){
				if (nullHypothesisLowerEqual)
					logger.info("Simulation started for a 'P>x' property with sequential probability ratio test");
				else
					logger.info("Simulation started for a 'P<x' property with sequential probability ratio test");
			} else {
				if (nullHypothesisLowerEqual)
					logger.info("Simulation started for a 'P<=x' property with sequential probability ratio test");
				else
					logger.info("Simulation started for a 'P>=x' property with sequential probability ratio test");
			}
		}
		
		SequentialProbabilityRatioTester tester = new SequentialProbabilityRatioTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		int run = 0;
		while (!tester.getResultAchieved() && run < maxNumberOfRuns){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
		
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);		
			
			tester.doRatioTest(run + 1, currentPlot);
			
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
				maxRun(run);
				minRun(run);
				totalRuns += run;

			}else{
				System.out.println(run + " runs needed. The property is NOT fulfilled");
				notFulfilled ++;
				maxRun(run);
				minRun(run);
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

	private void simulateAndTestGaussCIWithFixedNumberOfRuns (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
				
		if (logger != null){
			if (notEqual)
				logger.info("Simulation started for a 'P>x' property with Gauss Confidence Cnterval test");
			else
				logger.info("Simulation started for a 'P<x' property with Gauss Confidence Interval test");
		}
		
		GaussCIHypothesisTester tester = new GaussCIHypothesisTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		for (int run = 0; run < fixedNumberOfRuns; run++){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
		
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
			
			tester.GCITesting(run + 1, currentPlot);
			
			if (printRunResults){
				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
				model.printCurrentMarking(false, true);	
			}
			
			run++;		
		}
		
		if (tester.getResultAchieved()){
			if (tester.getPropertyFulfilled())
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is fulfilled");
			else
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is NOT fulfilled");
		} else
			System.out.println(fixedNumberOfRuns + " runs simulated. Not enough runs to make a decision on the property result.");
		
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
	}
	
	private void simulateAndTestCRWithFixedNumberOfRuns (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
				
		if (logger != null){
			if (notEqual)
				logger.info("Simulation started for a 'P>x' property with Chow-Robbins test");
			else
				logger.info("Simulation started for a 'P<x' property with Chow-Robbins test");
		}
		
		ChowRobbinsHypothesisTester tester = new ChowRobbinsHypothesisTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		for (int run = 0; run < fixedNumberOfRuns; run++){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
		
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
			
			tester.CRTesting(run + 1, currentPlot);
			
			if (printRunResults){
				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
				model.printCurrentMarking(false, true);	
			}
			
			run++;		
		}
		
		if (tester.getResultAchieved()){
			if (tester.getPropertyFulfilled())
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is fulfilled");
			else
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is NOT fulfilled");
		} else
			System.out.println(fixedNumberOfRuns + " runs simulated. Not enough runs to make a decision on the property result.");
		
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
	}

	private void simulateAndTestAzumaWithFixedNumberOfRuns (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
				
		if (logger != null){
			if (notEqual)
				logger.info("Simulation started for a 'P>x' property with Azuma test");
			else
				logger.info("Simulation started for a 'P<x' property with Azuma test");
		}
		
		AzumaHypothesisTester tester = new AzumaHypothesisTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		for (int run = 0; run < fixedNumberOfRuns; run++){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
		
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
			
			tester.azumaTesting(run + 1, currentPlot);
			
			if (printRunResults){
				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
				model.printCurrentMarking(false, true);	
			}
			
			run++;		
		}
		
		if (tester.getResultAchieved()){
			if (tester.getPropertyFulfilled())
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is fulfilled");
			else
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is NOT fulfilled");
		} else
			System.out.println(fixedNumberOfRuns + " runs simulated. Not enough runs to make a decision on the property result.");
		
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
	}

	private void simulateAndTestSPRWithFixedNumberOfRuns (Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
		
		if (logger != null){
			if (notEqual){
				if (nullHypothesisLowerEqual)
					logger.info("Simulation started for a 'P>x' property with sequential probability ratio test");
				else
					logger.info("Simulation started for a 'P<x' property with sequential probability ratio test");
			} else {
				if (nullHypothesisLowerEqual)
					logger.info("Simulation started for a 'P<=x' property with sequential probability ratio test");
				else
					logger.info("Simulation started for a 'P>=x' property with sequential probability ratio test");
			}
		}
		
		SequentialProbabilityRatioTester tester = new SequentialProbabilityRatioTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
		
		if (!printRunResults)
			System.out.println("Running simulation...");
		for (int run = 0; run < fixedNumberOfRuns; run++){
			
			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;
			try {
				model.resetMarking();
				generator.sampleGeneralTransitions(model, logger);
			} catch (InvalidDistributionParameterException e) {
				throw new ModelNotReadableException(e.getLocalizedMessage());				
			}
		
			currentPlot = new MarkingPlot(maxTime);
			plots.add(currentPlot);
			currentPlot.initialize(model);
			
			//simulation
			while (currentTime <= maxTime)
				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
			
			tester.doRatioTest(run + 1, currentPlot);
			
			if (printRunResults){
				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
				model.printCurrentMarking(false, true);	
			}
			
			run++;		
		}
		
		if (tester.getResultAchieved()){
			if (tester.getPropertyFulfilled())
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is fulfilled");
			else
				System.out.println(fixedNumberOfRuns + " runs simulated. The property is NOT fulfilled");
		} else
			System.out.println(fixedNumberOfRuns + " runs simulated. Not enough runs to make a decision on the property result.");
		
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
	}
	
	public void minRun(int currRun){
		if(minRun == 0){minRun = currRun;}
		if(minRun > currRun){minRun = currRun;}
	}
	
	public void maxRun(int currRun){
		if(maxRun < currRun){maxRun = currRun;}
	}
	
	public double calcPercentage(int calc){
		double curr =((double) calc) / testRuns;
		curr = curr*100;
		return curr;
	}
}
