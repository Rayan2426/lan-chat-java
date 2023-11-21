package com.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread{
    private Socket socket;

    public ClientThread(Socket socket){
        this.socket = socket;
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
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
