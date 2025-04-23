package com.voghbum.test;

public class TestMain {
    public static void main(String[] args) {
        TestClass test = new TestClass();

        Thread th1 = new Thread(() -> {
            test.setText("th1");
            test.setText2("th11");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(test.getText());
            System.out.println(test.getText2());
        });
        Thread th2 = new Thread(() -> {
            test.setText("th2");
            test.setText2("th22");
            System.out.println(test.getText());
            System.out.println(test.getText2());
        });

        th1.start();
        th2.start();
    }
}
