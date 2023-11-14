package com.client;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        HashMap<String,String> mates = new HashMap<>();
        mates.put("pavlov","10.22.9.1");
        mates.put("didi","10.22.9.2");
        mates.put("falli","10.22.9.3");
        mates.put("masi","10.22.9.4");
        mates.put("mohd","10.22.9.5");
        mates.put("socci","10.22.9.6");
        mates.put("molla","10.22.9.7");
        mates.put("rettori","10.22.9.8");
        mates.put("bernat","10.22.9.9");
        mates.put("yasser","10.22.9.10");
        mates.put("grandi","10.22.9.11");
        mates.put("gonza","10.22.9.12");
        mates.put("taiti","10.22.9.13");
        mates.put("singh","10.22.9.14");
        mates.put("ardi","10.22.9.15");
        mates.put("spagni","10.22.9.16");
        mates.put("skorz","10.22.9.17");
        mates.put("aldi","10.22.9.18");
        mates.put("local","localhost");
        mates.put("/change","change");
        mates.put("close","close");


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
