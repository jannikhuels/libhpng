package de.wwu.criticalsystems.libhpng.Main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidSimulationParameterException;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelNotReadableException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.formulaparsing.ParseException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SMCParser;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.init.ModelReader;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.simulation.SimulationHandler;


public class ModelHandler {
	
	public ModelHandler(){
		createLogger();
		simulationHandler = new SimulationHandler();
		simulationHandler.setLogger(logger);
	}
	

	public HPnGModel getModel() {
		return model;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public String getLoggerPath() {
		return loggerPath;
	}
	public void setLoggerPath(String loggerPath) {
		this.loggerPath = loggerPath;
		createLogger();
		simulationHandler.setLogger(logger);	    
	}
	
	
	private HPnGModel model;
	private Logger logger;	
	private String loggerPath = "logFile.log";
	private SMCParser parser;
	private SimulationHandler simulationHandler;
	
	
	public SimpleNode readFormula(){
		
		System.out.println("Please enter the model checking formula to check:");			
		
		try {
			//InputStreamReader in = new InputStreamReader(System.in);
		
			
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
	
	
	public void readModel(String xmlPath){
		
		try {    			
			ModelReader reader = new ModelReader();
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
			System.out.println("An error occured while reading the model file. Please see the error log and recheck the model.");
		}			
	}
	
	
	public void checkFormula(SimpleNode root){
	
    	try {
			simulationHandler.simulateAndCheckProperty(model, root);
		} catch (InvalidPropertyException e) {
			System.out.println("The formula to check is invalid. Please see the error log.");
		} catch (ModelNotReadableException e) {
			System.out.println("An error occured while simulating due to an incorrect model file. Please see the error log and recheck the model.");
		} catch (InvalidRandomVariateGeneratorException e) {
			System.out.println("An internal error occured while simulating. Please see the error log.");
		}    			
	}	
	
	
	public void plotPlaces(Double maxTime){
		   	
		try {
			simulationHandler.simulateAndPlotOnly(maxTime, model);
			
		} catch (ModelNotReadableException e) {
			if (logger != null) 
				logger.severe("The model could not be read in.");
			System.out.println("An error occured while reading the model file. Please see the error log and recheck the model.");
		} catch (InvalidRandomVariateGeneratorException e) {
			System.out.println("An internal error occured while simulating. Please see the error log.");
		}    			
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
    	System.out.println("- Confidence level: " + simulationHandler.getConfidenceLevel() + "\n");
    	
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
		simulationHandler.loadParameters();
	}


	public void storeParameters() {
		simulationHandler.storeParameters();
	}
    	
}
