package de.wwu.criticalsystems.libhpng.model;

public class DynamicContinuousDependency {

	public DynamicContinuousDependency(ContinuousTransition transition,
			Double coefficient) {
		super();
		this.transition = transition;
		this.coefficient = coefficient;
	}
	
	private ContinuousTransition transition;
	private Double coefficient;
	
	public ContinuousTransition getTransition() {
		return transition;
	}
	public void setTransition(ContinuousTransition transition) {
		this.transition = transition;
	}
	public Double getCoefficient() {
		return coefficient;
	}
	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}
}
