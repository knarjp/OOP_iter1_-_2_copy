package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

//A TakeableItem contains an EventCommand which it triggers when activated
public class TakeableItem extends Item {
    private String name;

    GameModelNotifier modelNotifier;

    public TakeableItem(String name, ItemType type, WorldNotifier worldNotifier, GameModelNotifier gameModelNotifier) {
        super(type, worldNotifier);
        this.name = name;
        this.modelNotifier = gameModelNotifier;
    }

    public void trigger(Entity entity, boolean entityIsPlayer){
        if(entityIsPlayer) {
            if (getWorldNotifier().notifyItemPickup(entity, this)) {
                removeItem();
            }
        }
    }

    public String getName() {
        return name;
    }

    protected GameModelNotifier getModelNotifier() {
        return this.modelNotifier;
    }

    public void equipToPlayer() { }

    public void accept(Visitor v) { v.visitTakeableItem(this); }
}