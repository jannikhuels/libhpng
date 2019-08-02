package de.wwu.criticalsystems.libhpng.model;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

// Does not support dynamic transition with a rate based on other continuous transitions
@XmlRootElement(name = "dynamicContinuousTransition")
public class DynamicContinuousTransitionVar extends FluidTransition {

    public DynamicContinuousTransitionVar() {
    }

    public DynamicContinuousTransitionVar(String id, Boolean enabled, Expression fluidExpression, Expression changeOfFluidExpressionString, ArrayList<DynamicContinuousDependencyVar> dependencies) {
        super(id, enabled,fluidExpression.getExpressionString());
        this.changeOfRateExpression = new Expression(changeOfFluidExpressionString.getExpressionString());
        this.dependencies = dependencies;
    }

    public DynamicContinuousTransitionVar(DynamicContinuousTransitionVar transitionToCopy, ArrayList<Transition> transitions) throws ModelCopyingFailedException {
        super(new String(transitionToCopy.getId()), new Boolean(transitionToCopy.getEnabled()),transitionToCopy.getRateExpression().getExpressionString());
        this.adapted = new Boolean(transitionToCopy.getAdapted());
        this.changeOfRateExpression = new Expression(transitionToCopy.getChangeOfRateExpression().getExpressionString());
        this.currentRateExpression = new Expression(transitionToCopy.getCurrentRateExpression().getExpressionString());

        for (DynamicContinuousDependencyVar currentDependencyToCopy : transitionToCopy.getDependencies()) {
            this.dependencies.add(new DynamicContinuousDependencyVar(currentDependencyToCopy, transitions));
        }
    }


    @XmlAttribute(name = "rateFunction")
    public void setRateFunction(String rateFunction) {
        this.rateExpression = new Expression(rateFunction);
        this.currentRateExpression = new Expression(rateFunction);
    }

    @XmlAttribute(name = "changeOfRateFunction")
    public void setChangeOfRateFunction(String changeOfRateFunction) {
        this.changeOfRateExpression = new Expression(changeOfRateFunction);
    }


    public Expression getChangeOfRateExpression() {
        return changeOfRateExpression;
    }



    public ArrayList<DynamicContinuousDependencyVar> getDependencies() {
        return dependencies;
    }


    public Boolean getAdapted() {
        return adapted;
    }

    public void setAdapted(Boolean adapted) {
        this.adapted = adapted;
    }

    @XmlElements({
            @XmlElement(name = "pid", type = DynamicContinuousDependency.class),
    })
    private ArrayList<DynamicContinuousDependencyVar> dependencies = new ArrayList<DynamicContinuousDependencyVar>();
    private Boolean adapted = false;
    private Expression changeOfRateExpression;
    private HashMap<String, ContinuousPlaceVar> placesForFluidExpression = new HashMap<String, ContinuousPlaceVar>();
    private HashMap<String, ContinuousPlaceVar> placesForChangeOfFluidExpression = new HashMap<String, ContinuousPlaceVar>();
    private Integer numberOfEntries = null;


//	public void computeCurrentFluidAndCurrentChangeOfFluid(ArrayList<Place> places){
//
//
//		currentFluid = 0.0;
//		currentChangeOfFluid = 0.0;
//
//		if (fluidExpression != null){
//
//			if(numberOfEntries == null)
//				createHashMapsAndArguments(places);
//
//			if (numberOfEntries > 0){
//
//				Entry<String, ContinuousPlace> pair = null;
//
//			    Iterator<Entry<String, ContinuousPlace>> it = placesForFluidExpression.entrySet().iterator();
//			    while (it.hasNext()) {
//			    	pair = (Entry<String, ContinuousPlace>)it.next();
//					fluidExpression.setArgumentValue(pair.getKey(), getArgumentValue(pair.getKey(),pair.getValue()));
//			    }
//
//			    it = placesForChangeOfFluidExpression.entrySet().iterator();
//			    while (it.hasNext()) {
//			    	pair = (Entry<String, ContinuousPlace>)it.next();
//			    	changeOfFluidExpression.setArgumentValue(pair.getKey(), getArgumentValue(pair.getKey(),pair.getValue()));
//			    }
//
//			    currentFluid = fluidExpression.calculate();
//			    if (currentFluid.isNaN())
//			    	currentFluid = 0.0;
//			    currentChangeOfFluid = changeOfFluidExpression.calculate();
//			    if (currentChangeOfFluid.isNaN())
//			    	currentChangeOfFluid = 0.0;
//			}
//		}
//
//
//		for (int i = 0; i < dependencies.size(); i++){
//			if (dependencies.get(i).getTransition().getEnabled()){
//				currentFluid+= (dependencies.get(i).getTransition().getCurrentFluidExpression * (dependencies.get(i).getCoefficient()));
//			}
//		}
//	}


    private void createHashMapsAndArguments(ArrayList<Place> places) {

        rateExpression.removeAllArguments();
        String[] missing = rateExpression.getMissingUserDefinedArguments();
        numberOfEntries = 0;

        for (int i = 0; i < missing.length; i++) {

            for (Place place : places) {

                if (place.getClass().equals(ContinuousPlaceVar.class) && (place.getId().equals(missing[i]) || ("delta_" + place.getId()).equals(missing[i]))) {

                    if (placesForFluidExpression.get(missing[i]) == null) {
                        placesForFluidExpression.put(missing[i], (ContinuousPlaceVar) place);
                        numberOfEntries++;
                    }

                    rateExpression.addArguments(new Argument(missing[i], getArgumentValue(missing[i], (ContinuousPlaceVar) place)));
                }
            }
        }


        changeOfRateExpression.removeAllArguments();
        missing = changeOfRateExpression.getMissingUserDefinedArguments();

        for (int i = 0; i < missing.length; i++) {

            for (Place place : places) {

                if (place.getClass().equals(ContinuousPlaceVar.class) && (place.getId().equals(missing[i]) || ("delta_" + place.getId()).equals(missing[i]))) {

                    if (placesForChangeOfFluidExpression.get(missing[i]) == null) {
                        placesForChangeOfFluidExpression.put(missing[i], (ContinuousPlaceVar) place);
                        numberOfEntries++;
                    }

                    changeOfRateExpression.addArguments(new Argument(missing[i], getArgumentValue(missing[i], (ContinuousPlaceVar) place)));
                }
            }
        }

    }


    private Double getArgumentValue(String argument, ContinuousPlaceVar p) {

        if (("delta_" + p.getId()).equals(argument))
            return p.getDrift().calculate();
        else
            return p.getCurrentFluidLevel();
    }

}
