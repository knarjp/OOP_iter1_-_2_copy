package WorldTests;

import com.MeanTeam.gamecontroller.EntityController;
import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.Obstacle;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.areaeffects.AreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.InfiniteAreaEffect;
import com.MeanTeam.gamemodel.tile.effectcommand.DamageEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.HealEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.SettableEventCommand;
import com.MeanTeam.gamemodel.tile.effectcommand.TransitionEvent;
import com.MeanTeam.gamemodel.tile.entities.Entity;
//import com.MeanTeam.gamemodel.tile.entities.Equipment;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.util.Orientation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

public class WorldTest {

    @Before
    public void resetGameModelNotifier() {
        GameModelNotifier.destroyInstance();
    }

    @Test
    public void testAreaEffectInteraction() {
        World testWorld = new World();

        testWorld.add(new Point(0, 0), Terrain.GRASS);
        testWorld.add(new Point(1, 0), Terrain.WATER);
        testWorld.add(new Point(1, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(0, 1), Terrain.GRASS);
        testWorld.add(new Point(2, 0), Terrain.WATER);
        testWorld.add(new Point(2, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(-1, 0), Terrain.GRASS);

        AreaEffect infiniteDamageEffect = new InfiniteAreaEffect(new DamageEvent(13));
        AreaEffect infiniteHealEffect = new InfiniteAreaEffect(new HealEvent(12));

        AreaEffect oneShotDamageEffect = new InfiniteAreaEffect(new DamageEvent(20));
        AreaEffect oneShotHealEffect = new InfiniteAreaEffect(new HealEvent(35));

        Entity entity1 = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(testWorld), null);
        Entity entity2 = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(testWorld),  null);
        Entity entity3 = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(testWorld),  null);

        testWorld.add(new Point(0, 0), infiniteDamageEffect);
        testWorld.add(new Point(1, 0), infiniteDamageEffect);

        testWorld.add(new Point(1, 1), infiniteHealEffect);
        testWorld.add(new Point(0, 1), infiniteHealEffect);

        testWorld.add(new Point(2, 0), oneShotDamageEffect);
        testWorld.add(new Point(2, 1), oneShotHealEffect);

        testWorld.addNpc(new Point(0, 0), entity1);
        testWorld.addNpc(new Point(1, 1), entity2);
        testWorld.addNpc(new Point(-1, 0), entity3);

