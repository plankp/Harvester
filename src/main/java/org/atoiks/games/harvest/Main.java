package org.atoiks.games.harvest;

import java.io.IOException;

import java.awt.Font;
import java.awt.FontFormatException;

public class Main {

    public static void main(String[] args) {
        // Load resources
        final Font fnt;
        try {
            fnt = Font
                    .createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/Logisoso.ttf"))
                    .deriveFont(16f);
        } catch (NullPointerException | FontFormatException | IOException ex) {
            throw new RuntimeException("Font loading failed!", ex);
        }

        // Initialize window
        final Window window = new Window(fnt);
        window.start();

        // --- Terrible game loop ---
        while (true) {
            window.update(0.02f);
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                //
            }
        }
    }
}
