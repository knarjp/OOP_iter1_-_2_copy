package com.MeanTeam.gamemodel.tile.entities;

import com.MeanTeam.gamemodel.notifiers.GameViewNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.skills.SkillPool;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.gamemodel.tile.inventory.Inventory;
import com.MeanTeam.visitors.Visitor;

import java.util.*;

public class Entity implements Visitable
{
    private EntityType type;
    private float currentHealth;
    private float maxHealth;
    private int experience;
    private int currentStamina;
    private int maxStamina;
    private int staminaRegenRate;
    private int currentCurrency;
    private long attackSpeed;
    private long nextAttackTime;
    private Inventory backpack;
    private WorldNotifier worldNotifier;
    private int attackDamage;
    // The convention is that 0 degrees represents North and the orientation varies clockwise
    // as orientation increases
    private Orientation orientation;
    private LinkedHashMap<Skill, Integer> skillMap;
    private List<Skill> nonWeaponSkillList;
    private Skill activeSkill = Skill.NULL;
    private long nextMoveTime;
    private long movementSpeed;
    private int noiseLevel; //max range that entities will start to notice player

    private WeaponItem equippedWeapon;
    private ArmorItem equippedArmor;
    private RingItem equippedRing;

    private WeaponItem defaultWeapon;

    public Entity(float maxHealth, int maxStamina,
                  int staminaRegenRate, long attackSpeed, Orientation orientation,
                  Inventory inventory, WorldNotifier worldNotifier,
                  WeaponItem defaultWeapon)
    {
        this(maxHealth, maxStamina, staminaRegenRate, attackSpeed, orientation, inventory, worldNotifier, defaultWeapon, EntityType.DEFAULT);
    }

    public Entity(float maxHealth, int maxStamina,
                  int staminaRegenRate, long attackSpeed, Orientation orientation,
                  Inventory inventory, WorldNotifier worldNotifier,
                  WeaponItem defaultWeapon, EntityType type)
    {
        this.currentHealth = maxHealth;
        this.maxHealth = maxHealth;
        this.experience = 0;
        this.currentStamina = maxStamina;
        this.maxStamina = maxStamina;
        this.staminaRegenRate = staminaRegenRate;
        this.currentCurrency = 100;
        this.attackSpeed = attackSpeed;
        this.orientation = orientation;
        this.backpack = inventory;
        this.worldNotifier = worldNotifier;
        this.skillMap = new LinkedHashMap<Skill, Integer>();
        this.nonWeaponSkillList = new ArrayList<>();
        nonWeaponSkillList.addAll(skillMap.keySet());
        this.nextMoveTime = 0;
        this.nextAttackTime = 0;
        this.movementSpeed = 0; //0500000000l for half-second move speed
        this.noiseLevel = 3; //base noise level of entity
        this.defaultWeapon = defaultWeapon;
        this.equippedWeapon = defaultWeapon;
        this.type = type;
    }

    public EntityType getType() { return type; }

    public void setCurrentStamina(int currentStamina) {
        this.currentStamina = currentStamina;
    }

