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
		
		} catch (ModelNotReadableException e) {		
			if (logger != null) 
				logger.severe("The model could not be read in.");
			System.out.println("An Error occured while reading the model file. Please see the error log and recheck the model.");
		}			
	}
	
	
	public void checkFormula(SimpleNode root){
	
    	try {
			simulationHandler.simulateAndCheckProperty(model, root);
		} catch (InvalidPropertyException e) {
			System.out.println("The formula to check is invalid. Please see the error log.");
		} catch (ModelNotReadableException e) {
			System.out.println("An Error occured while simulating due to an incorrect model file. Please see the error log and recheck the model.");
		}    			
	}	
	
	
	public void plotPlaces(Double maxTime){
		   	
		try {
			simulationHandler.simulateAndPlotOnly(maxTime, model);
			
		} catch (ModelNotReadableException e) {
			if (logger != null) 
				logger.severe("The model could not be read in.");
			System.out.println("An Error occured while reading the model file. Please see the error log and recheck the model.");
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
	    			simulationHandler.setHalfWidthOfIndifferenceRegion((Double)value);
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
	    	}
	    	
	    } catch (InvalidSimulationParameterException e) {
	    	if (logger != null) 
				logger.severe("The " + parameter + " parameter chould not be changed.");
			System.out.println("An Error occured while changing the parameter. Please see the error log.");
		
	    }
    }
    
    
   
    public void printParameters(){    	

    	System.out.println("- half interval width of the confidence interval: " + simulationHandler.getHalfIntervalWidth());
    	System.out.println("- half width of the indifference region: " + simulationHandler.getHalfWidthOfIndifferenceRegion());
    	System.out.println("- confidence level: " + simulationHandler.getConfidenceLevel());
    	System.out.println("- type 1 error: " + simulationHandler.getType1Error());
    	System.out.println("- type 2 error: " + simulationHandler.getType2Error());
    	System.out.println("- fixed number of runs: " + simulationHandler.getFixedNumberOfRuns());
    	
    	if (simulationHandler.getSimulationWithFixedNumberOfRuns())
    		System.out.println("- property check is set to: fixed number of runs");
    	else
    		System.out.println("- property check is set to: optimal number or runs");
    	
    	System.out.println("- minimum number of runs: " + simulationHandler.getMinNumberOfRuns());
    	System.out.println("- maximum number of runs: " + simulationHandler.getMaxNumberOfRuns());

    	if (simulationHandler.getPrintRunResults())
    		System.out.println("- printing the results of the single simulation runs: enabled");
    	else
    		System.out.println("- printing the results of the single simulation runs: disabled");
    
    }


	public void loadParameters() {
		simulationHandler.loadParameters();
	}


	public void storeParameters() {
		simulationHandler.storeParameters();
	}
    	
}
