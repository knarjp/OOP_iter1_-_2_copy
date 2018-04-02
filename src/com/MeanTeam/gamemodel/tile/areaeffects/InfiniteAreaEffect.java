package com.MeanTeam.gamemodel.tile.areaeffects;

import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class InfiniteAreaEffect extends AreaEffect {

    public InfiniteAreaEffect(EventCommand effect) {
        this.effect = effect;
    }

    public void trigger(Entity entity) {
        effect.trigger(entity);
    }

    public void accept(Visitor v) { v.visitInfiniteAreaEffect(this); }
}