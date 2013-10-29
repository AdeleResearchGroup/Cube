package fr.liglab.adele.cube.util.perf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * User: debbabi
 * Date: 9/26/13
 * Time: 11:13 AM
 */
public class PerformanceChecker {

    List<ResolutionMeasure> resolutionMeasures = new ArrayList<ResolutionMeasure>();
    List<MessageMeasure> messageMeasures = new ArrayList<MessageMeasure>();

    public PerformanceChecker() {
    }

    public synchronized void addResolutionMeasure(ResolutionMeasure m) {
        this.resolutionMeasures.add(m);
    }

    public synchronized void addMessageMeasure(MessageMeasure m) {
        this.messageMeasures.add(m);
    }

    public synchronized void saveToFile() {
        try{
            // Create file
            FileWriter fstream = new FileWriter("resolution.csv");
            BufferedWriter out = new BufferedWriter(fstream);

            for(ResolutionMeasure m : this.resolutionMeasures) {
                out.write(m.toString() + "\n");
            }

            //Close the output stream
            out.close();


            FileWriter fstream2 = new FileWriter("messages.csv");
            BufferedWriter out2 = new BufferedWriter(fstream2);

            for(MessageMeasure m : this.messageMeasures) {
                out2.write(m.toString() + "\n");
            }
            out2.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
