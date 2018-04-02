package com.MeanTeam.gamemodel.tile.items;

import com.MeanTeam.visitors.Visitor;

public enum ItemType
{
    DEFAULT(Visitor::visitDefault),
    DOOR(Visitor::visitDoor),
    OPEN_DOOR(Visitor::visitOpenDoor),
    KEY(Visitor::visitKey),
    HEALTH_POTION(Visitor::visitHealthPotion),
    DAMAGE_POTION(Visitor::visitDamagePotion),
    SWORD(Visitor::visitSword),
    CHESTPLATE(Visitor::visitChesplate),
    BUFFABLERING(Visitor::visitBuffableRing),
    COBRA_BOW(Visitor::visitDefault),
    PORCUPINE_TAIL(Visitor::visitDefault),
    ROCKS(Visitor::visitDefault),
    BODY_BLADE(Visitor::visitDefault),
    BODY_PEPPER_PELT(Visitor::visitDefault),
    BODY_SPIKE(Visitor::visitDefault),
    METAL_TAIL(Visitor::visitDefault),
    POISON_TAIL(Visitor::visitDefault),
    TAIL_BARBS(Visitor::visitDefault),
    BLUE_SCROLL(Visitor::visitDefault),
    GREEN_SCROLL(Visitor::visitDefault),
    ORANGE_SCROLL(Visitor::visitDefault),
    BROOM(Visitor::visitDefault),
    STAFF1(Visitor::visitDefault),
    WOOD_STAFF(Visitor::visitDefault);

    private ItemSpecializer specializer;

    ItemType(ItemSpecializer specializer)
    {
        this.specializer = specializer;
    }

    public void accept(Visitor v, Item i) { specializer.accept(v, i); }

    // ItemSpecializer interface:
    private interface ItemSpecializer
    {
        void accept(Visitor v, Item i);
    }
}
