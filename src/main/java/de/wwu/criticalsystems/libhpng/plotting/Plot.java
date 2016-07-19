package de.wwu.criticalsystems.libhpng.plotting;

import java.util.ArrayList;

public abstract class Plot {
	
	public Plot() {};
	
	public ArrayList<PlotEntry> getEntries() {
		return entries;
	}
	public void addEntry(PlotEntry entry) {
		this.entries.add(entry);
	}

	private ArrayList<PlotEntry> entries = new ArrayList<PlotEntry>();

	
	public PlotEntry getNextEntryBeforeOrAtGivenTime(Double time){
		
		if (entries.size() == 0)
			return null;
		
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
		if (entries.size() == 0)
			return null;
		return entries.get(entries.size()-1);
	}
}
