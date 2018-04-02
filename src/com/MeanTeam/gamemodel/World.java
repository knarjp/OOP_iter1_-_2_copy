package com.MeanTeam.gamemodel;

import com.MeanTeam.gamemodel.notifiers.GameViewNotifier;
import com.MeanTeam.gamemodel.tile.Decal;
import com.MeanTeam.gamemodel.tile.Obstacle;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.items.TakeableItem;
import com.MeanTeam.gamemodel.tile.skills.Skill;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.gamemodel.tile.areaeffects.AreaEffect;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.gamemodel.tile.items.Item;
import com.MeanTeam.visitors.Visitor;

import java.util.*;

import java.awt.*;
import java.util.List;

public class World implements Visitable
{
    private Map<Point, Terrain> terrainMap;
    private Map<Point, Entity> entityMap;
    private Map<Point, AreaEffect> effectMap;
    private Map<Point, Item> itemMap;
    private Map<Point, Obstacle> obstacleMap;
    private Map<Point, Decal> decalMap;
    private Map<Point, Trap> trapMap;

    private Queue<Entity> entityMoveQueue;
    private Queue<EntitySkillPair> entitySkillPairQueue;

    private Map<Entity, Point> npcSpawnLocations;

    public World()
    {
        this.terrainMap = new HashMap<>();
        this.entityMap = new HashMap<>();
        this.effectMap = new HashMap<>();
        this.itemMap = new HashMap<>();
        this.obstacleMap = new HashMap<>();
        this.decalMap = new HashMap<>();
        this.trapMap = new HashMap<>();
        this.entityMoveQueue = new LinkedList<>();
        this.entitySkillPairQueue = new LinkedList<>();
        this.npcSpawnLocations = new HashMap<>();
    }

    public Map<Entity, Point> getNpcSpawnLocations() {
        return npcSpawnLocations;
    }

    public void add(Point point, Terrain terrain) { terrainMap.put(point, terrain); }
    public void addPlayer(Point point, Entity entity) { entityMap.put(point, entity); }
    public void add(Point point, AreaEffect effect) { effectMap.put(point, effect); }
    public void add(Point point, Item item) { itemMap.put(point, item); }
    public void add(Point point, Obstacle obstacle) { obstacleMap.put(point, obstacle); }
    public void add(Point point, Decal decal) { decalMap.put(point, decal); }
    public void add(Point point, Trap trap) { trapMap.put(point, trap); }
    public void addNpc(Point point, Entity entity) {
        entityMap.put(point, entity);
        npcSpawnLocations.put(entity, point);
    }

    public void removeTerrain(Point point) { terrainMap.remove(point); }
    public void removeEntity(Point point) { entityMap.remove(point); }
    public void removeAreaEffect(Point point) { effectMap.remove(point); }
    public void removeItem(Point point) { itemMap.remove(point); }
    public void removeItem(Item item) {
        for(Point point : getAllEntityPoints())
        {
            if(getItem(point) == item)
            {
                itemMap.remove(point);
                return;
            }
        }
    }
    public void removeObstacle(Point point) { obstacleMap.remove(point); }
    public void removeDecal(Point point) { decalMap.remove(point); }
    public void removeTrap(Point point) { trapMap.remove(point); }

    public boolean hasItemAtPoint(Point point) {
        return itemMap.containsKey(point);
    }
    public boolean hasAreaEffectAtPoint(Point point) {
        return effectMap.containsKey(point);
    }

    public Terrain getTerrain(Point point) { return terrainMap.getOrDefault(point, Terrain.NONE); }
    public Entity getEntity(Point point) { return entityMap.get(point); }
    public AreaEffect getAreaEffect(Point point) { return effectMap.get(point); }
    public Item getItem(Point point) { return itemMap.get(point); }
    public Obstacle getObstacle(Point point) { return obstacleMap.get(point); }
    public Decal getDecal(Point point) { return decalMap.get(point); }
    public Trap getTrap(Point point) { return trapMap.get(point); }

    public Collection<Point> getAllEntityPoints() { return entityMap.keySet(); }
    public Collection<Point> getAllTerrainPoints() { return terrainMap.keySet(); }
    public Collection<Point> getAllAreaEffectPoints() { return effectMap.keySet(); }
    public Collection<Point> getAllItemPoints() { return itemMap.keySet(); }
    public Collection<Point> getAllObstaclePoints() { return obstacleMap.keySet(); }
    public Collection<Point> getAllDecalPoints() { return decalMap.keySet(); }
    public Collection<Point> getAllTrapPoints() { return trapMap.keySet(); }

