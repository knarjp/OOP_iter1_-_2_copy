package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.visitors.Visitor;

//A OneshotItem contains an EventCommand which it triggers when activated
public class OneshotItem extends Item {
    private EventCommand effect;

    public OneshotItem(EventCommand effect, WorldNotifier worldNotifier){
        this(effect, ItemType.DEFAULT, worldNotifier);
    }

    public OneshotItem(EventCommand effect, ItemType type, WorldNotifier worldNotifier){
        super(type, worldNotifier);
        this.effect = effect;
    }

    public void trigger(Entity entity, boolean entityIsPlayer){
        effect.trigger(entity);

        removeItem();
    }

    public EventCommand getEffect()
    {
        return effect;
    }

    public void accept(Visitor v) { v.visitOneshotItem(this); }
}
