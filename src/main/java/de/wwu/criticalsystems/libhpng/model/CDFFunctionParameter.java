package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "parameter")
public class CDFFunctionParameter {

	public CDFFunctionParameter(){}
	
	public CDFFunctionParameter(String name, Double value) {
		this.name = name;
		this.value = value;
	}
	
	public CDFFunctionParameter(CDFFunctionParameter parameterToCopy) {
		this.name = new String (parameterToCopy.getName());
		this.value = new Double (parameterToCopy.getValue());
	}

	public String getName() {
		return name;
	}
	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getValue() {
		return value;
	}
	@XmlAttribute(name = "value")
	public void setValue(Double value) {
		this.value = value;
	}	
	
	private String name;
	private Double value;
}
