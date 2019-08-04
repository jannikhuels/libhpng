package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.model.ContinuousPlaceVar;
import de.wwu.criticalsystems.libhpng.model.HPnGModelVar;
import de.wwu.criticalsystems.libhpng.model.ODESystem;
import de.wwu.criticalsystems.libhpng.model.Place;
import de.wwu.criticalsystems.libhpng.plotting.MarkingPlotVar;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import java.util.Arrays;

public class ODEStepHandler implements StepHandler {
    private int counter = 0;
    ODESystem odeSystem;
    MarkingPlotVar plot;

    public ODEStepHandler(ODESystem odeSystem, MarkingPlotVar plot) {
        this.odeSystem = odeSystem;
        this.plot = plot;
    }

    @Override
    public void init(double v, double[] doubles, double v1) {

    }


    @Override
    public void handleStep(StepInterpolator stepInterpolator, boolean b) throws MaxCountExceededException {
        double t = stepInterpolator.getCurrentTime();
        double[] y = stepInterpolator.getInterpolatedState();
        double[] drifts = stepInterpolator.getInterpolatedDerivatives();
        int counter = 0;
        for (Place place : odeSystem.getModel().getPlaces()) {
            if (place.getClass().equals(ContinuousPlaceVar.class)) {
                ((ContinuousPlaceVar) place).setCurrentFluidLevel(y[odeSystem.getIdToInt().get(place.getId())]);
                ((ContinuousPlaceVar) place).setDriftValue(drifts[odeSystem.getIdToInt().get(place.getId())]);
                ((ContinuousPlaceVar) place).setLastUpdate(t);
            }
        }
        plot.saveAll(t);
        counter++;
//        System.out.println("time: "+t+", a: " + y[0]+", b: "+ y[1]);
//        System.out.println("t: "+t+"fluid: "+ Arrays.toString(y)+"drifts: "+Arrays.toString(drifts)+ " counter: "+counter);

    }
}
