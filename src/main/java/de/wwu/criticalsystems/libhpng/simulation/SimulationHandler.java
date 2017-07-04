package de.wwu.criticalsystems.libhpng.simulation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import de.wwu.criticalsystems.libhpng.confidenceintervals.ConfidenceIntervalCalculator;
import de.wwu.criticalsystems.libhpng.errorhandling.*;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.hypothesistesting.HypothesisTesting;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.plotting.ContinuousPlacesPlotter;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;

public class SimulationHandler {

	public SimulationHandler(){
		loadParameters();
	}
	
	public Double getHalfIntervalWidth() {
		return halfIntervalWidth;
	}
	public void setHalfIntervalWidth(Double halfIntervalWidth) throws InvalidSimulationParameterException {
		
		if (halfIntervalWidth < 0.0 || halfIntervalWidth > 1.0){
			if (logger != null)
				logger.severe("The half interval width parameter must be between 0.0 and 1.0");
			throw new InvalidSimulationParameterException("The half interval width parameter must be between 0.0 and 1.0");
		}			

		this.halfIntervalWidth = halfIntervalWidth;
		if (logger != null) logger.info("The half interval width has been changed to: " + halfIntervalWidth);
	}


	public Double getHalfWidthOfIndifferenceRegion() {
		return halfWidthOfIndifferenceRegion;
	}
	public void setHalfWidthOfIndifferenceRegion(Double halfWidthOfIndifferenceRegion) throws InvalidSimulationParameterException {
		
		if (halfWidthOfIndifferenceRegion <= 0.0 || halfWidthOfIndifferenceRegion > 1.0){
			if (logger != null)
				logger.severe("The half width of the indifference region must be between 0.0 and 1.0 but greater than 0.0");
			throw new InvalidSimulationParameterException("The half width of the indifference region must be between 0.0 and 1.0 but greater than 0.0");
		}
		this.halfWidthOfIndifferenceRegion = halfWidthOfIndifferenceRegion;
		if (logger != null) logger.info("The half width of the indifference region has been changed to: " + halfWidthOfIndifferenceRegion);
	}
	
	
	public Double getConfidenceLevel() {
		return confidenceLevel;
	}
	public void setConfidenceLevel(Double confidenceLevel) throws InvalidSimulationParameterException {
		
		if (confidenceLevel < 0.0 || confidenceLevel > 1.0){
			if (logger != null)
				logger.severe("The confidence level parameter must be between 0.0 and 1.0");
			throw new InvalidSimulationParameterException("The confidence level parameter must be between 0.0 and 1.0");
		}
		
		this.confidenceLevel = confidenceLevel;
		if (logger != null) logger.info("The confidence level has been changed to: " + confidenceLevel);
	}
	

	public Double getType1Error() {
		return type1Error;
	}
	public void setType1Error(Double type1Error) throws InvalidSimulationParameterException {
		
		if (type1Error < 0.0 || type1Error > 1.0){
			if (logger != null)
				logger.severe("The type 1 error parameter must be between 0.0 and 1.0");
			throw new InvalidSimulationParameterException("The type 1 error parameter must be between 0.0 and 1.0");
		}
		
		this.type1Error = type1Error;
		if (logger != null) logger.info("The type 1 error has been changed to: " + type1Error);
	}


	public Double getType2Error() {
		return type2Error;
	}
	public void setType2Error(Double type2Error) throws InvalidSimulationParameterException {
		
		if (type2Error < 0.0 || type2Error > 1.0){
			if (logger != null)
				logger.severe("The type 2 error parameter must be between 0.0 and 1.0");
			throw new InvalidSimulationParameterException("The type 2 error parameter must be between 0.0 and 1.0");
		}
		
		this.type2Error = type2Error;
		if (logger != null) logger.info("The type 2 error has been changed to: " + type2Error);
	}


	public Integer getFixedNumberOfRuns() {
		return fixedNumberOfRuns;
	}
	public void setFixedNumberOfRuns(Integer fixedNumberOfRuns) throws InvalidSimulationParameterException {
		
		if (fixedNumberOfRuns < minNumberOfRuns || fixedNumberOfRuns > maxNumberOfRuns){
			if (logger != null)
				logger.severe("The number of runs must be between the minimum and maximum number of runs");
			throw new InvalidSimulationParameterException("The number of runs must be between the minimum and maximum number of runs");
		}
		
		this.fixedNumberOfRuns = fixedNumberOfRuns;
		if (logger != null) logger.info("The (fixed) number of runs has been changed to: " + fixedNumberOfRuns);
	}


