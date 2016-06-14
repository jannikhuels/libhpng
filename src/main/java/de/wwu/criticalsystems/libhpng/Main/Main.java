package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import de.wwu.criticalsystems.libhpng.errorhandling.*;
import de.wwu.criticalsystems.libhpng.formulaparsing.*;
import de.wwu.criticalsystems.libhpng.init.*;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.simulation.SimulationHandler;

public class Main {
	
    public static void main(String[] args) {
    	
    	Logger logger = createLogger();    		
    	
		ModelReader reader = new ModelReader();
		HPnGModel model = reader.readModel("examples/example2.xml");
		
		SimulationHandler handler = new SimulationHandler();
		handler.setLogger(logger);
		
		Boolean plotonly = false;
		handler.setPrintRunResults(false);
		
		if (plotonly)
			try {
				handler.simulateAndPlotOnly(30.0, model);
			} catch (ModelNotReadableException e) {
				e.printStackTrace();
			}
		else {
		
			
			try {
			  	SMCParser parser = new SMCParser(System.in);
				SimpleNode root = parser.Input();
				
				//handler.setSimulationWithFixedNumberOfRuns(true);
				handler.simulateAndCheckProperty(model, root);
				
		    } catch (ParseException e) {
				e.printStackTrace();
			} catch (PropertyError e) {
				e.printStackTrace();
			}
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