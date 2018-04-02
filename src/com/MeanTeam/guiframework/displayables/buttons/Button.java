package com.MeanTeam.guiframework.displayables.buttons;

import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.util.AbstractFunction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button extends ImageDisplayable
{
    private BufferedImage baseImage, selectedImage, pressedImage;
    private AbstractFunction pressEffect;

    public Button(Point position, BufferedImage baseImage, BufferedImage selectedImage, BufferedImage pressedImage, AbstractFunction pressEffect)
    {
        super(position, baseImage);
        this.baseImage = baseImage;
        this.selectedImage = selectedImage;
        this.pressedImage = pressedImage;

        this.pressEffect = pressEffect;
    }

    public void select() { super.setImage(selectedImage); }
    public void deselect() { super.setImage(baseImage); }

    public void press()
    {
        super.setImage(pressedImage);
        pressEffect.execute();
    }

    public void release() { super.setImage(baseImage); }

    public static Button NULL = new Button(new Point(0, 0), new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            AbstractFunction.NULL);
}
