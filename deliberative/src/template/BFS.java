package template;

import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.TaskSet;
import logist.task.Task;
import logist.topology.Topology.City;

public class BFS {
	
	public BFS() {}
	
	public Plan BFS(Vehicle vehicle, TaskSet pickUpTasks, TaskSet DeliverTasks) {
		
		City current = vehicle.getCurrentCity();
		int costPerKm = vehicle.costPerKm();
		
		Queue<State> Q = new LinkedList<State>();		
		State start = new State(current, pickUpTasks, DeliverTasks, 0.0, vehicle.capacity());
		
		Plan plan = new Plan(current);
		start.setPlan(plan);

		Q.add(start);
		
		Map<State, Boolean> map = new HashMap<State, Boolean>();
		
		while (!Q.isEmpty()) {
			State n = Q.remove();
			
			if (n.isFinalState()) {
				return n.plan;
			}
			
			if (map.get(n) == null) {
				
				map.put(n, true);
				
				// pickup at current city
				// add another state if there is a task to be picked up at current city
				Map<State, Boolean> visited_neighbours = new HashMap<State, Boolean>();

				for (Task task : n.availableTasks) {
					
					if (task.weight > n.capacity) continue;
					Plan new_plan = plan;
					if (n.currentCity.equals(task.pickupCity)) {
						
						new_plan.appendPickup(task);
						State successor = n.applyPickup(task);
						Q.add(successor);
						successor.setPlan(new_plan);
					} else {
						
						List<City> citiesOnPath = n.currentCity.pathTo(task.pickupCity);
						City nextMove = citiesOnPath.get(0);
						new_plan.appendMove(nextMove);
						double cost = n.currentCity.distanceTo(nextMove) * costPerKm;
						State successor = n.applyMove(nextMove, cost);
						
						if (visited_neighbours.get(successor) == null) {
							Q.add(successor);
							successor.setPlan(new_plan);
						}
					}
			    }
				
				// deliver at current city 
				// add another state if there is a task to be delivered at current city

				for (Task task : n.tasksToDeliver) {
					Plan new_plan = plan;

					if (n.currentCity.equals(task.deliveryCity)) {
						
						new_plan.appendDelivery(task);
						State successor = n.applyDelivery(task);
						Q.add(successor);
						successor.setPlan(new_plan);
					} else {
						List<City> citiesOnPath = n.currentCity.pathTo(task.pickupCity);
						City nextMove = citiesOnPath.get(0);
						new_plan.appendMove(nextMove);
						double cost = n.currentCity.distanceTo(nextMove) * costPerKm;
						State successor = n.applyMove(nextMove, cost);
						
						if (visited_neighbours.get(successor) == null) {
							Q.add(successor);
							successor.setPlan(new_plan);
						}
					}
			    }
			}
		}
		
		return plan;
	}
}
