package com.MeanTeam.gamemodel.notifiers;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.util.Path;
import com.MeanTeam.util.TimedEvent;

import java.awt.*;

public class GameModelNotifier {
    private static GameModelNotifier INSTANCE = null;
    private GameModel gameModel;

    private GameModelNotifier(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public static GameModelNotifier getGameModelNotifier(GameModel gameModel) {
        if(INSTANCE == null) {
            INSTANCE = new GameModelNotifier(gameModel);
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public void notifyWorldTransition(int worldIndex, Point playerSpawnPt) {
        gameModel.changeWorld(worldIndex, playerSpawnPt);
    }

    public void notifyPlayerWeaponEquip(WeaponItem weapon) {
        gameModel.addWeaponToPlayerWeaponSlot(weapon);
    }

    public void notifyPlayerArmorEquip(ArmorItem armor) {
        gameModel.addArmorToPlayerWeaponSlot(armor);
    }

    public void notifyPlayerRingEquip(RingItem ring) {
        gameModel.addRingToPlayerWeaponSlot(ring);
    }

    public void notifyPlayerConsume(ConsumableItem consumableItem) {
        gameModel.consumeItem(consumableItem);
    }

    public void notifyMakeNpcHostile(Entity entity) { gameModel.makeNpcHostile(entity); }

    public void notifyPlayerItemTransfer(TakeableItem item) {
        gameModel.transferItemToPlayer(item);
    }

    public void notifyAddToTimedEventList(TimedEvent timedEvent) {
        gameModel.addTimedEvent(timedEvent);
    }

    public void notifyActiveWorldTrapDisarm(Entity entity, int trapSuccessChance) {
        gameModel.tellActiveWorldToDisarmTrap(entity, trapSuccessChance);
    }

    public void notifymakeNpcConfused(Entity entity, Path path) { gameModel.makeNpcConfused(entity, path); }

    public boolean notifyIsPlayerDetected() {
        return gameModel.isPlayerDetected();
    }
}