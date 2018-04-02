package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public interface SettableEventCommand extends EventCommand {
    void setValue(int amount);

    void fail(Entity entity);

    SettableEventCommand NULL = new SettableEventCommand()
    {
        public void trigger(Entity e) {}
        public void setValue(int amount) {}
        public void fail(Entity entity) {}
        public void accept(Visitor v) { v.visitNullSettableCommand(this); }
    };
}
