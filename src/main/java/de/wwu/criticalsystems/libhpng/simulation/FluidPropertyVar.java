package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;

import java.util.logging.Logger;

public class FluidPropertyVar {


	public FluidPropertyVar(ContinuousPlaceVar place, Double boundary, Double propertyTime) {

		this.place = place;
		this.boundary = boundary;
		this.propertyTime = propertyTime;
	}

	public FluidPropertyVar(SimpleNode root, Logger logger, HPnGModelVar model, Double propertyTime, Boolean until, Double untilTime) throws InvalidPropertyException{
		
		this.propertyTime = propertyTime;
		this.until = until;
		this.untilTime = untilTime;
		String id = null;	
		GuardArcVar guardArc = null;
		
		String type = root.toString();
		
		//get ID of referenced place or transition, for guard arc condition get place ID for corresponding place
		String temp = PropertyCheckerVar.getPropertyID(root, logger);
		if (type.equals("ATOMIC_ARC")){			
			for (Arc arc : model.getArcs()){
				if (arc.getId().equals(temp) && arc.getClass().equals(GuardArcVar.class)){
					guardArc = (GuardArcVar) arc;
					id = arc.getConnectedPlace().getId();
					break;
				}
			}
		} else
			id = temp;	
		

		for (Place p: model.getPlaces()){				
			if (p.getClass().equals(ContinuousPlaceVar.class) && p.getId().equals(id))
				 place = (ContinuousPlaceVar)p;
		}
		if (place.equals(null)){
			if (logger != null)
				logger.severe("Property Error: the ID of the property node '" + root.toString() + "' could not be matched");
			throw new InvalidPropertyException("Property Error: the ID of the property node '" + root.toString() + "' could not be matched");
		}		
				
		boundary = PropertyCheckerVar.getPropertyBoundary(root, type, logger);
		
		//set boundaries for upper boundary, lower boundary and guard arc case
		switch (type){
		case "ATOMIC_UBOUND":
			if (place.getUpperBoundaryInfinity()){
				boundary = Double.POSITIVE_INFINITY;
				break;
			}
			boundary = (place.getUpperBoundary());		
			if (boundary.equals(null)){
				if (logger != null)
					logger.severe("Property Error: the boundary of the property node '" + root.toString() + "' could not be identified");
				throw new InvalidPropertyException("Property Error: the boundary of the property node '" + root.toString() + "' could not be identified");
			}
			break;
		case "ATOMIC_LBOUND":
			boundary = 0.0;
			break;
		case "ATOMIC_ARC":
			boundary = guardArc.getWeight();
			break;		
		default:
			break;
		}
		
		
	}
	
		
	public ContinuousPlaceVar getPlace() {
		return place;
	}

	public Double getBoundary() {
		return boundary;
	}

	public Double getPropertyTime() {
		return propertyTime;
	}

	public Boolean getUntil() {
		return until;
	}

	public Double getUntilTime() {
		return untilTime;
	}

	
	private ContinuousPlaceVar place = null;
	private Double boundary;
	private Double propertyTime;
	private Boolean until = false;
	private Double untilTime;

}
