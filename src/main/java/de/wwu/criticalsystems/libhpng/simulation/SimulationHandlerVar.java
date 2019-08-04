package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.confidenceintervals.ConfidenceIntervalCalculator;
import de.wwu.criticalsystems.libhpng.errorhandling.*;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.ContinuousPlacesPlotter;
import de.wwu.criticalsystems.libhpng.plotting.ContinuousPlacesPlotterVar;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

public class SimulationHandlerVar {


    public SimulationHandlerVar(String parameterFilePath) throws InvalidSimulationParameterException {

        loadParameters(parameterFilePath);
    }


    public Double getHalfIntervalWidth() {
        return halfIntervalWidth;
    }

    public void setHalfIntervalWidth(Double halfIntervalWidth) throws InvalidSimulationParameterException {

        if (halfIntervalWidth < 0.0 || halfIntervalWidth > 1.0) {
            if (logger != null)
                logger.severe("The half interval width parameter must be between 0.0 and 1.0");
            throw new InvalidSimulationParameterException("The half interval width parameter must be between 0.0 and 1.0");
        }

        this.halfIntervalWidth = halfIntervalWidth;
        if (logger != null) logger.info("The half interval width has been changed to: " + halfIntervalWidth);
    }


    public Double getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(Double confidenceLevel) throws InvalidSimulationParameterException {

        if (confidenceLevel < 0.0 || confidenceLevel > 1.0) {
            if (logger != null)
                logger.severe("The confidence level parameter must be between 0.0 and 1.0");
            throw new InvalidSimulationParameterException("The confidence level parameter must be between 0.0 and 1.0");
        }

        this.confidenceLevel = confidenceLevel;
        if (logger != null) logger.info("The confidence level has been changed to: " + confidenceLevel);
    }

    public Double getRealProbability() {
        return realProbability;
    }

    public void setRealProbability(Double realProbability) {
        this.realProbability = realProbability;
    }

    public Integer getCalculations() {
        return calculations;
    }

    public void setCalculations(Integer calculations) {
        this.calculations = calculations;
    }

    public Double getCorrectnessIndifferenceLevel() {
        return correctnessIndifferenceLevel;
    }

    public void setCorrectnessIndifferenceLevel(Double correctnessIndifferenceLevel) throws InvalidSimulationParameterException {

        if (correctnessIndifferenceLevel <= 0.0 || correctnessIndifferenceLevel > 1.0) {
            if (logger != null)
                logger.severe("The correctness indifference level must be between 0.0 and 1.0 but greater than 0.0");
            throw new InvalidSimulationParameterException("The  correctness indifference level must be between 0.0 and 1.0 but greater than 0.0");
        }
        this.correctnessIndifferenceLevel = correctnessIndifferenceLevel;
        if (logger != null)
            logger.info("The  correctness indifference level has been changed to: " + correctnessIndifferenceLevel);
    }

    public Double getPowerIndifferenceLevel() {
        return powerIndifferenceLevel;
    }

    public void setPowerIndifferenceLevel(Double powerIndifferenceLevel) throws InvalidSimulationParameterException {

        if (powerIndifferenceLevel <= 0.0 || powerIndifferenceLevel > 1.0) {
            if (logger != null)
                logger.severe("The power indifference level must be between 0.0 and 1.0 but greater than 0.0");
            throw new InvalidSimulationParameterException("The  power indifference level must be between 0.0 and 1.0 but greater than 0.0");
        }
        this.powerIndifferenceLevel = powerIndifferenceLevel;
        if (logger != null) logger.info("The  power indifference level has been changed to: " + powerIndifferenceLevel);
    }

    public Double getGuess() {
        return guess;
    }

    public void setGuess(Double guess) throws InvalidSimulationParameterException {
        this.guess = guess;

        if (guess < 0.0 || guess > 1.0) {
            if (logger != null)
                logger.severe("The value of the guess parameter must be between 0.0 and 1.0");
            throw new InvalidSimulationParameterException("The value of the guess parameter must be between 0.0 and 1.0");
        }
        if (logger != null) logger.info("The value of guess has been changed to: " + guess);

    }

