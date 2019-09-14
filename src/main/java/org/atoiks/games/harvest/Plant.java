package org.atoiks.games.harvest;

import java.awt.Image;
import java.awt.Graphics;

public enum Plant {
    PHASE_1 {
        @Override
        public void modifyScore(Score score) {
            score.addType1();
        }

        @Override
        public void renderImage(Graphics g, int x, int y, int w, int h) {
            g.drawImage(GRASS_IMG, x, y, w, h, null);
        }
    },
    PHASE_2 {
        @Override
        public void modifyScore(Score score) {
            score.addType2();
        }

        @Override
        public void renderImage(Graphics g, int x, int y, int w, int h) {
            g.drawImage(FLOWER_1_IMG, x, y, w, h, null);
        }
    },
    PHASE_3 {
        @Override
        public void modifyScore(Score score) {
            score.addType3();
        }

        @Override
        public void renderImage(Graphics g, int x, int y, int w, int h) {
            g.drawImage(FLOWER_1_IMG, x, y, w, h, null);
            g.drawImage(FLOWER_2_IMG, x, y, w, h, null);
        }
    };

    // ---Provided by Main.java---
    public static Image GRASS_IMG;
    public static Image FLOWER_1_IMG;
    public static Image FLOWER_2_IMG;

    public abstract void modifyScore(Score score);
    public abstract void renderImage(Graphics g, int x, int y, int w, int h);
}