/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Commons.Constants.ADDRESS;
import static Commons.Constants.MULTICAST_PORT;
import static Commons.ResponseParser.*;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;


/**
 * A class that implements a generic device. Has a reference to the gui.
 */
public class Client implements Runnable {

    private final ClientGUI gui;
    private int port, typeID, kW;
    private String type;
    private boolean stop = false;
    private CSMAManager csma;

    /**
     * Constructor
     * @param gui a ClientGUI instance
     * @param typeID the ID of the type of the device
     * @param kW the power needed
     * @param type a String to display
     */
    Client(ClientGUI gui, int typeID, int kW, String type) {
        this.gui = gui;
        this.typeID = typeID;
        this.kW = kW;
        this.type = type;
    }

    /**
     * Basically the main.Choose the type of type,opens the multicast socket and wait the message of server.
     * Client periodically sends the "alive" message to the server.
     */
    @Override
    public void run(){
        
        try {
            gui.setClient("This is the type selected: "+type);
            gui.setState("OFF");
                        
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(InetAddress.getByName(ADDRESS));

            csma = new CSMAManager(multicastSocket);

            DatagramSocket socket;

            //Generate the correct port
            while(true){
                try{
                    socket = new DatagramSocket(port);
                    break;
                } catch (BindException e) {
                    port = generatePort();
                }
            }

            byte[] message;

            //Instantiates the watchdog
            WatchdogThread watchdog = new WatchdogThread();

            //Continues running until the server is active or the gui is not closed
            while (!watchdog.haveIToStop() && !stop){

                //Receives a message from the server and waits if the CSMA protocol
                //has been activated
                DatagramPacket messagePacket = csma.csmaWait(watchdog);

                int serverPort = helloGetPort(messagePacket);
                Integer freeKW = helloGetFreeWatts(messagePacket);

                //Starts only if it receives a valid message from the server and the
                //available power is more than the power that it needs
                if(serverPort != -1 && freeKW != null && freeKW >= kW){
                    InetAddress addressOutput=messagePacket.getAddress();
                    gui.setServer("Address server: "+addressOutput+" Port: "+serverPort);

                    //CSMA Protocol
                    watchdog = new WatchdogThread();

                    //Start checking the multicast message to check if the maximum power has been reached
                    csma.check(watchdog);

                    //Sends alive (and so it's on) until the OFF button is pressed or the total maximum power
                    //is reached (so CSMA wait starts) or the server stops or the gui closes
                    while(gui.checkONPower() && !csma.disconnect() && !watchdog.haveIToStop() && !stop){
                        gui.setState("ON");
                        message = aliveMessage(typeID, kW);

                        DatagramPacket packet = new DatagramPacket(message, message.length, addressOutput, serverPort);
                        socket.send(packet);
                        gui.setTextArea("ALIVE MESSAGE SENT FROM "+type+"\n");

                        Thread.sleep(1000);
                    }

                    //Stop checking the multicast socket
                    csma.stopChecking();

                    if(!gui.checkONPower()){
                        csma.resetWait();
                        gui.setState("OFF");
                    }
                    else gui.setState("ACTIVATING CSMA PROTOCOL");
                }
                else if(serverPort != -1 && !csma.disconnect()){
                    if(gui.checkONPower()) gui.setState("WAITING TO HAVE ENOUGH FREE POWER");
                }
            }
            JOptionPane.showMessageDialog(null, "Server disconnected!", "Error!", JOptionPane.ERROR_MESSAGE);
            stopProtocol();
   
        } catch (IOException | InterruptedException ex) {
            gui.setState("ERROR!");
            stopProtocol();
        }
         
        
    }

    /**
     * Forces stopping the thread
     */
    void stopProtocol(){
        gui.dispose();
        stop = true;
        csma.stopChecking();
        System.exit(0);
    }

    /**
     * Generate random port number
     * @return a port number between 8000 and 9999
     */
    private int generatePort() {
        return new Random().nextInt(2000) + 8000;
    }

    
   
    
}




