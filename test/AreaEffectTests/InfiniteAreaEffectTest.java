package AreaEffectTests;

import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.areaeffects.AreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.InfiniteAreaEffect;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.effectcommand.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;

public class InfiniteAreaEffectTest {

    @Test
    public void testInstaDeathAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null,  null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null,  null);
        EventCommand effect = new InstaDeathEvent();
        AreaEffect infinite = new InfiniteAreaEffect(effect);

        infinite.trigger(entity);
        infinite.trigger(entity2);

        Assert.assertEquals(entity.getCurrentHealth(), 0, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 0, 0);
    }

    @Test
    public void testLevelUpAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null, null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null,  null);
        EventCommand effect = new LevelUpEvent();
        AreaEffect infinite = new InfiniteAreaEffect(effect);

        infinite.trigger(entity);
        infinite.trigger(entity2);

        Assert.assertEquals(entity.getLevel(), 1);
        Assert.assertEquals(entity2.getLevel(), 1);

        infinite.trigger(entity);
        Assert.assertEquals(entity.getLevel(), 2);

        infinite.trigger(entity);
        infinite.trigger(entity);
        infinite.trigger(entity);

        Assert.assertEquals(entity.getLevel(), 5);

        infinite.trigger(entity2);
        Assert.assertEquals(entity2.getLevel(), 2);
    }

    @Test
    public void testHealAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null,  null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null,  null);
        EventCommand effect = new HealEvent(10);
        AreaEffect infinite = new InfiniteAreaEffect(effect);

        entity.decreaseHealth(15);
        entity2.decreaseHealth(10);

        infinite.trigger(entity);
        infinite.trigger(entity2);

        Assert.assertEquals(entity.getCurrentHealth(), 95, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 100, 0);
    }

    @Test
    public void testDamangeAreaEffect() {
        Entity entity = new Entity(100, 100, 2, 0, null, null, null, null);
        Entity entity2 = new Entity(100, 100, 2, 0, null, null, null,  null);
        EventCommand effect = new DamageEvent(10);
        AreaEffect infinite = new InfiniteAreaEffect(effect);

        infinite.trigger(entity);
        infinite.trigger(entity2);

        Assert.assertEquals(entity.getCurrentHealth(), 90, 0);
        Assert.assertEquals(entity2.getCurrentHealth(), 90, 0);
    }
}
