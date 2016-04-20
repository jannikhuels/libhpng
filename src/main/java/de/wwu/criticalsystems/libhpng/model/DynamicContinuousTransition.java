package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "dynamicContinuousTransition" )
public class DynamicContinuousTransition extends Transition{
	
	public DynamicContinuousTransition(){}

	public DynamicContinuousTransition(String id, Boolean enabled,
			ArrayList<DynamicContinuousDependency> dependencies) {
		super(id, enabled);
		this.dependencies = dependencies;
	}
	
	public ArrayList<DynamicContinuousDependency> getDependencies() {
		return dependencies;
	}

	/*public void setDependencies(ArrayList<DynamicContinuousDependency> dependencies) {
		this.dependencies = dependencies;
	}*/
	

	@XmlElements({
	    @XmlElement(name="pid", type=DynamicContinuousDependency.class),
	})
	private ArrayList <DynamicContinuousDependency> dependencies = new ArrayList<DynamicContinuousDependency>();
	
}
