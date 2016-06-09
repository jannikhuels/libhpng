package de.wwu.criticalsystems.libhpng.Main;

import java.io.IOException;
import org.springframework.shell.Bootstrap;

public class Main {	
 
    public static void main(String[] args){
    		
    	try {
			Bootstrap.main(args);
		} catch (IOException e) {
			System.out.println("An error occured while loading the shell. libhpng cannot be executed.");
		}
    }
}
