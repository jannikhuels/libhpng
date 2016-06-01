package de.wwu.criticalsystems.libhpng.Main;

import de.wwu.criticalsystems.libhpng.formulaparsing.*;
import de.wwu.criticalsystems.libhpng.init.*;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.simulation.Simulator;

public class Main {
    public static void main(String[] args) {
    	
    		ModelReader reader = new ModelReader();
    		HPnGModel model = reader.readModel("examples/example2.xml");
    		
    		SMCParser parser = new SMCParser(System.in);
    		
    		Simulator simulator = new Simulator();
    		
    		//simulator.simulateAndPlotOnly(200, 30.0, model, 0.99);
    		
    		
    	    try {
				SimpleNode root = parser.Input();
				simulator.simulateAndCheckPropertyWithFixedIntervalWidth(model, root, 0.05, 0.95, 100, 1000000);
				//simulator.simulateAndCheckPropertyWithFixedNumberOfRuns(model, root, 100, 0.95);
				
    	    } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}