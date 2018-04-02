package com.MeanTeam.guiframework.displayables.buttons;

import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.util.AbstractFunction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class KeybindButton extends Button
{
    private KeyRole keyrole;

    public KeybindButton(InterfacePanel panel, KeyRole keyrole, Point origin, BufferedImage baseImage, BufferedImage selectedImage, BufferedImage pressedImage)
    {
        super(origin, baseImage, selectedImage, pressedImage, () -> panel.enableRebindingMode(keyrole));
        this.keyrole = keyrole;
    }

    public KeyRole getKeyrole() { return keyrole; }
}
