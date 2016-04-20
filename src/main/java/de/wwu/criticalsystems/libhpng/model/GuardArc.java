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

	private Boolean inhibitor;
}
