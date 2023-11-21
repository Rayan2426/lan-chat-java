package com.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip;

public class ClientThread extends Thread{
    // to store current position 
    Long currentFrame; 
    Clip clip;
      
    // current status of clip 
    String status; 
      
    AudioInputStream audioInputStream; 
    static String filePath = "newmessage.au"; 

    private Socket socket;

    public ClientThread(Socket socket){
        this.socket = socket;
        try {
            // create AudioInputStream object 
        audioInputStream =  
                AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile()); 
          
        // create clip reference 
        clip = AudioSystem.getClip(); 
          
        // open audioInputStream to the clip 
        clip.open(audioInputStream); 
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run(){
        try {
            //input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String input = "";
            while (!input.equals("/close")) {
                //message sent from server, if it equals to '/close' it ends the loop
                input = in.readLine();

                System.out.println(input);
                clip.stop(); 
                clip.close(); 
                try {
                    audioInputStream = AudioSystem.getAudioInputStream( 
                    new File(filePath).getAbsoluteFile()); 
                    clip.open(audioInputStream); 
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                currentFrame = 0L; 
                clip.setMicrosecondPosition(0); 
                clip.start(); 
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
