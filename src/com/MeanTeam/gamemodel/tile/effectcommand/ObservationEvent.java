package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.notifiers.DialogNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class ObservationEvent implements SettableEventCommand
{
    private int value = 0;

    public ObservationEvent(int value) { this.value = value; }

    public void trigger(Entity e) {
        DialogNotifier.notifyObservation(e, value);
    }

    public void fail(Entity e) { }

    public void setValue(int value) { this.value = value; }

    public int getValue() {
        return this.value;
    }

    public void accept(Visitor v) {
        v.visitObservationEvent(this);
    }
}
