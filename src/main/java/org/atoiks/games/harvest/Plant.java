package org.atoiks.games.harvest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import java.awt.geom.Rectangle2D;

public class Plant {

    private static final float IDLE_STATE = -1;
    private static final float START_STATE = 0;

    private static final float PHASE_2_PERCENTAGE = 0.5f;
    private static final float PHASE_3_PERCENTAGE = 0.8f;

    public static final Color PHASE_1 = Color.green;
    public static final Color PHASE_2 = Color.yellow;
    public static final Color PHASE_3 = Color.red;

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
    private Color color;

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
            this.color = PHASE_3;
        } else if (t > lim * PHASE_2_PERCENTAGE) {
            this.color = PHASE_2;
        }
        return true;
    }

    public boolean isGrowing() {
        return this.time >= START_STATE;
    }

    public void startGrowing() {
        this.time = START_STATE;
        this.color = PHASE_1;
    }

    public void harvest() {
        if (!this.isGrowing()) return;

        final Color c = this.color;
        if (c == PHASE_1) {
            this.score.addType1();
        } else if (c == PHASE_2) {
            this.score.addType2();
        } else if (c == PHASE_3) {
            this.score.addType3();
        }

        this.time = IDLE_STATE;
    }

    public void renderOutline(Graphics2D g) {
        g.draw(this.rect);
    }

    public void renderContent(Graphics2D g) {
        if (!this.isGrowing()) return;

        g.setColor(this.color);
        g.fill(this.rect);
    }
}