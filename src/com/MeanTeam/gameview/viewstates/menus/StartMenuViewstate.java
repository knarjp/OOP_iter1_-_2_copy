package com.MeanTeam.gameview.viewstates.menus;

import com.MeanTeam.gameview.viewstates.GameViewState;
import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.guiframework.displayables.buttons.Button;
import com.MeanTeam.util.ImageFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StartMenuViewstate extends MenuViewstate
{
    Image img = new ImageIcon("assets/mainMenu.jpg").getImage();
    private Displayable background;
    private Color c = new Color(0x5E0059);
    private Color h = new Color(0xF9F8CD);
    private Color s = new Color(0x003944);
    private Color p = new Color(0x005260);

    public StartMenuViewstate(InterfacePanel panel)
    {
        super(panel);
        Image backdrop = new ImageIcon("assets/mainMenu.jpg").getImage();
        BufferedImage baseImg = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = baseImg.createGraphics();

        // ----- New
        g2d.drawImage(backdrop, 0, 0, null);

        this.background = new ImageDisplayable(new Point(0, 0), baseImg);

        //super.addUnderlay(mainMenu);
/*
        super.imgBkgd(img);
        super.update();*/

        super.add(new Button(new Point(128, 128),
                ImageFactory.makeCenterLabeledRect(256, 64, h, s, c, "New Game"),
                ImageFactory.makeCenterLabeledRect(256, 64, p, s, c, "New Game"),
                ImageFactory.makeCenterLabeledRect(256, 64, p, s, c, "New Game"),
                () -> panel.setActiveState(new CharacterSelectViewstate(panel))
        ));

        super.add(new Button(new Point(128, 256),
                ImageFactory.makeCenterLabeledRect(256, 64, h, s, c, "Load Game"),
                ImageFactory.makeCenterLabeledRect(256, 64, p, s, c, "Load Game"),
                ImageFactory.makeCenterLabeledRect(256, 64, p, s, c, "Load Game"),
                () -> {GameViewState gamestate = new GameViewState(new Dimension(panel.getSize()), panel); panel.setActiveState(gamestate); gamestate.loadGame();}
        ));

        super.add(new Button(new Point(128, 384),
                ImageFactory.makeCenterLabeledRect(256, 64, h, s, c, "Configure Controls"),
                ImageFactory.makeCenterLabeledRect(256, 64, p, s, c, "Configure Controls"),
                ImageFactory.makeCenterLabeledRect(256, 64, p, s, c, "Configure Controls"),
                () -> panel.setActiveState(new ConfigViewstate(panel, this))
        ));

        super.add(new Button(new Point(128, 512),
                ImageFactory.makeCenterLabeledRect(256, 64, h, s, c, "Exit Game"),
                ImageFactory.makeCenterLabeledRect(256, 64, Color.YELLOW, s, c, "Exit Game"),
                ImageFactory.makeCenterLabeledRect(256, 64, Color.ORANGE, s, c, "Exit Game"),
                () -> System.exit(0)
        ));
        //super.add(displays);
        //super.add(menus);
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        background.draw(g2d);
        super.draw(g2d);
    }
}
