package template;

import java.util.List;
import logist.topology.Topology.City;
import logist.task.TaskSet;

public class State {
	public City currentCity;
	public TaskSet availableTasks;
	public int costSoFar;
	public int capacity;
	
	public State(City currentCity, TaskSet availableTasks, int costSoFar, int capacity) {
		this.currentCity = currentCity;
		this.availableTasks = availableTasks;
		this.costSoFar = costSoFar;
		this.capacity = capacity;
	}
}