    public void resetNpcs() {
        for(Map.Entry<Entity, Point> entry: npcSpawnLocations.entrySet()) {
            Point currLocation = getEntityLocation(entry.getKey());
            if(currLocation != null)
                moveEntity(currLocation, entry.getValue());
        }
    }

    public List<Entity> removeDeadEntities() {
        List<Entity> removed = new ArrayList<>();
        List<Entity> entities = new ArrayList<>(entityMap.values());
        for(Entity entity: entities) {
            if(entity.isDead()) {
                entityMap.remove(getEntityLocation(entity));
                npcSpawnLocations.remove(entity);
                removed.add(entity);
            }
        }
        return removed;
    }

    public void doInteractions(Entity player) {
        triggerAreaEffects();
        triggerItems(player);
        triggerTraps();
    }

    private void triggerAreaEffects() {
        ArrayList<Point> points = new ArrayList<>(entityMap.keySet());
        for(Point point : points) {
            Entity entity = getEntity(point);

            AreaEffect effect = effectMap.get(point);

            if (effect != null) {
                effect.trigger(entity);
            }
        }
    }

    private void triggerItems(Entity player) {
        ArrayList<Point> entities = new ArrayList<>(entityMap.keySet());
        for(Point point : entities) {
            Entity entity = getEntity(point);

            Item item = itemMap.get(point);

            if (item != null) {
                boolean entityIsPlayer = entity.equals(player);

                item.trigger(entity, entityIsPlayer);
            }
        }
    }

    public void triggerTraps() {
        for(Point point : entityMap.keySet()) {
            Entity entity = getEntity(point);

            Trap trap = trapMap.get(point);

            if(trap != null) {
                trap.fireTrap(entity);
            }
        }
    }

    public boolean attemptGiveItemToEntity(Entity entity, TakeableItem item) {
        if(entity.hasFreeBackPackSlot()) {
            entity.addItemToBackpack(item);

            return true;
        } else {
            return false;
        }
    }

    public void addEntityToMoveQueue(Entity entity) {
        if(!entityMoveQueue.contains(entity)) {
            entityMoveQueue.add(entity);
       }
    }

    public void addEntityToUseSkillQueue(Entity entity, Skill skill) {
        EntitySkillPair pair = new EntitySkillPair(entity, skill);
        if(!entitySkillPairQueue.contains(pair)) {
            entitySkillPairQueue.add(pair);
        }
    }

    private class EntitySkillPair {
        Entity entity;
        Skill skill;

        EntitySkillPair(Entity entity, Skill skill) {
            this.entity = entity;
            this.skill = skill;
        }

        public Entity getEntity() {
            return entity;
        }

        public Skill getSkill() {
            return skill;
        }

        public boolean equals(Object other) {
            if(other == null) return false;
            if(other == this) return true;
            return this.entity == ((EntitySkillPair)other).entity;
        }
    }

    public void processSkills() {
        Queue<EntitySkillPair> snapshot = new LinkedList<>(entitySkillPairQueue);
        entitySkillPairQueue.clear();
        while(!snapshot.isEmpty()) {
            EntitySkillPair pair = snapshot.poll();
            Entity entity = pair.getEntity();
            Skill skill = pair.getSkill();

            //self skills
            if(skill.getMaxRange() == 0 && skill.getSpread() == 0){
                skill.trigger(entity, 0, entity.getSkillLevel(skill));
            } else {
                List<Entity> effectedEntities = null;
                if(skill.getSpread() == 0) {
                    effectedEntities = linearEffectGetAllEntities(entity, skill.getMaxRange());
                } else if(skill.getSpread() == 90) {
                    effectedEntities = angularEffectGetAllEntities(entity, skill.getMaxRange());
                } else if(skill.getSpread() == 360) {
                    effectedEntities = radialEffectGetAllEntities(entity, skill.getMaxRange());
                }

                if(effectedEntities != null) {
                    for(Entity target: effectedEntities) {
                        skill.trigger(target, getInfluenceRadius(entity, target), entity.getSkillLevel(skill));
                    }
                }
            }
        }
    }