	public Integer getMinNumberOfRuns() {
		return minNumberOfRuns;
	}
	public void setMinNumberOfRuns(Integer minNumberOfRuns) throws InvalidSimulationParameterException {
		
		if (minNumberOfRuns < 1 || minNumberOfRuns > maxNumberOfRuns){
			if (logger != null)
				logger.severe("The minimum number of runs must be between 1 and maximum number of runs");
			throw new InvalidSimulationParameterException("The minimum number of runs must be between 1 and maximum number of runs");
		}
		
		this.minNumberOfRuns = minNumberOfRuns;
		if (logger != null) logger.info("The minimum number of runs has been changed to: " + minNumberOfRuns);
	}


	public Integer getMaxNumberOfRuns() {
		return maxNumberOfRuns;
	}
	public void setMaxNumberOfRuns(Integer maxNumberOfRuns) throws InvalidSimulationParameterException {

		if (maxNumberOfRuns < minNumberOfRuns || maxNumberOfRuns > 1000000){
			if (logger != null)
				logger.severe("The maximum number of runs must be between the minimum number of runs and 1 000 000");
			throw new InvalidSimulationParameterException("The maximum number of runs must be between the minimum number of runs and 1 000 000");
		}
		
		this.maxNumberOfRuns = maxNumberOfRuns;
		if (logger != null) logger.info("The maximum number of runs has been changed to: " + maxNumberOfRuns);
	}

	
	public Boolean getSimulationWithFixedNumberOfRuns() {
		return simulationWithFixedNumberOfRuns;
	}
	public void setSimulationWithFixedNumberOfRuns(Boolean simulationWithFixedNumberOfRuns) {
		this.simulationWithFixedNumberOfRuns = simulationWithFixedNumberOfRuns;
		if (logger != null){
			if (simulationWithFixedNumberOfRuns)
				logger.info("The simulation has been set to simulation with fixed number of runs");
			else
				logger.info("The simulation has been set to simulation with optimal number of runs");
		}
	}	
	
	

	public String getAlgorithmName(){
		
		switch(algorithmID){		
			case 0:
				return "Sequential Probability Ratio Test";
			case 1:
				return "Gauss Confidence Interval Test";
			case 2:
				return "Chow Robbins Test";
			case 3:
				return "Azuma Test";
		}
		return "";
	}
	
		
	public void setAlgorithmID(String algorithmName) throws InvalidSimulationParameterException {
		
		
		switch (algorithmName){
			case "SPR":
				this.algorithmID = 0;
				break;
			case "GCI":
				this.algorithmID = 1;
				break;
			case "CR":
				this.algorithmID = 2;
				break;
			case "Azuma":
				this.algorithmID = 3;
				break;
				
			default:	
				if (logger != null)
					logger.severe("Invalid algorithm, type help to see valid abbreviation");
				throw new InvalidSimulationParameterException("Invalid algorithm");
		}
		
		if (logger != null) logger.info("The algorithm used for hypothesis testing has been changed to : " + algorithmName);			
	}
	
		
	public void setGuess(Double guess) throws InvalidSimulationParameterException {
		this.guess = guess;
		
		if (guess < 0.0 || guess > 1.0){
			if (logger != null)
				logger.severe("The value of the guess parameter must be between 0.0 and 1.0");
			throw new InvalidSimulationParameterException("The value of the guess parameter must be between 0.0 and 1.0");
		}
		if (logger != null) logger.info("The value of guess has been changed to: " + guess);

	}
	
