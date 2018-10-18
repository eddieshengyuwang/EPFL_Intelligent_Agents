package template;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class BFS {
	
	private Plan BFS(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);
		
		Queue<State> Q = new LinkedList<State>();		
		State start = new State(current, tasks, 0.0, vehicle.capacity(), plan);

		Q.add(start);
		
		Map<State, Boolean> map = new HashMap<State, Boolean>();
		
		while (!Q.isEmpty()) {
			State n = Q.remove();
			if (n.availableTasks.size() == 0) {
				return n.plan;
			}
			if (map.get(n) != null) {
				map.put(n, true);
				
				// move to city
				for (City city : n.currentCity.neighbors()) {
					Plan new_plan = plan;
					new_plan.appendMove(city);
					double cost = n.currentCity.distanceTo(city);
					
					State neighbour_state = new State(city, tasks, cost, vehicle.capacity(), new_plan);
				}
				
				// pickup at current city
				// add another state if there is a task to be pickuped at current city
			}
		}
		
		return plan;
	}
}
