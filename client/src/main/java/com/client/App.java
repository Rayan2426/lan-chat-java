package com.client;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        Scanner input = new Scanner(System.in);

        String str = "";
        try {
            Socket mySocket = new Socket("10.22.9.8",3000);

            DataOutputStream out = new DataOutputStream(mySocket.getOutputStream());


            ClientThread thread = new ClientThread(mySocket);

            thread.start();
            while(!str.equals("/close")){
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
