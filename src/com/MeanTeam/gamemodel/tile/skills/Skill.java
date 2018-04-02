package com.MeanTeam.gamemodel.tile.skills;

import com.MeanTeam.gamemodel.tile.effectcommand.SettableEventCommand;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.visitors.Visitor;

public class Skill implements Visitable {
    private final String name;
    private int baseAmount;
    private int levelEffectivenessModifier;
    private int levelSuccessModifier;
    private double distanceEffectivenessModifier;
    private int maxRange;
    private int spread;
    private int successRate;
    private SettableEventCommand event;
    private int cooldown;
    private long nextCastableTime;

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public long getNextCastableTime() {
        return nextCastableTime;
    }

    public void setNextCastableTime(long nextCastableTime) {
        this.nextCastableTime = nextCastableTime;
    }

    public Skill(String name, int maxRange, int spread, int successRate, int baseAmount,
                 int levelEffectivenessModifier, int levelSuccessModifier, double distanceEffectivenessModifier, SettableEventCommand event, int cooldown){
        this.name = name;
        this.baseAmount = baseAmount;
        this.maxRange = maxRange;
        this.spread = spread;
        this.successRate = successRate;
        this.levelEffectivenessModifier = levelEffectivenessModifier;
        this.levelSuccessModifier = levelSuccessModifier;
        this.distanceEffectivenessModifier = distanceEffectivenessModifier;
        this.event = event;
        this.cooldown = cooldown;
        this.nextCastableTime = System.currentTimeMillis();
    }

    public Skill(String name, int maxRange, int spread, int successRate, int baseAmount,
                 int levelEffectivenessModifier, int levelSuccessModifier, double distanceEffectivenessModifier, SettableEventCommand event) {
        this.name = name;
        this.baseAmount = baseAmount;
        this.maxRange = maxRange;
        this.spread = spread;
        this.successRate = successRate;
        this.levelEffectivenessModifier = levelEffectivenessModifier;
        this.levelSuccessModifier = levelSuccessModifier;
        this.distanceEffectivenessModifier = distanceEffectivenessModifier;
        this.event = event;
        this.cooldown = 0;
        this.nextCastableTime = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public int getMaxRange() { return maxRange; }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    public void setSpread(int spread) {
        this.spread = spread;
    }

    public int getSpread() { return spread; }

    public void trigger(Entity target, int distance, int level) {
        int modifiedBase = baseAmount + level * levelEffectivenessModifier;
        int adjustedAmount = (int)(modifiedBase - (modifiedBase * .1 * distanceEffectivenessModifier * (distance - 1)));

        event.setValue(adjustedAmount);

        boolean succeeded = calculateSuccess(level);
        //if(succeeded && !isOnCooldown()) {
        if(succeeded){
            event.trigger(target);
        } else {
            event.fail(target);
        }

        //    System.out.println("Skill not on cooldown");
        //    nextCastableTime = System.currentTimeMillis() + (cooldown * 1000);
        //} else {
        //    System.out.println("Skill failed or is on cooldown");
        //}
    }
    public boolean isOnCooldown(){
        if(cooldown == 0)
            return false;
        else
            return nextCastableTime >= System.currentTimeMillis() ? true : false;}

    private boolean calculateSuccess(int level) {
        return (int)(Math.random() * 100 + 1) <= adjustedSuccessRate(level);
    }

    private int adjustedSuccessRate(int level) {
        return successRate + level * levelSuccessModifier;
    }

    public void setEvent(SettableEventCommand event) {
        this.event = event;
    }

    public SettableEventCommand getEvent() {
        return event;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    public static Skill NULL = new Skill("NONE", 0, 0, 0, 0, 0, 0, 0, SettableEventCommand.NULL, 0);

    public int getBaseAmount() {
        return baseAmount;
    }

    public int getLevelEffectivenessModifier() {
        return levelEffectivenessModifier;
    }

    public int getLevelSuccessModifier() {
        return levelSuccessModifier;
    }

    public double getDistanceEffectivenessModifier() {
        return distanceEffectivenessModifier;
    }

    public int getSuccessRate() {
        return successRate;
    }

    public void accept(Visitor v) {
        v.visitSkill(this);
    }
}
