package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "discretePlace" )
public class DiscretePlace extends Place{
	
	public DiscretePlace() {}
	
	public DiscretePlace(String id, Integer originalNumberOfTokens) {
		super(id);
		this.originalNumberOfTokens = originalNumberOfTokens;
	}

	public DiscretePlace(DiscretePlace placeToCopy) {
		super(new String (placeToCopy.getId()));
		this.originalNumberOfTokens = new Integer (placeToCopy.getOriginalNumberOfTokens());
		this.currentNumberOfTokens =  new Integer (placeToCopy.getNumberOfTokens());
	}

	public Integer getOriginalNumberOfTokens() {
		return originalNumberOfTokens;
	}
	@XmlAttribute (name = "marking")
	public void setOriginalNumberOfTokens(Integer numberOfTokens) {
		this.originalNumberOfTokens = numberOfTokens;
	}
	
	public Integer getNumberOfTokens() {
		return currentNumberOfTokens;
	}	
	public void setNumberOfTokens(Integer numberOfTokens) {
		this.currentNumberOfTokens = numberOfTokens;
	}
	
	private Integer currentNumberOfTokens;
	private Integer originalNumberOfTokens;

	public void resetNumberOfTokens(){
		this.currentNumberOfTokens = this.originalNumberOfTokens;
	}	
}
