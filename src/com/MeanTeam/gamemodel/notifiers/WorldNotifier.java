package com.MeanTeam.gamemodel.notifiers;

import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.Item;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.gamemodel.tile.skills.Skill;

public class WorldNotifier {
    private World world;

    public WorldNotifier(World world) {
        this.world = world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void notifyMove(Entity entity) {
        world.addEntityToMoveQueue(entity);
    }

    public void notifySkillTrigger(Entity entity, Skill skill) {
        world.addEntityToUseSkillQueue(entity, skill);
    }

    public boolean notifyItemPickup(Entity entity, TakeableItem item) {
        return world.attemptGiveItemToEntity(entity, item);
    }

    public void notifyItemRemoval(Item item) {
        world.removeItem(item);
    }

    public void notifyTrapDisarm(Entity entity, int trapSuccessChance) {
        world.disarmTrap(entity, trapSuccessChance);
    }
}