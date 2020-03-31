package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import java.io.PrintWriter;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidPropertyException;
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
    		
//    		ModelHandler handler = new ModelHandler();//
//    		handler.readModel("examples/kibam/test3.xml");
////			handler.readModel("examples/exampleperformanceeval.xml");
//			handler.changeParameter((byte)8, true);
//			handler.changeParameter((byte)6, 1);
//			handler.changeParameter((byte)5, 1);
//    		SimpleNode root = handler.readFormula();
//    		handler.checkFormula(root);
//			handler.changeParameter((byte)9, true);

//			handler.plotPlaces(4.0);
    		ModelHandlerVar testhandler = new ModelHandlerVar();
//    		testhandler.readModel("examples/kibam/kibam2.xml");
			testhandler.readModel("examples/heatedtank/heatedtank_v1_withT.xml");
//			testhandler.readModel("examples/models-0-1/models/307800000/model_0_1_307800000_450_240000.xml");
//			testhandler.readModel("examples/Lisa2.xml");
//    		testhandler.changeParameter((byte)9, true);
//			testhandler.changeParameter((byte)5, 10);
            testhandler.loadParameters();
//			testhandler.changeParameter((byte)8, true);
//			testhandler.changeParameter((byte)6, 1);
//			testhandler.changeParameter((byte)5, 1);
//			SimpleNode root2 = testhandler.readFormula("0.0:P=?(U[0.0,86400.0](tt,AND(tokens('inlot')=0,fluidlevel('ev')>=307800000.0)))");
//			SimpleNode root2 = testhandler.readFormula("86400.0:P=?(tokens('robust')=1)");
			SimpleNode root2 = testhandler.readFormula("0.0:P=?(U[0.0,300.0](tt,fluidlevel('T')>=100.0))");
//			SimpleNode root2 = testhandler.readFormula("0.0:P=?(U[0.0,500.0](tt,fluidlevel('H')<=97.0))");
//			SimpleNode root2 = testhandler.readFormula("24.0:P=?(tokens('pdproperty')<1)");
//			SimpleNode root2 = testhandler.readFormula("0.0:P=?(U[0.0,24.0](tt,fluidlevel('pcavailable')<=0.0))");
//			SimpleNode root2 = testhandler.readFormula("0.0:P=?(U[0.0,10.0](tt,AND(fluidlevel('pc1')>=9.0,fluidlevel('pc2')>=4.0)))");
//			testhandler.checkFormula(root2);
//			testhandler.plotPlaces(300.0, "tempFile.svg");//
			testhandler.checkFormula(root2);
    		
		} catch (IOException | InvalidPropertyException e) {
			System.out.println("An error occured while loading the shell. libhpng cannot be executed.");
		}
    }
}