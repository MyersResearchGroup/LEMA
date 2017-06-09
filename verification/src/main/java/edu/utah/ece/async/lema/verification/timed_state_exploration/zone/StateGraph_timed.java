/*******************************************************************************
 *  
 * This file is part of iBioSim. Please visit <http://www.async.ece.utah.edu/ibiosim>
 * for the latest version of iBioSim.
 *
 * Copyright (C) 2017 University of Utah
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online at <http://www.async.ece.utah.edu/ibiosim/License>.
 *  
 *******************************************************************************/
package edu.utah.ece.async.lema.verification.timed_state_exploration.zone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

import edu.utah.ece.async.lema.verification.lpn.LPN;
import edu.utah.ece.async.lema.verification.lpn.Transition;
import edu.utah.ece.async.lema.verification.platu.platuLpn.LpnTranList;
import edu.utah.ece.async.lema.verification.platu.stategraph.State;
import edu.utah.ece.async.lema.verification.platu.stategraph.StateGraph;
import edu.utah.ece.async.lema.verification.timed_state_exploration.zone.TimedState;
import edu.utah.ece.async.lema.verification.timed_state_exploration.zone.ZoneType;

/**
 * 
 *
 * @author 
 * @author Chris Myers
 * @author <a href="http://www.async.ece.utah.edu/ibiosim#Credits"> iBioSim Contributors </a>
 * @version %I%
 */
public class StateGraph_timed extends StateGraph{

	public StateGraph_timed(LPN lpn) {
		super(lpn);
	}
	
	
	
//	public LpnTranList getEnabled(TimedState curState)
	@Override
	public LpnTranList getEnabled(State curState)
	{
		// TODO: Overrides the base state and must add checks with zones.
		// Note : Can also just make a new method signature with the TimedState
		
//		LpnTranList initEnabled = super.getEnabled(curState);
//		
//		List<Transition> enabledByZone = curState.getEnabledTransitionByZone();
//		
//		for(Transition T : enabledByZone)
//		{
//			initEnabled.remove(T);
//		}
//		
//		return initEnabled;
		
		return new LpnTranList(((TimedState) curState).getEnabledTransitionByZone());
	}
	
	 //public TimedState[] fire(final StateGraph_timed[] curSgArray, 
	//		 final State[] curStateArray, Transition firedTran)
	public State[] fire(final StateGraph_timed[] curSgArray,
			final State[] curStateArray, Transition firedTran)
	 {
		 // TODO: Overrides the base and must add the zone factor.
		 
		 State[] newState = super.fire(curSgArray, curStateArray, firedTran);
		 //TimedState[] newTimedStates = new TimedState[newState.length];
		 State[] newTimedStates = new State[newState.length];
		 
		 for(int i=0; i<newState.length; i++)
		 {
			 LpnTranList enabledTransitions = super.getEnabled(newState[i]);
			 int[] tranArray = new int[enabledTransitions.size()];
			 
			 int j=0;
			 
			 // TODO: Might be able to change the signature of the fire method
			 // for zones so that this translation step is not necessary.
			 for(Transition T : enabledTransitions)
			 {
				 tranArray[j] = T.getIndex();
				 j++;
			 }
			 
			 TimedState ts = (TimedState) curStateArray[i];
			 
			 
			 if(ts.usingGraphs()){
				 ZoneType newZone = ts.getZoneGraph().extractZone()
				 		.fireTransitionbyTransitionIndex(firedTran.getIndex(),
			 				tranArray, newState[i]);
				 
				 newTimedStates[i] = new TimedState(newState[i], newZone, true);
				 
			 }
			 else{
				 ZoneType newZone = ts.getZone()
				 	.fireTransitionbyTransitionIndex(firedTran.getIndex(),
			 				tranArray, newState[i]);
				 
				 newTimedStates[i] = new TimedState(newState[i], newZone, false);
			 }
//			 ZoneType newZone = ((TimedState) curStateArray[i]).getZone()
//			 		.fireTransitionbyTransitionIndex(firedTran.getIndex(),
//			 				tranArray, newState[i]);
			 
			 
			 
//			 newTimedStates[i] = new TimedState(newState[i], newZone);
		 }
		 
		 
		 
		 return newTimedStates;
	 }



	public TimedState getInitStateTimed(boolean usegraph) {
		
		State initialStateNoTime = genInitialState();
		
		// Adds the zone factor.
		if(usegraph){
			return new TimedState(initialStateNoTime, true);
		}
		return new TimedState(initialStateNoTime, false);
	}
	 
	
	//public void outputLocalStateGraph(String file) {
	public void drawLocalStateGraph(String file) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("digraph G {\n");
			for (State curState : nextStateMap.keySet()) {
				String curStateName = "S" + curState.getIndex();
				out.write(curStateName + "[shape=\"ellipse\",label=\"" + curStateName + "\\n" + 
						((TimedState)curState).getZone().toString().replace("\n", "\\n") + "\"]\n");
			}
			
			for (State curState : nextStateMap.keySet()) {
				HashMap<Transition, State> stateTransitionPair = nextStateMap.get(curState);
				for (Transition curTran : stateTransitionPair.keySet()) {
					String curStateName = "S" + curState.getIndex();
					String nextStateName = "S" + stateTransitionPair.get(curTran).getIndex();
					String curTranName = curTran.getLabel();
					out.write(curStateName + " -> " + nextStateName + " [label=\"" + curTranName + "\"]\n");
				}
			}
			out.write("}");
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error outputting state graph as dot file.");
		}
		
	}

	
//	public TimedState addState(State mState) 
//	{
//		return null;
//	}

}
