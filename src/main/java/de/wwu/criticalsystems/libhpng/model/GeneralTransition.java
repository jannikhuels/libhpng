package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import umontreal.ssj.randvar.RandomVariateGen;

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
	
	public GeneralTransition(GeneralTransition transitionToCopy) {
		super(new String(transitionToCopy.getId()), new Boolean (transitionToCopy.getEnabled()));
		this.weight = new Double (transitionToCopy.getWeight());
		this.priority = new Integer (transitionToCopy.getPriority());
		this.distribution = transitionToCopy.getDistribution();
		this.policy = transitionToCopy.getPolicy();
		this.enablingTime = new Double(transitionToCopy.getEnablingTime());
		if (transitionToCopy.getDiscreteFiringTime() != null)
			this.discreteFiringTime = new Double(transitionToCopy.getDiscreteFiringTime());
		this.firings = new Integer(transitionToCopy.getFirings());
				
		for(CDFFunctionParameter currentParameterToCopy : transitionToCopy.getParameters()) {
			this.parameters.add(new CDFFunctionParameter(currentParameterToCopy));
		}
					
	}

	@XmlType
	@XmlEnum(String.class)
	public static enum CDFFunction{
		@XmlEnumValue("uniform") uniform,
	    @XmlEnumValue("normal") normal,
	    @XmlEnumValue("foldednormal") foldednormal,
	    @XmlEnumValue("halfnormal") halfnormal,
	    @XmlEnumValue("inversenormal") inversenormal,
	    @XmlEnumValue("lognormal") lognormal,	    
	    @XmlEnumValue("beta") beta,
	    @XmlEnumValue("cauchy") cauchy,
	    @XmlEnumValue("chi") chi,
	    @XmlEnumValue("chisquare") chisquare,
	    @XmlEnumValue("exp") exp,
	    @XmlEnumValue("fisherf") fisherf,
	    @XmlEnumValue("frechet") frechet,
	    @XmlEnumValue("gamma") gamma,
	    @XmlEnumValue("gumbel") gumbel,
	    @XmlEnumValue("inversegamma") inversegamma,
	    @XmlEnumValue("laplace") laplace,
	    @XmlEnumValue("logistic") logistic,
	    @XmlEnumValue("loglogistic") loglogistic,
	    @XmlEnumValue("pareto") pareto,
	    @XmlEnumValue("rayleigh") rayleigh,
	    @XmlEnumValue("student") student,
		@XmlEnumValue("weibull") weibull;
	}
	
	@XmlType
	@XmlEnum(String.class)
	public static enum Policy{
		@XmlEnumValue("resume") resume,
	    @XmlEnumValue("repeatdifferent") repeatdifferent,
	    @XmlEnumValue("repeatidentical") repeatidentical,
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

	public Policy getPolicy() {
		return policy;
	}
	@XmlAttribute(name = "policy")
	public void setPolicy(Policy policy) {
		this.policy = policy;
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
	
	public Double getDiscreteFiringTime() {
		return discreteFiringTime;
	}

	public RandomVariateGen getRandomGenerator() {
		return randomGenerator;
	}
	@XmlTransient
	public void setRandomGenerator(RandomVariateGen randomGenerator) {
		this.randomGenerator = randomGenerator;
	}
	
	public Integer getFirings() {
		return firings;
	}

	private CDFFunction distribution;	
	private Double weight;
	private Integer priority;
	private Policy policy;
	private Double enablingTime;
	private Double discreteFiringTime;
	private RandomVariateGen randomGenerator;
	private Integer firings;
	
	@XmlElements({
	    @XmlElement(name="parameter", type=CDFFunctionParameter.class),
	})
	private ArrayList <CDFFunctionParameter> parameters = new ArrayList<CDFFunctionParameter>();
	
	
	public void setNewRandomFiringTime(Boolean reset) throws InvalidRandomVariateGeneratorException {
		
		if (randomGenerator != null){
			discreteFiringTime = randomGenerator.nextDouble();
			if (discreteFiringTime < 0.0)
				discreteFiringTime = 0.0;
		} else if (!reset)
			throw new InvalidRandomVariateGeneratorException("No random variate generator was found for the general transition with ID '" + this.getId() + "'");

	}
	

	public void increaseFirings() {
		this.firings++;
	}

	public void setFiringsToZero() {
		this.firings = 0;
	}

	public void enableByPolicy(Boolean reset) throws InvalidRandomVariateGeneratorException{
		
		switch (policy){
		case repeatdifferent:
			setNewRandomFiringTime(reset);
			enablingTime = 0.0;
			break;
		case repeatidentical:
			enablingTime = 0.0;
			break;
		default: //resume			
			break;
		}		
	}
}