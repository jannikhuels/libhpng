package de.wwu.criticalsystems.libhpng.plotting;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RefineryUtilities;
import umontreal.ssj.probdist.StudentDist;

import de.wwu.criticalsystems.libhpng.model.*;

public class ContinuousPlacesPlotter {
	
	
	public ContinuousPlacesPlotter() {	}


	private ArrayList<ContinuousPlaceEntry> means = new ArrayList<ContinuousPlaceEntry>();
	private ArrayList<ContinuousPlaceEntry> ssquares = new ArrayList<ContinuousPlaceEntry>();

	
	public void plotContinuousPlaces(HPnGModel model, ArrayList<MarkingPlot> plots, Double maxTime, Double confidenceLevel){

		int series=0;
		Double t;
		
		System.out.println("Plotting graph...");
		
		XYLineGraph graph = new XYLineGraph("Fluid level of continuous places", "time", "fluid level");
			
		//find t distribution
		if (plots.size() < 2)
			t = 0.0;
		else {				
			Double alphaHalf = (1.0 - confidenceLevel)/2.0;
			t = StudentDist.inverseF(plots.size() - 1, 1.0 - alphaHalf);
		}
		
		//plot means and confidence intervals for all continuous places
		for (Place place : model.getPlaces()){			
			if (place.getClass().equals(ContinuousPlace.class)){
					
				graph = addSeriesToGraph(graph, place.getId(), series);
				plotMeansAndConfidenceIntervals((ContinuousPlace)place, plots, maxTime, graph, t);
					
				series +=3;				
			}
		}
		
		graph.pack();
		RefineryUtilities.centerFrameOnScreen(graph);
		graph.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		graph.setVisible(true);		
	}

	
	private void plotMeansAndConfidenceIntervals(ContinuousPlace place, ArrayList<MarkingPlot> plots, Double maxTime, XYLineGraph graph, Double t){
	
		Double meanFluid, interval, time, fluid, ssquareFluid;
		PlotEntry currentEntry;
			
		means.clear();
		ssquares.clear();
		meanFluid = 0.0;
		ssquareFluid = 0.0;

		//for time=0.0, calculate mean and s² and save to graph
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
		
		
		
		//for all event timings up to maxTime, calculate mean and s² and save to graph
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
			//graph.addSeriesEntry(place.getId() + "_up", time, (meanFluid + t * Math.sqrt(ssquareFluid/plots.size())));
			//graph.addSeriesEntry(place.getId() + "_low", time, (meanFluid - t * Math.sqrt(ssquareFluid/plots.size())));
			
		}
	}
	
	
	private XYLineGraph addSeriesToGraph(XYLineGraph oldGraph, String id, Integer series) {
		
		XYLineGraph graph = oldGraph;		
		Color color = defineColor(series);
		
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) graph.getPlot().getRenderer();
		Shape bar = new Rectangle2D.Double(-0.5, -0.1, 1.0, 0.2);
		
		graph.addSeries(id);		
		renderer.setSeriesPaint(series, color);			
		renderer.setSeriesShapesVisible((int)series, false);
		
		
		graph.addSeries(id + "_up");
		renderer.setSeriesPaint(series + 1, color);			
		renderer.setSeriesVisibleInLegend(series + 1, false);
		renderer.setSeriesLinesVisible(series + 1, false);	
		renderer.setSeriesShapesVisible(series + 1, true);
		renderer.setSeriesShape(series + 1, bar);

	
		graph.addSeries(id+ "_low");	
		renderer.setSeriesPaint(series + 2, color);			
		renderer.setSeriesVisibleInLegend(series + 2, false);
		renderer.setSeriesLinesVisible(series + 2, false);	
		renderer.setSeriesShapesVisible(series + 2, true);
		renderer.setSeriesShape(series + 2, bar);

		return graph;
	}

		
	
	private Color defineColor(Integer i){
		
		while (i>5)
			i-=5;		
		int rgb = Color.HSBtoRGB(i.floatValue()/5f,0.5f,0.9f);		
		return new Color(rgb);		
	}
}
