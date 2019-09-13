package org.atoiks.games.harvest;

public final class Score {

    private int countType1;
    private int countType2;
    private int countType3;

    public void addType1() {
        ++this.countType1;
    }

    public void addType2() {
        ++this.countType2;
    }

    public void addType3() {
        ++this.countType3;
    }

    public void reset() {
        this.countType1 = 0;
        this.countType2 = 0;
        this.countType3 = 0;
    }

    public int getType1() {
        return this.countType1;
    }

    public int getType2() {
        return this.countType2;
    }

    public int getType3() {
        return this.countType3;
    }

    public int getScore() {
        return this.countType1
            + this.countType2 << 1
            + this.countType3 << 2;
    }
}