package fr.liglab.adele.cube.util.perf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class PerformanceChecker {
	
	List<Measure> measures = new ArrayList<Measure>();
	boolean saveToFile = true;
	
	public PerformanceChecker() {		
	}
	
	public PerformanceChecker(boolean saveToFile) {
		this.saveToFile = saveToFile;
	}
		
	public void addMeasure(Measure m) {
		this.measures.add(m);
		if (this.saveToFile == true) {
			saveToFile();
		}
	}
	
	public void saveToFile() {
		try{
			// Create file 
			FileWriter fstream = new FileWriter("perf.csv");
			BufferedWriter out = new BufferedWriter(fstream);
			
			for(Measure m : this.measures) {
				out.write(m.toString() + "\n");				
			}
			
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
	    }			
	}
}
