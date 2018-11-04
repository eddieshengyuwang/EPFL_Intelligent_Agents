package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class Solution {
	public HashMap<Task, Integer> times;
	public HashMap<Task, Vehicle> vehicle_map;
	public HashMap<Task,Task> nextTask_task;
	public HashMap<Vehicle, Task> nextTask_vehicle;
	public List<Vehicle> vehicles;
	public TaskSet taskSet;
	
	public Solution (HashMap<Task, Integer> times, HashMap<Task, Vehicle> vehicle_map,
			         HashMap<Task, Task> nextTask_task, HashMap<Vehicle, Task> nextTask_vehicle,
			         List<Vehicle> vehicles, TaskSet taskSet) {
		this.times = times;
		this.vehicle_map = vehicle_map;
		this.nextTask_task = nextTask_task;
		this.nextTask_vehicle = nextTask_vehicle;
		this.vehicles = vehicles;
		this.taskSet = taskSet;
	}
	
	public void initialize() {
		
		Task[] tasks = (Task[]) taskSet.toArray();
		int time = 1;
		
		for (int i = 0; i < tasks.length; ++i) {
			if (i == tasks.length-1) {
				nextTask_task.put(tasks[i], null);
			} else {
				nextTask_task.put(tasks[i], tasks[i+1]);
			}
			vehicle_map.put(tasks[i], vehicles.get(0));
			times.put(tasks[i], time);
			time++;
		}
		
		boolean first = true;
		for (Vehicle vehicle : vehicles) {
			if (first) {
				nextTask_vehicle.put(vehicle, tasks[0]); // only first vehicle gets start of tasks
			} else {
				nextTask_vehicle.put(vehicle, null);
			}
		}
	}
	
	public List<Solution> choosingNeighbours() {
		List<Solution> N = new ArrayList<Solution>();
		int num_vehicles = vehicles.size();
		Random random = new Random();
		int i = random.nextInt(num_vehicles);
		while (nextTask_vehicle.get(vehicles.get(i)) == null) {
			i = random.nextInt(num_vehicles);
		}
		
		for (int j = 0; j < num_vehicles; j++) {
			if (j == i) continue;
			Task t = nextTask_vehicle.get(vehicles.get(i));
			if (t.weight <= vehicles.get(j).capacity()) {
				Solution A = changingVehicle(vehicles.get(i), vehicles.get(j));
				N.add(A);
			}
		}
		
		int length = 0;
		Task t = nextTask_vehicle.get(vehicles.get(i));
		while (t != null) {
			t = nextTask_task.get(t);
			length++;
		}
		
		if (length >= 2) {
			for (int tIdx1 = 0; tIdx1 < length-1; tIdx1++) {
				for (int tIdx2 = tIdx1 + 1; tIdx2 < length; tIdx2++) {
					Solution A = changingTaskOrder(vehicles.get(i), tIdx1, tIdx2);
					N.add(A);
				}
			}
		}
		
		return N;
	}
	
	public Solution copySolution (Solution solution) {
		Solution new_solution = new Solution(solution.times,
											 solution.vehicle_map,
											 solution.nextTask_task,
											 solution.nextTask_vehicle,
											 solution.vehicles,
											 solution.taskSet);
		return new_solution;
	}
	
	public Solution changingVehicle(Vehicle vi, Vehicle vj) {
		Solution A1 = copySolution(this);
		Task t = nextTask_vehicle.get(vi);
		A1.nextTask_vehicle.put(vi, A1.nextTask_task.get(t));
		A1.nextTask_task.put(t, A1.nextTask_vehicle.get(vj));
		A1.nextTask_vehicle.put(vj, t);
		A1.updateTime(vi);
		A1.updateTime(vj);
		A1.vehicle_map.put(t, vj);
		return A1;	
	}
	
	public Solution changingTaskOrder(Vehicle vi, int tIdx1, int tIdx2) {
		Solution A1 = copySolution(this);
		Task tPre1 = null;
		Task t1 = A1.nextTask_vehicle.get(vi);
		int count = 0;
		while (count < tIdx1) {
			tPre1 = t1;
			t1 = A1.nextTask_task.get(t1);
			count++;
		}
		Task tPost1 = A1.nextTask_task.get(t1);
		Task tPre2 = t1;
		Task t2 = A1.nextTask_task.get(tPre2);
		count++;
		while (count < tIdx2) {
			tPre2 = t2;
			t2 = A1.nextTask_task.get(t2);
			count++;
		}
		Task tPost2 = A1.nextTask_task.get(t2);
		if (tPost1 == t2) {
			// t2 is delivered immediately after t1
			if (tPre1 == null) {
				A1.nextTask_vehicle.put(vi, t2);
			} else {
				A1.nextTask_task.put(tPre1, t2);
			}
			A1.nextTask_task.put(t2, t1);
			A1.nextTask_task.put(t1, tPost2);
		} else {
			if (tPre1 == null) {
				A1.nextTask_vehicle.put(vi, t2);
			} else {
				A1.nextTask_task.put(tPre1, t2);
			}
			A1.nextTask_task.put(tPre2, t1);
			A1.nextTask_task.put(t2, tPost1);
			A1.nextTask_task.put(t1, tPost2);
		}
		A1.updateTime(vi); 
		return A1;
	}
	
	public void updateTime(Vehicle vehicle) {
		Task ti = nextTask_vehicle.get(vehicle);
		if (ti != null) {
			this.times.put(ti, 1);
			Task tj = nextTask_task.get(ti);
			while (tj != null) {
				this.times.put(tj, this.times.get(ti) + 1);
				ti = tj;
				tj = nextTask_task.get(tj);
			}
		}
	}
	
	public List<Plan> getPlan() {
		List<Plan> list_plan = new ArrayList<>();
		for (Vehicle vehicle : vehicles) {
			City current = vehicle.getCurrentCity();
	        Plan plan = new Plan(current);
	        Task task = nextTask_vehicle.get(vehicle);
	        
	        while (task != null) {
	        	for (City city : current.pathTo(task.pickupCity)) {
	                plan.appendMove(city);
	            }
	        	
	        	plan.appendPickup(task);
	        	
	        	for (City city : task.path()) {
	                plan.appendMove(city);
	            }

	            plan.appendDelivery(task);

	            // set current city
	            current = task.deliveryCity;
	        }
	        list_plan.add(plan);
		}
		return list_plan;
	}
}
