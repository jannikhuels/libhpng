package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

@XmlRootElement( name = "dynamicContinuousTransition" )
public class DynamicContinuousTransition extends Transition{
	
	public DynamicContinuousTransition(){}

	public DynamicContinuousTransition(String id, Boolean enabled, ArrayList<DynamicContinuousDependency> dependencies) {
		super(id, enabled);
		//this.dependencies = dependencies;
	}
	
	public DynamicContinuousTransition(DynamicContinuousTransition transitionToCopy) throws ModelCopyingFailedException {
		super(new String (transitionToCopy.getId()), new Boolean (transitionToCopy.getEnabled()));
		this.currentFluid = new Double(transitionToCopy.getCurrentFluid());
		this.currentChangeOfFluid = new Double (transitionToCopy.getCurrentChangeOfFluid());
		this.adapted = new Boolean(transitionToCopy.getAdapted());
		
		//for(DynamicContinuousDependency currentDependencyToCopy : transitionToCopy.getDependencies()) {
		//	this.dependencies.add(new DynamicContinuousDependency(currentDependencyToCopy, transitions));
		//}
	}

	//public ArrayList<DynamicContinuousDependency> getDependencies() {
	//	return dependencies;
	//}
	
	public Double getCurrentFluid() {
		return currentFluid;
	}
	public void setCurrentFluid(Double currentFluid) {
		this.currentFluid = currentFluid;
	}
	
	public Double getCurrentChangeOfFluid() {
		return currentChangeOfFluid;
	}
	public void setCurrentChangeOfFluid(Double currentChangeOfFluid) {
		this.currentChangeOfFluid = currentChangeOfFluid;
	}

	public Boolean getAdapted() {
		return adapted;
	}
	public void setAdapted(Boolean adapted) {
		this.adapted = adapted;
	}
	
	
	
	
	
	//TODO

	public void computeCurrentFluidAndCurrentChangeOfFluid(ArrayList<Place> places){		
			
			//search for place id
		
		ContinuousPlace a = null;
		ContinuousPlace b = null;
		
		Double c = 0.5;	
		
		Double p = 0.01;
		
		//Double totalCapacity = 10000.0;
		//a =  c * totalCapacity;
		//b = (1.0-c) * totalCapacity;
		//Double i = 400.0;
	
	    for(Place place: places){
	    	
	        if(place.getClass().equals(ContinuousPlace.class) && place.getId().equals("a")){
	        	//currentFluid =  0.1 * ((ContinuousPlace)place).getExactFluidLevel();
	        	//currentChangeOfFluid = 0.1 * ((ContinuousPlace)place).getExactDrift();
	        	//break;
	        	a = (ContinuousPlace) place;
	        }
	        
	        if(place.getClass().equals(ContinuousPlace.class) && place.getId().equals("b")){
	        	b = (ContinuousPlace) place;
	        }
	    }
	    
	    if (this.getId().equals("out") && b!= null){
	    	currentFluid =  p/(1.0-c) * b.getExactFluidLevel();
	    	currentChangeOfFluid = p/(1.0-c) * b.getExactDrift();
	    }
	    
	    if (this.getId().equals("in") && a!= null){
	    	currentFluid =  p/c * a.getExactFluidLevel();
	    	currentChangeOfFluid = p/c * a.getExactDrift();
	    }

	}
	
	



/*//Differential equation
	private Double computeU(Double[] q, Byte a, Double i, Double k, Double c){
		
		if (a==0)
			return -1.0*i + k*(q[1]/c - q [0]/c);
		else
			return -1.0*k*(q[1]/c - q[0]/c);		
	}
	
	
	
	//Differential equation
	private Double computeMU(Double[] mq, Byte a, Double k, Double c){
		
		if (a==0)
			return  k*(mq[1]/c - mq [0]/c);
		else
			return -1.0*k*(mq[1]/c - mq[0]/c);		
	}
	
	*/


	//@XmlElements({
	//    @XmlElement(name="pid", type=DynamicContinuousDependency.class),
	//})
	//private ArrayList <DynamicContinuousDependency> dependencies = new ArrayList<DynamicContinuousDependency>();
	
	
	private Double currentFluid;
	private Double currentChangeOfFluid;
	private Boolean adapted = false;
		
	
}
