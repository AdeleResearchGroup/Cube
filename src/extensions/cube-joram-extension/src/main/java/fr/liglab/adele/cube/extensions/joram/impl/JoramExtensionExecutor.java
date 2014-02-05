package fr.liglab.adele.cube.extensions.joram.impl; 

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.lang.InterruptedException ;
import java.lang.Thread ;


import fr.liglab.adele.cube.AutonomicManager;
import fr.liglab.adele.cube.autonomicmanager.CMessage;
import fr.liglab.adele.cube.autonomicmanager.MessagesListener;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModel;
import fr.liglab.adele.cube.autonomicmanager.RuntimeModelListener;
import fr.liglab.adele.cube.extensions.AbstractMonitorExecutor;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.metamodel.Attribute;
import fr.liglab.adele.cube.metamodel.ManagedElement;
import fr.liglab.adele.cube.metamodel.Notification;


import java.io.File;
import java.io.PrintWriter;


import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSContext;
import javax.jms.ConnectionFactory;
import javax.jms.JMSRuntimeException;

import org.objectweb.joram.client.jms.tcp.TcpConnectionFactory;

import fr.dyade.aaa.agent.AgentServer;


public class JoramExtensionExecutor extends AbstractMonitorExecutor  implements MessagesListener {


    private static final String NAME = "joram-executor";

	private AutonomicManager agent ;

  /** Constant used to specified a queue */
  protected static final String QUEUE = "queue";
  /** Constant used to specified a topic */
  protected static final String TOPIC = "topic";
  

	private short id = 0;
	private String user ="root" ; 
	private String pass= "root" ; 
	private String hostname ="localhost" ;
	private int joramPort = 16010 ;
	private int jndiPort = 16400 ;
	
	
	private AgentServer joramAgent ;
	
	private JoramServerThread server;
	private boolean isStarted = false;


	public JoramExtensionExecutor(Extension extension) {
		super(extension) ;
        this.agent = extension.getAutonomicManager();
	}
	
	public String getName() {
        return NAME;
    }
	
	public AutonomicManager getCubeAgent(){
        return this.agent;
    }
	
	public void setUser(String user){
		this.user= user;
	}
	
	public void setPass(String pass){
		this.pass=pass;
	}

	
	public void setHostname(String hostname){
		this.hostname = hostname;
	}
	
	public void setJoramPort(int port){
		this.joramPort= port;
	}
	

	public void start() {
		info("JORAM EXTENSION started.");
	}

	private void info(String s){
		System.out.println("[JORAM EXECUTOR] "+ s);	
	}
	
	
	private class JoramServerThread extends Thread {
			
		private JoramExtensionExecutor e;

		public JoramServerThread(JoramExtensionExecutor e){
			this.e=e;
		}

	    public void run() {
	    	try{
				e.createAndStartJoramServer();
			} catch (Exception e){
				info(e.toString());
			}
    	}

	}

	public void update(RuntimeModel rm, Notification notification) {
        if (notification.getNotificationType() == RuntimeModelListener.UPDATED_RUNTIMEMODEL) {
        	System.out.println("updateJoram");
        	updateJoram();
        }
    }
	
	public synchronized void  updateJoram(){		

        List<ManagedElement> mes = this.agent.getRuntimeModelController().getRuntimeModel().getManagedElements();
				info("in updateJoram " + mes.size() + " elements") ;

		for (ManagedElement elem : mes){
			if (elem instanceof Component){
				Component comp= (Component)elem ;	
				try {
					if (comp.getAttribute("isJoram")!=null){
						info("component  is joram") ;
						if (!this.isStarted){	
							this.server = new JoramServerThread(this);
							server.start();
//							createAndStartJoramServer();
							info("joram thread started");
							this.isStarted=true;
						}
					}				
				} catch(Exception e){
				}
			}
		}
	}
	
  public void createAndStartJoramServer() throws Exception {
    info("in createAndStartJoramServer");
    StringBuffer strbuf = new StringBuffer();
    
    strbuf.append("<?xml version=\"1.0\"?>\n")
    		.append("<config>\n")
    		.append("<property name=\"Transaction\" value=\"fr.dyade.aaa.ext.NGTransaction\"/>\n" +"<server id=\"")
            .append(this.id)
                  .append("\" name=\"S")
                  .append(this.id)
                  .append("\" hostname=\"")
                  .append(this.hostname)
                  .append("\">\n" +"<service class=\"org.objectweb.joram.mom.proxies.ConnectionManager\" args=\"")
                  .append(this.user).append(' ').append(this.pass)
                  .append("\"/>\n" +"<service class=\"org.objectweb.joram.mom.proxies.tcp.TcpProxyService\" args=\"")
                  .append(this.joramPort)
                  .append("\"/>\n" +"<service class=\"fr.dyade.aaa.jndi2.server.JndiServer\" args=\"")
                  .append(this.jndiPort)
                  .append("\"/>\n" +"</server>\n" + "</config>\n");
    PrintWriter pw = new PrintWriter(new File("a3servers.xml"));
    pw.println(strbuf.toString());
    pw.flush();
    pw.close();
	info("command created " + strbuf);
	try{
	    AgentServer.init((short) this.id, 
                     new File("S" + this.id).getPath(), 
    	                 null);
		info("init done ");
                 
    	AgentServer.start();
		info("\nstarted ");

	} catch(Exception e){
		info("unspecialized exception while starting joram");
		throw e ;
	}
  }
  
  /**
   * Stops a previously started Joram server.
   */
  public static void stopJoramServer() {
    AgentServer.stop();
  }

    public void destroy() {

    }
	public void receiveMessage(CMessage msg) {
	
	}

	public synchronized void stop() {
	}

	@Override
	public String toString() {
		return "JORAM EXECUTOR";
	}

}
