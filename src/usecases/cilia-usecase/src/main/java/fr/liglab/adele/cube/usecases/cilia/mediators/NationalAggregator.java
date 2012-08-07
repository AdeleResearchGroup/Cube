package fr.liglab.adele.cube.usecases.cilia.mediators;

import java.util.List;

public class NationalAggregator {
	public List process(List data) {
		System.out.println("Calling National Aggregator");
		return data;
	}

}
