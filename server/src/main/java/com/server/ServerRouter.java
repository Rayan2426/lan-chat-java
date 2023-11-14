package com.server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerRouter extends Thread{
    //list of all the current operative sockets
    private ArrayList<Socket> sockets;
    private HashMap<String, Socket> associations;

    public ServerRouter(){
        this.associations = new HashMap<>();
        this.sockets = new ArrayList<Socket>();
    }

    @Override
    public void run(){

        while(true){
            HashMap<String,String> packet;

            //CERCA PACCHETTI DA INOLTRARE DAL BUFFER
            do{
                packet = ServerIn.getFirstPacketInBuffer();
            }while(packet == null);


            System.out.println("TROVATO PACCHETTO DA " + packet.get("source") + " A " + packet.get("destination"));
            ServerIn.removeFirstPacketInBuffer();
            
            //TROVA IL SOCKET DI DESTINAZIONE
            Socket s = findSocket(packet.get("destination"));

            //SE IL SOCKET DESTINATARIO E' STATO TROVATO
            if(s != null ){
                System.out.println("SOCKET TROVATO\n");
                try {
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeBytes(packet.get("message"));
                    System.out.println("MESSAGGIO INVIATO AL DESTINATARIO\n");
                } catch (Exception e) {
                    System.out.println("INVIO AL MITTENTE NON RIUSCITO\n");
                    System.out.println(e.getMessage());
                }
            }
            else if(packet.get("destination").equals("broadcast")){
                for(String user: associations.keySet()){
                    if(!user.equals(packet.get("source"))){
                        try {
                            DataOutputStream out = new DataOutputStream(associations.get(user).getOutputStream());
                            out.writeBytes(packet.get("message"));
                        } catch (Exception e) {
                            System.out.println("INVIO IN BROADCAST FALLITO");
                            System.out.println(e.getMessage());
                        }
                    }
                }
                System.out.println("INVIO DI BROADCAST DA " + packet.get("source") + " COMPLETATA CON SUCCESSO");
            }
            //SE IL SOCKET DESTINATARIO NON E' STATO TROVATO
            else{
                System.out.println("SOCKET NON TROVATO\n");
                Socket socket = findSocket(packet.get("source"));
                
                try {
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeBytes("<server> il destinatario non e' online al momento o non e' valido\n");
                } catch (Exception e) {
                    System.out.println("INVIO MESSAGGIO DI ERRORE AL MITTENTE NON RIUSCITO\n");
                    if(socket == null)
                        System.out.println("SOCKET SOURCE DI "+ packet.get("source") +" NON TROVATO\n");
                    System.out.println(e.getMessage());
                }
                
                
            }

        }
    }

    public boolean availableUsername(String username){

        if(username.startsWith("/") || username.startsWith("'\'"))
            return false;

        for(String name : associations.keySet()){
            if(name.equals(username))
                return false;
        }

        return true;
    }

    public Socket findSocket(String username){
        Socket s = null;

        for(String name : associations.keySet()){
            if(name.equals(username)){
                s = associations.get(name);
                break;
            }
        }


        return s;
    }

    public void addConnection(Socket socket, String username){
        associations.put(username, socket);
        for(String user : associations.keySet()){
            if (!user.equals(username)) {
                try {
                    DataOutputStream out = new DataOutputStream(associations.get(user).getOutputStream());
                    out.writeBytes("L'utente " + username + " si e' connesso alla chat!\n");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        sockets.add(socket);
    }

    public void removeConnection(String username){
        for(String name : associations.keySet()){
                try {
                    Socket s = associations.get(name);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    if(name.equals(username)){
                        s.close();
                    }
                    out.writeBytes(username + " SI E' DISCONNESSO DALLA CHAT\n");
                    associations.remove(username);
                    System.out.println("RIMOSSO IL SOCKET " + s.getInetAddress() + "\n");
                    return;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            
        }
        
    }

    public String lista(){
        String lista = "";

        for(String user: associations.keySet()){
            lista += user + ":" + associations.get(user).getInetAddress().toString().replace("/", "") + "\n";
        }

        return lista.equals("") ? "non sono presenti ip disponibili\n" : lista;
    }
}
