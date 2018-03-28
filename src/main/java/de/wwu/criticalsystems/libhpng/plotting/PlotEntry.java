package de.wwu.criticalsystems.libhpng.plotting;

public abstract class PlotEntry {
	
	public PlotEntry(Double time) {
		this.time = time;
	}
	
	
	public Double getTime() {
		return time;
	}

	
	private Double time;
}
