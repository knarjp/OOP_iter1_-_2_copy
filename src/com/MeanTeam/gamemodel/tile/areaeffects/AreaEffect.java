package com.MeanTeam.gamemodel.tile.areaeffects;

import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.gamemodel.tile.entities.Entity;

public abstract class AreaEffect implements Visitable
{
    protected EventCommand effect;

    public abstract void trigger(Entity entity);

    public EventCommand getEffect() { return effect; }
}