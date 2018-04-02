package com.MeanTeam.gameview;

import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.util.ImageFactory;
import com.MeanTeam.util.Orientation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader
{
    private static final String assetsDir = "assets/";

    private static BufferedImage mountainTerrainImage = loadImage(assetsDir + "newStuff/mountain1.png");//makeBorderedRect(64, 64, Color.GRAY);
    private static BufferedImage grassTerrainImage = loadImage(assetsDir + "newStuff/grass1.png");//makeBorderedRect(64, 64, Color.GREEN);
    private static BufferedImage waterTerrainImage = loadImage(assetsDir + "newStuff/water1.png");//makeBorderedRect(64, 64, Color.BLUE);
    private static BufferedImage noneTerrainImage = ImageFactory.makeRect(64, 64, Color.BLACK);

    private static BufferedImage skullDecalImage = loadImage(assetsDir + "newStuff/skullDecal.png");
    private static BufferedImage crossDecalImage = loadImage(assetsDir + "newStuff/smallRedCross.png");
    private static BufferedImage starDecalImage = loadImage(assetsDir + "newStuff/starDecal.png");

    private static BufferedImage obstacleTree = loadImage(assetsDir + "newStuff/tree2.png");

    private static BufferedImage defaultItemImage = loadImage(assetsDir + "itempouch.png");
    private static BufferedImage doorItemImage = loadImage(assetsDir + "newStuff/closed_door.png");
    private static BufferedImage closedDoorItemImage = loadImage(assetsDir + "newStuff/closed_locked_door.png");
    private static BufferedImage keyItemImage = loadImage(assetsDir + "newStuff/key2.png");

    private static BufferedImage healthPotion = loadImage(assetsDir + "newStuff/healthLg.png");
    private static BufferedImage staminaPotion = loadImage(assetsDir + "newStuff/manaLg.png");

    private static BufferedImage[] scoutItems;
    static
    {
        scoutItems = new BufferedImage[]
                {
                        loadImage(assetsDir + "newStuff/cobraBow.png"),
                        loadImage(assetsDir + "newStuff/porcupineTail.png"),
                        loadImage(assetsDir + "newStuff/rocks.png")

                };
    }

    private static BufferedImage[] smasherItems;
    static
    {
        smasherItems = new BufferedImage[]
                {
                        loadImage(assetsDir + "newStuff/bodyBlade.png"),
                        loadImage(assetsDir + "newStuff/bodyPepperPelt.png"),
                        loadImage(assetsDir + "newStuff/bodySpike.png"),
                        loadImage(assetsDir + "newStuff/metalTail.png"),
                        loadImage(assetsDir + "newStuff/poisonTail.png"),
                        loadImage(assetsDir + "newStuff/tailBarbs.png")

                };
    }

    private static BufferedImage[] summonerItems;
    static
    {
        summonerItems = new BufferedImage[]
                {
                        loadImage(assetsDir + "newStuff/blueScroll.png"),
                        loadImage(assetsDir + "newStuff/greenScroll.png"),
                        loadImage(assetsDir + "newStuff/orangeScroll.png"),
                        loadImage(assetsDir + "newStuff/broom.png"),
                        loadImage(assetsDir + "newStuff/staff1.png"),
                        loadImage(assetsDir + "newStuff/woodStaff.png"),
                };
    }

    private static BufferedImage[] skillSummary;
    static
    {
        skillSummary = new BufferedImage[]
                {
                        loadImage(assetsDir + "smasherCard.png"),
                        loadImage(assetsDir + "summonerCard.png"),
                        loadImage(assetsDir + "scoutCard.png")
                };
    }

    private static BufferedImage[] entityImages;
    static
    {
        entityImages = new BufferedImage[]
                {
                        loadImage(assetsDir + "13i.png"),
                        loadImage(assetsDir + "3i.png"),
                        loadImage(assetsDir + "14i.png"),
                        loadImage(assetsDir + "1i.png"),
                        loadImage(assetsDir + "2i.png"),
                        /*loadImage(assetsDir + "4i.png"),
                        loadImage(assetsDir + "5i.png"),
                        loadImage(assetsDir + "6i.png"),
                        loadImage(assetsDir + "7i.png"),
                        loadImage(assetsDir + "8i.png"),
                        loadImage(assetsDir + "9i.png"),
                        loadImage(assetsDir + "10i.png"),
                        loadImage(assetsDir + "11i.png"),
                        loadImage(assetsDir + "12i.png"),
                        loadImage(assetsDir + "15i.png"),*/
                };
    }

    private static BufferedImage[] entityPedigree;
    static
    {
        entityPedigree = new BufferedImage[]
                {
                        loadImage(assetsDir + "13.png"),
                        loadImage(assetsDir + "3.png"),
                        loadImage(assetsDir + "14.png"),
                        loadImage(assetsDir + "1.png"),
                        loadImage(assetsDir + "2.png"),
                        /*loadImage(assetsDir + "4.png"),
                        loadImage(assetsDir + "5.png"),
                        loadImage(assetsDir + "6.png"),
                        loadImage(assetsDir + "7.png"),
                        loadImage(assetsDir + "8.png"),
                        loadImage(assetsDir + "9.png"),
                        loadImage(assetsDir + "10.png"),
                        loadImage(assetsDir + "11.png"),
                        loadImage(assetsDir + "12.png"),
                        loadImage(assetsDir + "15.png"),*/
                };
    }

    private static BufferedImage deadEntityImage = loadImage(assetsDir + "newStuff/deadEntity1.png");

    /*private static BufferedImage[] directionIndicatorImages;
    static
    {
        directionIndicatorImages = new BufferedImage[]
                {
                        loadImage(assetsDir + "indicatorn.png"),
                        loadImage(assetsDir + "indicatornw.png"),
                        loadImage(assetsDir + "indicatorw.png"),
                        loadImage(assetsDir + "indicatorsw.png"),
                        loadImage(assetsDir + "indicators.png"),
                        loadImage(assetsDir + "indicatorse.png"),
                        loadImage(assetsDir + "indicatore.png"),
                        loadImage(assetsDir + "indicatorne.png")
                };
    }*/
    private static EnumMap<Orientation, BufferedImage> directionIndicatorImages;
    static
    {
        Map<Orientation, BufferedImage> map = new HashMap<>();
        map.put(Orientation.NULL, loadImage(assetsDir + "indicatorn.png"));
        map.put(Orientation.NORTH, loadImage(assetsDir + "indicatorn.png"));
        map.put(Orientation.NORTHWEST, loadImage(assetsDir + "indicatornw.png"));
        map.put(Orientation.WEST, loadImage(assetsDir + "indicatorw.png"));
        map.put(Orientation.SOUTHWEST, loadImage(assetsDir + "indicatorsw.png"));
        map.put(Orientation.SOUTH, loadImage(assetsDir + "indicators.png"));
        map.put(Orientation.SOUTHEAST, loadImage(assetsDir + "indicatorse.png"));
        map.put(Orientation.EAST, loadImage(assetsDir + "indicatore.png"));
        map.put(Orientation.NORTHEAST, loadImage(assetsDir + "indicatorne.png"));
        directionIndicatorImages = new EnumMap<Orientation, BufferedImage>(map);

                        /*loadImage(assetsDir + "indicatorn.png"),
                        loadImage(assetsDir + "indicatornw.png"),
                        loadImage(assetsDir + "indicatorw.png"),
                        loadImage(assetsDir + "indicatorsw.png"),
                        loadImage(assetsDir + "indicators.png"),
                        loadImage(assetsDir + "indicatorse.png"),
                        loadImage(assetsDir + "indicatore.png"),
                        loadImage(assetsDir + "indicatorne.png")*/
    }


    private static Map<Terrain, BufferedImage> terrainMap = new HashMap<>();
    static
    {
        terrainMap.put(Terrain.NONE, noneTerrainImage);
        terrainMap.put(Terrain.GRASS, grassTerrainImage);
        terrainMap.put(Terrain.MOUNTAINS, mountainTerrainImage);
        terrainMap.put(Terrain.WATER, waterTerrainImage);
    }

    public static BufferedImage getTerrainImage(Terrain terrain)
    {
        return terrainMap.get(terrain);
    }

    public static BufferedImage[] getEntityImages() { return entityImages; }
    public static BufferedImage getDeadEntityImage() { return deadEntityImage; }
    //public static BufferedImage[] getDirectionIndicatorImages() { return directionIndicatorImages; }
    public static EnumMap<Orientation, BufferedImage> getDirectionIndicatorImages() { return directionIndicatorImages; }

    public static BufferedImage getSkullDecalImage() { return skullDecalImage; }
    public static BufferedImage getCrossDecalImage() { return crossDecalImage; }
    public static BufferedImage getStarDecalImage() { return starDecalImage; }

    public static BufferedImage getObstacleTree() { return obstacleTree; }

    public static BufferedImage getDefaultItemImage() { return defaultItemImage; }
    public static BufferedImage getDoorItemImage() { return doorItemImage; }
    public static BufferedImage getClosedDoorItemImage() { return closedDoorItemImage; }
    public static BufferedImage getKeyItemImage() { return keyItemImage; }

    public static BufferedImage getHealthPotion() { return healthPotion; }
    public static BufferedImage getStaminaPotion() { return staminaPotion; }

    public static BufferedImage getScoutItem(int index) { return scoutItems[index]; }
    public static BufferedImage getSmasherItem(int index) { return smasherItems[index]; }
    public static BufferedImage getSummonerItem(int index) { return summonerItems[index]; }

    private static BufferedImage loadImage(String path)
    {
        try
        {
            return ImageIO.read(new File(path));
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage getArrow() {
        return loadImage(assetsDir + "InventorySelector.png");
    }

    public static BufferedImage[] getSkillSummary() { return skillSummary; }

    public static BufferedImage getSkillSummary(int i) { return skillSummary[i]; }

    public static BufferedImage getEntityPedigree(int i) { return entityPedigree[i]; }
}
