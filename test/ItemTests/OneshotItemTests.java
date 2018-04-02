package ItemTests;

import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.effectcommand.EventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.Item;
import com.MeanTeam.gamemodel.tile.effectcommand.DamageEvent;
import com.MeanTeam.gamemodel.tile.effectcommand.HealEvent;
import com.MeanTeam.gamemodel.tile.items.OneshotItem;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;

public class OneshotItemTests {
    @Test
    public void testOneshotItem(){
        //create an entity
        Entity entity = new Entity(100, 100, 2, 0, null, null,null, null);

        //create an item
        EventCommand effect = EventCommand.NULL;
        Item item = new OneshotItem(effect, new WorldNotifier(new World()));

        //trigger the item
        item.trigger(entity, true);
    }

    @Test
    public void testDamageAndHealEvent(){
        //create an entity
        Entity entity = new Entity(100, 100, 2, 0, null, null,null, null);

        //create a healing item
        EventCommand effect = new HealEvent(10);
        Item healItem = new OneshotItem(effect, new WorldNotifier(new World()));

        //create a damage item
        effect = new DamageEvent(10);
        Item damageItem = new OneshotItem(effect, new WorldNotifier(new World()));

        //assert the entity has full health
        Assert.assertTrue(entity.getCurrentHealth() == 100);

        //trigger the item
        damageItem.trigger(entity, true);

        //assert the entity healed
        Assert.assertTrue(entity.getCurrentHealth() == 90);

        //trigger the item
        healItem.trigger(entity, true);

        //assert the entity healed
        Assert.assertTrue(entity.getCurrentHealth() == 100);
    }
}
