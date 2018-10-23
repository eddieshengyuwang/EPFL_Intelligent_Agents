package template;

import java.util.ArrayList;
import java.util.List;
import logist.topology.Topology.City;
import logist.task.Task;
import logist.task.TaskSet;
import logist.plan.Plan;
import logist.plan.Action;

import logist.plan.Action.Move;
import logist.plan.Action.Delivery;
import logist.plan.Action.Pickup;

public class State {
	public City currentCity;
	public TaskSet availableTasks;
	public TaskSet tasksToDeliver;
	public double costSoFar;
	public double capacity;
	public Plan plan;
	public List<Action> actions;
	public State prevState;
	public Action actionToGetHere;
	
	public State(City currentCity, TaskSet availableTasks, TaskSet tasksToDeliver, double costSoFar,
			double capacity, State prevState, Action actionToGetHere) {
		this.currentCity = currentCity;
		this.availableTasks = availableTasks;
		this.tasksToDeliver = tasksToDeliver;
		this.costSoFar = costSoFar;
		this.capacity = capacity;
		this.actions = new ArrayList<Action>();
		this.prevState = prevState;
		this.actionToGetHere = actionToGetHere;
	}
	
	public State applyMove(City dest, double cost) {
		double newCost = costSoFar + cost;
		
		return new State(dest, availableTasks, tasksToDeliver, newCost, capacity, this, new Move(dest)); 
	}
	
	public State applyPickup(Task task) {
		
		TaskSet nextStateTasks = availableTasks.clone();
		nextStateTasks.remove(task);
		
		TaskSet nextStateDeliveries = tasksToDeliver.clone();
		nextStateDeliveries.add(task);
		
		double newCapacity = capacity - task.weight;

		return new State(currentCity, nextStateTasks, nextStateDeliveries, costSoFar, newCapacity, this, new Pickup(task)); 
	}

	public State applyDelivery(Task task) {

		TaskSet nextStateDeliveries = tasksToDeliver.clone();
		nextStateDeliveries.remove(task);
		
		double newCapacity = capacity + task.weight;

		return new State(currentCity, availableTasks, nextStateDeliveries, costSoFar, newCapacity, this, new Delivery(task)); 
	}
	

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public Plan getPlan() {
		return this.plan;
	}
	
	public boolean isFinalState() {
		return ((availableTasks.size() == 0) && (tasksToDeliver.size() == 0));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availableTasks == null) ? 0 : availableTasks.hashCode());
		long temp;
		temp = Double.doubleToLongBits(capacity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((currentCity == null) ? 0 : currentCity.hashCode());
		result = prime * result + ((plan == null) ? 0 : plan.hashCode());
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
		State other = (State) obj;
		if (availableTasks == null) {
			if (other.availableTasks != null)
				return false;
		} else if (!availableTasks.equals(other.availableTasks))
			return false;
		if (Double.doubleToLongBits(capacity) != Double.doubleToLongBits(other.capacity))
			return false;
		if (currentCity == null) {
			if (other.currentCity != null)
				return false;
		} else if (!currentCity.equals(other.currentCity))
			return false;
		if (plan == null) {
			if (other.plan != null)
				return false;
		} else if (!plan.equals(other.plan))
			return false;
		if (tasksToDeliver == null) {
			if (other.tasksToDeliver != null)
				return false;
		} else if (!tasksToDeliver.equals(other.tasksToDeliver))
			return false;
		return true;
	}
	
}