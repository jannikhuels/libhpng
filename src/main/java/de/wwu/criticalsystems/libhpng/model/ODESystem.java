package de.wwu.criticalsystems.libhpng.model;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.HashMap;

import static javax.sound.midi.ShortMessage.STOP;


public class ODESystem implements FirstOrderDifferentialEquations {

    protected HPnGModelVar model;
    private ArrayList<Expression> ode = new ArrayList<Expression>();
    private HashMap<String, Integer> idToInt = new HashMap<String, Integer>();

    public ODESystem(HPnGModelVar model) {
        this.model = model;
        createMapIDtoInt();
        updateODESystem();
    }

    /**
     * constructs the current system of odes describing the evolution of the continuous places
     */
    public void updateODESystem() {
        ode.clear();
        int counter = 0;
        for (Place p : model.getPlaces()) {
            if (p.getClass().equals(ContinuousPlaceVar.class)) {
                Expression test = ((ContinuousPlaceVar) p).getCurrentDrift();
                ode.add(counter, test);
                counter++;
            }
        }
    }


    /**
     * for the ODE the ID of the continuous place needs to be assignable to the number in the ode array
     */
    public void createMapIDtoInt() {
        int counter = 0;
        for (Place p : model.getPlaces()) {
            if (p.getClass().equals(ContinuousPlaceVar.class)) {

                idToInt.put(p.getId(), counter);
                counter++;
            }
        }

    }

    public double[] getCurrentFluidLevels() {
        int counter = 0;
        double[] currentFluidlevels = new double[ode.size()];
        for (Place p : model.getPlaces()) {
            if (p.getClass().equals(ContinuousPlaceVar.class)) {
                currentFluidlevels[counter] = ((ContinuousPlaceVar) p).getCurrentFluidLevel();
                counter++;
            }
        }
        return currentFluidlevels;
    }


    @Override
    public int getDimension() {
        return ode.size();
    }

    @Override
    public void computeDerivatives(double t, double[] x, double[] xDot) throws MaxCountExceededException, DimensionMismatchException {
        for (int i = 0; i < ode.size(); i++) {
            ode.get(i).removeAllArguments();
            String[] missing = ode.get(i).getMissingUserDefinedArguments();
            for (int j = 0; j < missing.length; j++) {
                for (Place place : model.getPlaces()) {
                    if (place.getClass().equals(ContinuousPlaceVar.class) && (place.getId().equals(missing[j]) || ("delta_" + place.getId()).equals(missing[j]))) {

                        //TODO hier muss evtl noch ein delta_ dazu - ziemlich sicher nicht
                        ode.get(i).addArguments(new Argument(missing[j], x[idToInt.get(missing[j])]));
                        break;
                    }

                }
            }
            xDot[i] = ode.get(i).calculate();
//            System.out.println(xDot[i]);
        }
    }


    public HashMap<String, Integer> getIdToInt() {
        return idToInt;
    }

    public HPnGModelVar getModel() {
        return model;
    }
}
