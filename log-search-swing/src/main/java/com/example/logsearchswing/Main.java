package com.example.logsearchswing;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                LogSearcher logSearcher = new LogSearcher();
                new LogSearchGui(logSearcher);
            } catch (Exception e) {
                e.printStackTrace(); // show errors in Console
            }
        });
    }
}