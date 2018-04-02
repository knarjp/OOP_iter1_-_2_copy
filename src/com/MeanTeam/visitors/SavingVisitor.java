package com.MeanTeam.visitors;

import com.MeanTeam.gamecontroller.FriendlyNpcController;
import com.MeanTeam.gamecontroller.HostileNpcController;
import com.MeanTeam.gamemodel.GameModel;
import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.areaeffects.InfiniteAreaEffect;
import com.MeanTeam.gamemodel.tile.areaeffects.OneShotAreaEffect;
import com.MeanTeam.gamemodel.tile.effectcommand.*;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SavingVisitor implements Visitor
{
    private Map<String, String> skillReferenceAttributeMap = new HashMap<>();
    private Map<String, String> itemReferenceAttributeMap = new HashMap<>();
    private Map<String, String> eventReferenceAttributeMap = new HashMap<>();

    private String string = "";

    private String tempItemTypeString = "";

    public String getString() { return string; }

    // NPC controllers
    public void visitFriendlyNpcController(FriendlyNpcController fnc) {
            string += "FRIENDLY_NPC_CONTROLLER ";

            List<Entity> entities = fnc.getEntities();
            List<Path> paths = fnc.getPaths();

            for(int i = 0; i < entities.size(); i++) {
                string += "ENTITY " + entities.get(i).toString();
                paths.get(i).accept(this);
            }
            string += "\n";
    }

    public void visitHostileNpcController(HostileNpcController hnc) {
        string += "HOSTILE_NPC_CONTROLLER ";

        List<Entity> entities = hnc.getEntities();
        List<Path> paths = hnc.getPaths();
        List<Boolean> confusedEntities = hnc.getConfused();

        for(int i = 0; i < entities.size(); i++) {
            string += "ENTITY " + entities.get(i).toString();
            string += " CONFUSED " + confusedEntities.get(i).toString();
            paths.get(i).accept(this);
        }
        string += "\n";

    }

    // Area Effects
    public void visitInfiniteAreaEffect(InfiniteAreaEffect i) {
        string += "INF_AREA_EFFECT " + i.toString() + " EVENT " + i.getEffect().toString();
        i.getEffect().accept(this);
    }

    public void visitOneshotAreaEffect(OneShotAreaEffect o) {
        string += "ONESHOT_AREA_EFFECT " + " HASNOTFIRED " + o.hasNotFired() + " " + o.toString() + " EVENT " + o.getEffect().toString();
        o.getEffect().accept(this);
    }

    // Event Commands
    public void visitNullCommand(EventCommand i) {
        String reference = EventCommand.NULL.toString();
        String info = "NULL";

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitNullSettableCommand(SettableEventCommand s) {
        String reference = SettableEventCommand.NULL.toString();
        String info = "NULLSETTABLE";

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitConditionalTransitionEvent(ConditionalTransitionEvent c) {
        String reference = c.toString();

        String info = "CONDITIONAL_TRANSITION_EVENT DESTWORLD " + c.getDestinationWorld() + " DESTTILE "
                    + c.getStartingPoint().x + "," + c.getStartingPoint().y + " ISLOCKED "
                    + c.isLocked() + " KEY " + c.getRequiredItem().toString();

        c.getRequiredItem().accept(this);

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitConfuseEvent(ConfuseEvent c) {
        String reference = c.toString();

        String info = "CONFUSE_EVENT DURATION " + c.getDuration();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitCreepEvent(CreepEvent c) {
        String reference = c.toString();

        String info = "CREEP_EVENT DAMAGEAMT " + c.getDamageAmount();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitDamageEvent(DamageEvent d) {
        String reference = d.toString();

        String info = "DAMAGE_EVENT DAMAGEAMT " + d.getDamageAmount();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitTrapDisarmEvent(DisarmTrapEvent d) {
        String reference = d.toString();

        String info = "TRAP_DISARM_EVENT TRAP_SUCCESS " + d.getTrapSuccessChance();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitHealEvent(HealEvent h) {
        String reference = h.toString();

        String info = "HEAL_EVENT HEAL_AMOUNT " + h.getHealAmount();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitInstaDeathCommand(InstaDeathEvent i) {
        String reference = i.toString();

        String info = "INSTADEATH_EVENT";

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitLevelUpCommand(LevelUpEvent l) {
        String reference = l.toString();

        String info = "LEVELUP_EVENT";

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitObservationEvent(ObservationEvent o) {
        String reference = o.toString();

        String info = "OBSERVE_EVENT VALUE " + o.getValue();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitPickPocketEvent(PickPocketEvent p) {
        String reference = p.toString();

        String info = "PICK_POCKET_EVENT THIEVERY_CHANCE " + p.getThieveryNoticeChance();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitSlowdownEvent(SlowdownEvent s) {
        String reference = s.toString();

        String info = "SLOWDOWN_EVENT DURATION " + s.getDuration();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitStaminaDecreaseEvent(StaminaDecreaseEvent s) {
        String reference = s.toString();

        String info = "STAMINA_DECREASE_EVENT AMOUNT " + s.getStaminaDecreaseAmount();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitStartDialogEvent(StartDialogEvent d) {
        String reference = d.toString();

        String info = "START_DIALOG_EVENT VALUE " + d.getValue();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitToggleableHealthEvent(ToggleableHealthEvent h) {
        String reference = h.toString();

        String info = "TOGGLE_HEALTH_EVENT HASFIRED " + h.getHasFired() + " AMOUNT " + h.getAmount();

        eventReferenceAttributeMap.put(reference, info);
    }

    public void visitTransitionEvent(TransitionEvent t) {
        String reference = t.toString();

        Point tile = t.getStartingPoint();
        String info = "TRANSITION_EVENT DESTWORLD " + t.getDestinationWorld() + " DESTTILE " + tile.x + "," + tile.y;

        eventReferenceAttributeMap.put(reference, info);
    }

    // Entities
    public void visitEntity(Entity e) {
        string += "ENTITY TYPE " + e.getType()
                + " HP/MAX " + e.getCurrentHealth() + "/" + e.getMaxHealth()
                + " XP " + e.getExperience()
                + " STAMINA/MAX " + e.getCurrentStamina() + "/" + e.getMaxStamina()
                + " STAMINA_REGEN " + e.getStaminaRegenRate()
                + " CURRENCY " + e.getCurrentCurrency()
                + " ATTACK_SPEED " + e.getAttackSpeed() + " NEXT_ATTACK " + 0;

        string += " BACKPACK ";
        e.getBackpack().accept(this);

        string += " ATTACKDAMAGE " + e.getAttackDamage()
                + " ORIENTATION " + e.getOrientation().toString()
                + " SKILLMAP ";

        LinkedHashMap<Skill, Integer> skillMap = e.getSkillMap();

        for(Skill skill : skillMap.keySet()) {
            string += skill.toString();
            skill.accept(this);
            string += " LEVEL ";
            string += skillMap.get(skill) + " ";
        }

        string += "NONWEAPON ";

        List<Skill> nonWeaponSkills = e.getNonWeaponSkillList();

        for(Skill skill : nonWeaponSkills) {
            string += skill.toString() + " ";
            skill.accept(this);
        }

        if(e.getWeapon() != null) {
            e.getWeapon().accept(this);
        }
        if(e.getArmor() != null) {
            e.getArmor().accept(this);
        }
        if(e.getRing() != null) {
            e.getRing().accept(this);
        }

        string += " ACTIVE_SKILL " + e.getActiveSkill().toString()
                + " NEXT_MOVE_TIME " + 0
                + " MOVE_SPEED " + e.getMovementSpeed()
                + " NOISE_LEVEL " + e.getNoiseLevel()
                + " EQUIPPED_WEAPON " + ((e.getWeapon() != null) ? e.getWeapon().toString() : "null")
                + " EQUIPPED_ARMOR " + ((e.getArmor() != null) ? e.getArmor().toString() : "null")
                + " EQUIPPED_RING " + ((e.getRing() != null) ? e.getRing().toString() : "null")
                + " DEFAULT_WEAPON " + null
                + " " + e.toString();
    }

    // Inventories
    public void visitInventory(Inventory i) {
        string += "INVENTORY ";
        string += "SIZE " + i.getSizeLimit() + " ";

        for(int j = 0; j < i.getSizeLimit(); ++j) {
            if(i.getItemAtSlot(j) != null) {
                string += i.getItemAtSlot(j).toString() + " ";
                i.getItemAtSlot(j).accept(this);
            } else {
                string += "EMPTY ";
            }
        }
    }

    // Items
    public void visitArmorItem(ArmorItem a) {
        a.getItemType().accept(this, a);

        String reference = a.toString();

        String info = "ARMOR_ITEM NAME " + a.getName().replace(" ", "~")
                    + " EVENT " + a.getEvent().toString() + " TYPE " + tempItemTypeString;

        a.getEvent().accept(this);

        itemReferenceAttributeMap.put(reference, info);
    }

    public void visitConsumableItem(ConsumableItem c) {
        c.getItemType().accept(this, c);

        String reference = c.toString();

        String info = "CONSUMABLE_ITEM NAME " + c.getName().replace(" ", "~")
                + " EVENT " + c.getEvent().toString() + " TYPE " + tempItemTypeString;

        c.getEvent().accept(this);

        itemReferenceAttributeMap.put(reference, info);
    }

    public void visitInteractiveItem(InteractiveItem i) {
        i.getItemType().accept(this, i);

        String reference = i.toString();

        String info = "INTERACTIVE_ITEM EVENT " + i.getEffect().toString() + " TYPE " + tempItemTypeString;

        i.getEffect().accept(this);

        itemReferenceAttributeMap.put(reference, info);
    }

    public void visitOneshotItem(OneshotItem i) {
        i.getItemType().accept(this, i);

        String reference = i.toString();

        String info = "ONESHOT_ITEM EVENT " + i.getEffect().toString() + " TYPE " + tempItemTypeString;

        i.getEffect().accept(this);

        itemReferenceAttributeMap.put(reference, info);
    }

    public void visitRingItem(RingItem r) {
        r.getItemType().accept(this, r);

        String reference = r.toString();

        String info = "RING_ITEM NAME " + r.getName().replace(" ", "~")
                    + " EVENT " + r.getEvent().toString() + " TYPE " + tempItemTypeString;

        r.getEvent().accept(this);

        itemReferenceAttributeMap.put(reference, info);
    }

    public void visitTakeableItem(TakeableItem t) {
        t.getItemType().accept(this, t);

        String reference = t.toString();

        String info = "TAKEABLE_ITEM NAME " + t.getName().replace(" ", "~")
                    + " TYPE " + tempItemTypeString;

        itemReferenceAttributeMap.put(reference, info);
    }

    public void visitWeaponItem(WeaponItem w) {
        w.getItemType().accept(this, w);

        String reference = w.toString();

        String info = "WEAPON_ITEM NAME " + w.getName().replace(" ", "~")
                      + " SPEED " + w.getDefaultAttackSpeed()
                      + " COST " + w.getDefaultStaminaCost()
                      + " DAMAGE " + w.getAttackDamage()
                      + " SPREAD " + w.getSpread()
                      + " DIST " + w.getDistance()
                      + " EVENT " + w.getEventCommand().toString()
                      + " HOST_SKILL " + w.getSkill().toString()
                      + " TYPE " + tempItemTypeString;

        w.getEventCommand().accept(this);
        w.getSkill().accept(this);

        itemReferenceAttributeMap.put(reference, info);
    }

    // Skills
    public void visitSkill(Skill s) {
        String reference = s.toString();

        String info = "SKILL " + s.getName().replace(" ", "~")
                      + " BASEAMT " + s.getBaseAmount()
                      + " LEV_EFFECT_MOD " + s.getLevelEffectivenessModifier()
                      + " LEV_SUCC_MOD "  + s.getLevelSuccessModifier()
                      + " DIST_EFFECT_MOD " + s.getLevelEffectivenessModifier()
                      + " RANGE " + s.getMaxRange()
                      + " SPREAD " + s.getSpread()
                      + " SUCCESS_RATE " + s.getSuccessRate()
                      + " EVENT " + s.getEvent().toString()
                      + " COOLDOWN " + s.getCooldown()
                      + " NEXT_CAST_TIME " + 0;

        s.getEvent().accept(this);

        skillReferenceAttributeMap.put(reference, info);
    }

    // Traps
    public void visitTrap(Trap t) {
        string += "TRAP EVENT " + t.getEvent().toString();
        t.getEvent().accept(this);
        string += " STRENGTH " + t.getStrength()
                + " ISVISIBLE " + t.getIsVisible()
                + " HASNOTFIRED " + t.getHasNotFired()
                + " " + t.toString();
    }

    // World
    public void visitWorld(World w) {
        string += "WORLD\n";

        for(Point point : w.getAllTerrainPoints()) {
            string += "TERRAIN AT " + point.x + "," + point.y + " " + w.getTerrain(point).toString() + "\n";
        }

        for(Point point : w.getAllDecalPoints()) {
            string += "DECAL AT " + point.x + "," + point.y + " " + w.getDecal(point).toString() + "\n";
        }

        for(Point point : w.getAllEntityPoints()) {
            string += "ENTITY AT " + point.x + "," + point.y + " ";
            w.getEntity(point).accept(this);
            string += "\n";
        }

        for(Point point : w.getAllItemPoints()) {
            string += "ITEM AT " + point.x + "," + point.y + " ";
            w.getItem(point).accept(this);
            string += w.getItem(point).toString() + "\n";
        }

        for(Point point : w.getAllObstaclePoints()) {
            string += "OBSTACLE AT " + point.x + "," + point.y + " " + w.getObstacle(point).getId() + "\n";
        }

        for(Point point : w.getAllAreaEffectPoints()) {
            string += "AREA_EFFECT AT " + point.x + "," + point.y + " ";
            w.getAreaEffect(point).accept(this);
            string += "\n";
        }

        for(Point point : w.getAllTrapPoints()) {
            string += "TRAP AT " + point.x + "," + point.y + " ";
            w.getTrap(point).accept(this);
            string += "\n";
        }

        string += " SPAWNS ";

        for(Entity entity : w.getNpcSpawnLocations().keySet()) {
            Point point = w.getNpcSpawnLocations().get(entity);
            string += "NPC " + entity.toString() + " <==> " + point.x + "," + point.y + " ";
        }

        string += "END_SPAWNS\n";

        string += "ENDWORLD";
    }

    // Game Model
    public void visitGameModel(GameModel g) {
        string += "GAMEMODEL CURWORLDINDEX " + g.getCurWorldIndex() + " EntitySelectorIndex " + ModelDisplayableFactory.getEntityIndex() + " PLAYER " + g.getPlayer().toString();

        List<World> worlds = g.getWorlds();

        for(World world : worlds)
        {
            string += "\nWORLD " + worlds.indexOf(world) + " ";
            world.accept(this);
        }

        string += "\n";

        List<FriendlyNpcController> friendlyNpcControllers = g.getFriendlyNpcControllers();

        for(FriendlyNpcController friendlyNpcController : friendlyNpcControllers) {
            friendlyNpcController.accept(this);
        }

        List<HostileNpcController> hostileNpcControllers = g.getHostileNpcControllers();

        for(HostileNpcController hostileNpcController : hostileNpcControllers) {
            hostileNpcController.accept(this);
        }

        string += "\nTIMED_EVENTS\n";

        List<TimedEvent> timedEvents = g.getTimedEventList();

        for(TimedEvent timedEvent : timedEvents) {
            timedEvent.accept(this);
            string += "\n";
        }

        string += "ENDSAVE";

        string = stringifySkillMap() + stringifyItemMap() + stringifyEventMap() + string;
    }

    private String stringifySkillMap() {
        String mapString = "SKILL_REGISTRY\n";

        for(String key : skillReferenceAttributeMap.keySet()) {
            mapString += key + " " + skillReferenceAttributeMap.get(key) + "\n";
        }

        mapString += "END_SKILL_REGISTRY\n";

        return mapString;
    }

    private String stringifyItemMap() {
        String mapString = "ITEM_REGISTRY\n";

        for(String key : itemReferenceAttributeMap.keySet()) {
            mapString += key + " " + itemReferenceAttributeMap.get(key) + "\n";
        }

        mapString += "END_ITEM_REGISTRY\n";

        return mapString;
    }

    private String stringifyEventMap() {
        String mapString = "EVENT_REGISTRY\n";

        for(String key : eventReferenceAttributeMap.keySet()) {
            mapString += key + " " + eventReferenceAttributeMap.get(key) + "\n";
        }

        mapString += "END_EVENT_REGISTRY\n";

        return mapString;
    }

    // Utility classes
    public void visitPath(Path p) {
        string += " PATH ";

        ArrayList<Orientation> directions = p.getDirections();

        for(Orientation direction : directions) {
            string += direction.toString() + " ";
        }

        string += "COUNTER " + p.getCounter() + " ";
    }

    public void visitTimedEntityConfuseEvent(TimedEntityConfuseEvent tec) {
        string += " TIMED_CONFUSE_EVENT " + tec.getEntity().toString()
                + " COUNT_LEFT " + tec.getTurnCountLeft()
                + " OLD_PATH " + tec.getOldValue()
                + " NEW_PATH " + tec.getNewValue();
    }

    public void visitTimedEntityMovementSpeedEvent(TimedEntityMovementSpeedEvent tems) {
        string += " TIMED_CONFUSE_EVENT " + tems.getEntity().toString()
                + " COUNT_LEFT " + tems.getTurnCountLeft()
                + " OLD_PATH " + tems.getOldValue()
                + " NEW_PATH " + tems.getNewValue();
    }

    // ItemType/ItemSpecialzer visitors
    @Override
    public void visitDefault(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }

    @Override
    public void visitDoor(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }

    @Override
    public void visitOpenDoor(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }

    @Override
    public void visitKey(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }

    @Override
    public void visitHealthPotion(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }

    @Override
    public void visitDamagePotion(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }

    @Override
    public void visitSword(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }
    @Override
    public void visitChesplate(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }

    @Override
    public void visitBuffableRing(Item i) {
        tempItemTypeString = i.getItemType().toString();
    }
}