package InventoryTests;

import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.ItemType;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InventoryTest {

    @Before
    public void resetGameModelNotifier() {
        GameModelNotifier.destroyInstance();
    }

    @Test
    public  void testItemAdding() {
        GameModel model = new GameModel(new ArrayList<>() , new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new World(), new Entity(0 ,0 ,0 ,0, null, null, null, null));

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

        Inventory inventory = new Inventory(1, gameModelNotifier);

        TakeableItem item1 = new TakeableItem("item1", ItemType.DEFAULT, new WorldNotifier(new World()), gameModelNotifier);
        TakeableItem item2 = new TakeableItem("item2", ItemType.DEFAULT, new WorldNotifier(new World()), gameModelNotifier);

        inventory.addItem(item1);

        Assert.assertEquals(inventory.getItemAtSlot(0), item1);

        inventory.addItem(item2);

        Assert.assertEquals(inventory.getItemAtSlot(0), item1);
        Assert.assertEquals(inventory.getItemAtSlot(1), null);

        inventory.addItem(item1);
        inventory.addItem(item1);
        inventory.addItem(item1);

        Assert.assertEquals(inventory.getItemAtSlot(1), null);
        Assert.assertEquals(inventory.getItemAtSlot(123), null);
        Assert.assertEquals(inventory.getItemAtSlot(-32), null);
    }

    @Test
    public  void testItemRemoval() {
        GameModel model = new GameModel(new ArrayList<>() , new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new World(), new Entity(0 ,0 ,0 ,0, null, null, null, null));

        GameModelNotifier gameModelNotifier = GameModelNotifier.getGameModelNotifier(model);

        Inventory inventory = new Inventory(1, gameModelNotifier);

        TakeableItem item1 = new TakeableItem("item1", ItemType.DEFAULT, new WorldNotifier(new World()), gameModelNotifier);
        TakeableItem item2 = new TakeableItem("item2", ItemType.DEFAULT, new WorldNotifier(new World()), gameModelNotifier);

        inventory.addItem(item1);

        Assert.assertEquals(inventory.getItemAtSlot(0), item1);

        inventory.removeItem(0);

        Assert.assertEquals(inventory.getItemAtSlot(0), null);

        inventory.removeItem(1);
        inventory.removeItem(123);
        inventory.removeItem(-32);

        Assert.assertEquals(inventory.getItemAtSlot(1), null);
        Assert.assertEquals(inventory.getItemAtSlot(123), null);
        Assert.assertEquals(inventory.getItemAtSlot(-32), null);
    }
}
