package template;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

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

 public class MainAgent implements ReactiveBehavior {
	private int numActions;
	private Agent myAgent;
	
	// Map current state with next best action (represented by City)
	private HashMap <State, City> actionTable; 
	
	// Map current state with best values for each action
	private HashMap <State, Double> VTable; 
	
 	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
 		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);
		
		Vehicle vehicle = agent.vehicles().get(0);
		
		// All possible condition for tasks (no task is represented by NULL)
		List<City> tasks = new ArrayList<Topology.City>(topology.cities());		
		tasks.add(null);  
		
		actionTable = new HashMap<State, City>();
		VTable = new HashMap<State, Double>();
		
		boolean goodEnough = false;
		
		do {
			goodEnough = false;			
			
			for (City city: topology.cities()) {
				for (City taskCity: tasks) {
					
					if (taskCity != null && city.id == taskCity.id) continue;
					
					// State S
					State currentState = new State(city, taskCity);
					
					List<City> validActions = getValidDestFrom(currentState);
					
					double maxQ = Double.NEGATIVE_INFINITY;
					City nextAction = null;
					
					for (City action: validActions) {
					
						// R(s,a) 
						double qValue = getReward(currentState, action, vehicle, td);
						
						// Sum of T(s, a, s') for all s' in tasks
						
						City nextStateCity = action;
						
						for (City nextTaskCity: tasks) {
							
							if (nextTaskCity != null && nextStateCity.id == nextTaskCity.id) continue;
							
							// State S'
							State nextState = new State(nextStateCity, nextTaskCity);
							
							double probablity = getProbability(currentState, nextState, action, td);
							
							qValue += discount * probablity * getVTable(nextState);
						}
						
						// Pick max_a Q(s, a)  for each action
						if (qValue > maxQ) {
							maxQ = qValue;
							nextAction = action;
						}
					}
					
					// V(s, a) = max_a Q(s, a)
					if (getVTable(currentState) != maxQ) {
						setVTable(currentState, maxQ);
						setActionTable(currentState, nextAction);
						goodEnough = true;
					}
				}
			}
		} while (goodEnough);
		
		this.numActions = 0;
		this.myAgent = agent;
	}
 	
	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;
		City currentCity = vehicle.getCurrentCity();
		City deliveryCity = null;
		
		if (availableTask != null) {
			deliveryCity = availableTask.deliveryCity;
		}
		
		State state = new State(currentCity, deliveryCity);
		
		City nextAction = getActionTable(state);
		 
		if(nextAction.equals(deliveryCity)) {
			action = new Pickup(availableTask);
		} else {
			action = new Move(nextAction);
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}
	
	private List<City> getValidDestFrom(State state) {
		List<City> validDestCities = new ArrayList<Topology.City>(state.city.neighbors());
		
		if (state.taskCity != null) { // if delivery city is not null
			validDestCities.add(state.taskCity);
		}
		
		return validDestCities;
	}
	
 	
	private double getReward(State state, City destCity, Vehicle vehicle, TaskDistribution td) {
		
		double reward = 0;
		
		if (state.taskCity != null && destCity.id == state.taskCity.id) {
				reward = td.reward(state.city, destCity);
		} 
		
		reward  -= state.city.distanceTo(destCity) * vehicle.costPerKm();
		
		return reward;
	}
	
	private double getProbability(State fromState, State toState, City destCity, TaskDistribution td) {
		
		if ((fromState.city.id == destCity.id) ||
				(fromState.city.id == toState.city.id) ||
				(fromState.taskCity != null && fromState.city.id == fromState.taskCity.id)) {
			return 0.0;
		} else if (destCity.id != toState.city.id) {
			return 0.0;
		} else {
			return td.probability(toState.city, toState.taskCity); 
		}
	}
	
	private double getVTable(State state) {
		if (VTable.get(state) != null) {
			return VTable.get(state);
		} else {
			return 0.0;
		}
	}
	
	private void setVTable(State state, double value) {
		VTable.put(state, value);
	}
	
	private City getActionTable(State state) {
		return actionTable.get(state);
	}
	
	private void setActionTable(State state, City action) {
		actionTable.put(state, action);
	}
	
}