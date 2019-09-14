package org.atoiks.games.harvest;

import java.awt.Graphics2D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface State {

    public void update(float dt);

    public void render(Graphics2D g);

    public default void onKeyPressed(KeyEvent evt) {
        // do nothing
    }

    public default void onKeyReleased(KeyEvent evt) {
        // do nothing
    }

    public default void onMouseClicked(MouseEvent evt) {
        // do nothing
    }
}