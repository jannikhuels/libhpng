package de.wwu.criticalsystems.libhpng.model;

import org.mariuszgromada.math.mxparser.Expression;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.HashMap;

public abstract class FluidTransition extends Transition {

    public FluidTransition() {
    }

    public FluidTransition(String id, Boolean enabled, String rateExpression) {
        super(id, enabled);
        this.rateExpression.setExpressionString(rateExpression);
        currentRateExpression.setExpressionString(rateExpression);
    }


    protected Expression rateExpression = new Expression("0");
    protected Expression currentRateExpression = new Expression("0");
    protected HashMap<String, String> rateAdapationEvents = new HashMap<>();

    public void setRateExpressionFromString(String rateExpressionString) {
        rateExpression.setExpressionString(rateExpressionString);
        setCurrentRateExpressionFromString(rateExpressionString);
    }

    public void setCurrentRateExpressionFromString(String currentRateExpressionString) {
        currentRateExpression.setExpressionString(currentRateExpressionString);
    }

    public Expression getRateExpression() {
        return rateExpression;
    }

    public Expression getCurrentRateExpression() {
        return currentRateExpression;
    }

    public void resetCurrentRateExpression() {
        currentRateExpression = new Expression(rateExpression.getExpressionString());
    }

    public HashMap<String, String> getRateAdapationEvents() {
        return rateAdapationEvents;
    }

    public void addRateAdapationEvent(String placeID, String condition) {
        rateAdapationEvents.put(placeID, condition);
    }

    public void resetRateAdaptionEvents() {
        rateAdapationEvents.clear();
    }
}
