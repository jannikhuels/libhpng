package de.wwu.criticalsystems.libhpng.plotting;

public class DeterministicTransitionEntry extends TransitionEntry {
	
	public DeterministicTransitionEntry(Double time, Boolean enabled, Double clock) {
		super(time, enabled);
		this.clock = clock;
	}
		
	public Double getClock() {
		return clock;
	}
	public void setClock(Double clock) {
		this.clock = clock;
	}
	private Double clock;
}
