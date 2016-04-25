package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.*;
//import java.util.List;

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

}
