package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.guiframework.displayables.StringDisplayable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EntityStatusDisplayable extends CompositeDisplayable
{
    private static Dimension size = new Dimension(208, 141);

    private static BufferedImage background;
    static
    {
        background = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = background.createGraphics();

        g2d.setColor(new Color(0xC5A312));
        g2d.fillRect(0, 0, 208, 141);

        g2d.setColor(new Color(0x005260));
        g2d.fillRect(3, 3, 204, 135);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(6, 6, 200, 132);
    }

    public EntityStatusDisplayable(Point origin, Entity player)
    {
        super(origin);

        super.add(new ImageDisplayable(new Point(0, 0), background));
        super.add(new StringDisplayable(new Point(8, 5),
                () -> "Player Status"));
        super.add(new StringDisplayable(new Point(8, 20),
                () -> "HP: " + player.getCurrentHealth() + " / " + player.getMaxHealth()));
        super.add(new StringDisplayable(new Point(8, 37),
                () -> "XP: " + player.getExperience()));
        super.add(new StringDisplayable(new Point(8, 53),
                () -> "Level: " + player.getLevel()));
        super.add(new StringDisplayable(new Point(8, 69),
                () -> "Orientation: " + player.getOrientation()));
        super.add(new StringDisplayable(new Point(8, 85),
                () -> "Skill: " + player.getActiveSkill().getName()));
        super.add(new StringDisplayable(new Point(8, 101),
                () -> "Stamina: " + player.getCurrentStamina() + " / " + player.getMaxStamina()));
        super.add(new StringDisplayable(new Point(8, 117),
                () -> "Currency: " + player.getCurrentCurrency()));
    }
}
