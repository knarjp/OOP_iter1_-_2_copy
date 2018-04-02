package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.visitors.Visitor;

//An interactiveItem contains an InteractionCommand which it triggers when activated
public class InteractiveItem extends Item {
    private EventCommand effect;

    public InteractiveItem(EventCommand effect, WorldNotifier worldNotifier){
        this(effect, ItemType.DEFAULT, worldNotifier);
    }

    public InteractiveItem(EventCommand effect, ItemType type, WorldNotifier worldNotifier){
        super(type, worldNotifier);
        this.effect = effect;
    }

    public void trigger(Entity entity, boolean entityIsPlayer) {
        if(entityIsPlayer) {
            effect.trigger(entity);
        }
    }

    public EventCommand getEffect()
    {
        return effect;
    }

    public void accept(Visitor v) {
        v.visitInteractiveItem(this);
    }
}