package com.example;

public class ThreadA extends Thread {

    @Override
    public void run() {
        // ObjectService.methodA();
        new ObjectService().methodA();
    }
}