        Assert.assertEquals(entity1.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.doInteractions(entity1);

        Assert.assertEquals(entity1.getCurrentHealth(), 87, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.doInteractions(entity1);

        Assert.assertEquals(entity1.getCurrentHealth(), 74, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.removeEntity(new Point(0, 0));
        testWorld.removeEntity(new Point(1, 1));

        testWorld.doInteractions(entity1);

        Assert.assertEquals(entity1.getCurrentHealth(), 74, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.addNpc(new Point(1, 1), entity1);
        testWorld.addNpc(new Point(0, 0), entity2);

        testWorld.doInteractions(entity1);

        Assert.assertEquals(entity1.getCurrentHealth(), 86, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 87, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.doInteractions(entity1);

        Assert.assertEquals(entity1.getCurrentHealth(), 98, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 74, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.removeEntity(new Point(1, 1));
        testWorld.removeEntity(new Point(0, 0));

        testWorld.addNpc(new Point(2, 0), entity1);
        testWorld.addNpc(new Point(2, 1), entity2);

        testWorld.doInteractions(entity1);

        Assert.assertEquals(entity1.getCurrentHealth(), 78, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.doInteractions(entity1);

        Assert.assertEquals(entity1.getCurrentHealth(), 58, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);

        testWorld.removeEntity(new Point(2, 0));
        testWorld.removeEntity(new Point(2, 1));

        testWorld.addNpc(new Point(2, 1), entity1);
        testWorld.addNpc(new Point(2, 0), entity2);

        Assert.assertEquals(entity1.getCurrentHealth(), 58, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(entity3.getCurrentHealth(), 100, 0);
    }

    @Test
    public void testItemInteraction() {
        World testWorld = new World();
        World testTransitionWorld = new World();

        ArrayList<World> worlds = new ArrayList<>();
        ArrayList<WorldNotifier> worldNotifiers = new ArrayList<>();

        worlds.add(testWorld);
        worlds.add(testTransitionWorld);

        WorldNotifier testWorldNotifier = new WorldNotifier(testWorld);
        WorldNotifier testTransitionWorldNotifier = new WorldNotifier(testTransitionWorld);

        worldNotifiers.add(testWorldNotifier);
        worldNotifiers.add(testTransitionWorldNotifier);

        GameModel model = new GameModel(worlds, worldNotifiers, new ArrayList<>(),
                new ArrayList<>(), testWorld, null);

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

        Entity entity1 = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), testWorldNotifier, null);
        Entity entity2 = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), testWorldNotifier, null);
        Entity entity3 = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), testWorldNotifier, null);

        model.initialize(0, entity3);

        testWorld.add(new Point(0, 0), Terrain.GRASS);
        testWorld.add(new Point(1, 0), Terrain.WATER);
        testWorld.add(new Point(1, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(0, 1), Terrain.GRASS);
        testWorld.add(new Point(2, 0), Terrain.WATER);
        testWorld.add(new Point(2, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(-1, 0), Terrain.GRASS);

        testTransitionWorld.add(new Point(0, 0), Terrain.GRASS);

        testWorld.addNpc(new Point(0, 0), entity1);
        testWorld.addNpc(new Point(1, 0), entity2);
        testWorld.addNpc(new Point(1, 1), entity3);

        TakeableItem takeable = new TakeableItem("item", ItemType.DEFAULT, testWorldNotifier, gameModelNotifier);
        OneshotItem oneshot = new OneshotItem(new DamageEvent(10), testWorldNotifier);
        InteractiveItem interactive = new InteractiveItem(new TransitionEvent(1, new Point(0,0), gameModelNotifier), testWorldNotifier);

        // called 3 times to simulate each entity being the player
        testWorld.doInteractions(entity1);
        testWorld.doInteractions(entity2);
        testWorld.doInteractions(entity3);

        Assert.assertEquals(entity1.hasItemInBackpack(takeable), false);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
        Assert.assertEquals(testWorld.getEntity(new Point(1 ,1)), entity3);
        Assert.assertEquals(testTransitionWorld.getEntity(new Point(0 ,0)), null);

        testWorld.add(new Point(0, 0), takeable);
        testWorld.add(new Point(1, 0), oneshot);
        testWorld.add(new Point(1, 1), interactive);

        Assert.assertEquals(testWorld.getItem(new Point(0, 0)), takeable);
        Assert.assertEquals(testWorld.getItem(new Point(1, 0)), oneshot);
        Assert.assertEquals(testWorld.getItem(new Point(1, 1)), interactive);

        takeable.trigger(entity1, true);
        oneshot.trigger(entity2, true);
        interactive.trigger(entity3, true);

        Assert.assertEquals(entity1.hasItemInBackpack(takeable), true);
        Assert.assertEquals(entity2.getCurrentHealth(), 90, 0);
        Assert.assertEquals(testWorld.getEntity(new Point(1 ,1)), null);
        Assert.assertEquals(testTransitionWorld.getEntity(new Point(0 ,0)), entity3);

        Assert.assertEquals(testWorld.getItem(new Point(0, 0)), null);
        Assert.assertEquals(testWorld.getItem(new Point(1, 0)), null);
        Assert.assertEquals(testWorld.getItem(new Point(1, 1)), interactive);
    }

    @Test
    public void testMaxInventoryCapacity() {
        World testWorld = new World();
        World testTransitionWorld = new World();

        ArrayList<World> worlds = new ArrayList<>();
        ArrayList<WorldNotifier> worldNotifiers = new ArrayList<>();
        worlds.add(testWorld);
        worlds.add(testTransitionWorld);
        worldNotifiers.add(new WorldNotifier(testWorld));
        worldNotifiers.add(new WorldNotifier(testTransitionWorld));

        GameModel model = new GameModel(worlds, worldNotifiers, new ArrayList<>(),
                new ArrayList<>(), testWorld, null);

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

        Entity entity = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), new WorldNotifier(testWorld), null);

        model.initialize(0, entity);

        testWorld.add(new Point(0, 0), Terrain.GRASS);
        testWorld.add(new Point(1, 0), Terrain.GRASS);
        testWorld.add(new Point(1, 1), Terrain.GRASS);
        testWorld.add(new Point(0, 1), Terrain.GRASS);
        testWorld.add(new Point(-1, 1), Terrain.GRASS);
        testWorld.add(new Point(-1, 0), Terrain.GRASS);
        testWorld.add(new Point(-1, -1), Terrain.GRASS);
        testWorld.add(new Point(0, -1), Terrain.GRASS);
        testWorld.add(new Point(1, -1), Terrain.GRASS);

        testTransitionWorld.add(new Point(0,0), Terrain.GRASS);

        testWorld.addNpc(new Point(0,0), entity);

        ArrayList<TakeableItem> takeables = new ArrayList<>();

        for(int i = 1; i <=11; i++) {
            takeables.add(new TakeableItem(Integer.toString(i), ItemType.DEFAULT, new WorldNotifier(testWorld), gameModelNotifier));
        }

        for(int i = 0; i < 10; i++) {
            testWorld.add(new Point(0, 0), takeables.get(i));
            takeables.get(i).trigger(entity,true);
        }

        for(int i = 0; i < 10; i++) {
            Assert.assertEquals(entity.hasItemInBackpack(takeables.get(i)), true);
        }

        testWorld.add(new Point(0,0), takeables.get(10));
        Assert.assertEquals(entity.hasItemInBackpack(takeables.get(10)), false);
        Assert.assertEquals(testWorld.getItem(new Point(0,0)), takeables.get(10));
        testWorld.removeItem(new Point(0,0));

        OneshotItem damage = new OneshotItem(new DamageEvent(20), new WorldNotifier(testWorld));
        InteractiveItem interactive = new InteractiveItem(new TransitionEvent(1, new Point(0,0), gameModelNotifier), new WorldNotifier(testWorld));

        testWorld.add(new Point(0,0), damage);
        damage.trigger(entity, true);
        Assert.assertFalse(entity.getCurrentHealth() == entity.getMaxHealth());
        Assert.assertNotEquals(damage, testWorld.getItem(new Point(0,0)));

        testWorld.add(new Point(0,0), interactive);
        interactive.trigger(entity, true);
        Assert.assertEquals(testTransitionWorld.getEntity(new Point(0,0)), entity);
    }

    @Test
    public void testMovement() {
        World testWorld = new World();
        Entity playerEntity = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(testWorld), null);
        Entity otherEntity = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(testWorld), null);
        Obstacle obstacle = new Obstacle(0);

