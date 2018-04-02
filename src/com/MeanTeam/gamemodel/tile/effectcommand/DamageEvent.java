package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

//DamageEvent is a one time event that damages the player
//can be used for things such as traps
public class DamageEvent implements SettableEventCommand
{
    //damageAmount is the amount of damage that will be dealt to the player
    private int damageAmount;

    public DamageEvent(int amt){
        damageAmount = amt;
    }

    @Override
    //when the event is triggered, the entity's health will be decreased by damageAmount
    public void trigger(Entity entity) {
        entity.decreaseHealth(damageAmount);
    }

    public void fail(Entity entity) { }

    public int getDamageAmount()
    {
        return damageAmount;
    }

    public void setValue(int amount) {
        damageAmount = amount;
    }

    public void accept(Visitor v) {
        v.visitDamageEvent(this);
    }
}
