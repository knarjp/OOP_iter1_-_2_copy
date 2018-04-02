package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

//HealEvent is a one time event that heals the player
//can be used for things such as health potions
public class HealEvent implements SettableEventCommand
{
    //healAmount is the amount of health that will be given to the player
    private int healAmount;

    public HealEvent(int healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    //when the event is triggered, the entity's health will be increased by healAmount
    public void trigger(Entity entity){
        entity.addHealth(healAmount);
    }

    public void fail(Entity entity) { }

    public int getHealAmount() { return healAmount; }

    public void setValue(int amount) {
        healAmount = amount;
    }

    public void accept(Visitor v) {
        v.visitHealEvent(this);
    }
}