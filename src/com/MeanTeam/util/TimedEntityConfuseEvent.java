package com.MeanTeam.util;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class TimedEntityConfuseEvent extends TimedEvent {
    private GameModelNotifier gameModelNotifier;
    private Entity entity;

    private Path oldValue;
    private Path newValue;

    public TimedEntityConfuseEvent(int turnCountLeft, Entity entity, Path oldValue /*, Path newValue*/,
                                         GameModel gameModel){
        super(turnCountLeft);

        gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);
        this.entity = entity;
        this.oldValue = oldValue;
        //this.newValue = newValue;
        this.newValue = new Path();
        newValue.addOrientation(Orientation.EAST);
        newValue.addOrientation(Orientation.SOUTH);
        newValue.addOrientation(Orientation.WEST);
        newValue.addOrientation(Orientation.NORTH);
    }

    public void start() {
        gameModelNotifier.notifymakeNpcConfused(entity, newValue);
    }

    public void stop() {
        gameModelNotifier.notifymakeNpcConfused(entity, oldValue);
    }

    public Entity getEntity() {
        return entity;
    }

    public Path getNewValue() {
        return newValue;
    }

    public Path getOldValue() {
        return oldValue;
    }

    public void accept(Visitor v) {
        v.visitTimedEntityConfuseEvent(this);
    }
}
