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

import de.wwu.criticalsystems.libhpng.errorhandling.ModelNotReadableException;
import de.wwu.criticalsystems.libhpng.formulaparsing.ParseException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SMCParser;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.init.ModelReader;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;


public class ModelHandler {
	
	public ModelHandler(){
		createLogger();   
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
	}

	
	private HPnGModel model;
	private Logger logger;	
	private String loggerPath = "logFile.log";
	    
	
	public SimpleNode readFormula(){
		
		System.out.println("Please enter the model checking formula to check:");			
		
		try {

			BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
			String formula = buffer.readLine();
			formula += "\n";
			InputStream stream = new ByteArrayInputStream(formula.getBytes(StandardCharsets.UTF_8));
			
			SMCParser parser = new SMCParser(stream);
			SimpleNode root = parser.Input();
		
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
				logger.info("Model has been read successfully.");	
		
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


	
}
