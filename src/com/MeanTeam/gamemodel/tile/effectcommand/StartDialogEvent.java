package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.notifiers.DialogNotifier;
import com.MeanTeam.gamemodel.notifiers.GameViewNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.entities.EntityType;
import com.MeanTeam.visitors.Visitor;

public class StartDialogEvent implements SettableEventCommand
{
    private int value = 0;

    public StartDialogEvent(int defaultValue) {
        value = defaultValue;
    }

    public void trigger(Entity e)
    {
        if(e.getType() == EntityType.SHOPKEEPER)
        {
            GameViewNotifier.notifyShopping(e);
        }
        DialogNotifier.notifyDialog(e);
    }

    public void fail(Entity e) { }

    public void setValue(int amount)
    {
        this.value = amount;
    }

    public int getValue() {
        return value;
    }

    public void accept(Visitor v) {
        v.visitStartDialogEvent(this);
    }
}
