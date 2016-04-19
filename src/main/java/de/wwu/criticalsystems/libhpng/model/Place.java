package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;
//import java.util.List;

@XmlRootElement (name = "places")
@XmlSeeAlso({DiscretePlace.class, ContinuousPlace.class})
public abstract class Place {
	
	public Place(){}
	
	public Place(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	@XmlAttribute (name = "id")
	public void setId(String id) {
		this.id = id;
	}
	
	private String id;
	/*private List<Arc> inputArcs;
	private List<Arc> outputArcs;
	private List<Arc> guardArcs;*/	
}
