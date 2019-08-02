package de.wwu.criticalsystems.libhpng.model;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidModelConnectionException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;
import de.wwu.criticalsystems.libhpng.model.ContinuousArc.ContinuousArcType;
import de.wwu.criticalsystems.libhpng.model.DiscreteArc.DiscreteArcType;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;


@XmlRootElement(name = "HPnG")
public class HPnGModelVar {

    public HPnGModelVar() {
    }

    public HPnGModelVar(HPnGModelVar modelToCopy, Logger logger) throws ModelCopyingFailedException, InvalidModelConnectionException {

        for (Place currentPlaceToCopy : modelToCopy.getPlaces()) {

            if (currentPlaceToCopy.getClass().equals(DiscretePlace.class))
                places.add(new DiscretePlace((DiscretePlace) currentPlaceToCopy));
            else if (currentPlaceToCopy.getClass().equals(ContinuousPlaceVar.class))
                places.add(new ContinuousPlaceVar((ContinuousPlaceVar) currentPlaceToCopy));
        }


        for (Transition currentTransitionToCopy : modelToCopy.getTransitions()) {

            if (currentTransitionToCopy.getClass().equals(DeterministicTransition.class))
                transitions.add(new DeterministicTransition((DeterministicTransition) currentTransitionToCopy));
            else if (currentTransitionToCopy.getClass().equals(ImmediateTransition.class))
                transitions.add(new ImmediateTransition((ImmediateTransition) currentTransitionToCopy));
            else if (currentTransitionToCopy.getClass().equals(GeneralTransition.class))
                transitions.add(new GeneralTransition((GeneralTransition) currentTransitionToCopy));
            else if (currentTransitionToCopy.getClass().equals(ContinuousTransition.class))
                transitions.add(new ContinuousTransitionVar((ContinuousTransitionVar) currentTransitionToCopy));
        }

        for (Transition currentTransitionToCopy : modelToCopy.getTransitions()) {

            if (currentTransitionToCopy.getClass().equals(DynamicContinuousTransition.class))
                transitions.add(new DynamicContinuousTransition((DynamicContinuousTransition) currentTransitionToCopy, transitions));
        }


        for (Arc currentArcToCopy : modelToCopy.getArcs()) {

            if (currentArcToCopy.getClass().equals(DiscreteArc.class))
                arcs.add(new DiscreteArc((DiscreteArc) currentArcToCopy));
            else if (currentArcToCopy.getClass().equals(ContinuousArc.class))
                arcs.add(new ContinuousArc((ContinuousArc) currentArcToCopy));
            else if (currentArcToCopy.getClass().equals(GuardArc.class))
                arcs.add(new GuardArc((GuardArc) currentArcToCopy));
        }

        //initialize model
        setConnectedPlacesAndTransitions(logger);
        sortLists(logger);

    }


