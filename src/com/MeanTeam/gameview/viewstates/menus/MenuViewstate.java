package com.MeanTeam.gameview.viewstates.menus;

import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.guiframework.displayables.buttons.Button;
import com.MeanTeam.util.AbstractFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class MenuViewstate extends Viewstate
{
    private InterfacePanel panel;
    private List<Button> buttons;
    private int currentIndex;

    private Map<KeyRole, AbstractFunction> inputMap;

    public MenuViewstate(InterfacePanel panel)
    {
        super();
        this.panel = panel;

        super.add(this.buttons = new ArrayList<>());
        this.currentIndex = 0;

        this.inputMap = new HashMap<>();
        inputMap.put(KeyRole.ACTION, () -> buttons.get(currentIndex).press());
        inputMap.put(KeyRole.N, () -> adjustIndex(-1));
        inputMap.put(KeyRole.S, () -> adjustIndex(1));
        inputMap.put(KeyRole.W, () -> adjustIndex(-1));
        inputMap.put(KeyRole.E, () -> adjustIndex(1));

    }

    public void bind(KeyRole keyrole, AbstractFunction func)
    {
        inputMap.put(keyrole, func);
    }

    public void add(Button button)
    {
        buttons.add(button);
    }

    public void remove(Button button)
    {
        buttons.remove(button);
    }

    @Override
    public void parseKeyPress(KeyRole keyrole)
    {
        inputMap.getOrDefault(keyrole, AbstractFunction.NULL).execute();
    }

    protected InterfacePanel getPanel() { return panel; }

    private void adjustIndex(int delta)
    {
        buttons.get(currentIndex).deselect();
        currentIndex += delta;
        if(currentIndex < 0) currentIndex = buttons.size() - 1;
        if(currentIndex > buttons.size() - 1) currentIndex = 0;
        buttons.get(currentIndex).select();
    }
    public void imgBkgd(Image img){
        //Image backdrop = new ImageIcon("assets/mainMenu.jpg").getImage();
        BufferedImage image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.drawImage(img, 0, 0,null);
        panel.paint(g2d);
    }
}
