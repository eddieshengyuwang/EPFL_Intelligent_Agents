package template;
import logist.simulation.Vehicle;
import java.util.ArrayList;
import java.util.List;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class State {
	public City city;
	public List<City> neighbourCities;
	public Vehicle vehicle;
	
	public State(City city, Vehicle vehicle) {
		this.city = city;
		this.neighbourCities = city.neighbors();
		this.vehicle = vehicle;
	}	
	
}
