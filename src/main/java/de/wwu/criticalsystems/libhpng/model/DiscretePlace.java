package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "discretePlace" )
public class DiscretePlace extends Place{
	
	public DiscretePlace() {}
	
	public DiscretePlace(String id, Integer numberOfTokens) {
		super(id);
		this.numberOfTokens = numberOfTokens;
	}

	public Integer getNumberOfTokens() {
		return numberOfTokens;
	}

	@XmlAttribute (name = "marking")
	public void setNumberOfTokens(Integer numberOfTokens) {
		this.numberOfTokens = numberOfTokens;
	}
	
	private Integer numberOfTokens;

}
