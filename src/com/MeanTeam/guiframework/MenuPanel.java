package com.MeanTeam.guiframework;

import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.util.AbstractFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public abstract class MenuPanel extends JPanel implements KeyListener
{
    //private javax.swing.Timer renderTimer;

    private Viewstate activeState;

    private Map<Integer, AbstractFunction> keybinds;

    public MenuPanel(Viewstate initialState)
    {

        this.activeState = initialState;

        this.keybinds = new HashMap<>();

        initializeKeybinds();
    }

    abstract protected void initializeKeybinds();

    // Define later to implement adaptive window resizing:
    public void changeSize()
    {
        activeState.changeSize(this.getSize());
    }

    public void setActiveState(Viewstate nextState)
    {
        this.activeState = nextState;
        activeState.changeSize(this.getSize());
    }

    public Viewstate getActiveState()
    {
        return activeState;
    }
    // Unused method, required for KeyListener interface
    public void keyPressed(KeyEvent event) { }
    public void keyReleased(KeyEvent event) { }

    public void keyTyped(KeyEvent event) { }


    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        activeState.draw((Graphics2D) g);
    }
}
