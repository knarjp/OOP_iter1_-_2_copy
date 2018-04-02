package com.MeanTeam.gamemodel.tile.skills;

import com.MeanTeam.gamemodel.tile.effectcommand.*;

public class SkillPool
{
    //Testing
    public static final Skill damageRing = new Skill("DMG_RING", 1, 360,
            100, 10, 0, 0, 0, new DamageEvent(0), 1);

    //Common
    public static final Skill BIND_WOUNDS = new Skill("Lick Wounds", 0, 0,
            75, 10, 5, 0, 0, new HealEvent(0), 1);

    public static final Skill BARGAIN = new Skill("Poof", 1, 0,
            100, 10, 5, 0, 0, new StartDialogEvent(0), 1);

    public static final Skill OBSERVE = new Skill("Taste the Air", 4, 360,
            100, 10, 5, 0, 5, new ObservationEvent(0), 1);

    //Smashers
    public static final Skill BRAWLING = new Skill("Paw Attack", 1, 0,
            81, 0, 5, 4, 0, new DamageEvent(0), 1);

    public static final Skill ONE_HANDED = new Skill("Tail Attack", 1, 0,
            71, 0, 7, 4, 0, new DamageEvent(0), 1);

    public static final Skill TWO_HANDED = new Skill("Full Body Attack", 1, 90,
            61, 0, 10, 4, 0, new DamageEvent(0), 1);

    //Summoners
    public static final Skill STAFF = new Skill("Scold", 1, 0,
            81, 0, 5, 0, 0, new DamageEvent(0), 1);

    public static final Skill ENCHANTMENT = new Skill("Mystify", 5, 0,
            50, 0, 5, 4, 0, new DamageEvent(0), 1);

    public static final Skill BANE = new Skill("Dark Aura", 1, 0,
            71, 0, 5, 4, 0, new DamageEvent(0), 1);

    public static final Skill BOON = new Skill("Light Aura", 1, 0,
            71, 0, 5, 4, 0, new DamageEvent(0), 1);

    //Sneaks
    //public static final Skill PICK_POCKET = new Skill("Thieve", 1, 0,
    //        1, 10, 5, 0, 0, new PickPocketEvent(0));

  //  public static final Skill DETECT_REMOVE_TRAP = new Skill("Sense Danger", 0, 0,
  //          71, 10, 5, 4, 0, new DisarmTrapEvent(0));

    //public static final Skill CREEP = new Skill("Stalk", 0, 0,
    //        100, 0, 0, 0, 0, new CreepEvent(0), 1);

    public static final Skill BACKSTAB = new Skill("Backstab", 1, 0,
            100, 75, 0, 0, 0, new DamageEvent(0), 1);


    public static final Skill RANGED = new Skill("Sling", 5, 0,
            71, 10, 5, 0, 1, new DamageEvent(0), 1);
}
