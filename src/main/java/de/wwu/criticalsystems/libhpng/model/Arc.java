package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement (name = "arcs")
@XmlSeeAlso({ContinuousArc.class, DiscreteArc.class, GuardArc.class})
public abstract class Arc {
	
	public Arc(){}

	public Arc(String id, Double weight, Place connectedPlace, 
			Transition connectedTransition) {
		this.id = id;
		this.weight = weight;
		this.connectedPlace = connectedPlace;
		this.connectedTransition = connectedTransition;
	}
	
	
	public Arc(String id, Double weight, String fromNode, String toNode){
		this.id = id;
		this.weight = weight;
		this.fromNode = fromNode;
		this.toNode = toNode;
	}
	
	public String getId() {
		return id;
	}
	@XmlAttribute (name = "id")
	public void setId(String id) {
		this.id = id;
	}
	
	public Double getWeight() {
		return weight;
	}
	@XmlAttribute (name = "weight")
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	public Place getConnectedPlace() {
		return connectedPlace;
	}
	public void setConnectedPlace(Place connectedPlace) {
		this.connectedPlace = connectedPlace;
	}
	
	public Transition getConnectedTransition() {
		return connectedTransition;
	}
	public void setConnectedTransition(Transition connectedTransition) {
		this.connectedTransition = connectedTransition;
	}
	
	public String getFromNode() {
		return fromNode;
	}
	@XmlAttribute (name = "fromNode")
	public void setFromNode(String fromNode) {
		this.fromNode = fromNode;
	}

	public String getToNode() {
		return toNode;
	}
	@XmlAttribute (name = "toNode")
	public void setToNode(String toNode) {
		this.toNode = toNode;
	}

	private String id;
	private Double weight;
	private Place connectedPlace;
	private Transition connectedTransition;	
	private String fromNode;
	private String toNode;
}
