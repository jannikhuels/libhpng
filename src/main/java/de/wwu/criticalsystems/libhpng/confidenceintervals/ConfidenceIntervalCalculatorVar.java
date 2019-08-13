package de.wwu.criticalsystems.libhpng.confidenceintervals;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import de.wwu.criticalsystems.libhpng.model.HPnGModel;
import de.wwu.criticalsystems.libhpng.model.HPnGModelVar;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlot;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;
import de.wwu.criticalsystems.libhpng.simulation.PropertyChecker;
import de.wwu.criticalsystems.libhpng.simulation.PropertyCheckerVar;

import java.util.logging.Logger;

public class ConfidenceIntervalCalculatorVar {

    public ConfidenceIntervalCalculatorVar(Byte intervalID, HPnGModelVar model, Double time, Integer minNumberOfRuns, Logger logger, SimpleNode root, Double confidenceLevel, Double halfIntervalWidth) throws InvalidPropertyException {

        this.minNumberOfRuns = minNumberOfRuns;
        this.halfIntervalWidth = halfIntervalWidth;
        this.intervalID = intervalID;

        checker = new PropertyCheckerVar(root, model, time);
        checker.setLogger(logger);

//TODO anpassen an weitere Konfidenzintervalle
/*        switch (intervalID) {

            case 0:*/
                interval = new StandardConfidenceIntervalVar(minNumberOfRuns, confidenceLevel, halfIntervalWidth);
                /*break;

            case 1:
                interval = new WaldConfidenceInterval(confidenceLevel);
                break;

            case 2:
                interval = new ClopperPearsonConfidenceInterval(confidenceLevel);
                break;

            case 3:
                interval = new ScoreConfidenceInterval(confidenceLevel);
                break;

            case 4:
                interval = new AdjustedWaldConfidenceInterval();
                break;
        }*/


    }


    public Double getMidpoint() {
        return midpoint;
    }

    public PropertyCheckerVar getChecker() {
        return checker;
    }


    private Integer numberOfRuns = 0;
    private Integer minNumberOfRuns;
    private Double midpoint;
    private PropertyCheckerVar checker;
    private Double halfIntervalWidth;
    private Double currentHalfIntervalWidth;
    private Byte intervalID;
    private ConfidenceIntervalVar interval;


    public void calculateConfidenceInterval(Integer currentRun, MarkingPlotVar plot) throws InvalidPropertyException {

        numberOfRuns = interval.calculateMidpointAndHalfIntervalWidthForProperty(checker, currentRun, plot);
        midpoint = interval.getMidpoint();
        currentHalfIntervalWidth = interval.getCurrentHalfIntervalWidth();

    }

    public Boolean checkBound() {

        if (numberOfRuns < minNumberOfRuns)
            return false;


        if (intervalID == 0)
            return ((StandardConfidenceIntervalVar) interval).checkBound();


        return (currentHalfIntervalWidth <= halfIntervalWidth);
    }

    public Double getLowerBorder() {
        return Math.max(0.0, (midpoint - currentHalfIntervalWidth));
    }

    public Double getUpperBorder() {
        return Math.min(1.0, (midpoint + currentHalfIntervalWidth));
    }

    public void resetResults() {
        numberOfRuns = 0;
        currentHalfIntervalWidth = 0.0;
        midpoint = 0.0;
    }


}
