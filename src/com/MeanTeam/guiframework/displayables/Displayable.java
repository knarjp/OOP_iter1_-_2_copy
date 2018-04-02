package com.MeanTeam.guiframework.displayables;

import java.awt.*;

public interface Displayable
{
    Point getOrigin();
    Dimension getSize();

    default void draw(Graphics2D g2d)
    {
        this.drawAt(g2d, this.getOrigin());
    }

    default void drawWithOffset(Graphics2D g2d, Point offset)
    {
        Point origin = this.getOrigin();
        this.drawAt(g2d, new Point(origin.x + offset.x, origin.y + offset.y));
    }

    void drawAt(Graphics2D g2d, Point drawPt);

    default void update() {}
    default boolean expired() {return false;}
}
