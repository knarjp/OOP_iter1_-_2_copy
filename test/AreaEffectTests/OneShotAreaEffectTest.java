package AreaEffectTests;

import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.areaeffects.AreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.OneShotAreaEffect;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.effectcommand.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;

public class OneShotAreaEffectTest {

    @Test
    public void testInstaDeathAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null,null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null,null);
        EventCommand effect = new InstaDeathEvent();
        AreaEffect oneShot = new OneShotAreaEffect(effect);

        oneShot.trigger(entity);
        oneShot.trigger(entity2);

        Assert.assertEquals(entity.getCurrentHealth(), 0, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
    }

    @Test
    public void testLevelUpAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null, null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null, null);
        EventCommand effect = new LevelUpEvent();
        AreaEffect oneShot = new OneShotAreaEffect(effect);

        oneShot.trigger(entity);
        oneShot.trigger(entity2);

        Assert.assertEquals(entity.getLevel(), 1);
        Assert.assertEquals(entity2.getLevel(), 0);
    }

    @Test
    public void testHealAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null, null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null, null);
        EventCommand effect = new HealEvent(10);
        AreaEffect oneShot = new OneShotAreaEffect(effect);

        entity.decreaseHealth(15);
        entity2.decreaseHealth(10);

        oneShot.trigger(entity);
        oneShot.trigger(entity2);

        Assert.assertEquals(entity.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 90, 0);
    }

    @Test
    public void testDamangeAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null,null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null, null);
        EventCommand effect = new DamageEvent(10);
        AreaEffect oneShot = new OneShotAreaEffect(effect);

        oneShot.trigger(entity);
        oneShot.trigger(entity2);

        Assert.assertEquals(entity.getCurrentHealth(), 90, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
    }
}
