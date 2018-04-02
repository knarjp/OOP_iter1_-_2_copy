package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.util.ImageFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EntityInteractionDisplayable implements Displayable
{
    private Point origin;
    private static Dimension size = new Dimension(128, 128);

    private static Point[] selectorOrigins = new Point[]{
            new Point(114, 22 - 16),
            new Point(114, 38 - 16),
            new Point(114, 54 - 16),
            new Point(114, 70 - 16)
    };

    private int selectorIndex = -1;

    private static BufferedImage background = ImageFactory.makeBorderedRect(128, 128, Color.WHITE);

    public EntityInteractionDisplayable(Point origin)
    {
        this.origin = origin;
    }

    public Point getOrigin() { return origin; }
    public Dimension getSize() { return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(background, drawPt.x, drawPt.y, null);
        g2d.setColor(Color.BLACK);
        g2d.drawString("TALK", drawPt.x + 4, drawPt.y + 16);
        g2d.drawString("ATTACK", drawPt.x + 4, drawPt.y + 32);
        g2d.drawString("USE SKILL", drawPt.x + 4, drawPt.y + 48);
        g2d.drawString("USE ITEM", drawPt.x + 4, drawPt.y + 64);

        if(selectorIndex >= 0 && selectorIndex < selectorOrigins.length)
        {
            Point point = selectorOrigins[selectorIndex];
            g2d.drawImage(ImageFactory.makeFilledTriangleLeft(12, 12, Color.MAGENTA),
                    drawPt.x + point.x, drawPt.y + point.y, null);
        }
    }

    public void setSelectorIndex(int selectorIndex) { this.selectorIndex = selectorIndex; }
}
