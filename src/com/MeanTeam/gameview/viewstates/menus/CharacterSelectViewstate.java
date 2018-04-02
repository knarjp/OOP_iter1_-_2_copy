package com.MeanTeam.gameview.viewstates.menus;

import com.MeanTeam.gameview.ImageLoader;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.gameview.viewstates.GameViewState;
import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.displayables.buttons.Button;
import com.MeanTeam.util.ImageFactory;

import java.awt.*;

public class CharacterSelectViewstate extends MenuViewstate
{
    public CharacterSelectViewstate(InterfacePanel panel)
    {
        super(panel);

        //render pedigree here SOMEHOW

        super.add(new Button(new Point(730, 30),
                ImageFactory.makeIcon(ImageLoader.getSkillSummary(0),538, 300),
                ImageFactory.makeIconSelec(ImageLoader.getSkillSummary(0),538, 300),
                ImageFactory.makeIconSelec(ImageLoader.getSkillSummary(0),538, 300),
                () -> {
                    ModelDisplayableFactory.setEntityIndex(0); panel.setActiveState(new GameViewState(panel.getSize(), panel));}
        ));

        super.add(new Button(new Point(730, 360),
                ImageFactory.makeIcon(ImageLoader.getSkillSummary(1), 538, 300),
                ImageFactory.makeIconSelec(ImageLoader.getSkillSummary(1),538, 300),
                ImageFactory.makeIconSelec(ImageLoader.getSkillSummary(1),538, 300),
                () -> {ModelDisplayableFactory.setEntityIndex(1); panel.setActiveState(new GameViewState(panel.getSize(), panel));}
        ));

        super.add(new Button(new Point(120, 330),
                ImageFactory.makeIcon(ImageLoader.getSkillSummary(2),538,345),
                ImageFactory.makeIconSelec(ImageLoader.getSkillSummary(2),538, 345),
                ImageFactory.makeIconSelec(ImageLoader.getSkillSummary(2),538, 345),
                () -> {ModelDisplayableFactory.setEntityIndex(2); panel.setActiveState(new GameViewState(panel.getSize(), panel));}
        ));
    }
}
