package org.atoiks.games.harvest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.Random;

public class Cell {

    private static final Random RAND_GEN = new Random();

    private static final int VIRTUAL_SIDE = 5;

    private final Rectangle2D.Double rect;

    private final double cellWidth;
    private final double cellHeight;

    private final Plant[][] grid = new Plant[VIRTUAL_SIDE][VIRTUAL_SIDE];

    public Cell(final double width, final double height, Score score) {
        this.rect = new Rectangle2D.Double(0, 0, width, height);

        this.cellWidth = width / VIRTUAL_SIDE;
        this.cellHeight = height / VIRTUAL_SIDE;
        for (int i = 0; i < VIRTUAL_SIDE; ++i) {
            for (int j = 0; j < VIRTUAL_SIDE; ++j) {
                this.grid[i][j] = new Plant(this.cellWidth, this.cellHeight, score);
            }
        }
    }

    public boolean update(final float dt) {
        for (int i = 0; i < VIRTUAL_SIDE; ++i) {
            for (int j = 0; j < VIRTUAL_SIDE; ++j) {
                if (!this.grid[i][j].update(dt)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void requestGrowing() {
        // Randomly select a plant-cell
        final int x = RAND_GEN.nextInt(VIRTUAL_SIDE);
        final int y = RAND_GEN.nextInt(VIRTUAL_SIDE);
        final Plant cell = this.grid[x][y];

        // Only grow if it is vacant
        if (!cell.isGrowing()) {
            cell.startGrowing();
        }
    }

    public void onClick(double x, double y) {
        final int xCell = (int) (x / this.cellWidth);
        final int yCell = (int) (y / this.cellHeight);
        this.grid[xCell][yCell].harvest();
    }

    private Point2D getCellPoint(int i, int j) {
        return new Point2D.Double(
                i * this.cellWidth,
                j * this.cellHeight);
    }

    public void renderOutline(Graphics2D g) {
        g.draw(this.rect);
    }

    public void renderPlants(Graphics2D g) {
        for (int i = 0; i < VIRTUAL_SIDE; ++i) {
            for (int j = 0; j < VIRTUAL_SIDE; ++j) {
                final Point2D p = this.getCellPoint(i, j);
                g.translate(p.getX(), p.getY());

                this.grid[i][j].renderContent(g);

                g.translate(-p.getX(), -p.getY());
            }
        }
    }
}