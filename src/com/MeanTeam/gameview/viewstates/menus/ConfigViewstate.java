package com.MeanTeam.gameview.viewstates.menus;

import com.MeanTeam.gameview.displayables.KeybindDisplayable;
import com.MeanTeam.gameview.viewstates.MainMenuViewstate;
import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.guiframework.displayables.buttons.Button;
import com.MeanTeam.guiframework.displayables.buttons.KeybindButton;
import com.MeanTeam.util.AbstractFunction;
import com.MeanTeam.util.ImageFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ConfigViewstate extends Viewstate
{

    private InterfacePanel panel;
    private List<Button> buttons;
    private Set<KeybindDisplayable> displays;
    private int currentIndex;

    private Map<KeyRole, AbstractFunction> inputMap;

    public ConfigViewstate(InterfacePanel panel, Viewstate mainMenuViewstate)
    {
        this.panel = panel;
        this.inputMap = new HashMap<>();
        inputMap.put(KeyRole.BACK, () -> panel.setActiveState(mainMenuViewstate));
        inputMap.put(KeyRole.ACTION, () -> buttons.get(currentIndex).press());
        inputMap.put(KeyRole.N, () -> adjustIndex(-1));
        inputMap.put(KeyRole.S, () -> adjustIndex(1));

        super.add(buttons = new ArrayList<>());
        super.add(displays = new HashSet<>());

        int i = 0;
        int j = 0;
        for(KeyRole keyrole : KeyRole.values())
        {
            if (i > this.panel.getKeybinds().size() / 2){
                buttons.add(new KeybindButton(panel, keyrole, new Point(440, 32 + j * 40),
                        ImageFactory.makeCenterLabeledRect(128, 32, Color.WHITE, Color.GRAY, Color.BLACK, keyrole.toString()),
                        ImageFactory.makeCenterLabeledRect(128, 32, Color.RED, Color.GRAY, Color.BLACK, keyrole.toString()),
                        ImageFactory.makeCenterLabeledRect(128, 32, Color.ORANGE, Color.GRAY, Color.BLACK, keyrole.toString())));

                displays.add(new KeybindDisplayable(new Point(440 + 128 + 16, 32 + j * 40), keyrole, panel.getKeybinds()));

                j++;
            }
            else {
                buttons.add(new KeybindButton(panel, keyrole, new Point(64, 32 + i * 40),
                        ImageFactory.makeCenterLabeledRect(128, 32, Color.WHITE, Color.GRAY, Color.BLACK, keyrole.toString()),
                        ImageFactory.makeCenterLabeledRect(128, 32, Color.RED, Color.GRAY, Color.BLACK, keyrole.toString()),
                        ImageFactory.makeCenterLabeledRect(128, 32, Color.ORANGE, Color.GRAY, Color.BLACK, keyrole.toString())));

                displays.add(new KeybindDisplayable(new Point(64 + 128 + 16, 32 + i * 40), keyrole, panel.getKeybinds()));

            }
            i++;
        }

        currentIndex = 0;
        buttons.get(0).select();

        // instantiate all the keybinding buttons here...
        // buttons.add(new Button(...));

        buttons.add(new Button(new Point(800, 32),
                ImageFactory.makeCenterLabeledRect(128, 64, Color.WHITE, Color.GRAY, Color.BLACK, "MAIN MENU"),
                ImageFactory.makeCenterLabeledRect(128, 64, Color.RED, Color.GRAY, Color.BLACK, "MAIN MENU"),
                ImageFactory.makeCenterLabeledRect(128, 64, Color.ORANGE, Color.GRAY, Color.BLACK, "MAIN MENU"),
                () -> panel.setActiveState(mainMenuViewstate)));
    }

    private void adjustIndex(int delta)
    {
        buttons.get(currentIndex).deselect();
        currentIndex += delta;
        if(currentIndex < 0) currentIndex = buttons.size() - 1;
        if(currentIndex > buttons.size() - 1) currentIndex = 0;
        buttons.get(currentIndex).select();
    }

    private void bind(int keycode, KeyRole keyrole)
    {
        panel.bind(keycode, keyrole);
    }

    private void unbind(int keycode)
    {
        panel.unbind(keycode);
    }

    // could workaround using non-configurable mouse input?
    // could also have separate inputstates on the InterfacePanel level like we do with some Viewstates
    @Override
    public void parseKeyPress(KeyRole keyrole)
    {
        inputMap.getOrDefault(keyrole, AbstractFunction.NULL).execute();
    }

    @Override
    public void parseKeyRelease(KeyRole keyrole)
    {
        if(keyrole == KeyRole.ACTION) buttons.get(currentIndex).release();
    }
}
