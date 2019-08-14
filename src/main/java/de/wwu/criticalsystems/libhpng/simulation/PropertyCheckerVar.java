package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class PropertyCheckerVar {


	public PropertyCheckerVar(SimpleNode root, HPnGModelVar model, Double time) throws InvalidPropertyException{

		this.model = model;
		this.time = time;		
		propertyRoot = root;
	}
	


	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	
	private static enum PropertyFamily {discrete, continuous, undefined}
	private HPnGModelVar model;
	private MarkingPlotVar plot;
	private Logger logger;
	private Double time = 0.0;
	private SimpleNode propertyRoot;
	
	
	public static Double getMaxTimeForSimulation(SimpleNode propertyRoot, Double time) throws InvalidPropertyException{
		
		Double maxTime = time;
		Double untilTime = checkForUntil(propertyRoot, maxTime);
		
		return untilTime;
	}	
	
		
	
	public static Double getPropertyBoundary(SimpleNode atomic, String type, Logger logger) throws InvalidPropertyException {
		
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("DOUBLE"))
				return Double.parseDouble(((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString());
			else if 
				(atomic.jjtGetChild(i).toString().equals("INTEGER")){
				Integer value = Integer.parseInt(((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString());
				return value.doubleValue();
				}
		}
		if (!type.equals("ATOMIC_UBOUND") && !type.equals("ATOMIC_LBOUND") && !type.equals("ATOMIC_ARC") && !type.equals("ATOMIC_ENABLED")){
			if (logger != null) 
				logger.severe("Property Error: the boundary of the property node '" + atomic.toString() + "' could not be identified");
		throw new InvalidPropertyException("Property Error: the boundary of the property node '" + atomic.toString() + "' could not be identified");
		}
		return 0.0;
	}
		
	public static String getPropertyID(SimpleNode atomic, Logger logger) throws InvalidPropertyException{
		
		String id;
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("ID")){
				id = ((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString();
				id = id.substring(1, id.length() - 1);
				return id;
			}
		}
		if (logger != null)
			logger.severe("Property Error: the ID the property node '" + atomic.toString() + "' could not be identified");
		throw new InvalidPropertyException("Property Error: the ID the property node '" + atomic.toString() + "' could not be identified");
	}	
	
	public static ArrayList<FluidPropertyVar> getFluidProperties(SimpleNode root, ArrayList<FluidPropertyVar> fluidProperties, Logger myLogger, HPnGModelVar myModel, Double propertyTime, Double untilTime, Boolean until) throws InvalidPropertyException{
		
		FluidPropertyVar f;
		
		if (root.toString().equals("ROOT"))
			root = ((SimpleNode)((SimpleNode)root.jjtGetChild(1)).jjtGetChild(0));
		
		
		if (root.toString().equals("ATOMIC_FLUID") || root.toString().equals("ATOMIC_UBOUND") || root.toString().equals("ATOMIC_LBOUND") || root.toString().equals("ATOMIC_ARC")){
			f = new FluidPropertyVar(root, myLogger, myModel, propertyTime, until, untilTime);
			fluidProperties.add(f);			
			return fluidProperties;
		} 
		
		
		if (root.toString().equals("UNTIL")){		
			untilTime = untilTime + Double.parseDouble(((SimpleNode)root.jjtGetChild(1)).jjtGetValue().toString());
			until = true;
		}	
	
			
		if (root.jjtGetNumChildren() > 0) {
			for (int i=0;i< root.jjtGetNumChildren();i++)
				fluidProperties = getFluidProperties(((SimpleNode)root.jjtGetChild(i)), fluidProperties, myLogger, myModel, propertyTime, untilTime, until);	
		}
		
		return fluidProperties;
	}
	

	
	
	public Boolean checkProperty(MarkingPlotVar plot) throws InvalidPropertyException{
		this.plot = plot;	
		
		return checkAnyProperty(propertyRoot, time);	
	}
	
	public ArrayList<String> getAllRelatedPlaceAndTransitionIds() throws InvalidPropertyException{
		
		ArrayList<String> related = new ArrayList<String>();
		addIdsRecursive(propertyRoot, related);
		return related;
	}
		
	private void addIdsRecursive(SimpleNode root, ArrayList<String> related) throws InvalidPropertyException{
		
		String id = null;	
		String type = root.toString();
		
		if (type.startsWith("ATOMIC_")){			
			
			String temp = getPropertyID(root);
			
			if (type.equals("ATOMIC_ARC")){			
				for (Arc arc : model.getArcs()){
					if (arc.getId().equals(temp) && arc.getClass().equals(GuardArcVar.class)){
						id = arc.getConnectedPlace().getId();
						break;
					}
				}
			} else
				id = temp;
			
			if(!related.contains(id))
				related.add(id);
			
			if (id == null){
				if (logger != null)
					logger.severe("Property Error: the ID of the property node '" + propertyRoot.toString() + "' could not be matched");
				throw new InvalidPropertyException("Property Error: the ID of the property node '" + propertyRoot.toString() + "' could not be matched");
			}
		}else {
			switch (type){
			case "TRUE":
				break;
			case "NOT":
				addIdsRecursive((SimpleNode)root.jjtGetChild(0), related);
				break;
			case "AND":
				addIdsRecursive((SimpleNode)root.jjtGetChild(0), related);
				addIdsRecursive((SimpleNode)root.jjtGetChild(1), related);
				break;
			case "OR":
				addIdsRecursive((SimpleNode)root.jjtGetChild(0), related);
				addIdsRecursive((SimpleNode)root.jjtGetChild(1), related);
				break;
			case "UNTIL":
				addIdsRecursive((SimpleNode)root.jjtGetChild(2), related);
				addIdsRecursive((SimpleNode)root.jjtGetChild(3), related);
				break;
			 default: 
				if (logger != null)
					logger.severe("Property Error: the property node '" + propertyRoot.toString() + "' could not be identified");
				throw new InvalidPropertyException("Property Error: the property node '" + propertyRoot.toString() + "' could not be identified");
			}
		}
	}
	
		
	//recursive method for checking property
	private Boolean checkAnyProperty(SimpleNode propertyRoot, Double time) throws InvalidPropertyException{
		
		if (propertyRoot.toString().startsWith("ATOMIC_"))
			return checkAtomicProperty(propertyRoot, time);
			
		switch (propertyRoot.toString()){
			case "TRUE":
				return true;
			case "NOT":
				return !checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time);
			case "AND":
				return (checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time) && checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(1), time));
			case "OR":
				return (checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time) || checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(1), time));
			case "UNTIL":
				return (checkUntilProperty(propertyRoot, time) > -1.0);
		}
		
		if (logger != null)
			logger.severe("Property Error: the property node '" + propertyRoot.toString() + "' could not be identified");
		throw new InvalidPropertyException("Property Error: the property node '" + propertyRoot.toString() + "' could not be identified");
	}
	
	private Boolean checkAtomicProperty(SimpleNode propertyRoot, Double time) throws InvalidPropertyException {
		
		Double value;
		String id = null;	
		GuardArcVar guardArc = null;
		
		String type = propertyRoot.toString();
		
		//get ID of referenced place or transition, for guard arc condition get place ID for corresponding place
		String temp = getPropertyID(propertyRoot);
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
		
		if (id.equals(null)){
			if (logger != null)
				logger.severe("Property Error: the ID of the property node '" + propertyRoot.toString() + "' could not be matched");
			throw new InvalidPropertyException("Property Error: the ID of the property node '" + propertyRoot.toString() + "' could not be matched");
		}
		
		PlotEntry currentEntry = getCurrentEntry(time, type, id);		
		Double boundary = getPropertyBoundary(propertyRoot, type);
		String compare = getPropertyCompare(propertyRoot, type);
		
		//set boundaries for upper boundary, lower boundary and guard arc case
		switch (type){
		case "ATOMIC_UBOUND":
			for (Place place : model.getPlaces()){				
				if (place.getId().equals(id) && place.getClass().equals(ContinuousPlace.class)){
					if (((ContinuousPlace)place).getUpperBoundaryInfinity())
						return false;					
					boundary = ((ContinuousPlace)place).getUpperBoundary();		
					compare = ">=";
				}					
			}
			if (boundary.equals(null)){
				if (logger != null)
					logger.severe("Property Error: the boundary of the property node '" + propertyRoot.toString() + "' could not be identified");
				throw new InvalidPropertyException("Property Error: the boundary of the property node '" + propertyRoot.toString() + "' could not be identified");
			}
			break;
		case "ATOMIC_LBOUND":
			boundary = 0.0;
			compare = "<=";
			break;
		case "ATOMIC_ARC":
			boundary = guardArc.getWeight();
			compare = ">=";
			break;		
		default:
			break;
		}
		
		//compare values depending on case
		switch (type){
			case "ATOMIC_FLUID":
			case "ATOMIC_UBOUND":
			case "ATOMIC_LBOUND":
			case "ATOMIC_ARC":
				if (currentEntry.getClass().equals(ContinuousPlaceEntry.class)) {
					value = ((ContinuousPlaceEntry)currentEntry).getFluidLevel();
					if (currentEntry.getTime() < time)
						value = Math.max(0.0, value + ((ContinuousPlaceEntry)currentEntry).getDrift()*(time - currentEntry.getTime()));				
					return (compareValues(value, boundary, compare));
				}
				break;
			case "ATOMIC_TOKENS":
				if (currentEntry.getClass().equals(DiscretePlaceEntry.class)){ 
					value = ((DiscretePlaceEntry)currentEntry).getNumberOfTokens().doubleValue();
					return (compareValues(value, boundary, compare));
				}
				break;
			case "ATOMIC_DRIFT":
				if (currentEntry.getClass().equals(ContinuousPlaceEntry.class)) {
					value = ((ContinuousPlaceEntry)currentEntry).getDrift();
					return (compareValues(value, boundary, compare));
				}
				break;
			case "ATOMIC_ENABLED":
				if (currentEntry.getClass().equals(TransitionEntry.class)) 
					return ((TransitionEntry)currentEntry).getEnabled();
				break;
			case "ATOMIC_CLOCK":
				if (currentEntry.getClass().equals(DeterministicTransitionEntry.class)) {
					value = ((DeterministicTransitionEntry)currentEntry).getClock();
					if (currentEntry.getTime() < time)
						value = value + (time - currentEntry.getTime());			
					return (compareValues(value, boundary, compare));
				}
				break;
			case "ATOMIC_ENABLINGTIME":
				if (currentEntry.getClass().equals(GeneralTransitionEntry.class)) {
					value = ((GeneralTransitionEntry)currentEntry).getEnablingTime();
					if (currentEntry.getTime() < time)
						value = value + (time - currentEntry.getTime());
					return (compareValues(value, boundary, compare));
				}
				break;
		}
		
		if (logger != null)
			logger.severe("Property Error: the property '" + propertyRoot.toString() + "' could not be identified");
		throw new InvalidPropertyException("Property Error: the property '" + propertyRoot.toString() + "' could not be identified");
	
	}	
	
	private Double checkUntilProperty(SimpleNode propertyRoot, Double time) throws InvalidPropertyException {
		
		Double t1 = time + Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(0)).jjtGetValue().toString());
		Double t2 = time + Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(1)).jjtGetValue().toString());
		if (t1 > t2){
			if (logger != null)
				logger.severe("Property Error: the second border is smaller than the first border for the Until property node '" + propertyRoot.toString() + "'");
			throw new InvalidPropertyException("Property Error: the second border is smaller than the first border for the Until property node '" + propertyRoot.toString() + "'");
		}
		
		SimpleNode phi1 = (SimpleNode)propertyRoot.jjtGetChild(2);
		SimpleNode phi2 = (SimpleNode)propertyRoot.jjtGetChild(3);		
		PropertyFamily phi1Family = checkPropertyFamily(phi1);
		PropertyFamily phi2Family = checkPropertyFamily(phi2);
		
		if (phi1Family.equals(PropertyFamily.undefined) || phi1Family.equals(PropertyFamily.undefined)){
			if (logger != null)
				logger.severe("Property Error: the type (discrete or continuous) of the property node '" + propertyRoot.toString() + "' could not be identified");
			throw new InvalidPropertyException("Property Error: the type (discrete or continuous) of the property node '" + propertyRoot.toString() + "' could not be identified");
		}
				
		//check t = time	
		if (!checkAnyProperty(phi1, time))
			return -1.0;		
		
		//check interval [time, t1)
		Double currentEventTime = plot.getNextEventTime(time);
		Double previousEventTime = time;
		while (currentEventTime < t1){		
			if (phi1Family.equals(PropertyFamily.discrete)){
				if (!checkAnyProperty(phi1, currentEventTime))
					return -1.0;
			} else {
				if (findTForInvalidProperty(previousEventTime, currentEventTime, false, true, phi1) != -1.0)
					return -1.0;
			}
			previousEventTime = currentEventTime;
			currentEventTime = plot.getNextEventTime(currentEventTime);
		}
		
		//if t1 fulfills phi2, until is already fulfilled
		if (plot.eventAtTime(t1) && checkAnyProperty(phi2, t1))
			return t1;
		
		//check interval [t1, t2]
		currentEventTime = plot.getNextEventTime(t1);
		previousEventTime = t1;
		
		while (currentEventTime <= t2){		
			
			if (phi2Family.equals(PropertyFamily.discrete) && phi1Family.equals(PropertyFamily.discrete)){
			//both discrete
				
				if (checkAnyProperty(phi2, currentEventTime))
					return currentEventTime;
				if (!checkAnyProperty(phi1, currentEventTime))
					return -1.0;
				
			} else if (phi2Family.equals(PropertyFamily.discrete) && phi1Family.equals(PropertyFamily.continuous)){
			//phi1 continuous and phi2 discrete	
				
				Double tNotPhi1 = findTForInvalidProperty(previousEventTime, currentEventTime, false, false, phi1);				
				if (checkAnyProperty(phi2, currentEventTime) && (tNotPhi1 == -1.0 || tNotPhi1 >= currentEventTime))
					return currentEventTime;				
				if (!checkAnyProperty(phi1, currentEventTime) || (tNotPhi1 > -1.0 && tNotPhi1 < currentEventTime))
					return -1.0;
				
			} else {
			//phi2 is continuous
				
				//search in current interval for a time tphi2 where phi2 is fulfilled
				Double tPhi2 = findTForProperty(previousEventTime, currentEventTime, false, true, phi2);
				
				if (phi1Family.equals(PropertyFamily.discrete)){
				//phi2 discrete
					
					if (tPhi2 > -1.0)
						return tPhi2;					
					if (!checkAnyProperty(phi1, currentEventTime))
						return -1.0;
					
				} else {
				//both continuous
					
					if (tPhi2 > -1.0){
						if (findTForInvalidProperty(previousEventTime, tPhi2, false, false, phi1) != -1.0)
							return -1.0;
						return tPhi2;
					} else {
						if (findTForInvalidProperty(previousEventTime, currentEventTime, false, true, phi1) != -1.0)
							return -1.0;
					}
				}
			}
			previousEventTime = currentEventTime;
			if (currentEventTime >= t2)
				return -1.0;
			currentEventTime = plot.getNextEventTime(currentEventTime);
			if (currentEventTime > t2)
				currentEventTime = t2;
		}
	
		return -1.0;
	}
	
	private Double findTForProperty(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot) throws InvalidPropertyException{

		if (leftBorder + Double.MIN_VALUE >= rightBorder){
			if (leftBorderIncluded && checkAnyProperty(propertyRoot, leftBorder))
				return leftBorder;
			if (rightBorderIncluded && checkAnyProperty(propertyRoot, rightBorder))
				return rightBorder;
			return -1.0;
		}
				
		switch (propertyRoot.toString()){			
		
			case "TRUE":
				if (leftBorderIncluded)
					return leftBorder;
				return leftBorder + Double.MIN_VALUE;
						
			case "ATOMIC_FLUID":				
				return findTForAtomicFluid(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, false);
				
		   	case "ATOMIC_CLOCK":			
		   		return findTForAtomicClock(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, false);
			
		   	case "ATOMIC_ENABLINGTIME":			
		   		return findTForAtomicEnablingTime(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, false);
			
			case "ATOMIC_TOKENS":
			case "ATOMIC_ENABLED":
			case "ATOMIC_DRIFT":
			case "ATOMIC_UBOUND":
			case "ATOMIC_LBOUND":
			case "ATOMIC_ARC":
				if (checkAnyProperty(propertyRoot, leftBorder)){
					if (leftBorderIncluded)
						return leftBorder;
					return leftBorder + Double.MIN_VALUE;
				}
				if (rightBorderIncluded && checkAnyProperty(propertyRoot, rightBorder))
					return rightBorder;
				return -1.0;

			case "NOT":						
				return findTForInvalidProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0)));
				
			case "AND":
				Double tAnd1 = findTForProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0)));
				if (tAnd1 == -1.0)
					return -1.0;				
				Double tAnd1End = findTForInvalidProperty(tAnd1, rightBorder, false, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0)));
				if (tAnd1End == -1.0)
					return findTForProperty(tAnd1, rightBorder, true, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(1)));
				return findTForProperty(tAnd1, tAnd1End, true, false, ((SimpleNode)propertyRoot.jjtGetChild(1)));
				
			case "OR":
				Double tOr1 = findTForProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0))); 
				Double tOr2 = findTForProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(1)));
				if (tOr1 > -1.0 && tOr2 > -1.0)
					return Math.min(tOr1, tOr2);
				return Math.max(tOr1, tOr2);

			case "UNTIL":
				//attention: relative time bounds
				Double t1 = Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(0)).jjtGetValue().toString());
				Double t2 = Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(1)).jjtGetValue().toString());
				
				if (t1 > t2){
					if (logger != null)
						logger.severe("Property Error: the second border of the  is smaller than or equal to the first border for the Until property node '" + propertyRoot.toString() + "'");
					throw new InvalidPropertyException("Property Error: the second border of the  is smaller than or equal to the first border for the Until property node '" + propertyRoot.toString() + "'");
				}
				
				SimpleNode psi1 = (SimpleNode)propertyRoot.jjtGetChild(2);
				SimpleNode psi2 = (SimpleNode)propertyRoot.jjtGetChild(3);
				
				Double tPsi2 = findTForProperty(leftBorder + t1, rightBorder, leftBorderIncluded, false, psi2);
				
				if (tPsi2 > -1.0){
				//tPsi2 within [leftBorder, rightBorder)
					Double tPsi1 = findTForProperty(Math.min(leftBorder, tPsi2 - t2), tPsi2 - t1, true, true, psi1);
					
					//special case t1=0 and no tPsi1 found
					if (t1 == 0.0 && tPsi1 == -1.0){
						if (tPsi2.equals(leftBorder) && !leftBorderIncluded){
							if (checkAnyProperty(psi2, tPsi2 + Double.MIN_VALUE))
								return tPsi2 + Double.MIN_VALUE;
							return -1.0;
						}							
						return tPsi2;
					}
					
					if (tPsi1 > -1.0 && findTForInvalidProperty(tPsi1, tPsi2, true, false, psi1) == -1.0){
						if (tPsi1.equals(leftBorder) && !leftBorderIncluded){
							return tPsi1 + Double.MIN_VALUE;
						}	
						return tPsi1;
					}						
					return -1.0;
					
				} else {
				//tPsi2 not within [leftBorder, rightBorder]
					
					((SimpleNode)propertyRoot.jjtGetChild(0)).jjtSetValue(0.0);
					tPsi2 = checkUntilProperty(propertyRoot, rightBorder);
					((SimpleNode)propertyRoot.jjtGetChild(0)).jjtSetValue(t1);
					
					if (tPsi2 > -1.0){
						Double tPsi1 = findTForProperty(Math.max(leftBorder, tPsi2 - t1), rightBorder, true, true, psi1);
						
						//special case t1=0 and no tPsi1 found
						if (t1 == 0.0 && tPsi1 == -1.0){
							if (rightBorderIncluded)
								return rightBorder;
							return -1.0;	
						}
						
						if (tPsi1 > -1.0 && findTForInvalidProperty(tPsi1, rightBorder, true, false, psi1) == -1.0){													
							if (tPsi1.equals(leftBorder) && !leftBorderIncluded)
								return tPsi1 + Double.MIN_VALUE;	
							return tPsi1;
						}						
						return -1.0;
					}
				}				
		}		
		return -1.0;
	}
			
	private Double findTForInvalidProperty(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot) throws InvalidPropertyException{

		if (leftBorder + Double.MIN_VALUE >= rightBorder){
			if (leftBorderIncluded && !checkAnyProperty(propertyRoot, leftBorder))
				return leftBorder;
			if (rightBorderIncluded && !checkAnyProperty(propertyRoot, rightBorder))
				return rightBorder;
			return -1.0;
		}
		
		switch (propertyRoot.toString()){			
		
			case "TRUE":
				return -1.0;
		
			case "ATOMIC_FLUID":				
				return findTForAtomicFluid(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, true);
				
		   	case "ATOMIC_CLOCK":
		   		return findTForAtomicClock(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, true);
		   		
		   	case "ATOMIC_ENABLINGTIME":			
		   		return findTForAtomicEnablingTime(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, true);	
			
			case "ATOMIC_TOKENS":
			case "ATOMIC_ENABLED":
			case "ATOMIC_FIRINGS":
			case "ATOMIC_DRIFT":
			case "ATOMIC_UBOUND":
			case "ATOMIC_LBOUND":
			case "ATOMIC_ARC":
				if (leftBorderIncluded && !checkAnyProperty(propertyRoot, leftBorder))
					return leftBorder;
				if (rightBorderIncluded && !checkAnyProperty(propertyRoot, rightBorder))
					return rightBorder;
				return -1.0;
	
			case "NOT":						
				return findTForProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0)));
				
			case "AND":					
				Double tAnd1 = findTForInvalidProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0))); 
				Double tAnd2 = findTForInvalidProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(1)));
				if (tAnd1 > -1.0 && tAnd2 > -1.0)
					return Math.min(tAnd1, tAnd2);
				return Math.max(tAnd1, tAnd2);
				
			case "OR":
				Double tOr1 = findTForInvalidProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0)));
				if (tOr1 == -1.0)
					return -1.0;				
				Double tOr1End = findTForProperty(tOr1, rightBorder, false, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(0)));
				if (tOr1End == -1.0)
					return findTForInvalidProperty(tOr1, rightBorder, true, rightBorderIncluded, ((SimpleNode)propertyRoot.jjtGetChild(1)));
				return findTForInvalidProperty(tOr1, tOr1End, true, false, ((SimpleNode)propertyRoot.jjtGetChild(1)));
	
			case "UNTIL":
				//attention: relative time bounds
				Double t1 = Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(0)).jjtGetValue().toString());
				Double t2 = Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(1)).jjtGetValue().toString());
				
				if (t1 > t2){
					if (logger != null)
						logger.severe("Property Error: the second border of the  is smaller than or equal to the first border for the Until property node '" + propertyRoot.toString() + "'");
					throw new InvalidPropertyException("Property Error: the second border of the  is smaller than or equal to the first border for the Until property node '" + propertyRoot.toString() + "'");
				}
				
				SimpleNode psi1 = (SimpleNode)propertyRoot.jjtGetChild(2);
				SimpleNode psi2 = (SimpleNode)propertyRoot.jjtGetChild(3);
				
				Double tNotPsi1 = findTForInvalidProperty(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, psi1);
				
				if (tNotPsi1 > -1.0){
					
					Boolean notPsi2 = !checkAnyProperty(psi2, tNotPsi1);
					if (notPsi2 || leftBorder < tNotPsi1 - t2){
						if (!leftBorderIncluded){
							if (notPsi2 ||  leftBorder + Double.MIN_VALUE < tNotPsi1 - t2)
								return leftBorder + Double.MIN_VALUE;
						} else
							return leftBorder;					
					} 

					if (t1 > 0.0)
						return tNotPsi1 - t1 + Double.MIN_VALUE;
						
					//special case t1 = 0
					Double tNotPsi2 = findTForInvalidProperty(tNotPsi1, rightBorder, false, rightBorderIncluded, psi1);					
					return tNotPsi2;					 
					
				} else {
					//psi1 gilt die ganze zeit
					
					if (leftBorder < rightBorder - t2){
						if (!leftBorderIncluded)
							if (leftBorder + Double.MIN_VALUE < rightBorder - t2)
								return leftBorder + Double.MIN_VALUE;
						else
							return leftBorder;					
					} 
					
					((SimpleNode)propertyRoot.jjtGetChild(0)).jjtSetValue(0.0);
					Double tPsi2 = checkUntilProperty(propertyRoot, rightBorder);
					((SimpleNode)propertyRoot.jjtGetChild(0)).jjtSetValue(t1);
					
					if (tPsi2 == -1.0  || leftBorder < tPsi2 - t2){
						if (!leftBorderIncluded){
							if (tPsi2 == -1.0  || leftBorder + Double.MIN_VALUE < tPsi2 - t2)
								return leftBorder + Double.MIN_VALUE;
						} else
							return leftBorder;	
					}
					
					if (tPsi2 - rightBorder > t1){
						return (tPsi2 - t1) + Double.MIN_VALUE;
					}
					
					if (tPsi2 - rightBorder == t1){
						if (rightBorderIncluded)
							return rightBorder;
					}
				}				
		}	    
	    return -1.0;
	}
		
	private Double findTForAtomicFluid(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot, Boolean invalid) throws InvalidPropertyException{
		
		String type = propertyRoot.toString();
		String id = null;
		GuardArcVar guardArc = null;
		
		Double boundary = getPropertyBoundary(propertyRoot, "ATOMIC_FLUID");
		String compare = getPropertyCompare(propertyRoot, "ATOMIC_FLUID");	
		
		//get ID of referenced place or transition, for guard arc condition get place ID for corresponding place
		String temp = getPropertyID(propertyRoot);
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
				
		if (id.equals(null)){
			if (logger != null)
				logger.severe("Property Error: the ID of the property node '" + propertyRoot.toString() + "' could not be matched");
			throw new InvalidPropertyException("Property Error: the ID of the property node '" + propertyRoot.toString() + "' could not be matched");
		}
		
		//set boundaries for upper boundary, lower boundary and guard arc case
		switch (type){
		case "ATOMIC_UBOUND":
			for (Place place : model.getPlaces()){				
				if (place.getId().equals(id) && place.getClass().equals(ContinuousPlaceVar.class)){
					if (((ContinuousPlaceVar)place).getUpperBoundaryInfinity() && (compare.equals("<") || compare.equals("<="))){
						if (leftBorderIncluded) 
							return leftBorder;
						return leftBorder + Double.MIN_VALUE;
					} else if (((ContinuousPlaceVar)place).getUpperBoundaryInfinity())
						return -1.0;					
					boundary = ((ContinuousPlaceVar)place).getUpperBoundary();
					compare = ">=";
				}					
			}
			if (boundary.equals(null)){
				if (logger != null)
					logger.severe("Property Error: the boundary of the property node '" + propertyRoot.toString() + "' could not be identified");
				throw new InvalidPropertyException("Property Error: the boundary of the property node '" + propertyRoot.toString() + "' could not be identified");
			}
			break;
		case "ATOMIC_LBOUND":
			boundary = 0.0;
			compare = "<=";
			break;
		case "ATOMIC_ARC":
			boundary = guardArc.getWeight();
			compare = ">=";
			break;	
		default:
			break;
		}
		
	
		if (boundary < 0.0){
			if (logger != null)
				logger.severe("Property Error: the boundary for the atomic fluid property must be at least zero");
			throw new InvalidPropertyException("Property Error: the boundary for the atomic fluid property must be at least zero");
		}
		
    	PlacePlot placePlot = null, currentPlacePlot;			
		Iterator<Entry<String, PlacePlot>> placeIterator = plot.getPlacePlots().entrySet().iterator();
	    while (placeIterator.hasNext()) {
	    	currentPlacePlot = placeIterator.next().getValue();		
			if (currentPlacePlot.getReferencedPlace().getId().equals(id)){
				placePlot = currentPlacePlot;
				break;
			}
		}
	    if (!placePlot.getReferencedPlace().getClass().equals(ContinuousPlaceVar.class)){
	    	if (logger != null)
				logger.severe("Property Error: the ID of the place for the atomic fluid property must refer to a continuous place");
			throw new InvalidPropertyException("Property Error: the ID of the place for for the atomic fluid property must refer to a continuous place");
		}
	    

		if (!invalid && ((compare.equals("<") && boundary == 0.0) || (compare.equals(">") && !((ContinuousPlaceVar)placePlot.getReferencedPlace()).getUpperBoundaryInfinity() && boundary > ((ContinuousPlaceVar)placePlot.getReferencedPlace()).getUpperBoundary())))
			return -1.0;
		
		
		PlotEntry startEntry = placePlot.getNextEntryBeforeOrAtGivenTime(leftBorder);
		PlotEntry endEntry = placePlot.getNextEntryBeforeOrAtGivenTime(rightBorder);

	    Double startValue = ((ContinuousPlaceEntry)startEntry).getFluidLevel() + ((ContinuousPlaceEntry)startEntry).getDrift()*(leftBorder - startEntry.getTime());
	    Double endValue= ((ContinuousPlaceEntry)endEntry).getFluidLevel() + ((ContinuousPlaceEntry)endEntry).getDrift()*(rightBorder - endEntry.getTime());

	    
		Boolean leftBorderFulfills = compareValues(endValue, boundary, compare);
		Boolean rightBorderFulfills = compareValues(startValue, boundary, compare);
		
		if (leftBorderFulfills != invalid){
			if (leftBorderIncluded) 
				return leftBorder;
			return leftBorder + Double.MIN_VALUE;
		}		

		Double drift = ((ContinuousPlaceEntry)startEntry).getDrift();
		
		if (drift != 0.0){
				
			Double timeDelta = (boundary - startValue)/ drift;	
			
			if ((compare.contains("=") != invalid)){
				if (timeDelta > 0.0 && leftBorder + timeDelta < rightBorder)
					return leftBorder + timeDelta;
			} else {
				if (timeDelta >= 0.0 && leftBorder + timeDelta + Double.MIN_VALUE < rightBorder)
					return leftBorder + timeDelta + Double.MIN_VALUE;
			}
		}
		
		
			
		if (rightBorderIncluded && rightBorderFulfills != invalid)
   			return rightBorder;
	   	return -1.0;
	}
	
	private Double findTForAtomicClock(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot, Boolean invalid) throws InvalidPropertyException{
		
		Double boundary = getPropertyBoundary(propertyRoot, "ATOMIC_CLOCK");
		String compare = getPropertyCompare(propertyRoot, "ATOMIC_CLOCK");		
		if (boundary < 0.0){
			if (logger != null)
				logger.severe("Property Error: the boundary for the atomic clock property must be at least zero");
			throw new InvalidPropertyException("Property Error: the boundary for the atomic clock property must be at least zero");
		}
		
		TransitionPlot transitionPlot = null, currentTransitionPlot;					
		String id = getPropertyID(propertyRoot);		
		Iterator<Entry<String, TransitionPlot>> transitionIterator = plot.getTransitionPlots().entrySet().iterator();
	    while (transitionIterator.hasNext()) {
	    	currentTransitionPlot = transitionIterator.next().getValue();		
			if (currentTransitionPlot.getReferencedTransition().getId().equals(id)){
				transitionPlot = currentTransitionPlot;
				break;
			}
		}
	    if (!transitionPlot.getReferencedTransition().getClass().equals(DeterministicTransition.class)){
	    	if (logger != null)
				logger.severe("Property Error: the ID of the transition for the atomic clock property must refer to a deterministic transition");
			throw new InvalidPropertyException("Property Error: the ID of the transition for for the clock property must refer to a deterministic transition");
	    }
	    
	    if (!invalid && ((compare.equals("<") && boundary == 0.0)))
			return -1.0;
				
		PlotEntry startEntry = transitionPlot.getNextEntryBeforeOrAtGivenTime(leftBorder);
		PlotEntry endEntry = transitionPlot.getNextEntryBeforeOrAtGivenTime(rightBorder);
		Double startClock = ((DeterministicTransitionEntry)startEntry).getClock() + (leftBorder - startEntry.getTime());
	    Double endClock = ((DeterministicTransitionEntry)endEntry).getClock() + (rightBorder - endEntry.getTime());

		Boolean leftBorderFulfills = compareValues(endClock, boundary, compare);
		Boolean rightBorderFulfills = compareValues(startClock, boundary, compare);
		
		if (leftBorderFulfills != invalid){
			if (leftBorderIncluded) 
				return leftBorder;
			return leftBorder + Double.MIN_VALUE;
		}
		
		if (((DeterministicTransitionEntry)startEntry).getEnabled()){
							
			Double timeDelta = (boundary - startClock);		
			
			if ((compare.contains("=") != invalid)){
				if (timeDelta > 0.0 && leftBorder + timeDelta < rightBorder)
					return leftBorder + timeDelta;
			} else {
				if (timeDelta >= 0.0 && leftBorder + timeDelta + Double.MIN_VALUE < rightBorder)
					return leftBorder + timeDelta + Double.MIN_VALUE;
			}
		}
			
		if (rightBorderIncluded && rightBorderFulfills != invalid)
   			return rightBorder;
	   	return -1.0;
	}

	private Double findTForAtomicEnablingTime(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot, Boolean invalid) throws InvalidPropertyException{
		
		Double boundary = getPropertyBoundary(propertyRoot, "ATOMIC_ENABLINGTIME");
		String compare = getPropertyCompare(propertyRoot, "ATOMIC_ENABLINGTIME");		
		if (boundary < 0.0){
			if (logger != null)
				logger.severe("Property Error: the boundary for the atomic enabling time property must be at least zero");
			throw new InvalidPropertyException("Property Error: the boundary for the atomic enabling time property must be at least zero");
		}
		
		TransitionPlot transitionPlot = null, currentTransitionPlot;					
		String id = getPropertyID(propertyRoot);		
		Iterator<Entry<String, TransitionPlot>> transitionIterator = plot.getTransitionPlots().entrySet().iterator();
	    while (transitionIterator.hasNext()) {
	    	currentTransitionPlot = transitionIterator.next().getValue();		
			if (currentTransitionPlot.getReferencedTransition().getId().equals(id)){
				transitionPlot = currentTransitionPlot;
				break;
			}
		}
	    if (!transitionPlot.getReferencedTransition().getClass().equals(GeneralTransition.class)){
	    	if (logger != null)
				logger.severe("Property Error: the ID of the transition for the atomic enabling time property must refer to a general transition");
			throw new InvalidPropertyException("Property Error: the ID of the transition for for the enabling time property must refer to a general transition");
	    }
	    
	    if (!invalid && ((compare.equals("<") && boundary == 0.0)))
			return -1.0;
				
		PlotEntry startEntry = transitionPlot.getNextEntryBeforeOrAtGivenTime(leftBorder);
		PlotEntry endEntry = transitionPlot.getNextEntryBeforeOrAtGivenTime(rightBorder);
		Double startClock = ((GeneralTransitionEntry)startEntry).getEnablingTime() + (leftBorder - startEntry.getTime());
	    Double endClock = ((GeneralTransitionEntry)endEntry).getEnablingTime() + (rightBorder - endEntry.getTime());

		Boolean leftBorderFulfills = compareValues(endClock, boundary, compare);
		Boolean rightBorderFulfills = compareValues(startClock, boundary, compare);
		
		if (leftBorderFulfills != invalid){
			if (leftBorderIncluded) 
				return leftBorder;
			return leftBorder + Double.MIN_VALUE;
		}
		
		if (((GeneralTransitionEntry)startEntry).getEnabled()){
							
			Double timeDelta = (boundary - startClock);		
			
			if ((compare.contains("=") != invalid)){
				if (timeDelta > 0.0 && leftBorder + timeDelta < rightBorder)
					return leftBorder + timeDelta;
			} else {
				if (timeDelta >= 0.0 && leftBorder + timeDelta + Double.MIN_VALUE < rightBorder)
					return leftBorder + timeDelta + Double.MIN_VALUE;
			}
		}
			
		if (rightBorderIncluded && rightBorderFulfills != invalid)
   			return rightBorder;
	   	return -1.0;
	}
	
	private PropertyFamily checkPropertyFamily(SimpleNode propertyRoot){
		
		switch (propertyRoot.toString()){
			case "ATOMIC_FLUID":
			case "ATOMIC_CLOCK":
			case "ATOMIC_ENABLINGTIME":
			case "ATOMIC_UBOUND":
			case "ATOMIC_LBOUND":
			case "ATOMIC_ARC":	
				return PropertyFamily.continuous;
				
			case "ATOMIC_TOKENS":
			case "ATOMIC_ENABLED":
			case "ATOMIC_DRIFT":
			case "TRUE":
				return PropertyFamily.discrete;
				
			case "NOT":
				return checkPropertyFamily((SimpleNode)propertyRoot.jjtGetChild(0));
				
			case "AND":
			case "OR":
				return checkCombinedPropertyFamily((SimpleNode)propertyRoot.jjtGetChild(0), (SimpleNode)propertyRoot.jjtGetChild(1));
				
			case "UNTIL":
				return checkCombinedPropertyFamily((SimpleNode)propertyRoot.jjtGetChild(2), (SimpleNode)propertyRoot.jjtGetChild(3));
		}
			
		return PropertyFamily.undefined;
	}
		
	private PropertyFamily checkCombinedPropertyFamily(SimpleNode property1, SimpleNode property2){
		
		PropertyFamily pf1 = checkPropertyFamily(property1);
		PropertyFamily pf2 = checkPropertyFamily(property2);
		
		if (pf1.equals(PropertyFamily.undefined) || pf2.equals(PropertyFamily.undefined))
			return PropertyFamily.undefined;
		
		if (pf1.equals(PropertyFamily.continuous) || pf2.equals(PropertyFamily.continuous))
			return PropertyFamily.continuous;
		
		return PropertyFamily.discrete;
	}
	
	private PlotEntry getCurrentEntry(Double time, String type, String id) throws InvalidPropertyException {
		
		if (type.equals("ATOMIC_ENABLED") || type.equals("ATOMIC_CLOCK") || type.equals("ATOMIC_ENABLINGTIME")){
			for (Transition transition : model.getTransitions()){;
				if (transition.getId().equals(id))
					return plot.getTransitionPlots().get(transition.getId()).getNextEntryBeforeOrAtGivenTime(time);
			}	
		} else {
			for (Place place : model.getPlaces()){				
				if (place.getId().equals(id))
					return plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(time);
			}		
		}
		if (logger != null)
			logger.severe("Property Error: the ID of the place or transition '" + id + "' could not be matched to any plot entry");
		throw new InvalidPropertyException("Property Error: the ID of the place or transition '" + id + "' could not be matched to any plot entry");
	}
	
	private Boolean compareValues(Double value, Double boundary, String compare) throws InvalidPropertyException{

		switch (compare){
			case ">":
				return (value > boundary);
			case "=":
				return ((value - boundary) == 0.0);
			case "<":
				return (value < boundary);
			case ">=":
				return (value >= boundary);
			case "<=":
				return (value <= boundary);	
		}		
		if (logger != null)
			logger.severe("Property Error: the comparison '" + compare + "' could not be identified");
		throw new InvalidPropertyException("Property Error: the comparison '" + compare + "' could not be identified");
	}
		
	/*private SimpleNode getPropertyRoot(SimpleNode root) throws InvalidPropertyException{
		
		for (int i=0;i < root.jjtGetNumChildren(); i++){
			if (root.jjtGetChild(i).toString().equals("PROBGE") || root.jjtGetChild(i).toString().equals("PROBL") || root.jjtGetChild(i).toString().equals("PROBLE") || root.jjtGetChild(i).toString().equals("PROBG"))
				return (((SimpleNode)root.jjtGetChild(i).jjtGetChild(1)));
			if (root.jjtGetChild(i).toString().equals("PROBQ") || root.jjtGetChild(i).toString().equals("PROB"))
				return (((SimpleNode)root.jjtGetChild(i).jjtGetChild(0)));
		}
		if (logger != null)
			logger.severe("Property Error: the property root could not be identified");
		throw new InvalidPropertyException("Property Error: the property root could not be identified");
	
	}*/
		
	private String getPropertyID(SimpleNode atomic) throws InvalidPropertyException{
		
		String id;
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("ID")){
				id = ((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString();
				id = id.substring(1, id.length() - 1);
				return id;
			}
		}
		if (logger != null)
			logger.severe("Property Error: the ID the property node '" + atomic.toString() + "' could not be identified");
		throw new InvalidPropertyException("Property Error: the ID the property node '" + atomic.toString() + "' could not be identified");
	}
		
	private Double getPropertyBoundary(SimpleNode atomic, String type) throws InvalidPropertyException {
		
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("DOUBLE"))
				return Double.parseDouble(((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString());
			else if 
				(atomic.jjtGetChild(i).toString().equals("INTEGER")){
				Integer value = Integer.parseInt(((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString());
				return value.doubleValue();
				}
		}
		if (!type.equals("ATOMIC_UBOUND") && !type.equals("ATOMIC_LBOUND") && !type.equals("ATOMIC_ARC") && !type.equals("ATOMIC_ENABLED")){
			if (logger != null) 
				logger.severe("Property Error: the boundary of the property node '" + atomic.toString() + "' could not be identified");
		throw new InvalidPropertyException("Property Error: the boundary of the property node '" + atomic.toString() + "' could not be identified");
		}
		return 0.0;
	}
		
	private String getPropertyCompare(SimpleNode atomic, String type) throws InvalidPropertyException{
		
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("COMPARE"))
				return ((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString();
		}
		if (!type.equals("ATOMIC_UBOUND") && !type.equals("ATOMIC_LBOUND") && !type.equals("ATOMIC_ARC") && !type.equals("ATOMIC_ENABLED")){
			if (logger != null)
				logger.severe("Property Error: the comparison the property node '" + atomic.toString() + "' could not be identified");
			throw new InvalidPropertyException("Property Error: the comparison the property node '" + atomic.toString() + "' could not be identified");
		}
		return "";
	}
	
	private static Double checkForUntil(SimpleNode propertyRoot, Double maxTime) {
		
		Double currentMax;
		
		if (propertyRoot.toString().equals("UNTIL"))			
			maxTime = maxTime + Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(1)).jjtGetValue().toString());
			

		if (propertyRoot.jjtGetNumChildren() > 0) {
			
			currentMax = checkForUntil((SimpleNode)propertyRoot.jjtGetChild(0), maxTime);
			for (int i = 1;i < propertyRoot.jjtGetNumChildren(); i++){
				currentMax = Math.max(currentMax, checkForUntil((SimpleNode)propertyRoot.jjtGetChild(i), maxTime));
			}	
			return currentMax;
		}
		
		return maxTime;
	}
}
