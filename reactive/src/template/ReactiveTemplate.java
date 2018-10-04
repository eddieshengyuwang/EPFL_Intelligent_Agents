package template;

import java.util.List;
import java.util.Random;

import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import logist.LogistPlatform;

public class ReactiveTemplate implements ReactiveBehavior {

	private Random random;
	private double pPickup;
	private int numActions;
	private Agent myAgent;
	private int[][] pickupRewardTable;
	private int[][] moveRewardTable;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);
		
		List<City> allCities = topology.cities();
		this.pickupRewardTable = new int[allCities.size()][allCities.size()];
		this.moveRewardTable = new int[allCities.size()][allCities.size()];
		
		for (int i = 0; i < allCities.size(); ++i) {
			City fromCity = allCities.get(i);
			if (fromCity.id != i) {
				System.out.println("Not same");
			}
			for (int j = 0; j < allCities.size(); ++j) {
				City toCity = allCities.get(j);
				if (toCity.id != j) {
					System.out.println("Not same");
				}
				this.pickupRewardTable[i][j] = td.reward(fromCity, toCity);
				this.moveRewardTable[i][j] = td.reward(fromCity, toCity);
			}
		}
		
		this.random = new Random();
		this.pPickup = discount;
		this.numActions = 0;
		this.myAgent = agent;
		
	}

	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;
		City currentCity = vehicle.getCurrentCity();
		State state = new State(currentCity, vehicle);
		List<City> possibleCities = state.neighbourCities;
		
		
		if (availableTask == null || random.nextDouble() > pPickup ||
	        vehicle.capacity() < availableTask.weight) {
			action = new Move(currentCity.randomNeighbor(random));
		} else {
			action = new Pickup(availableTask);
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}
}
