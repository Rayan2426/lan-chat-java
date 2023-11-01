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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input = "";
            while (!input.equals("/close")) {
                input = in.readLine();

                System.out.println(input);
            }
            socket.close();
        } catch (Exception e) {
            
        }
    }
    
}
