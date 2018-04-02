package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class DisarmTrapEvent implements SettableEventCommand {
    // traps are like-a (but not) a oneshot item that have an invisibility boolean
    // the states for this skill are:
    //                                  - the trap is detected, and revealed on the map
    //                                  - the trap is not detected
    //                                  - the detected trap is disarmed, and no damage is taken
    //                                  - the detected trap is not disarmed, and damage is taken

    private int trapSuccessChance;
    private GameModelNotifier gameModelNotifier;

    public DisarmTrapEvent(int amt, GameModelNotifier gameModelNotifier) {
        this.trapSuccessChance = amt;
        this.gameModelNotifier = gameModelNotifier;
    }

    @Override
    // when the event is triggered, we first look for a trap, and then tell it to either reveal or remove itself
    public void trigger(Entity entity) {
        gameModelNotifier.notifyActiveWorldTrapDisarm(entity, trapSuccessChance);
    }

    public void fail(Entity entity) { gameModelNotifier.notifyActiveWorldTrapDisarm(entity, 0);}

    public int getTrapSuccessChance() {
        return trapSuccessChance;
    }

    public void setValue(int amount) {
        trapSuccessChance = amount;
    }

    public void accept(Visitor v) {
        v.visitTrapDisarmEvent(this);
    }
}