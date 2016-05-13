package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;

@XmlRootElement (name = "places")
@XmlSeeAlso({DeterministicTransition.class, ContinuousTransition.class, ImmediateTransition.class, GeneralTransition.class, DynamicContinuousTransition.class})
public abstract class Transition {
	
	public Transition(){}

	public Transition(String id, Boolean enabled) {
		this.id = id;
		this.enabled = enabled;
	}

	public String getId() {
		return id;
	}	
	@XmlAttribute (name = "id") @XmlID
	public void setId(String id) {
		this.id = id;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public ArrayList<Arc> getConnectedArcs() {
		return connectedArcs;
	}
	public void setConnectedArcs(ArrayList<Arc> connectedArcs) {
		this.connectedArcs = connectedArcs;
	}

	private String id;
	private Boolean enabled;
	private ArrayList<Arc> connectedArcs = new ArrayList<Arc>();
		
	public void fireTransition(){
		
		DiscretePlace place;		
		for (Arc arc: connectedArcs){
			
			if (arc.getClass().equals(DiscreteArc.class)){
				place = (DiscretePlace)arc.getConnectedPlace();
				
				if (((DiscreteArc)arc).getDirection() == DiscreteArcType.input) 
					//input for place = output for transition -> add tokens
					place.setNumberOfTokens(place.getNumberOfTokens() + arc.getWeight().intValue());
				else //output for place = input for transition -> reduce tokens
					place.setNumberOfTokens(place.getNumberOfTokens() - arc.getWeight().intValue());
				
			}
		}
		
		if (this.getClass().equals(DeterministicTransition.class)){
			((DeterministicTransition)this).setClock(0.0);
		} else if (this.getClass().equals(GeneralTransition.class)){
			((GeneralTransition)this).setEnablingTime(0.0);
			((GeneralTransition)this).setNewRandomFiringTime();
			((GeneralTransition)this).increaseFirings();
		}
	}
}