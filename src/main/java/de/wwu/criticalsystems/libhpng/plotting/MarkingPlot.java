package de.wwu.criticalsystems.libhpng.plotting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jfree.ui.RefineryUtilities;
import de.wwu.criticalsystems.libhpng.model.*;

public class MarkingPlot {

	public MarkingPlot() {}

	public HashMap<String, PlacePlot> getPlacePlots() {
		return placePlots;
	}
	
	public HashMap<String,TransitionPlot> getTransitionPlots() {
		return transitionPlots;
	}

	private HashMap<String, PlacePlot> placePlots = new HashMap<String, PlacePlot>();
	private HashMap<String, TransitionPlot> transitionPlots = new HashMap<String, TransitionPlot>();
	
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
	
	
	public void addPlace(Place place) {
		this.placePlots.put(place.getId(),new PlacePlot(place));
	}
	
	
	public void addAllPlaces(HPnGModel model){
		for (Place place: model.getPlaces())
			addPlace(place);
	}
	
	
	public void saveContinuousPlaceData(Double time){
		
		PlacePlot currentPlot;
		ContinuousPlaceEntry previous;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedPlace().getClass().equals(ContinuousPlace.class)){
	    	   
	    	   ContinuousPlace place = (ContinuousPlace) currentPlot.getReferencedPlace();				
	    	   previous = (ContinuousPlaceEntry)currentPlot.getNextEntryBeforeOrAtGivenTime(time);
	    	   
	    	   if (previous == null || !previous.getFluidLevel().equals(place.getFluidLevel()) || !previous.getDrift().equals(place.getDrift())){
					ContinuousPlaceEntry entry = new ContinuousPlaceEntry(time, place.getFluidLevel(), place.getDrift());
					currentPlot.addEntry(entry);				
	    	   }			
			}
	    }
	}
	
	
	public void saveDiscretePlaceData(Double time){		
		
		PlacePlot currentPlot;
		DiscretePlaceEntry previous;
		
		Iterator<Entry<String, PlacePlot>> it = placePlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedPlace().getClass().equals(DiscretePlace.class)){
	    	   
				DiscretePlace place = (DiscretePlace) currentPlot.getReferencedPlace();
				previous = (DiscretePlaceEntry)currentPlot.getNextEntryBeforeOrAtGivenTime(time);
				
				if (previous == null || !previous.getNumberOfTokens().equals(place.getNumberOfTokens())){
					DiscretePlaceEntry entry = new DiscretePlaceEntry(time, place.getNumberOfTokens());
					currentPlot.addEntry(entry);
				}
			}
	    }
	}
	
	public void addTransition(Transition transition) {
		this.transitionPlots.put(transition.getId(),new TransitionPlot(transition));
	}
	
	public void addAllTransitions(HPnGModel model){
		for (Transition transition: model.getTransitions())
			addTransition(transition);
	}
	
	
	public void saveDeterministicTransitionData(Double time){		
		TransitionPlot currentPlot;
		DeterministicTransitionEntry previous;
		
		Iterator<Entry<String, TransitionPlot>> it = transitionPlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedTransition().getClass().equals(DeterministicTransition.class)){
	    	   
	    	   DeterministicTransition transition = (DeterministicTransition) currentPlot.getReferencedTransition();
	    	   previous = (DeterministicTransitionEntry)currentPlot.getNextEntryBeforeOrAtGivenTime(time);
	    	   
	    	   if (previous == null || !previous.getEnabled().equals(transition.getEnabled()) || !previous.getClock().equals(transition.getClock())){
	    		   DeterministicTransitionEntry entry = new DeterministicTransitionEntry(time, transition.getEnabled(), transition.getClock());
	    		   currentPlot.addEntry(entry);
	    	   }	    	   
			}
	    }
	}
	
	
	public void saveImmediateOrContinuousTransitionData(Double time){		
		
		TransitionPlot currentPlot;
		TransitionEntry previous;
		
		Iterator<Entry<String, TransitionPlot>> it = transitionPlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedTransition().getClass().equals(ImmediateTransition.class) || currentPlot.getReferencedTransition().getClass().equals(ContinuousTransition.class)){
	    	   
	    	   Transition transition = currentPlot.getReferencedTransition();
	    	   previous = (TransitionEntry) currentPlot.getNextEntryBeforeOrAtGivenTime(time);
	    	   
	    	   if (previous == null || !previous.getEnabled().equals(transition.getEnabled())){	    		   
		    	   TransitionEntry entry = new TransitionEntry(time, transition.getEnabled());
		    	   currentPlot.addEntry(entry);
	    	   }	    	  
	       }
	    }
	}
	
	public void saveGeneralTransitionData(Double time){		
		
		TransitionPlot currentPlot;
		GeneralTransitionEntry previous;
		
		Iterator<Entry<String, TransitionPlot>> it = transitionPlots.entrySet().iterator();
	    while (it.hasNext()) {
	       currentPlot = it.next().getValue();
	       if (currentPlot.getReferencedTransition().getClass().equals(GeneralTransition.class)){
	    	   
	    	   GeneralTransition transition = (GeneralTransition)currentPlot.getReferencedTransition(); 
	    	   previous = (GeneralTransitionEntry)currentPlot.getNextEntryBeforeOrAtGivenTime(time);
	    	   if (previous == null || !previous.getEnabled().equals(transition.getEnabled()) || !previous.getFirings().equals(transition.getFirings())){
	    		   GeneralTransitionEntry entry = new GeneralTransitionEntry(time, transition.getEnabled(), transition.getFirings());
		    	   currentPlot.addEntry(entry);
	    	   }	    	   
	       }
	    }
	}
	
	
	public void saveAllTransitionData(Double time){
		saveDeterministicTransitionData(time);
		saveImmediateOrContinuousTransitionData(time);
		saveGeneralTransitionData(time);
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
	
	public void plotContinuousPlaces(){
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
	}

}
