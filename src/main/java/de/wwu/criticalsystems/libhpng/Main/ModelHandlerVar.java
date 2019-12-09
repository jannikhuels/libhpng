package de.wwu.criticalsystems.libhpng.Main;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidSimulationParameterException;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelNotReadableException;
import de.wwu.criticalsystems.libhpng.formulaparsing.ParseException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SMCParser;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.init.ModelReaderVar;
import de.wwu.criticalsystems.libhpng.model.ContinuousPlaceVar;
import de.wwu.criticalsystems.libhpng.model.HPnGModelVar;
import de.wwu.criticalsystems.libhpng.model.ODESystem;
import de.wwu.criticalsystems.libhpng.model.Place;
import de.wwu.criticalsystems.libhpng.simulation.ODEEventHandler;
import de.wwu.criticalsystems.libhpng.simulation.ODEStepHandler;
import de.wwu.criticalsystems.libhpng.simulation.SimulationHandler;
import de.wwu.criticalsystems.libhpng.simulation.SimulationHandlerVar.ProbabilityOperator;
import de.wwu.criticalsystems.libhpng.simulation.SimulationHandlerVar;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ModelHandlerVar {


	public ModelHandlerVar() {
		createLogger();
		try {
			simulationHandler = new SimulationHandlerVar("libhpng_parameters.cfg");
		} catch (InvalidSimulationParameterException e) {
			System.out.println("An error occured while simulating due to an incorrect simulation parameter. Please see the error log and recheck the parameter file.");
		}
		simulationHandler.setLogger(logger);
	}
	

	public HPnGModelVar getModel() {
		return model;
	}	

	public void setLoggerPath(String loggerPath) {
		this.loggerPath = loggerPath;
		createLogger();
		simulationHandler.setLogger(logger);	    
	}
	
	
	private HPnGModelVar model;
	private Logger logger;	
	private String loggerPath = "logFile.log";
	private SMCParser parser;
	private SimulationHandlerVar simulationHandler;
	
	
	public SimpleNode readFormula(){
		
		System.out.println("Please enter the model checking formula to check:");			
		
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
		
			String formula = buffer.readLine();
			formula += "\n";
			InputStream stream = new ByteArrayInputStream(formula.getBytes(StandardCharsets.UTF_8));
			
			 if (parser == null) 
				 parser = new SMCParser(stream);
			 else 
				 SMCParser.ReInit(stream);
			 
			SimpleNode root = SMCParser.Input();
		
			if (logger != null) 
				logger.info("The following formula has been parsed successfully: " + formula);
			
			return root;
			
		} catch (ParseException e) {
			
			System.out.println("The formula could not be parsed. Please see the error log.");
			if (logger != null) 
				logger.severe(e.getLocalizedMessage());	
			
		} catch (IOException e) {
			
			System.out.println("The input could not be read in. Please see the error log.");
			if (logger != null) 
				logger.severe(e.getLocalizedMessage());	
		}		
		return null;		
	}
	
	
	public SimpleNode readFormula(String formula) throws InvalidPropertyException {
		
		String formulaEOF = formula + "\n";
		try {

			InputStream stream = new ByteArrayInputStream(formulaEOF.getBytes(StandardCharsets.UTF_8));
			
			 if (parser == null) 
				 parser = new SMCParser(stream);
			 else 
				 SMCParser.ReInit(stream);
			 
			SimpleNode root = SMCParser.Input();
		
			if (logger != null) 
				logger.info("The following formula has been parsed successfully: " + formula);
			
			return root;
			
		} catch (ParseException e) {
			
			//System.out.println("The formula could not be parsed. Please see the error log.");
			if (logger != null) 
				logger.severe("The formula could not be parsed: " + e.getLocalizedMessage());	
			
			throw new InvalidPropertyException ("The formula could not be parsed: " + e.getLocalizedMessage());
			
		}		
	}
	
	
	public void readModel(String xmlPath){
		
		try {    			
			ModelReaderVar reader = new ModelReaderVar();
			reader.setLogger(logger);
			model = reader.readModel(xmlPath);
			if (logger != null) 
				logger.info("Model '" + xmlPath + "' has been read successfully.");	
			System.out.println("Model '" + xmlPath + "' has been read successfully.");
			
			
			int[][] numbers = model.getArrayOfNumberOfComponents();
			
			System.out.println("Discrete Places: " + numbers[0][0] + ", Continuous Places: " + numbers[0][1]);
			System.out.println("General Transitions: " + numbers[1][0] + ", Immediate Transitions: " + numbers[1][1]+ ", Deterministic Transitions: " + numbers[1][2]+ ", Static Continuous Transitions: " + numbers[1][3]+ ", Dynamic Continuous Transitions: " + numbers[1][4]);
			System.out.println("Discrete Arcs: " + numbers[2][0] + ", Continuous Arcs: " + numbers[2][1] + ", Guards Arcs: " + numbers[2][2]);
			
			
		
		} catch (ModelNotReadableException e) {		
			if (logger != null) 
				logger.severe("The model could not be read in.");			
		}			
	}
	
	
	public void checkFormula(SimpleNode root) {
	
    	try {

    		Double time = getTimeFromRoot(root);
    		SimpleNode child = null;

    		ProbabilityOperator operator = getPropertyKind(root);

    		Double boundary = 0.0;
    		if (!operator.equals(ProbabilityOperator.confidenceinterval)) {
    			boundary = getProbBound(root);
    			 child =  (SimpleNode)root.jjtGetChild(1).jjtGetChild(1);
    		} else
    			child =  (SimpleNode)root.jjtGetChild(1).jjtGetChild(0);

    		File results = new File("results.cfg");

			simulationHandler.simulateAndCheckProperty(model, child, time, operator, boundary, results);

		} catch (InvalidPropertyException e) {
			System.out.println("The formula to check is invalid. Please see the error log.");
		} catch (ModelNotReadableException e) {
			System.out.println("An error occured while simulating due to an incorrect model file. Please see the error log and recheck the model.");
		} catch (InvalidRandomVariateGeneratorException e) {
			System.out.println("An internal error occured while simulating. Please see the error log.");
		} catch (FileNotFoundException e) {
			System.out.println("An internal error occured while writing to output file. Please see the error log.");
		} catch (IOException e) {
			System.out.println("An internal error occured while writing to output file. Please see the error log.");
		}
	}	
	
	
	public void checkFormula(String formula, Double time, Double boundary, ProbabilityOperator operator){
		
//    	try {
//    		SimpleNode root = readFormula(formula);
//    		File results = new File("results.cfg");
//			simulationHandler.simulateAndCheckProperty(model, root , time, operator, boundary, results);
//		} catch (InvalidPropertyException e) {
//			System.out.println("The formula to check is invalid. Please see the error log.");
//		} catch (ModelNotReadableException e) {
//			System.out.println("An error occured while simulating due to an incorrect model file. Please see the error log and recheck the model.");
//		} catch (InvalidRandomVariateGeneratorException e) {
//			System.out.println("An internal error occured while simulating. Please see the error log.");
//		} catch (FileNotFoundException e) {
//			System.out.println("An internal error occured while writing to output file. Please see the error log.");
//		} catch (IOException e) {
//			System.out.println("An internal error occured while writing to output file. Please see the error log.");
//		}
	}	
	
	
	public void plotPlaces(Double maxTime, String imagePath){

//		System.out.println(Arra);


		try {
			simulationHandler.simulateAndPlotOnly(maxTime, model, imagePath);

		} catch (ModelNotReadableException e) {
			if (logger != null)
				logger.severe("The model could not be read in.");
			System.out.println("An error occured while reading the model file. Please see the error log and recheck the model.");
		} catch (InvalidRandomVariateGeneratorException e) {
			System.out.println("An internal error occured while simulating. Please see the error log.");
		} catch (InvalidPropertyException e) {
			System.out.println("An internal error occured while simulating. Please see the error log.");
		}
	}
	
      
    public void changeParameter(Byte parameter, Object value){
    	
    	try {
    		
    		//change selected parameter
	    	switch (parameter){
	    		case 0:				
					simulationHandler.setHalfIntervalWidth((Double)value);
					break;
	    		case 1:
	    			simulationHandler.setCorrectnessIndifferenceLevel((Double)value);
	    			break;
	    		case 2:
	    			simulationHandler.setConfidenceLevel((Double)value);
	    			break;
	    		case 3:
	    			simulationHandler.setType1Error((Double)value);
	    			break;
	    		case 4:
	    			simulationHandler.setType2Error((Double)value);
	    			break;
	    		case 5:
	    			simulationHandler.setFixedNumberOfRuns((Integer)value);
	    			break;
	    		case 6:
	    			simulationHandler.setMinNumberOfRuns((Integer)value);
	    			break;
	    		case 7:
	    			simulationHandler.setMaxNumberOfRuns((Integer)value);
	    			break;
	    		case 8:
	    			simulationHandler.setSimulationWithFixedNumberOfRuns((Boolean)value);
	    			break;
	    		case 9:
	    			simulationHandler.setPrintRunResults((Boolean)value);
	    			break;			
	    		case 10:
	    			simulationHandler.setAlgorithmID((String)value);
	    			break;
	    		case 11:
	    			simulationHandler.setGuess((Double)value);
	    			break;
	    		case 12:
	    			simulationHandler.setNumberOfTestRuns((Integer)value);
	    			break;
	    		case 13:
	    			simulationHandler.setIntervalID((String)value);
	    			break;
	    		case 14:
	    			simulationHandler.setPowerIndifferenceLevel((Double)value);
	    			break;
	    		case 15:
	    			simulationHandler.setRealProbability((Double)value);
	    			break;
	    		case 16:
	    			simulationHandler.setCalculations((Integer)value);
	    			break;
	    	}
	    	
	    } catch (InvalidSimulationParameterException e) {
	    	if (logger != null) 
				logger.severe("The parameter chould not be changed.");
			System.out.println("An Error occured while changing the parameter. Please see the error log.");
		
	    }
    }
    
   
    public void printParameters(){    	

    	System.out.println("- Used confidence interval calculation approach: " + simulationHandler.getIntervalName() + " Confidence Interval");    	
    	System.out.println("- Half interval width of the confidence interval: " + simulationHandler.getHalfIntervalWidth());
    	System.out.println("- Confidence level: " + simulationHandler.getConfidenceLevel());
    	System.out.println("- Real Probability: " + simulationHandler.getRealProbability());
    	System.out.println("- Calculations: " + simulationHandler.getCalculations() + "\n");
    	
    	System.out.println("- Used hypothesis testing algorithm: " + simulationHandler.getAlgorithmName());
    	System.out.println("- Correctness indifference level: " + simulationHandler.getCorrectnessIndifferenceLevel());
    	System.out.println("- Power indifference level: " + simulationHandler.getPowerIndifferenceLevel());    	
    	System.out.println("- Guess: " + simulationHandler.getGuess());
    	System.out.println("- TestRuns: " + simulationHandler.getTestRuns());
    	System.out.println("- Type 1 error: " + simulationHandler.getType1Error());
    	System.out.println("- Type 2 error: " + simulationHandler.getType2Error() + "\n");
    	
    	System.out.println("- Fixed number of runs: " + simulationHandler.getFixedNumberOfRuns());
    	System.out.println("- Minimum number of runs: " + simulationHandler.getMinNumberOfRuns());
    	System.out.println("- Maximum number of runs: " + simulationHandler.getMaxNumberOfRuns());    	
    	
    	if (simulationHandler.getSimulationWithFixedNumberOfRuns())
    		System.out.println("- Property check is set to: fixed number of runs");
    	else
    		System.out.println("- Property check is set to: optimal number or runs");    	


    	if (simulationHandler.getPrintRunResults())
    		System.out.println("- Printing the results of the single simulation runs: enabled");
    	else
    		System.out.println("- Printing the results of the single simulation runs: disabled");
    
    }


	public void loadParameters() {
		simulationHandler.loadParameters("libhpng_parameters.cfg");
	}


	public void storeParameters() {
		simulationHandler.storeParameters();
	}
	
	
    private void createLogger(){
		
		logger = Logger.getLogger("libHPnGLog");  
	    FileHandler handler;  

	    try {  

	        //configure the logger with handler and formatter  
	        handler = new FileHandler(loggerPath);  
	        logger.addHandler(handler);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        handler.setFormatter(formatter); 
	        logger.setUseParentHandlers(false);


	    } catch (SecurityException | IOException e) {  
	        System.out.println("logger could not be initialized."); 
	    }
    }
    
    
    
    
	private  Double getTimeFromRoot(SimpleNode propertyRoot) throws InvalidPropertyException{
		
		for (int i=0;i < propertyRoot.jjtGetNumChildren(); i++){
			if (propertyRoot.jjtGetChild(i).toString().equals("TIME"))
				return Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(i).jjtGetChild(0)).jjtGetValue().toString());
		}		
		throw new InvalidPropertyException("Property Error: the time of the property node '" + propertyRoot.toString() + "' could not be identified");    
	}
	
	private  ProbabilityOperator getPropertyKind(SimpleNode root) throws InvalidPropertyException{
		for (int i=0;i < root.jjtGetNumChildren(); i++){
			if (root.jjtGetChild(i).toString().equals("PROBQ"))
				return ProbabilityOperator.confidenceinterval;
			if (root.jjtGetChild(i).toString().equals("PROBGE"))
				return ProbabilityOperator.hypothesisgreaterequal;
			if (root.jjtGetChild(i).toString().equals("PROBL"))
				return ProbabilityOperator.hypothesislower;
			if (root.jjtGetChild(i).toString().equals("PROBLE"))
				return ProbabilityOperator.hypothesislowerequal;
			if (root.jjtGetChild(i).toString().equals("PROBG"))
				return ProbabilityOperator.hypothesisgreater;
		}
		throw new InvalidPropertyException("Property Error: the kind of property (P=?, P>=x, P<=x, P<x, P>x) could not be identified");	
	}
	
	private  Double getProbBound(SimpleNode root) throws InvalidPropertyException{
		for (int i=0;i < root.jjtGetNumChildren(); i++){
			if (root.jjtGetChild(i).toString().equals("PROBGE") || root.jjtGetChild(i).toString().equals("PROBL") || root.jjtGetChild(i).toString().equals("PROBLE") || root.jjtGetChild(i).toString().equals("PROBG"))
				return Double.parseDouble(((SimpleNode)root.jjtGetChild(i).jjtGetChild(0)).jjtGetValue().toString());
				
		}
		if (logger != null)
			logger.severe("Property Error: the boundary node of the property root could not be identified");
		throw new InvalidPropertyException("Property Error: the boundary node of the property root could not be identified");
	}
    	
}
