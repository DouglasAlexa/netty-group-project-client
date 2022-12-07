package org.example;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        new NettyClient("localhost", 8081).start();
    }
}