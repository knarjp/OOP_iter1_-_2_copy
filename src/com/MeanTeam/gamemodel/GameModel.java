package com.MeanTeam.gamemodel;

import com.MeanTeam.gamecontroller.FriendlyNpcController;
import com.MeanTeam.gamecontroller.HostileNpcController;
import com.MeanTeam.gamemodel.notifiers.GameViewNotifier;
import com.MeanTeam.gamemodel.notifiers.WorldNotifier;
import com.MeanTeam.gamemodel.tile.Decal;
import com.MeanTeam.gamemodel.tile.Obstacle;
import com.MeanTeam.gamemodel.tile.Terrain;
import com.MeanTeam.gamemodel.tile.items.*;
import com.MeanTeam.gamemodel.tile.traps.Trap;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.util.TimedEvent;
import com.MeanTeam.util.Path;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.visitors.Visitor;
import java.util.Queue;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class GameModel implements Visitable
{
    private World activeWorld;
    private List<World> worlds;
    private List<FriendlyNpcController> friendlyNpcControllers;
    private List<HostileNpcController> hostileNpcControllers;
    private List<WorldNotifier> worldNotifiers;
    private List<TimedEvent> timedEventList;
    private Entity player;

    private int curWorldIndex = 0;

    private int turnCount = 0;

    public GameModel(List<World> worlds, List<WorldNotifier> worldNotifiers, List<FriendlyNpcController> friendlyNpcControllers,
                     List<HostileNpcController> hostileNpcControllers, World activeWorld, Entity player, int curWorldIndex)
    {
        this.player = player;
        this.activeWorld = activeWorld;
        this.friendlyNpcControllers = friendlyNpcControllers;
        this.hostileNpcControllers = hostileNpcControllers;
        this.worlds = worlds;
        this.worldNotifiers = worldNotifiers;
        this.curWorldIndex = curWorldIndex;
        this.timedEventList = new LinkedList<>();
    }

    public GameModel(List<World> worlds, List<WorldNotifier> worldNotifiers, List<FriendlyNpcController> friendlyNpcControllers,
                     List<HostileNpcController> hostileNpcControllers, World activeWorld, Entity player)
    {
        this.player = player;
        this.activeWorld = activeWorld;
        this.friendlyNpcControllers = friendlyNpcControllers;
        this.hostileNpcControllers = hostileNpcControllers;
        this.worlds = worlds;
        this.worldNotifiers = worldNotifiers;
        this.curWorldIndex = 0;
        this.timedEventList = new LinkedList<>();
    }


    public List<FriendlyNpcController> getFriendlyNpcControllers() {
        return friendlyNpcControllers;
    }

    public List<HostileNpcController> getHostileNpcControllers() {
        return hostileNpcControllers;
    }

    public List<TimedEvent> getTimedEventList() {
        return timedEventList;
    }

    public void changeWorld(int worldIndex, Point playerSpawnPt)
    {
        Point playerOrigin = activeWorld.getEntityLocation(player);
        activeWorld.removeEntity(playerOrigin);

        if(worldIndex != curWorldIndex) {
            setActiveWorld(worldIndex);

            activeWorld.resetNpcs();
            resetNpcPaths();

            WorldNotifier newNotifier = worldNotifiers.get(curWorldIndex);
            player.changeWorldNotifier(newNotifier);
        }

        activeWorld.addPlayer(playerSpawnPt, player);
    }

    private void resetNpcPaths() {

        if(friendlyNpcControllers != null && curWorldIndex < friendlyNpcControllers.size()) {
            friendlyNpcControllers.get(curWorldIndex).resetNpcPaths();
        }
        if(hostileNpcControllers != null && curWorldIndex < hostileNpcControllers.size()) {
            hostileNpcControllers.get(curWorldIndex).resetNpcPaths();
        }
    }

    public void initialize(int worldIndex, Entity player)
    {
        setActiveWorld(worldIndex);
        this.player = player;
    }

    public void setActiveWorld(int worldIndex)
    {
        activeWorld = worlds.get(curWorldIndex = worldIndex);
    }

    public Point getOffsetFromPlayer(Point offset)
    {
        Point playerPt = activeWorld.getEntityLocation(player);
        if(playerPt == null) return null;

        return new Point(playerPt.x + offset.x, playerPt.y + offset.y);
    }

    public Terrain getTerrain(Point point) { return activeWorld.getTerrain(point); }
    public Entity getEntity(Point point) { return activeWorld.getEntity(point); }
    public Item getItem(Point point) { return activeWorld.getItem(point); }
    public Obstacle getObstacle(Point point) { return activeWorld.getObstacle(point); }
    public Decal getDecal(Point point) { return activeWorld.getDecal(point); }
    public Trap getTrap(Point point) { return activeWorld.getTrap(point); }

    public int getCurWorldIndex() { return curWorldIndex; }

    public int getTurnCount() { return turnCount; }

    public void advance()
    {
        removeDeadEntities();
        if(activeWorld.getEntityLocation(player) == null){
            //TODO: add game over logic
            GameViewNotifier.notifyPlayerDeath();
            return;
        }
        //long startTime = System.nanoTime();
        ++turnCount;
        processTimedEvents();
        processNpcs();
        processSkills();
        processMoves();
        processInteractions();
        updateEntityStats();
        //System.out.println("Advance took: " + (double)((System.nanoTime() - startTime))/1000000 + " seconds");
    }

    public void processTimedEvents() {
        Queue<TimedEvent> toRemove = new LinkedList<>();
        for(int i = 0; i < timedEventList.size(); ++i) {
            timedEventList.get(i).decrementTurnCountLeft();

            if(timedEventList.get(i).stopIfNeeded()){
                toRemove.add(timedEventList.get(i));
            }
        }

        while(!toRemove.isEmpty()) {
            TimedEvent timedEvent = toRemove.poll();
            timedEventList.remove(timedEvent);
        }
    }

    private void removeDeadEntities(){
        List<Entity> removed = activeWorld.removeDeadEntities();
        for(Entity entity: removed) {
            friendlyNpcControllers.get(getCurWorldIndex()).removeEntity(entity);
            hostileNpcControllers.get(getCurWorldIndex()).removeEntity(entity);
        }
    }

    private void processNpcs() {
        friendlyNpcControllers.get(curWorldIndex).nextAction(activeWorld);
        hostileNpcControllers.get(curWorldIndex).nextAction(activeWorld);
    }

    private void processSkills() {activeWorld.processSkills(); }

    private void processMoves() {
        activeWorld.attemptMoves();
    }

    private void processInteractions() {
        activeWorld.doInteractions(player);
    }

    private void updateEntityStats() { activeWorld.updateEntityStats(); }

    public Entity getPlayer()
    {
        return player;
    }

    public List<World> getWorlds() { return worlds; }

    public List<WorldNotifier> getWorldNotifiers() { return worldNotifiers; }

    public void addWeaponToPlayerWeaponSlot(WeaponItem weapon) {
        player.equipWeapon(weapon);
    }

    public void addArmorToPlayerWeaponSlot(ArmorItem armor) {
        player.equipArmor(armor);
    }

    public void addRingToPlayerWeaponSlot(RingItem ring) {
        player.equipRing(ring);
    }

    public void consumeItem(ConsumableItem consumableItem) {
        player.useConsumableItem(consumableItem);
    }

    public void transferItemToPlayer(TakeableItem item) {
        player.addItemToBackpack(item);
    }

    public void addFriendlyNpcController(){
        friendlyNpcControllers.add(new FriendlyNpcController());
    }

    public void addFriendlyNpcPath(Entity entity, World world, Path path) {
        int NpcControllerIndex = -1;
        for(int i = 0; i < worlds.size(); ++i) {
            if(world == worlds.get(i)) {
                NpcControllerIndex = i;
            }
        }
        if(NpcControllerIndex != -1) {
            friendlyNpcControllers.get(NpcControllerIndex).setPath(entity, path);
        }
    }

    public void addHostileNpcController(Entity player){
        hostileNpcControllers.add(new HostileNpcController(player));
    }

    public void addHostileNpcPath(Entity entity, World world, Path path) {
        int NpcControllerIndex = -1;
        for(int i = 0; i < worlds.size(); ++i) {
            if(world == worlds.get(i)) {
                NpcControllerIndex = i;
            }
        }
        if(NpcControllerIndex != -1) {
            hostileNpcControllers.get(NpcControllerIndex).setPath(entity, path);
        }
    }

    public void addFriendlyNpc(World world, Entity entity) {
        int worldIndex = -1;
        for(int i = 0; i < worlds.size(); ++i) {
            if(worlds.get(i) == world) {
                worldIndex = i;
                break;
            }
        }
        if(worldIndex != -1) {
            friendlyNpcControllers.get(worldIndex).addEntity(entity);
        }
    }

    public void addHostileNpc(World world, Entity entity) {
        int worldIndex = -1;
        for(int i = 0; i < worlds.size(); ++i) {
            if(worlds.get(i) == world) {
                worldIndex = i;
                break;
            }
        }
        if(worldIndex != -1){
            hostileNpcControllers.get(worldIndex).addEntity(entity);
        }
    }

    public void makeNpcHostile(Entity entity) {
        int worldIndex = -1;
        for(int i = 0; i < worlds.size(); ++i) {
            if(friendlyNpcControllers.get(i).contains(entity)) {
                worldIndex = i;
                break;
            }
        }
        if(worldIndex != -1) {
            Path oldPath = friendlyNpcControllers.get(worldIndex).getPath(entity);

            friendlyNpcControllers.get(worldIndex).removeEntity(entity);
            hostileNpcControllers.get(worldIndex).addEntity(entity, oldPath);
        }
    }

    public void makeDamagedFriendlyNpcsHostile() {
        List<Entity> list = friendlyNpcControllers.get(curWorldIndex).getDamagedNpcs();

        for(Entity entity: list) {
            makeNpcHostile(entity);
        }
    }

    public void addTimedEvent(TimedEvent timedEvent) {
        timedEvent.start();

        timedEventList.add(timedEvent);
    }

    public void tellActiveWorldToDisarmTrap(Entity entity, int trapSuccessChance) {
        worldNotifiers.get(curWorldIndex).notifyTrapDisarm(entity, trapSuccessChance);
    }

    public void makeNpcConfused(Entity entity, Path path) {
        boolean friendly = true;
        int worldIndex = -1;
        for(int i = 0; i < worlds.size(); ++i) {
            if(friendlyNpcControllers.get(i).contains(entity)) {
                worldIndex = i;
                break;
            }
            if(hostileNpcControllers.get(i).contains(entity)) {
                worldIndex = i;
                friendly = false;
                break;
            }
        }
        if(worldIndex != -1) {
            if(friendly) {
                friendlyNpcControllers.get(worldIndex).setPath(entity, path);
            } else {
                hostileNpcControllers.get(worldIndex).setPath(entity, path);
                hostileNpcControllers.get(worldIndex).toggleConfused(entity);
            }

        }
    }

    public Path getNpcPath(Entity entity) {
        boolean friendly = true;
        int worldIndex = -1;
        for(int i = 0; i < worlds.size(); ++i) {
            if(friendlyNpcControllers.get(i).contains(entity)) {
                worldIndex = i;
                break;
            }
            if(hostileNpcControllers.get(i).contains(entity)) {
                worldIndex = i;
                friendly = false;
                break;
            }
        }
        if(worldIndex != -1) {
            if(friendly) {
                return friendlyNpcControllers.get(worldIndex).getPath(entity);
            } else {
                return hostileNpcControllers.get(worldIndex).getPath(entity);
            }

        }
        return null;
    }

    public boolean isNpcFriendly(Entity entity) {
        return friendlyNpcControllers.get(curWorldIndex).contains(entity);
    }

    public void accept(Visitor v) { v.visitGameModel(this); }
    public boolean isPlayerDetected() {
        return hostileNpcControllers.get(curWorldIndex).isPlayerDetected(activeWorld);
    }

}
