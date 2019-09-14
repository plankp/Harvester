package org.atoiks.games.harvest;

import java.awt.Color;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.geom.Rectangle2D;

public class Plant {

    public enum Phase {
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

        public abstract void modifyScore(Score score);
        public abstract void renderImage(Graphics g, int x, int y, int w, int h);
    }

    private static final float IDLE_STATE = -1;
    private static final float START_STATE = 0;

    private static final float PHASE_2_PERCENTAGE = 0.5f;
    private static final float PHASE_3_PERCENTAGE = 0.8f;

    // ---Provided by Main.java---
    public static Image GRASS_IMG;
    public static Image FLOWER_1_IMG;
    public static Image FLOWER_2_IMG;

    static {
        // Sanity check
        if (IDLE_STATE >= START_STATE) {
            throw new IllegalStateException("IDLE_STATE constant must be less than START_STATE constant");
        }

        // Sanity check
        if (PHASE_2_PERCENTAGE >= PHASE_3_PERCENTAGE) {
            throw new IllegalStateException("PHASE_2_PERCENTAGE constant must be less than PHASE_3_PERCENTAGE constant");
        }
    }

    private final Rectangle2D.Double rect;
    private final Score score;

    private float time = IDLE_STATE;
    private float limit = 5;
    private Phase phase;

    public Plant(final double width, final double height, Score score) {
        this.rect = new Rectangle2D.Double(0, 0, width, height);
        this.score = score;
    }

    public boolean update(final float dt) {
        if (!this.isGrowing()) return true;

        final float t = this.time += dt;
        final float lim = this.limit;

        if (t > lim) {
            return false;
        } else if (t > lim * PHASE_3_PERCENTAGE) {
            this.phase = Phase.PHASE_3;
        } else if (t > lim * PHASE_2_PERCENTAGE) {
            this.phase = Phase.PHASE_2;
        }
        return true;
    }

    public boolean isGrowing() {
        return this.time >= START_STATE;
    }

    public void startGrowing() {
        this.time = START_STATE;
        this.phase = Phase.PHASE_1;
    }

    public void harvest() {
        if (!this.isGrowing()) return;

        this.phase.modifyScore(this.score);
        this.time = IDLE_STATE;
    }

    public void renderOutline(Graphics2D g) {
        g.draw(this.rect);
    }

    public void renderContent(Graphics2D g) {
        if (!this.isGrowing()) return;

        this.phase.renderImage(g, 0, 0, (int) this.rect.width, (int) this.rect.height);
    }
}