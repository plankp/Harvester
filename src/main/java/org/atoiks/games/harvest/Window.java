package org.atoiks.games.harvest;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

import java.awt.Font;
import java.awt.BorderLayout;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

public class Window {

    private final GameFrame gframe;
    private final JFrame frame;

    public Window(Font fnt) {
        this.gframe = new GameFrame(fnt);

        this.frame = new JFrame("Atoiks Games - Harvest");
        this.frame.add(this.gframe, BorderLayout.CENTER);
        this.frame.setIgnoreRepaint(true);
        this.frame.setAlwaysOnTop(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame.addKeyListener(new KeyboardHandler());
        this.frame.addMouseListener(new MouseHandler());
    }

    public void start() {
        this.frame.setSize(600, 480);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);
        this.frame.setVisible(true);

        this.gframe.setInsets(this.frame.getInsets());
    }

    public void update(final float dt) {
        this.gframe.update(0.020f);
        this.frame.repaint();
    }

    private class KeyboardHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            Window.this.gframe.onKeyPressed(evt);
        }

        @Override
        public void keyReleased(KeyEvent evt) {
            Window.this.gframe.onKeyReleased(evt);
        }
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {
            Window.this.gframe.onMouseClicked(evt);
        }
    }
}