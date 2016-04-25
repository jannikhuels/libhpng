package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement (name = "guardArc")
public class GuardArc extends Arc{

	public GuardArc(){}
	
	public GuardArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition) {
		super(id, weight, connectedPlace, connectedTransition);
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
	
	
	public void checkCondition(){
		
		Place p = this.getConnectedPlace();
		
		conditionFulfilled = true;
		if (p.getClass().equals(DiscretePlace.class)){			
			if (((DiscretePlace)p).getNumberOfTokens() < this.getWeight())
				conditionFulfilled = false;						
		} else {			
			if (((ContinuousPlace)p).getFluidLevel() < this.getWeight())
				conditionFulfilled = false;		
		}
		
		if (inhibitor) 
			conditionFulfilled = !conditionFulfilled;
	
	}
}
