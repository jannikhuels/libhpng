package de.wwu.criticalsystems.libhpng.plotting;

public class TransitionEntry extends PlotEntry{
	
	public TransitionEntry(Double time, Boolean enabled) {
		super(time);
		this.enabled = enabled;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}	

	private Boolean enabled;

}
