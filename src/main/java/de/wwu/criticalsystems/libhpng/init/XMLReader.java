package de.wwu.criticalsystems.libhpng.init;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import de.wwu.criticalsystems.libhpng.model.*;

public class XMLReader {
	
	public HPnGModel readXmlIntoModel(File xmlFile) throws JAXBException {
		
		HPnGModel model=null;
    	JAXBContext jaxbContext;
    	
    	//extract information from xml file into HPnG model
		jaxbContext = JAXBContext.newInstance(HPnGModel.class);
    	Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    	model = (HPnGModel)jaxbUnmarshaller.unmarshal(xmlFile);
		
		return model;
	}
}

