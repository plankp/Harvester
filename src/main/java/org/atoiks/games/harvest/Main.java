package org.atoiks.games.harvest;

public class Main {

    public static void main(String[] args) {
        final Window window = new Window();
        window.start();

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
