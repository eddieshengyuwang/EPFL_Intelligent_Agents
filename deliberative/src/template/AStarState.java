package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.rank.Max;

import logist.topology.Topology.City;
import logist.task.Task;
import logist.task.TaskSet;
import logist.plan.Plan;
import logist.plan.Action;

import logist.plan.Action.Delivery;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;


public class AStarState {
	public City currentCity;
	public TaskSet availableTasks;
	public TaskSet tasksToDeliver;
	public double costSoFar;
	public double capacityLeft;
	
	public int costPerKm;
	public AStarState prevState;
	public double heuristicCost;
	public Action actionToGetHere;
	
	public AStarState(City currentCity, TaskSet availableTasks, TaskSet tasksToDeliver, double costSoFar, double capacity,
			int costPerKm, AStarState prevState,
			double heuristicCost, 
			Action actionToGetHere
			) {
		this.currentCity = currentCity;
		this.availableTasks = availableTasks;
		this.tasksToDeliver = tasksToDeliver;
		this.costSoFar = costSoFar;
		this.capacityLeft = capacity;
		
		this.costPerKm = costPerKm;
		this.prevState = prevState;
		
		this.heuristicCost = heuristicCost;
		this.actionToGetHere = actionToGetHere;
	}
	
	public double computeHeurisitic(String type,
			TaskSet availableTasks, TaskSet tasksToDeliver, int costPerKm, double costSoFar) {
		
		double deliverMax = 0;
		for (Task t : availableTasks) {
			deliverMax = Math.max(deliverMax,
					currentCity.distanceTo(t.deliveryCity));
		}

		double toPickUpAndDeliverMax = 0;
		for (Task t : availableTasks) {
			toPickUpAndDeliverMax = Math.max(toPickUpAndDeliverMax,
					currentCity.distanceTo(t.pickupCity) + t.pickupCity.distanceTo(t.deliveryCity));
		}
		
		double g = costSoFar;

		double h = Math.max(deliverMax, toPickUpAndDeliverMax);
		double f = g + h;
		return f;
	}
	
	
	public List<AStarState> getSuccessorStates() {

		List<AStarState> successorStates = new ArrayList<AStarState>();
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		
		// Loop through tasks to see if can deliver
		for (Task t : tasksToDeliver) {
			if (t.deliveryCity.equals(currentCity)) {
				// We are in the same city so we can deliver it now.
				
				TaskSet nextStateDeliveries = tasksToDeliver.clone();
				nextStateDeliveries.remove(t);
	
				double newCapacity = capacityLeft + t.weight;
				double heuristic = computeHeurisitic("Deliver",  availableTasks,
						nextStateDeliveries, costPerKm, costSoFar);
				AStarState state = new AStarState(currentCity, availableTasks, nextStateDeliveries, costSoFar, newCapacity
						, costPerKm, this, 
						heuristic, 
						new Delivery(t)
						);
				successorStates.add(state);
			}
		}
		
		// Loop through tasks to see if can pickup
		for (Task t : availableTasks) {
			if (t.weight <= this.capacityLeft && t.pickupCity.equals(currentCity)) {
				TaskSet nextStateTasks = availableTasks.clone();
				nextStateTasks.remove(t);
				
				TaskSet nextStateDeliveries = tasksToDeliver.clone();
				nextStateDeliveries.add(t);
				
				double newCapacity = capacityLeft - t.weight;
				
				double heuristic = computeHeurisitic("Pickup", 
						nextStateTasks, nextStateDeliveries, costPerKm, costSoFar);
				AStarState state = new AStarState(currentCity, nextStateTasks, nextStateDeliveries, costSoFar, newCapacity
						, costPerKm, this, 
						heuristic,
						new Pickup(t));
				successorStates.add(state);	
			}
		}
		
		// Loop through rest of tasks in tasksToDeliver to go to city
		for (Task t : tasksToDeliver) {
			if (!t.deliveryCity.equals(currentCity)) {
				List<City> path = currentCity.pathTo(t.deliveryCity);
				if (path.size() == 0) {
					System.out.println(currentCity.name);
					System.out.println(t.pickupCity.name);
				}
				City nextCityOnWay = path.get(0);
				if (map.get(nextCityOnWay.name) == null) {
					map.put(nextCityOnWay.name, true);
					double cost = currentCity.distanceTo(nextCityOnWay) * this.costPerKm;

					double newCost = costSoFar + cost;

					double heuristic = computeHeurisitic("Move", 
							availableTasks, tasksToDeliver, costPerKm, newCost);
					AStarState state = new AStarState(nextCityOnWay, availableTasks, tasksToDeliver, newCost, capacityLeft
							, costPerKm, this, 
							heuristic, 
							new Move(nextCityOnWay));
					successorStates.add(state);
				}
			}
		}
		
		// Loop through rest of tasks in tasksAvailable to go to city
		for (Task t : availableTasks) {
			if (!(t.weight <= this.capacityLeft && t.pickupCity.equals(currentCity))) {
				// We need to go to this task's pickup site and similarly to the drop case
				// we add the next city toward it.
				List<City> path = currentCity.pathTo(t.pickupCity);
				if (path.size() == 0) {
					System.out.println(currentCity.name);
					System.out.println(t.pickupCity.name);
				}
				City nextCityOnWay = path.get(0);
				if (map.get(nextCityOnWay.name) == null) {
					map.put(nextCityOnWay.name, true);
					double cost = currentCity.distanceTo(nextCityOnWay) * this.costPerKm;
	
					double newCost = costSoFar + cost;
					
					double heuristic = computeHeurisitic("Move", 
							availableTasks, tasksToDeliver, costPerKm, newCost);
					AStarState state = new AStarState(nextCityOnWay, availableTasks, tasksToDeliver, newCost, capacityLeft
							, costPerKm, this, 
							//0, 
							heuristic,
							new Move(nextCityOnWay));
					successorStates.add(state);
				}
			}
		}
		
		return successorStates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availableTasks == null) ? 0 : availableTasks.hashCode());
		long temp;
		temp = Double.doubleToLongBits(capacityLeft);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + costPerKm;
		result = prime * result + ((currentCity == null) ? 0 : currentCity.hashCode());
		result = prime * result + ((tasksToDeliver == null) ? 0 : tasksToDeliver.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AStarState other = (AStarState) obj;
		if (availableTasks == null) {
			if (other.availableTasks != null)
				return false;
		} else if (!availableTasks.equals(other.availableTasks))
			return false;
		if (Double.doubleToLongBits(capacityLeft) != Double.doubleToLongBits(other.capacityLeft))
			return false;
		if (costPerKm != other.costPerKm)
			return false;
		if (currentCity == null) {
			if (other.currentCity != null)
				return false;
		} else if (!currentCity.equals(other.currentCity))
			return false;
		if (tasksToDeliver == null) {
			if (other.tasksToDeliver != null)
				return false;
		} else if (!tasksToDeliver.equals(other.tasksToDeliver))
			return false;
		return true;
	}
	
	public boolean isFinalState() {
		return (availableTasks.size() == 0) && (tasksToDeliver.size() == 0);
	}
}
	
	