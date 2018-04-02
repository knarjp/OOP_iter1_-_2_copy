package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.util.TimedEntityConfuseEvent;
import com.MeanTeam.util.TimedEvent;
import com.MeanTeam.visitors.Visitor;

public class ConfuseEvent implements SettableEventCommand {
    private GameModelNotifier gameModelNotifier;
    private GameModel gameModel;
    private int duration;

    public ConfuseEvent(int amt, GameModel gameModel){
        gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);
        this.gameModel = gameModel;
        this.duration = amt;
    }

    @Override
    public void trigger(Entity entity) {
        TimedEvent timedEvent = new TimedEntityConfuseEvent(duration, entity,
                gameModel.getNpcPath(entity), gameModel);
        gameModelNotifier.notifyAddToTimedEventList(timedEvent);
    }

    public void fail(Entity entity) {
        gameModelNotifier.notifyMakeNpcHostile(entity);
    }

    public int getDuration()
    {
        return duration;
    }

    public void setValue(int amount) {
        duration = amount;
    }

    public void accept(Visitor v) {
        v.visitConfuseEvent(this);
    }
}
