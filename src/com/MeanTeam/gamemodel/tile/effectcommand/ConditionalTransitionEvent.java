package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.visitors.Visitor;

import java.awt.*;

//ConditionalTransitionEvent is a type of TransitionEvent which requires the player to have a certain item in their
//inventory in order to trigger the command
public class ConditionalTransitionEvent extends TransitionEvent {
    //when isLocked is true, the command trigger will not trigger unless the player has the required item
    //in which case isLocked will switch to false and trigger
    //if isLocked is false, the command will trigger
    private boolean isLocked;
    //required is the name of item the player must have in order to unlock the event
    private TakeableItem requiredItem;

    public ConditionalTransitionEvent(int destination, Point start, GameModelNotifier gameModelNotifier, TakeableItem requiredItem, boolean isLocked){
        super(destination, start, gameModelNotifier);
        this.isLocked = isLocked;
        this.requiredItem = requiredItem;
    }

    public ConditionalTransitionEvent(int destination, Point start, GameModelNotifier gameModelNotifier, TakeableItem requiredItem){
        super(destination, start, gameModelNotifier);
        isLocked = true;
        this.requiredItem = requiredItem;
    }

    public void trigger(Entity entity){
        //if the event is locked, check if the entity has the required item
        if(isLocked) {
            //if he or she does, unlock the event, remove the item, and trigger the command
            System.out.println("Door locked");
            if(entity.hasItemInBackpack(requiredItem)){
                System.out.println("Unlocking door with key");
                isLocked = false;
                entity.removeItemFromBackpack(requiredItem);
                super.trigger(entity);
            }
        //otherwise, the event is unlocked, so we trigger the command
        } else {
            System.out.println("Door unlocked");
            super.trigger(entity);
        }
    }

    public boolean isLocked() { return isLocked; }

    public void setLocked(boolean locked) { isLocked = locked; }

    public TakeableItem getRequiredItem() { return requiredItem; }

    @Override
    public void accept(Visitor v)
    {
        v.visitConditionalTransitionEvent(this);
    }
}
