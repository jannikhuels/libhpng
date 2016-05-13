package de.wwu.criticalsystems.libhpng.plotting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jfree.ui.RefineryUtilities;

import de.wwu.criticalsystems.libhpng.model.ContinuousPlace;
import de.wwu.criticalsystems.libhpng.model.DiscretePlace;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.model.Place;

public class MarkingPlot {

	public MarkingPlot() {}

	public HashMap<String, PlacePlot> getPlacePlots() {
		return placePlots;
	}
	

	private HashMap<String, PlacePlot> placePlots = new HashMap<String, PlacePlot>();
	
	public void addPlace(Place place) {
		this.placePlots.put(place.getId(),new PlacePlot(place));
	}
	
	public void addAllPlaces(HPnGModel model){
		for (Place place: model.getPlaces())
			addPlace(place);
	}
	
	public void saveContinuousPlaceData(Double time){
		
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
	
	public void saveDiscretePlaceData(Double time){		
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
