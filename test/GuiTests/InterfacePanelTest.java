package GuiTests;

import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class InterfacePanelTest
{
    @Test
    public void testKeyPressAndRelease()
    {
        InputTestingState inputTestingState = new InputTestingState();
        InterfacePanel testingPanel = new InterfacePanel(inputTestingState);

        int keyCode = 100;

        KeyEvent event = new KeyEvent(new JButton(), 0, 0, 0, keyCode, 'A');

        //Assert.assertTrue(!inputTestingState.isDepressed(keyCode));

        testingPanel.keyPressed(event);

        //Assert.assertTrue(inputTestingState.isDepressed(keyCode));

        testingPanel.keyReleased(event);

        //Assert.assertTrue(!inputTestingState.isDepressed(keyCode));

        testingPanel.keyTyped(event);

        //Assert.assertTrue(!inputTestingState.isDepressed(keyCode));
    }

    @Test
    public void testStateSwapping()
    {
        Viewstate initialState = new Viewstate();
        Viewstate swapTestingState = new Viewstate();

        Assert.assertNotEquals(initialState, swapTestingState);

        InterfacePanel testingPanel = new InterfacePanel(initialState);

        Assert.assertEquals(testingPanel.getActiveState(), initialState);

        testingPanel.setActiveState(swapTestingState);

        Assert.assertEquals(testingPanel.getActiveState(), swapTestingState);
    }

    @Test
    public void testPanelRendering()
    {
        BufferedImage redImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = redImage.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, redImage.getWidth(), redImage.getHeight());

        ImageDisplayable displayable = new ImageDisplayable(new Point(500, 500), redImage);
        Set<Displayable> displayables = new HashSet<>();
        displayables.add(displayable);

        Viewstate state = new Viewstate();
        state.add(displayables);

        InterfacePanel panel = new InterfacePanel(state);

        BufferedImage canvas = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();

        panel.paintComponent(g2d);

        Assert.assertEquals(canvas.getRGB(525, 525), Color.RED.getRGB());
    }

    private class InputTestingState extends Viewstate
    {
        private Set<KeyRole> depressedKeycodes = new HashSet<>();

        @Override
        public void parseKeyPress(KeyRole keyRole)
        {
            super.parseKeyPress(keyRole);
            depressedKeycodes.add(keyRole);
        }

        @Override
        public void parseKeyRelease(KeyRole keyRole)
        {
            super.parseKeyRelease(keyRole);
            depressedKeycodes.remove(keyRole);
        }

        public boolean isDepressed(KeyRole keyrole)
        {
            return depressedKeycodes.contains(keyrole);
        }
    }
}
