package de.wwu.criticalsystems.libhpng.simulation;

import java.util.logging.Logger;
import umontreal.ssj.randvar.*;
import umontreal.ssj.rng.MRG31k3p;
import de.wwu.criticalsystems.libhpng.errorhandling.InvalidDistributionParameterException;
import de.wwu.criticalsystems.libhpng.model.CDFFunctionParameter;
import de.wwu.criticalsystems.libhpng.model.GeneralTransition;

public class DistributionSetting {
	
	//one of the following distributions with parameters mu and sigma:
	//normal, foldednormal, halfnormal, lognormal
	public static RandomVariateGen setDistributionMuSigma(GeneralTransition transition, MRG31k3p stream, byte type, Logger logger) throws InvalidDistributionParameterException{
		
		Double mu = 0.0;
		Double sigma = 1.0;
		Boolean muFound = false;
		Boolean sigmaFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "mu":
					mu = parameter.getValue();
					muFound = true;
					break;
				case "sigma":
					sigma = parameter.getValue();
					sigmaFound = true;	
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		if (muFound && sigmaFound && sigma > 0.0 && (mu >= 0.0 || type != 1)){
			
			switch (type){
				case 0 :
					NormalGen generator0 = new NormalGen(stream, mu, sigma);
					return generator0;
				case 1 :
					FoldedNormalGen generator1 = new FoldedNormalGen(stream, mu, sigma);
					return generator1;
				case 2:
					LognormalGen generator2 = new LognormalGen(stream, mu, sigma);
					return generator2;
			}

		} else if (sigma <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter sigma has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter sigma has to be greater than zero");
		} else if (type == 1 && mu < 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter mu has to be greater than or equal to zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter mu has to be greater than or equal to zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
		return null;
	}
	
	
	//One of the following distributions with parameters alpha and beta
	//cauchy, inversegamma, loglogistic, pareto
	public static RandomVariateGen setDistributionAlphaBeta(GeneralTransition transition, MRG31k3p stream, byte type, Logger logger) throws InvalidDistributionParameterException{
			
		Double alpha = 1.0;
		Double beta = 1.0;
		Boolean alphaFound = false;
		Boolean betaFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "alpha":
					alpha = parameter.getValue();
					alphaFound = true;
					break;
				case "beta":
					beta = parameter.getValue();
					betaFound = true;	
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		if (alphaFound && betaFound && (alpha > 0.0 || type == 0) && beta > 0.0){
			switch (type){
				case 0 :
					CauchyGen generator0 = new CauchyGen(stream, alpha, beta);
					return generator0;
				case 1:
					InverseGammaGen generator1 = new InverseGammaGen(stream, alpha, beta);
					return generator1;
				case 2:
					ParetoGen generator2 = new ParetoGen(stream, alpha, beta);
					return generator2;
				case 3:
					BetaGen generator3 = new BetaGen(stream, alpha, beta);
					return generator3;
				case 4:
					GammaGen generator4 = new GammaGen(stream, alpha, beta);
					return generator4;
				case 5:
					WeibullGen generator5 = new WeibullGen(stream, alpha, beta, 0.0);
					return generator5;
			}
			
		} else if (alpha <= 0.0 && type != 0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter alpha has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter alpha has to be greater than zero");
		} else if (beta <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter beta has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter beta has to be greater than zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
			
		return null;
	}
	
	

	
	//One of the following distributions with (integer) parameter n
	//chi square, student
	public static RandomVariateGen setDistributionN(GeneralTransition transition, MRG31k3p stream, byte type, Logger logger) throws InvalidDistributionParameterException{
			
		Integer n = 1;
		Boolean nFound = false;	
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "n":
					Double nDouble = parameter.getValue();
					if (nDouble.equals(Math.floor(nDouble))){
						n = nDouble.intValue();
						nFound = true;
					} else{
						if (logger != null)
							logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be an integer value");
						throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be an integer value");
					}	
						System.out.println();					
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		
		if (nFound && n > 0){
			switch (type){
				case 0:
					ChiSquareGen generator0 = new ChiSquareGen(stream, n);
					return generator0;
				case 1:
					StudentGen generator1 = new StudentGen(stream, n);
					return generator1;
			}
		} else if (n <= 0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be greater than zero");
		} else{
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}		
		return null;
	}
	
		
	//uniform distribution within interval (a, b)
	public static RandomVariateGen setUniformDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
			
		Double a = 0.0;
		Double b = 1.0;
		Boolean aFound = false;
		Boolean bFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "a":
					a = parameter.getValue();
					aFound = true;
					break;
				case "b":
					b = parameter.getValue();
					bFound = true;	
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());										
			}
		}
		
		if (aFound && bFound && a < b){
			UniformGen generator = new UniformGen(stream, a, b);
			return generator;
		} else if (b <= a){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter a has to be less than parameter b");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter a has to be less than parameter b");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}

	
	//exponential distribution with parameter lambda
	public static RandomVariateGen setExpDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
		
		Double lambda = 1.0;
		Boolean lambdaFound = false;
		Boolean meanFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "lambda":
					lambda = parameter.getValue();
					lambdaFound = true;						
					break;
				case "mean":
					lambda = 1.0 / parameter.getValue();
					meanFound = true;						
					break;	
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
			
		if ((lambdaFound || meanFound) && lambda > 0.0){
			ExponentialGen generator = new ExponentialGen(stream, lambda);
			return generator;
		} else if (lambda <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter lambda has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter lambda has to be greater than zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}	
	
	
	//fisher's f distribution with (integer) parameters m and n
	public static RandomVariateGen setFisherFDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
			
		Integer n = 1;
		Boolean nFound = false;
		Integer m = 1;
		Boolean mFound = false;
	
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "m":
					double mDouble = parameter.getValue();
					if (mDouble == Math.floor(mDouble)){
						m = (int) mDouble;
						mFound = true;
					} else{
						if (logger != null) 
							logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter m has to be an integer value");
						throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter m has to be an integer value");
					}	
					break;
				case "n":
					double nDouble = parameter.getValue();
					if (nDouble == Math.floor(nDouble)){
						n = (int) nDouble;
						nFound = true;
					} else{
						if (logger != null) 
							logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be an integer value");
						throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be an integer value");
					}					
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		
		if (mFound && nFound && m > 0 && n > 0){
			FisherFGen generator = new FisherFGen(stream, n, m);
			return generator;
		} else if (m <= 0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter m has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter m has to be greater than zero");
		}else if (n <= 0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter n has to be greater than zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}
	

	//exponential distribution with parameter lambda
	public static RandomVariateGen setHalfNormalDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
			
		Double sigma = 1.0;
		Boolean sigmaFound = false;		
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "sigma":
					sigma = parameter.getValue();
					sigmaFound = true;						
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
			
		if (sigmaFound && sigma > 0.0){
			HalfNormalGen generator = new HalfNormalGen(stream, 0.0, sigma);
			return generator;
		} else if (sigma <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter sigma has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter sigma has to be greater than zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}	
	
	
	//Inverse normal (inverse Gaussian) distribution with parameters mu and lambda:
	public static RandomVariateGen setInverseNormalDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
		
		Double mu = 1.0;
		Double lambda = 1.0;
		Boolean muFound = false;
		Boolean lambdaFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "mu":
					mu = parameter.getValue();
					muFound = true;
					break;
				case "lambda":
					lambda = parameter.getValue();
					lambdaFound = true;	
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		if (muFound && lambdaFound && lambda > 0.0 && mu > 0.0){
			InverseGaussianGen generator = new InverseGaussianGen(stream, mu, lambda);
			return generator;
		} else if (lambda <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter lambda has to be greater than zero");	
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter lambda has to be greater than zero");	
		} else if (mu <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter mu has to be greater than zero");	
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter mu has to be greater than zero");	
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}
	
	
	//Laplace distribution with parameters mu and beta:
	public static RandomVariateGen setLaplaceDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
		
		Double mu = 0.0;
		Double beta = 1.0;
		Boolean muFound = false;
		Boolean betaFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "mu":
					mu = parameter.getValue();
					muFound = true;
					break;
				case "beta":
					beta = parameter.getValue();
					betaFound = true;	
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		if (muFound && betaFound && beta > 0.0){
			LaplaceGen generator = new LaplaceGen(stream, mu, beta);
			return generator;
		} else if (beta <= 0.0) {
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter beta has to be greater than zero");	
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter beta has to be greater than zero");	
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}
	
	
	//Rayleigh distribution with parameter beta and lower boundary a
	public static RandomVariateGen setRayleighDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
			
		Double beta = 1.0;
		Boolean betaFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "beta":
					beta = parameter.getValue();
					betaFound = true;	
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		if (betaFound && beta > 0.0){
			RayleighGen generator = new RayleighGen(stream, 0.0, beta);
			return generator;
		} else if (beta < 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter beta has to be greater than zero");	
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter beta has to be greater than zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}
	
	//Logistic distribution with parameters alpha and lambda
	public static RandomVariateGen setLogisticDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
					
		Double mu = 1.0;
		Double s = 1.0;
		Boolean muFound = false;
		Boolean sFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "mu":
					mu = parameter.getValue();
					muFound = true;
					break;
				case "s":
					s = parameter.getValue();
					sFound = true;	
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());										
			}
		}
		
		if (muFound && sFound && s > 0.0){
			LogisticGen generator1 = new LogisticGen(stream, mu, 1.0/s);
			return generator1;
		} else if (s <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter s has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter s has to be greater than zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}
	
	
	//Weibull distribution with parameters alpha, lambda and delta
	public static RandomVariateGen setWeibullDistribution(GeneralTransition transition, MRG31k3p stream, Logger logger) throws InvalidDistributionParameterException{
					
		Double alpha = 1.0;
		Double lambda = 1.0;
		Double delta = 0.0;
		Boolean alphaFound = false;
		Boolean lambdaFound = false;
		Boolean deltaFound = false;
		
		for (CDFFunctionParameter parameter : transition.getParameters()){
			switch (parameter.getName()){
				case "alpha":
					alpha = parameter.getValue();
					alphaFound = true;
					break;
				case "lambda":
					lambda = parameter.getValue();
					lambdaFound = true;	
					break;
				case "delta":
					delta = parameter.getValue();
					deltaFound = true;
					break;
				default :
					if (logger != null) 
						logger.warning("Unknown distribution parameter " + parameter.getName() + " for General Transition " + transition.getId());					
			}
		}
		
		if (alphaFound && lambdaFound && deltaFound && alpha > 0.0 && lambda > 0.0){
			WeibullGen generator = new WeibullGen(stream, alpha, lambda, delta);
			return generator;					
		} else if (alpha <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter alpha has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter alpha has to be greater than zero");
		}else if (lambda <= 0.0){
			if (logger != null)
				logger.severe("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter lambda has to be greater than zero");
			throw new InvalidDistributionParameterException("Invalid distribution parameter for General Transition " + transition.getId() + ": Parameter lambda has to be greater than zero");
		} else {
			if (logger != null)
				logger.severe("Missing distribution parameter for General Transition " + transition.getId());
			throw new InvalidDistributionParameterException("Missing distribution parameter for General Transition " + transition.getId());
		}
	}
}