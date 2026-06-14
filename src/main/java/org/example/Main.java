package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // הדרך הנכונה ובטוחה להפעיל חלונות Swing ב-Java
        SwingUtilities.invokeLater(() -> {
            new SetupUI();
        });
    }
}