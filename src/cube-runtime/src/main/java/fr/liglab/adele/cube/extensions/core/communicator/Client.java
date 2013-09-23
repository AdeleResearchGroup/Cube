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

package fr.liglab.adele.cube.extensions.core.communicator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.liglab.adele.cube.util.Utils;


public class Client implements Runnable {

    private String host;
    private long port;
    private Socket socket;
    private ObjectOutputStream oos = null;
    private Object obj;

    public Client(Object obj, String host, long port) {
        this.obj = obj;
        this.host = host;
        this.port = port;
        try {
            socket = new Socket(this.host, Utils.safeLongToInt(this.port));
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    public void run() {
        try {
            oos.writeObject(obj);
            oos.flush();
            // close streams and connections
            oos.close();
            socket.close();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                //e1.printStackTrace();
            }
        }
    }
}
