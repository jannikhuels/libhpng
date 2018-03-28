package de.wwu.criticalsystems.libhpng.simulation;

import java.util.ArrayList;

public class SimulationEvent {
	
	public SimulationEvent(Double maxTime) {		
		priority = -1;
		occurenceTime = maxTime + 0.01;
		eventType = SimulationEventType.no_event;
	}

	
	public static enum SimulationEventType{immediate_transition, deterministic_transition, general_transition, place_boundary, guard_arcs_immediate, guard_arcs_continuous, guard_arcs_deterministic, place_internaltransition, no_event}
	
	
	public SimulationEventType getEventType() {
		return eventType;
	}
	public void setEventType(SimulationEventType eventType) {
		this.eventType = eventType;
	}
	
	public ArrayList<Object> getRelatedObjects() {
		return relatedObjects;
	}
	
	public Double getOccurenceTime() {
		return occurenceTime;
	}
	public void setOccurenceTime(Double occurenceTime) {
		this.occurenceTime = occurenceTime;
	}
	
	public Integer getPriority() {
		return priority;
	}

	
	private SimulationEventType eventType;
	private ArrayList<Object> relatedObjects = new ArrayList<Object>();
	private Double occurenceTime;
	private Integer priority;

	
	public void setFirstEventItem(Object eventObject, Integer priority){
		relatedObjects.clear();
		relatedObjects.add(eventObject);
		this.priority = priority;		
	}
}