package org.atoiks.games.harvest;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.util.Random;

import javax.swing.JPanel;

public class GameFrame extends JPanel {

    private static final Random RAND_GEN = new Random();

    private static final float LIMIT_UPPER_BOUND = 5.0f;
    private static final float LIMIT_LOWER_BOUND = 1.5f;

    private static final int VIRTUAL_WIDTH = 6;
    private static final int VIRTUAL_HEIGHT = 4;

    private static final int PADDING_X = 20;
    private static final int PADDING_Y = 80;

    private Insets insets;
    private boolean renderScoreOverlay = false;
    private boolean gameOver = false;

    private float time = 0;
    private float limit = LIMIT_UPPER_BOUND;

    private final Font font16;
    private final Font font48;

    private final double screenWidth;
    private final double screenHeight;

    private final double cellWidth;
    private final double cellHeight;

    private final Cell[][] grid = new Cell[VIRTUAL_WIDTH][VIRTUAL_HEIGHT];

    private final Score score = new Score();

    public GameFrame(Font fnt) {
        this.font16 = fnt.deriveFont(16f);
        this.font48 = fnt.deriveFont(48f);

        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        this.screenWidth = d.getWidth();
        this.screenHeight = d.getHeight();

        this.cellWidth = (d.getWidth() - PADDING_X * 2) / VIRTUAL_WIDTH;
        this.cellHeight = (d.getHeight() - PADDING_Y * 2) / VIRTUAL_HEIGHT;

        for (int i = 0; i < VIRTUAL_WIDTH; ++i) {
            for (int j = 0; j < VIRTUAL_HEIGHT; ++j) {
                this.grid[i][j] = new Cell(this.cellWidth, this.cellHeight, this.score);
            }
        }

        this.addMouseListener(new MouseInputHandler());
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    public void update(final float dt) {
        if (this.renderScoreOverlay || this.gameOver) return;

        for (int i = 0; i < VIRTUAL_WIDTH; ++i) {
            for (int j = 0; j < VIRTUAL_HEIGHT; ++j) {
                if (!this.grid[i][j].update(dt)) {
                    this.gameOver = true;
                    return;
                }
            }
        }

        if ((this.time += dt) > this.limit) {
            // Reset timer
            this.time -= this.limit;

            // Make growth-trigger go faster
            this.limit = Math.max(this.limit - dt, LIMIT_LOWER_BOUND);

            // Randomly select a cell to try to grow!
            final int x = RAND_GEN.nextInt(VIRTUAL_WIDTH);
            final int y = RAND_GEN.nextInt(VIRTUAL_HEIGHT);
            this.grid[x][y].requestGrowing();
        }
    }

    private Point2D getCellPoint(int i, int j) {
        return new Point2D.Double(
                i * this.cellWidth + PADDING_X,
                j * this.cellHeight + PADDING_Y);
    }

    @Override
    public void paintComponent(final Graphics gr) {
        if (this.insets == null) return;

        final Graphics2D g = (Graphics2D) gr;
        if (this.renderScoreOverlay || this.gameOver) {
            g.setColor(Color.gray);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            if (this.gameOver) {
                g.setFont(this.font48);
                g.setColor(Color.black);
                g.drawString("GAME OVER!", 200, 60);
                g.translate(0, 80);
            }

            g.setColor(Plant.PHASE_1);
            g.fillRect(40, 40, 10, 10);
            g.setColor(Plant.PHASE_2);
            g.fillRect(40, 60, 10, 10);
            g.setColor(Plant.PHASE_3);
            g.fillRect(40, 80, 10, 10);

            g.setFont(this.font16);
            g.setColor(Color.black);
            g.drawString("x" + this.score.getType1(), 85, 40 + 10);
            g.drawString("x" + this.score.getType2(), 85, 60 + 10);
            g.drawString("x" + this.score.getType3(), 85, 80 + 10);

            g.drawString("Score", 40, 120);
            g.drawString(Integer.toString(this.score.getScore()), 85, 120);

            if (this.renderScoreOverlay) {
                g.drawString("ETA", 40, 160);
                g.drawString(String.format("%.2fs", Math.max(this.limit - this.time, 0)), 85, 160);
            }
        } else {
            g.setColor(Color.black);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            final Point origin = this.getLocationOnScreen();
            final double tx = this.insets.left - origin.getX();
            final double ty = this.insets.top - origin.getY();
            g.translate(tx, ty);

            for (int i = 0; i < VIRTUAL_WIDTH; ++i) {
                for (int j = 0; j < VIRTUAL_HEIGHT; ++j) {
                    final Point2D p = this.getCellPoint(i, j);
                    g.translate(p.getX(), p.getY());

                    final Cell cell = this.grid[i][j];
                    g.setColor(Color.white);
                    cell.renderOutline(g);
                    cell.renderPlants(g);

                    g.translate(-p.getX(), -p.getY());
                }
            }
        }
    }

    public void onKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_S) {
            this.renderScoreOverlay = true;
        }
    }

    public void onKeyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_S) {
            this.renderScoreOverlay = false;
        }
    }

    private class MouseInputHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (insets == null) return;

            final int xScr = evt.getXOnScreen() - insets.left;
            final int yScr = evt.getYOnScreen() - insets.top;

            // Ignore out of grid clicks
            if (xScr < PADDING_X || xScr > screenWidth - PADDING_X) return;
            if (yScr < PADDING_Y || yScr > screenHeight - PADDING_Y) return;

            // Map it to virtual (grid) coordinates

            final int xCell = (int) ((xScr - PADDING_X) / cellWidth);
            final int yCell = (int) ((yScr - PADDING_Y) / cellHeight);

            final Point2D p = getCellPoint(xCell, yCell);
            grid[xCell][yCell].onClick(xScr - p.getX(), yScr - p.getY());
        }
    }
}