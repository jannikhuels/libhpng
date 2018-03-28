package de.wwu.criticalsystems.libhpng.simulation;

import java.util.ArrayList;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.simulation.SimulationEvent.SimulationEventType;

public class DynamicSimulator extends Simulator {

	public DynamicSimulator(HPnGModel model, Double maxTime, ArrayList<FluidProperty> fluidProperties) {
		super(model, maxTime);
				this.fluidProperties = fluidProperties;
	}
	
	
	public Double getAndCompleteNextEvent(Double currentTime, MarkingPlot currentPlot, Boolean printRunResults) throws InvalidRandomVariateGeneratorException, InvalidPropertyException{
		
		super.getNextEvent(currentTime);
		Double currentPropertyTime;
		Double currentUntilTime;
		
			
		Boolean check = true;
		for (int i = 0;i<fluidProperties.size();i++){	
			
			currentPropertyTime = fluidProperties.get(i).getPropertyTime();
			currentUntilTime = fluidProperties.get(i).getUntilTime() ;
		
			
			if (currentPropertyTime <= event.getOccurenceTime() && currentTime < currentUntilTime){
				if (!checkAtomicFluidProperty(fluidProperties.get(i), currentTime, Math.max(currentTime, currentPropertyTime), Math.min(event.getOccurenceTime(), currentUntilTime)));
					check = false;
			}	
								
		}			
				
		if (check == false)
			super.getNextEvent(currentTime);
				
		
		//complete event and update model marking
		if (maxTime < event.getOccurenceTime() || event.getEventType().equals(SimulationEventType.no_event)){
			if (maxTime- currentTime > 0.0)
				model.advanceMarking(maxTime- currentTime);
			
			model.updateEnabling(false);
			model.updateFluidRates(maxTime);
			currentPlot.saveAll(maxTime);
			
			
		} else {
			if (event.getOccurenceTime() - currentTime > 0.0)
				model.advanceMarking(event.getOccurenceTime() - currentTime);
				
			completeEvent(printRunResults, currentPlot);
			
			if (printRunResults) 
				model.printCurrentMarking(false, false);
		}
		
		return event.getOccurenceTime(); 
		
	}
		

	private ArrayList<FluidProperty> fluidProperties;
	
		
	private Boolean checkAtomicFluidProperty(FluidProperty property, Double currentTime, Double startPoint, Double endPoint) throws InvalidPropertyException{
		
		Boolean check;
		Double startValue;
		Double endValue;
		Double boundary = property.getBoundary();		
		ContinuousPlace place = property.getPlace();
		Double timeToBoundaryHit;
		
		if (!property.getUntil()){
							
			//compare values 
			startValue = place.getCurrentFluidLevel();
			startValue = Math.max(0.0, startValue + place.getDrift()*(startPoint - currentTime));				
			check = !(startValue - place.getQuantum() <= boundary && boundary <= startValue + place.getQuantum());
					
			
		 	if (check == false) //inconclusive test
				place.setTimeToNextInternalTransition(Math.min(place.getTimeToNextInternalTransition(), startPoint - currentTime));
 	
		} else {
			
			startValue = place.getCurrentFluidLevel();
			startValue = Math.max(0.0, startValue + place.getDrift()*(startPoint - currentTime));
			check = !(startValue - place.getQuantum() <= boundary && boundary <= startValue + place.getQuantum()) || (currentTime == startPoint);
			endValue = Math.max(0.0, startValue + place.getDrift()*(endPoint - startPoint));
			check = check && !(endValue - place.getQuantum() <= boundary && boundary <= endValue + place.getQuantum()) && ((startValue - boundary)*(endValue - boundary)>0.0);
				
			
			if (check == false){
				timeToBoundaryHit = Math.min(place.getTimeTilCurrentFluidLevelHitsBoundary(boundary), place.getTimeTilExactFluidLevelHitsBoundary(boundary, currentTime));
			
				if ((currentTime + timeToBoundaryHit) < endPoint && (timeToBoundaryHit > 0.0 || place.getLastUpdate() < currentTime)) //inconclusive test
					place.setTimeToNextInternalTransition(Math.min(place.getTimeToNextInternalTransition(), timeToBoundaryHit));

			}
		}
 	
		return check;
	}
}
