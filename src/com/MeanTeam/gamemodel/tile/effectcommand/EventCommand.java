package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.visitors.Visitor;

public interface EventCommand extends Visitable
{
    void trigger(Entity e);

    EventCommand NULL = new EventCommand()
    {
        public void accept(Visitor v) { v.visitNullCommand(this); }
        public void trigger(Entity e) {}
    };
}
