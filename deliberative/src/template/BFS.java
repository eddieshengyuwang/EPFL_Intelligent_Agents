package template;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import logist.plan.Action;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.TaskSet;
import logist.task.Task;
import logist.topology.Topology.City;
import logist.plan.Action.Delivery;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;

public class BFS {
	
	public BFS() {}
	
	public Plan BFS(Vehicle vehicle, TaskSet pickUpTasks, TaskSet DeliverTasks) {
		
		City current = vehicle.getCurrentCity();
		int costPerKm = vehicle.costPerKm();
		
		Queue<State> Q = new LinkedList<State>();		
		State start = new State(current, pickUpTasks, DeliverTasks, 0.0, vehicle.capacity(), null, null);
		
		Q.add(start);
		
		Map<State, Boolean> map = new HashMap<State, Boolean>();
		
		while (!Q.isEmpty()) {
			State n = Q.remove();
			if (n.isFinalState()) {
				Plan plan = new Plan(current, n.actions);
				
				for (Action a : n.actions) {
					System.out.println(a.toLongString());
				}
				
				System.out.println(n.costSoFar);
				
				return plan;
			}
			
			if (map.get(n) != null) {
				continue;
			}
			
			if (map.get(n) == null) {
				
				map.put(n, true);
				Map<String, Boolean> city_map = new HashMap<String, Boolean>();
				
				List<Action> new_action = new ArrayList<Action>(n.actions);
				
				for (Task task : n.tasksToDeliver) {
					
					if (n.currentCity.equals(task.deliveryCity)) {
						
						new_action = new ArrayList<Action>(n.actions);
						Action a = new Delivery(task);
						new_action.add(new Delivery(task));
						State successor = n.applyDelivery(task);
						successor.actions = new_action;
						Q.add(successor);
						
					} else {
						List<City> citiesOnPath = n.currentCity.pathTo(task.deliveryCity);
						City nextMove = citiesOnPath.get(0);
						if (city_map.get(nextMove.name) == null) {
							city_map.put(nextMove.name, true);
							new_action = new ArrayList<Action>(n.actions);
							Action a = new Move(nextMove);
							new_action.add(new Move(nextMove));
							double cost = n.currentCity.distanceTo(nextMove) * costPerKm;
							State successor = n.applyMove(nextMove, cost);
							successor.actions = new_action;

							Q.add(successor);
						}
					}
			    }

				for (Task task : n.availableTasks) {
					
					if (task.weight > n.capacity) continue;
					
					if (n.currentCity.equals(task.pickupCity)) {
						
						new_action =  new ArrayList<Action>(n.actions);
						Action a = new Pickup(task);
						new_action.add(new Pickup(task));
						State successor = n.applyPickup(task);
						successor.actions = new_action;
						Q.add(successor);
					}
					
					else {
						
						List<City> citiesOnPath = n.currentCity.pathTo(task.pickupCity);
								
						City nextMove = citiesOnPath.get(0);
						if (city_map.get(nextMove.name) == null) {
							city_map.put(nextMove.name, true);
							new_action =  new ArrayList<Action>(n.actions);
							Action a = new Move(nextMove);
							new_action.add(new Move(nextMove));
							double cost = n.currentCity.distanceTo(nextMove) * costPerKm;
							State successor = n.applyMove(nextMove, cost);
							successor.actions = new_action;
							
							Q.add(successor);
						}
					}
			    }
			}
		}
		
		return null;
	}
}
