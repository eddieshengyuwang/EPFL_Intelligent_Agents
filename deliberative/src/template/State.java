package template;

import java.util.List;
import logist.topology.Topology.City;
import logist.task.TaskSet;
import logist.plan.Plan;

public class State {
	public City currentCity;
	public TaskSet availableTasks;
	public double costSoFar;
	public int capacity;
	public Plan plan;
	
	public State(City currentCity, TaskSet availableTasks, double costSoFar, int capacity, Plan plan) {
		this.currentCity = currentCity;
		this.availableTasks = availableTasks;
		this.costSoFar = costSoFar;
		this.capacity = capacity;
		this.plan = plan;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((availableTasks == null) ? 0 : availableTasks.hashCode());
		result = prime * result + capacity;
		long temp;
		temp = Double.doubleToLongBits(costSoFar);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((currentCity == null) ? 0 : currentCity.hashCode());
		result = prime * result + ((plan == null) ? 0 : plan.hashCode());
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
		if (capacity != other.capacity)
			return false;
		if (Double.doubleToLongBits(costSoFar) != Double.doubleToLongBits(other.costSoFar))
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
		return true;
	}
	
}