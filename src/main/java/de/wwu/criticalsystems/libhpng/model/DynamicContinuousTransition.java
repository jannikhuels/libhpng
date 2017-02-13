package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

@XmlRootElement( name = "dynamicContinuousTransition" )
public class DynamicContinuousTransition extends Transition{
	
	public DynamicContinuousTransition(){}

	public DynamicContinuousTransition(String id, Boolean enabled, ArrayList<DynamicContinuousDependency> dependencies) {
		super(id, enabled);
		this.dependencies = dependencies;
	}
	
	public DynamicContinuousTransition(DynamicContinuousTransition transitionToCopy, ArrayList<Transition> transitions) throws ModelCopyingFailedException {
		super(new String (transitionToCopy.getId()), new Boolean (transitionToCopy.getEnabled()));
		this.currentFluid = new Double(transitionToCopy.getCurrentFluid());
		this.adapted = new Boolean(transitionToCopy.getAdapted());
		
		for(DynamicContinuousDependency currentDependencyToCopy : transitionToCopy.getDependencies()) {
			this.dependencies.add(new DynamicContinuousDependency(currentDependencyToCopy, transitions));
		}
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

	public Boolean getAdapted() {
		return adapted;
	}
	public void setAdapted(Boolean adapted) {
		this.adapted = adapted;
	}

	@XmlElements({
	    @XmlElement(name="pid", type=DynamicContinuousDependency.class),
	})
	private ArrayList <DynamicContinuousDependency> dependencies = new ArrayList<DynamicContinuousDependency>();	
	private Double currentFluid;
	private Boolean adapted = false;
}
