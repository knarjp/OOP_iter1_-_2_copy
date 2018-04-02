package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class ConsumableItem extends TakeableItem {

    private EventCommand event;

    public ConsumableItem(String name, EventCommand event, ItemType type, WorldNotifier worldNotifier, GameModelNotifier gameModelNotifier) {
        super(name, type, worldNotifier, gameModelNotifier);

        this.event = event;
    }

    @Override
    public void equipToPlayer() {
        getModelNotifier().notifyPlayerConsume(this);
    }

    public void fireEvent(Entity entity) {
        event.trigger(entity);
    }

    public EventCommand getEvent() {
        return event;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConsumableItem(this);
    }
}