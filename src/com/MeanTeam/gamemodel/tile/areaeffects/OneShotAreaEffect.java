package com.MeanTeam.gamemodel.tile.areaeffects;

import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class OneShotAreaEffect extends AreaEffect {

    private boolean hasNotFired;

    public OneShotAreaEffect(EventCommand effect, boolean hasNotFired)
    {
        this.effect = effect;
        this.hasNotFired = hasNotFired;
    }

    public OneShotAreaEffect(EventCommand effect) {
        hasNotFired = true;
        this.effect = effect;
    }

    public void trigger(Entity entity) {
        if(hasNotFired) {
            effect.trigger(entity);
            hasNotFired = false;
        }
    }

    public boolean hasNotFired() { return hasNotFired; }

    public void setHasNotFired(boolean hasNotFired) {
        this.hasNotFired = hasNotFired;
    }

    public void accept(Visitor v) { v.visitOneshotAreaEffect(this); }
}