    public void setEquippedWeapon(WeaponItem equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public void setEquippedArmor(ArmorItem equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    public void setEquippedRing(RingItem equippedRing) {
        this.equippedRing = equippedRing;
    }

    public int getCurrentCurrency() {
        return currentCurrency;
    }

    public void setCurrentCurrency(int currentCurrency) {
        this.currentCurrency = currentCurrency;
    }

    public long getNextAttackTime() {
        return nextAttackTime;
    }

    public LinkedHashMap<Skill, Integer> getSkillMap() {
        return skillMap;
    }

    public List<Skill> getNonWeaponSkillList() {
        return nonWeaponSkillList;
    }

    public WeaponItem getDefaultWeapon() {
        return defaultWeapon;
    }

    public boolean isNoiseLevelZero() {
        return modifiedNoiseLevel() == 0;
    }

    public int getNoiseLevel() {
        return modifiedNoiseLevel();
    }

    private int modifiedNoiseLevel() {
        return noiseLevel;
    }

    //Made this so that our tests for Npc chasing Player still work
    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public void creep() {
        if(noiseLevel == 0) {
            noiseLevel = 3;
            movementSpeed /= 2;
        } else if (noiseLevel == 3) {
            noiseLevel = 0;
            movementSpeed *= 2;
        }
    }

    public void decreaseStamina(int amount) {
        currentStamina = currentStamina - amount;
    }

    public void move() {
        worldNotifier.notifyMove(this);
    }

    public void useWeaponSkill() {
        if(equippedWeapon == null) {
            return;
        }
        if(!skillMap.containsKey(equippedWeapon.getSkill())) {
            return;
        }

        if(isAllowedToAttack() && currentStamina >= equippedWeapon.getDefaultStaminaCost()) {
            currentStamina -= equippedWeapon.getDefaultStaminaCost();
            nextAttackTime = System.nanoTime() + equippedWeapon.getDefaultAttackSpeed() + attackSpeed;

            equippedWeapon.setSkillModifiers();

            worldNotifier.notifySkillTrigger(this, equippedWeapon.getSkill());
        }
    }

    public void useSkill(Skill skill) {
        if(SkillPool.BACKSTAB == skill && !isNoiseLevelZero()) {
            return;
        }

        worldNotifier.notifySkillTrigger(this, skill);

        if( (SkillPool.BACKSTAB == skill || SkillPool.RANGED == skill) && isNoiseLevelZero()) {
            creep();
        }
    }

    public long getNextMoveTime() {
        return nextMoveTime;
    }

    public void updateNextMoveTime() {
        nextMoveTime = System.nanoTime() + movementSpeed;
    }

    public void addSkill(Skill skill, int startingLevel) {
        int curLevel = getSkillLevel(skill);

        if(curLevel <= startingLevel)
        {
            skillMap.put(skill, startingLevel);
        }

        if(!nonWeaponSkillList.contains(skill))
        {
            nonWeaponSkillList.add(skill);
        }
    }

    public void addWeaponSkill(Skill skill, int startingLevel) {
        int curLevel = getSkillLevel(skill);

        if(curLevel <= startingLevel) {
            skillMap.put(skill, startingLevel);
        }
    }

    public void setActiveSkill(Skill skill) {
        activeSkill = skill;
    }

    public void givePlayerItemFromBackpack() {
        backpack.transferRandomItemToPlayerInventory();
    }

    public void changeWorldNotifier(WorldNotifier newNotifier) {
        worldNotifier = newNotifier;

        if(getWeapon() != null) {
            equippedWeapon.setWorldNotifier(newNotifier);
        }
        if(getArmor() != null) {
            equippedArmor.setWorldNotifier(newNotifier);
        }
        if(getRing() != null) {
            equippedRing.setWorldNotifier(newNotifier);
        }
        if(defaultWeapon != null) {
            defaultWeapon.setWorldNotifier(newNotifier);
        }

        backpack.setNewItemWorldNotifiers(newNotifier);
    }

    public Orientation getOrientation() { return orientation; }

    public void setOrientation( Orientation orientation ) { this.orientation = orientation; }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(float currentHealth) {
       this.currentHealth = currentHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;

        if(this.currentHealth > this.maxHealth) {
            setCurrentHealth(this.maxHealth);
        }
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getCurrentStamina() {
        return currentStamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public int getStaminaRegenRate() {
        return staminaRegenRate;
    }

    public int getLevel() {
        return calculateLevel();
    }

    public int getSkillLevel(Skill skill) { return skillMap.getOrDefault(skill, 0); }

    public List<Skill> getSkills() { return nonWeaponSkillList; }

    public long getAttackSpeed() {
        return attackSpeed;
    }

    public String getObservationMessage(float successfulness)
    {
        Random rand = new Random();
        int roll = rand.nextInt(5);

        float errorBound = 100 - successfulness;

        if (errorBound < 0) {
            errorBound = 0;
        }

        String message = "";

        switch(roll) {
            case 0:
                float healthGuess = currentHealth + (errorBound / 10 * (rand.nextInt(3) -1));
                if(healthGuess < 0) {
                    healthGuess = 0;
                }
                message = "Health: " + healthGuess;
                break;
            case 1:
                if(equippedWeapon == null) {
                    message = "No equipped weapon";
                }
                else {
                    message = "Current weapon: " + equippedWeapon.getName();
                }
                break;
            case 2:
                float maxHealthGuess = maxHealth + (errorBound / 10 * (rand.nextInt(3) -1));
                if(maxHealthGuess < 0) {
                    maxHealthGuess = 0;
                }
                message = "Max Health:" + maxHealthGuess;
                break;
            case 3:
                float experienceGuess = experience + (errorBound / 10 * (rand.nextInt(3) -1));
                if(experienceGuess < 0) {
                    experienceGuess = 0;
                }
                message = "Current XP: " + experienceGuess;
                break;
            default:
                message = "This cat has really nice fur!";
        }

        return message;
    }

    public String getDialog()
    {
        Random rand = new Random();
        int roll = rand.nextInt(10);
        if(roll % 3 == 0)
            return "Hi there!";
        else if (roll % 3 == 1)
            return "Sup.";
        else
            return "What's goin on?";
    }

    public boolean isAllowedToMove() {
        return System.nanoTime() > nextMoveTime;
    }

    public boolean isAllowedToAttack() {
        return System.nanoTime() > nextAttackTime;
    }

    public void addHealth(float amount) {
        currentHealth += amount;

        if(currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    public void decreaseHealth(float amount) {
        if(currentHealth >= 0) {
            currentHealth -= amount;
            if(currentHealth < 0) {
                currentHealth = 0;
            }
        }
    }

    public void increaseSkillLevel(Skill skill)
    {
        int prevLevel = skillMap.getOrDefault(skill, 0);
        skillMap.put(skill, prevLevel + 1);
    }

    public void increaseExperience(int amount)
    {
        int previousLevel = calculateLevel();
        experience += amount;
        int delta = calculateLevel() - previousLevel;
        if(delta > 0)
        {
            try {
                GameViewNotifier.notifyLevelUp(this, delta);
            } catch(Exception e) {}

        }

    }

    private int calculateLevel() {
        return experience / 100;
    }


    public void levelUp()
    {
        int experienceToAdd = 100 - (this.getExperience() % 100);
        this.increaseExperience(experienceToAdd);
    }

    public void kill() {
        currentHealth = 0;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public void setMovementSpeed(long newSpeed) {
        movementSpeed = newSpeed;
    }

    public long getMovementSpeed() { return movementSpeed; }

    public boolean addItemToBackpack(TakeableItem item) {
        return backpack.addItem(item);
    }

    public void removeItemFromBackpack(TakeableItem item){
        backpack.removeItem(item);
    }

    public void removeItemFromBackpack(int slot) {
        backpack.removeItem(slot);
    }

    public boolean hasFreeBackPackSlot() {
        return backpack.hasFreeSlot();
    }

    public boolean hasEquipped(TakeableItem item){
        return item.equals(equippedWeapon) || item.equals(equippedArmor) || item.equals(equippedRing);
    }
    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }
    public boolean hasItemInBackpack(TakeableItem item){
        return backpack.hasItem(item) || hasEquipped(item);
    }

    public Inventory getBackpack() { return backpack; }

    public List<TakeableItem> getActiveEquipment() {

        List<TakeableItem> activeEquipment = new ArrayList<>();

        if(equippedWeapon != null) {
            activeEquipment.add(equippedWeapon);
        }

        if(equippedArmor != null) {
            activeEquipment.add(equippedArmor);
        }

        if(equippedRing != null) {
            activeEquipment.add(equippedRing);
        }

        return activeEquipment;
    }

    public void equipWeapon(WeaponItem weapon) {
        if(skillMap.containsKey(weapon.getSkill())) {
            if (equippedWeapon == defaultWeapon) {
                this.equippedWeapon = weapon;
                backpack.removeItem(weapon);

                equippedWeapon.changeHostSkillEvent();
            } else {
                backpack.removeItem(weapon);
                addItemToBackpack(equippedWeapon);
                this.equippedWeapon = weapon;

                equippedWeapon.changeHostSkillEvent();
            }
        }
    }

    public WeaponItem getWeapon() {
        return this.equippedWeapon;
    }

    public void unequipWeapon() {
        if (equippedWeapon != defaultWeapon) {
            if(hasFreeBackPackSlot()) {
                this.addItemToBackpack(equippedWeapon);
                equippedWeapon = defaultWeapon;
            }
        }
    }

    public void equipArmor(ArmorItem armor) {
        if (equippedArmor != null) {
                equippedArmor.fireEvent(this);

                backpack.removeItem(armor);
                this.addItemToBackpack(equippedArmor);

                this.equippedArmor = armor;
                armor.fireEvent(this);
        } else {
            this.equippedArmor = armor;
            armor.fireEvent(this);

            backpack.removeItem(armor);
        }
    }

    public void unequipArmor() {
        if (equippedArmor != null) {
            if(hasFreeBackPackSlot()) {
                equippedArmor.fireEvent(this);
                this.addItemToBackpack(equippedArmor);
                equippedArmor = null;
            }
        }
    }

    public ArmorItem getArmor() {
        return this.equippedArmor;
    }

    public void equipRing(RingItem ring) {
        if (equippedRing != null) {
                equippedRing.fireEvent(this);
                backpack.removeItem(ring);

                this.addItemToBackpack(equippedRing);

                this.equippedRing = ring;
                ring.fireEvent(this);
        } else {
            this.equippedRing = ring;
            ring.fireEvent(this);

            backpack.removeItem(ring);
        }
    }

    public void unequipRing() {
        if (equippedRing != null) {
            if(hasFreeBackPackSlot()) {
                equippedRing.fireEvent(this);
                this.addItemToBackpack(equippedRing);
                equippedRing = null;
            }
        }
    }

    public RingItem getRing() {
        return this.equippedRing;
    }

    public void useConsumableItem(ConsumableItem consumableItem) {
        if(hasItemInBackpack(consumableItem)) {
            consumableItem.fireEvent(this);
            removeItemFromBackpack(consumableItem);
        }
    }

    public void regenStamina() {
        currentStamina = currentStamina + staminaRegenRate;
        if(currentStamina > maxStamina) {
            currentStamina = maxStamina;
        }
    }

    public Skill getActiveSkill() {
        return activeSkill;
    }

    public void accept(Visitor v) {
        v.visitEntity(this);
    }
}