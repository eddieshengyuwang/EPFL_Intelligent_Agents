package template;

import java.util.List;
import logist.topology.Topology.City;

public class State {
	public City city;
	public City taskCity;
	
	public State(City city, City taskCity) {
		this.city = city;
		this.taskCity = taskCity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((taskCity == null) ? 0 : taskCity.hashCode());
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
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (taskCity == null) {
			if (other.taskCity != null)
				return false;
		} else if (!taskCity.equals(other.taskCity))
			return false;
		return true;
	}		
}