package template;

import java.util.List;

import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class RewardTables {
	public double[][][] PickupRewardTable;
	public double[][][] MoveRewardTable;
	
	public RewardTables(List<Vehicle> vehicles, List<City> cities, TaskDistribution td) {
		
		this.PickupRewardTable = new double[vehicles.size()][cities.size()][cities.size()];
		this.MoveRewardTable = new double[vehicles.size()][cities.size()][cities.size()];

		// TODO Auto-generated constructor stub
		for (Vehicle vehicle : vehicles) {
			for (int i = 0; i < cities.size(); ++i) {
				City fromCity = cities.get(i);
				if (fromCity.id != i) {
					System.out.println("Not same");
				}
				for (int j = 0; j < cities.size(); ++j) {
					City toCity = cities.get(j);
					if (toCity.id != j) {
						System.out.println("Not same");
					}
					double distance = fromCity.distanceTo(toCity);
					double cost = distance * vehicle.costPerKm();
					
					this.PickupRewardTable[vehicle.id()][i][j] = cost;
					this.MoveRewardTable[vehicle.id()][i][j] = td.reward(fromCity, toCity) - cost;
				}
			}
		}
	}
	
}
