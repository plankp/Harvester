package org.atoiks.games.harvest;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.Point2D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

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

    private float time = (LIMIT_UPPER_BOUND + LIMIT_LOWER_BOUND) / 2;
    private float limit = LIMIT_UPPER_BOUND;

    private final Font font16;
    private final Font font48;

    private final double screenWidth;
    private final double screenHeight;

    private final double cellWidth;
    private final double cellHeight;

    private final Cell[][] grid = new Cell[VIRTUAL_WIDTH][VIRTUAL_HEIGHT];

    private final Score score = new Score();

    private State currentState = new TitleState();

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
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    public void update(final float dt) {
        this.currentState.update(dt);
    }

    private Point2D getCellPoint(int i, int j) {
        return new Point2D.Double(
                i * this.cellWidth + PADDING_X,
                j * this.cellHeight + PADDING_Y);
    }

    @Override
    public void paintComponent(final Graphics gr) {
        final Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        this.currentState.render(g);
    }

    public void onKeyPressed(KeyEvent evt) {
        this.currentState.onKeyPressed(evt);
    }

    public void onKeyReleased(KeyEvent evt) {
        this.currentState.onKeyReleased(evt);
    }

    public void onMouseClicked(MouseEvent evt) {
        this.currentState.onMouseClicked(evt);
    }

    private class TitleState implements State {

        // Magic numbers: -140 to 600
        private float x = -140;

        @Override
        public void update(float dt) {
            if ((this.x -= dt * 80) < -140) {
                this.x = 600;
            }
        }

        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, GameFrame.this.getWidth(), GameFrame.this.getHeight());

            g.setFont(GameFrame.this.font48);
            g.setColor(Color.white);
            g.drawString("Harvester", 200, 60);

            g.setFont(GameFrame.this.font16);
            g.drawString("Click anywhere on screen to start", 176, 180);
            g.drawString("Mouse is highly recommended!", 193, 200);
            g.drawString("Made by Atoiks Games", this.x, 400);

            GameFrame.this.genericGameRender(g);
        }

        @Override
        public void onMouseClicked(MouseEvent evt) {
            GameFrame.this.currentState = new GamePlayState();
        }
    }

    private class GamePlayState implements State {

        @Override
        public void update(float dt) {
            for (int i = 0; i < VIRTUAL_WIDTH; ++i) {
                for (int j = 0; j < VIRTUAL_HEIGHT; ++j) {
                    if (!GameFrame.this.grid[i][j].update(dt)) {
                        GameFrame.this.currentState = new GameOverState();
                        return;
                    }
                }
            }

            if ((GameFrame.this.time += dt) > GameFrame.this.limit) {
                // Reset timer
                GameFrame.this.time -= GameFrame.this.limit;

                // Make growth-trigger go faster
                GameFrame.this.limit = Math.max(GameFrame.this.limit - dt, LIMIT_LOWER_BOUND);

                // Randomly select a cell to try to grow!
                final int x = RAND_GEN.nextInt(VIRTUAL_WIDTH);
                final int y = RAND_GEN.nextInt(VIRTUAL_HEIGHT);
                GameFrame.this.grid[x][y].requestGrowing();
            }
        }

        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, GameFrame.this.getWidth(), GameFrame.this.getHeight());

            GameFrame.this.genericGameRender(g);
        }

        @Override
        public void onKeyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_S) {
                GameFrame.this.currentState = new PauseState();
            }
        }

        @Override
        public void onMouseClicked(MouseEvent evt) {
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

    private class PauseState implements State {

        @Override
        public void update(float dt) { /* do nothing */ }

        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.gray);
            g.fillRect(0, 0, GameFrame.this.getWidth(), GameFrame.this.getHeight());

            GameFrame.this.genericScoreRender(g);

            g.drawString("ETA", 40, 230);
            g.drawString(String.format("%.2fs", Math.max(GameFrame.this.limit - GameFrame.this.time, 0)), 85, 230);
        }

        @Override
        public void onKeyReleased(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_S) {
                GameFrame.this.currentState = new GamePlayState();
            }
        }
    }

    private class GameOverState implements State {

        @Override
        public void update(float dt) { /* do nothing */ }

        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.gray);
            g.fillRect(0, 0, GameFrame.this.getWidth(), GameFrame.this.getHeight());

            g.setFont(GameFrame.this.font48);
            g.setColor(Color.black);
            g.drawString("GAME OVER!", 200, 60);
            g.translate(0, 80);

            GameFrame.this.genericScoreRender(g);
        }
    }

    private void genericScoreRender(Graphics2D g) {
        Plant.PHASE_1.renderImage(g, 40, 40, 30, 30);
        Plant.PHASE_2.renderImage(g, 40, 100, 30, 30);
        Plant.PHASE_3.renderImage(g, 40, 160, 30, 30);

        g.setFont(this.font16);
        g.setColor(Color.black);
        g.drawString("x" + this.score.getType1(), 85, 40 + 20);
        g.drawString("x" + this.score.getType2(), 85, 100 + 20);
        g.drawString("x" + this.score.getType3(), 85, 160 + 20);

        g.drawString("Score", 40, 210);
        g.drawString(Integer.toString(this.score.getScore()), 85, 210);
    }

    private void genericGameRender(Graphics2D g) {
        if (insets == null) return;

        final Point origin = GameFrame.this.getLocationOnScreen();
        final double tx = GameFrame.this.insets.left - origin.getX();
        final double ty = GameFrame.this.insets.top - origin.getY();
        g.translate(tx, ty);

        for (int i = 0; i < VIRTUAL_WIDTH; ++i) {
            for (int j = 0; j < VIRTUAL_HEIGHT; ++j) {
                final Point2D p = GameFrame.this.getCellPoint(i, j);
                g.translate(p.getX(), p.getY());

                final Cell cell = GameFrame.this.grid[i][j];
                g.setColor(Color.white);
                cell.renderOutline(g);
                cell.renderPlants(g);

                g.translate(-p.getX(), -p.getY());
            }
        }
    }
}