    public void attemptMoves() {
        Queue<Entity> snapshot = new LinkedList<>(entityMoveQueue);
        entityMoveQueue.clear();
        while(!snapshot.isEmpty()) {
            Entity entity = snapshot.poll();
            if(System.nanoTime() <= entity.getNextMoveTime())
                continue;

            /*int orientation = entity.getOrientation();
            Point sourcePoint = getEntityLocation(entity);
            float radians = (float) ((orientation + 90) * Math.PI / 180);
            int dx = Math.round((float) Math.cos(radians));
            int dy = -1 * Math.round((float) Math.sin(radians));*/
            Orientation orientation = entity.getOrientation();
            Point sourcePoint = getEntityLocation(entity);
            int dx = orientation.getDx();
            int dy = orientation.getDy();
            System.out.println("dx: " + dx + " | dy: " + dy);

            Point destination = new Point(sourcePoint.x + dx, sourcePoint.y + dy);
            System.out.println("Entity at [" + (int) sourcePoint.getX() + "," + (int) sourcePoint.getY() + "] attempting move to ["
                    + (int) destination.getX() + "," + (int) destination.getY() + "]");

            if (isValidMove(destination)) {
                moveEntity(sourcePoint, destination);
            } else {
                if(entityMap.get(destination) != null)
                {
                    GameViewNotifier.notifyInteraction(entityMap.get(destination));
                }
                else
                    System.out.println("Move is illegal, there is " + getTerrain(destination).toString() + " there.");
            }

            entity.updateNextMoveTime();
        }
    }

    public void updateEntityStats() {
        for(Entity e: entityMap.values()) {
            e.regenStamina();
        }
    }

    public Point getEntityLocation(Entity entity) {
        for(Point point : getAllEntityPoints())
        {
            if(getEntity(point) == entity)
            {
                return point;
            }
        }
        return null;
    }

    public boolean isValidMove(Point destination) {
        if(getTerrain(destination) != Terrain.GRASS) {
            return false;
        }
        if(getEntity(destination) != null) {
            return false;
        }
        if(getObstacle(destination) != null) {
            return false;
        }
        return true;
    }

    public void moveEntity(Point origin, Point destination) {
        Entity entity = entityMap.get(origin);
        entityMap.remove(origin);
        entityMap.put(destination, entity);
    }

    public Entity linearEffectGetClosestEntity(Entity entity) {
        return linearEffectGetClosestEntity(getEntityLocation(entity), entity.getOrientation());
    }

    public Entity linearEffectGetClosestEntity(Entity entity, int distance) {
        return linearEffectGetClosestEntity(getEntityLocation(entity), entity.getOrientation(), distance);
    }

    public Entity linearEffectGetClosestEntity(Point startingPoint, Orientation orientation) {
        return linearEffectGetClosestEntity(startingPoint, orientation, Integer.MAX_VALUE);
    }

    //Gets the closest entity to entityPoint in the direction of orientation, out to a max of distance
    //points away
    public Entity linearEffectGetClosestEntity(Point startingPoint, Orientation orientation, int distance) {
        if(startingPoint == null)
            return null;

        //get the next point to query for entities
        Point nextPoint = getNextPointFromOrientation(startingPoint, orientation);
        Entity closestEntity = null;

        //keep looking until we find an entity, we reach the edge of the world, or we reach the max distance
        while(closestEntity == null && nextPoint != null && distance != 0) {
            //update closest entity to the entity at the point we are querying
            closestEntity = getEntity(nextPoint);
            //update nextPoint to one point further away in the direction of orientation
            nextPoint = getNextPointFromOrientation(nextPoint, orientation);
            //decrement distance
            --distance;
        }

        //return the found (or not found) entity
        return closestEntity;
    }

    public List<Entity> linearEffectGetAllEntities(Entity entity) {
        return linearEffectGetAllEntities(getEntityLocation(entity), entity.getOrientation());
    }

    public List<Entity> linearEffectGetAllEntities(Entity entity, int distance) {
        return linearEffectGetAllEntities(getEntityLocation(entity), entity.getOrientation(), distance);
    }

    public List<Entity> linearEffectGetAllEntities(Point startingPoint, Orientation orientation){
        return linearEffectGetAllEntities(startingPoint, orientation, Integer.MAX_VALUE);
    }

    //Gets all entities in the direction of orientation starting at the point next to startingPoint
    //up to a max of distance points away
    public List<Entity> linearEffectGetAllEntities(Point startingPoint, Orientation orientation, int distance){
        if(startingPoint == null)
            return null;

        //get the next point to query for entities
        Point nextPoint = getNextPointFromOrientation(startingPoint, orientation);
        List<Entity> entities = new ArrayList<>();
        //keep looking until we reach the edge of the world or reach the max distance
        while(nextPoint != null && distance != 0) {
            //if there is an entity at the point, add it to the list
            Entity currEntity = getEntity(nextPoint);
            if(currEntity != null) {
                entities.add(currEntity);
            }
            //update nextPoint to one point further away in the direction of orientation
            nextPoint = getNextPointFromOrientation(nextPoint, orientation);
            //decrement the distance
            --distance;
        }

        return entities;
    }

