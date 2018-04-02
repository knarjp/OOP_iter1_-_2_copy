package com.MeanTeam.visitors;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.Decal;
import com.MeanTeam.gamemodel.tile.Obstacle;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.areaeffects.AreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.InfiniteAreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.OneShotAreaEffect;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.entities.EntityType;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.tile.effectcommand.*;
import com.MeanTeam.gamemodel.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;

import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.util.Path;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class LoadingParser
{
    private static List<World> worlds = new ArrayList<>();
    private static List<WorldNotifier> worldNotifiers = new ArrayList<>();
    private static Entity player = null;
    private static String playerID = "";

    private static Map<String, Item> itemMap = new HashMap<>();
    private static Map<String, EventCommand> eventMap = new HashMap<>();
    private static Map<String, Entity> entityMap = new HashMap<>();
    private static Map<Entity, World> npcWorldMap = new HashMap<>();
    private static Map<String, AreaEffect> effectMap = new HashMap<>();
    private static Map<String, Skill> skillMap = new HashMap<>();

    private static GameModelNotifier gameModelNotifier;
    private static GameModel model = null;

    public static GameModel loadGame() {
        File file = new File("savegame.txt");

        try {
            clearGameData();

            GameModel model = new GameModel(worlds, worldNotifiers, null, null, null, null, -1);

            gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            initializeMaps(line, bufferedReader, file);

            line = bufferedReader.readLine();
            String[] token = line.split(" ");

            System.out.println("DONE!!!!!!!!!!!!!!!!!!!!!!!!!!");

            int curWorldIndex = getCurrentWorldIndex(token);

            setModelDisplayableFactoryEntity(token);

            playerID = token[6];

            line = bufferedReader.readLine();

            while(notAtEndOfSave(line)) {
                if(atWorldStart(line)) {
                    System.out.println("Loading World");

                    World world = new World();
                    worlds.add(world);

                    WorldNotifier worldNotifier = new WorldNotifier(world);
                    worldNotifiers.add(worldNotifier);

                    String contentsLine = bufferedReader.readLine();

                    while(notAtWorldEnd(contentsLine)) {
                        System.out.println("Reading world contents line");
                        String[] contentsTokens = contentsLine.split(" ");

                        for(String s : contentsTokens) {
                            System.out.println(s);
                        }

                        switch (contentsTokens[0]) {
                            case "TERRAIN":
                            {
                                addTerrainTo(world, contentsTokens);
                                break;
                            }
                            case "DECAL":
                            {
                                addDecalTo(world, contentsTokens);
                                break;
                            }
                            case "ITEM":
                                addItemTo(world, worldNotifier, contentsTokens);
                                break;
                            case "ENTITY":
                            {
                                addEntityTo(world, worldNotifier, contentsTokens);
                                break;
                            }
                            case "OBSTACLE":
                            {
                                addObstacleTo(world, contentsTokens);
                                break;
                            }
                            case "AREA_EFFECT":
                            {
                                addAreaEffectTo(world, worldNotifier, contentsTokens);
                                break;
                            }
                            case "TRAP":
                            {
                                addTrapTo(world, contentsTokens);
                                break;
                            }
                            case "SPAWNS": {

                                int i = 1;

                                while(!token[i].equals("END_SPAWNS")) {
                                    String[] pointCoords = token[i+3].split(",");
                                    Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));

                                    Entity entity = entityMap.get(token[i+1]);

                                    world.addNpc(point, entity);

                                    i++;
                                }

                                break;
                            }
                            case "FRIENDLY_NPC_CONTROLLER":
                            {
                                model.addFriendlyNpcController();

                                int i = 0;

                                int entityCount = -1;
                                int pathCount = -1;

                                List<Entity> entities = new ArrayList<>();
                                List<Path> paths = new ArrayList<>();

                                while(i < token.length) {
                                    if(token[i].equals("ENTITY")) {
                                        entityCount++;
                                        entities.add(entityMap.get(token[i+1]));
                                    }
                                    else if(token[i].equals("PATH")) {
                                        pathCount++;
                                        paths.add(new Path());
                                    } else {
                                        switch(token[i]) {
                                            case "NORTH":
                                                paths.get(pathCount).addOrientation(Orientation.NORTH);
                                            case "SOUTH":
                                                paths.get(pathCount).addOrientation(Orientation.SOUTH);
                                            case "EAST":
                                                paths.get(pathCount).addOrientation(Orientation.EAST);
                                            case "WEST":
                                                paths.get(pathCount).addOrientation(Orientation.WEST);
                                            case "NORTHEAST":
                                                paths.get(pathCount).addOrientation(Orientation.NORTHEAST);
                                            case "NORTHWEST":
                                                paths.get(pathCount).addOrientation(Orientation.NORTHWEST);
                                            case "SOUTHEAST":
                                                paths.get(pathCount).addOrientation(Orientation.SOUTHEAST);
                                            case "SOUTHWEST":
                                                paths.get(pathCount).addOrientation(Orientation.SOUTHWEST);
                                            default:
                                                break;
                                        }
                                    }

                                    i++;
                                }

                                for(int j = 0; j < entities.size(); j++) {
                                    model.addFriendlyNpc(world, entities.get(i));
                                    model.addFriendlyNpcPath(entities.get(i), world, paths.get(i));
                                }

                                break;
                            }
                            case "HOSTILE_NPC_CONTROLLER":
                            {
                                model.addHostileNpcController(player);

                                int i = 0;

                                int entityCount = -1;
                                int pathCount = -1;

                                List<Entity> entities = new ArrayList<>();
                                List<Path> paths = new ArrayList<>();

                                while(i < token.length) {
                                    if(token[i].equals("ENTITY")) {
                                        entityCount++;
                                        entities.add(entityMap.get(token[i+1]));
                                    }
                                    else if(token[i].equals("PATH")) {
                                        pathCount++;
                                        paths.add(new Path());
                                    }
                                    else if(token[i].equals("CONFUSED")) {

                                    } else {
                                        switch(token[i]) {
                                            case "NORTH":
                                                paths.get(pathCount).addOrientation(Orientation.NORTH);
                                            case "SOUTH":
                                                paths.get(pathCount).addOrientation(Orientation.SOUTH);
                                            case "EAST":
                                                paths.get(pathCount).addOrientation(Orientation.EAST);
                                            case "WEST":
                                                paths.get(pathCount).addOrientation(Orientation.WEST);
                                            case "NORTHEAST":
                                                paths.get(pathCount).addOrientation(Orientation.NORTHEAST);
                                            case "NORTHWEST":
                                                paths.get(pathCount).addOrientation(Orientation.NORTHWEST);
                                            case "SOUTHEAST":
                                                paths.get(pathCount).addOrientation(Orientation.SOUTHEAST);
                                            case "SOUTHWEST":
                                                paths.get(pathCount).addOrientation(Orientation.SOUTHWEST);
                                            default:
                                                break;
                                        }
                                    }

                                    i++;
                                }

                                for(int j = 0; j < entities.size(); j++) {
                                    model.addHostileNpc(world, entities.get(i));
                                    model.addHostileNpcPath(entities.get(i), world, paths.get(i));
                                }
                                break;
                            }
                            default:
                            {

                            }
                        }

                        contentsLine = bufferedReader.readLine();
                    }
                }

                line = bufferedReader.readLine();
            }

            model.initialize(curWorldIndex, player);
            return model;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void clearGameData() {
        worlds.clear();
        worldNotifiers.clear();
        player = null;
        model = null;
        playerID = "";
        itemMap.clear();
        entityMap.clear();
        eventMap.clear();
        effectMap.clear();
        skillMap.clear();
        GameModelNotifier.destroyInstance();
    }

    private static void initializeMaps(String line, BufferedReader bufferedReader, File file) {
        try {
            line = bufferedReader.readLine();

            while(!line.equals("END_SKILL_REGISTRY")) {
                String[] contentsTokens = line.split(" ");

                System.out.println(line + " 1");

                createSkillFromToken(contentsTokens, file);

                line = bufferedReader.readLine();
            }

            line = bufferedReader.readLine();
            line = bufferedReader.readLine();

            while(!line.equals("END_ITEM_REGISTRY")) {
                String[] contentsTokens = line.split(" ");

                System.out.println(line + " 2");

                createItemFromToken(contentsTokens, file);

                line = bufferedReader.readLine();
            }

            line = bufferedReader.readLine();
            line = bufferedReader.readLine();

            while(!line.equals("END_EVENT_REGISTRY")) {
                String[] contentsTokens = line.split(" ");

                System.out.println(line + " 3");

                createEventFromToken(contentsTokens, file);

                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createSkillFromToken(String[] token, File file) {
        String reference = token[0];

        if(!skillMap.containsKey(reference)) { // must create skill, it is not in the map
            System.out.println("creating skill");
            String name = token[2].replace("~", " ");
            int baseAmount = Integer.parseInt(token[4]);
            int levelEffectiveness = Integer.parseInt(token[6]);
            int levelSuccess = Integer.parseInt(token[8]);
            double distanceEffectiveness = Integer.parseInt(token[10]);
            int maxRange = Integer.parseInt(token[12]);
            int spread = Integer.parseInt(token[14]);
            int successRate = Integer.parseInt(token[16]);
            String eventReference = token[18];
            SettableEventCommand event;
            int cooldown = Integer.parseInt(token[20]);
            int nextCast = Integer.parseInt(token[22]);

            if (effectMap.containsKey(eventReference)) { // have effect in map
                event = (SettableEventCommand) eventMap.get(eventReference);
            } else { // dont have event, create it
                try {
                    // need to read file until the event is created
                    FileReader fileReader2 = new FileReader(file);
                    BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                    String tempLine = bufferedReader2.readLine();

                    while(!tempLine.split(" ")[0].equals(eventReference)) { // get event reference
                        tempLine = bufferedReader2.readLine();
                    }

                    bufferedReader2.close();
                    fileReader2.close();

                    String[] eventToken = tempLine.split(" ");

                    createEventFromToken(eventToken, file);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                event = (SettableEventCommand) eventMap.get(eventReference);
            }

            Skill skill = new Skill(name, maxRange, spread, successRate, baseAmount,
                    levelEffectiveness, levelSuccess, distanceEffectiveness, event, cooldown);

            skillMap.put(reference, skill);
        }
    }

    private static void createItemFromToken(String[] token, File file) {
        String reference = token[0];

        if(!itemMap.containsKey(reference)) { // must create item, it is not in the map
            System.out.println("creating item");
            switch(token[1]) {
                case "ARMOR_ITEM":
                    String name = token[3].replace("~", " ");

                    ItemType type = ItemType.valueOf(token[7]);

                    String eventReference = token[5];

                    ToggleableEventCommand event;

                    if (effectMap.containsKey(eventReference)) { // have effect in map
                        event = (ToggleableEventCommand) eventMap.get(eventReference);
                    } else { // dont have event, create it
                        try {
                            // need to read file until the event is created
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while(!tempLine.split(" ")[0].equals(eventReference)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createEventFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        event = (ToggleableEventCommand) eventMap.get(eventReference);
                    }

                    itemMap.put(reference, new ArmorItem(name, event, type, null, gameModelNotifier));
                    break;
                case "CONSUMABLE_ITEM":
                    String name1 = token[3].replace("~", " ");

                    ItemType type1 = ItemType.valueOf(token[7]);

                    String eventReference1 = token[5];

                    EventCommand event1;

                    if (effectMap.containsKey(eventReference1)) { // have effect in map
                        event1 = eventMap.get(eventReference1);
                    } else { // dont have event, create it
                        try {
                            // need to read file until the event is created
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while(!tempLine.split(" ")[0].equals(eventReference1)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createEventFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        event1 = eventMap.get(eventReference1);
                    }

                    itemMap.put(reference, new ConsumableItem(name1, event1, type1, null, gameModelNotifier));
                    break;
                case "INTERACTIVE_ITEM":
                    String eventReference2 = token[3];

                    ItemType type2 = ItemType.valueOf(token[5]);

                    EventCommand event2;

                    if (effectMap.containsKey(eventReference2)) { // have effect in map
                        event2 = eventMap.get(eventReference2);
                    } else { // dont have event, create it
                        try {
                            // need to read file until the event is created
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while(!tempLine.split(" ")[0].equals(eventReference2)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createEventFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        event2 = eventMap.get(eventReference2);
                    }

                    itemMap.put(reference, new InteractiveItem(event2, type2, null));
                    break;
                case "ONESHOT_ITEM":
                    String eventReference3 = token[3];

                    ItemType type3 = ItemType.valueOf(token[5]);

                    EventCommand event3;

                    if (effectMap.containsKey(eventReference3)) { // have effect in map
                        event3 = eventMap.get(eventReference3);
                    } else { // dont have event, create it
                        try {
                            // need to read file until the event is created
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while(!tempLine.split(" ")[0].equals(eventReference3)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createEventFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        event3 = eventMap.get(eventReference3);
                    }

                    itemMap.put(reference, new OneshotItem(event3, type3, null));
                    break;
                case "RING_ITEM":
                    String name4 = token[3].replace("~", " ");

                    ItemType type4 = ItemType.valueOf(token[7]);

                    String eventReference4 = token[5];

                    ToggleableEventCommand event4;

                    if (effectMap.containsKey(eventReference4)) { // have effect in map
                        event4 = (ToggleableEventCommand) eventMap.get(eventReference4);
                    } else { // dont have event, create it
                        try {
                            // need to read file until the event is created
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while(!tempLine.split(" ")[0].equals(eventReference4)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createEventFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        event4 = (ToggleableEventCommand) eventMap.get(eventReference4);
                    }

                    itemMap.put(reference, new RingItem(name4, event4, type4, null, gameModelNotifier));
                    break;
                case "TAKEABLE_ITEM":
                    String name5 = token[3].replace("~", " ");

                    ItemType type5 = ItemType.valueOf(token[5]);

                    itemMap.put(reference, new TakeableItem(name5, type5, null, gameModelNotifier));
                    break;
                case "WEAPON_ITEM":
                    String name6 = token[3].replace("~", " ");
                    ItemType type6 = ItemType.valueOf(token[19]);
                    long attackSpeed6 = Long.parseLong(token[5]);
                    float staminaCost6 = Float.parseFloat(token[7]);
                    int attackDamage6 = Integer.parseInt(token[9]);
                    int spread6 = Integer.parseInt(token[11]);
                    int distance6 = Integer.parseInt(token[13]);

                    String eventReference6 = token[15];
                    String skillReference6 = token[17];

                    SettableEventCommand event6;

                    if (effectMap.containsKey(eventReference6)) { // have effect in map
                        event6 = (SettableEventCommand) eventMap.get(eventReference6);
                    } else { // dont have event, create it
                        try {
                            // need to read file until the event is created
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while(!tempLine.split(" ")[0].equals(eventReference6)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createEventFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        event6 = (SettableEventCommand) eventMap.get(eventReference6);
                    }

                    Skill skill6;

                    if (skillMap.containsKey(skillReference6)) { // have effect in map
                        skill6 = skillMap.get(skillReference6);
                    } else { // dont have event, create it
                        try {
                            // need to read file until the event is created
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while(!tempLine.split(" ")[0].equals(skillReference6)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createSkillFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        skill6 = skillMap.get(skillReference6);
                    }

                    itemMap.put(reference, new WeaponItem(name6, type6, null, attackSpeed6, staminaCost6,
                            attackDamage6, spread6, distance6, event6, skill6, gameModelNotifier));
                    break;
                default:
                    itemMap.put(reference, null);
                    break;
            }
        }
    }

    private static void createEventFromToken(String[] token, File file) {
        String reference = token[0];

        if(!eventMap.containsKey(reference)) { // must create event, it is not in the map
            System.out.println("creating event");
            switch(token[1]) {
                case "NULL":
                    eventMap.put(reference, EventCommand.NULL);
                    break;
                case "NULLSETTABLE":
                    eventMap.put(reference, SettableEventCommand.NULL);
                    break;
                case "CONDITIONAL_TRANSITION_EVENT":
                    int destWorld = Integer.parseInt(token[3]);

                    boolean isLocked = Boolean.parseBoolean(token[7]);

                    String[] pointCoords = token[5].split(",");
                    Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));

                    String itemReference = token[9];

                    TakeableItem key;

                    if (itemMap.containsKey(itemReference)) { // have item in map
                        key = (TakeableItem) itemMap.get(itemReference);
                    } else {
                        try {
                            // need to read file until the item is found
                            FileReader fileReader2 = new FileReader(file);
                            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

                            String tempLine = bufferedReader2.readLine();

                            while (!tempLine.split(" ")[0].equals(itemReference)) { // get event reference
                                tempLine = bufferedReader2.readLine();
                            }

                            bufferedReader2.close();
                            fileReader2.close();

                            String[] eventToken = tempLine.split(" ");

                            createItemFromToken(eventToken, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        key = (TakeableItem) itemMap.get(itemReference);
                    }

                    eventMap.put(reference, new ConditionalTransitionEvent(destWorld, point, gameModelNotifier, key, isLocked));
                    break;
                case "CONFUSE_EVENT":
                    eventMap.put(reference, new ConfuseEvent(Integer.parseInt(token[3]), model));
                    break;
                case "CREEP_EVENT":
                    eventMap.put(reference, new CreepEvent(Integer.parseInt(token[3]), gameModelNotifier));
                    break;
                case "DAMAGE_EVENT":
                    eventMap.put(reference, new DamageEvent(Integer.parseInt(token[3])));
                    break;
                case "TRAP_DISARM_EVENT":
                    eventMap.put(reference, new DisarmTrapEvent(Integer.parseInt(token[3]), gameModelNotifier));
                    break;
                case "HEAL_EVENT":
                    eventMap.put(reference, new HealEvent(Integer.parseInt(token[3])));
                    break;
                case "INSTADEATH_EVENT":
                    eventMap.put(reference, new InstaDeathEvent());
                    break;
                case "LEVELUP_EVENT":
                    eventMap.put(reference, new LevelUpEvent());
                    break;
                case "OBSERVE_EVENT":
                    eventMap.put(reference, new ObservationEvent(Integer.parseInt(token[3])));
                    break;
                case "PICK_POCKET_EVENT":
                    eventMap.put(reference, new PickPocketEvent(Integer.parseInt(token[3]), gameModelNotifier));
                    break;
                case "SLOWDOWN_EVENT":
                    eventMap.put(reference, new SlowdownEvent(Integer.parseInt(token[3]), model));
                    break;
                case "STAMINA_DECREASE_EVENT":
                    eventMap.put(reference, new StaminaDecreaseEvent(Integer.parseInt(token[3])));
                    break;
                case "START_DIALOG_EVENT":
                    eventMap.put(reference, new StartDialogEvent(Integer.parseInt(token[3])));
                    break;
                case "TOGGLE_HEALTH_EVENT":
                    eventMap.put(reference, new ToggleableHealthEvent(Integer.parseInt(token[5])));
                    break;
                case "TRANSITION_EVENT":
                    int destWorld2 = Integer.parseInt(token[3]);

                    String[] pointCoords2 = token[5].split(",");
                    Point point2 = new Point(Integer.parseInt(pointCoords2[0]), Integer.parseInt(pointCoords2[1]));

                    eventMap.put(reference, new TransitionEvent(destWorld2, point2, gameModelNotifier));
                    break;
                default:
                    eventMap.put(reference, EventCommand.NULL);
                    break;
            }
        }
    }

    private static int getCurrentWorldIndex(String[] token) {
        int curWorldIndex = Integer.parseInt(token[2]);
        System.out.println("CurWorldIndex = " + curWorldIndex);

        return curWorldIndex;
    }

    private static void setModelDisplayableFactoryEntity(String[] token) {
        int entityIndex = Integer.parseInt(token[4]);
        ModelDisplayableFactory.setEntityIndex(entityIndex);
    }

    private static boolean notAtEndOfSave(String line) {
        return !line.equals("ENDSAVE");
    }

    private static boolean atWorldStart(String line) {
        return line.split(" ")[0].equals("WORLD");
    }

    private static boolean notAtWorldEnd(String line) {
        return !line.contains("ENDWORLD");
    }

    private static void addTerrainTo(World world, String[] token) {
        System.out.println("Loading Terrain");
        String[] pointCoords = token[2].split(",");
        Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));
        Terrain terrain = Terrain.valueOf(token[3]);
        world.add(point, terrain);
    }

    private static void addDecalTo(World world, String[] token) {
        System.out.println("Loading Decal");
        String[] pointCoords = token[2].split(",");
        Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));
        Decal decal = Decal.valueOf(token[3]);
        world.add(point, decal);
    }

    private static void addItemTo(World world, WorldNotifier worldNotifier, String[] token) {
        System.out.println("Loading Item");

        String reference = token[3];

        String[] pointCoords = token[2].split(",");
        Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));

        Item item = itemMap.get(reference);
        item.setWorldNotifier(worldNotifier);

        world.add(point, item);
    }

    private static void addEntityTo(World world, WorldNotifier worldNotifier, String[] token) {
        System.out.println("Loading Entity");

        String[] pointCoords = token[2].split(",");
        Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));

        String[] hpString = token[7].split("/");

        String[] staminaString = token[11].split("/");

        EntityType entityType = EntityType.valueOf(token[5]);
        float currentHealth = Float.parseFloat(hpString[0]);
        float maxHealth = Float.parseFloat(hpString[1]);
        int experience = Integer.parseInt(token[9]);
        int currentStamina = Integer.parseInt(staminaString[0]);
        int maxStamina = Integer.parseInt(staminaString[1]);
        int staminaRegenRate = Integer.parseInt(token[13]);
        int currentCurrency = Integer.parseInt(token[15]);
        long attackSpeed = Long.parseLong(token[17]);
        long nextAttackTime = Long.parseLong(token[19]);
        int backpackSize = Integer.parseInt(token[23]);
        Inventory backpack = new Inventory(backpackSize, gameModelNotifier);

        int i;

        for(i = 24; i < backpackSize + 24; i++) {
            TakeableItem item;
            if(itemMap.containsKey(token[i])) {
                item = (TakeableItem) itemMap.get(token[i]);
                item.setWorldNotifier(worldNotifier);
            }
        }

        int attackDamage = Integer.parseInt(token[i + 2]);

        while(!token[i].equals("ORIENTATION")) {
            i++;
        }

        Orientation orientation = Orientation.valueOf(token[i + 1]);

        i = i + 3; // get to SKILLMAP's first skill

        LinkedHashMap<Skill, Integer> skillList = new LinkedHashMap<>();
        List<Skill> allSkills = new ArrayList<>();
        List<Skill> nonWeaponSkills = new ArrayList<>();

        while(!token[i].equals("NONWEAPON")) {
            System.out.println("added skill");
            skillList.put(skillMap.get(token[i]), Integer.parseInt(token[i + 2]));
            allSkills.add(skillMap.get(token[i]));
            i = i + 3;
        }

        while(!token[i+1].equals("ACTIVE_SKILL")) {
            System.out.println(token[i]);
            if(skillMap.get(token[i]) == null) {
                System.out.println("bad");
            } else {
                System.out.println("good");
            }
            System.out.println("added nono weaponskill");
            nonWeaponSkills.add(skillMap.get(token[i]));
            i++;
        }

        System.out.println("hererere "+token[i+2]);

        Skill activeSkill = skillMap.get(token[i + -1]);

        if(activeSkill != null) {
            System.out.println("added active skill");
        }

        i = i + 4;

        long nextMoveTime = Long.parseLong(token[i]);
        long movementSpeed = Long.parseLong(token[i + 2]);
        int noiseLevel = Integer.parseInt(token[i + 4]);

        WeaponItem equippedWeapon;
        ArmorItem equippedArmor;
        RingItem equippedRing;
        WeaponItem defaultWeapon = null;

        if(token[i + 6].equals("null")) {
            equippedWeapon = null;
        } else {
            equippedWeapon = (WeaponItem) itemMap.get(token[i + 6]);
        }
        if(token[i + 8].equals("null")) {
            equippedArmor = null;
        } else {
            equippedArmor = (ArmorItem) itemMap.get(token[i + 8]);
        }

        if(token[i + 10].equals("null")) {
            equippedRing = null;
        } else {
            equippedRing = (RingItem) itemMap.get(token[i + 10]);
        }

        String entityID = token[i + 13];

        Entity entity = new Entity(maxHealth, maxStamina, staminaRegenRate, attackSpeed, orientation,
                                    backpack, worldNotifier, defaultWeapon, entityType);

        for(Skill skill : nonWeaponSkills) { // add non weapon skills and their level
           if(skillList.get(skill) == null) {
               System.out.println("here");
           } else {
               entity.addSkill(skill, skillList.get(skill));
           }
        }

        for(Skill skill : allSkills) { // add weapon skills and their level
            if(!nonWeaponSkills.contains(skill)) {
                entity.addWeaponSkill(skill, skillList.get(skill));
            }
        }

        entity.setCurrentHealth(currentHealth);
        entity.setExperience(experience);
        entity.setCurrentStamina(currentStamina);
        entity.setCurrentCurrency(currentCurrency);
        entity.setAttackDamage(attackDamage);
        entity.setActiveSkill(activeSkill);
        entity.setMovementSpeed(movementSpeed);
        entity.setNoiseLevel(noiseLevel);
        entity.setEquippedWeapon(equippedWeapon);
        entity.setEquippedArmor(equippedArmor);
        entity.setEquippedRing(equippedRing);

        if (playerID.equals(entityID)) {
            player = entity;
            world.addPlayer(point, entity);
        } else {
            entityMap.put(entityID, entity);
            npcWorldMap.put(entity, world);
        }
    }

    private static void addObstacleTo(World world, String[] token) {
        System.out.println("Loading Obstacle");

        String[] pointCoords = token[2].split(",");
        Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));
        Obstacle obstacle = new Obstacle(Integer.parseInt(token[3]));
        world.add(point, obstacle);
    }

    private static void addAreaEffectTo(World world, WorldNotifier worldNotifier, String[] token) {
        System.out.println("Loading AreaEffect");

        String[] pointCoords = token[2].split(",");
        Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));

        switch(token[3]) {
            case "INF_AREA_EFFECT":
                    world.add(point, new InfiniteAreaEffect(eventMap.get(token[6])));
                break;
            case "ONESHOT_AREA_EFFECT":
                OneShotAreaEffect effect = new OneShotAreaEffect(eventMap.get(token[8]));

                effect.setHasNotFired(Boolean.parseBoolean(token[5]));

                world.add(point, effect);
                break;
        }
    }

    private static void addTrapTo(World world, String[] token) {
        System.out.println("Loading Trap");

        String[] pointCoords = token[2].split(",");
        Point point = new Point(Integer.parseInt(pointCoords[0]), Integer.parseInt(pointCoords[1]));

        Trap trap = new Trap((SettableEventCommand) eventMap.get(token[5]), Integer.parseInt(token[7]));

        trap.setHasNotFired(Boolean.parseBoolean(token[11]));
        trap.setVisible(Boolean.parseBoolean(token[9]));

        world.add(point, trap);
    }
}