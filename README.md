# libhpng
The libhpng is a Java library provided for the investigation of [Hybrid Petri nets with general one shot transitions (HPnG)](http://ieeexplore.ieee.org/xpl/login.jsp?tp=&arnumber=5634312&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpls%2Fabs_all.jsp%3Farnumber%3D5634312). These are specifically tailored towards so-called fluid critical infrastructures allowing for timed, generally distributed and fluid transitions. Currently the libhpng supports simulation of HPnG models. Analyzation is planned to be included in the very near future. 

## How to get started?
Please refer to the [wiki](https://github.com/jannikhuels/libhpng/wiki) on how to get started with libhpng.

## What are the aims of libhpng?
Currently there are several tools and different algorithms available that are used to analyze or simulate HPnG models. libhpng aims at assembling all these in one *hopefully* well-structured and easy-to-use library. However this project is still in progress. So please do not heasitate to contact us for further information or interest in contribution. 

## Who is responsible for the libhpng project?
The project *libhpng* is organized by the group of [safety-critical systems](https://www.uni-muenster.de/Informatik.AGRemke/en/index.html) which is part of the institute of Mathematics and Computer Science at the Westfälischen Wilhelms-Universität Münster and lead by Prof. Dr. Anne Remke.

## Where may I get further Informations regarding HPnGs?
There are several papers available that introduce HPnG models and use them to assess real-world systems:
+ [Hybrid Petri Nets with General One-Shot Transitions for Dependability Evaluation of Fluid Critical Infrastructures](http://ieeexplore.ieee.org/xpl/login.jsp?tp=&arnumber=5634312&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpls%2Fabs_all.jsp%3Farnumber%3D5634312)
   
   Base paper: Introduction to the HPnG formalism and the Parametric Reachability analysis. 
+ [Survivability evaluation of fluid critical infrastructures using hybrid Petri nets](http://eprints.eemcs.utwente.nl/24178/)
   
   Introduction of a formal, model-based procedure to evaluate the survivabililty of fluid critical infrastructures. Introduction of the *Stochastic Time Logic (STL)*, as well as the corresponding analyzation technique *Stochastic Time Diagrams*, to express state-based and until-based properties for HPnG models. Presentation of a case-study of a water refinery showing the feasability of the chosen approach. 
+ [Energy Resilience Modeling for Smart Houses](http://eprints.eemcs.utwente.nl/26172/)
   
   Investigating a model of a home pv production system consisting of a local production and battery unit. Several battery management strategies are investigated in order to assess the survivability of the battery. In case grid-failures are taken into account there may be a chance that the battery is used in such a way that it prohibits the house from running out of power. 
+ [Assessing the Cost of Energy Independence](http://wwwhome.cs.utwente.nl/~jongerdenmr/papers/energycon_2016.pdf)
   
   Battery management strategies, that reserve power to ensure that the house is constantly powerd even in case of grid-failures, come at certain costs. A HPnG model is introduced that is used to weghing up these costs against the survivability probability of the battery management strategies.

## What is currently supported by libhpng?
+ Simulation, even for multiple general transitions.
+ ~~Analyzation using Stochastic Time Diagrams~~ (Will be implemented very soon)
+ ~~Analyzation using Parametric Reachability analysis~~ (Will be implemented even sooner)

## Are there former tools available that may support analyzation of HPnG models?
Yes, luckily there are. A [C++ tool](https://github.com/jannikhuels/HPnG) was introduced that supports the analyzation based on the Stochastic Time Diagrams. The [Fluid Survival Tool](https://github.com/bjornpostema/fluid-survival-tool) adds a graphical user interface to this initial tool and broadens its build-in model checking possibilities. As mentioned above, we plan to include the code base to analyze Stochastic Time Diagrams within libhpng.  
