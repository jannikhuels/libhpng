package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "parameter")
public class DynamicContinuousDependency {

	public DynamicContinuousDependency(){}
	
	public DynamicContinuousDependency(ContinuousTransition transition,
			Double coefficient) {
		super();
		this.transition = transition;
		this.coefficient = coefficient;
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
