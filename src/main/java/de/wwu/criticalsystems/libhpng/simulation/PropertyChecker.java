package de.wwu.criticalsystems.libhpng.simulation;

import java.util.Iterator;
import java.util.Map.Entry;

import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.ConfidenceIntervalCalculator.PropertyType;

public class PropertyChecker {
	
	private HPnGModel model;
	private MarkingPlot plot;
	
	private static enum PropertyFamily {discrete, continuous, undefined}

	public Boolean checkProperty(SimpleNode root, MarkingPlot plot, HPnGModel model){
	
		this.model = model;
		this.plot = plot;
		
		Boolean fulfilled = false;
		
		Double time = getTimeFromRoot(root);
		SimpleNode propertyRoot = getPropertyRoot(root);
		//TODO: PROB case instead of PROBQ
		if (propertyRoot == null)
			System.out.println("Property Error");
		
		fulfilled = checkAnyProperty(propertyRoot, time);	
		
		return fulfilled;		
	}
	
	
	private static Double getTimeFromRoot(SimpleNode propertyRoot){
		
		for (int i=0;i < propertyRoot.jjtGetNumChildren(); i++){
			if (propertyRoot.jjtGetChild(i).toString().equals("TIME"))
				return Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(i).jjtGetChild(0)).jjtGetValue().toString());
		}		
		System.out.println("Property Error!");
		return 0.0;
	}
	
	//recursive method for checking property
	private Boolean checkAnyProperty(SimpleNode propertyRoot, Double time){
		
		if (propertyRoot.toString().startsWith("ATOMIC_"))
			return checkAtomicProperty(propertyRoot, time);
			
		switch (propertyRoot.toString()){
			case "NOT":
				return !checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time);
			case "AND":
				return (checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time) && checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(1), time));
			case "OR":
				return (checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time) || checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(1), time));
			case "UNTIL":
				return (checkUntilProperty((SimpleNode)propertyRoot, time));
		}
		
		System.out.println("Property Error");
		return false;
	}

	private Boolean checkAtomicProperty(SimpleNode propertyRoot, Double time) {
		
		Double value;
		GuardArc guardArc = null;
		String temp = null;
		String id = null;
		
		PropertyType type = getAtomicPropertyType(propertyRoot);
		
		temp = getPropertyID(propertyRoot);
		
		//get place id for guard arc case
		if (type.equals(PropertyType.arc)){
			for (Arc arc : model.getArcs()){
				if (arc.getId().equals(temp) && arc.getClass().equals(GuardArc.class)){
					guardArc = (GuardArc)arc;
					id = arc.getConnectedPlace().getId();
					break;
				}
			}
		} else
			id = temp;		
		if (id == null){
			System.out.println("Property Error!");
			return false;
		}
		
		PlotEntry currentEntry = getCurrentEntry(time, type, id);		
		Double boundary = getPropertyBoundary(propertyRoot);;
		String compare = getPropertyCompare(propertyRoot);
		
		//set boundaries for upper boundary, lower boundary and guard arc case
		switch (type){
		case ubound:
			for (Place place : model.getPlaces()){				
				if (place.getId().equals(id) && place.getClass().equals(ContinuousPlace.class)){
					if (((ContinuousPlace)place).getUpperBoundaryInfinity() && (compare.equals("<") || compare.equals("<=")))
						return true;
					else if (((ContinuousPlace)place).getUpperBoundaryInfinity())
						return false;					
					boundary = ((ContinuousPlace)place).getUpperBoundary();							
				}					
			}
			if (boundary == null){
				System.out.println("Property Error (boundary)");
				return false;
			}
			break;
		case arc:
			boundary = guardArc.getWeight();
			break;
		case lbound:
			boundary = 0.0;
			break;
		default:
			break;
		}
		
		//compare values depending on case
		switch (type){
			case fluidlevel:
			case ubound:
			case lbound:
			case arc:
				value = ((ContinuousPlaceEntry)currentEntry).getFluidLevel();
				if (currentEntry.getTime() < time)
					value = Math.max(0.0, value + ((ContinuousPlaceEntry)currentEntry).getDrift()*(time - currentEntry.getTime()));				
				return (compareValues(value, boundary, compare));
			case token:
				value = ((DiscretePlaceEntry)currentEntry).getNumberOfTokens().doubleValue();
				return (compareValues(value, boundary, compare));
			case drift:
				value = ((ContinuousPlaceEntry)currentEntry).getDrift();
				return (compareValues(value, boundary, compare));
			case enabled:
				return ((TransitionEntry)currentEntry).getEnabled();
			case clock:
				value = ((DeterministicTransitionEntry)currentEntry).getClock();
				return (compareValues(value, boundary, compare));
			case firings:
				value = ((GeneralTransitionEntry)currentEntry).getFirings().doubleValue();
				return (compareValues(value, boundary, compare));			
		}
		
		System.out.println("Property Error");
		return false;
	}
	
	
	private Boolean checkUntilProperty(SimpleNode propertyRoot, Double time) {
		
		Double t1 = time + Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(0)).jjtGetValue().toString());
		Double t2 = time + Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(1)).jjtGetValue().toString());
		if (t1 > t2)
			System.out.println("Property Error");
		
		SimpleNode psi1 = (SimpleNode)propertyRoot.jjtGetChild(2);
		SimpleNode psi2 = (SimpleNode)propertyRoot.jjtGetChild(3);		
		PropertyFamily psi1Family = checkPropertyFamily(psi1);
		PropertyFamily psi2Family = checkPropertyFamily(psi2);
		
		if (psi1Family.equals(PropertyFamily.undefined) || psi1Family.equals(PropertyFamily.undefined))
			System.out.println("Property Error");
				
		//check t = time	
		if (!checkAnyProperty(psi1, time))
			return false;		
		
		//check interval [time, t1)
		Double currentEventTime = plot.getNextEventTime(time);
		Double previousEventTime = time;
		while (currentEventTime < t1){		
			if (psi1Family.equals(PropertyFamily.discrete)){
				if (!checkAnyProperty(psi1, currentEventTime))
					return false;
			} else {
				if (findTForInvalidProperty(previousEventTime, currentEventTime, false, true, psi1) != -1.0)
					return false;
			}
			previousEventTime = currentEventTime;
			currentEventTime = plot.getNextEventTime(currentEventTime);
		}
		
		//check interval [t1, t2]
		if (plot.eventAtTime(t1) && checkAnyProperty(psi2, t1))
			return true;
		
		currentEventTime = plot.getNextEventTime(t1);
		previousEventTime = t1;
		
		while (currentEventTime < t2){		
			
			if (psi2Family.equals(PropertyFamily.discrete) && psi1Family.equals(PropertyFamily.discrete)){
				
				if (checkAnyProperty(psi2, currentEventTime))
					return true;
				if (!checkAnyProperty(psi1, currentEventTime))
					return false;
				
			} else if (psi2Family.equals(PropertyFamily.discrete) && psi1Family.equals(PropertyFamily.continuous)){
				
				Double tNotPsi1 = findTForInvalidProperty(previousEventTime, currentEventTime, false, false, psi1);				
				if (checkAnyProperty(psi2, currentEventTime) && (tNotPsi1 == -1.0 || tNotPsi1 >= currentEventTime))
					return true;				
				if (!checkAnyProperty(psi1, currentEventTime) || (tNotPsi1 > -1.0 && tNotPsi1 < currentEventTime))
					return false;
				
			} else {
				//psi2 is continuous
				
				//search in current interval for a time tPsi2 where psi2 is fulfilled
				Double tPsi2 = findTForProperty(previousEventTime, currentEventTime, false, true, psi2);
				
				if (psi1Family.equals(PropertyFamily.discrete)){
					
					if (tPsi2 > -1.0)
						return true;					
					if (!checkAnyProperty(psi1, currentEventTime))
						return false;
					
				} else {
					//psi1 and psi2 are continuous
					
					if (tPsi2 > -1.0){
						if (findTForInvalidProperty(previousEventTime, tPsi2, false, false, psi1) != -1.0)
							return false;
						return true;
					} else {
						if (findTForInvalidProperty(previousEventTime, currentEventTime, false, true, psi1) != -1.0)
							return false;
					}
				}
			}
			previousEventTime = currentEventTime;
			currentEventTime = plot.getNextEventTime(currentEventTime);
		}
		return false;
	}

	private PropertyFamily checkPropertyFamily(SimpleNode propertyRoot){
		
		switch (propertyRoot.toString()){
			case "ATOMIC_FLUID":
			case "ATOMIC_CLOCK":
				return PropertyFamily.continuous;
				
			case "ATOMIC_TOKENS":
			case "ATOMIC_ENABLED":
			case "ATOMIC_FIRINGS":
			case "ATOMIC_DRIFT":
			case "ATOMIC_UBOUND":
			case "ATOMIC_LBOUND":
			case "ATOMIC_ARC":	
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


	private PropertyType getAtomicPropertyType(SimpleNode propertyRoot) {
		
		switch (propertyRoot.toString()){
			case "ATOMIC_FLUID":
				return PropertyType.fluidlevel;
			case "ATOMIC_TOKENS":
				return PropertyType.token;
			case "ATOMIC_ENABLED":
				return PropertyType.enabled;
			case "ATOMIC_CLOCK":
				return PropertyType.clock;
			case "ATOMIC_FIRINGS":
				return PropertyType.firings;
			case "ATOMIC_DRIFT":
				return PropertyType.drift;
			case "ATOMIC_UBOUND":
				return PropertyType.ubound;
			case "ATOMIC_LBOUND":
				return PropertyType.lbound;
			case "ATOMIC_ARC":	
				return PropertyType.arc;
		}
		System.out.println("Property Error!");
		return null;
	}

	
	private PlotEntry getCurrentEntry(Double time, PropertyType type, String id) {
		
		PlotEntry currentEntry = null;
		
		if (type.equals(PropertyType.fluidlevel) || type.equals(PropertyType.token) || type.equals(PropertyType.drift) || type.equals(PropertyType.ubound) || type.equals(PropertyType.lbound)){
			for (Place place : model.getPlaces()){				
				if (place.getId().equals(id))
					return plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(time);
			}	
		} else {
			for (Transition transition : model.getTransitions()){;
				if (transition.getId().equals(id))
					return plot.getTransitionPlots().get(transition.getId()).getNextEntryBeforeOrAtGivenTime(time);
			}			
		}
		System.out.println("ID error in Property: " + id);		
		return currentEntry;
	}

	private Boolean compareValues(Double value, Double boundary, String compare){

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
		System.out.println("Property Error!");
		return false;
	}
	
	
	private SimpleNode getPropertyRoot(SimpleNode root){
		for (int i=0;i < root.jjtGetNumChildren(); i++){
			if (root.jjtGetChild(i).toString().equals("PROBQ"))
				return (((SimpleNode)root.jjtGetChild(i).jjtGetChild(0)));
		}
		
		System.out.println("Property Error!");
		return null;
	}
	
	private String getPropertyID(SimpleNode atomic){
		
		String id;
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("ID")){
				id = ((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString();
				id = id.substring(1, id.length() - 1);
				return id;
			}
		}
		System.out.println("Property Error!");
		return null;
	}
	
	private Double getPropertyBoundary(SimpleNode atomic) {
		
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("DOUBLE"))
				return Double.parseDouble(((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString());
			else if 
				(atomic.jjtGetChild(i).toString().equals("INTEGER")){
				Integer value = Integer.parseInt(((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString());
				return value.doubleValue();
				}
		}
		return null;
	}
	
	private String getPropertyCompare(SimpleNode atomic){
		
		for (int i=0;i < atomic.jjtGetNumChildren(); i++){
			if (atomic.jjtGetChild(i).toString().equals("COMPARE"))
				return ((SimpleNode)atomic.jjtGetChild(i)).jjtGetValue().toString();
		}
		return "";
	}
	
		
	private Double findTForProperty(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot){

		if (leftBorder + 0.0000000001 >= rightBorder){
			if (leftBorderIncluded && checkAnyProperty(propertyRoot, leftBorder))
				return leftBorder;
			if (rightBorderIncluded && checkAnyProperty(propertyRoot, rightBorder))
				return rightBorder;
			return -1.0;
		}
				
		switch (propertyRoot.toString()){			
		
			case "ATOMIC_FLUID":				
				return findTForAtomicFluid(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, false);
				
		   	case "ATOMIC_CLOCK":			
		   		return findTForAtomicClock(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, false);
			
			case "ATOMIC_TOKENS":
			case "ATOMIC_ENABLED":
			case "ATOMIC_FIRINGS":
			case "ATOMIC_DRIFT":
			case "ATOMIC_UBOUND":
			case "ATOMIC_LBOUND":
			case "ATOMIC_ARC":
				if (checkAnyProperty(propertyRoot, leftBorder)){
					if (leftBorderIncluded)
						return leftBorder;
					return leftBorder + 0.0000000001;
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
				//TODO
				System.out.println("Nested UNTIL not supported");
		}		
		return -1.0;
	}
	
	
	
	private Double findTForInvalidProperty(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot){

		if (leftBorder + 0.0000000001 >= rightBorder){
			if (leftBorderIncluded && !checkAnyProperty(propertyRoot, leftBorder))
				return leftBorder;
			if (rightBorderIncluded && !checkAnyProperty(propertyRoot, rightBorder))
				return rightBorder;
			return -1.0;
		}
		
		switch (propertyRoot.toString()){			
		
			case "ATOMIC_FLUID":				
				return findTForAtomicFluid(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, true);
		    	
				
		   	case "ATOMIC_CLOCK":
		   		return findTForAtomicClock(leftBorder, rightBorder, leftBorderIncluded, rightBorderIncluded, propertyRoot, true);
			
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
				//TODO
				System.out.println("Nested UNTIL not supported");
		}	    
	    return -1.0;
	}
	
	
	private Double findTForAtomicFluid(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot, Boolean invalid){
		
		Double boundary = getPropertyBoundary(propertyRoot);
		String compare = getPropertyCompare(propertyRoot);		
		if (boundary < 0.0){
			System.out.println("Property Error");
		 	return -1.0;
		}
		
    	PlacePlot placePlot = null, currentPlacePlot;		
    	String id = getPropertyID(propertyRoot);		
		Iterator<Entry<String, PlacePlot>> placeIterator = plot.getPlacePlots().entrySet().iterator();
	    while (placeIterator.hasNext()) {
	    	currentPlacePlot = placeIterator.next().getValue();		
			if (currentPlacePlot.getReferencedPlace().getId().equals(id)){
				placePlot = currentPlacePlot;
				break;
			}
		}
	    if (!placePlot.getReferencedPlace().getClass().equals(ContinuousPlace.class)){
	    	System.out.println("ID Error");
	    	return -1.0;
	    }
	    

		if (!invalid && ((compare.equals("<") && boundary == 0.0) || (compare.equals(">") && !((ContinuousPlace)placePlot.getReferencedPlace()).getUpperBoundaryInfinity() && boundary > ((ContinuousPlace)placePlot.getReferencedPlace()).getUpperBoundary())))
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
			return leftBorder + 0.000000001;
		}
		
		Double drift = ((ContinuousPlaceEntry)startEntry).getDrift();
		
		if (drift != 0.0){
							
			Double timeDelta = (boundary - startValue)/ drift;		
			
			if ((compare.contains("=") != invalid)){
				if (timeDelta > 0.0 && leftBorder + timeDelta < rightBorder)
					return leftBorder + timeDelta;
			} else {
				if (timeDelta >= 0.0 && leftBorder + timeDelta + 0.0000001 < rightBorder)
					return leftBorder + timeDelta + 0.000000001;
			}
		}
			
		if (rightBorderIncluded && rightBorderFulfills != invalid)
   			return rightBorder;
	   	return -1.0;
	}

	
	private Double findTForAtomicClock(Double leftBorder, Double rightBorder, Boolean leftBorderIncluded, Boolean rightBorderIncluded, SimpleNode propertyRoot, Boolean invalid){
		
		Double boundary = getPropertyBoundary(propertyRoot);
		String compare = getPropertyCompare(propertyRoot);		
		if (boundary < 0.0){
			System.out.println("Property Error");
		 	return -1.0;
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
	    	System.out.println("ID Error");
	    	return -1.0;
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
			return leftBorder + 0.000000001;
		}
		
		if (((DeterministicTransitionEntry)startEntry).getEnabled()){
							
			Double timeDelta = (boundary - startClock);		
			
			if ((compare.contains("=") != invalid)){
				if (timeDelta > 0.0 && leftBorder + timeDelta < rightBorder)
					return leftBorder + timeDelta;
			} else {
				if (timeDelta >= 0.0 && leftBorder + timeDelta + 0.0000001 < rightBorder)
					return leftBorder + timeDelta + 0.000000001;
			}
		}
			
		if (rightBorderIncluded && rightBorderFulfills != invalid)
   			return rightBorder;
	   	return -1.0;
	}
	
	
	
	public static Double getMaxTimeForSimulation(SimpleNode propertyRoot){
		
		Double maxTime = getTimeFromRoot(propertyRoot);
		Double untilTime = checkForUntil(propertyRoot, maxTime);
		
		return untilTime;
	}


	private static Double checkForUntil(SimpleNode propertyRoot, Double maxTime) {
		
		Double currentMax;
		
		if (propertyRoot.toString().equals("UNTIL")){
			
			Double t2 = maxTime + Double.parseDouble(((SimpleNode)propertyRoot.jjtGetChild(1)).jjtGetValue().toString());
			return t2;
			
		} else if (propertyRoot.jjtGetNumChildren() > 0) {
			
			currentMax = checkForUntil((SimpleNode)propertyRoot.jjtGetChild(0), maxTime);
			for (int i = 1;i < propertyRoot.jjtGetNumChildren(); i++){
				currentMax = Math.max(currentMax, checkForUntil((SimpleNode)propertyRoot.jjtGetChild(i), maxTime));
			}	
			return currentMax;
		}
		
		return maxTime;
	}
	
}
