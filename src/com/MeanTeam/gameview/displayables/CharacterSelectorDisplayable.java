package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;
import com.MeanTeam.guiframework.displayables.Displayable;

import java.awt.*;

public class CharacterSelectorDisplayable extends CompositeDisplayable
{
    public CharacterSelectorDisplayable(Point origin)
    {
        super(origin);
    }

    @Override
    public void update()
    {
        super.clear();

        Displayable entityDisplayable = ModelDisplayableFactory.getEntityImageDisplayable();

        super.add(entityDisplayable);
    }
}
