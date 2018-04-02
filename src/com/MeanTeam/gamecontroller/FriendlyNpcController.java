package com.MeanTeam.gamecontroller;

import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.util.Path;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.visitors.Visitor;

import java.util.ArrayList;
import java.util.List;

public class FriendlyNpcController implements Visitable {

    private List<Entity> entities;
    private List<Path> paths;

    public FriendlyNpcController() {
        this.entities = new ArrayList<>();
        this.paths = new ArrayList<>();
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void resetNpcPaths() {
        for(Path path: paths) {
            path.resetCounter();
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        paths.add(new Path());
    }

    public void removeEntity(Entity entity) {
        int entityIndex = -1;
        for(int i = 0; i < entities.size(); ++i) {
            if(entity == entities.get(i)) {
                entityIndex = i;
            }
        }
        if(entityIndex != -1) {
            entities.remove(entityIndex);
            paths.remove(entityIndex);
        }
    }

    public void addOrientationToPath(Entity entity, Orientation orientation) {
        int entityIndex = -1;
        for(int i = 0; i < entities.size(); ++i){
            if(entities.get(i) == entity){
                entityIndex = i;
                break;
            }
        }
        if(entityIndex != -1){
            paths.get(entityIndex).addOrientation(orientation);
        }
    }

    public void nextAction(World world) {
        for(int i = 0; i < paths.size(); ++i) {
            if(!entities.get(i).isAllowedToMove()) {
                continue;
            }
            this.nextMove(entities.get(i), paths.get(i));
        }
    }

    private void nextMove(Entity entity, Path path) {
        if (!path.isEmpty()) {
            Orientation orientation = path.getOrientation();
            entity.setOrientation(orientation);
            entity.move();
            path.incrementCounter();
        }
    }

    public boolean contains(Entity entity){
        return entities.contains(entity);
    }

    public Path getPath(Entity entity) {
        int entityIndex = -1;
        for(int i = 0; i < entities.size(); ++i) {
            if(entity == entities.get(i)) {
                entityIndex = i;
                break;
            }
        }
        if(entityIndex != -1) {
            return paths.get(entityIndex);
        }
        return null;
    }

    public List<Entity> getDamagedNpcs() {
        List<Entity> list = new ArrayList<>();

        for(Entity entity: entities) {
            if(entity.getCurrentHealth() < entity.getMaxHealth()) {
                list.add(entity);
            }
        }

        return list;
    }

    public void setPath(Entity entity, Path path) {
        int entityIndex = -1;
        for(int i = 0; i < entities.size(); ++i) {
            if(entity == entities.get(i)) {
                entityIndex = i;
                break;
            }
        }
        if(entityIndex != -1) {
            paths.set(entityIndex, path);
        }
    }

    public void accept(Visitor v) {
        v.visitFriendlyNpcController(this);
    }
}
