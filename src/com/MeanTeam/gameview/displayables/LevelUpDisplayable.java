package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gameview.ImageLoader;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.util.ImageFactory;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class LevelUpDisplayable implements Displayable
{
    private Font font = new Font("Garamond", Font.CENTER_BASELINE, 12);
    private static Point[] selectorOrigins = new Point[]{
            new Point(114, 22),
            new Point(114, 38),
            new Point(114, 54),
            new Point(114, 70),
            new Point(114, 86),
            new Point(114, 102),
            new Point(114, 118),
            new Point(114, 134),
            new Point(114, 150),
            new Point(114, 166),
            new Point(114, 210),
            new Point(114, 226),
            new Point(114, 244)
    };
    private Image img = new ImageIcon("assets/level_menu.jpg").getImage();
    private static BufferedImage background = new BufferedImage(144, 256, BufferedImage.TYPE_INT_ARGB);

    private Point origin;
    private static Dimension size = new Dimension(background.getWidth(), background.getHeight());

    private Entity entity;
    private List<Skill> skills;
    private int skillPoints;
    private int index;

    public LevelUpDisplayable(Point origin, Entity entity, List<Skill> skills, int skillPoints)
    {
        this.origin = origin;
        this.entity = entity;
        this.skills = skills;
        this.skillPoints = skillPoints;
        this.index = 0;
    }

    public int getSkillPoints() { return skillPoints; }
    public void spendSkillPoint() { skillPoints--; }

    public void setIndex(int index) { this.index = index; }

    public Point getOrigin() { return origin; }
    public Dimension getSize() { return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(img, drawPt.x, drawPt.y, null);
        g2d.setColor(new Color(0xBB9FB5));
        g2d.drawString("Skill points to spend: " + skillPoints, drawPt.x + 4, drawPt.y + 16);
        int j = 32;
        for(Skill skill : skills)
        {
            g2d.drawString(skill.getName() + " (" + entity.getSkillLevel(skill) + ")", drawPt.x + 4, drawPt.y + j);
            j += 16;
        }

        if(index >= 0 && index < selectorOrigins.length)
        {
            Point point = selectorOrigins[index];
            g2d.drawImage(ImageLoader.getArrow(),
                    drawPt.x + point.x, drawPt.y + point.y, null);
        }
    }
}
