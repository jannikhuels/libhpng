package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import org.springframework.shell.Bootstrap;

public class Main {	
 
    public static void main(String[] args){
    		
    	try {
			Bootstrap.main(args);
		} catch (IOException e) {
			System.out.println("An error occured while loading the shell. libhpng cannot be executed.");
		}
    }
    
      
    //TODO: in shell
    public static void mainSimulation(String[] args) {
    	
    	/*	Logger logger = createLogger();    		
    	
    	ModelReader reader = new ModelReader();
    	HPnGModel model = reader.readModel("examples/example.xml");
    	
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
    			
    			handler.simulateAndCheckProperty(model, root);
    			
    	    } catch (ParseException e) {
    			e.printStackTrace();
    		} catch (PropertyError e) {
    			e.printStackTrace();
    		}
    	}*/
    }
}




