package com.MeanTeam.guiframework.displayables;

import com.MeanTeam.util.TypedAbstractFunction;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class ConditionalDisplayable implements Displayable
{
    private Set<ConditionalTuple> conditionals;

    private Displayable defaultDisplayable;
    private Displayable activeDisplayable;

    private Point origin;

    public ConditionalDisplayable(Point origin, Displayable defaultDisplayable)
    {
        this.origin = origin;
        this.defaultDisplayable = this.activeDisplayable = defaultDisplayable;
        this.conditionals = new LinkedHashSet<>();
    }

    public void add(TypedAbstractFunction<Boolean> condition, Displayable displayable)
    {
        conditionals.add(new ConditionalTuple(condition, displayable));
    }

    public void remove(TypedAbstractFunction<Boolean> condition, Displayable displayable)
    {
        ConditionalTuple target = null;

        for(ConditionalTuple conditional : conditionals)
        {
            if(conditional.condition == condition || conditional.displayable == displayable)
            {
                target = conditional;
                break;
            }
        }

        if(target != null)
        {
            conditionals.remove(target);
        }
    }

    public void clear()
    {
        conditionals.clear();
    }

    // Displayable interface:

    public Point getOrigin() {return origin;}
    public Dimension getSize() {return activeDisplayable.getSize();}

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        activeDisplayable.drawWithOffset(g2d, drawPt);
    }

    @Override
    public void update()
    {
        for(ConditionalTuple conditional : conditionals)
        {
            if(conditional.condition.execute())
            {
                activeDisplayable = conditional.displayable;
                activeDisplayable.update();
                return;
            }
        }

        activeDisplayable = defaultDisplayable;
        activeDisplayable.update();
    }

    private class ConditionalTuple
    {
        private TypedAbstractFunction<Boolean> condition;
        private Displayable displayable;

        public ConditionalTuple(TypedAbstractFunction<Boolean> condition, Displayable displayable)
        {
            this.condition = condition;
            this.displayable = displayable;
        }
    }
}
