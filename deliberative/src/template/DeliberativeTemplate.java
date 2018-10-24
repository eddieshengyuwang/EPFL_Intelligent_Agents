package template;

/* import table */
import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.util.LinkedList;
import java.util.Queue;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")
public class DeliberativeTemplate implements DeliberativeBehavior {

	enum Algorithm { BFS, ASTAR }
	
	/* Environment */
	Topology topology;
	TaskDistribution td;
	
	/* the properties of the agent */
	Agent agent;
	int capacity;
	double master_time;

	/* the planning class */
	Algorithm algorithm;
	
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
		this.topology = topology;
		this.td = td;
		this.agent = agent;
		
		// initialize the planner
		int capacity = agent.vehicles().get(0).capacity();
		String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");
		
		// Throws IllegalArgumentException if algorithm is unknown
		algorithm = Algorithm.valueOf(algorithmName.toUpperCase());
	}
	
	@Override
	public Plan plan(Vehicle vehicle, TaskSet tasks) {
		Plan plan;

		long startTime = System.currentTimeMillis();
		
		// Compute the plan with the selected algorithm.
		switch (algorithm) {
		case ASTAR:
			// ...
			plan = ASTAR_alg(vehicle, tasks);
			break;
		case BFS:
			// ...
			plan = BFS_alg(vehicle, tasks);
			break;
		default:
			throw new AssertionError("Should not happen.");
		}		
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Plan computed in " + (endTime - startTime) / 1000.0 + "s");
		this.master_time += (endTime - startTime) / 1000.0;
		System.out.println(master_time);
		return plan;
	}
	private Plan BFS_alg(Vehicle vehicle, TaskSet tasks) {
		BFS bfs_class = new BFS();
		
		TaskSet empty = tasks.noneOf(tasks);
		
		Plan plan = bfs_class.BFS(vehicle, tasks, vehicle.getCurrentTasks());
		return plan;
	}
	
	private Plan ASTAR_alg(Vehicle vehicle, TaskSet tasks) {
		TaskSet empty = tasks.noneOf(tasks);
		AStarState start = new AStarState(vehicle.getCurrentCity(), tasks, vehicle.getCurrentTasks(), 0, vehicle.capacity(),
				vehicle.costPerKm(), null, 0, null);
		
		AStarSearch Astar_class = new AStarSearch("hi", start);	
		Plan plan = Astar_class.algo();
		// System.out.println("Total Distance " + plan.totalDistance());
		return plan;
	}
	
	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}

	@Override
	public void planCancelled(TaskSet carriedTasks) {
		
		if (!carriedTasks.isEmpty()) {
			
			// This cannot happen for this simple agent, but typically
			// you will need to consider the carriedTasks when the next
			// plan is computed.
		}
	}
}
