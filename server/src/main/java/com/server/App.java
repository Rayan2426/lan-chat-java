package com.server;

import java.net.ServerSocket;
import java.net.Socket;

public class App 
{
    public static void main( String[] args )
    {
        try {
            System.out.println("SERVER AVVIATO");
            ServerSocket server = new ServerSocket(3000);
            ServerRouter router = new ServerRouter();
            router.start();
            
            do{
                Socket s = server.accept();
                ServerIn thread = new ServerIn(s,router);
                thread.start();
                System.out.println("Un client si è connesso");
            
            }while(Thread.activeCount() > 0);
          
            server.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("ERRORE IN SERVER");
            System.exit(1);
        }
    }
}
