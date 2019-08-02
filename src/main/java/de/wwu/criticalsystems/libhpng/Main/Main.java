package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import java.io.PrintWriter;

import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;
import org.springframework.shell.Bootstrap;
//import de.wwu.criticalsystems.libhpng.formulaparsing.SimpleNode;

public class Main {	
	

    public static void main(String[] args){
    	

    	try {
    		PrintWriter writer = new PrintWriter("shellLogFile.log");
    		writer.print("");
    		writer.close();
    		
    		
    	//Bootstrap.main(args);
    		
    		ModelHandler handler = new ModelHandler();//
//    		handler.readModel("examples/kibam/test2.xml");//
			handler.readModel("examples/exampleperformanceeval.xml");
//			handler.changeParameter((byte)8, true);
//			handler.changeParameter((byte)6, 1);
//			handler.changeParameter((byte)5, 1);
//    		SimpleNode root = handler.readFormula();
//    		handler.checkFormula(root);
//			handler.changeParameter((byte)9, true);

			handler.plotPlaces(4.0);
    		ModelHandlerVar testhandler = new ModelHandlerVar();
    		testhandler.readModel("examples/kibam/test2.xml");
//    		testhandler.changeParameter((byte)9, true);
//			testhandler.changeParameter((byte)5, 10);
			testhandler.changeParameter((byte)8, true);
			testhandler.changeParameter((byte)6, 1);
			testhandler.changeParameter((byte)5, 1);
//			SimpleNode root2 = testhandler.readFormula();
			//testhandler.checkFormula(root2);
			testhandler.plotPlaces(5.0);
    		
		} catch (IOException e) {
			System.out.println("An error occured while loading the shell. libhpng cannot be executed.");
		}
    }
}