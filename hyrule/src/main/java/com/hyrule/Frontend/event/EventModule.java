package com.hyrule.Frontend.event;

import javax.swing.JFrame;

public class EventModule extends JFrame {
    
    public EventModule() {
        setTitle("Event Module");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        EventModule eventModule = new EventModule();
        eventModule.setVisible(true);
    }
    
}
