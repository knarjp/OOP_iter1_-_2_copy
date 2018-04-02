package com.MeanTeam.gameview;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.Item;
import com.MeanTeam.gamemodel.tile.items.ItemType;
import com.MeanTeam.gamemodel.tile.Decal;
import com.MeanTeam.gamemodel.tile.Obstacle;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.gameview.displayables.EntityDirectionDisplayable;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;
import com.MeanTeam.guiframework.displayables.ConditionalDisplayable;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import com.MeanTeam.util.ImageFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ModelDisplayableFactory
{
    // =========== avatar Image configuring =======================
    public static int getEntityIndex() {
        return entityIndex;
    }

    public static void setEntityIndex(int entityIndex) {
        ModelDisplayableFactory.entityIndex = entityIndex;
    }

    private static int entityIndex = 0;

    public static void incrementEntityIndex()
    {
        entityIndex++;
        if(entityIndex >= ImageLoader.getEntityImages().length)
        {
            entityIndex = 0;
        }
    }

    public static void decrementEntityIndex()
    {
        entityIndex--;
        if(entityIndex < 0)
        {
            entityIndex = ImageLoader.getEntityImages().length - 1;
        }
    }

    private static Map<Entity, Integer> entityToAvatarMap = new HashMap<Entity, Integer>();

    public static Displayable getEntityImageDisplayable() { return new ImageDisplayable(new Point(0, 0), ImageLoader.getEntityImages()[entityIndex]); }
    public static Displayable getEntityPedigreeDisplayable() { return new ImageDisplayable(new Point(0, 0), ImageLoader.getEntityPedigree(entityIndex)); }

    public static Displayable getEntityDisplayable(Entity entity)
    {
        int index = entity.getType().ordinal(); //entityToAvatarMap.get(entity);
        CompositeDisplayable entityDisplayable = new CompositeDisplayable(new Point(12, 6));

        ConditionalDisplayable entitySprite = new ConditionalDisplayable(new Point(0, 0), new ImageDisplayable(new Point(0, 0), ImageLoader.getEntityImages()[index]));
        entitySprite.add(() -> entity.getCurrentHealth() <= 0, new ImageDisplayable(new Point(0, 0), ImageLoader.getDeadEntityImage()));

        entityDisplayable.add(entitySprite);
        entityDisplayable.add(new EntityDirectionDisplayable(new Point(-4, -4), entity, ImageLoader.getDirectionIndicatorImages()));

        return entityDisplayable;
    }

    public static Displayable getTrapDisplayable(Trap trap)
    {
        ConditionalDisplayable trapSprite = new ConditionalDisplayable(new Point(22, 22),
                new ImageDisplayable(new Point(0, 0), new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)));
        trapSprite.add(trap::getIsVisible, new ImageDisplayable(new Point(0, 0), ImageLoader.getSkullDecalImage()));
        return trapSprite;
    }

    public static void addToEntityToAvatarMap(Entity entity, int index) {
        entityToAvatarMap.put(entity, index);
    }

    // =========== avatar skill configuring =======================
    public static int getSkillIndex() {
        return skillIndex;
    }

    public static void setSkillIndex(int skillIndex) {
        ModelDisplayableFactory.skillIndex = skillIndex;
    }

    private static int skillIndex = 0;

    public static void incrementSkillIndex()
    {
        skillIndex++;
        if(skillIndex >= ImageLoader.getSkillSummary().length)
        {
            skillIndex = 0;
        }
    }

    public static void decrementSkillIndex()
    {
        skillIndex--;
        if(skillIndex < 0)
        {
            skillIndex = ImageLoader.getSkillSummary().length - 1;
        }
    }
    public static Displayable getSkillImageDisplayable() {
        return new ImageDisplayable(new Point(0, 0), ImageLoader.getSkillSummary()[skillIndex]);
    }

    // ====================== Map Image Management and Items ==========================
    private static Displayable mountainTerrainDisplayable = new ImageDisplayable(new Point(0, 0), ImageLoader.getTerrainImage(Terrain.MOUNTAINS));
    private static Displayable grassTerrainDisplayable = new ImageDisplayable(new Point(0, 0), ImageLoader.getTerrainImage(Terrain.GRASS));
    private static Displayable waterTerrainDisplayable = new ImageDisplayable(new Point(0, 0), ImageLoader.getTerrainImage(Terrain.WATER));
    private static Displayable noneTerrainDisplayable = new ImageDisplayable(new Point(0, 0), ImageLoader.getTerrainImage(Terrain.NONE));

    private static Map<Terrain, Displayable> terrainDisplayables = new HashMap<>();
    static
    {
        terrainDisplayables.put(Terrain.NONE, noneTerrainDisplayable);
        terrainDisplayables.put(Terrain.GRASS, grassTerrainDisplayable);
        terrainDisplayables.put(Terrain.MOUNTAINS, mountainTerrainDisplayable);
        terrainDisplayables.put(Terrain.WATER, waterTerrainDisplayable);
    }

    private static Map<Decal, Displayable> decalDisplayables = new HashMap<>();
    static
    {
        decalDisplayables.put(Decal.SKULL, new ImageDisplayable(new Point(39, 39), ImageLoader.getSkullDecalImage()));
        decalDisplayables.put(Decal.CROSS, new ImageDisplayable(new Point(20, 20), ImageLoader.getCrossDecalImage()));
        decalDisplayables.put(Decal.STAR, new ImageDisplayable(new Point(31, 31), ImageLoader.getStarDecalImage()));
    }

    private static Displayable itemDisplayable = new ImageDisplayable(new Point(22, 22), ImageLoader.getDefaultItemImage());
    private static Displayable doorwayDisplayable = new ImageDisplayable(new Point(10, 10), ImageLoader.getDoorItemImage());
    private static Displayable closedDoorDisplayable = new ImageDisplayable(new Point(10, 10), ImageLoader.getClosedDoorItemImage());
    private static Displayable keyDisplayable = new ImageDisplayable(new Point(15, 20), ImageLoader.getKeyItemImage());
    private static Displayable damagePotionDisplayable = new ImageDisplayable(new Point(26, 26), ImageFactory.makeFilledTriangleUp(10, 10, Color.RED));
    private static Displayable healthPotionDisplayable = new ImageDisplayable(new Point(26, 26), ImageLoader.getHealthPotion());
    //Scout items
    private static Displayable cobraBow = new ImageDisplayable(new Point(10,10), ImageLoader.getScoutItem(0));
    private static Displayable porcupineTail = new ImageDisplayable(new Point(10,10), ImageLoader.getScoutItem(1));
    private static Displayable rocks = new ImageDisplayable(new Point(10,10), ImageLoader.getScoutItem(2));
    //Smasher items
    private static Displayable bodyBlade = new ImageDisplayable(new Point(10,10), ImageLoader.getSmasherItem(0));
    private static Displayable bodyPepperPelt = new ImageDisplayable(new Point(10,10), ImageLoader.getSmasherItem(1));
    private static Displayable bodySpike = new ImageDisplayable(new Point(10,10), ImageLoader.getSmasherItem(2));
    private static Displayable metalTail = new ImageDisplayable(new Point(10,10), ImageLoader.getSmasherItem(3));
    private static Displayable poisonTail = new ImageDisplayable(new Point(10,10), ImageLoader.getSmasherItem(4));
    private static Displayable tailBarbs = new ImageDisplayable(new Point(10,10), ImageLoader.getSmasherItem(5));
    //Summoner items
    private static Displayable blueScroll = new ImageDisplayable(new Point(10,10), ImageLoader.getSummonerItem(0));
    private static Displayable greenScroll = new ImageDisplayable(new Point(10,10), ImageLoader.getSummonerItem(1));
    private static Displayable orangeScroll = new ImageDisplayable(new Point(10,10), ImageLoader.getSummonerItem(2));
    private static Displayable broom = new ImageDisplayable(new Point(10,10), ImageLoader.getSummonerItem(3));
    private static Displayable staff1 = new ImageDisplayable(new Point(10,10), ImageLoader.getSummonerItem(4));
    private static Displayable woodStaff = new ImageDisplayable(new Point(10,10), ImageLoader.getSummonerItem(5));

    private static Displayable obstacleTreeDisplayable = new ImageDisplayable(new Point(5, 5), ImageLoader.getObstacleTree());
    private static Map<ItemType, Displayable> itemDisplayables = new HashMap<>();
    static
    {
        itemDisplayables.put(ItemType.DEFAULT, itemDisplayable);
        itemDisplayables.put(ItemType.OPEN_DOOR, doorwayDisplayable);
        itemDisplayables.put(ItemType.DOOR, closedDoorDisplayable);
        itemDisplayables.put(ItemType.KEY, keyDisplayable);
        itemDisplayables.put(ItemType.DAMAGE_POTION, damagePotionDisplayable);
        itemDisplayables.put(ItemType.HEALTH_POTION, healthPotionDisplayable);
        itemDisplayables.put(ItemType.COBRA_BOW, cobraBow);
        itemDisplayables.put(ItemType.PORCUPINE_TAIL, porcupineTail);
        itemDisplayables.put(ItemType.BODY_BLADE, bodyBlade);
        itemDisplayables.put(ItemType.BODY_PEPPER_PELT, bodyPepperPelt);
        itemDisplayables.put(ItemType.ROCKS, rocks);
        itemDisplayables.put(ItemType.BODY_SPIKE, bodySpike);
        itemDisplayables.put(ItemType.METAL_TAIL, metalTail);
        itemDisplayables.put(ItemType.POISON_TAIL, poisonTail);
        itemDisplayables.put(ItemType.TAIL_BARBS, tailBarbs);
        itemDisplayables.put(ItemType.BLUE_SCROLL, blueScroll);
        itemDisplayables.put(ItemType.GREEN_SCROLL, greenScroll);
        itemDisplayables.put(ItemType.ORANGE_SCROLL, orangeScroll);
        itemDisplayables.put(ItemType.BROOM, broom);
        itemDisplayables.put(ItemType.STAFF1, staff1);
        itemDisplayables.put(ItemType.WOOD_STAFF, woodStaff);


    }

    public static Displayable getTerrainDisplayable(Terrain terrain) { return terrainDisplayables.get(terrain); }

    public static Displayable getDecalDisplayable(Decal decal) { return decalDisplayables.get(decal); }
    public static Displayable getItemDisplayable(Item item) { return itemDisplayables.getOrDefault(item.getItemType(), itemDisplayable); }
    public static Displayable getObstacleDisplayable(Obstacle obstacle) { return obstacleTreeDisplayable; }
}
