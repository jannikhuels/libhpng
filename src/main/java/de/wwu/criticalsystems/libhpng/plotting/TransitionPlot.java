package de.wwu.criticalsystems.libhpng.plotting;

import de.wwu.criticalsystems.libhpng.model.Transition;

public class TransitionPlot extends Plot{	
	
	public TransitionPlot(Transition referencedTransition) {
		super();
		this.referencedTransition = referencedTransition;
	}
	
	
	public Transition getReferencedTransition() {
		return referencedTransition;
	}
	
		
	private Transition referencedTransition;
}
