package de.wwu.criticalsystems.libhpng.Main;

import de.wwu.criticalsystems.libhpng.init.*;
import de.wwu.criticalsystems.libhpng.model.*;

public class Main {
    public static void main(String[] args) {
    	
    		ModelReader reader = new ModelReader();
    		HPnGModel model = reader.readModel("examples/example2.xml");

    }
}
