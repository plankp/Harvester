package org.atoiks.games.harvest;

import java.io.IOException;

import java.awt.Font;
import java.awt.Image;
import java.awt.FontFormatException;

import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        // Load resources
        final Font fnt;
        try {
            fnt = Font
                    .createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/font/Logisoso.ttf"))
                    .deriveFont(16f);
        } catch (NullPointerException | FontFormatException | IOException ex) {
            throw new RuntimeException("Font loading failed!", ex);
        }

        try {
            Plant.GRASS_IMG = ImageIO.read(Main.class.getResourceAsStream("/image/grass.png"));
            Plant.FLOWER_1_IMG = ImageIO.read(Main.class.getResourceAsStream("/image/flower_1.png"));
            Plant.FLOWER_2_IMG = ImageIO.read(Main.class.getResourceAsStream("/image/flower_2.png"));
        } catch (IOException ex) {
            throw new RuntimeException("Image loading failed!", ex);
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
