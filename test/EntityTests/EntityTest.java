package EntityTests;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.effectcommand.DamageEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.HealEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.SettableEventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.ItemType;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.gamemodel.tile.items.WeaponItem;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.skills.SkillPool;
import com.MeanTeam.util.Orientation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.MeanTeam.gamemodel.tile.skills.SkillPool.damageRing;

public class EntityTest {

    @Before
    public void resetGameModelNotifier() {
        GameModelNotifier.destroyInstance();
    }

    @Test
    public void testEntityLeveling() {
        Entity entity = new Entity(100, 100, 2, 0, null, null,null, null);

        Assert.assertEquals(entity.getExperience(), 0);
        Assert.assertEquals(entity.getLevel(), 0);

        entity.increaseExperience(100);

        Assert.assertEquals(entity.getExperience(), 100);
        Assert.assertEquals(entity.getLevel(), 1);

        entity.increaseExperience(54);

        Assert.assertEquals(entity.getExperience(), 154);
        Assert.assertEquals(entity.getLevel(), 1);

        entity.increaseExperience(47);

        Assert.assertEquals(entity.getExperience(), 201);
        Assert.assertEquals(entity.getLevel(), 2);

        entity.increaseExperience(500);
        Assert.assertEquals(entity.getExperience(), 701);
        Assert.assertEquals(entity.getLevel(), 7);
    }

    @Test
    public void testEntityHealthCap() {
        Entity entity = new Entity(104, 100, 2, 0, null, null, null, null);

        entity.decreaseHealth(50);

        Assert.assertEquals(entity.getCurrentHealth(), 54, 0);

        entity.addHealth(100000);

        Assert.assertEquals(entity.getCurrentHealth(), 104, 0);
    }
    /*
    @Test
    public void testWeaponAttack() {
        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        ArrayList<World> worldList = new ArrayList<>();
        worldList.add(world);

        GameModel gameModel = new GameModel(worldList , new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), world, null);
        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);

        WeaponItem defaultWeapon = new WeaponItem("fist", ItemType.SWORD, worldNotifier, 0, 0, 0, 0, 1, new DamageEvent(10), damageRing, gameModelNotifier);

        Entity entity = new Entity(100, 100, 2, 0, null, new Inventory(10, gameModelNotifier), worldNotifier, defaultWeapon);

        gameModel.initialize(0, entity);

        entity.addWeaponSkill(damageRing, 1);
        damageRing.setCooldown(0);
        WeaponItem sword = new WeaponItem("sword", ItemType.SWORD, worldNotifier,
                                            0, 50, 1, 0, 1,
                                            new DamageEvent(0), damageRing, gameModelNotifier);

        entity.addItemToBackpack(sword);
        sword.equipToPlayer();
        Assert.assertTrue(entity.hasEquipped(sword));

        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null, null);
        Entity entity3 = new Entity(100, 100, 2, 0, null, null, null, null);
        Entity entity4 = new Entity(100, 100, 2, 0, null, null, null, null);
        world.addPlayer(new Point(0, 0), entity);
        world.addNpc(new Point(1, 0), entity2);
        world.addNpc(new Point(2, 0), entity3);
        world.addNpc(new Point(1, 1), entity4);
        world.add(new Point(0, 0), Terrain.GRASS);
        world.add(new Point(1, 0), Terrain.GRASS);
        world.add(new Point(2, 0), Terrain.GRASS);
        world.add(new Point(1, 1), Terrain.GRASS);

        entity.setOrientation(Orientation.EAST);

        entity.useWeaponSkill();
        world.processSkills();

        Assert.assertEquals(entity.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 94, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity4.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity.getCurrentStamina(), 50, 0);

        entity.useWeaponSkill();
        world.processSkills();

        Assert.assertEquals(entity.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 88, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity4.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity.getCurrentStamina(), 0, 0);

        entity.useWeaponSkill();
        world.processSkills();

        Assert.assertEquals(entity.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 88, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity4.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity.getCurrentStamina(), 0, 0);

        for(int i = 0; i < 100; ++i) {
            entity.regenStamina();
        }

        WeaponItem healingSword = new WeaponItem("healing sword", ItemType.SWORD, worldNotifier,
                                                0, 25, 1, 0, 1,
                                                new HealEvent(0), damageRing, gameModelNotifier);

        healingSword.equipToPlayer();
        entity.useWeaponSkill();
        world.processSkills();

        Assert.assertEquals(entity.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 94, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity4.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity.getCurrentStamina(), 75, 0);
    }
    */
}