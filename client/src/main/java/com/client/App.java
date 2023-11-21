package com.client;

import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        Scanner input = new Scanner(System.in);

        String str = "";

        try {
            //becomes true if server is found
            boolean acceptable = false;
            Socket mySocket = new Socket();
            do{
                System.out.println("INSERISCI IP DEL SERVER A CUI CONNETTERSI");
                //ip from keyboard input
                str = input.nextLine();
                try {
                    mySocket = new Socket(str,3000);
                    acceptable = true;
                } catch (UnknownHostException e) {
                    System.out.println("SOCKET SERVER NOT FOUND");
                }
            }while(!acceptable);

            //output stream
            DataOutputStream out = new DataOutputStream(mySocket.getOutputStream());


            //thread for concurrent execution of input reading
            ClientThread thread = new ClientThread(mySocket);

            thread.start();
            while(!str.equals("/close")){
                //message sent to server, if string equals to '/close' it closes the connection
                str = input.nextLine();
                out.writeBytes(str+"\n");
            }
            mySocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        input.close();
    }
}
