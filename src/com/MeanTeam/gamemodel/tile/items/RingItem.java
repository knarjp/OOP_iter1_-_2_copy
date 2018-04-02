package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.effectcommand.ToggleableEventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class RingItem extends TakeableItem {

    private ToggleableEventCommand event;

    public RingItem(String name, ToggleableEventCommand event, ItemType type, WorldNotifier worldNotifier, GameModelNotifier modelNotifier) {
        super(name, type, worldNotifier, modelNotifier);
        this.event = event;
    }

    @Override
    public void equipToPlayer() {
        getModelNotifier().notifyPlayerRingEquip(this);
    }

    public void fireEvent(Entity entity) {
        event.trigger(entity);
    }

    public ToggleableEventCommand getEvent() {
        return event;
    }

    @Override
    public void accept(Visitor v) {
        v.visitRingItem(this);
    }
}