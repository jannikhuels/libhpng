package de.wwu.criticalsystems.libhpng.plotting;

public class ContinuousPlaceEntry extends PlotEntry {
	
	public ContinuousPlaceEntry(Double time, Double fluidLevel, Double drift) {
		super(time);
		this.fluidLevel = fluidLevel;
		this.drift = drift;
	}
	
	
	public Double getFluidLevel() {
		return fluidLevel;
	}
	
	public Double getDrift() {
		return drift;
	}
	
	
	private Double fluidLevel;
	private Double drift;
}
