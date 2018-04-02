package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.gamemodel.tile.entities.Entity;

public abstract class Item implements Visitable
{
    private ItemType type;

    private WorldNotifier worldNotifier;

    protected Item(ItemType type, WorldNotifier worldNotifier) {
        this.type = type;
        this.worldNotifier = worldNotifier;
    }

    protected Item()
    {
        this.type = ItemType.DEFAULT;
    }

    public abstract void trigger(Entity entity, boolean entityIsPlayer);

    final protected void removeItem() {
        worldNotifier.notifyItemRemoval(this);
    }

    final public void setWorldNotifier(WorldNotifier worldNotifier) {
        this.worldNotifier = worldNotifier;
    }

    final protected WorldNotifier getWorldNotifier() {
        return this.worldNotifier;
    }

    public ItemType getItemType(){
        return type;
    }
}
