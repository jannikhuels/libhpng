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
    		simulator.simulateAndPlotOnly(50, 30.0, model, 0.99);
    		//simulator.simulateAndCheckPropertyWithFixedIntervalWidth(model, PropertyType.firings, "tg1", 10.0, 2.0, Comparator.greaterequal, 0.05, 0.95, 100, 1000000);
    		//simulator.simulateAndCheckPropertyWithFixedNumberOfRuns(model, PropertyType.firings, "tg1", 10.0, 2.0, Comparator.greaterequal, 1000, 0.95);
    }
}