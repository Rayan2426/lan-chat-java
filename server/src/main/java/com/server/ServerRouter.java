package com.server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerRouter extends Thread{
    //list of all the current operative sockets
    private ArrayList<Socket> sockets;

    public ServerRouter(){
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
                for(Socket socket: sockets){
                    try {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeBytes(packet.get("message"));
                    } catch (Exception e) {
                        System.out.println("INVIO IN BROADCAST FALLITO");
                        System.out.println(e.getMessage());
                    }
                }
            }
            //SE IL SOCKET DESTINATARIO NON E' STATO TROVATO
            else{
                System.out.println("SOCKET NON TROVATO\n");
                Socket socket = findSocket(packet.get("source"));
                
                try {
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeBytes("<server> il destinatario non e' online al momento, riprovare piu' tardi\n");
                } catch (Exception e) {
                    System.out.println("INVIO MESSAGGIO DI ERRORE AL MITTENTE NON RIUSCITO\n");
                    if(socket == null)
                        System.out.println("SOCKET SOURCE DI "+ packet.get("source") +" NON TROVATO\n");
                    System.out.println(e.getMessage());
                }
                
                
            }

        }
    }

    public Socket findSocket(String ip){
        Socket s = null;

        for(Socket socket : sockets){
            if(ip.contains(socket.getInetAddress().toString()) || socket.getInetAddress().toString().contains(ip)){
                s = socket;
                break;
            }
        }


        return s;
    }

    public void addConnection(Socket socket){
        sockets.add(socket);
    }

    public void removeConnection(String address){
        for(Socket s : sockets){
            if(address.contains(s.getInetAddress().toString())){
                try {
                    sockets.remove(s);
                    s.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        
    }

    public String listaIP(){
        String lista = "";

        for(Socket s: sockets){
            lista += s.getInetAddress().toString().replace("/", "") + "\n";
        }

        return lista.equals("") ? "non sono presenti ip disponibili" : lista;
    }
}
