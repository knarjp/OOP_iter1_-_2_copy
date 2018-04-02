package com.MeanTeam.guiframework.displayables;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDisplayable implements Displayable
{
    private Point origin;
    private Dimension size;
    private BufferedImage image;

    public ImageDisplayable(Point origin, BufferedImage image)
    {
        this.origin = origin;
        this.setImage(image);
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;
        this.size = new Dimension(image.getWidth(), image.getHeight());
    }

    public Image getImage() {return image;}

    // Displayable interface:

    public Point getOrigin() {return origin;}
    public Dimension getSize() {return size;}

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(image, drawPt.x, drawPt.y, null);
    }
}
