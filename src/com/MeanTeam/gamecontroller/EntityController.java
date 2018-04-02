package com.MeanTeam.gamecontroller;

import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.skills.Skill;

import java.util.List;

import com.MeanTeam.gameview.ModelDisplayableFactory;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.util.Specialty;

public class EntityController
{
    private Entity entity;

    public EntityController(Entity entity)
    {
        this.entity = entity;
    }

    public void pressNorth() {
        this.entity.setOrientation(Orientation.NORTH);
        this.entity.move();
    }

    public void pressNorthEast() {
        this.entity.setOrientation(Orientation.NORTHEAST);
        this.entity.move();
    }

    public void pressEast() {
        this.entity.setOrientation(Orientation.EAST);
        this.entity.move();
    }

    public void pressSouthEast() {
        this.entity.setOrientation(Orientation.SOUTHEAST);
        this.entity.move();
    }

    public void pressSouth() {
        this.entity.setOrientation(Orientation.SOUTH);
        this.entity.move();
    }

    public void pressSouthWest() {
        this.entity.setOrientation(Orientation.SOUTHWEST);
        this.entity.move();
    }

    public void pressWest() {
        this.entity.setOrientation(Orientation.WEST);
        this.entity.move();
    }

    public void pressNorthWest() {
        this.entity.setOrientation(Orientation.NORTHWEST);
        this.entity.move();
    }

    public void setActiveSkill(int index)
    {
        List<Skill> skills = entity.getSkills();
        if(index < skills.size())
            entity.setActiveSkill(skills.get(index));
    }

    public void useActiveSkill() {
        entity.useSkill(entity.getActiveSkill());

    }

    public void useWeaponAttack() {
        entity.useWeaponSkill();
    }

    public Specialty getSpecialty() {
        int index = ModelDisplayableFactory.getEntityIndex();
        switch(index) {
            case 0:
                return Specialty.DEFENDER;
            case 1:
                return Specialty.WISE_ONE;
            case 2:
                return Specialty.SCOUT;

        }
        return Specialty.DEFENDER;
    }
}
