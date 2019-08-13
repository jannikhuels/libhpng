package de.wwu.criticalsystems.libhpng.model;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "guardArc")
public class GuardArcVar extends Arc{

	public GuardArcVar(){}

	public GuardArcVar(String id, Double weight, Place connectedPlace,
                       Transition connectedTransition) {
		super(id, weight, connectedPlace, connectedTransition);
	}

	public GuardArcVar(GuardArcVar arcToCopy) throws ModelCopyingFailedException {
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
			if (((ContinuousPlaceVar)p).getCurrentFluidLevel() < this.getWeight()  ||  ((((ContinuousPlaceVar)p).getCurrentFluidLevel().equals(this.getWeight())) && (((ContinuousPlaceVar)p).getDriftValue() < 0.0)))
				conditionFulfilled = false;		
		}	
		
		return conditionFulfilled;
	}
}
