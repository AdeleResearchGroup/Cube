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
    String autonomicManager = null;
    String from = null;
    String to = null;
    String headers = null;
    String object = null;
    String body = null;

    public MessageMeasure(String am, String from, String to, String headers, String object, String body) {
        this.autonomicManager = am;
        this.from = from;
        this.to = to;
        this.headers = headers;
        this.object = object;
        this.body = body;
        Date dateNow = new Date ();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd.kk.mm.ss");
        StringBuilder databuilder = new StringBuilder( dateformat.format( dateNow ) );
        timestamp = databuilder.toString();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAutonomicManager() {
        return autonomicManager;
    }

    public void setAutonomicManager(String autonomicManager) {
        this.autonomicManager = autonomicManager;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    @Override
    public String toString() {
        return timestamp+";"+autonomicManager+";"+from+";"+to+";"+headers+";"+object+";"+body;
    }
}
