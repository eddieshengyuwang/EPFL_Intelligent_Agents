package template;

import java.util.List;
import java.util.Random;

import java.util.ArrayList;
import java.util.Collections;

import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;
import logist.plan.Plan;


public class SLS {
	public ArrayList<Task> nextTasks;
	public ArrayList<Integer> time;
	public ArrayList<Vehicle> vehicles;
	public TaskSet taskSet;
	public List<Plan> finalPlan;
	
	public SLS(ArrayList<Task> nextTasks, ArrayList<Integer> time, 
			ArrayList<Vehicle> vehicles,  TaskSet tasks) {
		this.nextTasks = nextTasks;
		this.time = time;
		
		// sort vehicles in constructor desc by capacity
		vehicles.sort((o1, o2) -> Integer.compare(o2.capacity(), o1.capacity()));
		this.vehicles = vehicles;
		this.taskSet = tasks;
	}
	
	
	// TODO: define below
	public boolean isSatisfiable(Solution s) {
		return false;
	}
	
	public boolean checkPossible() {
		Vehicle vehicle = vehicles.get(0); // note vehicles sorted by descending in constructor
		// so above will be vehicle with max capacity
		
		for (Task task : taskSet) {
        	if (task.weight > vehicle.capacity()) {
        		// impossible solution
        		return false;
        	}
		}
		return true;
	}
	
	public List<Plan> SLS_algo() {
		if (checkPossible()) {

			Solution solution = new Solution(null, null, null, null, vehicles, taskSet);
			solution.initialize();

			int max_iters = 10000;
			for (int i = 0; i < max_iters; i++) {
				Solution solution_old = solution;
				List<Solution> neighbours = solution_old.choosingNeighbours();
				solution = localChoice(neighbours);

				if (solution == null) {
					return null; // Error?
				}

				if (isSatisfiable(solution)) {
					return solution.getPlan();
				}
			}
			return solution.getPlan();
		}
		return null;
	}
	
	public double dist_tt(Task ti, Task tj) {
		if (tj == null) {
			return 0;
		}
		List<City> city_path = ti.deliveryCity.pathTo(tj.pickupCity);
		double distance = 0;
		City prevCity = city_path.get(0);
		for (int i = 1; i < city_path.size(); i++) {
			distance += prevCity.distanceTo(city_path.get(i));
			prevCity = city_path.get(i);
		}
		return distance;
	}
	
	public double length(Task ti) {
		if (ti == null) {
			return 0;
		}
		return ti.pathLength();
	}
	
	public double dist_tv(Vehicle vehicle, Task tj) {
		if (tj == null) {
			return 0;
		}
		List<City> city_path = vehicle.getCurrentCity().pathTo(tj.pickupCity);
		double distance = 0;
		City prevCity = city_path.get(0);
		for (int i = 1; i < city_path.size(); i++) {
			distance += prevCity.distanceTo(city_path.get(i));
			prevCity = city_path.get(i);
		}
		return distance;
	}
	
	public double costFn (Solution solution) { 
		// the costFn is basically to sum up the cost of 
		// doing all the tasks in order for each vehicle 
		double cost = 0;
		List<Task> tasks = new ArrayList<Task> (solution.nextTask_task.keySet());
		for (int i = 0; i < tasks.size(); ++i) {
			Task ti = tasks.get(i);
			cost += (dist_tt(ti, solution.nextTask_task.get(ti)) + 
					 length(solution.nextTask_task.get(ti))) * 
					solution.vehicle_map.get(ti).costPerKm();
		}
		
		for (Vehicle v: solution.vehicles) {
			cost += (dist_tv(v, solution.nextTask_vehicle.get(v)) + 
					 length(solution.nextTask_vehicle.get(v))) *
					v.costPerKm();
		}
		return cost;
	}
	
	public Solution localChoice(List<Solution> solutions) {
		if (solutions.size() == 0) {
			System.out.println("Error? Empty solutions");
			return null;
		}
		List<Solution> best_solns = new ArrayList<Solution>();
		List<Double> solns = new ArrayList<>();
		
		for (Solution solution : solutions) {
			solns.add(costFn(solution));
		}
		
		double best_cost = Collections.min(solns);
		for (int i = 0; i < solns.size(); i++) {
			if (solns.get(i) == best_cost) {
				best_solns.add(solutions.get(i));
			}
		}
		
		Random rand = new Random();
		return best_solns.get(rand.nextInt(best_solns.size()));
	}
}
