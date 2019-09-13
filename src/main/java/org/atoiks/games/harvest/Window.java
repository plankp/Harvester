package org.atoiks.games.harvest;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

import java.awt.BorderLayout;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

public class Window {

    private final GameFrame gframe;
    private final JFrame frame;

    public Window() {
        this.gframe = new GameFrame();

        this.frame = new JFrame("Atoiks Games - Harvest");
        this.frame.add(this.gframe, BorderLayout.CENTER);
        this.frame.setIgnoreRepaint(true);
        this.frame.setAlwaysOnTop(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void start() {
        this.frame.setSize(600, 480);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);
        this.frame.setVisible(true);
        this.gframe.requestFocus();

        this.gframe.setInsets(this.frame.getInsets());
    }

    public void update(final float dt) {
        this.gframe.update(0.020f);
        this.frame.repaint();
    }
}