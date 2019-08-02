package de.wwu.criticalsystems.libhpng.simulation;

import de.wwu.criticalsystems.libhpng.errorhandling.InvalidDistributionParameterException;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidRandomVariateGeneratorException;
import de.wwu.criticalsystems.libhpng.model.*;
import umontreal.ssj.randvar.RandomVariateGen;
import umontreal.ssj.rng.MRG31k3p;

import java.util.Random;
import java.util.logging.Logger;


public class SampleGeneratorVar {

	public SampleGeneratorVar(){}
	
	
	public MRG31k3p getStream() {
		return stream;
	}
	
	
	private MRG31k3p stream;

	
	public void initializeRandomStream(){
		
		int[] seeds = new int[6];
    	for (int i=0; i < 3; i++){
    		do
    			seeds[i] = new Random().nextInt();
    		while (seeds[i] == 0 || seeds[i] > 2147483647);
    	}
    	for (int i=3; i < 6; i++){
    		do
    			seeds[i] = new Random().nextInt();
    		while (seeds[i] == 0 || seeds[i] > 2147462579);
    	}
    	
    	MRG31k3p.setPackageSeed(seeds);
    	stream = new MRG31k3p();
	}
	
	public void sampleGeneralTransitions(HPnGModelVar model, Logger logger) throws InvalidDistributionParameterException, InvalidRandomVariateGeneratorException{

    	for (Transition transition : model.getTransitions()){
    		
    		if (transition.getClass().equals(GeneralTransition.class)){
    			
    			RandomVariateGen randomGenerator = setDistributionParameters((GeneralTransition)transition, stream, logger);
    		    if (randomGenerator != null){
    		    	((GeneralTransition)transition).setRandomGenerator(randomGenerator);
    		    	((GeneralTransition)transition).setNewRandomFiringTime(false);    		    
    		    }
    		} else if (transition.getClass().equals(ContinuousTransition.class))
    			break;
    	}		
	}
		
	private RandomVariateGen setDistributionParameters(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
	
		RandomVariateGen distributionGenerator = null;
		
		switch (transition.getDistribution()){
			case uniform:
				distributionGenerator = DistributionSetting.setUniformDistribution(transition, stream, logger);
				break;
			case normal: 
				distributionGenerator = DistributionSetting.setDistributionMuSigma(transition, stream, (byte)0, logger);
				break;				
			case foldednormal: 
				distributionGenerator = DistributionSetting.setDistributionMuSigma(transition, stream, (byte)1, logger);
				break;
			case halfnormal: 
				distributionGenerator = DistributionSetting.setHalfNormalDistribution(transition, stream, logger);
				break;
			case lognormal:
				distributionGenerator = DistributionSetting.setDistributionMuSigma(transition, stream, (byte)2, logger);
				break;
			case inversenormal:
				distributionGenerator = DistributionSetting.setInverseNormalDistribution(transition, stream, logger);
				break;			
			case beta:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)3, logger);
				break;
			case cauchy:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)0, logger);
				break;
			case chi:
				if (logger != null)
					logger.severe("Invalid distribution for General Transition " + transition.getId() + ": Chi distribution not supported in this version.");
				throw new InvalidDistributionParameterException("Invalid distribution for General Transition " + transition.getId() + ": Chi distribution not supported in this version.");
			case chisquare:
				distributionGenerator = DistributionSetting.setDistributionN(transition, stream, (byte)0, logger);
				break;
			case exp:
				distributionGenerator = DistributionSetting.setExpDistribution(transition, stream, logger);
				break;
			case fisherf:
				distributionGenerator = DistributionSetting.setFisherFDistribution(transition, stream, logger);
				break;
			case frechet:
				if (logger != null)
					logger.severe("Invalid distribution for General Transition " + transition.getId() + ": Frechet distribution not supported in this version.");
				throw new InvalidDistributionParameterException("Invalid distribution for General Transition " + transition.getId() + ": Frechet distribution not supported in this version.");
			case gamma:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)4, logger);
				break;
			case gumbel:
				if (logger != null)
					logger.severe("Invalid distribution for General Transition " + transition.getId() + ": Gumbel distribution not supported in this version.");
				throw new InvalidDistributionParameterException("Invalid distribution for General Transition " + transition.getId() + ": Gumbel distribution not supported in this version.");
			case inversegamma:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)1, logger);
				break;
			case laplace:
				distributionGenerator = DistributionSetting.setLaplaceDistribution(transition, stream, logger);
				break;
			case logistic:
				distributionGenerator = DistributionSetting.setLogisticDistribution(transition, stream, logger);
				break;
			case loglogistic:
				if (logger != null)
					logger.severe("Invalid distribution for General Transition " + transition.getId() + ": Loglogistic distribution not supported in this version.");
				throw new InvalidDistributionParameterException("Invalid distribution for General Transition " + transition.getId() + ": Loglogistic distribution not supported in this version.");
			case pareto:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)2, logger);
				break;
			case rayleigh:
				distributionGenerator = DistributionSetting.setRayleighDistribution(transition, stream, logger);
				break;
			case student:
				distributionGenerator = DistributionSetting.setDistributionN(transition, stream, (byte)1, logger);
				break;
			case weibull:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)5, logger);
				break;

		default:
			break;
		}
		return distributionGenerator;
	}	
}