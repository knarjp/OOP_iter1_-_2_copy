package GameControllerTests;

import com.MeanTeam.gamecontroller.FriendlyNpcController;
import com.MeanTeam.gamecontroller.HostileNpcController;
import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.effectcommand.DamageEvent;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.entities.EntityType;
import com.MeanTeam.gamemodel.tile.items.ItemType;
import com.MeanTeam.gamemodel.tile.items.WeaponItem;
import com.MeanTeam.gamemodel.tile.skills.SkillPool;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.util.Orientation;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NpcControllerTest {
    @Test
    public void testBasicPatrol() {
        World world = new World();
        for(int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        Entity entity = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);

        world.addNpc(new Point(0,0), entity);

        FriendlyNpcController npcController = new FriendlyNpcController();
        npcController.addEntity(entity);
        Orientation orientation = Orientation.EAST;
        for(int i = 0; i < 4; ++i) {
            npcController.addOrientationToPath(entity, orientation);
            npcController.addOrientationToPath(entity, orientation);
            orientation = orientation.getRight90();
        }

        java.util.List<World> worlds = new ArrayList<>();
        worlds.add(world);

        for(int i = 0; i < 4; ++i) {
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(1, 0)), entity);
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(2, 0)), entity);
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(2, 1)), entity);
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(2, 2)), entity);
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(1, 2)), entity);
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(0, 2)), entity);
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(0, 1)), entity);
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntity(new Point(0, 0)), entity);
        }
    }

    @Test
    public void testChasePlayerSimple() {
        World world = new World();
        for(int i = -2; i < 3; ++i) {
            for (int j = -2; j < 3; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        Entity entity = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world),null);
        world.addNpc(new Point(-2,-2), entity);

        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);
        player.setNoiseLevel(20);
        world.addNpc(new Point(2, 2), player);

        HostileNpcController npcController = new HostileNpcController(player);
        npcController.addEntity(entity);

        List<World> worlds = new ArrayList<>();
        worlds.add(world);

        int[] routeX = {-1, 0, 1};
        int[] routeY = {-1, 0, 1};

        for(int i = 0; i < routeX.length; ++i) {
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntityLocation(entity), new Point(routeX[i], routeY[i]));
        }

        world.removeTerrain(new Point(-1, -1));
        world.add(new Point(-1, -1), Terrain.MOUNTAINS);

        world.moveEntity(new Point(1, 1), new Point(-2,-2));

        int[] routeX2 = {-1,  0, 1, 1};
        int[] routeY2 = {-2, -1, 0, 1};

        for(int i = 0; i < routeX2.length; ++i) {
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntityLocation(entity), new Point(routeX2[i], routeY2[i]));
        }


    }

    @Test
    public void testChasePlayerZigZag() {
        World world = new World();
        for(int i = -2; i < 3; ++i) {
            for (int j = -2; j < 3; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }

        Entity entity = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);
        world.addNpc(new Point(-2,-2), entity);

        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), null);
        player.setNoiseLevel(20);
        world.addPlayer(new Point(2, 2), player);

        HostileNpcController npcController = new HostileNpcController(player);
        npcController.addEntity(entity);

        List<World> worlds = new ArrayList<>();
        worlds.add(world);

        for(int j = -2; j < 2; ++j) {
            world.removeTerrain(new Point(-1,j));
            world.add(new Point(-1, j), Terrain.MOUNTAINS);
        }

        for(int j = 2; j > -2; --j) {
            world.removeTerrain(new Point(1,j));
            world.add(new Point(1, j), Terrain.MOUNTAINS);
        }

        int[] routeX = {-2, -2, -2, -1, 0, 0,  0,  1,  2, 2, 2};
        int[] routeY = {-1,  0,  1,  2, 1, 0, -1, -2, -1, 0, 1};

        for(int i = 0; i < routeX.length; ++i) {
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntityLocation(entity), new Point(routeX[i], routeY[i]));
        }
    }

    @Test
    public void testHostileNpcPathing() {
        World world = new World();
        WorldNotifier worldNotifier = new WorldNotifier(world);
        List<World> worldList = new ArrayList<>();
        List<WorldNotifier> worldNotifierList = new ArrayList<>();

        // Begin making hard-coded example worlds:
        World startingWorld = new World();
        WorldNotifier startingWorldNotifier = new WorldNotifier(startingWorld);
        worldList.add(startingWorld);

        GameModel gameModel = new GameModel(worldList, worldNotifierList, new ArrayList<>(), new ArrayList<>(),
                startingWorld, null, 0);
        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(gameModel);
        for(int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                world.add(new Point(i, j), Terrain.GRASS);
            }
        }
        WeaponItem defaultWeapon = new WeaponItem("", ItemType.DEFAULT , worldNotifier, 0, 0, 0, 0, 1, new DamageEvent(10), SkillPool.damageRing, gameModelNotifier);
        Entity entity = new Entity(100, 100, 2, 0, Orientation.NORTH, null,  new WorldNotifier(world), defaultWeapon);
        world.addNpc(new Point(6,6), entity);

        Entity player = new Entity(100, 100, 2, 0, Orientation.NORTH, null, new WorldNotifier(world), defaultWeapon);
        player.setNoiseLevel(0);
        world.addPlayer(new Point(0, 0), player);

        HostileNpcController npcController = new HostileNpcController(player);
        npcController.addEntity(entity);

        npcController.addOrientationToPath(entity, Orientation.WEST);
        npcController.addOrientationToPath(entity, Orientation.WEST);
        npcController.addOrientationToPath(entity, Orientation.WEST);
        npcController.addOrientationToPath(entity, Orientation.NORTH);
        npcController.addOrientationToPath(entity, Orientation.NORTH);
        npcController.addOrientationToPath(entity, Orientation.NORTH);
        npcController.addOrientationToPath(entity, Orientation.EAST);
        npcController.addOrientationToPath(entity, Orientation.EAST);
        npcController.addOrientationToPath(entity, Orientation.EAST);
        npcController.addOrientationToPath(entity, Orientation.SOUTH);
        npcController.addOrientationToPath(entity, Orientation.SOUTH);
        npcController.addOrientationToPath(entity, Orientation.SOUTH);


        List<World> worlds = new ArrayList<>();
        worlds.add(world);

        int[] routeX = {5,4,3,3,3,3,4,5,6,6,6,6};
        int[] routeY = {6,6,6,5,4,3,3,3,3,4,5,6};

        for(int i = 0; i < routeX.length; ++i) {
            npcController.nextAction(world);
            world.attemptMoves();
            Assert.assertEquals(world.getEntityLocation(entity), new Point(routeX[i], routeY[i]));
        }
    }
}
