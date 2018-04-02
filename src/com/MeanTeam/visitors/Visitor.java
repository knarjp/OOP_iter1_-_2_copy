package com.MeanTeam.visitors;

import com.MeanTeam.gamecontroller.FriendlyNpcController;
import com.MeanTeam.gamecontroller.HostileNpcController;
import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.areaeffects.InfiniteAreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.OneShotAreaEffect;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.tile.effectcommand.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.util.Path;
import com.MeanTeam.util.TimedEntityConfuseEvent;
import com.MeanTeam.util.TimedEntityMovementSpeedEvent;

public interface Visitor
{
    // NPC controllers
    void visitFriendlyNpcController(FriendlyNpcController fnc);
    void visitHostileNpcController(HostileNpcController hnc);

    // Area Effects
    void visitInfiniteAreaEffect(InfiniteAreaEffect i);
    void visitOneshotAreaEffect(OneShotAreaEffect o);

    // Event Commands
    void visitNullCommand(EventCommand e);
    void visitNullSettableCommand(SettableEventCommand s);
    void visitConditionalTransitionEvent(ConditionalTransitionEvent c);
    void visitConfuseEvent(ConfuseEvent c);
    void visitCreepEvent(CreepEvent c);
    void visitDamageEvent(DamageEvent d);
    void visitTrapDisarmEvent(DisarmTrapEvent d);
    void visitHealEvent(HealEvent h);
    void visitInstaDeathCommand(InstaDeathEvent i);
    void visitLevelUpCommand(LevelUpEvent l);
    void visitObservationEvent(ObservationEvent o);
    void visitPickPocketEvent(PickPocketEvent p);
    void visitSlowdownEvent(SlowdownEvent s);
    void visitStaminaDecreaseEvent(StaminaDecreaseEvent s);
    void visitStartDialogEvent(StartDialogEvent d);
    void visitToggleableHealthEvent(ToggleableHealthEvent h);
    void visitTransitionEvent(TransitionEvent t);

    // Entities
    void visitEntity(Entity e);

    // Inventories
    void visitInventory(Inventory i);

    // Items
    void visitArmorItem(ArmorItem a);
    void visitConsumableItem(ConsumableItem c);
    void visitInteractiveItem(InteractiveItem i);
    void visitOneshotItem(OneshotItem i);
    void visitRingItem(RingItem r);
    void visitTakeableItem(TakeableItem t);
    void visitWeaponItem(WeaponItem w);

    // Skills
    void visitSkill(Skill s);

    // Traps
    void visitTrap(Trap t);

    // World
    void visitWorld(World w);

    // Game Model
    void visitGameModel(GameModel g);

    // Util classes
    void visitPath(Path p);
    void visitTimedEntityConfuseEvent(TimedEntityConfuseEvent tec);
    void visitTimedEntityMovementSpeedEvent(TimedEntityMovementSpeedEvent tems);

    // ItemType/ItemSpecialzer visitors
    void visitDefault(Item i);
    void visitDoor(Item i);
    void visitOpenDoor(Item i);
    void visitKey(Item i);
    void visitHealthPotion(Item i);
    void visitDamagePotion(Item i);
    void visitSword(Item i);
    void visitChesplate(Item i);
    void visitBuffableRing(Item i);
}
