package de.wwu.criticalsystems.libhpng.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class XYStepGraph extends ApplicationFrame {

	private static final long serialVersionUID = 2626858936539520344L;

	public XYStepGraph(String title, String xAxisName, String yAxisName) {

        super(title);
        
        JFreeChart chart = ChartFactory.createXYStepChart(title, xAxisName, yAxisName, data, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(750, 405));
        setContentPane(chartPanel);
	}

	public XYSeries getSeries(String seriesName) {
		return data.getSeries(seriesName);
	}
	
	private XYSeriesCollection data = new XYSeriesCollection();
	
	public void addSeries(String seriesName){
		data.addSeries(new XYSeries(seriesName));
		data.getSeries(seriesName);
	}
	
	public void addSeriesEntry(String seriesName, Double x, Double y){
		data.getSeries(seriesName).add(x,y);
	}
}
