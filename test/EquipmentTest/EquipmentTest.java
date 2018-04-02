package EquipmentTest;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.effectcommand.DamageEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.HealEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.ToggleableEventCommand;
import com.MeanTeam.gamemodel.tile.effectcommand.ToggleableHealthEvent;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.skills.SkillPool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EquipmentTest {

    @Before
    public void resetGameModelNotifier() {
        GameModelNotifier.destroyInstance();
    }

    @Test
    public void testItemEquip(){

        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        ArrayList<World> worlds = new ArrayList<>();
        worlds.add(world);

        GameModel model = new GameModel(worlds , new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), world, null);

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

        Entity e1 = new Entity(100, 100, 2, 0, null, new Inventory(10, gameModelNotifier), worldNotifier,
                new WeaponItem("weapon", ItemType.DEFAULT, worldNotifier, 0,
                0, 0, 0, 1, new DamageEvent(10),
                SkillPool.damageRing, gameModelNotifier));

        model.initialize(0, e1);

        e1.addWeaponSkill(SkillPool.damageRing, 1);

        TakeableItem weapon = new WeaponItem("weapon", ItemType.DEFAULT, worldNotifier, 0,
                                            0, 0, 0, 1, new DamageEvent(10),
                                            SkillPool.damageRing, gameModelNotifier);
        TakeableItem armor = new ArmorItem("armor", new ToggleableHealthEvent(50), ItemType.CHESTPLATE, worldNotifier, gameModelNotifier);
        TakeableItem ring = new RingItem("armor", new ToggleableHealthEvent(25), ItemType.BUFFABLERING, worldNotifier, gameModelNotifier);

        Assert.assertEquals(e1.getMaxHealth(), 100, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);

        Assert.assertNull(e1.getRing());
        Assert.assertNull(e1.getArmor());

        armor.equipToPlayer();

        Assert.assertEquals(e1.getArmor(), armor);

        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getArmor(), armor);
        Assert.assertEquals(e1.getBackpack().hasItem(armor), false);

        e1.unequipArmor();
        Assert.assertEquals(e1.getMaxHealth(), 100, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getArmor(), null);
        Assert.assertEquals(e1.getBackpack().hasItem(armor), true);

        armor.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getArmor(), armor);
        Assert.assertEquals(e1.getBackpack().hasItem(armor), false);

        TakeableItem armor2 = new ArmorItem("armor2", new ToggleableHealthEvent(25), ItemType.CHESTPLATE, worldNotifier, gameModelNotifier);

        armor2.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 125, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getArmor(), armor2);
        Assert.assertEquals(e1.getBackpack().hasItem(armor2), false);
        Assert.assertEquals(e1.getBackpack().hasItem(armor), true);

        armor.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getArmor(), armor);
        Assert.assertEquals(e1.getBackpack().hasItem(armor), false);
        Assert.assertEquals(e1.getBackpack().hasItem(armor2), true);

        TakeableItem weapon2 = new WeaponItem("weapon", ItemType.DEFAULT, worldNotifier, 0,
                                              0, 0, 0, 1, new DamageEvent(10),
                                              SkillPool.damageRing, gameModelNotifier);

        weapon.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getWeapon(), weapon);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon), false);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon2), false);

        e1.unequipWeapon();
        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon), true);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon2), false);

        weapon2.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getWeapon(), weapon2);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon), true);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon2), false);

        weapon.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getWeapon(), weapon);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon), false);
        Assert.assertEquals(e1.getBackpack().hasItem(weapon2), true);

        TakeableItem ring2 = new RingItem("armor", new ToggleableHealthEvent(40), ItemType.BUFFABLERING, worldNotifier, gameModelNotifier);

        ring.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 175, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getRing(), ring);
        Assert.assertEquals(e1.getBackpack().hasItem(ring), false);
        Assert.assertEquals(e1.getBackpack().hasItem(ring2), false);

        e1.unequipRing();
        Assert.assertEquals(e1.getMaxHealth(), 150, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getRing(), null);
        Assert.assertEquals(e1.getBackpack().hasItem(ring), true);
        Assert.assertEquals(e1.getBackpack().hasItem(ring2), false);

        ring2.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 190, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getRing(), ring2);
        Assert.assertEquals(e1.getBackpack().hasItem(ring), true);
        Assert.assertEquals(e1.getBackpack().hasItem(ring2), false);

        ring.equipToPlayer();
        Assert.assertEquals(e1.getMaxHealth(), 175, 0);
        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(e1.getRing(), ring);
        Assert.assertEquals(e1.getBackpack().hasItem(ring), false);
        Assert.assertEquals(e1.getBackpack().hasItem(ring2), true);
    }

    @Test
    public void testConsumableUse(){

        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        ArrayList<World> worlds = new ArrayList<>();

        worlds.add(world);

        GameModel model = new GameModel(worlds, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), world, null);

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

        Entity e1 = new Entity(100, 100, 2, 0, null, new Inventory(10, gameModelNotifier), worldNotifier, null);

        model.initialize(0, e1);

        e1.addWeaponSkill(SkillPool.damageRing, 1);

        TakeableItem healPotion = new ConsumableItem("heal potion", new HealEvent(10), ItemType.HEALTH_POTION, worldNotifier, gameModelNotifier);
        TakeableItem hurtPotion = new ConsumableItem("hurt potion", new DamageEvent(15), ItemType.DAMAGE_POTION, worldNotifier, gameModelNotifier);

        // making sure you have the consumable in your inventory before you can use it (so you can't repeatedly use it)
        healPotion.equipToPlayer();
        hurtPotion.equipToPlayer();

        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);

        e1.addItemToBackpack(healPotion);
        e1.addItemToBackpack(hurtPotion);

        Assert.assertTrue(e1.hasItemInBackpack(healPotion));
        Assert.assertTrue(e1.hasItemInBackpack(healPotion));

        Assert.assertEquals(e1.getCurrentHealth(), 100, 0);

        hurtPotion.equipToPlayer();

        Assert.assertEquals(e1.getCurrentHealth(), 85, 0);

        healPotion.equipToPlayer();

        Assert.assertEquals(e1.getCurrentHealth(), 95, 0);

        hurtPotion.equipToPlayer();
        healPotion.equipToPlayer();

        Assert.assertEquals(e1.getCurrentHealth(), 95, 0);

        Assert.assertFalse(e1.hasItemInBackpack(healPotion));
        Assert.assertFalse(e1.hasItemInBackpack(healPotion));
    }
}
