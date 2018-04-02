package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.gamemodel.notifiers.GameModelNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.effectcommand.SettableEventCommand;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.visitors.Visitor;

public class WeaponItem extends TakeableItem {

    private long defaultAttackSpeed;
    private float defaultStaminaCost;
    private int attackDamage;
    private int spread;
    private int distance;
    private SettableEventCommand eventCommand;
    private Skill skill;

    public WeaponItem(String name, ItemType type, WorldNotifier worldNotifier, long attackSpeed, float staminaCost, int attackDamage, int spread, int distance,
                      SettableEventCommand eventCommand, Skill skill, GameModelNotifier modelNotifier) {
        super(name, type, worldNotifier, modelNotifier);

        this.defaultAttackSpeed = attackSpeed;
        this.defaultStaminaCost = staminaCost;
        this.attackDamage = attackDamage;
        this.eventCommand = eventCommand;
        this.spread = spread;
        this.distance = distance;
        this.skill = skill;
    }

    @Override
    public void equipToPlayer() {
        getModelNotifier().notifyPlayerWeaponEquip(this);
    }

    public long getDefaultAttackSpeed() {
        return defaultAttackSpeed;
    }

    public float getDefaultStaminaCost() {
        return defaultStaminaCost;
    }

    public Skill getSkill() {
        return skill;
    }

    public void changeHostSkillEvent() {
        skill.setEvent(eventCommand);
    }

    public void setSkillModifiers() {
        skill.setSpread(spread);
        skill.setMaxRange(distance);
        skill.setBaseAmount(attackDamage);
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public int getSpread() {
        return spread;
    }

    public int getDistance() {
        return distance;
    }

    public SettableEventCommand getEventCommand() {
        return eventCommand;
    }

    public void accept(Visitor v) {
        v.visitWeaponItem(this);
    }
}
