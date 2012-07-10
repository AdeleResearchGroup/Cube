package fr.liglab.adele.cube.util.perf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Measure {
		
	String cube = "";
	String source_instance = "";
	List<String> new_valid_instances = new ArrayList<String>();
	String timestamp=null;
	long startTime = 0;
	long endTime = 0;
	long duration = 0;
	
	public Measure(String cube, String source_instance) {			
		this.cube = cube;
		this.source_instance = source_instance;		
	}

	public void start() {
		// timestamp
		Date dateNow = new Date ();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd.kk.mm.ss");        
        StringBuilder databuilder = new StringBuilder( dateformat.format( dateNow ) );         
        timestamp = databuilder.toString();
        // start calculus
		startTime = System.nanoTime();;
	}
	
	public void end() {
		endTime = System.nanoTime();	
	}
	
	public void calculate() {
		duration = endTime - startTime;
	}
	
	public String getTimestamp() {
		return timestamp;
	}	

	public String getCube() {
		return cube;
	}
	
	public String getSource_instance() {
		return source_instance;
	}

	public List<String> getNew_valid_instances() {
		return new_valid_instances;
	}

	public String getNew_valid_instancesAsString() {
		String out = "";
		if (new_valid_instances.size() > 0) {
			for (int i=0; i<new_valid_instances.size(); i++) {			
				out += new_valid_instances.get(i);			
				if (i<new_valid_instances.size()-1) {
					out += " ";
				}
			}
		}
		return out;
	}
	
	public void addNewValidInstance(String instance) {
		this.new_valid_instances.add(instance);
	}
	
	public long getDuration() {
		return duration;
	}
	
	@Override
	public String toString() {
		return timestamp + ";" + cube + ";" + source_instance + ";" + "\"" +getNew_valid_instancesAsString()+"\"" + ";" + duration;
	}
}
