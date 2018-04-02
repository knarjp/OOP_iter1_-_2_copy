package com.MeanTeam.gameview.viewstates;

import com.MeanTeam.gamecontroller.HostileNpcController;
import com.MeanTeam.gamemodel.notifiers.DialogNotifier;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.GameViewNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.Decal;
import com.MeanTeam.gamemodel.tile.Obstacle;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.areaeffects.AreaEffect;
import com.MeanTeam.gamemodel.tile.effectcommand.*;
import com.MeanTeam.gamemodel.tile.entities.EntityType;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.skills.SkillPool;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.gameview.displayables.*;
import com.MeanTeam.gameview.viewstates.menus.StartMenuViewstate;
import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.control.KeyRole;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.util.Path;
import com.MeanTeam.util.Specialty;
import com.MeanTeam.visitors.LoadingParser;
import com.MeanTeam.gamemodel.tile.areaeffects.InfiniteAreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.OneShotAreaEffect;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamecontroller.EntityController;
import com.MeanTeam.gamemodel.*;
import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.visitors.SavingVisitor;
import com.MeanTeam.util.AbstractFunction;
import com.MeanTeam.visitors.Visitor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.List;

public class GameViewState extends Viewstate
{
    private Dimension size;
    private InterfacePanel interfacePanel;

    private Set<Displayable> tileDisplayables;
    private Set<Displayable> overlays;

    private CompositeDisplayable worldDisplayable;

    private InventoryDisplayable inventoryDisplayable;

    private GameModel gameModel;
    private GameModelNotifier gameModelNotifier;

    private BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
    private ImageDisplayable menuView;

    private EntityController playerController;

    private int frameCounter = 0;
    private int frameThreshold = 10;

    private MovementState movementState;
    private InventoryState inventoryState;
    private MenuState   menuState;
    private ShopState shopState;

    private InputState inputState;

    private Font font = new Font("Garamond", Font.CENTER_BASELINE, 32);

