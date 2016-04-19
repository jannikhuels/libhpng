package de.wwu.criticalsystems.libhpng.Main;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.wwu.criticalsystems.libhpng.model.*;

public class main {
    public static void main(String[] args) {
    	
    	File file = new File("example.xml");
    	JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(HPnGModel.class);
	    	Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    	HPnGModel myModel = (HPnGModel)jaxbUnmarshaller.unmarshal(file);
	    	ContinuousPlace p =  (ContinuousPlace) myModel.getPlaces().get(3); 
	    	System.out.println(p.getFluidLevel());  			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	
    	
    }
}