	public Double getGuess(){
		return guess;
	}
		
		
	public void setNumberOfTestRuns(Integer testRuns) throws InvalidSimulationParameterException {
		this.testRuns = testRuns;
		
		if (testRuns <= 0){
			if (logger != null)
				logger.severe("The number of runs for hypothesis testing must be at least 1");
			throw new InvalidSimulationParameterException("The number of runs for hypothesis testing must be at least 1");
		}
		if (logger != null) logger.info("The value of guess has been changed to: " + guess);
	}
	
	
	public Integer getTestRuns(){
		return testRuns;		
	}
		
	
	public Boolean getPrintRunResults() {
		return printRunResults;
	}
	public void setPrintRunResults(Boolean printRunResults) {
		this.printRunResults = printRunResults;
		if (logger != null){
			if (printRunResults)
				logger.info("Printing of results has been turned on.");
			else
				logger.info("Printing of results has been turned off.");
		}
	}
	
	
	public String getIntervalName(){
		
		switch(intervalID){		
			case 0:
				return "Standard";
			case 1:
				return "Wald";
			case 2:
				return "Clopper Pearson";
			case 3:
				return "Score";
			case 4:
				return "adjWald";

		}
		return "";
	}
	
		
	public void setIntervalID(String intervalName) throws InvalidSimulationParameterException {
		
		
		switch (intervalName){
			case "Standard":
				this.intervalID = 0;
				break;
			case "Wald":
				this.intervalID = 1;
				break;
			case "CP":
				this.intervalID = 2;
				break;
			case "Score":
				this.intervalID = 3;
				break;
			case "adjWald":
				this.intervalID = 4;
				break;
			
				
			default:	
				if (logger != null)
					logger.severe("Invalid confidence interval caclulation approach, type help to see valid abbreviation");
				throw new InvalidSimulationParameterException("Invalid interval type");
		}
		
		if (logger != null) logger.info("The approach for the calculation of confidence intervals has been changed to : " + intervalName);			
	}
	
