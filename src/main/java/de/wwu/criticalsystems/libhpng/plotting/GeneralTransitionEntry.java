package de.wwu.criticalsystems.libhpng.plotting;

public class GeneralTransitionEntry extends TransitionEntry {

	public GeneralTransitionEntry(Double time, Boolean enabled, Integer firings) {
		super(time, enabled);
		this.firings = firings;
	}
	
	public Integer getFirings() {
		return firings;
	}
	public void setFirings(Integer firings) {
		this.firings = firings;
	}
	
	private Integer firings;
}
