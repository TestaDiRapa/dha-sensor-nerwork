/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Commons.Constants.ADDRESS;
import static Commons.Constants.MAX;
import static Commons.Constants.MULTICAST_PORT;
import static Commons.Constants.PORT;
import static Commons.Constants.PORTSERVER;
import static Commons.ResponseParser.isAlive;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.plaf.OptionPaneUI;

public class Client implements Runnable {
    
    private int ID;
    private String addressOutput;
    private int portOutput;
    private boolean alive=true;
    
    
    
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
            //Da mettere MAX
            byte[] message = new byte[5];
            DatagramPacket messagePacket = new DatagramPacket(message, message.length);
            
            multicastSocket.receive(messagePacket);
            
            
             if(isServer(messagePacket)){
                System.out.println("RICEVUTO HELLO");
                InetAddress addressOutput=messagePacket.getAddress();
                System.out.println("Address server"+addressOutput);
                DatagramSocket socket = new DatagramSocket(PORT);
                  byte[] buffer = new byte[MAX];
                  
                  //In caso di pausa del client alive diventa false e esce dal while
                  //cosi ritorna nel primo while
                while(alive){
                message="ALIVE".getBytes(StandardCharsets.UTF_8);
                //DA CAMBIARE PORTSERVER xk lo deve prendere autonomamente la porta
                DatagramPacket packet = new DatagramPacket(message, message.length, addressOutput, PORTSERVER);
                socket.send(packet);
                
                System.out.println("INVIATO ALIVE da ID: " +ID);
                Thread.sleep(1000);
                
                }
             }
            }
   
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        
    }
    
    
    
    public static boolean isServer(DatagramPacket packet){
        
        String payload = new String(packet.getData());
        System.out.println(payload.length());
        //matchare anche con la porta, non funziona equals xk vale il MAX del message
        if(payload.equals("HELLO")){
            return true;
        }
        return false;
    } 
    
    
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




