package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import de.wwu.criticalsystems.libhpng.errorhandling.PropertyError;
import de.wwu.criticalsystems.libhpng.formulaparsing.*;
import de.wwu.criticalsystems.libhpng.init.*;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.simulation.Simulator;

public class Main {
	
    public static void main(String[] args) {
    	
    	Logger logger = createLogger();    		
    	
		ModelReader reader = new ModelReader();
		HPnGModel model = reader.readModel("examples/example2.xml");
		
		SMCParser parser = new SMCParser(System.in);
		
		Simulator simulator = new Simulator();
		simulator.setLogger(logger);
		
		//simulator.simulateAndPlotOnly(200, 30.0, model, 0.99);
		
		
		try {
			SimpleNode root = parser.Input();
			simulator.simulateAndCheckProperty(model, root);
			
	    } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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