package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.shell.Bootstrap;

public class Main {	
	

    public static void main(String[] args){
    		
    	try {
    		PrintWriter writer = new PrintWriter("shellLogFile.log");
    		writer.print("");
    		writer.close();
    		
    		
    		Bootstrap.main(args);
//    		
//    		ModelHandler handler = new ModelHandler();//    		
//    		handler.readModel("examples/example.xml");//    		
//    		SimpleNode root = handler.readFormula();    		
//    		handler.checkFormula(root);
    		
    		
		} catch (IOException e) {
			System.out.println("An error occured while loading the shell. libhpng cannot be executed.");
		}
    }
}