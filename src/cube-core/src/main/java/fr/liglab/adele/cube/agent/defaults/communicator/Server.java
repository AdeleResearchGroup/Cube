/*
 * Copyright 2011 Adele Team LIG (http://www-adele.imag.fr/)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.liglab.adele.cube.agent.defaults.communicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import fr.liglab.adele.cube.agent.CMessage;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.CubeLogger;

/**
 * Server Socket
 * 
 * @author debbabi
 * 
 */
public abstract class Server {

    private int port;
    private ServerSocket serverSocket;
    private boolean shutdownRequested = false;
    private static long inc = 1;
    private CubeLogger log = null;

    private Map<String, Socket> clients = new HashMap<String, Socket>();

    /**
     * Constructor
     *
     * @param port
     * @param btx
     * @throws Exception
     */
    public Server(int port, BundleContext btx) throws Exception {
        this.port = port;
        log = new CubeLogger(btx, Server.class.getName());

        try {
            serverSocket = new ServerSocket(this.port);
            Thread t = new Thread(new AcceptClients(serverSocket), "Communicator-Server-" + inc++);
            t.start();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public abstract void messageReceived(CMessage msg);

    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);
            log.info("Communicator Server created and listen to port " + this.port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (shutdownRequested == false) {
                    connectToClient(clientSocket);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void connectToClient(Socket clientSocket) {
        try {
            //log.info(" ... connect To Client ...");
			/*
			 * save the client socket to be used after to communicate with that
			 * client
			 */
            String cid = getClientID(clientSocket);
            saveClient(cid, clientSocket);
            //log.info("client:" + cid);
			/* input/output streams */
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();

			/*
			 * BufferedReader plec = new BufferedReader(new InputStreamReader(
			 * clientSocket.getInputStream()));
			 *
			 * PrintWriter pred = new PrintWriter(new BufferedWriter( new
			 * OutputStreamWriter(clientSocket.getOutputStream())), true);
			 *
			 * while (true) { String str = plec.readLine(); // lecture du
			 * message if (str.equals("END")) break;
			 * System.out.println("ECHO = " + str); // trace locale
			 * pred.println(str); // renvoi d'un écho } plec.close();
			 * pred.close();
			 */
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        log.info("Server shutdown..");
        shutdownRequested = true;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getClientID(Socket socket) {
        String cid = null;
        if (socket != null) {
            try {
                cid = "cube://" + socket.getInetAddress().getHostAddress() +"/"+ socket.getPort();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cid;
    }

    private void saveClient(String id, Socket clientSocket) {
        if (this.clients.get(id) == null) {
            this.clients.put(id, clientSocket);
        }
    }

    class AcceptClients implements Runnable {

        private ServerSocket socketserver;
        private Socket socket;
        private int nbrclient = 1;

        final WorkQueue queue = new WorkQueue("server-connected-clients", 5);

        public AcceptClients(ServerSocket s) {
            socketserver = s;
            queue.start();
        }

        public void run() {

            try {
                while (true) {

                    socket = socketserver.accept(); // Un client se connecte on
                    // l'accepte
                    //System.out.println("Le client numéro " + nbrclient
                    //		+ " est connecté !");
                    queue.execute(new ClientWorker(socket));
                    nbrclient++;
                    //socket.close();
                }

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    class ClientWorker implements Runnable {

        private Socket socket;
        private ObjectInputStream ois = null;

        public ClientWorker(Socket s) {
            this.socket = s;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                try {
                    socket.close();
                }catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            //TODO
        }

        public void run() {
            //System.out.println("//////////////// client worker!");
            try {
                CMessage msg = (CMessage) ois.readObject();
                messageReceived(msg);
                // System.out.print(msg.toString());
                socket.close();
                ois.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
	
}