    public Integer getTestRuns() {
        return testRuns;
    }

    public void setNumberOfTestRuns(Integer testRuns) throws InvalidSimulationParameterException {
        this.testRuns = testRuns;

        if (testRuns <= 0) {
            if (logger != null)
                logger.severe("The number of runs for hypothesis testing must be at least 1");
            throw new InvalidSimulationParameterException("The number of runs for hypothesis testing must be at least 1");
        }
        if (logger != null) logger.info("The value of guess has been changed to: " + guess);
    }


    public Double getType1Error() {
        return type1Error;
    }

    public void setType1Error(Double type1Error) throws InvalidSimulationParameterException {

        if (type1Error < 0.0 || type1Error > 1.0) {
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

        if (type2Error < 0.0 || type2Error > 1.0) {
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

        if (fixedNumberOfRuns < minNumberOfRuns || fixedNumberOfRuns > maxNumberOfRuns) {
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

        if (minNumberOfRuns < 1 || minNumberOfRuns > maxNumberOfRuns) {
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

        if (maxNumberOfRuns < minNumberOfRuns || maxNumberOfRuns > 1000000) {
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
        if (logger != null) {
            if (simulationWithFixedNumberOfRuns)
                logger.info("The simulation has been set to simulation with fixed number of runs");
            else
                logger.info("The simulation has been set to simulation with optimal number of runs");
        }
    }

    public Boolean getPrintRunResults() {
        return printRunResults;
    }

    public void setPrintRunResults(Boolean printRunResults) {
        this.printRunResults = printRunResults;
        if (logger != null) {
            if (printRunResults)
                logger.info("Printing of results has been turned on.");
            else
                logger.info("Printing of results has been turned off.");
        }
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public static enum ProbabilityOperator {confidenceinterval, hypothesisgreater, hypothesisgreaterequal, hypothesislower, hypothesislowerequal}


    private SimulatorVar simulator;
    private Logger logger;
    private HPnGModelVar model;
    private SimpleNode root;
    private Double maxTime;
    private Double currentTime;
    private Double propertyTime;
    private ArrayList<MarkingPlotVar> plots = new ArrayList<MarkingPlotVar>();
    private MarkingPlotVar currentPlot;
    private Double boundary;

    //simulation parameters
    private Byte intervalID;
    private Double halfIntervalWidth = 0.0;
    private Double confidenceLevel;
    private Double realProbability;
    private Integer calculations;
    private Byte algorithmID;
    private Double correctnessIndifferenceLevel;
    private Double powerIndifferenceLevel;
    private Double guess;
    private Integer testRuns;
    private Double type1Error;
    private Double type2Error;
    private Integer fixedNumberOfRuns;
    private Integer minNumberOfRuns;
    private Integer maxNumberOfRuns;
    private Boolean simulationWithFixedNumberOfRuns;
    private Boolean printRunResults;
    private int numericSolverID;
    private double fixedStepSize;
    private double minStep;
    private double maxStep;
    private double scalAbsoluteTolerance;
    private double scalRelativeTolerance;

    public MarkingPlotVar getCurrentPlot() {
        return currentPlot;
    }

    public void simulateAndPlotOnly(Double maxTime, HPnGModelVar model) throws ModelNotReadableException, InvalidRandomVariateGeneratorException, InvalidPropertyException {
        Integer firings = 0;
        Integer minFirings = Integer.MAX_VALUE;
        Integer maxFirings = Integer.MIN_VALUE;
        Integer thisrunsfirings;

        this.maxTime = maxTime;
        this.model = model;

        simulator = new SimulatorVar(model, maxTime, this);
        simulator.setLogger(logger);
        SampleGeneratorVar generator = new SampleGeneratorVar();
        generator.initializeRandomStream();
        if (logger != null)
            logger.info("Simulation started with plotting continuous places only");
        if (!printRunResults)
            System.out.println("Running simulation...");

        for (int run = 0; run < fixedNumberOfRuns; run++) {
            System.out.println("Starting simulation run no." + (run + 1));
            if (printRunResults)
                System.out.println("Starting simulation run no." + (run + 1));

            currentTime = 0.0;
            try {
                model.resetMarking();
                if (printRunResults)
                    model.printCurrentMarking(true, false);
                generator.sampleGeneralTransitions(model, logger);
            } catch (InvalidDistributionParameterException e) {
                throw new ModelNotReadableException(e.getLocalizedMessage());
            }

            currentPlot = new MarkingPlotVar(maxTime);
            plots.add(currentPlot);
            currentPlot.initializeContinuousPlacesOnly(this.model);

            //simulation
            while (currentTime <= maxTime)
                currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);
//			model.updateFluidRates(maxTime);
//			ODESystem ode = new ODESystem(model);
//
//			double[] asd = {1,1};
//			double[] asf= {0,0};
//			ode.computeDerivatives(0,asd,asf);
//			double[] initial = ode.getCurrentFluidLevels();
//			double[] ergebnis = new double[ode.getDimension()];
//			double endtime =maxTime;
//			FirstOrderIntegrator rungeKutta = new ClassicalRungeKuttaIntegrator(1.0e-1);
////			rungeKutta.addEventHandler(new ODEEventHandler(0,0),1.0e-2,0.01,10000);
//			ContinuousOutputModel outputModel = new ContinuousOutputModel();
//			ODEStepHandler stepHandler = new ODEStepHandler(ode,this);
//			rungeKutta.addStepHandler(stepHandler);
//			rungeKutta.addStepHandler(outputModel);
//			rungeKutta.integrate(ode,0,initial,endtime,ergebnis);
//			System.out.println(ergebnis[0]+" 2. teil "+ ergebnis[1]+" Eval "+rungeKutta.getEvaluations());
//			for(Place place:ode.getModel().getPlaces()){
//				if(place.getClass().equals(ContinuousPlaceVar.class))
//					System.out.println(((ContinuousPlaceVar)place).getCurrentFluidLevel());
//			}


            thisrunsfirings = 0;
            for (Transition t : model.getTransitions()) {
                if (t.getClass().equals(GeneralTransition.class))
                    thisrunsfirings += ((GeneralTransition) t).getFirings();
            }
            firings += thisrunsfirings;
            if (thisrunsfirings < minFirings)
                minFirings = thisrunsfirings;
            if (thisrunsfirings > maxFirings)
                maxFirings = thisrunsfirings;

            if (printRunResults) {
                System.out.println(maxTime + " seconds: simulation run no." + (run + 1) + " completed");
                model.printCurrentMarking(false, true);
            }

        }


        System.out.println("Mean number of random variables: " + (firings.doubleValue() / fixedNumberOfRuns.doubleValue()) + " (mininmum: " + minFirings + ", maximum: " + maxFirings + ")");


        ContinuousPlacesPlotterVar plotter = new ContinuousPlacesPlotterVar();
        plotter.plotContinuousPlaces(model, plots, maxTime, confidenceLevel);
        if (logger != null)
            logger.info("Simulation finished after " + fixedNumberOfRuns + " runs");

        plots.clear();

    }



    public void simulateAndCheckProperty(HPnGModelVar model, SimpleNode root, Double time, ProbabilityOperator operator, Double boundary, File results) throws InvalidPropertyException, ModelNotReadableException, InvalidRandomVariateGeneratorException, FileNotFoundException, IOException {

        final long timeStart = System.currentTimeMillis();

        this.model = model;
        this.root = root;
        this.maxTime = PropertyChecker.getMaxTimeForSimulation(root, time);
        this.propertyTime = time;
        this.boundary = boundary;


//        ArrayList<FluidPropertyVar> fluidProperyList = new ArrayList<FluidPropertyVar>();
//        fluidProperyList = PropertyCheckerVar.getFluidProperties(root, fluidProperyList, logger, model, this.propertyTime, this.propertyTime, false);
//
//
//        simulator = new DynamicSimulatorVar(model, maxTime, this, fluidProperyList);
        simulator = new SimulatorVar(model,maxTime,this);
        simulator.setLogger(logger);


        Properties parameters = new Properties();


        try {
            switch (operator) {
                case confidenceinterval:
                    //property check with confidence interval calculation
                    parameters = simulateAndCheckPROBQ(calculations, realProbability, simulationWithFixedNumberOfRuns);
                    break;
                case hypothesisgreaterequal:
                    //property check with hypothesis testing
                    parameters = simulateAndTestPROB(true, true, simulationWithFixedNumberOfRuns);
                    break;
                case hypothesislower:
                    //property check with hypothesis testing
                    parameters = simulateAndTestPROB(true, false, simulationWithFixedNumberOfRuns);
                    break;
                case hypothesislowerequal:
                    //property check with hypothesis testing
                    parameters = simulateAndTestPROB(false, true, simulationWithFixedNumberOfRuns);
                    break;
                case hypothesisgreater:
                    //property check with hypothesis testing
                    parameters = simulateAndTestPROB(false, false, simulationWithFixedNumberOfRuns);
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
        Long timeNeeded = (timeEnd - timeStart);
        System.out.printf("Time needed: " + "%,.2f" + "  ms.%n", timeNeeded.doubleValue());

        parameters.setProperty("Time", timeNeeded.toString());
        parameters.store(new FileOutputStream(results), "");

    }


    public void loadParameters(String parameterFilePath) {


        try {
            Properties parameters = new Properties();
            parameters.load(new FileInputStream(parameterFilePath));


            intervalID = Byte.parseByte(parameters.getProperty("intervalID"));
            halfIntervalWidth = Double.parseDouble(parameters.getProperty("halfIntervalWidth"));
            confidenceLevel = Double.parseDouble(parameters.getProperty("confidenceLevel"));
            realProbability = Double.parseDouble(parameters.getProperty("realProbability"));
            calculations = Integer.parseInt(parameters.getProperty("calculations"));

            algorithmID = Byte.parseByte(parameters.getProperty("algorithmID"));
            correctnessIndifferenceLevel = Double.parseDouble(parameters.getProperty("correctnessIndifferenceLevel"));
            powerIndifferenceLevel = Double.parseDouble(parameters.getProperty("powerIndifferenceLevel"));
            guess = Double.parseDouble(parameters.getProperty("guess"));
            testRuns = Integer.parseInt(parameters.getProperty("testRuns"));
            type1Error = Double.parseDouble(parameters.getProperty("type1Error"));
            type2Error = Double.parseDouble(parameters.getProperty("type2Error"));

            fixedNumberOfRuns = Integer.parseInt(parameters.getProperty("fixedNumberOfRuns"));
            minNumberOfRuns = Integer.parseInt(parameters.getProperty("minNumberOfRuns"));
            maxNumberOfRuns = Integer.parseInt(parameters.getProperty("maxNumberOfRuns"));
            simulationWithFixedNumberOfRuns = Boolean.parseBoolean(parameters.getProperty("simulationWithFixedNumberOfRuns"));
            printRunResults = Boolean.parseBoolean(parameters.getProperty("printRunResults"));

            numericSolverID = Integer.parseInt(parameters.getProperty("numericSolverID"));
            fixedStepSize = Double.parseDouble(parameters.getProperty("fixedStepSize"));
            minStep = Double.parseDouble(parameters.getProperty("minStep"));
            maxStep = Double.parseDouble(parameters.getProperty("maxStep"));
            scalRelativeTolerance = Double.parseDouble(parameters.getProperty("scalRelativeTolerance"));
            scalAbsoluteTolerance = Double.parseDouble(parameters.getProperty("scalAbsoluteTolerance"));


        } catch (Exception e) {

            if (logger != null)
                logger.warning("Simulation parameters from configuration file could not be loaded. Default values are set.");
            System.out.println("Simulation parameters from configuration file could not be loaded. Default values are set.");

            intervalID = 0;
            halfIntervalWidth = 0.005;
            confidenceLevel = 0.95;
            realProbability = 0.5;
            calculations = 1;

            algorithmID = 0;
            correctnessIndifferenceLevel = 0.005;
            powerIndifferenceLevel = 0.005;
            guess = 0.1;
            testRuns = 1;
            type1Error = 0.05;
            type2Error = 0.05;

            fixedNumberOfRuns = 1000;
            minNumberOfRuns = 100;
            maxNumberOfRuns = 100000;
            simulationWithFixedNumberOfRuns = false;
            printRunResults = false;

            fixedStepSize=1.0e-1;
            minStep=1.0e-8;
            maxStep=100.0;
            scalAbsoluteTolerance=1.0e-10;
            scalRelativeTolerance=1.0e-10;
        }
    }


    public void storeParameters() {

        try {

            Properties parameters = new Properties();

            parameters.setProperty("intervalID", intervalID.toString());
            parameters.setProperty("halfIntervalWidth", halfIntervalWidth.toString());
            parameters.setProperty("confidenceLevel", confidenceLevel.toString());
            parameters.setProperty("realProbability", realProbability.toString());
            parameters.setProperty("calculations", calculations.toString());

            parameters.setProperty("algorithmID", algorithmID.toString());
            parameters.setProperty("correctnessIndifferenceLevel", correctnessIndifferenceLevel.toString());
            parameters.setProperty("powerIndifferenceLevel", powerIndifferenceLevel.toString());
            parameters.setProperty("guess", guess.toString());
            parameters.setProperty("testRuns", testRuns.toString());
            parameters.setProperty("type1Error", type1Error.toString());
            parameters.setProperty("type2Error", type2Error.toString());

            parameters.setProperty("fixedNumberOfRuns", fixedNumberOfRuns.toString());
            parameters.setProperty("minNumberOfRuns", minNumberOfRuns.toString());
            parameters.setProperty("maxNumberOfRuns", maxNumberOfRuns.toString());
            parameters.setProperty("simulationWithFixedNumberOfRuns", simulationWithFixedNumberOfRuns.toString());
            parameters.setProperty("printRunResults", printRunResults.toString());

            parameters.setProperty("numericSolverID",Integer.toString(numericSolverID));
            parameters.setProperty("fixedStepSize",Double.toString(fixedStepSize));
            parameters.setProperty("minStep",Double.toString(minStep));
            parameters.setProperty("maxStep",Double.toString(maxStep));
            parameters.setProperty("scalRelativeTolerance",Double.toString(scalRelativeTolerance));
            parameters.setProperty("scalAbsoluteTolerance",Double.toString(scalAbsoluteTolerance));

            parameters.store(new FileOutputStream("libhpng_parameters.cfg"), "");

        } catch (Exception e) {

            if (logger != null)
                logger.severe("Simulation parameters could not be saved into the configuration file.");
            System.out.println("Simulation parameters could not be saved into the configuration file.");

        }
    }


    public String getIntervalName() {

        switch (intervalID) {
            case 0:
                return "Standard";
            case 1:
                return "Wald";
            case 2:
                return "Clopper Pearson";
            case 3:
                return "Score";
            case 4:
                return "adjusted Wald";

        }
        return "";
    }


    public void setIntervalID(String intervalName) throws InvalidSimulationParameterException {

        switch (intervalName) {
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

        if (logger != null)
            logger.info("The approach for the calculation of confidence intervals has been changed to : " + intervalName);
    }


    public String getAlgorithmName() {

        switch (algorithmID) {
            case 0:
                return "Sequential Probability Ratio Test";
            case 1:
                return "Gauss Confidence Interval Test";
            case 2:
                return "Chow Robbins Test";
            case 3:
                return "Azuma Test";
            case 4:
                return "Chernoff Confidence Interval Test";
            case 5:
                return "Gauss SSP Test";
            case 6:
                return "Darling Test";
        }
        return "";
    }

    public void setAlgorithmID(String algorithmName) throws InvalidSimulationParameterException {

        switch (algorithmName) {
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
            case "CCI":
                this.algorithmID = 4;
                break;
            case "GSSP":
                this.algorithmID = 5;
                break;
            case "Darling":
                this.algorithmID = 6;
                break;

            default:
                if (logger != null)
                    logger.severe("Invalid algorithm, type help to see valid abbreviation");
                throw new InvalidSimulationParameterException("Invalid algorithm");
        }

        if (logger != null)
            logger.info("The algorithm used for hypothesis testing has been changed to : " + algorithmName);
    }


    public int getNumericSolverID() {
        return numericSolverID;
    }

    public double getFixedStepSize() {
        return fixedStepSize;
    }

    public double getMinStep() {
        return minStep;
    }

    public double getMaxStep() {
        return maxStep;
    }

    public double getScalAbsoluteTolerance() {
        return scalAbsoluteTolerance;
    }

    public double getScalRelativeTolerance() {
        return scalRelativeTolerance;
    }

    private Properties simulateAndCheckPROBQ(Integer intervalCalcs, Double realProbability, Boolean fixedNumber) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException {


//        Integer coverage = 0;
//        Integer noCoverage = 0;
//        Integer maxRun = 0;
//        Integer minRun = 0;
//        Integer notEnoughRuns = 0;
//        Integer totalRuns = 0;
//        Integer run = 0;
//        Integer firings = 0;
//        Integer minFirings = Integer.MAX_VALUE;
//        Integer maxFirings = Integer.MIN_VALUE;
//        Integer thisrunsfirings;
//
//        Double lower;
//        Double upper;
//        Double midpoint;
//        Double halfwidth;
//
//        Double coveragePercentage;
//        Double noCoveragePercentage;
//        Double notEnoughRunsPercentage;
//        Integer averageRuns;
//
//
//        SampleGenerator generator;
//        ConfidenceIntervalCalculator calc = new ConfidenceIntervalCalculator(intervalID, model, this.propertyTime, minNumberOfRuns, logger, root, confidenceLevel, halfIntervalWidth);
//        ArrayList<String> related = calc.getChecker().getAllRelatedPlaceAndTransitionIds();
//
//        Integer n = 0;
//        while (n < calculations) {
//
//            generator = new SampleGenerator();
//            generator.initializeRandomStream();
//            if (logger != null)
//                logger.info("Simulation started for a 'P=?' property with fixed interval width");
//
//
//            if (fixedNumber) {
//
//                for (run = 0; run < fixedNumberOfRuns; run++) {
//
//                    if (printRunResults || ((run + 1) % 100 == 0))
//                        System.out.println("Starting simulation run no." + (run + 1));
//
//                    currentTime = 0.0;
//                    try {
//                        model.resetMarking();
//                        generator.sampleGeneralTransitions(model, logger);
//                    } catch (InvalidDistributionParameterException e) {
//                        throw new ModelNotReadableException(e.getLocalizedMessage());
//                    }
//
//                    currentPlot = new MarkingPlotVar(maxTime);
//                    currentPlot.initializeRelatedOnly(this.model, related);
//
//
//                    //simulation
//                    while (currentTime <= maxTime)
//                        currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);
//
//                    thisrunsfirings = 0;
//                    for (Transition t : model.getTransitions()) {
//                        if (t.getClass().equals(GeneralTransition.class))
//                            thisrunsfirings += ((GeneralTransition) t).getFirings();
//                    }
//                    firings += thisrunsfirings;
//                    if (thisrunsfirings < minFirings)
//                        minFirings = thisrunsfirings;
//                    if (thisrunsfirings > maxFirings)
//                        maxFirings = thisrunsfirings;
//
//                    calc.calculateConfidenceInterval(run + 1, currentPlot);
//
//                    if (printRunResults) {
//                        System.out.println(maxTime + " seconds: simulation run no." + (run + 1) + " completed");
//                        model.printCurrentMarking(false, true);
//                    }
//
//                }
//
//
//                midpoint = calc.getMidpoint();
//                lower = calc.getLowerBorder();
//                upper = calc.getUpperBorder();
//                halfwidth = (upper - lower) / 2.0;
//
//
//                System.out.println(run + " runs simulated. Midpoint: " + midpoint + ".");
//                System.out.println("Resulting confidence interval borders:" + lower + " & " + upper + " (one sided interval width = " + halfwidth + ")");
//
//
//                if (lower <= realProbability && realProbability <= upper) {
//                    System.out.println("The real probability lies within the interval.");
//                    coverage++;
//                    maxRun = calcMaxRun(run, maxRun);
//                    minRun = calcMinRun(run, minRun);
//                    totalRuns += run;
//
//                } else {
//                    System.out.println("The real probability does not lie within the interval.");
//                    noCoverage++;
//                    maxRun = calcMaxRun(run, maxRun);
//                    minRun = calcMinRun(run, minRun);
//                    totalRuns += run;
//
//                }
//
//                if (logger != null) {
//                    logger.info("Simulation finished after " + fixedNumberOfRuns + " runs");
//                    logger.info("Simulation results: Midpoint: " + midpoint + ". Resulting confidence interval borders:" + lower + " & " + upper + " (one sided interval width = " + halfwidth + ")");
//                }
//
//
//            } else {
//
//                run = 0;
//                while (!calc.checkBound() && run < maxNumberOfRuns) {
//
//                    if (printRunResults || ((run + 1) % 100 == 0))
//                        System.out.println("Starting simulation run no." + (run + 1));
//
//                    currentTime = 0.0;
//                    try {
//                        model.resetMarking();
//                        generator.sampleGeneralTransitions(model, logger);
//                    } catch (InvalidDistributionParameterException e) {
//                        throw new ModelNotReadableException(e.getLocalizedMessage());
//                    }
//
//                    currentPlot = new MarkingPlotVar(maxTime);
//                    currentPlot.initializeRelatedOnly(this.model, related);
//
//                    //simulation
//                    while (currentTime <= maxTime)
//                        currentTime = simulator.getAndCompleteNextEvent(currentTime, currentPlot, printRunResults);
//
//
//                    thisrunsfirings = 0;
//                    for (Transition t : model.getTransitions()) {
//                        if (t.getClass().equals(GeneralTransition.class))
//                            thisrunsfirings += ((GeneralTransition) t).getFirings();
//                    }
//                    firings += thisrunsfirings;
//                    if (thisrunsfirings < minFirings)
//                        minFirings = thisrunsfirings;
//                    if (thisrunsfirings > maxFirings)
//                        maxFirings = thisrunsfirings;
//
//
//                    calc.calculateConfidenceInterval(run + 1, currentPlot);
//
//                    if (printRunResults) {
//                        System.out.println(maxTime + " seconds: simulation run no." + (run + 1) + " completed");
//                        model.printCurrentMarking(false, true);
//                    }
//
//                    run++;
//                }
//
//                midpoint = calc.getMidpoint();
//                lower = calc.getLowerBorder();
//                upper = calc.getUpperBorder();
//                halfwidth = (upper - lower) / 2.0;
//
//
//                if (logger != null)
//                    logger.info("Simulation finished after " + run + " runs");
//
//                if (!calc.checkBound()) {
//
//                    System.out.println("The maximum number of " + maxNumberOfRuns + " runs has been achieved.");
//                    notEnoughRuns++;
//
//                    if (logger != null)
//                        logger.info("The maximum number of " + maxNumberOfRuns + " runs has been achieved.");
//
//
//                } else {
//
//                    System.out.println(run + " runs needed. Midpoint: " + midpoint + ".");
//                    System.out.println("Resulting confidence interval borders:" + lower + " & " + upper + " (one sided interval width = " + halfwidth + ")");
//
//
//                    if (lower <= realProbability && realProbability <= upper) {
//                        System.out.println("The real probability lies within the interval.");
//                        coverage++;
//                        maxRun = calcMaxRun(run, maxRun);
//                        minRun = calcMinRun(run, minRun);
//                        totalRuns += run;
//
//                    } else {
//                        System.out.println("The real probability does not lie within the interval.");
//                        noCoverage++;
//                        maxRun = calcMaxRun(run, maxRun);
//                        minRun = calcMinRun(run, minRun);
//                        totalRuns += run;
//
//                    }
//
//                    if (logger != null)
//                        logger.info("Simulation results: Midpoint: " + midpoint + ". Resulting confidence interval borders:" + lower + " & " + upper + " (one sided interval width = " + halfwidth + ")");
//
//                }
//            }
//
//            System.out.println("Mean number of random variables: " + (firings.doubleValue() / run.doubleValue()) + " (mininmum: " + minFirings + ", maximum: " + maxFirings + ")");
//
//            calc.resetResults();
//            n++;

//        }
//
//
//        coveragePercentage = calcPercentage(coverage);
//        noCoveragePercentage = calcPercentage(noCoverage);
//        notEnoughRunsPercentage = calcPercentage(notEnoughRuns);
//        averageRuns = totalRuns / (coverage + noCoverage + notEnoughRuns);
//
//        System.out.println("\n" + "The real probability lies within the interval in: " + calcPercentage(coverage) + "% (" + coverage + ") of " + calculations + " calculations");
//        System.out.println("The real probability does not lie within the interval in: " + calcPercentage(noCoverage) + "% (" + noCoverage + ") of " + calculations + " calculations");
//        System.out.println("The maximum number of " + maxNumberOfRuns + " runs has been achieved in: " + calcPercentage(notEnoughRuns) + "% (" + notEnoughRuns + ") of " + calculations + " calculations" + "\n");
//        System.out.println("Minimal number of simulation runs needed: " + minRun);
//        System.out.println("Maximal number of simulation runs needed: " + maxRun);
//        if (coverage > 0 || noCoverage > 0) {
//            System.out.println("Average number of simulation runs needed: " + totalRuns / (coverage + noCoverage) + "\n");
//        } else {
//            System.out.println("There where no successfull runs" + "\n");
//        }
//
//
//        Properties parameters = new Properties();
//
//
//        parameters.setProperty("Coverage", coverage.toString());
//        parameters.setProperty("noCoverage", noCoverage.toString());
//        parameters.setProperty("notEnoughRuns", notEnoughRuns.toString());
//        parameters.setProperty("calculations", calculations.toString());
//        parameters.setProperty("minRun", minRun.toString());
//        parameters.setProperty("maxRun", maxRun.toString());
//        parameters.setProperty("averageRuns", averageRuns.toString());
//        parameters.setProperty("CoveragePercentage", coveragePercentage.toString());
//        parameters.setProperty("noCoveragePercentage", noCoveragePercentage.toString());
//        parameters.setProperty("notEnoughRunsPercentage", notEnoughRunsPercentage.toString());

//        return parameters;
        return null;
    }


    private Properties simulateAndTestPROB(Boolean checkLowerThan, Boolean invertPropertyAndThreshold, Boolean fixedNumber) throws ModelNotReadableException, InvalidPropertyException, InvalidRandomVariateGeneratorException {

		SimulationForHypothesisTestingVar testing;
		
		if (fixedNumber)
			testing = new SimulationForHypothesisTestingVar(model, minNumberOfRuns, logger, root, correctnessIndifferenceLevel, powerIndifferenceLevel, guess, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold, printRunResults, 0, currentTime, currentPlot, maxTime, simulator, true, fixedNumberOfRuns, testRuns);
		else		
			testing = new SimulationForHypothesisTestingVar(model, minNumberOfRuns, logger, root, correctnessIndifferenceLevel, powerIndifferenceLevel, guess, type1Error, type2Error, checkLowerThan, invertPropertyAndThreshold, printRunResults, maxNumberOfRuns, currentTime, currentPlot, maxTime, simulator, false, 0, testRuns);
		
		return testing.performTesting(algorithmID, getAlgorithmName(), this.propertyTime, this.boundary);
    }


    private Integer calcMinRun(Integer currRun, Integer minRun) {

        if (minRun == 0)
            minRun = currRun;

        if (minRun > currRun)
            minRun = currRun;

        return minRun;
    }

    private Integer calcMaxRun(Integer currRun, Integer maxRun) {

        if (maxRun < currRun) {
            maxRun = currRun;
        }

        return maxRun;
    }

    private double calcPercentage(Integer calc) {
        double curr = ((double) calc) / calculations;
        curr = curr * 100;
        return curr;
    }
}
