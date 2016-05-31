package de.wwu.criticalsystems.libhpng.simulation;

import umontreal.iro.lecuyer.probdist.StudentDist;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.*;
import de.wwu.criticalsystems.libhpng.plotting.*;



public class ConfidenceIntervalCalculator {
	
	
	public ConfidenceIntervalCalculator(HPnGModel model, Integer min_runs) {
		this.model = model;
		this.min_runs = min_runs;
	}

	public static enum PropertyType{fluidlevel, token, drift, enabled, clock, firings, ubound, lbound, arc};

	public void setModel(HPnGModel model) {
		this.model = model;
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
	private Integer n_runs;
	private Integer min_runs;
	private Integer fulfilled;
	private Double ssquare;
	private Double mean;
	private Double t;
	
	public void calculateSSquare(SimpleNode root, Integer currentRun, MarkingPlot plot) {
		
		/*switch (typeToCheck){
		case drift:
		case fluidlevel:
		case token:
			calculateSSquareForPlaceConditions(timeToCheck, id, typeToCheck, boundaryToCheck, compare, currentRun, currentPlot);
			break;
		case enabled:
		case clock:
		case firings:
			calculateSSquareForTransitionConditions(timeToCheck, id, typeToCheck, boundaryToCheck, compare, currentRun, currentPlot);
			break;
		default:
			break;
		
		}		*/
		
		
		//PlotEntry currentEntry;
		
		if (currentRun == 1){
			n_runs = 0;
			fulfilled = 0;
		}
					
		PropertyChecker checker = new PropertyChecker();
		
		//currentEntry = plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(time);

		if (checker.checkProperty(root, plot, model))						
			fulfilled++;
				
		n_runs++;	
		mean = fulfilled.doubleValue() / n_runs.doubleValue();
		
		if (n_runs == 1)
			ssquare = 0.0;
		else
			ssquare = (fulfilled.doubleValue()*(n_runs.doubleValue() - fulfilled.doubleValue()))/(n_runs.doubleValue()*(n_runs.doubleValue() - 1.0));		
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
	/*private void calculateSSquareForPlaceConditions(Double time, String id, PropertyType type, Double boundary, Comparator compare, Integer currentRun, MarkingPlot plot){
			
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
	
	private void calculateSSquareForTransitionConditions(Double time, String id, PropertyType type, Double boundary, Comparator compare, Integer currentRun, MarkingPlot plot){
		
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
	}*/
	
	
}
