package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

public class StaminaDecreaseEvent implements SettableEventCommand {
    //staminaDecreaseAmount is the amount of stamina the player will lose
    private int staminaDecreaseAmount;

    public StaminaDecreaseEvent(int amt){
        staminaDecreaseAmount = amt;
    }

    @Override
    //when the event is triggered, the entity's stamina will be decreased by staminaDecreaseAmount
    public void trigger(Entity entity) {
        entity.decreaseStamina(staminaDecreaseAmount);
    }

    public void fail(Entity entity) { }

    public int getStaminaDecreaseAmount()
    {
        return staminaDecreaseAmount;
    }

    public void setValue(int amount) {
        staminaDecreaseAmount = amount;
    }

    public void accept(Visitor v) {
        v.visitStaminaDecreaseEvent(this);
    }
}
