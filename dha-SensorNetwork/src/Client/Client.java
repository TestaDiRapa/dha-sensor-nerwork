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

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Client implements Runnable {
    
    private int ID;
    private String addressOutput;
    private int portOutput;
    private boolean alive=true;
    
    
   /**
     * Basically the main.Choise the type of type,opens the multicast socket and wait the message of server.
     * Client periodically sends the "alive" message to the server.
     */
    
    @Override
    public void run(){
        
        try {
            Object[] possibleType={"WASHING MACHINE","FRIDGE","LIGHT BULB","THERMOSTAT","OVEN","FISH TANK","TV"};
            String type;
            type=(String)JOptionPane.showInputDialog(null,"Possible Type", "Choise",JOptionPane.QUESTION_MESSAGE,null,possibleType,"TV");
            ID=setID(type);
            System.out.println(ID);
            
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(InetAddress.getByName(ADDRESS));
            //Controllo connessione da parte del server
            //while true xk in caso di disconnessione e poi riconnessione
            while (true){
            byte[] message = new byte[MAX];
            DatagramPacket messagePacket = new DatagramPacket(message, message.length);
            
            multicastSocket.receive(messagePacket);
            
            int serverPort = isServer(messagePacket);

            if(serverPort != -1){
                System.out.println("RICEVUTO HELLO");
                InetAddress addressOutput=messagePacket.getAddress();
                System.out.println("Address server"+addressOutput);

                int port = generatePort();
                DatagramSocket socket;
                while(true){
                    try{
                        socket = new DatagramSocket(port);
                        System.out.println(port);
                        break;
                    } catch (BindException e) {
                        port = generatePort();
                    }
                }

                byte[] buffer = new byte[MAX];
                  
                  //In caso di pausa del client alive diventa false e esce dal while
                  //cosi ritorna nel primo while
                while(alive){
                message = aliveMessage(ID);

                DatagramPacket packet = new DatagramPacket(message, message.length, addressOutput, serverPort);
                socket.send(packet);
                
                System.out.println("INVIATO ALIVE da ID: " +ID);
                Thread.sleep(1000);
                
                }
             }
            }
   
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        
    }
    
    private int generatePort() {
        return new Random().nextInt(2000) + 8000;
    }
    
    /**
     * Controll if the message is from the server, takes the port number from the message content
     * @param packet the packet from the server
     * @return 
     */
    public static int isServer(DatagramPacket packet){
        
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
     * Set the ID number corresponding to the type of client selected
     * @param type the type of client selected
     * @return 
     */
    
    public static int setID(String type){
        switch(type){
            case "WASHING MACHINE": return 0;
            case "FRIDGE": return 1;
            case "LIGHT BULB":return 2;
            case "THERMOSTAT": return 3;
            case "OVEN":return 4;
            case "FISH TANK":return 5;
            case "TV":return 6;
            default : return -1;
        }
    }
    
    
}




