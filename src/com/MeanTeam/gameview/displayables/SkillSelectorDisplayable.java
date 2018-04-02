package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.util.ImageFactory;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class SkillSelectorDisplayable extends CompositeDisplayable
{
    public Color selected = new Color(0xC5F2ED);
    private static ImageDisplayable background = new ImageDisplayable(new Point(0, 0), ImageFactory.makeBorderedRect(700, 30, Color.WHITE));

    private Entity entity;

    public SkillSelectorDisplayable(Point origin, Entity entity)
    {
        super(origin);
        this.entity = entity;
        super.add(background);
    }

    public void update()
    {
        super.clear();
        super.add(background);

        // add SkillDisplayables for each of Entity's skills

        int i = 0;
        for(Skill skill : entity.getSkills())
        {
            super.add(new SkillDisplayable(new Point(i, 0), entity, skill));
            i += (skill.getName().length()*10);
            i += 10;
        }

        super.update();
    }

    private static class SkillDisplayable implements Displayable
    {
        private Point origin;
        private static Dimension size = new Dimension(100, 25);

        private Entity entity;
        private Skill skill;

        public SkillDisplayable(Point origin, Entity entity, Skill skill)
        {
            this.origin = origin;
            this.entity = entity;
            size.width = (skill.getName().length()*9);
            this.skill = skill;
        }

        public Point getOrigin() { return origin; }
        public Dimension getSize() { return size; }

        public void drawAt(Graphics2D g2d, Point drawPt)
        {
            Color backgroundColor = skill.equals(entity.getActiveSkill()) ? Color.getColor("",new Color(237, 190, 111, 255)) : Color.WHITE;
            g2d.setColor(backgroundColor);
            g2d.fillRect(drawPt.x + 2, drawPt.y + 2, size.width - 4, 21);
            g2d.setColor(new Color(0x003944));
            g2d.setFont(new Font("Garamond", Font.CENTER_BASELINE, 16));
            g2d.drawString(skill.getName(), drawPt.x + 7, drawPt.y + 20);
            if(skill.isOnCooldown()) {
                g2d.setColor(new Color(40, 0, 60, 112));
                g2d.fillRect(drawPt.x, drawPt.y, size.width, 30);
            }
            else { }


        }
    }
}
