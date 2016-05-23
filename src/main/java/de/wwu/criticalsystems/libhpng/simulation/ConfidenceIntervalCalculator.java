package de.wwu.criticalsystems.libhpng.simulation;

import umontreal.iro.lecuyer.probdist.StudentDist;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;



public class ConfidenceIntervalCalculator {
	
	
	public ConfidenceIntervalCalculator(HPnGModel model, String id, Integer min_runs) {
		this.model = model;
		this.id = id;
		this.min_runs = min_runs;
	}
	
	public static enum Comparator{greater, equal, less, greaterequal, lessequal};
	public static enum PropertyType{fluidlevel, token, drift, enabled, clock, firings};

	public void setModel(HPnGModel model) {
		this.model = model;
	}

	public void setPlaceTransitionOrArcID(String id) {
		this.id = id;
	}

	public Integer getN_runs() {
		return n_runs;
	}
	
	public Integer getMin_runs() {
		return min_runs;
	}
	
	public Double getSsquare() {
		return ssquare;
	}

	public Double getMean() {
		return mean;
	}	

	public Double getT() {
		return t;
	}
	
	private HPnGModel model;
	private String id;
	private Integer n_runs;
	private Integer min_runs;
	private Integer fulfilled;
	private Double ssquare;
	private Double mean;
	private Double t;
	
	public void calculateSSquare(Double timeToCheck, PropertyType typeToCheck, Double boundaryToCheck, Comparator compare, Integer currentRun, MarkingPlot currentPlot) {
		
		switch (typeToCheck){
		case drift:
		case fluidlevel:
		case token:
			calculateSSquareForPlaceConditions(timeToCheck, typeToCheck, boundaryToCheck, compare, currentRun, currentPlot);
			break;
		case enabled:
		case clock:
		case firings:
			calculateSSquareForTransitionConditions(timeToCheck, typeToCheck, boundaryToCheck, compare, currentRun, currentPlot);
			break;
		default:
			break;
		
		}		
	}
	

	public void findTDistribution(Double confidenceLevel){
		
		if (n_runs < 2)
			t = 0.0;
		else {				
			Double alphaHalf = (1.0 - confidenceLevel)/2.0;
			t = StudentDist.inverseF(n_runs - 1, 1.0 - alphaHalf);
		}
	}
	
	public Boolean checkBound(Double width){
		if (ssquare == null || (ssquare == 0.0 && n_runs < min_runs)) return false;
		Double bound = Math.pow(t, 2.0)*ssquare / Math.pow(width, 2.0);
		return (bound <= n_runs);
	}
	
	public Double getLowerBorder(){
		return Math.max(0.0,(mean - t * Math.sqrt(ssquare/n_runs)));
	}
	
	public Double getUpperBorder(){
		return Math.min(1.0,(mean + t * Math.sqrt(ssquare/n_runs)));
	}
		
	
	//According to http://www.prismmodelchecker.org/papers/vincent-nimal-msc.pdf
	private void calculateSSquareForPlaceConditions(Double time, PropertyType type, Double boundary, Comparator compare, Integer currentRun, MarkingPlot plot){
			
		PlotEntry currentEntry;
		
		if (currentRun == 1){
			n_runs = 0;
			fulfilled = 0;
		}
					
		for (Place place : model.getPlaces()){			
	
			if (place.getId().equals(id)){			

				currentEntry = plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(time);

				if (getPropertyFulfilled(currentEntry, time, type, boundary, compare))						
					fulfilled++;
				
				n_runs++;	
				mean = fulfilled.doubleValue() / n_runs.doubleValue();
				
				if (n_runs == 1)
					ssquare = 0.0;
				else
					ssquare = (fulfilled.doubleValue()*(n_runs.doubleValue() - fulfilled.doubleValue()))/(n_runs.doubleValue()*(n_runs.doubleValue() - 1.0));
			}						
		}		
	}
	
	private void calculateSSquareForTransitionConditions(Double time, PropertyType type, Double boundary, Comparator compare, Integer currentRun, MarkingPlot plot){
		
		PlotEntry currentEntry;
		
		if (currentRun == 1){
			n_runs = 0;
			fulfilled = 0;
		}
					
		for (Transition transition : model.getTransitions()){			
	
			if (transition.getId().equals(id)){			

				currentEntry = plot.getTransitionPlots().get(transition.getId()).getNextEntryBeforeOrAtGivenTime(time);

				if (getPropertyFulfilled(currentEntry, time, type, boundary, compare))						
					fulfilled++;
				
				n_runs++;	
				mean = fulfilled.doubleValue() / n_runs.doubleValue();
				
				if (n_runs == 1)
					ssquare = 0.0;
				else
					ssquare = (fulfilled.doubleValue()*(n_runs.doubleValue() - fulfilled.doubleValue()))/(n_runs.doubleValue()*(n_runs.doubleValue() - 1.0));
			}						
		}		
	}
	
	private Boolean getPropertyFulfilled(PlotEntry currentEntry, Double time, PropertyType type, Double boundary, Comparator compare) {
		Double value;
		
		switch (type){
			case fluidlevel:
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
			default:
				return false;				
		}
		
	}

	private Boolean compareValues(Double value, Double boundary, Comparator compare){

		switch (compare){
			case greater:
				return (value > boundary);
			case equal:
				return ((value - boundary) == 0.0);
			case less:
				return (value < boundary);
			case greaterequal:
				return (value >= boundary);
			case lessequal:
				return (value <= boundary);	
		}		
		return false;
	}
}
