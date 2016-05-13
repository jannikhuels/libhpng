package de.wwu.criticalsystems.libhpng.plotting;

import java.util.ArrayList;
import de.wwu.criticalsystems.libhpng.model.Place;

public class PlacePlot {

	public PlacePlot(Place referencedPlace) {
		this.referencedPlace = referencedPlace;
	}
		
	public Place getReferencedPlace() {
		return referencedPlace;
	}
	public void setReferencedPlace(Place referencedPlace) {
		this.referencedPlace = referencedPlace;
	}
	
	public ArrayList<PlotEntry> getEntries() {
		return entries;
	}
	public void addEntry(PlotEntry entry) {
		this.entries.add(entry);
	}

	private Place referencedPlace;
	private ArrayList<PlotEntry> entries = new ArrayList<PlotEntry>();

	public PlotEntry getNextEntryBeforeOrAtGivenTime(Double time){
		PlotEntry previousEntry = entries.get(0);
		for (PlotEntry currentEntry : entries){
			if (currentEntry.getTime() > time)
				return previousEntry;
			else 
				previousEntry = currentEntry;
		}
		return previousEntry;
	}
	
	
	public PlotEntry getNextEntryAfterGivenTime(Double time){		
		for (PlotEntry currentEntry : entries){
			if (currentEntry.getTime() > time)
				return currentEntry;
		}
		return entries.get(entries.size()-1);
	}
}
