package com.MeanTeam.gameview.displayables;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.Item;
import com.MeanTeam.gamemodel.tile.Decal;
import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.tile.Obstacle;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;

import java.awt.*;

public class TileDisplayable extends CompositeDisplayable
{
    private static Dimension size = new Dimension(64, 64);
    private GameModel model;
    private Point offsetFromPlayer;

    private int turnCount = -1;

    public TileDisplayable(Point origin, Point offsetFromPlayer, GameModel model)
    {
        super(origin);
        this.model = model;
        this.offsetFromPlayer = offsetFromPlayer;
    }

    @Override
    public void update()
    {
        int turnCount = model.getTurnCount();
        if(turnCount != this.turnCount)
        {
            super.clear();

            Point targetPt = model.getOffsetFromPlayer(offsetFromPlayer);

            super.add(ModelDisplayableFactory.getTerrainDisplayable(model.getTerrain(targetPt)));

            Decal decal = model.getDecal(targetPt);
            if(decal != null)
                super.add(ModelDisplayableFactory.getDecalDisplayable(decal));

            Item item = model.getItem(targetPt);
            if(item != null)
                super.add(ModelDisplayableFactory.getItemDisplayable(item));

            Obstacle obstacle = model.getObstacle(targetPt);
            if(obstacle != null)
                super.add(ModelDisplayableFactory.getObstacleDisplayable(obstacle));

            Trap trap = model.getTrap(targetPt);
            if(trap != null)
                super.add(ModelDisplayableFactory.getTrapDisplayable(trap));

            Entity entity = model.getEntity(targetPt);
            if(entity != null)
                super.add(ModelDisplayableFactory.getEntityDisplayable(entity));

            this.turnCount = turnCount;
        }
        super.update();
    }

    @Override
    public Dimension getSize() { return size; }
}