    public GameViewState(Dimension initialSize, InterfacePanel interfacePanel)
    {
        super();

        this.interfacePanel = interfacePanel;
        Image backdrop = new ImageIcon("assets/mainMenu.jpg").getImage();
        Graphics2D img = image.createGraphics();

        img.drawImage(backdrop, 0, 0,null);
        img.setColor(new Color(0x5E0059));
        img.fillRect(500, 180, 300, 60);
        img.fillRect(500, 280, 300, 60);
        img.fillRect(490, 380, 400, 60);
        img.setColor(Color.ORANGE);
        img.setFont(font.deriveFont(Font.BOLD));
        img.drawString("Save Game", 550, 200);
        img.drawString("Quit Game", 550, 300);
        img.drawString("Return to Game", 550, 400);

        menuView = new ImageDisplayable(new Point(0, 0), image);

        this.size = initialSize;

        List<World> worldList = new ArrayList<>();
        List<WorldNotifier> worldNotifierList = new ArrayList<>();

        // Begin making hard-coded example worlds:
        World startingWorld = new World();
        WorldNotifier startingWorldNotifier = new WorldNotifier(startingWorld);
        worldList.add(startingWorld);

        gameModel = new GameModel(worldList, worldNotifierList, new ArrayList<>(), new ArrayList<>(),
                startingWorld, null, 0);
        gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);

        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), startingWorldNotifier, null, EntityType.values()[ModelDisplayableFactory.getEntityIndex()]);
        player.setNoiseLevel(3);
        player.setMovementSpeed(500000000l);

        player.addSkill(SkillPool.BIND_WOUNDS, 1);
        player.addSkill(SkillPool.BARGAIN, 1);
        player.addSkill(SkillPool.OBSERVE, 1);

        this.playerController = new EntityController(player);

        addSpecialitySpecificSkills(player, playerController.getSpecialty(), startingWorldNotifier,
                                    gameModelNotifier, gameModel);


        gameModel.initialize(0, player);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);

        startingWorld.addPlayer(new Point(0, 0), player);
        worldNotifierList.add(startingWorldNotifier);

        /*populateStartingWorld(startingWorld, gameModel);

        worldList.add(generateSecondWorld());

        worldList.add(generateLockedWorld());*/

        populateDemoWorld(startingWorld, startingWorldNotifier, gameModel);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);
        generateSecondWorld(worldNotifierList, worldList);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);
        generateLocalZone1(worldNotifierList, worldList);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);
        generateLocalZone2(worldNotifierList, worldList);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);
        generateLockedLocalZone(worldNotifierList, worldList);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);
        generateLargeLocalZone(worldNotifierList, worldList);

        // End making example worlds

        /*
        keyReleaseMap.put(KeyEvent.VK_NUMPAD4, playerController::releaseW);
        keyReleaseMap.put(KeyEvent.VK_NUMPAD8, playerController::releaseN);
        keyReleaseMap.put(KeyEvent.VK_NUMPAD6, playerController::releaseE);
        keyReleaseMap.put(KeyEvent.VK_NUMPAD2, playerController::releaseS);

        keyReleaseMap.put(KeyEvent.VK_A, playerController::releaseW);
        keyReleaseMap.put(KeyEvent.VK_W, playerController::releaseN);
        keyReleaseMap.put(KeyEvent.VK_D, playerController::releaseE);
        keyReleaseMap.put(KeyEvent.VK_S, playerController::releaseS);
        */

        this.overlays = new HashSet<>();
        overlays.add(new EntityStatusDisplayable(new Point(16, 16), gameModel.getPlayer()));
        overlays.add(inventoryDisplayable = new InventoryDisplayable(new Point(16, 160), gameModel.getPlayer()));
        overlays.add(new SkillSelectorDisplayable(new Point(384, 16), player));

        DialogDisplayable dialogDisplayable = new DialogDisplayable(new Point(20, 619));
        overlays.add(dialogDisplayable);
        DialogNotifier.setDisplayable(dialogDisplayable);

        GameViewNotifier.setGameview(this);

        this.tileDisplayables = new HashSet<>();
        worldDisplayable = new CompositeDisplayable(new Point(0, 0));
        tileDisplayables.add(worldDisplayable);

        // Populate tileDisplayables:

        changeSize(initialSize);

        super.add(tileDisplayables);
        super.add(overlays);

        // Initialize InputStates:

        inputState = movementState = new MovementState();
        inventoryState = new InventoryState();
        menuState = new MenuState();
    }

    private void addSpecialitySpecificSkills(Entity entity, Specialty specialty, WorldNotifier startingWorldNotifier,
                                             GameModelNotifier gameModelNotifier, GameModel gameModel) {
        switch(specialty) {
            case DEFENDER:
                entity.addWeaponSkill(SkillPool.BRAWLING, 1);
                entity.addWeaponSkill(SkillPool.ONE_HANDED, 1);
                entity.addWeaponSkill(SkillPool.TWO_HANDED, 1);

                WeaponItem brawling = new WeaponItem("Brass_Knuckles", ItemType.DEFAULT, startingWorldNotifier,
                        0, 5, 10, 0, 1,
                        new DamageEvent(0), SkillPool.BRAWLING, gameModelNotifier);
                entity.addItemToBackpack(brawling);

                WeaponItem onehandedweapon = new WeaponItem("Tail_Barbs", ItemType.TAIL_BARBS, startingWorldNotifier,
                        0, 5, 10, 0, 1,
                        new DamageEvent(0), SkillPool.ONE_HANDED, gameModelNotifier);
                entity.addItemToBackpack(onehandedweapon);

                WeaponItem twohandedweapon = new WeaponItem("Body_Spike", ItemType.BODY_SPIKE, startingWorldNotifier,
                        2000000000l, 5, 90, 90, 1,
                        new DamageEvent(0), SkillPool.TWO_HANDED, gameModelNotifier);
                entity.addItemToBackpack(twohandedweapon);

                break;
            case WISE_ONE:
                entity.addWeaponSkill(SkillPool.STAFF, 1);
                entity.addWeaponSkill(SkillPool.ENCHANTMENT, 1);
                entity.addWeaponSkill(SkillPool.BANE, 1);
                entity.addWeaponSkill(SkillPool.BOON, 1);

                /*WeaponItem staff = new WeaponItem("Apprentice_Staff", ItemType.DEFAULT, startingWorldNotifier,
                        0, 25, 30, 0, 3,
                        new SlowdownEvent(0, gameModel), SkillPool.STAFF, gameModelNotifier);
                entity.addItemToBackpack(staff);*/

                break;
            case SCOUT:
                entity.addSkill(new Skill("Thieve", 1, 0,
                                50, 10, 5, 0,
                                0,
                                new PickPocketEvent(0, GameModelNotifier.getGameModelNotifier(gameModel))),
                                1);
                entity.addSkill(new Skill("Sense Danger", 0, 0,
                                  50, 10, 5, 4,
                                0,
                                new DisarmTrapEvent(0, GameModelNotifier.getGameModelNotifier(gameModel))),
                                1);
                entity.addSkill(new Skill("Stalk", 0, 0,
                                100, 0, 0, 0,
                                0,
                                new CreepEvent(0, GameModelNotifier.getGameModelNotifier(gameModel))),
                                1);
                entity.addSkill(SkillPool.BACKSTAB, 1);
                entity.addWeaponSkill(SkillPool.RANGED, 1);
                break;
        }
    }

    private void reinitialize()
    {
        this.overlays.clear();
        this.tileDisplayables.clear();
        overlays.add(new EntityStatusDisplayable(new Point(16, 16), gameModel.getPlayer()));
        overlays.add(inventoryDisplayable = new InventoryDisplayable(new Point(16, 160), gameModel.getPlayer()));

        worldDisplayable = new CompositeDisplayable(new Point(0, 0));
        tileDisplayables.add(worldDisplayable);

        changeSize(this.size);

        this.playerController = new EntityController(gameModel.getPlayer());
        inputState = movementState = new MovementState();
        inventoryState = new InventoryState();
    }

    private void populateStartingWorld(World startingWorld, WorldNotifier startingWorldNotifier, GameModel gameModel){
        for(int i = -3; i <= 3; i++)
        {
            for(int j = -3; j <= 3; j++)
            {
                startingWorld.add(new Point(i, j), Terrain.GRASS);
            }
        }

        for(int i = -4; i <= 4; i++)
        {
            startingWorld.add(new Point(i, -4), Terrain.WATER);
            startingWorld.add(new Point(i, 4), Terrain.WATER);
            startingWorld.add(new Point(-4, i), Terrain.WATER);
            startingWorld.add(new Point(4, i), Terrain.WATER);
        }

        startingWorld.add(new Point(-1, 0), new InfiniteAreaEffect(new DamageEvent(10)));
        startingWorld.add(new Point(-1, 0), Decal.SKULL);
        startingWorld.add(new Point(3, 3), Decal.STAR);
        startingWorld.add(new Point(3, 3), new OneShotAreaEffect(new LevelUpEvent()));


        startingWorld.add(new Point(2, -1), new InfiniteAreaEffect(new HealEvent(10)));
        startingWorld.add(new Point(2, -1), Decal.CROSS);

        startingWorld.add(new Point(-1, -2), new Obstacle(1));
        startingWorld.add(new Point(-1, -1), Terrain.MOUNTAINS);

    //    startingWorld.add(new Point(-2, 2), new TakeableItem("Armor", EventCommand.NULL, ItemType.DEFAULT, true,

        startingWorld.add(new Point(-2, 2), new ArmorItem("Armor", new ToggleableHealthEvent(40), ItemType.CHESTPLATE, startingWorldNotifier, gameModelNotifier));

        TakeableItem key = new TakeableItem("Key", ItemType.KEY, startingWorldNotifier, gameModelNotifier);
        startingWorld.add(new Point(-1, 3), key);

        startingWorld.add(new Point(-3, 3), new InteractiveItem(
                new ConditionalTransitionEvent(2, new Point(1, 1), gameModelNotifier, key), ItemType.DOOR, startingWorldNotifier));
        startingWorld.add(new Point(-2, -1), new InteractiveItem(new TransitionEvent(1, new Point(0, 0), gameModelNotifier), ItemType.OPEN_DOOR, startingWorldNotifier));
        startingWorld.add(new Point(0, -1), new InteractiveItem(new TransitionEvent(1, new Point(7, 0), gameModelNotifier), ItemType.OPEN_DOOR, startingWorldNotifier));
    }

    private void generateSecondWorld(List<WorldNotifier> worldNotifierList, List<World> worldList){
        World secondWorld = new World();
        WorldNotifier secondWorldNotifier = new WorldNotifier(secondWorld);
        worldNotifierList.add(secondWorldNotifier);
        worldList.add(secondWorld);

        for(int i = -1; i <= 8; i++)
        {
            secondWorld.add(new Point(i, 1), Terrain.MOUNTAINS);
            secondWorld.add(new Point(i, 0), Terrain.GRASS);
            secondWorld.add(new Point(i, -1), Terrain.MOUNTAINS);
        }

        secondWorld.add(new Point(-1, 0), new InteractiveItem(new TransitionEvent(0, new Point(-3, -1), gameModelNotifier), ItemType.OPEN_DOOR, secondWorldNotifier));
        secondWorld.add(new Point(8, 0 ), new InteractiveItem(new TransitionEvent(0, new Point(1, -1), gameModelNotifier), ItemType.OPEN_DOOR, secondWorldNotifier));
    }

    private World generateLockedWorld(List<WorldNotifier> worldNotifierList){
        World lockedWorld = new World();
        WorldNotifier lockedWorldNotifier = new WorldNotifier(lockedWorld);
        worldNotifierList.add(lockedWorldNotifier);

        for(int i = 1; i < 6; i++)
        {
            for(int j = 1; j < 6; j++)
            {
                lockedWorld.add(new Point(i, j), Terrain.GRASS);
            }
        }
        for(int i = 0; i < 7; ++i){
            lockedWorld.add(new Point(0, i), Terrain.MOUNTAINS);
            lockedWorld.add(new Point(6, i), Terrain.MOUNTAINS);
            lockedWorld.add(new Point(i, 0), Terrain.MOUNTAINS);
            lockedWorld.add(new Point(i, 6), Terrain.MOUNTAINS);
        }

        lockedWorld.add(new Point(4, 4 ), new InteractiveItem(
                new TransitionEvent(0, new Point(-1, 3), gameModelNotifier), ItemType.OPEN_DOOR, lockedWorldNotifier));
        return lockedWorld;
    }

    private void populateDemoWorld(World world, WorldNotifier worldNotifier, GameModel gameModel){
        for(int i = -3; i <= 3; i++)
        {
            for(int j = -3; j <= 3; j++)
            {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        for(int i = -4; i <= 4; i++)
        {
            world.add(new Point(i, -4), Terrain.WATER);
            world.add(new Point(i, 4), Terrain.WATER);
            world.add(new Point(-4, i), Terrain.WATER);
            world.add(new Point(4, i), Terrain.WATER);
        }

        world.add(new Point(-1, -1), Terrain.MOUNTAINS);

        world.add(new Point(-2, -1), new InteractiveItem(new TransitionEvent(1, new Point(0, 0), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));
        world.add(new Point(0, -1), new InteractiveItem(new TransitionEvent(1, new Point(7, 0), gameModelNotifier), ItemType.OPEN_DOOR,worldNotifier));

        world.add(new Point(3, -1), new InteractiveItem(new TransitionEvent(2, new Point(-2, 0), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));
        world.add(new Point(3, 1), new InteractiveItem(new TransitionEvent(3, new Point(-2, 0), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));

        world.add(new Point(-3, 3), new InteractiveItem(new TransitionEvent(5, new Point(1, 0), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));
    }

    private void generateLocalZone1(List<WorldNotifier> worldNotifierList, List<World> worldList){
        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        worldNotifierList.add(worldNotifier);
        worldList.add(world);

        for(int i = -3; i <= 3; i++)
        {
            for(int j = -3; j <= 3; j++)
            {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        for(int i = -4; i <= 4; i++)
        {
            world.add(new Point(i, -4), Terrain.MOUNTAINS);
            world.add(new Point(i, 4), Terrain.MOUNTAINS);
            world.add(new Point(-4, i), Terrain.MOUNTAINS);
            world.add(new Point(4, i), Terrain.MOUNTAINS);
        }

        world.add(new Point(-2, 2), new ArmorItem("Armor", new ToggleableHealthEvent(45), ItemType.CHESTPLATE, worldNotifier, gameModelNotifier));
        world.add(new Point(0, 2), new OneshotItem(new DamageEvent(25), ItemType.DAMAGE_POTION, worldNotifier));
        world.add(new Point(2, 2), new OneshotItem(new HealEvent(10), ItemType.HEALTH_POTION, worldNotifier));
        world.add(new Point(-2, -2), new Obstacle(1));

        world.add(new Point(0, -3), new Trap(new DamageEvent(22), 5));

        TakeableItem key = new TakeableItem("key", ItemType.KEY, worldNotifier, gameModelNotifier);
        world.add(new Point(0, 0), key);

        world.add(new Point (-2, -1), new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));

        world.add(new Point(-3, 0), new InteractiveItem(new TransitionEvent(0, new Point(2, -1), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));
        world.add(new Point(3, 0), new InteractiveItem(new ConditionalTransitionEvent(4, new Point(-3, 0), gameModelNotifier, key), ItemType.DOOR, worldNotifier));
    }

    private void generateLocalZone2(List<WorldNotifier> worldNotifierList, List<World> worldList){
        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        worldNotifierList.add(worldNotifier);
        worldList.add(world);

        for(int i = -3; i <= 3; i++)
        {
            for(int j = -3; j <= 3; j++)
            {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        for(int i = -4; i <= 4; i++)
        {
            world.add(new Point(i, -4), Terrain.MOUNTAINS);
            world.add(new Point(i, 4), Terrain.MOUNTAINS);
            world.add(new Point(-4, i), Terrain.MOUNTAINS);
            world.add(new Point(4, i), Terrain.MOUNTAINS);
        }

        InfiniteAreaEffect infiniteAreaEffect = new InfiniteAreaEffect(new DamageEvent(10));
        for(int i = -3; i < -1; ++i){
            for(int j = -3; j < -1; ++j) {
                world.add(new Point(i, j), infiniteAreaEffect);
                world.add(new Point(i, j), Decal.SKULL);
            }
        }

        infiniteAreaEffect = new InfiniteAreaEffect(new HealEvent(5));
        for(int i = -3; i < -1; ++i){
            for(int j = 2; j < 4; ++j) {
                world.add(new Point(i, j), infiniteAreaEffect);
                world.add(new Point(i, j), Decal.CROSS);
            }
        }

        OneShotAreaEffect oneshotAreaEffect = new OneShotAreaEffect(new LevelUpEvent());
        for(int i = 2; i < 4; ++i){
            for(int j = 2; j < 4; ++j) {
                world.add(new Point(i, j), oneshotAreaEffect);
                world.add(new Point(i, j), Decal.STAR);
            }
        }

        oneshotAreaEffect = new OneShotAreaEffect(new InstaDeathEvent());
        for(int i = 2; i < 4; ++i) {
            for(int j = -3; j < -1; ++j) {
                world.add(new Point(i, j), oneshotAreaEffect);
                world.add(new Point(i, j), Decal.SKULL);
            }
        }

        world.add(new Point(-3, 0), new InteractiveItem(new TransitionEvent(0, new Point(2, 1), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));
    }

    private void generateLockedLocalZone(List<WorldNotifier> worldNotifierList, List<World> worldList){
        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        worldNotifierList.add(worldNotifier);
        worldList.add(world);

        for(int i = -4; i <= 4; i++)
        {
            for(int j = 0; j <= 1; j++)
            {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        for(int i = -5; i <= 5; i++)
        {
            world.add(new Point(i, -1), Terrain.WATER);
            world.add(new Point(i, 2), Terrain.WATER);
        }

        for(int j = 0; j <= 1; j++)
        {
            world.add(new Point(-5, j), Terrain.WATER);
            world.add(new Point(5, j), Terrain.WATER);
        }

        world.add(new Point(-4, 0), new InteractiveItem(new TransitionEvent(2, new Point(2, 0), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));
    }

    private void generateLargeLocalZone(List<WorldNotifier> worldNotifierList, List<World> worldList) {
        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        worldNotifierList.add(worldNotifier);
        worldList.add(world);

        WeaponItem defWep = new WeaponItem("claws", ItemType.SWORD, worldNotifier, 0, 0, 0, 0, 1, new DamageEvent(10), SkillPool.damageRing, GameModelNotifier.getGameModelNotifier(gameModel));
        Entity monster = new Entity(100, 100, 2, 900000000l, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, defWep, EntityType.SHOPKEEPER);
        monster.setMovementSpeed(900000000l);
        monster.addWeaponSkill(SkillPool.damageRing, 1);
        monster.equipWeapon(defWep);
        monster.decreaseHealth(20);
        world.addNpc(new Point(8, 6), monster);
        gameModel.addFriendlyNpc(world, monster);

        generateBlockOfTerrainTiles(0,7,-3,4,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(3,5,-12,-4,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(-2,11,-18,-13,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(12,22,-17,-15,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(20,22,-14,-11,Terrain.GRASS,world);

        generateBlockOfTerrainTiles(17,18,-8,-7,Terrain.MOUNTAINS,world);
        generateBlockOfTerrainTiles(23,24,-6,-5,Terrain.MOUNTAINS,world);
        generateBlockOfTerrainTiles(26,27,2,3,Terrain.MOUNTAINS,world);
        generateBlockOfTerrainTiles(30,31,-8,-7,Terrain.MOUNTAINS,world);

        fillBlockOfRemainingEmptyTiles(15,35,-10,2,Terrain.GRASS,world);

        fillBlockOfRemainingEmptyTiles(22,35,3,3,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(23,35,4,4,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(24,35,5,5,Terrain.GRASS,world);

        generateBlockOfTerrainTiles(25,28,6,14,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(13,24,11,14,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(8,12,7,14,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(8,17,3,6,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(8,11,1,2,Terrain.GRASS,world);
        generateBlockOfTerrainTiles(8,9,-1,0,Terrain.GRASS,world);

        generateBlockOfTerrainTiles(1,2,12,13,Terrain.MOUNTAINS,world);
        generateBlockOfTerrainTiles(5,6,9,10,Terrain.MOUNTAINS,world);
        world.add(new Point(-2,11), Terrain.MOUNTAINS);
        world.add(new Point(1,9), Terrain.MOUNTAINS);
        world.add(new Point(5,13), Terrain.MOUNTAINS);

        fillBlockOfRemainingEmptyTiles(-3,7,8,14,Terrain.GRASS,world);
        fillBlockOfRemainingEmptyTiles(-5,37,-20,16,Terrain.WATER,world);

        List<Entity> hostiles = new ArrayList<>();
        for(int i = 0; i < 9; ++i) {
            WeaponItem defaultWeapon = new WeaponItem("", ItemType.SWORD, worldNotifier, 2000000000l, 0, 1, 0, 1, new DamageEvent(0), SkillPool.damageRing, GameModelNotifier.getGameModelNotifier(gameModel));
            hostiles.add(new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, defaultWeapon));
            hostiles.get(i).addWeaponSkill(SkillPool.damageRing, 1);
            hostiles.get(i).equipWeapon(defaultWeapon);
            hostiles.get(i).setMovementSpeed(900000000l);
            gameModel.addHostileNpc(world, hostiles.get(i));
        }

        Path squarePath = new Path();
        for(int i = 0; i < 3; ++i) {
            squarePath.addOrientation(Orientation.WEST);
        }
        for(int i = 0; i < 3; ++i) {
            squarePath.addOrientation(Orientation.SOUTH);
        }
        for(int i = 0; i < 3; ++i) {
            squarePath.addOrientation(Orientation.EAST);
        }
        for(int i = 0; i < 3; ++i) {
            squarePath.addOrientation(Orientation.NORTH);
        }

        world.addNpc(new Point(13, -16), hostiles.get(0));

        world.addNpc(new Point(19, -9), hostiles.get(1));
        gameModel.addHostileNpcPath(hostiles.get(1), world, new Path(squarePath));

        world.addNpc(new Point(25, -7), hostiles.get(2));
        gameModel.addHostileNpcPath(hostiles.get(2), world, new Path(squarePath));

        world.addNpc(new Point(32, -9), hostiles.get(3));
        gameModel.addHostileNpcPath(hostiles.get(3), world, new Path(squarePath));

        world.addNpc(new Point(31, -2), hostiles.get(4));
        gameModel.addHostileNpcPath(hostiles.get(4), world, new Path(squarePath));
        Path line1 = new Path();
        for(int i = 0; i < 14; ++i) {
            line1.addOrientation(Orientation.WEST);
        }
        for(int i = 0; i < 14; ++i) {
            line1.addOrientation(Orientation.EAST);
        }
        gameModel.addHostileNpcPath(hostiles.get(4), world, line1);

        world.addNpc(new Point(28, 1), hostiles.get(5));
        gameModel.addHostileNpcPath(hostiles.get(5), world, new Path(squarePath));

        world.addNpc(new Point(34, 3), hostiles.get(6));
        Path line2 = new Path();
        for(int i = 0; i < 11; ++i) {
            line2.addOrientation(Orientation.NORTH);
        }
        for(int i = 0; i < 11; ++i) {
            line2.addOrientation(Orientation.SOUTH);
        }
        gameModel.addHostileNpcPath(hostiles.get(6), world, line2);

        world.addNpc(new Point(28, 11), hostiles.get(7));
        gameModel.addHostileNpcPath(hostiles.get(7), world, new Path(squarePath));

        world.addNpc(new Point(16, 2), hostiles.get(8));

        world.add(new Point(0, 0), new InteractiveItem(new TransitionEvent(0, new Point(-2, 3), gameModelNotifier), ItemType.OPEN_DOOR, worldNotifier));

        addSmasherDemo(world, worldNotifier);

        addSummonerDemo(world, worldNotifier);

        addSneakDemo(world, worldNotifier);

        addTeleportEffects(world, worldNotifier);

        AreaEffect effect = new InfiniteAreaEffect(new DamageEvent(5));
        world.add(new Point(4,4), effect);
        Decal decal = Decal.SKULL;
        world.add(new Point(4,4), decal);

        for(int i = 13; i <= 17; ++i) {
            for(int j = 5; j <= 6; ++j) {
                OneShotAreaEffect levelup = new OneShotAreaEffect(new LevelUpEvent());
                world.add(new Point(i, j), levelup);
                world.add(new Point(i, j), Decal.STAR);
            }
        }

        ArmorItem armor = new ArmorItem("Armor", new ToggleableHealthEvent(50), ItemType.DEFAULT,
                worldNotifier,gameModelNotifier);
        world.add(new Point(10, 1), armor);

        RingItem ring = new RingItem("Ring", new ToggleableHealthEvent(50), ItemType.DEFAULT,
                worldNotifier,gameModelNotifier);
        world.add(new Point(11, 1), ring);

        Obstacle obstacle = new Obstacle(0);
        world.add(new Point(15,13), obstacle);
        world.add(new Point(18,0), obstacle);
        world.add(new Point(1,-2), obstacle);
        world.add(new Point(25,-3), obstacle);

                 /*WeaponItem confuseWeapon = new WeaponItem("Confuse_Scroll", ItemType.DEFAULT, worldNotifier,
                0, 25, 30, 0, 3,
                new ConfuseEvent(0, gameModel), SkillPool.ENCHANTMENT, gameModelNotifier);
        world.add(new Point(6, -2), confuseWeapon);

        Entity friendly1 = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly1.setMovementSpeed(900000000l);
        world.addNpc(new Point(7, -2), friendly1);
        gameModel.addFriendlyNpc(world, friendly1);*/
    }

    private void addSmasherDemo(World world, WorldNotifier worldNotifier){

        /*
        =========================
                SMASHER
        =========================
         */
        //brawling
        Entity friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -3), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        WeaponItem item = new WeaponItem("Spiked_Gloves", ItemType.PORCUPINE_TAIL, worldNotifier,
                0, 6, 10, 0, 1,
                new ConfuseEvent(0, gameModel), SkillPool.BRAWLING, gameModelNotifier);
        world.add(new Point(3, -4), item);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -4), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        item = new WeaponItem("Super_Brawler", ItemType.BODY_SPIKE, worldNotifier,
                0, 10, 12, 0, 1,
                new ConfuseEvent(0, gameModel), SkillPool.BRAWLING, gameModelNotifier);
        world.add(new Point(3, -5), item);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -5), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        //one-handed
        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -6), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        item = new WeaponItem("Poison_Tail", ItemType.POISON_TAIL, worldNotifier,
                500000000l, 20, 25, 0, 1,
                new ConfuseEvent(0, gameModel), SkillPool.ONE_HANDED, gameModelNotifier);
        world.add(new Point(3, -7), item);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -7), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        item = new WeaponItem("Metal_Tail", ItemType.METAL_TAIL, worldNotifier,
                500000000l, 22, 30, 0, 1,
                new ConfuseEvent(0, gameModel), SkillPool.ONE_HANDED, gameModelNotifier);
        world.add(new Point(3, -8), item);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -8), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        //two-handed
        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -9), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -10), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        item = new WeaponItem("Body_Blade", ItemType.BODY_BLADE, worldNotifier,
                1000000000l, 35, 45, 0, 1,
                new ConfuseEvent(0, gameModel), SkillPool.TWO_HANDED, gameModelNotifier);
        world.add(new Point(3, -11), item);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -11), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -12), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        item = new WeaponItem("Body_Pepper_Pelt", ItemType.BODY_PEPPER_PELT, worldNotifier,
                1000000000l, 45, 55, 0, 1,
                new ConfuseEvent(0, gameModel), SkillPool.TWO_HANDED, gameModelNotifier);
        world.add(new Point(3, -13), item);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -13), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(5, -14), friendly);
        friendly.addItemToBackpack(new ConsumableItem(("health potion"), new HealEvent(15), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier));
        gameModel.addFriendlyNpc(world, friendly);
    }

    private void addSummonerDemo(World world, WorldNotifier worldNotifier) {
        WeaponItem item = new WeaponItem("Confuse", ItemType.BLUE_SCROLL, worldNotifier,
                500000000l, 20, 20, 0, 3,
                new ConfuseEvent(0, gameModel), SkillPool.ENCHANTMENT, gameModelNotifier);
        world.add(new Point(0, 2), item);

        item = new WeaponItem("Weak_Slow", ItemType.BLUE_SCROLL, worldNotifier,
                500000000l, 15, 30, 0, 3,
                new SlowdownEvent(0, gameModel), SkillPool.ENCHANTMENT, gameModelNotifier);
        world.add(new Point(1, 2), item);

        item = new WeaponItem("Strong_Slow", ItemType.BLUE_SCROLL, worldNotifier,
                700000000l, 50, 100, 0, 3,
                new SlowdownEvent(0, gameModel), SkillPool.ENCHANTMENT, gameModelNotifier);
        world.add(new Point(2, 2), item);

        item = new WeaponItem("Super_Purr", ItemType.GREEN_SCROLL, worldNotifier,
                500000000l, 60, 50, 0, 3,
                new HealEvent(0), SkillPool.BOON, gameModelNotifier);
        world.add(new Point(0, 4), item);

        item = new WeaponItem("Chuff", ItemType.GREEN_SCROLL, worldNotifier,
                100000000l, 15, 10, 0, 3,
                new HealEvent(0), SkillPool.BOON, gameModelNotifier);
        world.add(new Point(1, 4), item);

        item = new WeaponItem("Super_Purr", ItemType.GREEN_SCROLL, worldNotifier,
                1000000000l, 20, -40, 0, 3,
                new StaminaDecreaseEvent(0), SkillPool.BOON, gameModelNotifier);
        world.add(new Point(2, 4), item);

        item = new WeaponItem("Firestorm", ItemType.ORANGE_SCROLL, worldNotifier,
                800000000l, 70, 40, 360, 4,
                new DamageEvent(0), SkillPool.BANE, gameModelNotifier);
        world.add(new Point(0, 3), item);

        item = new WeaponItem("Lightning", ItemType.ORANGE_SCROLL, worldNotifier,
                1000000000l, 60, 60, 0, 6,
                new DamageEvent(0), SkillPool.BANE, gameModelNotifier);
        world.add(new Point(1, 3), item);

        item = new WeaponItem("Fireblast", ItemType.ORANGE_SCROLL, worldNotifier,
                500000000l, 35, 30, 90, 3,
                new DamageEvent(0), SkillPool.BANE, gameModelNotifier);
        world.add(new Point(2, 3), item);

        item = new WeaponItem("Broom", ItemType.BROOM, worldNotifier,
                100000000l, 10, 10, 0, 1,
                new DamageEvent(0), SkillPool.STAFF, gameModelNotifier);
        world.add(new Point(0, 1), item);

        item = new WeaponItem("Ice_Staff", ItemType.STAFF1, worldNotifier,
                100000000l, 10, 5, 0, 1,
                new DamageEvent(0), SkillPool.STAFF, gameModelNotifier);
        world.add(new Point(1, 1), item);

        item = new WeaponItem("Tree_Branch", ItemType.WOOD_STAFF, worldNotifier,
                100000000l, 10, 20,0, 1,
                new DamageEvent(0), SkillPool.STAFF, gameModelNotifier);
        world.add(new Point(2, 1), item);

        //entities for radial effect
        Entity friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(7, 9), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(9, 9), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(11, 9), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(7, 11), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(11, 11), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(7, 13), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(9, 13), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
        friendly.setMovementSpeed(900000000l);
        world.addNpc(new Point(11, 13), friendly);
        gameModel.addFriendlyNpc(world, friendly);

        //entites for angular effect
        for(int i = 9; i <= 13; ++i){
            friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
            friendly.setMovementSpeed(900000000l);
            world.addNpc(new Point(-1, i), friendly);
            gameModel.addFriendlyNpc(world, friendly);
        }

    }

    private void addSneakDemo(World world, WorldNotifier worldNotifier) {
        WeaponItem item = new WeaponItem("Rocks", ItemType.ROCKS, worldNotifier,
                500000000l, 20, 20, 0, 5,
                new DamageEvent(0), SkillPool.RANGED, gameModelNotifier);
        world.add(new Point(-2, -18), item);

        item = new WeaponItem("Porcupine_Tail", ItemType.PORCUPINE_TAIL, worldNotifier,
                1000000000l, 20, 40, 0, 5,
                new DamageEvent(0), SkillPool.RANGED, gameModelNotifier);
        world.add(new Point(-1, -18), item);

        item = new WeaponItem("Cobra_Bow", ItemType.COBRA_BOW, worldNotifier,
                2000000000l, 20, 60, 0, 5,
                new DamageEvent(0), SkillPool.RANGED, gameModelNotifier);
        world.add(new Point(0, -18), item);

        for(int i = 4; i <= 6; ++i) {
            Entity friendly = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), worldNotifier, null);
            friendly.setMovementSpeed(900000000l);
            world.addNpc(new Point(i, -18), friendly);
            gameModel.addFriendlyNpc(world, friendly);
        }

        for(int i = 13; i <= 21; ++i) {
            Trap trap = new Trap(new DamageEvent(10), 5);
            world.add(new Point(i, 14), trap);
        }
    }

    private void addTeleportEffects(World world, WorldNotifier worldNotifier) {
        //teleport between main and summoner demonstration
        AreaEffect areaEffect = new InfiniteAreaEffect(new TransitionEvent(5, new Point(-3, 10), gameModelNotifier));
        world.add(new Point(5, 4), areaEffect);
        world.add(new Point(5, 4), Decal.STAR);

        areaEffect = new InfiniteAreaEffect(new TransitionEvent(5, new Point(5, 3), gameModelNotifier));
        world.add(new Point(-3, 11), areaEffect);
        world.add(new Point(-3, 11), Decal.STAR);

        //teleport between main and patrol area
        areaEffect = new InfiniteAreaEffect(new TransitionEvent(5, new Point(20, 1), gameModelNotifier));
        world.add(new Point(9, -1), areaEffect);
        world.add(new Point(9, -1), Decal.STAR);

        areaEffect = new InfiniteAreaEffect(new TransitionEvent(5, new Point(8, -1), gameModelNotifier));
        world.add(new Point(20, 2), areaEffect);
        world.add(new Point(20, 2), Decal.STAR);

        //teleport between main and sneak demonstration
        areaEffect = new InfiniteAreaEffect(new TransitionEvent(5, new Point(0, -2), gameModelNotifier));
        world.add(new Point(-1, -16), areaEffect);
        world.add(new Point(-1, -16), Decal.STAR);

        areaEffect = new InfiniteAreaEffect(new TransitionEvent(5, new Point(-1, -15), gameModelNotifier));
        world.add(new Point(0, -3), areaEffect);
        world.add(new Point(0, -3), Decal.STAR);


    }

    private void generateBlockOfTerrainTiles(int xMin, int xMax, int yMin, int yMax, Terrain terrain, World world) {
        for(int x = xMin; x <= xMax; ++x) {
            for(int y = yMin; y <= yMax; ++y) {
                world.add(new Point(x, y), terrain);
            }
        }
    }

    private void fillBlockOfRemainingEmptyTiles(int xMin, int xMax, int yMin, int yMax, Terrain terrain, World world) {
        for(int x = xMin; x <= xMax; ++x) {
            for(int y = yMin; y <= yMax; ++y) {
                if (world.getTerrain(new Point(x, y)) == Terrain.NONE) {
                    world.add(new Point(x, y), terrain);
                }
            }
        }
    }

    @Override
    public void changeSize(Dimension size)
    {
        super.changeSize(size);

        this.size = size;

        int tileWidth = size.width / 64;
        int tileHeight = size.height / 64;

        if(tileWidth % 2 == 0) tileWidth--;
        if(tileHeight % 2 == 0) tileHeight--;

        Point midPoint = new Point(tileWidth / 2, tileHeight / 2);

        worldDisplayable.clear();

        for(int i = -midPoint.x, xIndex = 0; i <= midPoint.x && xIndex <= tileWidth; i++, xIndex++)
        {
            for(int j = -midPoint.y, yIndex = 0; j <= midPoint.y && yIndex <= tileHeight; j++, yIndex++)
            {
                worldDisplayable.add(new TileDisplayable(new Point(xIndex * 64, yIndex * 64), new Point(i, j), gameModel));
            }
        }

        Point centerPt = new Point(size.width / 2, size.height / 2);
        Point worldCenterPt = new Point(centerPt.x - worldDisplayable.getSize().width / 2, centerPt.y - worldDisplayable.getSize().height / 2);

        worldDisplayable.getOrigin().setLocation(worldCenterPt);
    }

    @Override
    public void update()
    {
        if(++frameCounter >= frameThreshold)
        {
            if(gameModel != null)
            {
                gameModel.advance();
                frameCounter = 0;
            }
        }
        super.update();
    }

    private void saveGame() {
        SavingVisitor visitor = new SavingVisitor();
        gameModel.accept(visitor);
        String string = visitor.getString();
        String keybindString = saveKeyBindStrings();
        System.out.println(string);
        try {
            File file = new File("savegame.txt");
            FileWriter writer = new FileWriter(file);
            writer.write(string);
            writer.flush();
            writer.close();

            File file2 = new File("keybinds.txt");
            FileWriter writer2 = new FileWriter(file2);
            writer2.write(keybindString);
            writer2.flush();
            writer2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveKeyBindStrings() {
        Map<Integer, KeyRole> currentBinds = interfacePanel.getKeybinds();

        String string = "KEYBINDS:\n";

        for(Integer bindIndex : currentBinds.keySet()) {
            string += bindIndex.toString() + "," + currentBinds.get(bindIndex).toString() + "\n";
        }

        string += "END_KEYBINDS";

        return string;
    }

    @Override
    public void parseKeyPress(KeyRole keyCode)
    {
        inputState.parseKeyPress(keyCode);
    }

    @Override
    public void parseKeyRelease(KeyRole keyCode)
    {
        inputState.parseKeyRelease(keyCode);
    }

    public void loadGame()
    {
        loadKeyBinds();

       // gameModel = LoadingParser.loadGame();

    //    reinitialize();
    }

    private void loadKeyBinds() {
        Map<Integer, KeyRole> newBinds = new HashMap<>();

        File file = new File("keybinds.txt");

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            line = bufferedReader.readLine();

            while(!line.equals("END_KEYBINDS")) {
                String[] integer_keyrole = line.split(",");

                Integer integer = Integer.parseInt(integer_keyrole[0]);

                switch(integer_keyrole[1]) {
                    case "ZERO":
                        newBinds.put(integer, KeyRole.ZERO);
                        break;
                    case "ONE":
                        newBinds.put(integer, KeyRole.ONE);
                        break;
                    case "TWO":
                        newBinds.put(integer, KeyRole.TWO);
                        break;
                    case "THREE":
                        newBinds.put(integer, KeyRole.THREE);
                        break;
                    case "FOUR":
                        newBinds.put(integer, KeyRole.FOUR);
                        break;
                    case "FIVE":
                        newBinds.put(integer, KeyRole.FIVE);
                        break;
                    case "SIX":
                        newBinds.put(integer, KeyRole.SIX);
                        break;
                    case "SEVEN":
                        newBinds.put(integer, KeyRole.SEVEN);
                        break;
                    case "N":
                        newBinds.put(integer, KeyRole.N);
                        break;
                    case "NE":
                        newBinds.put(integer, KeyRole.NE);
                        break;
                    case "E":
                        newBinds.put(integer, KeyRole.E);
                        break;
                    case "SE":
                        newBinds.put(integer, KeyRole.SE);
                        break;
                    case "S":
                        newBinds.put(integer, KeyRole.S);
                        break;
                    case "SW":
                        newBinds.put(integer, KeyRole.SW);
                        break;
                    case "W":
                        newBinds.put(integer, KeyRole.W);
                        break;
                    case "NW":
                        newBinds.put(integer, KeyRole.NW);
                        break;
                    case "ACTION":
                        newBinds.put(integer, KeyRole.ACTION);
                        break;
                    case "USE":
                        newBinds.put(integer, KeyRole.USE);
                        break;
                    case "INVENTORY":
                        newBinds.put(integer, KeyRole.INVENTORY);
                        break;
                    case "BACK":
                        newBinds.put(integer, KeyRole.BACK);
                        break;
                    case "SAVE":
                        newBinds.put(integer, KeyRole.SAVE);
                        break;
                    case "LOAD":
                        newBinds.put(integer, KeyRole.LOAD);
                        break;
                    default: // NONE
                        newBinds.put(integer, KeyRole.NONE);
                }

                line = bufferedReader.readLine();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        interfacePanel.setKeybinds(newBinds);
    }

    public void setMovementState()
    {
        inputState = movementState;
        inventoryDisplayable.setSelectorIndex(-1);
    }

    public void setInventoryState()
    {
        inputState = inventoryState;
        inventoryDisplayable.setSelectorIndex(0);
    }

    public void setMenuState()
    {
        interfacePanel.setActiveState(new StartMenuViewstate(interfacePanel));
        GameModelNotifier.destroyInstance();
        // in-game menu:
        //inputState = menuState;
        //this.overlays.add(menuView);
    }

    public void setShopState(Entity shopkeeper)
    {
        inputState = new ShopState(shopkeeper);
    }

    public void setInteractState(Entity other)
    {
        if(other != gameModel.getPlayer() && gameModel.isNpcFriendly(other))
            inputState = new InteractState(other);
    }

    public void setLevelUpState(Entity entity, int skillPoints)
    {
        if(entity == gameModel.getPlayer())
        {
            inputState = new LevelUpState(skillPoints);
        }
    }

    public void setUseItemState(Entity target)
    {
        inputState = new UseItemState(target);
    }

    private interface InputState
    {
        void parseKeyPress(KeyRole keyCode);
        void parseKeyRelease(KeyRole keyCode);
    }

    private class MovementState implements InputState
    {
        private Map<KeyRole, AbstractFunction> keyPressMap = new HashMap<>();
        private Map<KeyRole, AbstractFunction> keyReleaseMap = new HashMap<>();

        public MovementState()
        {
            // Initialize keybindings:
            keyPressMap.put(KeyRole.NW, playerController::pressNorthWest);
            keyPressMap.put(KeyRole.NE, playerController::pressNorthEast);
            keyPressMap.put(KeyRole.SW, playerController::pressSouthWest);
            keyPressMap.put(KeyRole.SE, playerController::pressSouthEast);

            keyPressMap.put(KeyRole.W, playerController::pressWest);
            keyPressMap.put(KeyRole.N, playerController::pressNorth);
            keyPressMap.put(KeyRole.E, playerController::pressEast);
            keyPressMap.put(KeyRole.S, playerController::pressSouth);

            keyPressMap.put(KeyRole.INVENTORY, GameViewState.this::setInventoryState);

            keyPressMap.put(KeyRole.SAVE, GameViewState.this::saveGame);
            keyPressMap.put(KeyRole.LOAD, GameViewState.this::loadGame);

            keyPressMap.put(KeyRole.BACK, GameViewState.this::setMenuState);

            keyPressMap.put(KeyRole.ZERO, () -> playerController.setActiveSkill(0) );
            keyPressMap.put(KeyRole.ONE, () -> playerController.setActiveSkill(1) );
            keyPressMap.put(KeyRole.TWO, () -> playerController.setActiveSkill(2) );
            keyPressMap.put(KeyRole.THREE, () -> playerController.setActiveSkill(3) );
            keyPressMap.put(KeyRole.FOUR, () -> playerController.setActiveSkill(4) );
            keyPressMap.put(KeyRole.FIVE, () -> playerController.setActiveSkill(5) );
            keyPressMap.put(KeyRole.SIX, () -> playerController.setActiveSkill(6) );
            keyPressMap.put(KeyRole.SEVEN, () -> playerController.setActiveSkill(7) );

            keyPressMap.put(KeyRole.ACTION, playerController::useActiveSkill);
            keyPressMap.put(KeyRole.USE, playerController::useWeaponAttack);
        }

        public void parseKeyPress(KeyRole keyCode)
        {
            keyPressMap.getOrDefault(keyCode, AbstractFunction.NULL).execute();
        }

        public void parseKeyRelease(KeyRole keyCode)
        {
            keyReleaseMap.getOrDefault(keyCode, AbstractFunction.NULL).execute();
        }
    }

    private class InventoryState implements InputState
    {
        private Map<KeyRole, AbstractFunction> keyPressMap = new HashMap<>();

        private int index = 0;

        public InventoryState()
        {
            inventoryDisplayable.setSelectorIndex(index);

            keyPressMap.put(KeyRole.INVENTORY, GameViewState.this::setMovementState);

            keyPressMap.put(KeyRole.N, this::decrementIndex);
            keyPressMap.put(KeyRole.S, this::incrementIndex);


            keyPressMap.put(KeyRole.ACTION, () ->
            {
                Entity player = gameModel.getPlayer();
                if(index < player.getBackpack().getSizeLimit())
                {
                    TakeableItem item = player.getBackpack().getItemAtSlot(index);
                    if(item != null )
                    {
                        item.equipToPlayer();
                    }
                }
                else
                {
                    int index = this.index - player.getBackpack().getSizeLimit();
                    if( 3 /*player.getActiveEquipment().size()*/ > index)
                    {
                        //TakeableItem item = player.getActiveEquipment().get(index);
                        //if(item != null)
                        //{
                        //    item.unequip(player);
                        if(index == 0) {
                            player.unequipWeapon();
                        } else if (index == 1) {
                            player.unequipArmor();
                        } else if (index == 2) {
                            player.unequipRing();
                        }

                            inventoryDisplayable.setSelectorIndex(this.index = 0);
                        //}
                    }
                }

            });
        }

        private void incrementIndex()
        {
            index = (index >= gameModel.getPlayer().getBackpack().getSizeLimit() + 3 - 1/*gameModel.getPlayer().getActiveEquipment().size() - 1*/) ? 0 : index + 1;
            inventoryDisplayable.setSelectorIndex(index);
        }

        private void decrementIndex()
        {
            index = (index <= 0) ? gameModel.getPlayer().getBackpack().getSizeLimit() + 3 - 1 /*gameModel.getPlayer().getActiveEquipment().size() - 1*/: index - 1;
            inventoryDisplayable.setSelectorIndex(index);
        }

        public void parseKeyPress(KeyRole keyCode)
        {
            keyPressMap.getOrDefault(keyCode, AbstractFunction.NULL).execute();
        }

        public void parseKeyRelease(KeyRole keyCode) { }

    }

    private class ShopState implements InputState
    {
        private Map<KeyRole, AbstractFunction> keyPressMap = new HashMap<>();

        private InventoryDisplayable shopDisplayable;
        private InventoryDisplayable focusedInventory;

        private Entity shopkeeper;

        private int index = 0;

        public ShopState(Entity shopkeeper)
        {
            focusedInventory = shopDisplayable = new InventoryDisplayable(new Point(384, 384), shopkeeper);
            overlays.add(shopDisplayable);
            this.shopkeeper = shopkeeper;
            keyPressMap.put(KeyRole.S, this::incrementIndex);
            keyPressMap.put(KeyRole.N, this::decrementIndex);
            keyPressMap.put(KeyRole.E, this::toggleFocus);
            keyPressMap.put(KeyRole.W, this::toggleFocus);
            keyPressMap.put(KeyRole.ACTION, this::transact);
            keyPressMap.put(KeyRole.BACK, this::returnToGame);
        }

        public void parseKeyPress(KeyRole keyrole)
        {
            keyPressMap.getOrDefault(keyrole, AbstractFunction.NULL).execute();
        }

        public void parseKeyRelease(KeyRole keyrole) {}

        private void returnToGame()
        {
            overlays.remove(shopDisplayable);
            focusedInventory.setSelectorIndex(-1);
            setMovementState();
        }

        private void incrementIndex()
        {
            index = (index >= gameModel.getPlayer().getBackpack().getSizeLimit() - 1) ? 0 : index + 1;
            focusedInventory.setSelectorIndex(index);
        }

        private void decrementIndex()
        {
            index = (index <= 0) ? gameModel.getPlayer().getBackpack().getSizeLimit() - 1 : index - 1;
            focusedInventory.setSelectorIndex(index);
        }

        private void transact()
        {
            Entity seller = (focusedInventory == inventoryDisplayable) ? gameModel.getPlayer() : shopkeeper;
            Entity buyer = (focusedInventory == inventoryDisplayable) ? shopkeeper : gameModel.getPlayer();
            TakeableItem forSale = seller.getBackpack().getItemAtSlot(index);

            int price = 5;
            if(buyer == gameModel.getPlayer()){
                price = 5 / gameModel.getPlayer().getSkillLevel(SkillPool.BARGAIN);
            }

            if(buyer.getBackpack().hasFreeSlot() && buyer.getCurrentCurrency() >= price && forSale != null)
            {
                buyer.setCurrentCurrency(buyer.getCurrentCurrency() - price);
                seller.setCurrentCurrency(seller.getCurrentCurrency() + price);
                buyer.getBackpack().addItem(forSale);
                seller.getBackpack().removeItem(forSale);
            }
        }

        // Switches control from
        private void toggleFocus()
        {
            focusedInventory.setSelectorIndex(-1);

            focusedInventory = (focusedInventory == shopDisplayable) ? inventoryDisplayable : shopDisplayable;

            focusedInventory.setSelectorIndex(index);
        }
    }

    private class LevelUpState implements InputState
    {
        private Map<KeyRole, AbstractFunction> keyPressMap = new HashMap<>();

        private int index = 0;

        private List<Skill> skills;

        private LevelUpDisplayable levelUpDisplayable;

        public LevelUpState(int skillPoints)
        {
            keyPressMap.put(KeyRole.N, this::decrementIndex);
            keyPressMap.put(KeyRole.S, this::incrementIndex);
            keyPressMap.put(KeyRole.ACTION, this::spendPoint);

            Entity player = gameModel.getPlayer();

            skills = new ArrayList<>(player.getSkillMap().keySet());

            overlays.add(levelUpDisplayable = new LevelUpDisplayable(new Point(384, 384), player, skills, skillPoints));
        }

        public void parseKeyPress(KeyRole keyRole)
        {
            keyPressMap.getOrDefault(keyRole, AbstractFunction.NULL).execute();
        }

        public void parseKeyRelease(KeyRole keyRole)
        {

        }

        private void incrementIndex()
        {
            index++;
            if(index > skills.size() - 1) index = 0;
            levelUpDisplayable.setIndex(index);
        }

        private void decrementIndex()
        {
            index--;
            if(index < 0) index = skills.size() - 1;
            levelUpDisplayable.setIndex(index);
        }

        private void spendPoint()
        {
            gameModel.getPlayer().increaseSkillLevel(skills.get(index));
            levelUpDisplayable.spendSkillPoint();
            if(levelUpDisplayable.getSkillPoints() <= 0)
            {
                overlays.remove(levelUpDisplayable);
                GameViewState.this.setMovementState();
            }
        }
    }

    private class MenuState implements InputState
    {
        private Map<KeyRole, AbstractFunction> keyPressMap = new HashMap<>();

        public MenuState()
        {
            keyPressMap.put(KeyRole.SAVE, GameViewState.this::saveGame);
            keyPressMap.put(KeyRole.BACK, this::returnState);
            keyPressMap.put(KeyRole.QUIT, () -> System.exit(0));
        }
        private void returnState()
        {
            setMovementState();
            overlays.remove(menuView);
        }
        public void parseKeyPress(KeyRole keyCode)
        {
            keyPressMap.getOrDefault(keyCode, AbstractFunction.NULL).execute();
        }

        public void parseKeyRelease(KeyRole keyCode) { }
    }

    private class InteractState implements InputState
    {
        private Map<KeyRole, AbstractFunction> keyPressMap = new HashMap<>();
        private int index = 0;

        private EntityInteractionDisplayable entityInteractionDisplayable = new EntityInteractionDisplayable(new Point(384, 384));

        private Entity target;

        public InteractState(Entity target)
        {
            if(!gameModel.isNpcFriendly(target)) {
                return;
            }
            this.target = target;
            entityInteractionDisplayable.setSelectorIndex(index);
            keyPressMap.put(KeyRole.N, this::decrementIndex);
            keyPressMap.put(KeyRole.S, this::incrementIndex);
            keyPressMap.put(KeyRole.ACTION, this::executeAction);
            keyPressMap.put(KeyRole.BACK, this::cancel);
            overlays.add(entityInteractionDisplayable);
        }

        private void cancel()
        {
            GameViewState.this.setMovementState();
            overlays.remove(entityInteractionDisplayable);
        }

        private void executeAction()
        {
            Entity player = gameModel.getPlayer();

            switch(index)
            {
                case 0:
                    DialogNotifier.notifyDialog(target);
                    if(target.getType() == EntityType.SHOPKEEPER)
                    {
                        GameViewNotifier.notifyShopping(target);
                        overlays.remove(entityInteractionDisplayable);
                        return;
                    }
                    break;

                case 1:
                    player.useWeaponSkill();
                    break;

                case 2:
                    player.useSkill(player.getActiveSkill());
                    break;

                case 3:
                    GameViewState.this.setUseItemState(target);
                    overlays.remove(entityInteractionDisplayable);
                    return;
            }

            overlays.remove(entityInteractionDisplayable);
            GameViewState.this.setMovementState();
        }

        private void incrementIndex()
        {
            index = (index >= 3) ? 0 : index + 1;
            entityInteractionDisplayable.setSelectorIndex(index);
        }

        private void decrementIndex()
        {
            index = (index <= 0) ? 3 : index - 1;
            entityInteractionDisplayable.setSelectorIndex(index);
        }

        public void parseKeyPress(KeyRole keyCode)
        {
            keyPressMap.getOrDefault(keyCode, AbstractFunction.NULL).execute();
        }

        public void parseKeyRelease(KeyRole keyCode) { }
    }

    private class UseItemState implements InputState
    {
        private Map<KeyRole, AbstractFunction> inputMap;
        private int index = 0;

        private Entity target;

        public UseItemState(Entity target)
        {
            System.out.println("UseItemState initialized");
            inputMap = new HashMap<>();
            inputMap.put(KeyRole.N, this::decrementIndex);
            inputMap.put(KeyRole.S, this::incrementIndex);
            inputMap.put(KeyRole.ACTION, this::useItem);
            inputMap.put(KeyRole.BACK, this::cancel);
            this.target = target;
            inventoryDisplayable.setSelectorIndex(index);
        }

        private void decrementIndex()
        {
            index--;
            inventoryDisplayable.setSelectorIndex(index);
        }

        private void incrementIndex()
        {
            index++;
            inventoryDisplayable.setSelectorIndex(index);
        }

        private void useItem()
        {
            TakeableItem item = gameModel.getPlayer().getBackpack().getItemAtSlot(index);
            if(item != null && target.hasFreeBackPackSlot())
            {
                // You can currently only use ConsumableItems on other Entities
                if(item instanceof ConsumableItem)
                {
                    gameModel.getPlayer().removeItemFromBackpack(item);
                    target.addItemToBackpack(item);
                    target.useConsumableItem((ConsumableItem) item);
                }
                cancel();
            }

        }

        private void cancel()
        {
            inventoryDisplayable.setSelectorIndex(-1);
            GameViewState.this.setMovementState();
        }

        public void parseKeyRelease(KeyRole keyrole)
        {

        }

        public void parseKeyPress(KeyRole keyrole)
        {
            inputMap.getOrDefault(keyrole, AbstractFunction.NULL).execute();
        }
    }
}
