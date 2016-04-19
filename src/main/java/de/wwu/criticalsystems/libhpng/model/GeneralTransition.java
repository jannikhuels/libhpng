package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "generalTransition" )
public class GeneralTransition extends Transition{

	public GeneralTransition(String id, Boolean enabled,
			CDFFunctionType distributionFunction, Double enablingtime) {
		super(id, enabled);
		this.distributionFunction = distributionFunction;
		this.enablingtime = enablingtime;
	}
	
	public CDFFunctionType getDistributionFunction() {
		return distributionFunction;
	}
	public void setDistributionFunction(CDFFunctionType distributionFunction) {
		this.distributionFunction = distributionFunction;
	}
	public Double getEnablingtime() {
		return enablingtime;
	}
	public void setEnablingtime(Double enablingtime) {
		this.enablingtime = enablingtime;
	}
	
	private CDFFunctionType distributionFunction;
	private Double enablingtime;
}
