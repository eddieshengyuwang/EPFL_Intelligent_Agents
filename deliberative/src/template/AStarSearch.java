package template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.print.attribute.standard.MediaSize.Other;

import logist.plan.Action;
import logist.plan.Plan;

public class AStarSearch {
	
	public String heuristic;
	
	//LinkedList<AStarState> Q;	
	
	private final PriorityQueue<AStarState> queue = new PriorityQueue<AStarState>();
	
	Map<AStarState, Boolean> map;
	
	public AStarSearch(String heuristic, AStarState start) {
		this.heuristic = heuristic;
//		this.Q = new LinkedList<AStarState>();
//		this.Q.add(start);
		
		this.queue.add(start);
		
		this.map = new HashMap<AStarState, Boolean>();
		//this.map.put(start, true);
	}
	
	public Plan algo() {
		int i = 0;
		do {
			i++;
			
			// System.out.println(i);
			// if (Q.isEmpty()) return null;
			// AStarState node = Q.remove();
			
			if (queue.isEmpty()) return null;
			AStarState node = queue.remove();
//			
//			if (node.prevState != null) {
//				System.out.println(node.prevState.currentCity.name 
//						+ " " + node.actionToGetHere.toLongString() + " " + node.heuristicCost
//						+ " " + node.tasksToDeliver.toString());
//			}
			
			if (node.isFinalState()) {
				return computePlan(node);
			}
			if (map.get(node) != null) {
				// cycle
				continue;
			}
			map.put(node, true);
			List<AStarState> sortedS = heuristicSort(node.getSuccessorStates());
			// merge(sortedS);	

			// if (node.prevState == null || node.prevState.costSoFar > node.costSoFar) {
				for (AStarState s : sortedS) {
					queue.add(s);
				}
			
		} while (true);
	}
	
	public Plan computePlan(AStarState node) {
		Deque<Action> actions = new LinkedList<Action>();
		while (node.actionToGetHere != null) {
			actions.addFirst(node.actionToGetHere);
			node = node.prevState;
		}
		List<Action> finalActions = new ArrayList(actions);
		return new Plan(node.currentCity, finalActions);			
	}
	
	public List<AStarState> heuristicSort(List<AStarState> successorStates) {
	    successorStates.sort(Comparator.comparing(a -> a.heuristicCost));
		return successorStates;
	}
	
//	public void merge(List<AStarState> sortedActions) {
//		int i = 0;
//		int j = 0;
//		while (i < Q.size() && j < sortedActions.size()) {
//			if (Q.get(i).heuristicCost > sortedActions.get(j).heuristicCost) {
//				Q.add(i, sortedActions.get(j));
//				j++;
//			} else {
//				i++;
//			}
//		}
//		while (j < sortedActions.size()) {
//			Q.add(sortedActions.get(j));
//			j++;
//		}
//	}
}
