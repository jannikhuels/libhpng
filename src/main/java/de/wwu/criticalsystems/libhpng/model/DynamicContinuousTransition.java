package de.wwu.criticalsystems.libhpng.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.xml.bind.annotation.*;
import org.mariuszgromada.math.mxparser.*;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

@XmlRootElement( name = "dynamicContinuousTransition" )
public class DynamicContinuousTransition extends Transition{
	
	public DynamicContinuousTransition(){}

	public DynamicContinuousTransition(String id, Boolean enabled, Expression fluidExpression, Expression changeOfFluidExpressionString, ArrayList<DynamicContinuousDependency> dependencies) {
		super(id, enabled);
		this.fluidExpression = new Expression(fluidExpression.getExpressionString());
		this.changeOfFluidExpression = new Expression(changeOfFluidExpressionString.getExpressionString());		
		this.dependencies = dependencies;
	}
	
	public DynamicContinuousTransition(DynamicContinuousTransition transitionToCopy, ArrayList<Transition> transitions) throws ModelCopyingFailedException {
		super(new String (transitionToCopy.getId()), new Boolean (transitionToCopy.getEnabled()));
		this.currentFluid = new Double(transitionToCopy.getCurrentFluid());
		this.currentChangeOfFluid = new Double (transitionToCopy.getCurrentChangeOfFluid());
		this.adapted = new Boolean(transitionToCopy.getAdapted());
		this.fluidExpression = new Expression(transitionToCopy.getFluidExpression().getExpressionString());
		this.changeOfFluidExpression = new Expression(transitionToCopy.getChangeOfFluidExpression().getExpressionString());
		
		for(DynamicContinuousDependency currentDependencyToCopy : transitionToCopy.getDependencies()) {
			this.dependencies.add(new DynamicContinuousDependency(currentDependencyToCopy, transitions));
		}
	}

	
	@XmlAttribute (name = "rateFunction")
	public void setRateFunction(String rateFunction) {
		this.fluidExpression = new Expression(rateFunction);
	}

	@XmlAttribute (name = "changeOfRateFunction")
	public void setChangeOfRateFunction(String changeOfRateFunction) {
		this.changeOfFluidExpression = new Expression(changeOfRateFunction);
	}
	
	public Expression getFluidExpression() {
		return fluidExpression;
	}
	public void setFluidExpression(Expression fluidExpression) {
		this.fluidExpression = fluidExpression;
	}

	public Expression getChangeOfFluidExpression() {
		return changeOfFluidExpression;
	}
	public void setChangeOfFluidExpression(Expression changeOfFluidExpression) {
		this.changeOfFluidExpression = changeOfFluidExpression;
	}	

	public ArrayList<DynamicContinuousDependency> getDependencies() {
		return dependencies;
	}
	
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

	@XmlElements({
	    @XmlElement(name="pid", type=DynamicContinuousDependency.class),
	})
	private ArrayList <DynamicContinuousDependency> dependencies = new ArrayList<DynamicContinuousDependency>();	
	private Double currentFluid;
	private Double currentChangeOfFluid;
	private Boolean adapted = false;
	private Expression fluidExpression;
	private Expression changeOfFluidExpression;
	private HashMap<String, ContinuousPlace> placesForFluidExpression = new HashMap<String, ContinuousPlace>();
	private HashMap<String, ContinuousPlace> placesForChangeOfFluidExpression = new HashMap<String, ContinuousPlace>();
	private Integer numberOfEntries = null;
	

	public void computeCurrentFluidAndCurrentChangeOfFluid(ArrayList<Place> places){			
		
		
		currentFluid = 0.0;
		currentChangeOfFluid = 0.0;
		
		if (fluidExpression != null){
			
			if(numberOfEntries == null)
				createHashMapsAndArguments(places);

			if (numberOfEntries > 0){
				
				Entry<String, ContinuousPlace> pair = null;
				
			    Iterator<Entry<String, ContinuousPlace>> it = placesForFluidExpression.entrySet().iterator();		    
			    while (it.hasNext()) {
			    	pair = (Entry<String, ContinuousPlace>)it.next();
					fluidExpression.setArgumentValue(pair.getKey(), getArgumentValue(pair.getKey(),pair.getValue()));	       
			    }
				   
			    it = placesForChangeOfFluidExpression.entrySet().iterator();		    
			    while (it.hasNext()) {
			    	pair = (Entry<String, ContinuousPlace>)it.next();
			    	changeOfFluidExpression.setArgumentValue(pair.getKey(), getArgumentValue(pair.getKey(),pair.getValue()));
			    }
			    
			    currentFluid = fluidExpression.calculate();
			    currentChangeOfFluid = changeOfFluidExpression.calculate();
			}
		}
	    	    

		for (int i = 0; i < dependencies.size(); i++){
			if (dependencies.get(i).getTransition().getEnabled()){
				currentFluid+= (dependencies.get(i).getTransition().getCurrentFluid() * (dependencies.get(i).getCoefficient()));						
			}
		}	
	}
	
	
	private void createHashMapsAndArguments(ArrayList<Place> places){
		
		fluidExpression.removeAllArguments();
		String[] missing = fluidExpression.getMissingUserDefinedArguments();
		numberOfEntries = 0;
		
		for (int i=0;i < missing.length; i++){
		
		    for(Place place: places){
		    	
		        if(place.getClass().equals(ContinuousPlace.class) && (place.getId().equals(missing[i]) || ("delta_" + place.getId()).equals(missing[i]))){
		        	
		        	if (placesForFluidExpression.get(missing[i]) == null){
		        		placesForFluidExpression.put(missing[i], (ContinuousPlace) place);		
		        		numberOfEntries++;
		        	}

		        	fluidExpression.addArguments(new Argument (missing[i], getArgumentValue(missing[i],(ContinuousPlace) place)));
		        }	        
		    }
		}	
		
		 	  
		changeOfFluidExpression.removeAllArguments();
		missing = changeOfFluidExpression.getMissingUserDefinedArguments();

		for (int i=0;i < missing.length; i++){
		
		    for(Place place: places){
		    	
		        if(place.getClass().equals(ContinuousPlace.class) && (place.getId().equals(missing[i]) || ("delta_" + place.getId()).equals(missing[i]))){
		        	
		        	if (placesForChangeOfFluidExpression.get(missing[i]) == null){
		        		placesForChangeOfFluidExpression.put(missing[i], (ContinuousPlace) place);		
		        		numberOfEntries++;
		        	}

		        	changeOfFluidExpression.addArguments(new Argument (missing[i], getArgumentValue(missing[i],(ContinuousPlace) place)));
		        }	        
		    }
		}	
		
	}
		
	
	private Double getArgumentValue(String argument, ContinuousPlace p){
    	
    	if (("delta_" + p.getId()).equals(argument))
    		return p.getDrift(); 
    	else 
    		return p.getCurrentFluidLevel();	
	}
}