    public List<Entity> angularEffectGetAllEntities(Entity entity) {
        return angularEffectGetAllEntities(getEntityLocation(entity), entity.getOrientation(), Integer.MAX_VALUE);
    }

    public List<Entity> angularEffectGetAllEntities(Entity entity, int distance) {
        return angularEffectGetAllEntities(getEntityLocation(entity), entity.getOrientation(), distance);
    }

    public List<Entity> angularEffectGetAllEntities(Point startingPoint, Orientation orientation) {
        return angularEffectGetAllEntities(startingPoint, orientation, Integer.MAX_VALUE);
    }

    //Gets all entities in a 90 degree arc centered in the direction of orientation starting at
    //(but not including) startingPoint up to a max of distance points away
    //This function is based on the observation that a angular effect is really a linear effect
    //with the same orientation, plus a perpendicular linear effect at every point on the main
    //linear effect
    public List<Entity> angularEffectGetAllEntities(Point startingPoint, Orientation orientation, int distance) {
        if(startingPoint == null)
            return null;

        List<Entity> entities = new ArrayList<>();

        //special edge case for NW, SW, SE, and NE orientations to make sure we get the two points
        //directly next to the startingPoint
        if(distance != 0 && (orientation == Orientation.NORTHEAST || orientation == Orientation.SOUTHEAST
                            || orientation == Orientation.SOUTHWEST || orientation == Orientation.NORTHWEST)){
            Point gapPoint = getNextPointFromOrientation(startingPoint, orientation.getLeft45());
            if (gapPoint != null && getEntity(gapPoint) != null)
                entities.add(getEntity(gapPoint));
            gapPoint = getNextPointFromOrientation(startingPoint, orientation.getRight45());
            if (gapPoint != null && getEntity(gapPoint) != null)
                entities.add(getEntity(gapPoint));
        }

        //get the perpendicular orientations for the cross linear effects
        Orientation perpendicularOrientationLeft = orientation.getLeft90();
        Orientation perpendicularOrientationRight = orientation.getRight90();

        //get the next point on the main linear effect to query for entities
        Point nextPoint = getNextPointFromOrientation(startingPoint, orientation);
        //spread indicates how many points outward the cross linear effects should search
        int spread = 1;
        //loop until the main linear effect reaches the edge of the world, or it reaches the max distance
        while(nextPoint != null && distance != 0) {
            //if the point on the main linear effect has an entity, add it to the list
            if(getEntity(nextPoint) != null)
                entities.add(getEntity(nextPoint));

            //add all the entities found by the cross linear effects
            entities.addAll(linearEffectGetAllEntities(nextPoint, perpendicularOrientationLeft, spread));
            entities.addAll(linearEffectGetAllEntities(nextPoint, perpendicularOrientationRight, spread));

            //edge case for NW, SW, SE, and NE orientations to include points that would otherwise
            //not be reached by the cross linear effects
            if(orientation == Orientation.NORTHWEST || orientation == Orientation.SOUTHEAST
                                || orientation == Orientation.SOUTHWEST || orientation == Orientation.NORTHEAST){
                //get all the entities found by the cross linear effects
                Point gapPoint = getNextPointFromOrientation(nextPoint, orientation.getLeft45());
                if(gapPoint != null){
                    if(getEntity(gapPoint) != null)
                        entities.add(getEntity(gapPoint));
                    entities.addAll(linearEffectGetAllEntities(gapPoint, perpendicularOrientationLeft, spread));
                    entities.addAll(linearEffectGetAllEntities(gapPoint, perpendicularOrientationRight, spread+1));
                }
            }

            //update nextPoint to one point further away in the direction of orientation
            nextPoint = getNextPointFromOrientation(nextPoint, orientation);
            //increase the spread by one
            ++spread;
            //decrement the distance
            --distance;
        }

        return entities;
    }

    public List<Entity> radialEffectGetAllEntities(Entity entity) {
        return radialEffectGetAllEntities(getEntityLocation(entity), Integer.MAX_VALUE);
    }

    public List<Entity> radialEffectGetAllEntities(Entity entity, int distance) {
        return radialEffectGetAllEntities(getEntityLocation(entity), distance);
    }

    public List<Entity> radialEffectGetAllEntities(Point point) {
        return radialEffectGetAllEntities(point, Integer.MAX_VALUE);
    }

