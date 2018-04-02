package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.guiframework.displayables.StringDisplayable;
import com.MeanTeam.util.ImageFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DialogDisplayable extends CompositeDisplayable
{
    private static BufferedImage background = ImageFactory.makeBorderedRect(256, 32, Color.WHITE);

    private String message = "";

    public DialogDisplayable(Point origin)
    {
        super(origin);
    }

    public void update()
    {
        super.clear();
        super.add(new ImageDisplayable(new Point(0, 0), background));
        super.add(new StringDisplayable(new Point(4,  8), () -> message));
        super.update();
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
