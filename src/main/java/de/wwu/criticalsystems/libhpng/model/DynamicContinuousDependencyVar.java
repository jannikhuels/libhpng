package de.wwu.criticalsystems.libhpng.model;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;

@XmlRootElement(name = "parameter")
public class DynamicContinuousDependencyVar {

	public DynamicContinuousDependencyVar(){}

	public DynamicContinuousDependencyVar(ContinuousTransitionVar transition, Double coefficient) {
		this.transition = transition;
		this.coefficient = coefficient;
	}

	public DynamicContinuousDependencyVar(DynamicContinuousDependencyVar dependencyToCopy, ArrayList<Transition> transitions) throws ModelCopyingFailedException {

		String id = dependencyToCopy.getTransition().getId();
		for (Transition continuousTransition : transitions) {
	        if (continuousTransition.getId().equals(id) && continuousTransition.getClass().equals(ContinuousTransition.class)) {
	            this.transition = (ContinuousTransitionVar) continuousTransition;
	        }
	    }
		if (this.transition == null)
			throw new ModelCopyingFailedException("Continuous transition with the ID '" + id + "' could not be matched for a dynamic continuous transition");
		this.coefficient = new Double(dependencyToCopy.getCoefficient());
	}

	public ContinuousTransitionVar getTransition() {
		return transition;
	}
	@XmlValue @XmlIDREF
	public void setTransition(ContinuousTransitionVar transition) {
		this.transition = transition;
	}
	
	public Double getCoefficient() {
		return coefficient;
	}
	@XmlAttribute(name = "coef")
	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}
		
	private ContinuousTransitionVar transition;
	private Double coefficient;
}
