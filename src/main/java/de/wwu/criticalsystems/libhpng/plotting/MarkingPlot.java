package de.wwu.criticalsystems.libhpng.plotting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import de.wwu.criticalsystems.libhpng.model.*;

public class MarkingPlot {

	public MarkingPlot(Double maxTime) {
		this.maxTime = maxTime;
	}

	public HashMap<String, PlacePlot> getPlacePlots() {
		return placePlots;
	}
	
	public HashMap<String, TransitionPlot> getTransitionPlots() {
		return transitionPlots;
	}	

	public Double getMaxTime() {
		return maxTime;
	}

	private HashMap<String, PlacePlot> placePlots = new HashMap<String, PlacePlot>();
	private HashMap<String, TransitionPlot> transitionPlots = new HashMap<String, TransitionPlot>();
	private Double maxTime;
	

	public void initialize(HPnGModel model){		
		addAllPlaces(model);		
		addAllTransitions(model);
		saveAll(0.0);		
	}
	
		
	public void saveAll(Double time){		
		saveAllTransitionData(time);
		saveDiscretePlaceData(time);
		saveContinuousPlaceData(time);		
	}
	

	
	
	
	public void addAllPlaces(HPnGModel model){
		for (Place place: model.getPlaces())
			addPlace(place);
	}
	

	public void addTransition(Transition transition) {
		this.transitionPlots.put(transition.getId(),new TransitionPlot(transition));
	}
	
	
	public void addAllTransitions(HPnGModel model){
		for (Transition transition: model.getTransitions())
			addTransition(transition);
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

	
	public void printPlot(){
					
		PlacePlot currentPlot;
		System.out.println("Plot:");
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       System.out.println(currentPlot.getReferencedPlace().getId());
			for (PlotEntry entry : currentPlot.getEntries()){
				if (entry.getClass().equals(ContinuousPlaceEntry.class))
					System.out.println("   " + entry.getTime() + ": " + ((ContinuousPlaceEntry)entry).getFluidLevel() + ", " + ((ContinuousPlaceEntry)entry).getDrift() );
				else
					System.out.println("   " + entry.getTime() + ": " + ((DiscretePlaceEntry)entry).getNumberOfTokens() );
			}
	    }		
	}
	
	
	/*public void plotContinuousPlaces(){
		XYLineGraph graph = new XYLineGraph("Continuous Places", "time", "fluid level");
		
		PlacePlot currentPlot;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
			if (currentPlot.getReferencedPlace().getClass().equals(ContinuousPlace.class)){			
				graph.addSeries(currentPlot.getReferencedPlace().getId());
				for (PlotEntry entry : currentPlot.getEntries()){
					graph.addSeriesEntry(currentPlot.getReferencedPlace().getId(), entry.getTime(),  ((ContinuousPlaceEntry)entry).getFluidLevel());
				}
			}
		}		
        graph.pack();
        RefineryUtilities.centerFrameOnScreen(graph);
        graph.setVisible(true);
	}
	
	public void plotDiscretePlaces(){
		XYLineGraph graph = new XYLineGraph("Discrete Places", "time", "tokens");		
		PlacePlot currentPlot;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	    	currentPlot = it.next().getValue();
			if (currentPlot.getReferencedPlace().getClass().equals(DiscretePlace.class)){			
				graph.addSeries(currentPlot.getReferencedPlace().getId());
				for (PlotEntry entry : currentPlot.getEntries()){
					graph.addSeriesEntry(currentPlot.getReferencedPlace().getId(), entry.getTime(),  ((DiscretePlaceEntry)entry).getNumberOfTokens().doubleValue());
				}
			}
		}		
        graph.pack();
        RefineryUtilities.centerFrameOnScreen(graph);
        graph.setVisible(true);
	}*/
	
	
	private void addPlace(Place place) {
		this.placePlots.put(place.getId(),new PlacePlot(place));
	}
	

	
	private void saveContinuousPlaceData(Double time){
		
		PlacePlot currentPlot;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedPlace().getClass().equals(ContinuousPlace.class)){
	    	   
	    	  ContinuousPlace place = (ContinuousPlace) currentPlot.getReferencedPlace();				
	    	  ContinuousPlaceEntry entry = new ContinuousPlaceEntry(time, place.getFluidLevel(), place.getDrift());
	    	  currentPlot.addEntry(entry);	    	  			
			}
	    }
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
