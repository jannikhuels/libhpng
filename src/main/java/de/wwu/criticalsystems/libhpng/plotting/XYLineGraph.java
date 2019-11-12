package de.wwu.criticalsystems.libhpng.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class XYLineGraph extends JFrame {
	
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

	public void getSVG(String imagePath){
		SVGGraphics2D svg =  new SVGGraphics2D(750,405);
		chart.draw(svg, new Rectangle(750,405));
		String svgElement = svg.getSVGElement();
		try {
			SVGUtils.writeToSVG(new File(imagePath),svgElement);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
