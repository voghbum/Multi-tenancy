package com.voghbum.test;

public class TestClass {
    private final ThreadLocal<String> text = ThreadLocal.withInitial(() -> "");
    private String text2 = "";



    public void setText(String text) {
        this.text.set(text);
    }

    public String getText() {
        return text.get();
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }
}
