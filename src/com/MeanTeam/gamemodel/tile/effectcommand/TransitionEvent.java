package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;

import java.awt.*;

//TransitionEvent will teleport the player to a new location when triggered
public class TransitionEvent implements EventCommand
{
    private int destinationWorld;
    private Point startingPoint;
    private GameModelNotifier gameModelNotifier;

    public TransitionEvent(int destination, Point start, GameModelNotifier gameModelNotifier){
        this.destinationWorld = destination;
        this.startingPoint = start;
        this.gameModelNotifier = gameModelNotifier;
    }

    //when triggered, the TransitionEvent changes the active world
    public void trigger(Entity entity){
        gameModelNotifier.notifyWorldTransition(destinationWorld, startingPoint);
    }

    public int getDestinationWorld(){
        return destinationWorld;
    }

    public Point getStartingPoint(){
        return startingPoint;
    }

    public void accept(Visitor v) { v.visitTransitionEvent(this); }
}
