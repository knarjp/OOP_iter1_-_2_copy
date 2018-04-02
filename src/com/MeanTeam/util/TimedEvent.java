package com.MeanTeam.util;


import com.MeanTeam.visitors.Visitable;

public abstract class TimedEvent implements Visitable {
    private int turnCountLeft;

    protected TimedEvent(int turnCountLeft) {
        this.turnCountLeft = turnCountLeft;
    }

    public abstract void start();
    public abstract void stop();

    public void decrementTurnCountLeft() { --turnCountLeft; }

    public boolean stopIfNeeded() {
        if (turnCountLeft == 0) {
            stop();
        }
        return turnCountLeft == 0;
    }

    public int getTurnCountLeft() {
        return this.turnCountLeft;
    }
}
