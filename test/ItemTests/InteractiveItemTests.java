package ItemTests;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.effectcommand.DamageEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.effectcommand.ConditionalTransitionEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.TransitionEvent;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.skills.SkillPool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InteractiveItemTests {

    @Before
    public void resetGameModelNotifier() {
        GameModelNotifier.destroyInstance();
    }

    @Test
    public void testTransitionEvent(){
        ArrayList<World> worlds = new ArrayList<>();
        ArrayList<WorldNotifier> worldNotifiers = new ArrayList<>();
        World world1 = new World();
        World world2 = new World();
        World world3 = new World();
        WorldNotifier worldNotifier1 = new WorldNotifier(world1);
        WorldNotifier worldNotifier2 = new WorldNotifier(world2);
        WorldNotifier worldNotifier3 = new WorldNotifier(world3);
        worlds.add(world1);
        worlds.add(world2);
        worlds.add(world3);
        worldNotifiers.add(worldNotifier1);
        worldNotifiers.add(worldNotifier2);
        worldNotifiers.add(worldNotifier3);


        GameModel game = new GameModel(worlds, worldNotifiers, null, null, worlds.get(0), null);

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(game);

        //create an entitiy and basic game setup
        Entity entity = new Entity(100, 100, 2, 0, null, new Inventory(10, gameModelNotifier), worldNotifier1, new WeaponItem("weapon", ItemType.DEFAULT, worldNotifier1, 0,
                0, 0, 0, 1, new DamageEvent(10),
                SkillPool.damageRing, null));

        game.initialize(0, entity);

        game.changeWorld(0, new Point(0, 0));

        //create effect and item to test TransitionEvent
        EventCommand effect = new TransitionEvent(1, new Point(0, 0), gameModelNotifier);
        Item item = new InteractiveItem(effect, worldNotifier1);

        //Assert the player is in the correct location
        Assert.assertTrue(worlds.get(1).getEntityLocation(entity) == null);
        Point oldLocation = worlds.get(0).getEntityLocation(entity);
        Assert.assertTrue(oldLocation.getX() == 0 && oldLocation.getY() == 0);

        //trigger TransitionEvent
        item.trigger(entity, true);

        //Assert the player transitioned properly
        Assert.assertTrue(worlds.get(0).getEntityLocation(entity) == null);
        Point newLocation = worlds.get(1).getEntityLocation(entity);
        Assert.assertTrue(newLocation.getX() == 0 && newLocation.getY() == 0);
    }

    @Test
    public void testConditionalTransitionEvent(){
        ArrayList<World> worlds = new ArrayList<>();
        ArrayList<WorldNotifier> worldNotifiers = new ArrayList<>();
        World world1 = new World();
        World world2 = new World();
        World world3 = new World();
        WorldNotifier worldNotifier1 = new WorldNotifier(world1);
        WorldNotifier worldNotifier2 = new WorldNotifier(world2);
        WorldNotifier worldNotifier3 = new WorldNotifier(world3);
        worlds.add(world1);
        worlds.add(world2);
        worlds.add(world3);
        worldNotifiers.add(worldNotifier1);
        worldNotifiers.add(worldNotifier2);
        worldNotifiers.add(worldNotifier3);

        GameModel game = new GameModel(worlds, worldNotifiers, null, null, world1, null);
        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(game);

        //create an entitiy and basic game setup
        Entity entity = new Entity(100, 100, 2, 0, null, new Inventory(10, gameModelNotifier), worldNotifier1, new WeaponItem("weapon", ItemType.DEFAULT, worldNotifier1, 0,
                0, 0, 0, 1, new DamageEvent(10),
                SkillPool.damageRing, gameModelNotifier));

        game.initialize(0, entity);

        game.changeWorld(0, new Point(0, 0));

        //create effect and item to test TransitionEvent
        TakeableItem key = new TakeableItem("key", ItemType.KEY, worldNotifier1, gameModelNotifier);
        EventCommand effect = new ConditionalTransitionEvent(1, new Point(0, 0), gameModelNotifier, key);
        Item item = new InteractiveItem(effect, new WorldNotifier(world1));

        //Assert the player is in the correct location
        Assert.assertTrue(worlds.get(1).getEntityLocation(entity) == null);
        Point location = worlds.get(0).getEntityLocation(entity);
        Assert.assertTrue(location.getX() == 0 && location.getY() == 0);

        //trigger ConditionalTransitionEvent
        item.trigger(entity, true);

        //Assert the player has not transitioned since they do not have the required item
        Assert.assertTrue(worlds.get(1).getEntityLocation(entity) == null);
        location = worlds.get(0).getEntityLocation(entity);
        Assert.assertTrue(location.getX() == 0 && location.getY() == 0);

        //give the player the required item and trigger the ConditionalTransitionEvent again
        entity.addItemToBackpack(key);
        Assert.assertTrue(entity.hasItemInBackpack(key));
        item.trigger(entity, true);

        //Assert the player has transitioned
        Assert.assertTrue(worlds.get(0).getEntityLocation(entity) == null);
        location = worlds.get(1).getEntityLocation(entity);
        Assert.assertTrue(location.getX() == 0 && location.getY() == 0);
        Assert.assertTrue(!entity.hasItemInBackpack(key));
    }
}
