package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.simulation.SimulationEvent.SimulationEventType;

public class DynamicSimulator extends Simulator {

	public DynamicSimulator(HPnGModel model, Double maxTime) {
		super(model, maxTime);
	}
	

	public Double getAndCompleteNextEvent(Double currentTime, MarkingPlot currentPlot, Boolean printRunResults) throws InvalidRandomVariateGeneratorException{
		
		
				
		return event.getOccurenceTime(); 
		
	}
	
	


}
