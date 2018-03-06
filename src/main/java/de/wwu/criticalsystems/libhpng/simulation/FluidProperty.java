package de.wwu.criticalsystems.libhpng.simulation;

import java.util.logging.Logger;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;


public class FluidProperty {

	public FluidProperty(ContinuousPlace place, Double boundary, Double propertyTime) {

		this.place = place;
		this.boundary = boundary;
		this.propertyTime = propertyTime;
	}
	
	
	
	public FluidProperty(SimpleNode root, Logger logger, HPnGModel model, Double propertyTime, Boolean until, Double untilTime) throws InvalidPropertyException{
		
		this.propertyTime = propertyTime;
		this.until = until;
		this.untilTime = untilTime;
		String id = null;	
		GuardArc guardArc = null;
		
		String type = root.toString();
		
		//get ID of referenced place or transition, for guard arc condition get place ID for corresponding place
		String temp = PropertyChecker.getPropertyID(root, logger);
		if (type.equals("ATOMIC_ARC")){			
			for (Arc arc : model.getArcs()){
				if (arc.getId().equals(temp) && arc.getClass().equals(GuardArc.class)){
					guardArc = (GuardArc)arc;
					id = arc.getConnectedPlace().getId();
					break;
				}
			}
		} else
			id = temp;	
		

		for (Place p: model.getPlaces()){				
			if (p.getClass().equals(ContinuousPlace.class) && p.getId().equals(id))
				 place = (ContinuousPlace)p;	
		}
		if (place.equals(null)){
			if (logger != null)
				logger.severe("Property Error: the ID of the property node '" + root.toString() + "' could not be matched");
			throw new InvalidPropertyException("Property Error: the ID of the property node '" + root.toString() + "' could not be matched");
		}
		
				
		boundary = PropertyChecker.getPropertyBoundary(root, type, logger);
		
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
	
	
	
	
	
	
	
	public ContinuousPlace getPlace() {
		return place;
	}
	public void setPlace(ContinuousPlace place) {
		this.place = place;
	}


	public Double getBoundary() {
		return boundary;
	}
	public void setBoundary(Double boundary) {
		this.boundary = boundary;
	}


	public Double getPropertyTime() {
		return propertyTime;
	}
	public void setPropertyTime(Double propertyTime) {
		this.propertyTime = propertyTime;
	}


	public Boolean getUntil() {
		return until;
	}
	public void setUntil(Boolean until) {
		this.until = until;
	}


	public Double getUntilTime() {
		return untilTime;
	}
	public void setUntilTime(Double untilTime) {
		this.untilTime = untilTime;
	}


	private ContinuousPlace place = null;
	private Double boundary;
	private Double propertyTime;
	private Boolean until = false;
	private Double untilTime;

}
