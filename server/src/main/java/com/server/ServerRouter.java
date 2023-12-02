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
            //RIMUOVE DAL BUFFER IL PACCHETTO APPENA LETTO
            ServerIn.removeFirstPacketInBuffer();
            
            //TROVA IL SOCKET DI DESTINAZIONE
            Socket s = findSocket(packet.get("destination"));

            //SE IL SOCKET DESTINATARIO E' STATO TROVATO
            if(s != null ){
                System.out.println("SOCKET TROVATO\n");
                try {
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeBytes("!m" + packet.get("source") + ":" + packet.get("message") + "\n");
                    System.out.println("MESSAGGIO INVIATO AL DESTINATARIO\n");
                } catch (Exception e) {
                    System.out.println("INVIO AL MITTENTE NON RIUSCITO\n");
                    System.out.println(e.getMessage());
                }
            }
            //SE IL MESSAGGIO E' DA MANDARE IN BROADCAST
            else if(packet.get("destination").equals("broadcast")){
                for(String user: associations.keySet()){
                    if(!user.equals(packet.get("source"))){
                        try {
                            DataOutputStream out = new DataOutputStream(associations.get(user).getOutputStream());
                            out.writeBytes("!b" + packet.get("source") + ":" + packet.get("message") + "\n");
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

    //controlla disponibilità del nome
    public boolean availableUsername(String username){

        if(username == null ||
        username.contains("/") ||
        username.contains("'\'")  ||
        username.equals("") ||
        username.contains(",") ||
        username.contains(";") ||
        username.contains("()") ||
        username.contains(")") ||
        username.contains("[]") ||
        username.contains("]") ||
        username.contains("{}") ||
        username.contains("}") ||
        username.contains("*") ||
        username.contains("|") ||
        username.contains("&") ||
        username.contains("'") ||
        username.contains("\"") ||
        username.contains(">") ||
        username.contains("<") ||
        username.contains(":") ||
        username.contains("+")
        )
            return false;

        for(String name : associations.keySet()){
            if(name.toLowerCase().equals(username.toLowerCase()))
                return false;
        }

        return true;
    }

    //trova una socket in base all'username associato
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

    //aggiunge una connessione al router e notifica gli altri utenti
    public void addConnection(Socket socket, String username){
        associations.put(username, socket);
        for(String user : associations.keySet()){
            if (!user.equals(username)) {
                try {
                    DataOutputStream out = new DataOutputStream(associations.get(user).getOutputStream());
                    out.writeBytes("!j" + username + "\n");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        sockets.add(socket);
    }

    //rimuove una connessione dal router e notifica gli altri utenti
    public void removeConnection(String username){

        associations.remove(username);

        for(String name : associations.keySet()){
                try {
                    Socket s = associations.get(name);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeBytes("!d" + username + "\n");
                    associations.remove(username);
                    System.out.println("RIMOSSO IL SOCKET " + s.getInetAddress() + " di " + username + "\n");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            
        }
        
    }

    //ritorna una lista degli utenti connessi
    public String lista(){
        String lista = "";

        for(String user: associations.keySet()){
            lista += user + "," + associations.get(user).getInetAddress().toString().replace("/", "") + ";";
        }

        return lista.equals("") ? "!l0\n" : "!l" + lista + "\n";
    }
}
