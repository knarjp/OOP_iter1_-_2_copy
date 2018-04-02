package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.util.TimedEntityMovementSpeedEvent;
import com.MeanTeam.util.TimedEvent;
import com.MeanTeam.visitors.Visitor;

public class SlowdownEvent implements SettableEventCommand {
    private GameModelNotifier gameModelNotifier;
    private int duration;

    public SlowdownEvent(int amt, GameModel gameModel){
        gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);
        this.duration = amt;
    }

    @Override
    public void trigger(Entity entity) {
        TimedEvent timedEvent = new TimedEntityMovementSpeedEvent(duration, entity,
                entity.getMovementSpeed(), entity.getMovementSpeed() * 3);
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
        v.visitSlowdownEvent(this);
    }
}
