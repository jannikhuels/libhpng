package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.XmlAttribute;

import org.mariuszgromada.math.mxparser.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;


public class ContinuousPlaceVar extends Place {
    public ContinuousPlaceVar() {
    }

    public ContinuousPlaceVar(String id, Double originalFluidLevel, Double upperBoundary,
                              Expression drift, Boolean upperBoundaryInfinity) {

        super(id);
        this.originalFluidLevel = originalFluidLevel;
        this.upperBoundary = upperBoundary;
        this.drift = drift;
        this.upperBoundaryInfinity = upperBoundaryInfinity;
    }


    public ContinuousPlaceVar(ContinuousPlaceVar placeToCopy) {

        super(new String(placeToCopy.getId()));
        this.originalFluidLevel = new Double(placeToCopy.getOriginalFluidLevel());
        this.currentFluidLevel = new Double(placeToCopy.getCurrentFluidLevel());
        this.upperBoundary = new Double(placeToCopy.getUpperBoundary());
        this.upperBoundaryInfinity = new Boolean(placeToCopy.getUpperBoundaryInfinity());
        this.upperBoundaryReached = new Boolean(placeToCopy.getUpperBoundaryReached());
        this.lowerBoundaryReached = new Boolean(placeToCopy.lowerBoundaryReached);
        this.drift = placeToCopy.getDrift();
        this.lastUpdate = new Double(placeToCopy.getLastUpdate());
    }

    private Double currentFluidLevel;
    private Double originalFluidLevel;
    private Double upperBoundary = Double.POSITIVE_INFINITY;
    //    private Double approximationdrift = 0.0;
    private Boolean upperBoundaryInfinity;
    private Boolean upperBoundaryReached;
    private Boolean lowerBoundaryReached;
    private Double quantum;
    private Double timeToNextInternalTransition;
    private Double lastUpdate = 0.0;
    private Expression currentDrift = new Expression("");
    private Expression drift = new Expression("");
    private Double driftValue = 0.0;
    private Expression rateAdaptionCondition= new Expression("");


    public Expression getCurrentDrift() {
        return currentDrift;
    }


    public void setCurrentDriftFromString(String currentDriftString) {
        this.currentDrift.setExpressionString(currentDriftString);
    }

    public void setRateAdaptionConditionFromString(String condition) {
        this.rateAdaptionCondition.setExpressionString(condition);
    }

    public Expression getRateAdaptionCondition() {
        return rateAdaptionCondition;
    }


    public Double getDriftValue() {
        return driftValue;
    }


    public void setDriftValue(Double driftValue) {
        this.driftValue = driftValue;
    }


    public Double getOriginalFluidLevel() {
        return originalFluidLevel;
    }

    @XmlAttribute(name = "level")
    public void setOriginalFluidLevel(Double fluidLevel) {
        this.originalFluidLevel = fluidLevel;
    }

    public Double getCurrentFluidLevel() {
        return currentFluidLevel;
    }

    public void setCurrentFluidLevel(Double fluidLevel) {
        this.currentFluidLevel = fluidLevel;
    }

    public Double getUpperBoundary() {
        return upperBoundary;
    }

    @XmlAttribute(name = "capacity")
    public void setUpperBoundary(Double upperBoundary) {
        this.upperBoundary = upperBoundary;
    }

    public Expression getDrift() {
        return drift;
    }

    public void setDrift(Expression drift) {
        this.drift = drift;
    }

    public void setDriftFromString(String expressionString) {
        this.drift.clearExpressionString();
        this.drift.setExpressionString(expressionString);
    }

    public Double getExactFluidLevel() {
        //TODO: Expression muss mit aktuellen Werten ausgewertet gefüllt werden
        return currentFluidLevel;
    }


    public Boolean getUpperBoundaryInfinity() {
        return upperBoundaryInfinity;
    }

    @XmlAttribute(name = "infiniteCapacity")
    public void setUpperBoundaryInfinity(Boolean upperBoundaryInfinity) {
        this.upperBoundaryInfinity = upperBoundaryInfinity;
    }

    public Boolean getUpperBoundaryReached() {
        return upperBoundaryReached;
    }

    public Boolean getLowerBoundaryReached() {
        return lowerBoundaryReached;
    }


    public Double getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Double lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public void resetFluidLevel() {
        this.currentFluidLevel = this.originalFluidLevel;
    }


    public Boolean checkUpperBoundary() {

        BigDecimal level = new BigDecimal(currentFluidLevel);
        level = level.setScale(14, BigDecimal.ROUND_UP);

        if (!upperBoundaryInfinity && level.doubleValue() >= upperBoundary)
            upperBoundaryReached = true;
        else
            upperBoundaryReached = false;
        return upperBoundaryReached;
    }


    public Boolean checkLowerBoundary() {

        BigDecimal level = new BigDecimal(currentFluidLevel);
        level = level.setScale(14, BigDecimal.ROUND_DOWN);

        if (level.doubleValue() <= 0.0)
            lowerBoundaryReached = true;
        else
            lowerBoundaryReached = false;
        return lowerBoundaryReached;
    }


}
