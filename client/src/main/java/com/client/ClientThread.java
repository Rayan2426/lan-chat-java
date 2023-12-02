package com.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip;

public class ClientThread extends Thread{
    Clip clip;

    AudioInputStream audioInputStream; 
    static String filePath = "newmessage.au"; 

    private Socket socket;

    private final String RED = "\u001B[31m";
    private final String GREEN = "\u001B[32m";
    private static String CYAN = "\u001B[36m";
    private static String YELLOW = "\u001B[33m";
    private final String RESET = "\u001B[0m";

    public ClientThread(Socket socket){
        this.socket = socket;
        try {
            // create AudioInputStream object 
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile()); 
            
            // create clip reference 
            clip = AudioSystem.getClip(); 
            
            // open audioInputStream to the clip 
            clip.open(audioInputStream); 
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String protocolConverter(String msg){
        String result = "";
        
        if(msg.startsWith("!l")){
            if(msg.equals("!l0")){
                result = "Non sono presenti altri utenti connessi alla chat!";
            }
            else{
                msg = msg.replaceFirst("!l","");
                String users[] = msg.split(";");
                result = "\nLista utenti connessi:\n";
                for(String s : users){
                    String info[] = s.split(",");
                    String username = info[0];
                    String ip = info[1];
                    result += username + " : " + ip + "\n";
                }
            }
            
        }
        else if(msg.equals("?n")){
            result = CYAN + "<server> Inserire nickname valido" + RESET;
        }
        else if(msg.equals("!n")){
            result = CYAN + "<server> Nome inserito con successo!" + RESET;
        }
        else if(msg.equals(":n")){
            result = CYAN + "<server> Il nickname inserito non e' valido oppure non disponibile, inserisci di nuovo un username!" + RESET;
        }
        else if(msg.equals("!e")){
            result = CYAN + "<server> messaggio inviato non valido" + RESET;
        }
        else if(msg.startsWith("!b") ){
            msg = msg.replaceFirst("!b", "");
            boolean endColumn = msg.endsWith(":");
            String fields[] = msg.split(":");
            String sender = YELLOW +"<" + fields[0] + "(BROADCAST)> ";
            for(int i = 1; i < fields.length; i++){
                result += fields[i] + ":";
            }
            if(!endColumn)
                result = result.substring(0, result.length() - 1);
            result = sender + result + RESET;
        }
        else if(msg.startsWith("!m") ){
            msg = msg.replaceFirst("!m", "");
            boolean endColumn = msg.endsWith(":");
            String fields[] = msg.split(":");
            String sender = "<" + fields[0] + "> ";
            for(int i = 1; i < fields.length; i++){
                result += fields[i] + ":";
            }
            if(!endColumn)
                result = result.substring(0, result.length() - 1);
            result = sender + result;
        }
        else if(msg.startsWith("!d")){
            String user = msg.replaceFirst("!d", "");
            result = RED + user + " si e' disconnesso dalla chat" + RESET;
        }
        else if(msg.startsWith("!j")){
            String user = msg.replaceFirst("!j", "");
            result = GREEN + user + " si e' unito alla chat" + RESET;
        }
        else{
            result = msg;
        }
        
        return result;
    }

    @Override
    public void run(){
        try {
            //input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String input = "";
            while (!input.equals("!c")) {
                //message sent from server, if it equals to '!c' it ends the loop
                input = in.readLine();
                
                if(!input.equals("!c")){
                    System.out.println(protocolConverter(input));
                    clip.stop(); 
                    clip.close(); 
                    try {
                        audioInputStream = AudioSystem.getAudioInputStream( 
                        new File(filePath).getAbsoluteFile()); 
                        clip.open(audioInputStream); 
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    clip.setMicrosecondPosition(0); 
                    clip.start();
                }
                 
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
