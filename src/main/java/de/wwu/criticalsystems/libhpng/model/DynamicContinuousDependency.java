package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

@XmlRootElement(name = "parameter")
public class DynamicContinuousDependency {

	public DynamicContinuousDependency(){}
	
	public DynamicContinuousDependency(ContinuousTransition transition, Double coefficient) {
		this.transition = transition;
		this.coefficient = coefficient;
	}
	
	public DynamicContinuousDependency(DynamicContinuousDependency dependencyToCopy, ArrayList<Transition> transitions) throws ModelCopyingFailedException {

		String id = dependencyToCopy.getTransition().getId();
		for (Transition continuousTransition : transitions) {
	        if (continuousTransition.getId().equals(id) && continuousTransition.getClass().equals(ContinuousTransition.class)) {
	            this.transition = (ContinuousTransition) continuousTransition;
	        }
	    }
		if (this.transition == null)
			throw new ModelCopyingFailedException("Continuous transition with the ID '" + id + "' could not be matched for a dynamic continuous transition");
		this.coefficient = new Double(dependencyToCopy.getCoefficient());
	}

	public ContinuousTransition getTransition() {
		return transition;
	}
	@XmlValue @XmlIDREF
	public void setTransition(ContinuousTransition transition) {
		this.transition = transition;
	}
	
	public Double getCoefficient() {
		return coefficient;
	}
	@XmlAttribute(name = "coef")
	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}
		
	private ContinuousTransition transition;
	private Double coefficient;
}
