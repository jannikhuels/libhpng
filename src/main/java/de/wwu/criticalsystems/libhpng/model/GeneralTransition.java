package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "generalTransition" )
public class GeneralTransition extends Transition{
	
	public GeneralTransition(){}

	public GeneralTransition(String id, Boolean enabled, Double weight,
			Integer priority, CDFFunction distribution, Double enablingTime) {
		super(id, enabled);
		this.weight = weight;
		this.priority = priority;
		this.distribution = distribution;
		this.enablingTime = enablingTime;
	}
	
	@XmlType
	@XmlEnum(String.class)
	public static enum CDFFunction{
	    @XmlEnumValue("normal") normal,
	    @XmlEnumValue("foldednormal") foldednormal,
	}
	
	public Double getWeight() {
		return weight;
	}	
	@XmlAttribute(name = "weight")
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Integer getPriority() {
		return priority;
	}	
	@XmlAttribute(name = "priority")
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public CDFFunction getDistribution() {
		return distribution;
	}
	
	@XmlAttribute(name = "cdf")
	public void setDistribution(CDFFunction distribution) {
		this.distribution = distribution;
	}
	public Double getEnablingTime() {
		return enablingTime;
	}
	public void setEnablingTime(Double enablingTime) {
		this.enablingTime = enablingTime;
	}
	
	public ArrayList<CDFFunctionParameter> getParameters() {
		return parameters;
	}

	/*public void setParameters(ArrayList<CDFFunctionParameter> parameters) {
		this.parameters = parameters;
	}*/
	
	private CDFFunction distribution;	
	private Double weight;
	private Integer priority;
	private Double enablingTime;
	//private Boolean oneShot = true;
	//private Boolean hasFired = false;
	
	@XmlElements({
	    @XmlElement(name="parameter", type=CDFFunctionParameter.class),
	})
	private ArrayList <CDFFunctionParameter> parameters = new ArrayList<CDFFunctionParameter>();
}
