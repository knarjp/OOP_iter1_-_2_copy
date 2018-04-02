package com.MeanTeam.gamemodel.notifiers;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gameview.displayables.DialogDisplayable;

public class DialogNotifier
{
    private static DialogNotifier instance = new DialogNotifier();

    private static DialogDisplayable displayable;

    public static void setDisplayable(DialogDisplayable displayable)
    {
        DialogNotifier.displayable = displayable;
    }

    public static void notifyDialog(Entity e)
    {
        displayable.setMessage(e.getDialog());
    }

    public static void notifyObservation(Entity e, int successfulness) { displayable.setMessage(e.getObservationMessage(successfulness)); }
}
