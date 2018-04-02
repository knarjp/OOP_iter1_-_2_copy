package com.MeanTeam;

import com.MeanTeam.gameview.viewstates.menus.ConfigViewstate;
import com.MeanTeam.gameview.viewstates.MainMenuViewstate;
import com.MeanTeam.gameview.viewstates.menus.StartMenuViewstate;
import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class RunGame
{
    //private static final String assetsDir = "assets/";

    private static JFrame frame;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(RunGame::createAndShowGUI);
    }

    public static JFrame getFrame() {return frame;}

    public static void createAndShowGUI()
    {
        frame = new JFrame();

        frame.setTitle("COP4331 - S18 - Mean Team - Iteration 2");
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Viewstate exampleViewstate = new Viewstate();

        //=========================================================
        // Initialize example viewstate stuff:
        BufferedImage exampleImage = new BufferedImage(336, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D exampleGraphics = exampleImage.createGraphics();
        exampleGraphics.setColor(Color.WHITE);
        exampleGraphics.fillRect(0, 0, exampleImage.getWidth(), exampleImage.getHeight());
        exampleGraphics.setColor(Color.BLACK);
        exampleGraphics.setFont(new Font("calibri", Font.BOLD, 64));
        exampleGraphics.drawString("ITERATION 1", 0, 56);
        ImageDisplayable exampleDisplayable = new ImageDisplayable(
                new Point(frame.getWidth() / 2 - exampleImage.getWidth() / 2,
                        frame.getHeight() / 2 - exampleImage.getHeight() / 2),
                exampleImage);

        List<Displayable> exampleDisplayableSet = new ArrayList<>();
        exampleDisplayableSet.add(exampleDisplayable);

        exampleViewstate.add(exampleDisplayableSet);

        // End example viewstate stuff
        //=========================================================

        InterfacePanel panel = new InterfacePanel(new Viewstate()); //new InterfacePanel(exampleViewstate);

        panel.setActiveState(new StartMenuViewstate(panel));

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.addKeyListener(panel);
        //frame.getContentPane().add(mainMenu, BorderLayout.CENTER);

        frame.addComponentListener(new ComponentListener()
        {
            public void componentResized(ComponentEvent e)
            {
                panel.changeSize();
            }
            public void componentMoved(ComponentEvent e) { }
            public void componentHidden(ComponentEvent e) { }
            public void componentShown(ComponentEvent e) { }
        });

        frame.validate();
        frame.setVisible(true);
    }
}
