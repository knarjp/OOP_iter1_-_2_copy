package com.MeanTeam.gamemodel.notifiers;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gameview.viewstates.GameViewState;

public class GameViewNotifier
{
    private static GameViewState gameview;

    public static void setGameview(GameViewState gameview) { GameViewNotifier.gameview = gameview; }

    public static void notifyShopping(Entity shopkeeper)
    {
        gameview.setShopState(shopkeeper);
    }

    public static void notifyInteraction(Entity other)
    {
        if(gameview != null) {
            gameview.setInteractState(other);
        }
    }

    public static void notifyLevelUp(Entity entity, int skillPoints)
    {
        gameview.setLevelUpState(entity, skillPoints);
    }

    public static void notifyPlayerDeath()
    {
        gameview.setMenuState();
    }
}
