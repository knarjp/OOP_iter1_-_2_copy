package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class InstaDeathEvent implements EventCommand
{
    public void trigger(Entity entity) { entity.kill(); }

    public void accept(Visitor v) { v.visitInstaDeathCommand(this); }
}
