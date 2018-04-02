package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class LevelUpEvent implements EventCommand
{
    public void trigger(Entity entity) { entity.levelUp(); }

    public void accept(Visitor v) { v.visitLevelUpCommand(this); }
}