    //Gets all the entites in a 360 degree area around but not including the startingPoint
    //This function is based on the observation that a radial effect is really just four
    //angular effects in the four cardinal directions (ignoring duplicates)
    public List<Entity> radialEffectGetAllEntities(Point startingPoint, int distance) {
        if(startingPoint == null)
            return null;

        //using a set so we don't get duplicate entities
        HashSet<Entity> entitiesSet = new HashSet<>();
        //get all the entities from angular effects in the four cardinal directions
        entitiesSet.addAll(angularEffectGetAllEntities(startingPoint, Orientation.NORTH, distance));
        entitiesSet.addAll(angularEffectGetAllEntities(startingPoint, Orientation.WEST, distance));
        entitiesSet.addAll(angularEffectGetAllEntities(startingPoint, Orientation.SOUTH, distance));
        entitiesSet.addAll(angularEffectGetAllEntities(startingPoint, Orientation.EAST, distance));

        //add all the entities from the set to a list
        List<Entity> entities = new ArrayList<>(entitiesSet);
        return entities;
    }

    //Given a point and an orientation, will return the point next to point in the direction
    //of orientation, or null, if that point does not exist in the world
    public Point getNextPointFromOrientation(Point point, Orientation orientation) {
        if(point == null)
            return null;

        Point nextPoint;
        switch(orientation){
            //facing north
            case NORTH:
                nextPoint = new Point(point.x, point.y-1);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
            //facing north-west
            case NORTHWEST:
                nextPoint = new Point(point.x - 1, point.y-1);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
            //facing west
            case WEST:
                nextPoint = new Point(point.x - 1, point.y);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
            //facing south-west
            case SOUTHWEST:
                nextPoint = new Point(point.x - 1, point.y + 1);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
            //facing south
            case SOUTH:
                nextPoint = new Point(point.x, point.y + 1);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
            //facing south-east
            case SOUTHEAST:
                nextPoint = new Point(point.x + 1, point.y + 1);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
            //facing east
            case EAST:
                nextPoint = new Point(point.x + 1, point.y);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
            //facing north-east
            case NORTHEAST:
                nextPoint = new Point(point.x + 1, point.y - 1);
                if(getTerrain(nextPoint) != Terrain.NONE)
                    return nextPoint;
                return null;
        }

        return null;
    }

    public int getInfluenceRadius(Entity source, Entity target) {
        return getInfluenceRadius(getEntityLocation(source), getEntityLocation(target));
    }

    public static int getInfluenceRadius(Point source, Point destination) {
        if(source == null || destination == null)
            return 0;

        int dx = Math.abs(destination.x - source.x);
        int dy = Math.abs(destination.y - source.y);
        if(dy == dx) {
            if(dx < 2) {
                return dx;
            } else {
                return dx+1;
            }
        } else {
            return Math.max(dx, dy);
        }
    }

    public void disarmTrap(Entity entity, int trapSuccessShance) {
        Point entityPoint = getEntityLocation(entity);

        // search in a circle around the entity for traps, then try to either reveal them, or disarm them
        if(getTrap(new Point(entityPoint.x + -1, entityPoint.y + -1)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + -1, entityPoint.y + -1));

            trap.disarm(entity, trapSuccessShance);
        }
        if(getTrap(new Point(entityPoint.x + -1, entityPoint.y + 0)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + -1, entityPoint.y + 0));

            trap.disarm(entity, trapSuccessShance);
        }
        if(getTrap(new Point(entityPoint.x + -1, entityPoint.y + 1)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + -1, entityPoint.y + 1));

            trap.disarm(entity, trapSuccessShance);
        }
        if(getTrap(new Point(entityPoint.x + 0, entityPoint.y + -1)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + 0, entityPoint.y + -1));

            trap.disarm(entity, trapSuccessShance);
        }
        if(getTrap(new Point(entityPoint.x + 0, entityPoint.y + 1)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + 0, entityPoint.y + 1));

            trap.disarm(entity, trapSuccessShance);
        }
        if(getTrap(new Point(entityPoint.x + 1, entityPoint.y + -1)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + 1, entityPoint.y + -1));

            trap.disarm(entity, trapSuccessShance);
        }
        if(getTrap(new Point(entityPoint.x + 1, entityPoint.y + 0)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + 1, entityPoint.y + 0));

            trap.disarm(entity, trapSuccessShance);
        }
        if(getTrap(new Point(entityPoint.x + 1, entityPoint.y + 1)) != null) {
            Trap trap = getTrap(new Point(entityPoint.x + 1, entityPoint.y + 1));

            trap.disarm(entity, trapSuccessShance);
        }
    }

    public void accept(Visitor v) {
        v.visitWorld(this);
    }
}
