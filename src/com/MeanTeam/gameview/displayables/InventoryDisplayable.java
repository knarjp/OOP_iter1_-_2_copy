package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
//import com.MeanTeam.gamemodel.tile.items.EquippableItem;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.gameview.ImageLoader;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.util.ImageFactory;
import javafx.scene.layout.BorderStroke;
import javafx.scene.shape.StrokeType;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class InventoryDisplayable implements Displayable
{
    private Point origin;
    private static Dimension size = new Dimension(208,303);
    private Font font = new Font("Garamond", Font.CENTER_BASELINE, 14);

    private static Point[] selectorOrigins = new Point[]{
            new Point(200, 3+19),
            new Point(200, 3+35),
            new Point(200, 3+51),
            new Point(200, 3+67),
            new Point(200, 3+83),
            new Point(200, 3+99),
            new Point(200, 3+115),
            new Point(200, 3+131),
            new Point(200, 3+147),
            new Point(200, 3+163),
            new Point(200, 3+207),
            new Point(200, 3+223),
            new Point(200, 3+241)
    };

    private int selectorIndex = -1;

    private static BufferedImage background = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    static
    {
        Graphics2D g2d = background.createGraphics();
        g2d.setColor(new Color(0xC5A312));
        g2d.fillRect(0, 0, size.width+2, size.height);

        g2d.setColor(new Color(0x005260));
        g2d.fillRect(3, 3, size.width, 297);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(6, 6, size.width - 5, 294);
    }

    private Entity entity;

    public InventoryDisplayable(Point origin, Entity entity)
    {
        this.origin = origin;
        this.entity = entity;
    }

    public Point getOrigin() { return origin; }
    public Dimension getSize() { return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(background, drawPt.x, drawPt.y, null);

        g2d.setFont(font.deriveFont(Font.BOLD));
        g2d.setColor(new Color(0x50002D));
        g2d.drawString("INVENTORY: ", drawPt.x + 8, drawPt.y + 20);

        Inventory inventory = entity.getBackpack();

        int ymax = 16;
        g2d.setFont(font.deriveFont(12));
        g2d.setColor(new Color(0x562553));
        for(int i = 0; i < inventory.getSizeLimit(); i++)
        {
            TakeableItem item = inventory.getItemAtSlot(i);
            String lineString = "SLOT " + i + ": ";
            if(item != null)
            {
                lineString += item.getName();
            }
            else
            {
                lineString += " IS EMPTY";
            }
            g2d.drawString(lineString, drawPt.x + 8, drawPt.y + 20 + 16 + (i * 16));

            ymax = 16 + 16 + (i * 16);
        }
        g2d.setFont(font.deriveFont(Font.BOLD));
        g2d.setColor(new Color(0x002B2A));
        g2d.drawString("EQUIPMENT: ", drawPt.x + 8, drawPt.y + ymax + 16 + 16);

        g2d.setFont(font.deriveFont(12));
        g2d.setColor(new Color(0x3F4538));
        TakeableItem weapon = entity.getWeapon();
        if(weapon != null) {
            g2d.drawString(weapon.getName(), drawPt.x + 8, drawPt.y + ymax + 32 + 16 + (0 * 16));
        }

        TakeableItem armor = entity.getArmor();
        if(armor != null) {
            g2d.drawString(armor.getName(), drawPt.x + 8, drawPt.y + ymax + 32 + 16 + (1 * 16));
        }

        TakeableItem ring = entity.getRing();
        if(ring != null) {
            g2d.drawString(ring.getName(), drawPt.x + 8, drawPt.y + ymax + 32 + 16 + (2 * 16));
        }

        if(selectorIndex >= 0 && selectorIndex < selectorOrigins.length)
        {
            Point point = selectorOrigins[selectorIndex];
            g2d.drawImage(ImageLoader.getArrow(),
                    drawPt.x + point.x, drawPt.y + point.y, null);
        }
    }

    public void setSelectorIndex(int index)
    {
        selectorIndex = index;
    }
}
