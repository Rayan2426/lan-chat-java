package com.client;

import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class App 
{
    private static String instructions = "\u001B[36m<server> digita /close per disconnettersi dalla chat \n" + 
                        "<server> digita '/list' come destinatario se si vuole avere la lista di indirizzi ip disponibili \n" +
                        "<server> usa il formato @USERNAME 'messaggio' per inviare un messaggio in privato a un terminale, altrimenti inviera' in broadcast\n"+
                        "<server> digita /help come destinatario per ricevere di nuovo le istruzioni d'uso \u001B[0m \n";
    private static boolean localCommand(String cmd){
        switch(cmd){
            case("/help"):
                System.out.println(instructions);
                return true;
        }
        return false;
    }
    private static String protocolConversion(String p){
        switch(p){
            case("/close"):
                p = "?c";
                break;
            case("/list"):
                p = "?l";
                break;
        }

        return p + "\n";
    }
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

            //prints instructions
            System.out.println(instructions);
            out.writeBytes("?l\n");

            while(!str.equals("?c") || !str.equals("/close")){
                
                //message sent to server, if string equals to '?c', which occurs when the input message is '/close', it closes the connection
                str = input.nextLine();

                if(!localCommand(str)){
                    str = protocolConversion(str);
                    out.writeBytes(str);
                }
                
            }
            thread.join();
            mySocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        input.close();
    }
}

