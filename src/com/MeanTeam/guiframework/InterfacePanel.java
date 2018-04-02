package com.MeanTeam.guiframework;

import com.MeanTeam.guiframework.control.KeyRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class InterfacePanel extends JPanel implements KeyListener
{
    private javax.swing.Timer renderTimer;

    private Viewstate activeState;

    private Map<Integer, KeyRole> keybinds;

    private boolean rebinding;
    private KeyRole roleToRebind;

    public InterfacePanel(Viewstate initialState)
    {

        this.setBackground(new Color(0xF9F8CD));

        this.activeState = initialState;

        this.keybinds = new HashMap<>();

        initializeKeybinds();

        renderTimer = new javax.swing.Timer(19, (event) ->
        {
            renderTimer.stop();

            activeState.update();

            repaint();

            renderTimer.start();
        });

        renderTimer.start();

        this.rebinding = false;
        this.roleToRebind = KeyRole.NONE;
    }

    private void initializeKeybinds()
    {
        keybinds.put(KeyEvent.VK_W, KeyRole.N);
        keybinds.put(KeyEvent.VK_A, KeyRole.W);
        keybinds.put(KeyEvent.VK_S, KeyRole.S);
        keybinds.put(KeyEvent.VK_D, KeyRole.E);

        keybinds.put(KeyEvent.VK_UP, KeyRole.N);
        keybinds.put(KeyEvent.VK_LEFT, KeyRole.W);
        keybinds.put(KeyEvent.VK_DOWN, KeyRole.S);
        keybinds.put(KeyEvent.VK_RIGHT, KeyRole.E);

        keybinds.put(KeyEvent.VK_I, KeyRole.INVENTORY);
        keybinds.put(KeyEvent.VK_SPACE, KeyRole.INVENTORY);

        keybinds.put(KeyEvent.VK_ENTER, KeyRole.ACTION);
        keybinds.put(KeyEvent.VK_SPACE, KeyRole.USE);

        keybinds.put(KeyEvent.VK_F5, KeyRole.SAVE);
        keybinds.put(KeyEvent.VK_F8, KeyRole.LOAD);

        keybinds.put(KeyEvent.VK_NUMPAD7, KeyRole.NW);
        keybinds.put(KeyEvent.VK_NUMPAD9, KeyRole.NE);
        keybinds.put(KeyEvent.VK_NUMPAD1, KeyRole.SW);
        keybinds.put(KeyEvent.VK_NUMPAD3, KeyRole.SE);

        keybinds.put(KeyEvent.VK_NUMPAD4, KeyRole.W);
        keybinds.put(KeyEvent.VK_NUMPAD8, KeyRole.N);
        keybinds.put(KeyEvent.VK_NUMPAD6, KeyRole.E);
        keybinds.put(KeyEvent.VK_NUMPAD2, KeyRole.S);

        keybinds.put(KeyEvent.VK_ESCAPE, KeyRole.BACK);
        keybinds.put(KeyEvent.VK_DELETE, KeyRole.BACK);

        keybinds.put(KeyEvent.VK_1, KeyRole.ZERO);
        keybinds.put(KeyEvent.VK_2, KeyRole.ONE);
        keybinds.put(KeyEvent.VK_3, KeyRole.TWO);
        keybinds.put(KeyEvent.VK_4, KeyRole.THREE);
        keybinds.put(KeyEvent.VK_5, KeyRole.FOUR);
        keybinds.put(KeyEvent.VK_6, KeyRole.FIVE);
        keybinds.put(KeyEvent.VK_7, KeyRole.SIX);
        keybinds.put(KeyEvent.VK_8, KeyRole.SEVEN);
    }

    public void enableRebindingMode(KeyRole roleToRebind)
    {
        rebinding = true;
        this.roleToRebind = roleToRebind;
    }

    public void bind(int keycode, KeyRole keyrole)
    {
        keybinds.remove(keycode);
        keybinds.put(keycode, keyrole);
        rebinding = false;
    }

    public void unbind(int keycode)
    {
        keybinds.remove(keycode);
    }

    public Map<Integer, KeyRole> getKeybinds() {
        return keybinds;
    }

    public void setKeybinds(Map<Integer, KeyRole> keybinds) {
        this.keybinds = keybinds;
    }

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

    public void keyPressed(KeyEvent event)
    {
        if(rebinding)
        {
            bind(event.getKeyCode(), roleToRebind);
        }
        else activeState.parseKeyPress(keybinds.getOrDefault(event.getKeyCode(), KeyRole.NONE));
        //System.out.println("" + event.getKeyCode() + " " + event.getKeyChar());
    }

    public void keyReleased(KeyEvent event) { activeState.parseKeyRelease(keybinds.getOrDefault(event.getKeyCode(), KeyRole.NONE));}

    // Unused method, required for KeyListener interface
    public void keyTyped(KeyEvent event) {  }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        super.validate();
        super.repaint();
        activeState.draw((Graphics2D) g);
    }

/*    public void imgBkgd(Image img){
        //Image backdrop = new ImageIcon("assets/mainMenu.jpg").getImage();
        BufferedImage image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.drawImage(img, 0, 0,null);
        super.paintComponent(g2d);
    }*/
}
