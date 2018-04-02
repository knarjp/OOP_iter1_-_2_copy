package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class ToggleableHealthEvent extends ToggleableEventCommand {

    private int amount;

    public ToggleableHealthEvent(int amount) {
        setHasNotFired();
        this.amount = amount;
    }

    protected void execute(Entity entity) {
        entity.setMaxHealth(entity.getMaxHealth() + amount);
    }

    protected void unExecute(Entity entity) {
        entity.setMaxHealth(entity.getMaxHealth() - amount);
    }

    public int getAmount() {
        return amount;
    }

    public void accept(Visitor v) {
        v.visitToggleableHealthEvent(this);
    }
}
