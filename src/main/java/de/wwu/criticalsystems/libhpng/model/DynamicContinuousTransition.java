package de.wwu.criticalsystems.libhpng.model;

import java.util.List;


public class DynamicContinuousTransition extends Transition{

	public DynamicContinuousTransition(String id, Boolean enabled,
			List<DynamicContinuousDependency> dependencies) {
		super(id, enabled);
		this.dependencies = dependencies;
	}
	
	public List<DynamicContinuousDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<DynamicContinuousDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	private List<DynamicContinuousDependency> dependencies;
	
}
