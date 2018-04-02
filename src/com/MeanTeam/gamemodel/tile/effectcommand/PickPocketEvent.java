package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.visitors.Visitor;

import java.util.Random;

public class PickPocketEvent implements SettableEventCommand {

    // pick-pocket will work in a way that, even if the skill triggers "successfully", and you are able to take the item
    // from the entity, there may still be a chance with which the entity that you stole the item from will realize
    // that you have stolen that item, thus they will become angry at you and start attacking you
    private GameModelNotifier gameModelNotifier;
    private int thieveryNoticeChance;

    public PickPocketEvent(int amt, GameModelNotifier gameModelNotifier){
        thieveryNoticeChance = amt;
        this.gameModelNotifier = gameModelNotifier;
    }

    @Override
    // when the event is triggered, we steal from the entity and then see if we need to aggro it based
    // on the thieveryNoticeChance
    public void trigger(Entity entity) {
        entity.givePlayerItemFromBackpack();

        randomMakeNpcHostile(entity);
    }

    public void fail(Entity entity) {
        randomMakeNpcHostile(entity);
    }

    private void randomMakeNpcHostile(Entity entity) {
        boolean detected = (int)(Math.random() * 100 + 1) <= thieveryNoticeChance;
        if(detected) {
            gameModelNotifier.notifyMakeNpcHostile(entity);
        }

        /*Random generator = new Random();
        int i = generator.nextInt(thieveryNoticeChance) + 1;
        if (i / 2 < thieveryNoticeChance) {
            gameModelNotifier.notifyMakeNpcHostile(entity);
        }*/
    }

    public int getThieveryNoticeChance() {
        return thieveryNoticeChance;
    }

    public void setValue(int amount) {
        thieveryNoticeChance = amount;
    }

    public void accept(Visitor v) {
        v.visitPickPocketEvent(this);
    }
}