	public Logger getLogger() {
		return logger;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	
	//simulation parameters
	private Double halfIntervalWidth = 0.0;
	private Double halfWidthOfIndifferenceRegion;
	private Double confidenceLevel;
	private Double type1Error;
	private Double type2Error;
	private Integer fixedNumberOfRuns;
	private Integer minNumberOfRuns;
	private Integer maxNumberOfRuns;
	private Boolean simulationWithFixedNumberOfRuns;
	private Boolean printRunResults;
	private Byte algorithmID;
	private Double guess;
	private Integer testRuns;
	private Byte intervalID;
	
	private Simulator simulator;
	private Logger logger;
	private HPnGModel model;
	private SimpleNode root;
	private Double maxTime;
	private Double currentTime;
	private ArrayList<MarkingPlot> plots = new ArrayList<MarkingPlot>();
	private MarkingPlot currentPlot;

	
	
	public void simulateAndPlotOnly(Double maxTime, HPnGModel model) throws ModelNotReadableException, InvalidRandomVariateGeneratorException{
		
		this.maxTime = maxTime;
		this.model = model;
		
		
		
		//int cores = 1;//Runtime.getRuntime().availableProcessors();
		
		
		simulator = new Simulator(model, maxTime);
		simulator.setLogger(logger);
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
		if (logger != null)
			logger.info("Simulation started with plotting continuous places only");
		if (!printRunResults)
			System.out.println("Running simulation...");
				
		for (int run = 0; run < fixedNumberOfRuns; run++){			
			
			if (printRunResults)
				System.out.println("Starting simulation run no." + (run+1));
			
			currentTime = 0.0;			
			try {
				model.resetMarking();
				if (printRunResults)
					model.printCurrentMarking(true, false);
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
			
			if (printRunResults){
				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
				model.printCurrentMarking(false, true);	
			}
			
			ContinuousPlacesPlotter plotter = new ContinuousPlacesPlotter();
			plotter.plotContinuousPlaces(model, plots, maxTime, confidenceLevel);
			if (logger != null)
				logger.info("Simulation finished after " + fixedNumberOfRuns + " runs");
			
		}

	}
	
	
	public void simulateAndCheckProperty(HPnGModel model, SimpleNode root) throws InvalidPropertyException, ModelNotReadableException, InvalidRandomVariateGeneratorException{
		
		final long timeStart = System.currentTimeMillis();
	     		
		this.model = model;
		this.root = root;
		this.maxTime = PropertyChecker.getMaxTimeForSimulation(root);
		
		simulator = new Simulator(model, maxTime);
		simulator.setLogger(logger);
		
		String prob = "";
		try {
			prob = PropertyChecker.getProbKind(root);
		} catch (InvalidPropertyException e) {		
			if (logger != null)
				logger.severe(e.getLocalizedMessage());
			throw new InvalidPropertyException(e.getLocalizedMessage());
		}
	
			
		try {
			switch (prob){
				case "PROBQ":
					//property check with confidence interval calculation
					if (simulationWithFixedNumberOfRuns)
						simulateAndCheckPROBQWithFixedNumberOfRuns();
					else
						simulateAndCheckPROBQ();
					break;
				case "PROBGE":
					//property check with hypothesis testing	
					if (simulationWithFixedNumberOfRuns)
						simulateAndTestPROBWithFixedNumberOfRuns(false, false);	
					else
						simulateAndTestPROB(false, false);	
					break;
				case "PROBL":
					//property check with hypothesis testing	
					if (simulationWithFixedNumberOfRuns)
						simulateAndTestPROBWithFixedNumberOfRuns(true, false);	
					else
						simulateAndTestPROB(true, false);			
					break;
				case "PROBLE":
					//property check with hypothesis testing	
					if (simulationWithFixedNumberOfRuns)
						simulateAndTestPROBWithFixedNumberOfRuns(false, true);	
					else
						simulateAndTestPROB(false, true);	
					break;
				case "PROBG":
					//property check with hypothesis testing	
					if (simulationWithFixedNumberOfRuns)
						simulateAndTestPROBWithFixedNumberOfRuns(true, true);	
					else
						simulateAndTestPROB(true, true);			
					break;
			}
			
					
		} catch (ModelNotReadableException e) {		
			if (logger != null) 
				logger.severe("The simulation could not be executed.");
			throw new ModelNotReadableException(e.getLocalizedMessage());
			
		} catch (InvalidRandomVariateGeneratorException e) {
			if (logger != null) 
				logger.severe("The simulation could not be executed.");
			throw new InvalidRandomVariateGeneratorException(e.getLocalizedMessage());
		}
		
		final long timeEnd = System.currentTimeMillis(); 
		System.out.printf("Time needed: " + "%,.2f" + "  ms.%n" ,(double)(timeEnd - timeStart));

	}
	
	
//	private void simulateAndCheckPROBQ() throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
//			
//		SampleGenerator generator = new SampleGenerator();
//		generator.initializeRandomStream();
//				
//		if (logger != null)
//			logger.info("Simulation started for a 'P=?' property with fixed interval width");
//	
//		StandardConfidenceInterval calc = new StandardConfidenceInterval(model, minNumberOfRuns, logger, root, confidenceLevel, halfIntervalWidth);
//		
//		if (!printRunResults)
//			System.out.println("Running simulation...");
//		int run = 0;
//		while (!calc.checkBound() && run < maxNumberOfRuns){
//			
//			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
//			
//			currentTime = 0.0;
//			try {
//				model.resetMarking();
//				generator.sampleGeneralTransitions(model, logger);
//			} catch (InvalidDistributionParameterException e) {
//				throw new ModelNotReadableException(e.getLocalizedMessage());				
//			}
//		
//			currentPlot = new MarkingPlot(maxTime);
//			plots.add(currentPlot);
//			currentPlot.initialize(model);
//			
//			//simulation
//			while (currentTime <= maxTime)
//				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
//			
//			calc.calculateSSquareForProperty(run+1, currentPlot);
//			calc.findTDistribution();
//			
//			if (printRunResults){
//				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
//				model.printCurrentMarking(false, true);	
//			}
//			
//			
//			run++;		
//		}
//		
//		System.out.println(run + " runs needed. Midpoint: value: " + calc.getMidpoint() + ".");
//		System.out.println("Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
//		
//		if (logger != null){
//			logger.info("Simulation finished after " + run + " runs");
//			logger.info("Simulation results: Midpoint: " + calc.getMidpoint() + ". Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
//		}
//	}
//	
	
	
	
	private void simulateAndCheckPROBQ() throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		
	SampleGenerator generator = new SampleGenerator();
	generator.initializeRandomStream();
			
	if (logger != null)
		logger.info("Simulation started for a 'P=?' property with fixed interval width");

	ConfidenceIntervalCalculator calc = new ConfidenceIntervalCalculator(intervalID, model, minNumberOfRuns, logger, root, confidenceLevel, halfIntervalWidth);
	
	if (!printRunResults)
		System.out.println("Running simulation...");
	int run = 0;
	while (!calc.checkBound() && run < maxNumberOfRuns){
		
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
		
		calc.calculateConfidenceInterval(run+1, currentPlot);
				
		if (printRunResults){
			System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
			model.printCurrentMarking(false, true);	
		}
		
		
		run++;		
	}
	
	System.out.println(run + " runs needed. Midpoint: " + calc.getMidpoint() + ".");
	System.out.println("Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
	
	if (logger != null){
		logger.info("Simulation finished after " + run + " runs");
		logger.info("Simulation results: Midpoint: " + calc.getMidpoint() + ". Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
	}
}

//	
//	private void simulateAndCheckPROBQWithFixedNumberOfRuns() throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
//		
//		SampleGenerator generator = new SampleGenerator();
//		generator.initializeRandomStream();
//		
//		if (logger != null)
//			logger.info("Simulation started for a 'P=?' property with fixed number of runs");
//		
//		
//		StandardConfidenceInterval calc = new StandardConfidenceInterval(model, fixedNumberOfRuns, logger, root, confidenceLevel, halfIntervalWidth);
//		
//		if (!printRunResults)
//			System.out.println("Running simulation...");
//		for (int run = 0; run < fixedNumberOfRuns; run++){
//			
//			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
//
//			currentTime = 0.0;
//			try {
//				model.resetMarking();
//				generator.sampleGeneralTransitions(model, logger);
//			} catch (InvalidDistributionParameterException e) {
//				throw new ModelNotReadableException(e.getLocalizedMessage());				
//			}
//			currentPlot = new MarkingPlot(maxTime);
//			plots.add(currentPlot);
//			currentPlot.initialize(model);
//			
//			//simulation
//			while (currentTime <= maxTime)
//				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
//			
//			calc.calculateSSquareForProperty(run+1, currentPlot);
//			calc.findTDistribution();
//			
//			if (printRunResults){
//				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
//				model.printCurrentMarking(false, true);	
//			}
//				
//		}
//		System.out.println(fixedNumberOfRuns + " runs simulated. Midpoint: " + calc.getMidpoint() + ".");
//		System.out.println("Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
//		
//		if (logger != null){
//			logger.info("Simulation finished after " + fixedNumberOfRuns + " runs");
//			logger.info("Simulation results: Midpoint: " + calc.getMidpoint() + ". Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
//		}
//	}	
		
	
	private void simulateAndCheckPROBQWithFixedNumberOfRuns() throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
			
		SampleGenerator generator = new SampleGenerator();
		generator.initializeRandomStream();
		
		if (logger != null)
			logger.info("Simulation started for a 'P=?' property with fixed number of runs");
		
		
		ConfidenceIntervalCalculator calc = new ConfidenceIntervalCalculator(intervalID, model, fixedNumberOfRuns, logger, root, confidenceLevel, halfIntervalWidth);
		
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
			
			calc.calculateConfidenceInterval(run+1, currentPlot);
						
			if (printRunResults){
				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
				model.printCurrentMarking(false, true);	
			}
				
		}
		System.out.println(fixedNumberOfRuns + " runs simulated. Midpoint: " + calc.getMidpoint() + ".");
		System.out.println("Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
		
		if (logger != null){
			logger.info("Simulation finished after " + fixedNumberOfRuns + " runs");
			logger.info("Simulation results: Midpoint: " + calc.getMidpoint() + ". Resulting confidence interval borders:" + calc.getLowerBorder() + " & " + calc.getUpperBorder() + " (one sided interval width = " + (calc.getUpperBorder() - calc.getLowerBorder())/2.0 + ")");
		}
	}	
		
	
	
	private void simulateAndTestPROB(Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
		
		HypothesisTesting tester = new HypothesisTesting(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual, printRunResults, maxNumberOfRuns, currentTime, currentPlot, maxTime, plots, simulator, 0, testRuns);
		tester.performTesting(algorithmID);
	}
	
	
	
	private void simulateAndTestPROBWithFixedNumberOfRuns(Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{

		HypothesisTesting tester = new HypothesisTesting(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, guess, type1Error, type2Error, notEqual, nullHypothesisLowerEqual, printRunResults, 0, currentTime, currentPlot, maxTime, plots, simulator, fixedNumberOfRuns, testRuns);
		tester.performTesting(algorithmID);		
	}
	

	
//	private void simulateAndTestPROB(Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
//		
//		SampleGenerator generator = new SampleGenerator();
//		generator.initializeRandomStream();
//		
//		if (logger != null){
//			if (notEqual){
//				if (nullHypothesisLowerEqual)
//					logger.info("Simulation started for a 'P>x' property with sequential probability ratio test");
//				else
//					logger.info("Simulation started for a 'P<x' property with sequential probability ratio test");
//			} else {
//				if (nullHypothesisLowerEqual)
//					logger.info("Simulation started for a 'P<=x' property with sequential probability ratio test");
//				else
//					logger.info("Simulation started for a 'P>=x' property with sequential probability ratio test");
//			}
//		}
//		
//		SequentialProbabilityRatioTester tester = new SequentialProbabilityRatioTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
//		
//		if (!printRunResults)
//			System.out.println("Running simulation...");
//		int run = 0;
//		while (!tester.getResultAchieved() && run < maxNumberOfRuns){
//			
//			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
//			
//			currentTime = 0.0;
//			try {
//				model.resetMarking();
//				generator.sampleGeneralTransitions(model, logger);
//			} catch (InvalidDistributionParameterException e) {
//				throw new ModelNotReadableException(e.getLocalizedMessage());				
//			}
//		
//			currentPlot = new MarkingPlot(maxTime);
//			plots.add(currentPlot);
//			currentPlot.initialize(model);
//			
//			//simulation
//			while (currentTime <= maxTime)
//				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
//			
//			tester.doRatioTest(run + 1, currentPlot);
//			
//			if (printRunResults){
//				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
//				model.printCurrentMarking(false, true);	
//			}
//			
//			run++;		
//		}
//		if (tester.getResultAchieved()){
//			if (tester.getPropertyFulfilled())
//				System.out.println(run + " runs needed. The property is fulfilled");
//			else
//				System.out.println(run + " runs needed. The property is NOT fulfilled");
//		} else
//			System.out.println("The maximum number of " + run + " runs has been achieved without a decision on the property result.");
//		
//		if (logger != null){
//			logger.info("Simulation finished after " + run + " runs");
//			if (tester.getResultAchieved()){
//				if (tester.getPropertyFulfilled())
//					logger.info("The property is fulfilled");
//				else
//					logger.info("The property is not fulfilled");
//			} else
//				logger.info("Not enough runs executed to make a decision on the property");
//		}
//	}
//	
//	
//	private void simulateAndTestPROBWithFixedNumberOfRuns(Boolean notEqual, Boolean nullHypothesisLowerEqual) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException{
//
//		SampleGenerator generator = new SampleGenerator();
//		generator.initializeRandomStream();
//				
//		if (logger != null){
//			if (notEqual)
//				logger.info("Simulation started for a 'P<x' property with sequential probability ratio test");
//			else
//				logger.info("Simulation started for a 'P>=x' property with sequential probability ratio test");
//		}
//		
//		SequentialProbabilityRatioTester tester = new SequentialProbabilityRatioTester(model, minNumberOfRuns, logger, root, halfWidthOfIndifferenceRegion, type1Error, type2Error, notEqual, nullHypothesisLowerEqual);
//		
//		if (!printRunResults)
//			System.out.println("Running simulation...");
//		for (int run = 0; run < fixedNumberOfRuns; run++){
//			
//			if (printRunResults) System.out.println("Starting simulation run no." + (run+1));
//			
//			currentTime = 0.0;
//			try {
//				model.resetMarking();
//				generator.sampleGeneralTransitions(model, logger);
//			} catch (InvalidDistributionParameterException e) {
//				throw new ModelNotReadableException(e.getLocalizedMessage());				
//			}
//		
//			currentPlot = new MarkingPlot(maxTime);
//			plots.add(currentPlot);
//			currentPlot.initialize(model);
//			
//			//simulation
//			while (currentTime <= maxTime)
//				currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);			
//			
//			tester.doRatioTest(run + 1, currentPlot);
//			
//			if (printRunResults){
//				System.out.println(maxTime + " seconds: simulation run no." + (run+1) + " completed");			
//				model.printCurrentMarking(false, true);	
//			}
//			
//			run++;		
//		}
//		
//		if (tester.getResultAchieved()){
//			if (tester.getPropertyFulfilled())
//				System.out.println(fixedNumberOfRuns + " runs simulated. The property is fulfilled");
//			else
//				System.out.println(fixedNumberOfRuns + " runs simulated. The property is NOT fulfilled");
//		} else
//			System.out.println(fixedNumberOfRuns + " runs simulated. Not enough runs to make a decision on the property result.");
//		
//		if (logger != null){
//			logger.info("Simulation finished after " + fixedNumberOfRuns + " runs");
//			if (tester.getResultAchieved()){
//				if (tester.getPropertyFulfilled())
//					logger.info("The property is fulfilled");
//				else
//					logger.info("The property is not fulfilled");
//			} else
//				logger.info("Not enough runs executed to make a decision on the property");
//		}
//		
//	}
//	
	
	public void loadParameters(){		
		

		try {	
			Properties parameters = new Properties();
			parameters.load(new FileInputStream("libhpng_parameters.cfg"));
			
			halfIntervalWidth = Double.parseDouble(parameters.getProperty("halfIntervalWidth"));
			halfWidthOfIndifferenceRegion = Double.parseDouble(parameters.getProperty("halfWidthOfIndifferenceRegion"));
			confidenceLevel = Double.parseDouble(parameters.getProperty("confidenceLevel"));
			type1Error = Double.parseDouble(parameters.getProperty("type1Error"));
			type2Error = Double.parseDouble(parameters.getProperty("type2Error"));
			fixedNumberOfRuns = Integer.parseInt(parameters.getProperty("fixedNumberOfRuns"));
			minNumberOfRuns = Integer.parseInt(parameters.getProperty("minNumberOfRuns"));
			maxNumberOfRuns = Integer.parseInt(parameters.getProperty("maxNumberOfRuns"));
			simulationWithFixedNumberOfRuns = Boolean.parseBoolean(parameters.getProperty("simulationWithFixedNumberOfRuns"));
			printRunResults = Boolean.parseBoolean(parameters.getProperty("printRunResults"));
			algorithmID = Byte.parseByte(parameters.getProperty("algorithmID"));
			guess = Double.parseDouble(parameters.getProperty("guess"));
			testRuns = Integer.parseInt(parameters.getProperty("testRuns"));
			intervalID = Byte.parseByte(parameters.getProperty("intervalID"));
					
			
		} catch (Exception e) {
			
			if (logger != null)
				logger.warning("Simulation parameters from configuration file could not be loaded. Default values are set.");
			System.out.println("Simulation parameters from configuration file could not be loaded. Default values are set.");
		
			halfIntervalWidth = 0.005;
			halfWidthOfIndifferenceRegion = 0.005;
			confidenceLevel = 0.95;
			type1Error = 0.05;
			type2Error = 0.05;
			fixedNumberOfRuns = 1000;
			minNumberOfRuns = 100;
			maxNumberOfRuns = 100000;
			simulationWithFixedNumberOfRuns = false;
			printRunResults = false;
			algorithmID = 0;
			guess = 0.1;
			testRuns = 1;
			intervalID = 0;
			
		}
	}
	
	
	public void storeParameters(){		
		
		try {
			
			Properties parameters = new Properties();
			
			parameters.setProperty("halfIntervalWidth", halfIntervalWidth.toString());
			parameters.setProperty("halfWidthOfIndifferenceRegion", halfWidthOfIndifferenceRegion.toString());
			parameters.setProperty("confidenceLevel", confidenceLevel.toString());
			parameters.setProperty("type1Error", type1Error.toString());
			parameters.setProperty("type2Error", type2Error.toString());
			parameters.setProperty("fixedNumberOfRuns", fixedNumberOfRuns.toString());
			parameters.setProperty("minNumberOfRuns", minNumberOfRuns.toString());
			parameters.setProperty("maxNumberOfRuns", maxNumberOfRuns.toString());
			parameters.setProperty("simulationWithFixedNumberOfRuns", simulationWithFixedNumberOfRuns.toString());
			parameters.setProperty("printRunResults", printRunResults.toString());
			parameters.setProperty("algorithmID", algorithmID.toString());
			parameters.setProperty("guess", guess.toString());
			parameters.setProperty("testRuns", testRuns.toString());
			parameters.setProperty("intervalID", intervalID.toString());
			
			parameters.store(new FileOutputStream("libhpng_parameters.cfg"),"");
						
		} catch (Exception e) {
			
			if (logger != null)
				logger.severe("Simulation parameters could not be saved into the configuration file.");
			System.out.println("Simulation parameters could not be saved into the configuration file.");
			
		}
	}

}
