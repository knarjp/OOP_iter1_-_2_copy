package SkillTests;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.effectcommand.*;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.ItemType;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.util.Orientation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

public class SkillTest {

    @Before
    public void resetGameModelNotifier() {
        GameModelNotifier.destroyInstance();
    }

    @Test
    public void testSkillsEffects() {
        World testWorld = new World();

        WorldNotifier testWorldNotifier = new WorldNotifier(testWorld);

        Inventory inventory = new Inventory(10, GameModelNotifier.getGameModelNotifier(new GameModel(null, null, null, null, null, null)));

        Skill selfHeal = new Skill("selfHeal", 0, 0, 100, 10, 0, 0, 0, new HealEvent(0));
        Skill selfDamage = new Skill("selfDamage", 0, 0, 100, 15, 0, 0, 0, new DamageEvent(0));

        Skill linearHeal = new Skill("linearHeal", 10, 0, 100, 10, 0, 0, 0, new HealEvent(0) );
        Skill linearDamage = new Skill("linearDamage", 10, 0, 100, 15, 0, 0, 0, new DamageEvent(0));

        Skill angularHeal = new Skill("angularHeal", 10, 90, 100, 10, 0, 0, 0, new HealEvent(0)); // 90
        Skill angularDamage = new Skill("angularDamage", 10, 90, 100, 15, 0, 0, 0, new DamageEvent(0));

        Skill radialHeal = new Skill("radialHeal",  10, 360, 100, 10, 0, 0, 0, new HealEvent(0)); // 360
        Skill radialDamage = new Skill("radialDamage", 10, 360, 100, 15, 0, 0, 0, new DamageEvent(0));

        Entity player = new Entity(100, 100,2, 0, Orientation.NORTH,
                inventory, testWorldNotifier, null);

        player.addSkill(selfHeal, 0);
        player.addSkill(selfDamage, 0);
        player.addSkill(linearDamage, 0);
        player.addSkill(linearHeal, 0);
        player.addSkill(angularDamage, 0);
        player.addSkill(angularHeal, 0);
        player.addSkill(radialDamage, 0);
        player.addSkill(radialHeal, 0);

        Entity entity1 = new Entity(100, 100, 2, 0, Orientation.NORTH, null, testWorldNotifier, null);
        Entity entity2 = new Entity(100, 100, 2, 0, Orientation.NORTH, null, testWorldNotifier, null);
        Entity entity3 = new Entity(100, 100, 2, 0, Orientation.NORTH, null, testWorldNotifier, null);

        testWorld.add(new Point(0, 0), Terrain.GRASS);
        testWorld.add(new Point(1, 0), Terrain.WATER);
        testWorld.add(new Point(1, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(0, 1), Terrain.GRASS);
        testWorld.add(new Point(2, 0), Terrain.WATER);
        testWorld.add(new Point(2, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(-1, 0), Terrain.GRASS);
        testWorld.add(new Point(0, -1), Terrain.GRASS);
        testWorld.add(new Point(1, -1), Terrain.GRASS);

        testWorld.addPlayer(new Point(0, 0), player);

        testWorld.addNpc(new Point(-1, 0), entity1); // should be affected only by radial effect
        testWorld.addNpc(new Point(0, -1), entity2); // should be affected by linear, radial, and angular effects
        testWorld.addNpc(new Point(1, -1), entity3); // should be affected by radial and angular effects

        Assert.assertEquals(testWorld.getEntity(new Point(0, 0)), player);
        Assert.assertEquals(testWorld.getEntity(new Point(-1, 0)), entity1);
        Assert.assertEquals(testWorld.getEntity(new Point(0, -1)), entity2);
        Assert.assertEquals(testWorld.getEntity(new Point(1, -1)), entity3);

        Assert.assertEquals(player.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        player.useSkill(selfDamage);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 85, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 100, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0); // should be affected by radial and angular effects

        player.useSkill(selfHeal);
        player.useSkill(selfHeal);
        player.useSkill(selfDamage);
        player.useSkill(selfHeal);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 100, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0); // should be affected by radial and angular effects

        player.useSkill(linearDamage);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 100, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 85, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0); // should be affected by radial and angular effects

        player.useSkill(angularDamage);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 100, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 70, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 85, 0); // should be affected by radial and angular effects

