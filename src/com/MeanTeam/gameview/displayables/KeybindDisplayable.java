package com.MeanTeam.gameview.displayables;

import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.util.ImageFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

public class KeybindDisplayable implements Displayable
{
    private static BufferedImage background = ImageFactory.makeBorderedRect(200, 32, Color.WHITE);
    private static Dimension size = new Dimension(background.getWidth(), background.getHeight());

    private Point origin;

    private KeyRole keyrole;
    private Map<Integer, KeyRole> keyMap;

    public KeybindDisplayable(Point origin, KeyRole keyrole, Map<Integer, KeyRole> keyMap)
    {
        this.origin = origin;
        this.keyrole = keyrole;
        this.keyMap = keyMap;
    }

    public Point getOrigin() { return origin; }
    public Dimension getSize() { return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(background, drawPt.x, drawPt.y, null);
        g2d.setColor(Color.BLACK);

        String string = "" + keyrole + ":";

        for(int key : keyMap.keySet())
        {
            if(keyMap.get(key) == keyrole)
            {
                string += " " + KeyEvent.getKeyText(key);
            }
        }

        g2d.drawString(string, drawPt.x + 4, drawPt.y + 24);
    }
}
