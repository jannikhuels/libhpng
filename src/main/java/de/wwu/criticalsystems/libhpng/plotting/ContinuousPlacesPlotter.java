package de.wwu.criticalsystems.libhpng.plotting;

import java.awt.Color;
import java.util.ArrayList;
import org.jfree.ui.RefineryUtilities;

import umontreal.iro.lecuyer.probdist.StudentDist;
import de.wwu.criticalsystems.libhpng.model.*;

public class ContinuousPlacesPlotter {
	
	
	private ArrayList<ContinuousPlaceEntry> means = new ArrayList<ContinuousPlaceEntry>();
	private ArrayList<ContinuousPlaceEntry> ssquares = new ArrayList<ContinuousPlaceEntry>();

	
	public void plotContinuousPlaces(HPnGModel model, ArrayList<MarkingPlot> plots, Double maxTime, Double confidenceLevel){

		int series=0;
		Double t;
		
		XYLineGraph graph = new XYLineGraph("Continuous Places Mean", "time", "fluid level");
			
		//find t distribution
		if (plots.size() < 2)
			t = 0.0;
		else {				
			Double alphaHalf = (1.0 - confidenceLevel)/2.0;
			t = StudentDist.inverseF(plots.size() - 1, 1.0 - alphaHalf);
		}
		
		for (Place place : model.getPlaces()){			
			if (place.getClass().equals(ContinuousPlace.class)){
					
				graph = addSeriesToGraph(graph, place.getId(), series);
				plotMeansAndConfidenceIntervals((ContinuousPlace)place, plots, maxTime, graph, t);
					
				series +=3;				
			}
		}
		
		graph.pack();
		RefineryUtilities.centerFrameOnScreen(graph);
		graph.setVisible(true);
		
	}

	
	

	private void plotMeansAndConfidenceIntervals(ContinuousPlace place, ArrayList<MarkingPlot> plots, Double maxTime, XYLineGraph graph, Double t){
	
		Double meanFluid, interval, time, fluid, ssquareFluid;
		PlotEntry currentEntry;
			
		means.clear();
		ssquares.clear();
		meanFluid = 0.0;
		ssquareFluid = 0.0;

		//for time=0.0
		for (MarkingPlot plot : plots){
			currentEntry = plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(0.0);
			fluid = ((ContinuousPlaceEntry)currentEntry).getFluidLevel();
			meanFluid += fluid;
		}					
		meanFluid = meanFluid / plots.size();
		means.add(new ContinuousPlaceEntry(0.0, meanFluid, null));
		graph.addSeriesEntry(place.getId(), 0.0,  meanFluid);
		
		for (MarkingPlot plot : plots){
			currentEntry = plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(0.0);
			fluid = ((ContinuousPlaceEntry)currentEntry).getFluidLevel();
			ssquareFluid += Math.pow(fluid - meanFluid,2);
		}
		ssquareFluid = ssquareFluid / (plots.size()-1);		
		ssquares.add(new ContinuousPlaceEntry(0.0, ssquareFluid, null));
		graph.addSeriesEntry(place.getId() + "_up", 0.0, (meanFluid + t * Math.sqrt(ssquareFluid/plots.size())));
		graph.addSeriesEntry(place.getId() + "_low", 0.0, (meanFluid - t * Math.sqrt(ssquareFluid/plots.size())));
		
	
		time=0.0;
		interval = maxTime;	
	
		while (time <= maxTime && interval > 0.0){
						
			meanFluid = 0.0;
			ssquareFluid = 0.0;
			interval = maxTime-time;
			for (MarkingPlot plot : plots){
				currentEntry = plot.getPlacePlots().get(place.getId()).getNextEntryAfterGivenTime(time);
				if (currentEntry.getTime() < (time + interval) && currentEntry.getTime() > time)
					interval = currentEntry.getTime() - time;							
			}
			time+=interval;
			
			for (MarkingPlot plot : plots){
				currentEntry = plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(time);
				fluid = ((ContinuousPlaceEntry)currentEntry).getFluidLevel();
				if (currentEntry.getTime() < time)
					fluid = Math.max(0.0, fluid + ((ContinuousPlaceEntry)currentEntry).getDrift()*(time - currentEntry.getTime()));
				
				meanFluid += fluid;
			}			
			meanFluid = meanFluid / plots.size();						
			means.add(new ContinuousPlaceEntry(time, meanFluid, 0.0));
			graph.addSeriesEntry(place.getId(),time,  meanFluid);
			
			for (MarkingPlot plot : plots){
				currentEntry = plot.getPlacePlots().get(place.getId()).getNextEntryBeforeOrAtGivenTime(time);
				fluid = ((ContinuousPlaceEntry)currentEntry).getFluidLevel();
				if (currentEntry.getTime() < time)
					fluid = Math.max(0.0, fluid + ((ContinuousPlaceEntry)currentEntry).getDrift()*(time - currentEntry.getTime()));
				
				ssquareFluid += Math.pow(fluid - meanFluid,2);
			}			
			ssquareFluid = ssquareFluid / (plots.size() - 1);		
			ssquares.add(new ContinuousPlaceEntry(time, ssquareFluid, null));
			graph.addSeriesEntry(place.getId() + "_up", time, (meanFluid + t * Math.sqrt(ssquareFluid/plots.size())));
			graph.addSeriesEntry(place.getId() + "_low", time, (meanFluid - t * Math.sqrt(ssquareFluid/plots.size())));
			
		}
	}
	
	private XYLineGraph addSeriesToGraph(XYLineGraph oldGraph, String id, Integer series) {
		
		XYLineGraph graph = oldGraph;
		
		Color color = defineColor(series);

		
		graph.addSeries(id);
		graph.getPlot().getRenderer().setSeriesPaint(series, color);			
		graph.addSeries(id + "_up");
		graph.getPlot().getRenderer().setSeriesPaint(series + 1, color);
		graph.getPlot().getRenderer().setSeriesVisibleInLegend(series + 1, false);
		graph.addSeries(id+ "_low");
		graph.getPlot().getRenderer().setSeriesPaint(series + 2, color);
		graph.getPlot().getRenderer().setSeriesVisibleInLegend(series + 2, false);
		//graph.getPlot().getRenderer().setSeriesStroke(series + 2, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {2.0f, 2.0f}, 0.0f));
		
		return graph;
	}

	private Color defineColor(Integer i){
		
		while (i>5)
			i-=5;		
		int rgb = Color.HSBtoRGB(i.floatValue()/5f,0.9f,0.9f);		
		return new Color(rgb);		
	}
}
