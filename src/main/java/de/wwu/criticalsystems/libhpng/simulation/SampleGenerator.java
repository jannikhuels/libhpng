package de.wwu.criticalsystems.libhpng.simulation;

import java.util.Random;

import de.wwu.criticalsystems.libhpng.model.*;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;

public class SampleGenerator {
	
	public SampleGenerator(){}
	
	private MRG31k3p stream;
	
	public MRG31k3p getStream() {
		return stream;
	}

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

	//create samples for all general transitions according to their CDF function and parameters
	public void sampleGeneralTransitions(HPnGModel model) {

    	for (Transition transition : model.getTransitions()){
    		
    		if (transition.getClass().equals(GeneralTransition.class)){
    			
    			RandomVariateGen randomGenerator = setDistributionParameters((GeneralTransition)transition, stream);
    		    if (randomGenerator != null){
    		    	((GeneralTransition)transition).setRandomGenerator(randomGenerator);
    		    	((GeneralTransition)transition).setNewRandomFiringTime();    		    
    		    }
    		} else if (transition.getClass().equals(ContinuousTransition.class))
    			break;
    	}		
	}
	
	//set parameters for CDF function of general transition and return sample generator
	private RandomVariateGen setDistributionParameters(GeneralTransition transition, MRG31k3p stream){
	
		RandomVariateGen distributionGenerator = null;
		
		switch (transition.getDistribution()){
			case uniform:
				distributionGenerator = DistributionSetting.setUniformDistribution(transition, stream);
				break;
			case normal: 
				distributionGenerator = DistributionSetting.setDistributionMuSigma(transition, stream, (byte)0);
				break;				
			case foldednormal: 
				distributionGenerator = DistributionSetting.setDistributionMuSigma(transition, stream, (byte)1);
				break;
			case halfnormal: 
				distributionGenerator = DistributionSetting.setDistributionMuSigma(transition, stream, (byte)2);
				break;
			case lognormal:
				distributionGenerator = DistributionSetting.setDistributionMuSigma(transition, stream, (byte)3);
				break;
			case inversenormal:
				distributionGenerator = DistributionSetting.setInverseNormalDistribution(transition, stream);
				break;			
			case beta:
				distributionGenerator = DistributionSetting.setBetaDistribution(transition, stream);
				break;
			case cauchy:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)0);
				break;
			case chi:
				distributionGenerator = DistributionSetting.setChiDistribution(transition, stream);
				break;
			case chisquare:
				distributionGenerator = DistributionSetting.setDistributionN(transition, stream, (byte)0);
				break;
			case exp:
				distributionGenerator = DistributionSetting.setExpDistribution(transition, stream);
				break;
			case fisherf:
				distributionGenerator = DistributionSetting.setFisherFDistribution(transition, stream);
				break;
			case frechet:
				distributionGenerator = DistributionSetting.setFrechetDistribution(transition, stream);
				break;
			case gamma:
				distributionGenerator = DistributionSetting.setDistributionAlphaLambda(transition, stream, (byte)0);
				break;
			case gumbel:
				distributionGenerator = DistributionSetting.setGumbelDistribution(transition, stream);
				break;
			case inversegamma:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)1);
				break;
			case logistic:
				distributionGenerator = DistributionSetting.setDistributionAlphaLambda(transition, stream, (byte)1);
				break;
			case loglogistic:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)2);
				break;
			case pareto:
				distributionGenerator = DistributionSetting.setDistributionAlphaBeta(transition, stream, (byte)3);
				break;
			case rayleigh:
				distributionGenerator = DistributionSetting.setRayleighDistribution(transition, stream);
				break;
			case student:
				distributionGenerator = DistributionSetting.setDistributionN(transition, stream, (byte)1);
				break;
		}
		return distributionGenerator;
	}
	
	
}