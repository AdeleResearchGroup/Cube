package fr.liglab.adele.cube.extensions.cilia.monitorsExecutors;

import fr.liglab.adele.cilia.CiliaContext;
import fr.liglab.adele.cilia.builder.Architecture;
import fr.liglab.adele.cilia.builder.Builder;
import fr.liglab.adele.cilia.exceptions.CiliaException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalParameterException;
import fr.liglab.adele.cilia.exceptions.CiliaIllegalStateException;
import fr.liglab.adele.cilia.model.Chain;
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * User: debbabi
 * Date: 9/19/13
 * Time: 10:19 AM
 */
public class CiliaMonitorExecutor extends AbstractMonitorExecutor implements MessagesListener {

    private static final String NAME = "cilia-executor";

    private final String chainId;

    private AutonomicManager agent ;
    private CiliaContext ciliaContext ;

    private String msgObject ;

    private Hashtable<String, Component> instanciatedComponents ;

    private static ArrayList<String> ciliaProperties;
    private int socketAdapterPortCounter = 99999;
    private Hashtable<String, CMessage> msgLocksHT;
    private Object commLock;
    private ArrayList<CiliaBinding> bindings;
    private String connectorType;

    public CiliaMonitorExecutor(Extension extension, CiliaContext cContext) {
        super(extension);
        this.agent = extension.getAutonomicManager();
        //this.chainId = agent.getArchetype().getId() + "-"+agent.getLocalId();
        this.chainId = agent.getArchetype().getId();
        this.msgLocksHT = new Hashtable<String, CMessage>();
        this.ciliaContext =cContext;
        this.instanciatedComponents= new Hashtable<String, Component>();
        this.ciliaProperties = new ArrayList<String>();
        this.ciliaProperties.add("isCilia");
        this.ciliaProperties.add("kind");
        this.ciliaProperties.add("input");
        this.ciliaProperties.add("output");
        this.ciliaProperties.add("ciliaNamespace");
        this.msgObject = "ciliaplugin";
        this.commLock = new Object();
        this.bindings= new ArrayList<CiliaBinding>();
        try{
            this.agent.getCommunicator().addMessagesListener(this.agent.getUri()+"/ciliaplugin", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AutonomicManager getCubeAgent(){
        return this.agent;
    }

    public void setConnectorType(String ct){
        this.connectorType= ct;
    }

    private int getNextPortNumber(){
        int res =socketAdapterPortCounter ;
        socketAdapterPortCounter++;
        return res ;
    }

    public String getName() {
        return NAME;
    }

    public void start() {
        info("\n\n\nCILIA EXTENSION started.\n\n");
        info("connectorType " + this.connectorType) ;

        // 1. create cilia chain
        Builder ciliaBuilder = this.ciliaContext.getBuilder();
        try {
            Architecture chain = ciliaBuilder.create(chainId);
            // 2. read the runtime model, and see if there exist already cube component instances created!
            this.updateCilia();
            ciliaBuilder.done();
        } catch (CiliaException e) {
            e.printStackTrace();
        }
        //3. Initialize the chain
        try {
            this.ciliaContext.getApplicationRuntime().startChain(chainId);
        } catch (CiliaIllegalParameterException e) {
            e.printStackTrace();
        } catch (CiliaIllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void stop() {


    }

    public void destroy() {

    }


    public synchronized void  updateCilia() throws CiliaException {
        info("Executor - update Cilia"  + chainId);
        Builder ciliaBuilder = this.ciliaContext.getBuilder();
        Architecture chain = ciliaBuilder.get(chainId);

        List<ManagedElement> mes = this.agent.getRuntimeModelController().getRuntimeModel().getManagedElements();

        info( mes.size() + " elements found");
        for (ManagedElement elem : mes){
            if (elem instanceof Component){
                Component comp= (Component)elem ;
                info("is cilia ? " +comp.getAttribute("isCilia"));
                try {
                    if (comp.getAttribute("isCilia")!=null){
                        String compID=comp.getUUID() ;
                        createMediator(comp, chain);
                    }
                }catch(CiliaException e){
                    info("problem with component " + elem.getAttribute("type"));
                }
            }
        }
        for (ManagedElement elem : mes){
            if (elem instanceof Component){
                Component comp= (Component)elem ;
                info("is cilia ? " +comp.getAttribute("isCilia"));
                try {
                    if (comp.getAttribute("isCilia")!=null){
                        info( comp.getInputComponents().size() + " input components, " +  comp.getOutputComponents().size() + " output components");
                        for (String outCompID : comp.getOutputComponents()) {
                            this.connectToOutputComponent(comp, outCompID, chain);
                        }
                        for (String inCompID : comp.getInputComponents()) {
                            this.connectToInputComponent(comp, inCompID, chain);
                        }
                    }
                }catch(CiliaException e){
                    info("problem with component " + elem.getAttribute("type"));
                }
            }
        }

        ciliaBuilder.done();
    }

    /**
     * Create Cilia Mediator and add it to the cilia local chain
     * @param i
     * @throws CiliaIllegalParameterException
     */
    private void createMediator(Component i, Architecture chain) throws CiliaException {
        info("Creating Mediator Instance:");
        String componentID = i.getUUID() ;

        String componentType = i.getAttribute("type");

        if  (!instanciatedComponents.containsKey(componentID)){

            info("not yet instantiated " + componentType );

            info("Type " + componentType + " kind " + i.getAttribute("kind") + " ID " + componentID);
            Hashtable  props = this.buildComponentProperties(i, chain);
            if (i.getAttribute("kind") != null && i.getAttribute("kind").equals("adapter")) {
                info("adapter");
                chain.create().adapter().type(componentType).namespace(i.getAttribute("ciliaNamespace")).id(componentID).configure().set(props);
            } else {
                info("mediator");
                chain.create().mediator().type(componentType).namespace(i.getAttribute("ciliaNamespace")).id(componentID).configure().set(props);
            }

            instanciatedComponents.put(componentID, i);

            info("creation done");
        }else{
            info("Already instantiated " + componentType);
        }

    }

    private Hashtable buildComponentProperties(Component comp, Architecture chain) throws CiliaException{
        String componentID = comp.getUUID() ;
        Hashtable CiliaProp = new Hashtable();

        for( Attribute p :  comp.getAttributes()) {
            String pName = p.getName();
            if (!ciliaProperties.contains(pName)){
                CiliaProp.put(pName,p.getValue() );
            }
        }
        return CiliaProp;

    }

    private void connectToOutputComponent(Component comp, String outCompID, Architecture chain) throws CiliaException{
        info("connection to output component");
        Component outComp = (Component) getCubeAgent().getRuntimeModelController().getRuntimeModel().getManagedElement(outCompID) ;

        CiliaBinding binding  = new CiliaBinding(comp.getUUID(), outCompID );

        if(!this.bindings.contains(binding)){

            info("new binding " + binding);
//				info("existing bindings " + this.bindings);
            Chain mchain = this.ciliaContext.getApplicationRuntime().getChain(chainId);

            if (outComp !=null)
            {
                // outComponent is hosted locally
                if (this.instanciatedComponents.containsKey(outCompID)){
                    if(mchain.getMediator(outCompID) != null || mchain.getAdapter(outCompID) != null){
                        String outCompInputPort = outComp.getAttribute("input");
                        if (outCompInputPort ==null ){
                            info("standart input");
                            outCompInputPort="in";
                        }
                        String CompOutputPort = comp.getAttribute("output");
                        if (CompOutputPort ==null ){
                            info("standart output");
                            CompOutputPort="out";
                        }
                        chain.bind().from(comp.getUUID()+":"+CompOutputPort).to(outCompID +":"+ outCompInputPort);
                        this.bindings.add(binding);

                    }
                }else{
                    info("output component not yet instantiated");
                }
            }else{
                try{
                    this.remoteOutput(comp, outCompID, chain);
                    this.bindings.add(binding);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else{
            info("binding already exists");
        }
    }

    private void remoteOutput(Component comp, String outCompID, Architecture chain)throws InterruptedException, CiliaException{

        if(connectorType.equals("joram")){

            String queueID = "queue:" + comp.getUUID() +"-"+ outCompID ;
            info("joram : connect using queue " + queueID );

            String adapterID= comp.getUUID() +"-out-joram-adapter" ;
//			chain.create().adapter().type("JMS2-out-adapter").namespace("fr.liglab.adele.cilia").id(adapterID);
//			chain.configure().adapter().id(adapterID).key("jms.dest").value(queueID);
            chain.create().adapter().type("JMS2-out-adapter").namespace("fr.liglab.adele.cilia").id(adapterID).configure().key("jms.dest").value(queueID);

            String CompOutputPort = comp.getAttribute("output");
            if (CompOutputPort ==null ){
                info("standart output");
                CompOutputPort="out";
            }
            chain.bind().from(comp.getUUID()+":"+CompOutputPort).to(adapterID+":unique");

        }else{
            synchronized(this.commLock)
            {
                boolean nok=true;
                while(nok)
                {
                    if (msgLocksHT.get(comp.getUUID())!=null){
                        info( "message arrivé");
                        nok =false;
                    }
                    else{
                        info( " pas de message");
                        this.commLock.wait();
                    }
                }
            }
            CMessage msg =  this.msgLocksHT.get(comp.getUUID());

            // Create adapter
            String auri = agent.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(outCompID);
            info("default : connect using sockets to " + auri + " "+ msg.getHeader("port"));
            String adapterID= comp.getUUID() +"-out-socket-adapter" ;
            chain.create().adapter().type("tcp-out-adapter").id(adapterID);
            chain.configure().adapter().id(adapterID).key("port").value(msg.getHeader("port"));
            chain.configure().adapter().id(adapterID).key("hostname").value(auri);

            // connect to local component
            String CompOutputPort = comp.getAttribute("output");
            if (CompOutputPort ==null ){
                info("standart output");
                CompOutputPort="out";
            }
            chain.bind().from(comp.getUUID()+":"+CompOutputPort).to(adapterID+":in");
        }
    }

    private void connectToInputComponent( Component comp,  String inCompID, Architecture chain) throws CiliaException {
        info("connecting to input components");
        Component inComp = (Component) getCubeAgent().getRuntimeModelController().getRuntimeModel().getManagedElement(inCompID) ;

        CiliaBinding binding  = new CiliaBinding(inCompID,comp.getUUID());

        if(!this.bindings.contains(binding)){

            info("new binding " + binding);
//				info("existing bindings " + this.bindings);


            Chain mchain = this.ciliaContext.getApplicationRuntime().getChain(chainId);
            if  (inComp != null) {
                info(" local components");
                if  (this.instanciatedComponents.containsKey(inCompID)){
                    if(mchain.getMediator(inCompID) != null || mchain.getAdapter(inCompID) != null) {
                        //in Component is hosted locally
                        String inCompOutputPort = inComp.getAttribute("output");
                        if (inCompOutputPort ==null ){
                            info("standart output");
                            inCompOutputPort="out";
                        }
                        String CompInputPort = comp.getAttribute("input");
                        if (CompInputPort == null ){
                            info("standart input");
                            CompInputPort="in";
                        }
                        chain.bind().from(inCompID+":"+inCompOutputPort).to(comp.getUUID()+":"+CompInputPort);
                        this.bindings.add(binding);
                    }
                } else{
                    info ("input component not yet instanciated");
                }
            } else{
                //intComponent is hosted on a distant host
                remoteInput(comp, inCompID, chain);
                this.bindings.add(binding);
            }
        }else{
            info("binding already exists");
        }
    }

    private void remoteInput(Component comp,  String inCompID, Architecture chain) throws CiliaException{
        info("in component on distant host " + getCubeAgent().getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(inCompID) ) ;

        if(this.connectorType.equals("joram")){
            info("connect using joram");
            String adapterID = comp.getUUID()+"-in-joram-adapter"  ;
            String queueID = "queue:" + inCompID + "-" + comp.getUUID();
//			chain.create().adapter().type("JMS2-in-adapter").namespace("fr.liglab.adele.cilia").id(adapterID);
//			chain.configure().adapter().id(adapterID).key("jms.dest").value(queueID);
            chain.create().adapter().type("JMS2-in-adapter").namespace("fr.liglab.adele.cilia").id(adapterID).configure().key("jms.dest").value(queueID);


            String CompInputPort = comp.getAttribute("input");
            if (CompInputPort == null ){
                info("standart input");
                CompInputPort="in";
            }
            chain.bind().from(adapterID+":unique").to(comp.getUUID()+":"+CompInputPort);
        }else{
            // create  a listener socket adapter
            info("default - connect using socket");
            int port =this.getNextPortNumber();
            String  adapterID = comp.getUUID()+"-in-socket-adapter"  ;
            chain.create().adapter().type("tcp-in-adapter").id(adapterID);
            chain.configure().adapter().id(adapterID).key("port").value(port);

            CMessage msg = new CMessage();
            String auri = agent.getExternalInstancesHandler().getAutonomicManagerOfExternalInstance(inCompID);
            msg.setTo(auri +"/ciliaplugin");
            msg.setObject( this.msgObject);
            msg.setBody("openinginputadapter") ;
            msg.addHeader("srcComponentID", inCompID);
            msg.addHeader("destComponentID", comp.getUUID());

            msg.addHeader("port", port);
            info(" in socket port : " + port);

            //connect it to local component
            String CompInputPort = comp.getAttribute("input");
            if (CompInputPort == null ){
                info("standart input");
                CompInputPort="in";
            }
            chain.bind().from(adapterID+":out").to(comp.getUUID()+":"+CompInputPort);

            // send listener adapter port /joram queue to the agent managing the input Component
            try {
                send(msg);
            } catch(Exception e)
            {}
        }
    }

    public void update(RuntimeModel rm, Notification notification) {
        if (notification.getNotificationType() == RuntimeModelListener.UPDATED_RUNTIMEMODEL) {
            try{
                System.out.println("updateCilia");
                updateCilia();
            }catch(CiliaException e){
            }
        }
    }

    public void send(CMessage msg) throws Exception {
        if (msg != null) {
            info("sending msg");
            msg.setFrom(getCubeAgent().getUri()+"/ciliaplugin");
            msg.setReplyTo(getCubeAgent().getUri() +"/ciliaplugin");
            getCubeAgent().getCommunicator().sendMessage(msg);
        }
    }

    public void receiveMessage(CMessage msg) {
//		info("msg received"+ msg);
        if (msg.getObject().equals(this.msgObject)){
//			info("that would be for us"  );

            synchronized(this.commLock){
                String srcCompID= (String) msg.getHeader("srcComponentID");
                this.msgLocksHT.put(srcCompID, msg) ;
                commLock.notifyAll();
            }

        }
    }


    private void info(String s){
        System.out.println("[CILIA EXECUTOR] "+ s);
    }
}
