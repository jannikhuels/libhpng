package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import java.util.logging.*;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelNotReadableException;
import de.wwu.criticalsystems.libhpng.init.*;
import de.wwu.criticalsystems.libhpng.model.*;

public class Main {	
 
    public static void main(String[] args) {
    	
		Logger logger = createLogger();    		
		
		try {    			

			ModelReader reader = new ModelReader();
			reader.setLogger(logger);
			HPnGModel model = reader.readModel("examples/example2.xml");
			logger.info("Model has been read successfully.");				
		
		} catch (ModelNotReadableException e) {
		
			logger.severe("The model could not be read in.");
			System.out.println("An Error occured while reading the model file. Please see the error log and recheck the model.");
		
		}
    }
    
    
    private static Logger createLogger(){
		
		Logger logger = Logger.getLogger("libHPnGLog");  
	    FileHandler handler;  

	    try {  

	        // This block configure the logger with handler and formatter  
	        handler = new FileHandler("logFile.log");  
	        logger.addHandler(handler);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        handler.setFormatter(formatter); 
	        logger.setUseParentHandlers(false);


	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
		return logger;  
    }
}
