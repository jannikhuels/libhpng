package de.wwu.criticalsystems.libhpng.plotting;

public class GeneralTransitionEntry extends TransitionEntry {

	public GeneralTransitionEntry(Double time, Boolean enabled, Double enablingTime) {
		super(time, enabled);
		this.enablingTime = enablingTime;
	}
	
	
	public Double getEnablingTime() {
		return enablingTime;
	}
	
	
	private Double enablingTime;
}