        EntityController playerController = new EntityController(playerEntity);
        EntityController otherController = new EntityController(otherEntity);

        testWorld.add(new Point(0, 0), Terrain.GRASS);
        testWorld.add(new Point(1, 0), Terrain.WATER);
        testWorld.add(new Point(1, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(0, 1), Terrain.GRASS);
        testWorld.add(new Point(0, -1), Terrain.GRASS);
        testWorld.add(new Point(2, 0), Terrain.WATER);
        testWorld.add(new Point(2, 1), Terrain.MOUNTAINS);
        testWorld.add(new Point(-1, 0), Terrain.GRASS);

        testWorld.addPlayer(new Point(0,0), playerEntity);
        testWorld.addNpc(new Point(0,1), otherEntity);
        testWorld.add(new Point(-1, 0), obstacle);

        Assert.assertEquals(testWorld.getEntity(new Point(0,0)), playerEntity);
        Assert.assertEquals(testWorld.getEntity(new Point(0,1)), otherEntity);

        playerController.pressSouth();
        otherController.pressNorthEast();
        testWorld.attemptMoves();
        Assert.assertEquals(testWorld.getEntity(new Point(0,0)), playerEntity);
        Assert.assertEquals(testWorld.getEntity(new Point(0,1)), otherEntity);

        playerController.pressWest();
        otherController.pressEast();
        testWorld.attemptMoves();
        Assert.assertEquals(testWorld.getEntity(new Point(0,0)), playerEntity);
        Assert.assertEquals(testWorld.getEntity(new Point(0,1)), otherEntity);

        playerController.pressNorth();
        testWorld.attemptMoves();
        Assert.assertEquals(testWorld.getEntity(new Point(0,1)), otherEntity);
        Assert.assertEquals(testWorld.getEntity(new Point(0,-1)), playerEntity);

        otherController.pressNorth();
        testWorld.attemptMoves();
        Assert.assertEquals(testWorld.getEntity(new Point(0,0)), otherEntity);
        Assert.assertEquals(testWorld.getEntity(new Point(0,-1)), playerEntity);
    }

