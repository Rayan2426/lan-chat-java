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
    private String username;
    private BufferedReader input;
    private static ArrayList<HashMap<String,String>> buffer = new ArrayList<HashMap<String,String>>();
    private static Semaforo semaforo = new Semaforo();
    private ServerRouter router;
    private static final String instructions = "<server> digita /close per disconnettersi dalla chat \n" + 
                        "<server> digita '/list' come destinatario se si vuole avere la lista di indirizzi ip disponibili \n" +
                        "<server> usa il formato @USERNAME 'messaggio' per inviare un messaggio in privato a un terminale, altrimenti invierà in broadcast\n"+
                        "<server> digita /help come destinatario per ricevere di nuovo le istruzioni d'uso\n";

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
            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            HashMap<String,String> packet = new HashMap<>();

            out.writeBytes(instructions);
            out.writeBytes("Lista di utenti online: \n" + router.lista());
            System.out.println("ISTRUZIONI INVIATE A " + source + "\n");

            do{

                boolean acceptable = false;

                do{
                    out.writeBytes("<server> Inserire nickname valido\n");
                    String name = input.readLine().replace(" ","");
                    if(router.availableUsername(name) && !name.equals("") && name != null){
                        username = name;
                        acceptable = true;
                    }
                    else if(name.equals("/list")){
                        out.writeBytes(router.lista());
                    }
                    else if(name.equals("/help")){
                        out.writeBytes(instructions);
                    }
                    else if(name.equals("/close")){
                        socket.close();
                    }
                    else{
                        out.writeBytes("Username " + name + " non e' disponibile!\n");
                    }
                }while(!acceptable);
                System.out.println(source + " HA SCELTO UN NICKNAME IDONEO");

                if(!socket.isClosed())
                    //AGGIUNGE IL SOCKET DI QUESTA CONNESSIONE ALLA LISTA NEL ROUTER
                    router.addConnection(socket,username);

                packet.put("source", username);
                
                out.writeBytes("<server> scelta valida\n");

                while (!socket.isClosed()) {
                    

                    String msg = input.readLine();
                    packet.put("message", msg);

                    if(msg.equals("/list")){
                        out.writeBytes(router.lista());
                    }
                    else if(msg.equals("/help")){
                        out.writeBytes(instructions);
                    }
                    else if(msg.equals("/close")){
                        socket.close();
                    }
                    else{
                        String destination;
                        destination = formatDestFromMsg(msg);
                        msg = formatMsg(msg);

                        if(msg.equals("") || msg == null){
                            out.writeBytes("<server> messaggio inviato non valido\n");
                        }
                        else{
                            packet.put("destination", destination);
                            System.out.println("MESSAGGIO RICEVUTO DA " + source + " A "+ destination + ": " + msg);

                            if(destination.equals("broadcast"))
                                msg = "<" + username + "(BROADCAST)> " + msg + "\n";
                            else
                                msg = "<" + username + "> " + msg + "\n";


                            packet.put("message",msg);

                            semaforo.P();

                            buffer.add(packet);

                            semaforo.V();
                        }
                    }
                }
                if(socket.isClosed()){
                    router.removeConnection(username);
                }

            }while(true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String formatDestFromMsg(String msg){
        String dest = "";

        if(msg.startsWith("@")){
            String split[] = msg.split(" ");
            dest = split[0].substring(1);
        }
        else
            dest = "broadcast";

        return dest;
    }

    public String formatMsg(String msg){
        String str = "";

        if(msg.startsWith("@") && !msg.equals("") && msg != null)
            str = msg.substring(formatDestFromMsg(msg).length()+1).trim();
        else{
            str = msg;
        }

        return str;
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
                    index += 1;
                }
            }

        return cont;
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
