package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

@XmlRootElement (name = "guardArc")
public class GuardArc extends Arc{

	public GuardArc(){}
	
	public GuardArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition) {
		super(id, weight, connectedPlace, connectedTransition);
	}
	
	public GuardArc(GuardArc arcToCopy) throws ModelCopyingFailedException {
		super(new String(arcToCopy.getId()), new Double(arcToCopy.getWeight()), new String(arcToCopy.getFromNode()), new String(arcToCopy.getToNode()));
		this.inhibitor = new Boolean(arcToCopy.getInhibitor());
		this.conditionFulfilled = new Boolean(arcToCopy.getConditionFulfilled());
	}
	

	public Boolean getInhibitor() {
		return inhibitor;
	}
	@XmlAttribute(name = "isInhibitor")
	public void setInhibitor(Boolean inhibitor) {
		this.inhibitor = inhibitor;
	}

	public Boolean getConditionFulfilled() {
		return conditionFulfilled;
	}
	public void setConditionFulfilled(Boolean conditionFulfilled) {
		this.conditionFulfilled = conditionFulfilled;
	}
	

	private Boolean inhibitor;
	private Boolean conditionFulfilled;
		
	
	public Boolean checkCondition(){
		
		Place p = this.getConnectedPlace();		
		conditionFulfilled = true;
		if (p.getClass().equals(DiscretePlace.class)){			
			if (((DiscretePlace)p).getNumberOfTokens() < this.getWeight())
				conditionFulfilled = false;						
		} else {			
			if (((ContinuousPlace)p).getCurrentFluidLevel() < this.getWeight())  //||  ((((ContinuousPlace)p).getCurrentFluidLevel().equals(this.getWeight())) && (((ContinuousPlace)p).getDrift() < 0.0)))
				conditionFulfilled = false;		
		}	
		
		return conditionFulfilled;
	}
}
