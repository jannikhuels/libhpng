package de.wwu.criticalsystems.libhpng.Main;

import de.wwu.criticalsystems.libhpng.init.*;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.simulation.Simulator;

public class Main {
    public static void main(String[] args) {
    	
    		ModelReader reader = new ModelReader();
    		HPnGModel model = reader.readModel("example.xml");
    		
    		Simulator simulator = new Simulator();
    		simulator.simulateNRuns(1, 20.0, model); 
    }
}
