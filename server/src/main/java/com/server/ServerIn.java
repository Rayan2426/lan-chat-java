package com.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerIn extends Thread{

    private Socket socket;
    private String source;
    private BufferedReader input;
    private static ArrayList<HashMap<String,String>> buffer = new ArrayList<HashMap<String,String>>();
    private static Semaforo semaforo = new Semaforo();
    private ServerRouter router;

    public ServerIn(Socket socket, ServerRouter router){
        this.socket = socket;
        this.router = router;
        this.source = socket.getInetAddress().toString();
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void run(){
        try {
            //AGGIUNGE IL SOCKET DI QUESTA CONNESSIONE ALLA LISTA NEL ROUTER
            router.addConnection(socket);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            HashMap<String,String> packet = new HashMap<>();

            out.writeBytes("<server> invia l'indirizzo ip del destinatario con cui si vuole comunicare \n"+
                        "<server> digita /change come messaggio se si vuole cambiare destinatario dei messaggi \n" + 
                        "<server> digita 'broadcast' come destinatario se si vuole comunicare con tutti i terminali connessi \n");
            System.out.println("ISTRUZIONI INVIATE A " + source + "\n");

            do{
                


                boolean acceptable = false;
                String destination;

                do {
                    out.writeBytes("<server> Inserire indirizzo ip del destinatario\n");
                    destination = input.readLine();

                    acceptable = validDestination(destination);

                    System.out.println("VALIDITA' INDIRIZZO RICEVUTO DA " + source + " : " + acceptable + "\n");

                    if(!acceptable)
                        out.writeBytes("<server> Destinazion non valida\n");
                    
                } while (!acceptable);
                
                out.writeBytes("<server> destinatario trovato\n");

                while (!socket.isClosed()) {
                    

                    packet.put("source", source);
                    packet.put("destination", destination);

                    String msg = input.readLine();
                    packet.put("message", msg);

                    if(!packet.get("message").equals("/change")){
                        
                        System.out.println("MESSAGGIO RICEVUTO DA " + source + " A "+ destination + ": " + msg);

                        packet.put("message","<" + source + "> " + msg + "\n");

                        semaforo.P();

                        buffer.add(packet);

                        semaforo.V();

                    }
                    else{
                        System.out.println(source + " HA INVIATO IL COMANDO /change \n");
                        //out.writeBytes("<server> cambiando destinatario \n<server> inserire il nuovo destinatario \n");
                        break;
                    }
                }

            }while(true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap<String,String> getFirstPacketInBuffer(){
        HashMap<String,String> packet = null;

        semaforo.P();

        if(buffer.size() > 0){
            packet = buffer.get(0);
        }

        semaforo.V();

        return packet;
    }

    public static void removeFirstPacketInBuffer(){
        semaforo.P();

        buffer.remove(0);

        semaforo.V();
    }

    public int findMatches(String string, String substring){
        int cont = 0;
        int index = 0;

         while (index != -1) {

                index = string.indexOf(substring, index);
            
                if (index != -1) {
                    cont++;
                    index += "=".length();
                }
            }

        return cont;
    }

    private boolean validDestination(String destination){
        
        if(validIP(destination) || destination.equals("broadcast"))
            return true;

        return false;
    }

    private boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }
    
            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }
    
            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }
    
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
