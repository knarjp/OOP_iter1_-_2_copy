package com.MeanTeam.guiframework;

import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.guiframework.displayables.Displayable;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class Viewstate
{
    private LinkedHashSet<Collection<? extends Displayable>> displayables;

    public Viewstate()
    {
        displayables = new LinkedHashSet<>();
    }

    public void update()
    {
        displayables.forEach((collection) -> collection.forEach(Displayable::update));
    }

    public void draw(Graphics2D g2d)
    {
        displayables.forEach((collection) -> collection.forEach((displayable) -> displayable.draw(g2d)));
    }

    public void add(Collection<? extends Displayable> newDisplayables)
    {
        displayables.add(newDisplayables);
    }

    public void delete(Collection<? extends Displayable> newDisplayables)
    {
        displayables.remove(newDisplayables);
    }

    public void changeSize(Dimension size) { }

    // parseKeyOperation methods should be overridden by subclasses

    public void parseKeyPress(KeyRole keyrole) { }

    public void parseKeyRelease(KeyRole keyrole) { }
}
