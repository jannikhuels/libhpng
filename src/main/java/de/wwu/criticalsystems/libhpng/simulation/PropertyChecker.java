package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;
import de.wwu.criticalsystems.libhpng.simulation.ConfidenceIntervalCalculator.PropertyType;

public class PropertyChecker {
	
	private HPnGModel model;
	private MarkingPlot plot;

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
	
	
	static public Double getTimeFromRoot(SimpleNode root){
		
		for (int i=0;i < root.jjtGetNumChildren(); i++){
			if (root.jjtGetChild(i).toString().equals("TIME"))
				return Double.parseDouble(((SimpleNode)root.jjtGetChild(i).jjtGetChild(0)).jjtGetValue().toString());
		}		
		System.out.println("Property Error!");
		return 0.0;
	}
	
	//recursive method for checking property
	private Boolean checkAnyProperty(SimpleNode propertyRoot, Double time){
		
		if (propertyRoot.toString().startsWith("ATOMIC_"))
			return checkAtomicProperty(propertyRoot, time);
			
		if (propertyRoot.toString().equals("NOT"))
			return !checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time);
		
		if (propertyRoot.toString().equals("AND"))
			return (checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time) && checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(1), time));
		
		if (propertyRoot.toString().equals("OR"))
			return (checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(0), time) || checkAnyProperty((SimpleNode)propertyRoot.jjtGetChild(1), time));
		
		//TODO: until
		
		System.out.println("Property Error");
		return false;
	}
	
	private Boolean checkAtomicProperty(SimpleNode propertyRoot, Double time) {
		
		Double value;
		GuardArc guardArc = null;
		String temp = null;
		String id = null;
		
		PropertyType type = getPropertyType(propertyRoot);
		
		temp = getPropertyID(propertyRoot, type);
		
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


	private PropertyType getPropertyType(SimpleNode propertyRoot) {
		
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
	
	private String getPropertyID(SimpleNode atomic, PropertyType type){
		
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
		
		System.out.println("Property Error!");
		return "";
	}
}
