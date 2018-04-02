package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class CreepEvent implements SettableEventCommand {
    private GameModelNotifier gameModelNotifier;
    private int amt;

    public CreepEvent(int amt, GameModelNotifier gameModelNotifier){
        this.amt = amt;
        this.gameModelNotifier = gameModelNotifier;
    }

    @Override
    // when the event is triggered, the entity's health will be decreased by damageAmount
    public void trigger(Entity entity) {
        if(!gameModelNotifier.notifyIsPlayerDetected()) {
            entity.creep();
        }
    }

    public void fail(Entity entity) { }

    public int getDamageAmount()
    {
        return amt;
    }

    public void setValue(int amount) {
        amt = amount;
    }

    public void accept(Visitor v) {
        v.visitCreepEvent(this);
    }
}
