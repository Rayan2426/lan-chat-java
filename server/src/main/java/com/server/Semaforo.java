package com.server;

public class Semaforo {
    private int value;

    public Semaforo(){
        this.value = 1;
    }

    public synchronized void P(){
        while(value == 0){
            try {
                wait();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        value--;
    }

    public synchronized void V(){
        value++;
        notify();
    }
}
