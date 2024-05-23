package com.example;

public class ThreadB extends Thread {

    @Override
    public void run() {
        // ObjectService.methodB();
        new ObjectService().methodB();
        // new ObjectService().methodC();
    }
}