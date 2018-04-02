package com.MeanTeam.util;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class TimedEntityMovementSpeedEvent extends TimedEvent {
    Entity entity;

    long oldValue;
    long newValue;

    public TimedEntityMovementSpeedEvent(int turnCountLeft, Entity entity, long oldValue, long newValue){
        super(turnCountLeft);

        this.entity = entity;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void start() {
        entity.setMovementSpeed(newValue);
    }

    public void stop() {
        entity.setMovementSpeed(oldValue);
    }

    public Entity getEntity() {
        return entity;
    }

    public long getNewValue() {
        return newValue;
    }

    public long getOldValue() {
        return oldValue;
    }

    public void accept(Visitor v) {
        v.visitTimedEntityMovementSpeedEvent(this);
    }
}
