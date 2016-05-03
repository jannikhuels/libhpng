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
	
	public Double getCurrentFluid() {
		return currentFluid;
	}
	public void setCurrentFluid(Double currentFluid) {
		this.currentFluid = currentFluid;
	}

	@XmlElements({
	    @XmlElement(name="pid", type=DynamicContinuousDependency.class),
	})
	private ArrayList <DynamicContinuousDependency> dependencies = new ArrayList<DynamicContinuousDependency>();	
	private Double currentFluid;
}
