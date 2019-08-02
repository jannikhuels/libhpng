package de.wwu.criticalsystems.libhpng.plotting;

import de.wwu.criticalsystems.libhpng.model.*;
import org.apache.commons.math3.ode.ContinuousOutputModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MarkingPlotVar {


	public MarkingPlotVar(Double maxTime) {
		this.maxTime = maxTime;
	}

	
	public HashMap<String, PlacePlot> getPlacePlots() {
		return placePlots;
	}
	
	public HashMap<String, TransitionPlot> getTransitionPlots() {
		return transitionPlots;
	}	


	private HashMap<String, PlacePlot> placePlots = new HashMap<String, PlacePlot>();
	private HashMap<String, TransitionPlot> transitionPlots = new HashMap<String, TransitionPlot>();
	private Double maxTime;
	

	
	public void initializeContinuousPlacesOnly(HPnGModelVar model){
		addAllContinuousPlaces( model);
		saveAll(0.0);		
	}
	
	public void initializeRelatedOnly(HPnGModelVar model, ArrayList<String> relatedIds){
		for (Place place: model.getPlaces())
		{
			for(String id: relatedIds)
			{
				if(id.equals(place.getId()))
					addPlace(place);
			}
		}
		for (Transition transition: model.getTransitions())
		{
			for(String id: relatedIds)
			{
				if(id.equals(transition.getId()))
					addTransition(transition);
			}
		}
		saveAll(0.0);	
	}	
		
	public void saveAll(Double time){		
		saveAllTransitionData(time);
		saveDiscretePlaceData(time);
		saveContinuousPlaceData(time);		
	}	

	public void addAllContinuousPlaces(HPnGModelVar model){
		for (Place place: model.getPlaces())
			if(place.getClass().equals(ContinuousPlaceVar.class))
				addPlace(place);
	}

	public void addTransition(Transition transition) {
		this.transitionPlots.put(transition.getId(),new TransitionPlot(transition));
	}
	
	public Double getNextEventTime(Double previousTime){
		
		Double time = maxTime;
		Double currentPlotTime = new Double(0);
		PlotEntry currentPlot;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	    	currentPlot = it.next().getValue().getNextEntryAfterGivenTime(previousTime);
	    	if (currentPlot != null)
	    		currentPlotTime = currentPlot.getTime();
	    	if (currentPlot != null && currentPlotTime < time && currentPlotTime > previousTime)
	    		time = currentPlotTime;
	    }
	    
		Iterator<Entry<String, TransitionPlot>> it2 = transitionPlots.entrySet().iterator();
	    while (it2.hasNext()) {
	    	currentPlot = it2.next().getValue().getNextEntryAfterGivenTime(previousTime);
	    	if (currentPlot != null)
	    		currentPlotTime = currentPlot.getTime();
	    	if (currentPlot != null && currentPlotTime < time  && currentPlotTime > previousTime)
	    		time = currentPlotTime;
	    }	
	    return time;
	}	
	
	public Boolean eventAtTime(Double time){
		
		Double currentPlotTime;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	    	currentPlotTime = it.next().getValue().getNextEntryBeforeOrAtGivenTime(time).getTime();
	    	if (currentPlotTime.equals(time))
	    		return true;
	    }
	    
		Iterator<Entry<String, TransitionPlot>> it2 = transitionPlots.entrySet().iterator();
	    while (it2.hasNext()) {
	    	currentPlotTime = it2.next().getValue().getNextEntryBeforeOrAtGivenTime(time).getTime();
	    	if (currentPlotTime.equals(time))
	    		time = currentPlotTime;
	    }
		
	    return false;
	}	
	
	private void addPlace(Place place) {
		this.placePlots.put(place.getId(),new PlacePlot(place));
	}
	
	private void saveContinuousPlaceData(Double time){
		
		PlacePlot currentPlot;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedPlace().getClass().equals(ContinuousPlaceVar.class)){
	    	   
	    	  ContinuousPlaceVar place = (ContinuousPlaceVar) currentPlot.getReferencedPlace();
	    	  ContinuousPlaceEntry entry = new ContinuousPlaceEntry(time, place.getCurrentFluidLevel(), place.getDriftValue());
	    	  currentPlot.addEntry(entry);	    	  			
			}
	    }
	}

	private void saveContinuousPlaceDataFromApprox(ContinuousOutputModel continuousOutputModel){
//		PlacePlot currentPlot;
//		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
//		while (it.hasNext()) {
//			currentPlot = it.next().getValue();
//			if (currentPlot.getReferencedPlace().getClass().equals(ContinuousPlaceVar.class)){
//
//				ContinuousPlaceVar place = (ContinuousPlaceVar) currentPlot.getReferencedPlace();
//				ContinuousPlaceEntry entry = new ContinuousPlaceEntry(time, place.getExactFluidLevel(), place.getExactDrift());
//				currentPlot.addEntry(entry);
//			}
//		}

	}
	
	
	private void saveAllTransitionData(Double time){
		saveDeterministicTransitionData(time);
		saveImmediateOrContinuousTransitionData(time);
		saveGeneralTransitionData(time);
	}	
	
	private void saveDiscretePlaceData(Double time){		
		
		PlacePlot currentPlot;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedPlace().getClass().equals(DiscretePlace.class)){
	    	   
				DiscretePlace place = (DiscretePlace) currentPlot.getReferencedPlace();
				DiscretePlaceEntry entry = new DiscretePlaceEntry(time, place.getNumberOfTokens());
				currentPlot.addEntry(entry);
			}
	    }
	}
		
	private void saveDeterministicTransitionData(Double time){		
		
		TransitionPlot currentPlot;
		
		Iterator<Entry<String, TransitionPlot>> it = transitionPlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedTransition().getClass().equals(DeterministicTransition.class)){
	    	   
	    	   DeterministicTransition transition = (DeterministicTransition) currentPlot.getReferencedTransition();
	    	   DeterministicTransitionEntry entry = new DeterministicTransitionEntry(time, transition.getEnabled(), transition.getClock());
    		   currentPlot.addEntry(entry);	
			}
	    }
	}
		
	private void saveImmediateOrContinuousTransitionData(Double time){		
		
		TransitionPlot currentPlot;

		Iterator<Entry<String, TransitionPlot>> it = transitionPlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedTransition().getClass().equals(ImmediateTransition.class) || currentPlot.getReferencedTransition().getClass().equals(ContinuousTransition.class)){
	    	   
	    	   Transition transition = currentPlot.getReferencedTransition();
	    	   TransitionEntry entry = new TransitionEntry(time, transition.getEnabled());
	    	   currentPlot.addEntry(entry);  	  
	       }
	    }
	}
		
	private void saveGeneralTransitionData(Double time){		
		
		TransitionPlot currentPlot;
		
		Iterator<Entry<String, TransitionPlot>> it = transitionPlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedTransition().getClass().equals(GeneralTransition.class)){
	    	   
	    	   GeneralTransition transition = (GeneralTransition)currentPlot.getReferencedTransition(); 
	    	   GeneralTransitionEntry entry = new GeneralTransitionEntry(time, transition.getEnabled(), transition.getEnablingTime());
	    	   currentPlot.addEntry(entry);	    	   
	       }
	    }
	}	
}
