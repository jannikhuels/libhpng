package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name = "places")
@XmlSeeAlso({DiscretePlace.class, ContinuousPlace.class})
public abstract class Place {

    public Place() {
    }

    public Place(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Arc> getConnectedArcs() {
        return connectedArcs;
    }

    private String id;
    private ArrayList<Arc> connectedArcs = new ArrayList<Arc>();

}
