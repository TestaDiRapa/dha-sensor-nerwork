/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Commons.Constants.ADDRESS;
import static Commons.Constants.MAX;
import static Commons.Constants.MULTICAST_PORT;
import static Commons.ResponseParser.aliveMessage;
import static Commons.ResponseParser.helloGetFreeWatts;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Client implements Runnable {
    
    private int typeID;
    private String addressOutput;
    private int portOutput;
    private final ClientGUI gui;
    private int port;
    private int kW;
    private Integer freeKW;
    
    public Client(ClientGUI gui){
        this.gui=gui;
    }
    
    
   
   /**
     * Basically the main.Choise the type of type,opens the multicast socket and wait the message of server.
     * Client periodically sends the "alive" message to the server.
     */
    
    @Override
    public void run(){
        
        try {
            Object[] possibleType={"WASHING MACHINE [240w]","FRIDGE [305w]","LIGHT BULB [150w]","THERMOSTAT [750w]","OVEN [1500w]","FISH TANK [400w]","TV [150w]"};
            String type;
            type=(String)JOptionPane.showInputDialog(null,"Possible Type", "Choise",JOptionPane.QUESTION_MESSAGE,null,possibleType,"TV");
            typeID=setID(type)[0];
            kW=setID(type)[1];
            gui.setClient("This is the type selected: "+type);
            gui.setState("OFF");
                        
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(InetAddress.getByName(ADDRESS));

            CSMAManager csma = new CSMAManager(multicastSocket);
            csma.csmaWait();

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

            //The communication starts
            byte[] message;
            while (true){
                DatagramPacket messagePacket = csma.csmaWait();

                int serverPort = isServer(messagePacket);
                freeKW=helloGetFreeWatts(messagePacket);

                //If is the "HELLO" message by the server
                if(serverPort != -1 && freeKW != null && freeKW>=kW){
                    InetAddress addressOutput=messagePacket.getAddress();
                    gui.setServer("Address server: "+addressOutput+" Port: "+serverPort);

                    csma.check();

                    while(gui.checkONPower() && !csma.disconnect()){
                        gui.setState("ON");
                        message = aliveMessage(typeID, kW);

                        DatagramPacket packet = new DatagramPacket(message, message.length, addressOutput, serverPort);
                        socket.send(packet);

                        Thread.sleep(1000);
                    }

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
   
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        
    }

    /**
     * Generate random port number
     * @return a port number between 8000 and 9999
     */
    private int generatePort() {
        return new Random().nextInt(2000) + 8000;
    }
    
    /**
     * Control if the message is from the server, takes the port number from the message content
     * @param packet the packet from the server
     * @return the port number or -1
     */
    private static int isServer(DatagramPacket packet){
        
        String payload = new String(packet.getData());
        if(payload.matches("HELLO[0-9]{4}.*")){
            Pattern p = Pattern.compile("(HELLO)([0-9]{4})(.*)");
            Matcher m = p.matcher(payload);
            m.find();
            return Integer.parseInt(m.group(2));
        }
        return -1;
    } 
    
    /**
     * Set the typeID number and kw corresponding to the type of client selected
     * @param type the type of client selected
     * @return v[0] is a typeID while v[1] is the kW
     */
    
    private static int[] setID(String type){
        int[] v = new int[2];
        switch(type){
            case "WASHING MACHINE [240w]": v[0]=0; v[1]=240;break;
            case "FRIDGE [305w]": v[0]=1;v[1]=305;break; 
            case "LIGHT BULB [150w]":v[0]=2;v[1]=150;break; //All the lighting 
            case "THERMOSTAT [750w]": v[0]=3;v[1]=750;break; 
            case "OVEN [1500w]":v[0]=4;v[1]=1500;break;
            case "FISH TANK [400w]":v[0]=5;v[1]=400;break; 
            case "TV [150w]":v[0]=6;v[1]=150;break;
            default : v[0]=-1;v[1]=0;
        }
        return v;
    }
    
   
    
}




