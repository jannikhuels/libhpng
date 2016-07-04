package de.wwu.criticalsystems.libhpng.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class XYLineGraph extends ApplicationFrame {
	
	private static final long serialVersionUID = -8732507005986290121L;

	public XYLineGraph(String title, String xAxisName, String yAxisName) {

		super(title);
        
        this.chart = ChartFactory.createXYLineChart(title, xAxisName, yAxisName, data, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(750, 405));
        setContentPane(chartPanel);
	}
	
	
	private JFreeChart chart;
	private XYSeriesCollection data = new XYSeriesCollection();
	
	public XYSeries getSeries(String seriesName) {
		return data.getSeries(seriesName);
	}
	
	public void addSeries(String seriesName){
		data.addSeries(new XYSeries(seriesName));
		data.getSeries(seriesName);
	}
	
	public void addSeriesEntry(String seriesName, Double x, Double y){
		data.getSeries(seriesName).add(x,y);
	}
	
	public XYPlot getPlot(){
		return (XYPlot)chart.getPlot();
	}
}
