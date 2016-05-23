package de.wwu.criticalsystems.libhpng.Main;

import de.wwu.criticalsystems.libhpng.init.*;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.simulation.ConfidenceIntervalCalculator.Comparator;
import de.wwu.criticalsystems.libhpng.simulation.ConfidenceIntervalCalculator.PropertyType;
import de.wwu.criticalsystems.libhpng.simulation.Simulator;

public class Main {
    public static void main(String[] args) {
    	
    		ModelReader reader = new ModelReader();
    		HPnGModel model = reader.readModel("examples/example2.xml");
    		
    		Simulator simulator = new Simulator();
    		//simulator.simulateNRuns(10, 30.0, model);
    		simulator.simulateIteratively(model, "tg1", 0.01, PropertyType.firings, 10.0, 2.0, Comparator.greaterequal, 100, 1000000);
    		
    		
    }
}