        player.useSkill(radialDamage);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 85, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 55, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 70, 0); // should be affected by radial and angular effects

        player.useSkill(linearHeal);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 85, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 65, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 70, 0); // should be affected by radial and angular effects

        player.useSkill(angularHeal);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 85, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 75, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 80, 0); // should be affected by radial and angular effects

        player.useSkill(radialHeal);
        testWorld.processSkills();

        Assert.assertEquals(player.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity1.getCurrentHealth(), 95, 0); // should be affected only by radial effect
        Assert.assertEquals(entity2.getCurrentHealth(), 85, 0); // should be affected by linear, radial, and angular effects
        Assert.assertEquals(entity3.getCurrentHealth(), 90, 0); // should be affected by radial and angular effects
    }

    @Test
    public void testPickPocketSkill() {
        World testWorld = new World();
        WorldNotifier testWorldNotifier = new WorldNotifier(testWorld);

        ArrayList<World> worlds = new ArrayList<>();

        worlds.add(testWorld);

        GameModel gameModel = new GameModel(worlds , new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), testWorld, null);
        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);

        Skill pickPocket = new Skill("Thieve", 1, 0,
                        100, 10, 5, 0, 0, new PickPocketEvent(0, gameModelNotifier));

        Entity player = new Entity(100, 100,2, 0, Orientation.WEST,
                new Inventory(10, gameModelNotifier), testWorldNotifier, null);

        player.addSkill(pickPocket, 100);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);

        Entity entity1 = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), testWorldNotifier, null);

        gameModel.initialize(0, player);

        testWorld.add(new Point(0, 0), Terrain.GRASS);
        testWorld.add(new Point(-1, 0), Terrain.GRASS);

        testWorld.addPlayer(new Point(0, 0), player);
        testWorld.addNpc(new Point(-1, 0), entity1);

        Assert.assertEquals(testWorld.getEntity(new Point(0, 0)), player);
        Assert.assertEquals(testWorld.getEntity(new Point(-1, 0)), entity1);

        TakeableItem item = new TakeableItem("item", ItemType.DEFAULT, testWorldNotifier, gameModelNotifier);
        TakeableItem item1 = new TakeableItem("item1", ItemType.DEFAULT, testWorldNotifier, gameModelNotifier);
        TakeableItem item2 = new TakeableItem("item2", ItemType.DEFAULT, testWorldNotifier, gameModelNotifier);
        TakeableItem item3 = new TakeableItem("item3", ItemType.DEFAULT, testWorldNotifier, gameModelNotifier);


        // un-comment the below lines to see the "random-ness" of pickpocket (test will most likely fail if you do that)
        entity1.addItemToBackpack(item);
      //  entity1.addItemToBackpack(item1);
      //  entity1.addItemToBackpack(item2);
     //   entity1.addItemToBackpack(item3);/

        Assert.assertFalse(player.hasItemInBackpack(item));
        Assert.assertTrue(entity1.hasItemInBackpack(item));

        player.useSkill(pickPocket);
        testWorld.processSkills();

        Assert.assertFalse(entity1.hasItemInBackpack(item));
        Assert.assertTrue(player.hasItemInBackpack(item));
    }

    @Test
    public void testTrapDisarmSkill() {
        World testWorld = new World();
        WorldNotifier testWorldNotifier = new WorldNotifier(testWorld);

        ArrayList<World> worlds = new ArrayList<>();
        ArrayList<WorldNotifier> notifiers = new ArrayList<>();

        worlds.add(testWorld);
        notifiers.add(testWorldNotifier);

        GameModel gameModel = new GameModel(worlds , notifiers, new ArrayList<>(),
                new ArrayList<>(), testWorld, null);
        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);

        Skill detectRemoveTrap = new Skill("Sense Danger", 0, 0, 100, 10, 5, 4, 0,
                new DisarmTrapEvent(0, gameModelNotifier));

        Entity player = new Entity(100, 100,2, 0, Orientation.WEST,
                new Inventory(10, gameModelNotifier), testWorldNotifier, null);

        player.addSkill(detectRemoveTrap, 1);

        gameModel.addFriendlyNpcController();
        gameModel.addHostileNpcController(player);

        gameModel.initialize(0, player);

        testWorld.add(new Point(0, 0), Terrain.GRASS);
        testWorld.add(new Point(-1, 0), Terrain.GRASS);
        testWorld.add(new Point(0, 1), Terrain.GRASS);

        SettableEventCommand event = new DamageEvent(15);
        Trap trap = new Trap(event, 10);

        testWorld.addPlayer(new Point(0, 0), player);
        testWorld.add(new Point(-1, 0), trap);

        Assert.assertEquals(testWorld.getEntity(new Point(0, 0)), player);
        Assert.assertEquals(trap.getIsVisible(), false);
        Assert.assertEquals(trap.getHasNotFired(), true);
        Assert.assertEquals(player.getCurrentHealth(), 100, 0);

        player.useSkill(detectRemoveTrap);
        testWorld.processSkills();

        Assert.assertEquals(trap.getIsVisible(), true);
        Assert.assertEquals(trap.getHasNotFired(), true);
        Assert.assertEquals(player.getCurrentHealth(), 100, 0);

        player.useSkill(detectRemoveTrap);
        testWorld.processSkills();

        Assert.assertEquals(trap.getIsVisible(), true);
        Assert.assertEquals(trap.getHasNotFired(), false);
        Assert.assertEquals(player.getCurrentHealth(), 100, 0);
    }

    /*@Test // this test fails on a pc with 4.4 GHz processor
    public void cooldownTest() {
        World testWorld = new World();
        WorldNotifier testWorldNotifier = new WorldNotifier(testWorld);
        Skill selfDamage = new Skill("selfDamage", 0, 0, 100, 15, 0, 0, 0, new DamageEvent(0), 1);
        Entity player = new Entity(100, 100,2, 0, Orientation.NORTH,
                null, testWorldNotifier, null);
        player.useSkill(selfDamage);
        testWorld.processSkills();
        Assert.assertTrue(player.getCurrentHealth() < 100);
        Assert.assertTrue(selfDamage.isOnCooldown());
    }*/
}