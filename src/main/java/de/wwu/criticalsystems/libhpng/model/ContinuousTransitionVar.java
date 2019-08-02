package de.wwu.criticalsystems.libhpng.model;

import org.mariuszgromada.math.mxparser.Expression;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name = "continuousTransition")
@XmlSeeAlso({Transition.class})
public class ContinuousTransitionVar extends FluidTransition {

    public ContinuousTransitionVar() {
    }

    public ContinuousTransitionVar(String id, Boolean enabled, String fluidRate) {
        super(id, enabled, fluidRate);
    }

    public ContinuousTransitionVar(ContinuousTransitionVar currentTransitionToCopy) {
        super(new String(currentTransitionToCopy.getId()), new Boolean(currentTransitionToCopy.getEnabled()), currentTransitionToCopy.getRateExpression().getExpressionString());
    }


    @XmlAttribute(name = "rate")
    public void setRateExpressionFromString(String rateExpressionString) {
        rateExpression.setExpressionString(rateExpressionString);
        setCurrentRateExpressionFromString(rateExpressionString);
    }

}