    public ArrayList<Place> getPlaces() {
        return places;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public ArrayList<Arc> getArcs() {
        return arcs;
    }

    @XmlElementWrapper(name = "places")
    @XmlElements({
            @XmlElement(name = "discretePlace", type = DiscretePlace.class),
            @XmlElement(name = "continuousPlace", type = ContinuousPlaceVar.class),
    })
    private ArrayList<Place> places = new ArrayList<Place>();

    @XmlElementWrapper(name = "transitions")
    @XmlElements({
            @XmlElement(name = "deterministicTransition", type = DeterministicTransition.class),
            @XmlElement(name = "continuousTransition", type = ContinuousTransitionVar.class),
            @XmlElement(name = "generalTransition", type = GeneralTransition.class),
            @XmlElement(name = "immediateTransition", type = ImmediateTransition.class),
            @XmlElement(name = "dynamicContinuousTransition", type = DynamicContinuousTransitionVar.class),
    })
    private ArrayList<Transition> transitions = new ArrayList<Transition>();

    @XmlElementWrapper(name = "arcs")
    @XmlElements({
            @XmlElement(name = "continuousArc", type = ContinuousArc.class),
            @XmlElement(name = "discreteArc", type = DiscreteArc.class),
            @XmlElement(name = "guardArc", type = GuardArc.class),
    })
    private ArrayList<Arc> arcs = new ArrayList<Arc>();


    //resets all marking-related properties of the model
    public void resetMarking() {

        setClockValuesToZero();
        setOriginalFluidLevelsAndTokens();
        checkAllGuardArcs();
        try {
            updateEnabling(true);
        } catch (InvalidRandomVariateGeneratorException e) {
        }

        updateFluidRates(false);
//        setDynamicContinuousTransitionsBack();
        computeInitialInternalTransitions();
    }


    public void setConnectedPlacesAndTransitions(Logger logger) throws InvalidModelConnectionException {

        for (Arc arc : arcs) {
            Boolean fromNodeFound = false;
            Boolean toNodeFound = false;

            //search for place id
            for (Place place : places) {

                if (!fromNodeFound && place.getId().equals(arc.getFromNode())) {

                    fromNodeFound = true;
                    arc.setConnectedPlace(place);
                    if (arc.getClass().equals(ContinuousArc.class)) {
                        ContinuousArcType dir = ContinuousArcType.output;
                        ((ContinuousArc) arc).setDirection(dir);
                    } else if (arc.getClass().equals(DiscreteArc.class)) {
                        DiscreteArcType dir = DiscreteArcType.output;
                        ((DiscreteArc) arc).setDirection(dir);
                    }
                    break;

                } else if (!toNodeFound && place.getId().equals(arc.getToNode())) {

                    toNodeFound = true;
                    arc.setConnectedPlace(place);
                    if (arc.getClass().equals(ContinuousArc.class)) {
                        ContinuousArcType dir = ContinuousArcType.input;
                        ((ContinuousArc) arc).setDirection(dir);
                    } else if (arc.getClass().equals(DiscreteArc.class)) {
                        DiscreteArcType dir = DiscreteArcType.input;
                        ((DiscreteArc) arc).setDirection(dir);
                    }
                    break;
                }
            }

            //search for transition id
            for (Transition transition : transitions) {
                if (!fromNodeFound && transition.getId().equals(arc.getFromNode())) {

                    fromNodeFound = true;
                    arc.setConnectedTransition(transition);
                    transition.getConnectedArcs().add(arc);
                    break;

                } else if (!toNodeFound && transition.getId().equals(arc.getToNode())) {

                    toNodeFound = true;
                    arc.setConnectedTransition(transition);
                    transition.getConnectedArcs().add(arc);
                    break;
                }
            }

            if (!fromNodeFound) {
                if (logger != null)
                    logger.severe("Model error: 'fromNode' for arc " + arc.getId() + " could not be matched to any place or transition");
                throw new InvalidModelConnectionException("'fromNode' for arc '" + arc.getId() + "' could not be matched to any place or transition");
            }
            if (!toNodeFound) {
                if (logger != null)
                    logger.severe("Model error: 'toNode' for arc '" + arc.getId() + "' could not be matched to any place or transition");
                throw new InvalidModelConnectionException("'toNode' for arc " + arc.getId() + " could not be matched to any place or transition");
            }

        }
        if (logger != null) logger.info("Model object connections were set successfully.");

    }

    public void sortLists(Logger logger) {

        Collections.sort(places, new PlaceComparator());
        Collections.sort(transitions, new TransitionComparator());
        Collections.sort(arcs, new ArcComparator());

        for (Transition transition : transitions)
            Collections.sort(transition.getConnectedArcs(), new ArcComparatorForTransitions());

        if (logger != null) logger.info("Model lists sorted successfully.");
    }


    //updates enabling status for all transitions, but does not include a new check of guard arc conditions
    public void updateEnabling(Boolean reset) throws InvalidRandomVariateGeneratorException {

        for (Transition transition : transitions) {
            Boolean enabled = true;

            for (Arc arc : transition.getConnectedArcs()) {

                if (arc.getClass().equals(GuardArc.class)) {
                    if ((!((GuardArc) arc).getInhibitor() && !((GuardArc) arc).getConditionFulfilled()) || (((GuardArc) arc).getInhibitor() && ((GuardArc) arc).getConditionFulfilled()))
                        enabled = false;
                    continue;
                }

                if (!transition.getClass().equals(ContinuousTransitionVar.class) && !transition.getClass().equals(DynamicContinuousTransitionVar.class)) {

                    if (((DiscreteArc) arc).getDirection().equals(DiscreteArcType.input))
                        continue;

                    if (((DiscretePlace) arc.getConnectedPlace()).getNumberOfTokens() < arc.getWeight())
                        enabled = false;
                }
            }

            if (!reset && enabled && transition.getClass().equals(GeneralTransition.class) && !transition.getEnabled())
                ((GeneralTransition) transition).enableByPolicy(reset);


            transition.setEnabled(enabled);
        }
    }


    public void checkAllGuardArcs() {
        for (Arc arc : arcs) {
            if (arc.getClass().equals(GuardArc.class)) {
                ((GuardArc) arc).checkCondition();
            } else
                break;
        }
    }


    public void checkGuardArcsForDiscretePlaces() {
        for (Arc arc : arcs) {
            if (arc.getClass().equals(GuardArc.class) && arc.getConnectedPlace().getClass().equals(DiscretePlace.class)) {
                ((GuardArc) arc).checkCondition();
            } else if (!arc.getClass().equals(GuardArc.class))
                break;
        }
    }


    public void updateFluidRates(boolean boundaryHit) {


        //check borders
        String inFlux;
        String outFlux;
        Boolean change = true;
        ContinuousPlaceVar p;
        String currentTransitionDrift;
        Expression placedrift = new Expression("0");
        String currentExpression;

        resetCurrentTransitionRates();
        updatePlaceDrift();


        while (change) {

            change = false;

            for (Place place : places) {
                placedrift.clearExpressionString();
                placedrift.setExpressionString("0");
                if (place.getClass().equals(ContinuousPlaceVar.class)) {

                    inFlux = "0";
                    outFlux = "0";

                    p = (ContinuousPlaceVar) place;


                    for (Arc arc : arcs) {
                        if (arc.getConnectedPlace().getId().equals(place.getId()) && !arc.getClass().equals(GuardArc.class)) {
                            if (arc.getConnectedTransition().getEnabled()) {

                                if (((ContinuousArc) arc).getDirection().equals(ContinuousArcType.input)) {

                                    if (arc.getConnectedTransition().getClass().equals(ContinuousTransitionVar.class)) {
                                        currentExpression = placedrift.getExpressionString();
                                        currentTransitionDrift = "("+((ContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + arc.getWeight();
                                        inFlux = inFlux + "+" + currentTransitionDrift;
                                        placedrift.setExpressionString(currentExpression + "+" + currentTransitionDrift);
                                    } else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {
                                        currentExpression = placedrift.getExpressionString();
                                        currentTransitionDrift = "("+(((DynamicContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression()).getExpressionString() + ")*" + arc.getWeight();
                                        inFlux = inFlux + "+" + currentTransitionDrift;
                                        placedrift.setExpressionString(currentExpression + "+" + currentTransitionDrift);
//										placedrift.addDefinitions(((DynamicContinuousTransition)arc.getConnectedTransition()).getFluidExpression()+"");//* arc.getWeight());
                                    }

                                } else {
                                    if (arc.getConnectedTransition().getClass().equals(ContinuousTransitionVar.class)) {
//										outFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid() * arc.getWeight();
                                        currentExpression = placedrift.getExpressionString();
                                        currentTransitionDrift = "("+((ContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + arc.getWeight();
                                        outFlux = outFlux + "+" + currentTransitionDrift;
                                        placedrift.setExpressionString(currentExpression + "-(" + currentTransitionDrift + ")");
                                    } else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {
                                        currentExpression = placedrift.getExpressionString();
                                        currentTransitionDrift ="("+ (((DynamicContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression()).getExpressionString() + ")*" + arc.getWeight();
                                        outFlux = outFlux + "+" + currentTransitionDrift;
                                        placedrift.setExpressionString(currentExpression + "-(" + currentTransitionDrift + ")");
                                    }
                                }
                            }
                        }
                    }
//					if (Double.isNaN(placedrift.calculate()))
//						placedrift.setExpressionString("0");
                    System.out.println(placedrift.getExpressionString() + "ausgewertet" + computeCurrentExpressionValue(placedrift));
                    System.out.println("influx " + inFlux + ", ausgewertet: " + computeCurrentExpressionValue(new Expression(inFlux)));
                    System.out.println("outflux " + outFlux + ", ausgewertet: " + computeCurrentExpressionValue(new Expression(outFlux)));
                    p.setCurrentDriftFromString(placedrift.getExpressionString());
                    //TODO ordne placedrift aktuelle werte zu +1e-11
                    if ((computeCurrentExpressionValue(placedrift)+1e-11< 0 && p.getCurrentFluidLevel() <= 0.0) || boundaryHit) {
                        rateAdaption(p, ContinuousArcType.output, new Expression(inFlux));
                        change = true;
//                        p.setCurrentDriftFromString("0");
                    } else if (placedrift.calculate() > 0 && p.getCurrentFluidLevel() >= p.getUpperBoundary() && !p.getUpperBoundaryInfinity() || boundaryHit) {
                        rateAdaption(p, ContinuousArcType.input, new Expression(outFlux));
                        change = true;
                    }


//                    if ((inFlux > outFlux) && p.getCurrentFluidLevel() >= p.getUpperBoundary() && !p.getUpperBoundaryInfinity()) {
//                        //if upper boundary reached
//                        Double timeToNextPriority = rateAdaption((ContinuousPlaceVar) place, outFlux, ContinuousArcType.input, changeOfOutFlux);
//                        p.setExactDrift(0.0);
//                        p.setChangeOfExactDrift(0.0);
//
//
//                        change = true;
//
//                    } else if ((outFlux > inFlux) && p.getCurrentFluidLevel() <= 0.0) {
//                        //lower boundary reached
//                        Double timeToNextPriority = rateAdaption((ContinuousPlaceVar) place, inFlux, ContinuousArcType.output, changeOfInFlux);
//
//                        p.setExactDrift(0.0);
//                        p.setChangeOfExactDrift(0.0);
//
//                        if (timeToNextPriority < Double.POSITIVE_INFINITY)
//                            p.setTimeToNextInternalTransition(Math.min(p.getTimeToNextInternalTransition(), timeToNextPriority));
//
//                        change = true;
//
//                    } else
//                        updateDynamicRates();
//
//
//                    if (oldDrift != null && oldChangeOfDrift != null && (Math.abs(p.getExactDrift() - oldDrift) >= 0.000001 || Math.abs(p.getChangeOfExactDrift() - oldChangeOfDrift) >= 0.000001)) {
//                        //p.performInternalTransition(timePoint, oldDrift, oldChangeOfDrift, arcs);
//                        //System.out.println(timePoint + " seconds: continuous place " + place.getId() + " has performed an internal transition --1");
//                    }

                } else
                    continue;
            }
            boundaryHit = false;
        }


    }


    public void advanceMarking(Double timeDelta) {


        for (Transition transition : transitions) {
            if (transition.getClass().equals(DeterministicTransition.class) && transition.getEnabled()) {
                ((DeterministicTransition) transition).setClock(((DeterministicTransition) transition).getClock() + timeDelta);
            } else if (transition.getClass().equals(GeneralTransition.class) && transition.getEnabled()) {
                ((GeneralTransition) transition).setEnablingTime(((GeneralTransition) transition).getEnablingTime() + timeDelta);
            }
        }
        checkAllGuardArcs();
    }

    public void printCurrentMarking(Boolean initial, Boolean last) {

        if (initial)
            System.out.print("Initial marking:");
        else if (last)
            System.out.print("Final marking:  ");
        else
            System.out.print("Current marking:");


        for (Place place : places) {
            if (place.getClass().equals(ContinuousPlaceVar.class))
                System.out.print("    " + place.getId() + ": " + ((ContinuousPlaceVar) place).getCurrentFluidLevel() + " (" + ((ContinuousPlaceVar) place).getDrift() + ")");
            else
                System.out.print("    " + place.getId() + ": " + ((DiscretePlace) place).getNumberOfTokens());
        }
        System.out.println();
        System.out.println();
    }


    public int[][] getArrayOfNumberOfComponents() {

        int[][] numbers = new int[3][5];
        Arrays.fill(numbers[0], 0);
        Arrays.fill(numbers[1], 0);
        Arrays.fill(numbers[2], 0);

        int counter1 = 0;
        int counter2 = 0;
        int counter3 = 0;
        int counter4 = 0;
        int counter5 = 0;


        for (Place place : places) {
            if (place.getClass().equals(DiscretePlace.class))
                counter1++;
            else if (place.getClass().equals(ContinuousPlaceVar.class))
                counter2++;
        }
        numbers[0][0] = counter1;
        numbers[0][1] = counter2;

        counter1 = 0;
        counter2 = 0;


        for (Transition transition : transitions) {
            if (transition.getClass().equals(GeneralTransition.class))
                counter1++;
            else if (transition.getClass().equals(ImmediateTransition.class))
                counter2++;
            else if (transition.getClass().equals(DeterministicTransition.class))
                counter3++;
            else if (transition.getClass().equals(ContinuousTransition.class))
                counter4++;
            else if (transition.getClass().equals(DynamicContinuousTransition.class))
                counter5++;
        }
        numbers[1][0] = counter1;
        numbers[1][1] = counter2;
        numbers[1][2] = counter3;
        numbers[1][3] = counter4;
        numbers[1][4] = counter5;

        counter1 = 0;
        counter2 = 0;
        counter3 = 0;


        for (Arc arc : arcs) {
            if (arc.getClass().equals(DiscreteArc.class))
                counter1++;
            else if (arc.getClass().equals(ContinuousArc.class))
                counter2++;
            else if (arc.getClass().equals(GuardArc.class))
                counter3++;
        }
        numbers[2][0] = counter1;
        numbers[2][1] = counter2;
        numbers[2][2] = counter3;

        return numbers;
    }


    private void rateAdaption(ContinuousPlaceVar place, ContinuousArcType direction, Expression availableFlow) {

        Integer arcIndex = 0;
        Integer arcIndex2;
        Integer currentPriority = 0;
//        String sum = "0";
        ArrayList<ContinuousArc> priorityArcs = new ArrayList<ContinuousArc>();
        Integer highestPriority;
        Integer lowestPriority;
        Boolean enoughForCurrentPriority = false;
        String remainingFlow = availableFlow.getExpressionString();
        String requiredFlow;
        String sumString;
        String currentArcFlow;
        String sharedFluid;
//        String currentPriorityActualFlow = "0";

        double remainingFlowValue;
        int i = 0;
        while (!arcs.get(i).getClass().equals(ContinuousArc.class))
            i++;
        highestPriority = ((ContinuousArc) arcs.get(i)).getPriority();
        lowestPriority = highestPriority;
        while (arcIndex < arcs.size()) {
            if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) && arcs.get(arcIndex).getClass().equals(ContinuousArc.class)
                    && ((ContinuousArc) arcs.get(arcIndex)).getDirection().equals(direction)) {

                currentPriority = ((ContinuousArc) arcs.get(arcIndex)).getPriority();
                enoughForCurrentPriority = false;
                requiredFlow = "0";
                sumString = "0";
                priorityArcs.clear();

                //sum up required flux for current priority
                for (arcIndex2 = arcIndex; arcIndex2 < arcs.size(); arcIndex2++) {
                    if (arcs.get(arcIndex2).getClass().equals(ContinuousArc.class)) {
                        ContinuousArc currentArc = (ContinuousArc) arcs.get(arcIndex2);

                        if (currentArc.getConnectedPlace().getId().equals(place.getId()) && currentArc.getConnectedTransition().getEnabled()
                                && currentArc.getPriority().equals(currentPriority) && currentArc.getDirection().equals(direction)) {

                            if (currentArc.getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {
//                                fluxRequired += ((DynamicContinuousTransition) currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getWeight();
//                                changeOfFluxRequired += ((DynamicContinuousTransition) currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getWeight();
//                                sum += ((DynamicContinuousTransition) currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getShare() * currentArc.getWeight();
                                //changeOfSum += (((DynamicContinuousTransition)currentArc.getConnectedTransition()).getCurrentChangeOfFluid()*currentArc.getShare()) * currentArc.getWeight();
//                                ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).setAdapted(false);

                                currentArcFlow = "(" + ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getWeight();
                                requiredFlow = requiredFlow + "+" + currentArcFlow;
                                sumString = sumString + "+(" + currentArcFlow + ")*" + currentArc.getShare();

                            } else {
//                                fluxRequired += ((ContinuousTransition) currentArc.getConnectedTransition()).getFluidRate();
//                                sum += (((ContinuousTransition) currentArc.getConnectedTransition()).getFluidRate() * currentArc.getShare());
                                currentArcFlow = "(" + ((ContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getWeight();
                                requiredFlow = requiredFlow + "+" + currentArcFlow;
                                sumString = sumString + "+(" + currentArcFlow + ")*" + currentArc.getShare();
                            }

                            priorityArcs.add(currentArc);

                        } else if (currentArc.getPriority() < currentPriority)
                            break;
                    } else
                        break;
                }

                //compute current flow to check whether the priority can be fulfilled at the moment
                remainingFlowValue = computeCurrentExpressionValue(new Expression(remainingFlow));
                double currentRequiredFlow = computeCurrentExpressionValue(new Expression(requiredFlow));
//TODO fall gleich?
                if (currentRequiredFlow <= remainingFlowValue) {
                    //subtract the distributed flow
                    remainingFlowValue -= currentRequiredFlow;
                    remainingFlow = remainingFlow + "-(" + requiredFlow + ")";
                    arcIndex = arcIndex2;
                    enoughForCurrentPriority = true;
                    //TODO EVent wenn grenze gebrochen wird dh required>remaining
                    place.setRateAdaptionConditionFromString(remainingFlow + "-(" + requiredFlow + ")");
                    continue;
                }


                //if enough flux for current priority, subtract
//                if (fluxRequired < flux) {
//                    flux = -fluxRequired;
//                    changeOfFlux = -changeOfFluxRequired;
//                    arcIndex = arcIndex2;
//                    enoughForCurrentPriority = true;
//                    accumulatedFluxRequired += fluxRequired;
//                    accumulatedChangeOfFluxRequired += changeOfFluxRequired;
//                    continue;
//                }
                double currentSumValue = computeCurrentExpressionValue(new Expression(sumString));

                //otherwise: share remaining flux
                if (priorityArcs.size() == 1 && priorityArcs.get(0).getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {
                    String oldCurrentRate = ((DynamicContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).getCurrentRateExpression().getExpressionString();
                    ((DynamicContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).setCurrentRateExpressionFromString("(" + remainingFlow + ")/" + priorityArcs.get(0).getWeight());
                    ((DynamicContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).setAdapted(true);
                    ((DynamicContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).addRateAdapationEvent(place.getId(), "(" + remainingFlow + ")/" + priorityArcs.get(0).getWeight() + "-(" + oldCurrentRate + ")");
                } else if (priorityArcs.size() == 1) {
                    String oldCurrentRate = ((ContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).getCurrentRateExpression().getExpressionString();
                    ((ContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).setCurrentRateExpressionFromString(remainingFlow + "/" + priorityArcs.get(0).getWeight());
                    ((ContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).addRateAdapationEvent(place.getId(), "(" + remainingFlow + ")/" + priorityArcs.get(0).getWeight() + "-(" + oldCurrentRate + ")");
                } else {
                    boolean change = true;
                    while (change) {
                        change = false;
                        for (ContinuousArc currentArc : priorityArcs) {

                            if (currentArc.getConnectedTransition().getClass().equals(DynamicContinuousTransition.class)) {

                                if (currentArc.getShare() / currentSumValue >= 1 / remainingFlowValue) {
                                    String currentRate = "(" + ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getWeight();
                                    double currentRateValue = computeCurrentExpressionValue(((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression()) * currentArc.getWeight();
                                    currentSumValue -= currentRateValue * currentArc.getShare();
                                    remainingFlowValue -= currentRateValue;
                                    sumString = sumString + "-((" + currentRate + ")*" + currentArc.getShare() + ")";
                                    remainingFlow = remainingFlow + "-(" + currentRate + ")";
                                    change = true;
                                    priorityArcs.remove(currentArc);
                                    break;
                                }


                            } else {

                                if (currentArc.getShare() / currentSumValue >= 1 / remainingFlowValue) {
                                    String currentRate = "(" + ((ContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getWeight();
//                                currentPriorityActualFlow = currentPriorityActualFlow + "+" + ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + "*" + currentArc.getWeight();
                                    double currentRateValue = computeCurrentExpressionValue(((ContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression()) * currentArc.getWeight();
                                    currentSumValue -= currentRateValue * currentArc.getShare();
                                    remainingFlowValue -= currentRateValue;
                                    sumString = sumString + "-((" + currentRate + ")*" + currentArc.getShare() + ")";
                                    remainingFlow = remainingFlow + "-(" + currentRate + ")";
                                    change = true;
                                    priorityArcs.remove(currentArc);
                                    break;
                                }
                            }

                        }
                    }

                    for (ContinuousArc currentArc : priorityArcs) {

                        if (currentArc.getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {

                            if (currentArc.getShare() / currentSumValue < 1 / remainingFlowValue) {
//                                String oldCurrentRate = ((DynamicContinuousTransitionVar) priorityArcs.get(0).getConnectedTransition()).getCurrentRateExpression().getExpressionString();
                                sharedFluid = "((" + ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getShare() + "*(" + remainingFlow + "))/" + "(" + sumString + ")";
                                ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).setCurrentRateExpressionFromString(sharedFluid);
                                ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).setAdapted(true);
//                                currentPriorityActualFlow = currentPriorityActualFlow + "+" + sharedFluid;
                                ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).addRateAdapationEvent(place.getId(), "(" + currentArc.getShare() + "*(" + remainingFlow + "))/(" + sumString + ")-1");//TODO
                            } /*else {
//                                flux -= ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getWeight();
//                                changeOfFlux -= ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getWeight();
//                                sum -= ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentFluid() * currentArc.getWeight() * currentArc.getShare();
                                String currentRate = "(" + ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getWeight();
//                                currentPriorityActualFlow = currentPriorityActualFlow + "+" + ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + "*" + currentArc.getWeight();
                                double currentRateValue = computeCurrentExpressionValue(((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression()) * currentArc.getWeight();
                                currentSumValue -= currentRateValue * currentArc.getShare();
                                remainingFlowValue -= currentRateValue;
                                sumString = sumString + "-((" + currentRate + ")*" + currentArc.getShare() + ")";
                                remainingFlow = remainingFlow + "-(" + currentRate + ")";

                            }*/


                        } else {

                            if (currentArc.getShare() / currentSumValue < 1 / remainingFlowValue) {
                                sharedFluid = "((" + ((ContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getShare() + "*(" + remainingFlow + "))/" + "(" + sumString + ")";
//                                changeOfSharedFluid = ((ContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentChangeOfFluid() * currentArc.getShare() * flux / sum;
                                ((ContinuousTransitionVar) currentArc.getConnectedTransition()).setCurrentRateExpressionFromString(sharedFluid);
                                ((ContinuousTransitionVar) currentArc.getConnectedTransition()).addRateAdapationEvent(place.getId(), "(" + currentArc.getShare() + "*(" + remainingFlow + "))/(" + sumString + ")-1");
                            } /*else {

                                String currentRate = "(" + ((ContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + currentArc.getWeight();
//                                currentPriorityActualFlow = currentPriorityActualFlow + "+" + ((DynamicContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + "*" + currentArc.getWeight();
                                double currentRateValue = computeCurrentExpressionValue(((ContinuousTransitionVar) currentArc.getConnectedTransition()).getCurrentRateExpression()) * currentArc.getWeight();
                                currentSumValue -= currentRateValue * currentArc.getShare();
                                remainingFlowValue -= currentRateValue;
                                sumString = sumString + "-((" + currentRate + ")*" + currentArc.getShare() + ")";
                                remainingFlow = remainingFlow + "-(" + currentRate + ")";
                            }*/
                        }

                    }
                }
                arcIndex = arcIndex2;

            } else
                arcIndex++;
        }

        if (enoughForCurrentPriority)
            currentPriority -= 1;


        //set lower priority arcs to zero
        while (arcIndex < arcs.size()) {
            if (arcs.get(arcIndex).getConnectedPlace().getId().equals(place.getId()) && arcs.get(arcIndex).getClass().equals(ContinuousArc.class) && ((ContinuousArc) arcs.get(arcIndex)).getDirection().equals(direction)) {

                if (arcs.get(arcIndex).getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {
                    ((DynamicContinuousTransitionVar) arcs.get(arcIndex).getConnectedTransition()).setCurrentRateExpressionFromString("0");
//                    ((DynamicContinuousTransitionVar) arcs.get(arcIndex).getConnectedTransition()).setCurrentChangeOfFluid(0.0);
                    ((DynamicContinuousTransitionVar) arcs.get(arcIndex).getConnectedTransition()).setAdapted(true);
                } else {
                    ((ContinuousTransitionVar) arcs.get(arcIndex).getConnectedTransition()).setCurrentRateExpressionFromString("0");
//                    ((ContinuousTransitionVar) arcs.get(arcIndex).getConnectedTransition()).setCurrentChangeOfFluid(0.0);
                }
                lowestPriority = ((ContinuousArc) arcs.get(arcIndex)).getPriority();
            }
            arcIndex++;
        }


//        if (highestPriority.equals(lowestPriority))
//            return Double.POSITIVE_INFINITY;
//
//        Double timeToNextPriority = Double.POSITIVE_INFINITY;

//        timeToNextPriority = computeTimeToNextPriority(flux, changeOfFlux, accumulatedFluxRequired, accumulatedChangeOfFluxRequired); //accumulated = priorities that got full flux
//        timeToNextPriority = Math.min(timeToNextPriority, computeTimeToNextPriority(flux, changeOfFlux, accumulatedFluxRequired + fluxRequired, accumulatedChangeOfFluxRequired + changeOfFluxRequired));
//
//
//        return timeToNextPriority;

    }

    public double computeCurrentExpressionValue(Expression expression) {
        //TODO implementieren
        expression.removeAllArguments();
        String[] missing = expression.getMissingUserDefinedArguments();
        for (int j = 0; j < missing.length; j++) {
            for (Place place : this.getPlaces()) {
                if (place.getClass().equals(ContinuousPlaceVar.class) && (place.getId().equals(missing[j]) || ("delta_" + place.getId()).equals(missing[j]))) {

                    //TODO hier muss evtl noch ein delta_ dazu - ziemlich sicher nicht
                    expression.addArguments(new Argument(missing[j], ((ContinuousPlaceVar) place).getCurrentFluidLevel()));
                    break;
                }

            }
        }
        return expression.calculate();
    }


    private void setClockValuesToZero() {

        for (Transition transition : transitions) {
            if (transition.getClass().equals(DeterministicTransition.class)) {
                ((DeterministicTransition) transition).setClock(0.0);
            } else if (transition.getClass().equals(GeneralTransition.class)) {
                ((GeneralTransition) transition).setEnablingTime(0.0);
                ((GeneralTransition) transition).setFiringsToZero();
            }
        }
    }


    private void setOriginalFluidLevelsAndTokens() {
        for (Place place : places) {
            if (place.getClass().equals(ContinuousPlaceVar.class)) {
                ((ContinuousPlaceVar) place).resetFluidLevel();
                ((ContinuousPlaceVar) place).checkLowerBoundary();
                ((ContinuousPlaceVar) place).checkUpperBoundary();
                ((ContinuousPlaceVar) place).setLastUpdate(0.0);
                ;
            } else
                ((DiscretePlace) place).resetNumberOfTokens();
        }
    }


    private void computeInitialInternalTransitions() {

        for (Place place : places) {

//			if (place.getClass().equals(ContinuousPlaceVar.class))
//				((ContinuousPlaceVar)place).computeTimeToNextInternalTransition(arcs);

        }
    }


    private Double computeTimeToNextPriority(Double flux, Double changeOfFlux, Double accumulatedFluxRequired, Double accumulatedChangeOfFluxRequired) {

        Double t = (accumulatedChangeOfFluxRequired - changeOfFlux) / (flux - accumulatedFluxRequired);

        if (t > 0.0)
            return t;

        return Double.POSITIVE_INFINITY;
    }


    public void updatePlaceDrift() {

        ContinuousPlaceVar p;
        Expression placedrift = new Expression("0");
        String currentExpression;


        for (Place place : places) {
            placedrift.clearExpressionString();
            placedrift.setExpressionString("0");
            if (place.getClass().equals(ContinuousPlaceVar.class)) {

                p = (ContinuousPlaceVar) place;

                for (Arc arc : arcs) {
                    if (arc.getConnectedPlace().getId().equals(place.getId()) && !arc.getClass().equals(GuardArc.class)) {
                        if (arc.getConnectedTransition().getEnabled()) {

                            if (((ContinuousArc) arc).getDirection().equals(ContinuousArcType.input)) {

                                if (arc.getConnectedTransition().getClass().equals(ContinuousTransitionVar.class)) {
                                    currentExpression = placedrift.getExpressionString();
                                    placedrift.setExpressionString(currentExpression + "+(" + ((ContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + "*)" + arc.getWeight());
                                } else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {
                                    currentExpression = placedrift.getExpressionString();
                                    placedrift.setExpressionString(currentExpression + "+(" + (((DynamicContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression()).getExpressionString() + ")*" + arc.getWeight());
                                }

                            } else {
                                if (arc.getConnectedTransition().getClass().equals(ContinuousTransitionVar.class)) {
//										outFlux += ((ContinuousTransition)arc.getConnectedTransition()).getCurrentFluid() * arc.getWeight();
                                    currentExpression = placedrift.getExpressionString();
                                    placedrift.setExpressionString(currentExpression + "-(" + ((ContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression().getExpressionString() + ")*" + arc.getWeight());
                                } else if (arc.getConnectedTransition().getClass().equals(DynamicContinuousTransitionVar.class)) {
                                    currentExpression = placedrift.getExpressionString();
                                    placedrift.setExpressionString(currentExpression + "-(" + (((DynamicContinuousTransitionVar) arc.getConnectedTransition()).getCurrentRateExpression()).getExpressionString() + ")*" + arc.getWeight());
//										outFlux += ((DynamicContinuousTransition)arc.getConnectedTransition()).getCurrentFluid() * arc.getWeight();
                                }
                            }
                        }
                    }
                }
//					if (Double.isNaN(placedrift.calculate()))
//						placedrift.setExpressionString("0");
//					System.out.println(placedrift.getExpressionString()+"ausgewertet"+placedrift.calculate());
                p.setDriftFromString(placedrift.getExpressionString());
                p.setCurrentDriftFromString(placedrift.getExpressionString());


            } else
                continue;
        }
    }

    public void resetCurrentTransitionRates() {
        for (Transition transition : transitions) {
            if (transition.getClass().equals(ContinuousTransitionVar.class)) {
                ((ContinuousTransitionVar) transition).resetCurrentRateExpression();
                ((ContinuousTransitionVar) transition).resetRateAdaptionEvents();
            } else if (transition.getClass().equals(DynamicContinuousTransitionVar.class)) {
                ((DynamicContinuousTransitionVar) transition).resetCurrentRateExpression();
                ((DynamicContinuousTransitionVar) transition).setAdapted(false);
                ((DynamicContinuousTransitionVar) transition).resetRateAdaptionEvents();
            }
        }
    }


}