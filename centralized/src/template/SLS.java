package template;

import java.util.List;
import java.util.ArrayList;

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
	
	public SLS(ArrayList<Task> nextTasks, ArrayList<Integer> time, 
			ArrayList<Vehicle> vehicles,  TaskSet tasks) {
		this.nextTasks = nextTasks;
		this.time = time;
		this.vehicles = vehicles;
		this.taskSet = tasks;
	}
	
	public Vehicle getMaxVehicle(List<Vehicle> vehicles) {
		Vehicle vehicle = null;
		int maxCap = 0;
		for (Vehicle v: vehicles) {
			if (vehicle.capacity() > maxCap) {
				maxCap = vehicle.capacity();
				vehicle = v;
			}
		}
		return vehicle;
	}
		
	
	public List<Plan> initialPlan(List<Vehicle> vehicles, TaskSet tasks) {
        long time_start = System.currentTimeMillis();
        
//		System.out.println("Agent " + agent.id() + " has tasks " + tasks);
        Plan planVehicle1 = naivePlan(getMaxVehicle(vehicles), tasks);
        if (planVehicle1 == null) return null; // impossible solution

        List<Plan> plans = new ArrayList<Plan>();
        plans.add(planVehicle1);
        while (plans.size() < vehicles.size()) {
            plans.add(Plan.EMPTY);
        }
        
        long time_end = System.currentTimeMillis();
        long duration = time_end - time_start;
        System.out.println("The plan was generated in "+duration+" milliseconds.");
        
        return plans;
    }
	
	public List<Plan> SLS_algo(ArrayList<Task> nextTasks, ArrayList<Integer> time, ArrayList<Vehicle> vehicles) {
		List<Plan> A = initialPlan(vehicles, taskSet);
		if (A == null) return null; // impossible solution
		
		while (true) {
			List<Plan> A_old = A;
			List<Plan> neighbours = chooseNeighbours(A_old, nextTasks, time, vehicles);
		}
		
		return null;
	}
	
	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
        City current = vehicle.getCurrentCity();
        Plan plan = new Plan(current);

        for (Task task : tasks) {
        	if (task.weight > vehicle.capacity()) {
        		// impossible solution
        		return null;
        	}
            // move: current city => pickup location
            for (City city : current.pathTo(task.pickupCity)) {
                plan.appendMove(city);
            }
            
            plan.appendPickup(task);

            // move: pickup location => delivery location
            for (City city : task.path()) {
                plan.appendMove(city);
            }

            plan.appendDelivery(task);

            // set current city
            current = task.deliveryCity;
        }
        return plan;
    }
}
