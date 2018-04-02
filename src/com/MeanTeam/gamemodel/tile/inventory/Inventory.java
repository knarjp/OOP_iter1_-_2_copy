package com.MeanTeam.gamemodel.tile.inventory;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.visitors.Visitor;

import java.util.ArrayList;
import java.util.Random;

public class Inventory implements Visitable {

    private int sizeLimit;
    private TakeableItem[] items;
    private GameModelNotifier modelNotifier;

    public Inventory(int sizeLimit, GameModelNotifier modelNotifier) {
        this.sizeLimit = sizeLimit;
        this.items = new TakeableItem[sizeLimit];
        this.modelNotifier = modelNotifier;
    }

    public int getFreeSlot() {
        for(int i = 0; i < sizeLimit; i++) {
            if(items[i] == null) {
                return i;
            }
        }

        return -1;
    }

    public boolean hasFreeSlot() {
        for(int i = 0; i < sizeLimit; i++) {
            if(items[i] == null) {
                return true;
            }
        }

        return false;
    }

    public TakeableItem getItemAtSlot(int slot) {
        if(validSlot(slot) && itemExistsAtSlot(slot)) {
            return items[slot];
        }
        return null;
    }

    public void transferRandomItemToPlayerInventory() {
        if(!isEmpty()) {
            int slot = getRandomOccupiedItemSlot();

            modelNotifier.notifyPlayerItemTransfer(getItemAtSlot(slot));

            removeItem(slot);
        }
    }

    public int getRandomOccupiedItemSlot() {
        ArrayList<Integer> slots = new ArrayList<>();

        for(int i = 0; i < sizeLimit; i++) {
            if(getItemAtSlot(i) != null) {
                slots.add(i);
            }
        }

        Random random = new Random();

        int slotNum = random.nextInt(slots.size());

        return slots.get(slotNum);
    }

    public boolean addItem(TakeableItem item) {
        System.out.println("Adding item!");
        int freeSlot = getFreeSlot();

        if (freeSlot != -1) {
            System.out.println("Added " + item.getName() + " to slot " + freeSlot);
            items[freeSlot] = item;
            return true;
        }

        return false;
    }

    public void addItemAtSlot(int i, TakeableItem item) {
        if(validSlot(i))
            items[i] = item;
    }

    public void removeItem(TakeableItem item) {
        if(hasItem(item)) {
            for(int i = 0; i < sizeLimit; ++i){
                if(item == items[i])
                {
                    items[i] = null;
                    return;
                }
            }
        }
    }

    public void removeItem(int slot) {
        if(validSlot(slot) && itemExistsAtSlot(slot)) {
            items[slot] = null;
        }
    }

    public boolean hasItem(TakeableItem item) {
        for(int i = 0; i < sizeLimit; ++i){
            if(items[i] == item) {
                return true;
            }
        }
        return false;
    }

    private boolean itemExistsAtSlot(int slot) {
        return items[slot] != null;
    }

    private boolean validSlot(int slot) {
        if (slot > -1 && slot < sizeLimit) {
            return true;
        } else {
            return false;
        }
    }

    public int getSizeLimit() { return sizeLimit; }

    public void setNewItemWorldNotifiers(WorldNotifier newNotifier) {
        for(TakeableItem item : items) {
            if (item != null) {
                item.setWorldNotifier(newNotifier);
            }
        }
    }

    private boolean isEmpty() {
        for(int i = 0; i < sizeLimit; i++) {
            if(itemExistsAtSlot(i)) {
                return false;
            }
        }
        return true;
    }

    public void accept(Visitor v) {
        v.visitInventory(this);
    }
}