package fr.liglab.adele.cube.util.perf;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: debbabi
 * Date: 9/26/13
 * Time: 11:08 AM
 */
public class MessageMeasure {

    String timestamp = null;
    String am = null;
    String source = null;
    String comment = null;
    boolean resolved = false;
    long startTime = 0;
    long endTime = 0;
    long duration = 0;

    public MessageMeasure(String am, String source) {
        this.am = am;
        this.source = source;
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

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAm() {
        return am;
    }

    public void setAm(String am) {
        this.am = am;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return timestamp+";"+am+";"+source+";"+duration+";"+resolved+";"+comment;
    }
}
