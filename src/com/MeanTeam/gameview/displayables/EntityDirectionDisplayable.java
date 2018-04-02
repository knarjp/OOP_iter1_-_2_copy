package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.util.Orientation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.EnumMap;

public class EntityDirectionDisplayable implements Displayable
{
    //private BufferedImage[] directionImages;
    private EnumMap<Orientation, BufferedImage> directionImages;
    private Point origin;
    private static Dimension size = new Dimension(28, 28);
    private Entity entity;

    public EntityDirectionDisplayable(Point origin, Entity entity,
            EnumMap<Orientation, BufferedImage> directionImages/*BufferedImage[] directionImages*/)
    {
        this.origin = origin;
        this.entity = entity;
        this.directionImages = directionImages;
    }

    public Point getOrigin() { return origin; }
    public Dimension getSize() { return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        //int index = entity.getOrientation() / 45;
        //g2d.drawImage(directionImages[index], drawPt.x, drawPt.y, null);
        g2d.drawImage(directionImages.get(entity.getOrientation()), drawPt.x, drawPt.y, null);
    }
}