    @Test
    public void testGetNextPointFromOreintation() {
        World world = new World();

        for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 5; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }
        //test top of the world bounds
        for(int i = 0; i < 5; ++i) {
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(i, 0), Orientation.NORTH));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(i, 0), Orientation.NORTHWEST));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(i, 0), Orientation.NORTHEAST));
        }
        //test left of the world bounds
        for(int i = 0; i < 5; ++i) {
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(0, i), Orientation.NORTHWEST));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(0, i), Orientation.WEST));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(0, i), Orientation.SOUTHWEST));
        }
        //test bottom of the world bounds
        for(int i = 0; i < 5; ++i) {
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(i, 4), Orientation.SOUTHWEST));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(i, 4), Orientation.SOUTH));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(i, 4), Orientation.SOUTHEAST));
        }
        //test right of the world bounds
        for(int i = 0; i < 5; ++i) {
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(4, i), Orientation.SOUTHEAST));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(4, i), Orientation.EAST));
            Assert.assertEquals(null, world.getNextPointFromOrientation(new Point(4, i), Orientation.NORTHEAST));
        }

        //test orientation 0
        for(int i = 0; i < 5; ++i){
            for(int j = 1; j < 5; ++j){
                Assert.assertEquals(new Point(i, j-1), world.getNextPointFromOrientation(new Point(i, j), Orientation.NORTH));
            }
        }
        //test orientation 45
        for(int i = 1; i < 5; ++i){
            for(int j = 1; j < 5; ++j){
                Assert.assertEquals(new Point(i-1, j-1), world.getNextPointFromOrientation(new Point(i, j), Orientation.NORTHWEST));
            }
        }
        //test orientation 90
        for(int i = 1; i < 5; ++i){
            for(int j = 0; j < 5; ++j){
                Assert.assertEquals(new Point(i-1, j), world.getNextPointFromOrientation(new Point(i, j), Orientation.WEST));
            }
        }
        //test orientation 135
        for(int i = 1; i < 5; ++i){
            for(int j = 0; j < 4; ++j){
                Assert.assertEquals(new Point(i-1, j+1), world.getNextPointFromOrientation(new Point(i, j), Orientation.SOUTHWEST));
            }
        }
        //test orientation 180
        for(int i = 0; i < 5; ++i){
            for(int j = 0; j < 4; ++j){
                Assert.assertEquals(new Point(i, j+1), world.getNextPointFromOrientation(new Point(i, j), Orientation.SOUTH));
            }
        }
        //test orientation 225
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                Assert.assertEquals(new Point(i+1, j+1), world.getNextPointFromOrientation(new Point(i, j), Orientation.SOUTHEAST));
            }
        }
        //test orientation 270
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 5; ++j){
                Assert.assertEquals(new Point(i+1, j), world.getNextPointFromOrientation(new Point(i, j), Orientation.EAST));
            }
        }
        //test orientation 315
        for(int i = 0; i < 4; ++i){
            for(int j = 1; j < 5; ++j){
                Assert.assertEquals(new Point(i+1, j-1), world.getNextPointFromOrientation(new Point(i, j), Orientation.NORTHEAST));
            }
        }


    }

    @Test
    public void testLinearEffectGetClosestEntity() {
        World world = new World();
        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);
        Entity monster = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);

        for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 5; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        world.addPlayer(new Point(2, 2), player);
        world.addNpc(new Point(2, 0), monster);
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.NORTH)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        world.moveEntity(new Point(2, 0), new Point(0, 0));
        for(Orientation orientation: Orientation.values()) {
            if (orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if (orientation == Orientation.NORTHWEST)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        world.moveEntity(new Point(0, 0), new Point(0, 2));
        for(Orientation orientation: Orientation.values()) {
            if (orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if (orientation == Orientation.WEST)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        world.moveEntity(new Point(0, 2), new Point(0, 4));
        for(Orientation orientation: Orientation.values()) {
            if (orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if (orientation == Orientation.SOUTHWEST)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        world.moveEntity(new Point(0, 4), new Point(2, 4));
        for(Orientation orientation: Orientation.values()) {
            if (orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if (orientation == Orientation.SOUTH)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        world.moveEntity(new Point(2, 4), new Point(4, 4));
        for(Orientation orientation: Orientation.values()) {
            if (orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if (orientation == Orientation.SOUTHEAST)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        world.moveEntity(new Point(4, 4), new Point(4, 2));
        for(Orientation orientation: Orientation.values()) {
            if (orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if (orientation == Orientation.EAST)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        world.moveEntity(new Point(4, 2), new Point(4, 0));
        for(Orientation orientation: Orientation.values()) {
            if (orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if (orientation == Orientation.NORTHEAST)
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), monster);
            else
                Assert.assertEquals(world.linearEffectGetClosestEntity(player), null);
        }

        //make sure the function returns the closest entity
        world.moveEntity(new Point(4,0), new Point(2, 0));
        Entity closerMonster = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);
        world.addNpc(new Point(2, 1), closerMonster);
        player.setOrientation(Orientation.NORTH);
        Assert.assertEquals(world.linearEffectGetClosestEntity(player), closerMonster);
    }

    @Test
    public void testLinearEffectGetAllEntities() {
        World world = new World();
        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);
        Entity monster =new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world),  null);
        Entity monster2 = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world),  null);
        List<Entity> both = new ArrayList<>(2);
        both.add(monster2);
        both.add(monster);

        for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 5; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        world.addPlayer(new Point(2, 2), player);
        world.addNpc(new Point(2, 0), monster);
        world.addNpc(new Point(2, 1), monster2);

        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.NORTH)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        world.moveEntity(new Point(2, 0), new Point(0, 0));
        world.moveEntity(new Point(2, 1), new Point(1, 1));
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.NORTHWEST)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        world.moveEntity(new Point(0, 0), new Point(0, 2));
        world.moveEntity(new Point(1, 1), new Point(1, 2));
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.WEST)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        world.moveEntity(new Point(0, 2), new Point(0, 4));
        world.moveEntity(new Point(1, 2), new Point(1, 3));
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.SOUTHWEST)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        world.moveEntity(new Point(0, 4), new Point(2, 4));
        world.moveEntity(new Point(1, 3), new Point(2, 3));
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.SOUTH)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        world.moveEntity(new Point(2, 4), new Point(4, 4));
        world.moveEntity(new Point(2, 3), new Point(3, 3));
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.SOUTHEAST)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        world.moveEntity(new Point(4, 4), new Point(4, 2));
        world.moveEntity(new Point(3, 3), new Point(3, 2));
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.EAST)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        world.moveEntity(new Point(4, 2), new Point(4, 0));
        world.moveEntity(new Point(3, 2), new Point(3, 1));
        for(Orientation orientation: Orientation.values()){
            if(orientation == Orientation.NULL)
                continue;
            player.setOrientation(orientation);
            if(orientation == Orientation.NORTHEAST)
                Assert.assertEquals(world.linearEffectGetAllEntities(player), both);
            else
                Assert.assertEquals(world.linearEffectGetAllEntities(player), new ArrayList<>());
        }

        //make sure the function returns the closest entity
        world.moveEntity(new Point(4,0), new Point(0, 2));
        world.moveEntity(new Point(3, 1), new Point(4, 2));
        player.setOrientation(Orientation.WEST);
        List<Entity> single = new ArrayList<>();
        single.add(monster);
        Assert.assertEquals(world.linearEffectGetAllEntities(player), single);

        single.clear();
        single.add(monster2);
        player.setOrientation(Orientation.EAST);
        Assert.assertEquals(world.linearEffectGetAllEntities(player), single);
    }

    @Test
    public void testAngularEffectGetAllEntities() {
        World world = new World();
        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);

        for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 5; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }
        world.addPlayer(new Point(2, 2), player);

        Entity[] monsters = new Entity[25];
        for(int i = 0; i < monsters.length; ++i) {
            monsters[i] = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world),  null);
        }

        for(int i = 0; i < 5; ++i){
            for(int j = 0; j < 5; ++j) {
                if(i != 2 || j != 2)
                    world.addNpc(new Point(j, i), monsters[i*5+j]);
            }
        }

        player.setOrientation(Orientation.NORTH);
        List<Entity> detected = world.angularEffectGetAllEntities(player);
        List<Entity> expected = new ArrayList<>();
        expected.add(monsters[0]);
        expected.add(monsters[1]);
        expected.add(monsters[2]);
        expected.add(monsters[3]);
        expected.add(monsters[4]);
        expected.add(monsters[6]);
        expected.add(monsters[7]);
        expected.add(monsters[8]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        player.setOrientation(Orientation.NORTHWEST);
        detected = world.angularEffectGetAllEntities(player);
        expected.clear();
        expected.add(monsters[0]);
        expected.add(monsters[1]);
        expected.add(monsters[2]);
        expected.add(monsters[5]);
        expected.add(monsters[6]);
        expected.add(monsters[7]);
        expected.add(monsters[10]);
        expected.add(monsters[11]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        player.setOrientation(Orientation.WEST);
        detected = world.angularEffectGetAllEntities(player);
        expected.clear();
        expected.add(monsters[0]);
        expected.add(monsters[5]);
        expected.add(monsters[6]);
        expected.add(monsters[10]);
        expected.add(monsters[11]);
        expected.add(monsters[15]);
        expected.add(monsters[16]);
        expected.add(monsters[20]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        player.setOrientation(Orientation.SOUTHWEST);
        detected = world.angularEffectGetAllEntities(player);
        expected.clear();
        expected.add(monsters[10]);
        expected.add(monsters[11]);
        expected.add(monsters[15]);
        expected.add(monsters[16]);
        expected.add(monsters[17]);
        expected.add(monsters[20]);
        expected.add(monsters[21]);
        expected.add(monsters[22]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        player.setOrientation(Orientation.SOUTH);
        detected = world.angularEffectGetAllEntities(player);
        expected.clear();
        expected.add(monsters[16]);
        expected.add(monsters[17]);
        expected.add(monsters[18]);
        expected.add(monsters[20]);
        expected.add(monsters[21]);
        expected.add(monsters[22]);
        expected.add(monsters[23]);
        expected.add(monsters[24]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        player.setOrientation(Orientation.SOUTHEAST);
        detected = world.angularEffectGetAllEntities(player);
        expected.clear();
        expected.add(monsters[13]);
        expected.add(monsters[14]);
        expected.add(monsters[17]);
        expected.add(monsters[18]);
        expected.add(monsters[19]);
        expected.add(monsters[22]);
        expected.add(monsters[23]);
        expected.add(monsters[24]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        player.setOrientation(Orientation.EAST);
        detected = world.angularEffectGetAllEntities(player);
        expected.clear();
        expected.add(monsters[4]);
        expected.add(monsters[8]);
        expected.add(monsters[9]);
        expected.add(monsters[13]);
        expected.add(monsters[14]);
        expected.add(monsters[18]);
        expected.add(monsters[19]);
        expected.add(monsters[24]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        player.setOrientation(Orientation.NORTHEAST);
        detected = world.angularEffectGetAllEntities(player);
        expected.clear();
        expected.add(monsters[2]);
        expected.add(monsters[3]);
        expected.add(monsters[4]);
        expected.add(monsters[7]);
        expected.add(monsters[8]);
        expected.add(monsters[9]);
        expected.add(monsters[13]);
        expected.add(monsters[14]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));
    }

    @Test
    public void testRadialEffectGetAllEntities() {
        World world = new World();
        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }
        world.addPlayer(new Point(2, 2), player);

        Entity[] monsters = new Entity[25];
        for (int i = 0; i < monsters.length; ++i) {
            monsters[i] = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world),null);
        }

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (i != 2 || j != 2)
                    world.addNpc(new Point(j, i), monsters[i * 5 + j]);
            }
        }

        List<Entity> detected = world.radialEffectGetAllEntities(player);
        List<Entity> expected = new ArrayList<>();
        for (int i = 0; i < 25; ++i) {
            if (i != 12)
                expected.add(monsters[i]);
        }
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));

        detected = world.radialEffectGetAllEntities(player, 1);
        expected.clear();
        expected.add(monsters[6]);
        expected.add(monsters[7]);
        expected.add(monsters[8]);
        expected.add(monsters[11]);
        expected.add(monsters[13]);
        expected.add(monsters[16]);
        expected.add(monsters[17]);
        expected.add(monsters[18]);
        Assert.assertEquals(expected.size(), detected.size());
        Assert.assertTrue(detected.containsAll(expected));
    }

    @Test
    public void testInfluenceRadius() {
        Point source = new Point(0,0);
        Assert.assertEquals(0, World.getInfluenceRadius(source, source));

        List<Point> radius1 = new ArrayList<>();
        radius1.add(new Point(-1, -1));
        radius1.add(new Point(-1, 0));
        radius1.add(new Point(-1, 1));
        radius1.add(new Point(0, -1));
        radius1.add(new Point(0, 1));
        radius1.add(new Point(1, -1));
        radius1.add(new Point(1, 0));
        radius1.add(new Point(1, 1));
        for(Point point: radius1) {
            Assert.assertEquals(1, World.getInfluenceRadius(source, point));
        }

        List<Point> radius2 = new ArrayList<>();
        for(int i = -1; i <= 1; ++i){
            radius2.add(new Point(i, -2));
            radius2.add(new Point(-2, i));
            radius2.add(new Point(i, 2));
            radius2.add(new Point(2, i));
        }
        for(Point point: radius2) {
            Assert.assertEquals(2, World.getInfluenceRadius(source, point));
        }

        List<Point> radius3 = new ArrayList<>();
        for(int i = -2; i <= 2; ++i){
            radius3.add(new Point(i, -3));
            radius3.add(new Point(-3, i));
            radius3.add(new Point(i, 3));
            radius3.add(new Point(3, i));
        }
        radius3.add(new Point(-2, -2));
        radius3.add(new Point(-2, 2));
        radius3.add(new Point(2, -2));
        radius3.add(new Point(2, 2));
        for(Point point: radius3) {
            Assert.assertEquals(3, World.getInfluenceRadius(source, point));
        }

        List<Point> radius4 = new ArrayList<>();
        for(int i = -3; i <= 3; ++i){
            radius4.add(new Point(i, -4));
            radius4.add(new Point(-4, i));
            radius4.add(new Point(i, 4));
            radius4.add(new Point(4, i));
        }
        radius4.add(new Point(-3, -3));
        radius4.add(new Point(-3, 3));
        radius4.add(new Point(3, -3));
        radius4.add(new Point(3, 3));
        for(Point point: radius4) {
            Assert.assertEquals(4, World.getInfluenceRadius(source, point));
        }
    }

    @Test
    public void testTrapInteraction() {
        World testWorld = new World();

        ArrayList<World> worlds = new ArrayList<>();
        ArrayList<WorldNotifier> worldNotifiers = new ArrayList<>();

        worlds.add(testWorld);

        WorldNotifier testWorldNotifier = new WorldNotifier(testWorld);

        worldNotifiers.add(testWorldNotifier);

        GameModel model = new GameModel(worlds, worldNotifiers, new ArrayList<>(),
                new ArrayList<>(), testWorld, null);

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

        Entity entity = new Entity(100, 100, 2, 0, Orientation.NORTH, new Inventory(10, gameModelNotifier), testWorldNotifier, null);

        model.initialize(0, entity);

        SettableEventCommand event = new DamageEvent(15);
        Trap trap = new Trap(event, 10);

        testWorld.add(new Point(0, 0), Terrain.GRASS);

        testWorld.addPlayer(new Point(0, 0), entity);
        testWorld.add(new Point(0,0), trap);

        Assert.assertEquals(entity.getCurrentHealth(), 100, 0);
        Assert.assertEquals(testWorld.getTrap(new Point(0, 0)), trap);
        Assert.assertEquals(trap.getIsVisible(), false);
        Assert.assertEquals(trap.getHasNotFired(), true);

        testWorld.triggerTraps();

        Assert.assertEquals(entity.getCurrentHealth(), 85, 0);
        Assert.assertEquals(testWorld.getTrap(new Point(0, 0)), trap);
        Assert.assertEquals(trap.getIsVisible(), true);
        Assert.assertEquals(trap.getHasNotFired(), false);

        testWorld.triggerTraps();

        Assert.assertEquals(entity.getCurrentHealth(), 85, 0);
        Assert.assertEquals(testWorld.getTrap(new Point(0, 0)), trap);
        Assert.assertEquals(trap.getIsVisible(), true);
        Assert.assertEquals(trap.getHasNotFired(), false);
    }
}
