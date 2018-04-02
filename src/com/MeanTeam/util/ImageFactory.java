package com.MeanTeam.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageFactory
{
    private static Font font = new Font("Garamond", Font.CENTER_BASELINE, 18);
    private Color c = new Color(0x5E0059);
    private Color h = new Color(0xF9F8CD);
    private Color s = new Color(0x003944);

    public static BufferedImage makeCenterLabeledRect(int width, int height, Color bodyColor, Color borderColor, Color textColor, String text)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.setColor(bodyColor);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(borderColor);
        g2d.drawRect(0, 0, width - 1, height - 1);
        g2d.setColor(textColor);
        g2d.drawString(text, (width / 2) - (metrics.stringWidth(text) / 2), (height/2) + (metrics.getHeight()/4));
        return image;
    }

    public static BufferedImage makeBorderedRect(int width, int height, Color bodyColor)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(0x7A0074));
        g2d.fillRect(0, 0, width , height);
        g2d.setColor(bodyColor);
        g2d.fillRect(2, 2, width - 4, height - 4);
        return image;
    }

    public static BufferedImage makeRect(int width, int height, Color bodyColor)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(bodyColor);
        g2d.fillRect(0, 0, width, height);
        return image;
    }

    public static BufferedImage makeFilledOval(int width, int height, Color bodyColor)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(bodyColor);
        g2d.fillOval(0, 0, width, height);
        return image;
    }

    public static BufferedImage makeFilledTriangleUp(int base, int height, Color bodyColor)
    {
        BufferedImage image = new BufferedImage(base, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        int[] xPoints = {0, (base/2), base};
        int[] yPoints = {height, 0, height};
        g2d.setColor(bodyColor);
        g2d.fillPolygon(xPoints, yPoints, 3);
        return image;
    }

    public static BufferedImage makeFilledTriangleLeft(int base, int height, Color bodyColor)
    {
        BufferedImage image = new BufferedImage(base, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        int[] xPoints = {0, base, base};
        int[] yPoints = {height/2, 0, height};
        g2d.setColor(bodyColor);
        g2d.fillPolygon(xPoints, yPoints, 3);
        return image;
    }

    public static BufferedImage makeIcon(Image img, int w, int h){
        BufferedImage image = new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        return image;
    }

    public static BufferedImage makeIconSelec(Image img, int w, int h){
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(new Color(236,119,59, 79));
        g2d.fillRect(0,0, w,h);
        return image;
    }
}
