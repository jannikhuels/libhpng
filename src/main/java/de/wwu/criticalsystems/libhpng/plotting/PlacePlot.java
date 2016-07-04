package de.wwu.criticalsystems.libhpng.plotting;

import de.wwu.criticalsystems.libhpng.model.Place;

public class PlacePlot extends Plot {

	public PlacePlot(Place referencedPlace) {
		super();
		this.referencedPlace = referencedPlace;
	}
		
	public Place getReferencedPlace() {
		return referencedPlace;
	}
	public void setReferencedPlace(Place referencedPlace) {
		this.referencedPlace = referencedPlace;
	}

	private Place referencedPlace;	
}
