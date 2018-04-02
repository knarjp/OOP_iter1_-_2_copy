package com.MeanTeam.gameview.viewstates;

import com.MeanTeam.gameview.displayables.CharacterSelectorDisplayable;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.MenuPanel;
import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.util.AbstractFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class MainMenuViewstate extends Viewstate
{
    private InterfacePanel panel;

    private List<Displayable> menus;

    private InputState inputState;
    public boolean start = false;

    public MainMenuViewstate(InterfacePanel panel) {
        super();
        this.panel = panel;
        this.menus = new ArrayList<>(5);
        //======================== Main Menu ViewState ==================================
        Image backdrop = new ImageIcon("assets/mainMenu.jpg").getImage();
        BufferedImage baseImg = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = baseImg.createGraphics();

        // ----- New
        g2d.drawImage(backdrop, 0, 0, null);
        g2d.setColor(new Color(0x4E5E55));
        g2d.fillRect(2 * backdrop.getWidth(null) / 3, backdrop.getHeight(null) / 3, 260, 50); //menu button
        //load box
        g2d.fillRect(2 * backdrop.getWidth(null) / 3, backdrop.getHeight(null) / 2, 260, 50); //menu button
        //exit box
        g2d.fillRect(2 * backdrop.getWidth(null) / 3, 4 * backdrop.getHeight(null) / 6, 260, 50); //menu button

        g2d.setColor(new Color(40, 11, 0)); //dark red-brown
        g2d.setFont(new Font("Garamond", Font.HANGING_BASELINE, 32).deriveFont(Font.BOLD));
        g2d.drawString("New Game ('ENTER')", 2 * backdrop.getWidth(null) / 3 + 20, backdrop.getHeight(null) / 3 + 35);
        g2d.drawString("Load Game ('F5')", 2 * backdrop.getWidth(null) / 3 + 20, backdrop.getHeight(null) / 2 + 35);
        g2d.drawString("Exit ('esc')", 2 * backdrop.getWidth(null) / 3 + 50, 4 * backdrop.getHeight(null) / 6 + 35);

        ImageDisplayable mainMenu = new ImageDisplayable(new Point(0, 0), baseImg);

        menus.add(0, mainMenu);
        //menus[0] = mainMenu;

    }
        @Override
        public void changeSize (Dimension size){
            //super.changeSize(size);
        }

        //=======================================================================================
        @Override
        public void parseKeyPress (KeyRole keyrole){
            inputState.parseKeyPress(keyrole);
        }

        @Override
        public void parseKeyRelease (KeyRole keyrole){
            inputState.parseKeyRelease(keyrole);
        }
/*
        public void setInitializeState ()
        {
            inputState = initializeGame;
        }*/

        //==========================================================================================
        private interface InputState {
            void parseKeyPress(KeyRole keyrole);

            void parseKeyRelease(KeyRole keyrole);
        }
}
