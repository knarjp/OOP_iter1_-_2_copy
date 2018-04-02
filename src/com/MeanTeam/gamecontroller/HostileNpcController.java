package com.MeanTeam.gamecontroller;

import com.MeanTeam.gamemodel.World;
import com.MeanTeam.gamemodel.tile.entities.Entity;
import com.MeanTeam.util.Orientation;
import com.MeanTeam.util.Path;
import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.visitors.Visitor;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class HostileNpcController implements Visitable {

    private List<Entity> entities;
    private List<Path> paths;
    private Entity player;
    private List<Boolean> confused;

    private static Orientation[][] dxdyToOrientation = {
            {Orientation.NORTHWEST, Orientation.NORTH, Orientation.NORTHEAST},
            {Orientation.WEST, Orientation.NULL, Orientation.EAST},
            {Orientation.SOUTHWEST, Orientation.SOUTH, Orientation.SOUTHEAST}
    };

    public HostileNpcController(List<Entity> entities, Entity player) {
        this.entities = entities;
        this.paths = new ArrayList<>();
        this.confused = new ArrayList<>();
        this.player = player;
    }

    public HostileNpcController(Entity player) {
        this.entities = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.confused = new ArrayList<>();
        this.player = player;
    }

    public void resetNpcPaths() {
        for(Path path: paths) {
            path.resetCounter();
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public Entity getPlayer() {
        return player;
    }

    public List<Boolean> getConfused() {
        return confused;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        paths.add(new Path());
        confused.add(false);
    }

    public void addEntity(Entity entity, Path path) {
        entities.add(entity);
        paths.add(path);
        confused.add(false);
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
            confused.remove(entityIndex);
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

    //For testing purposes only
    public Orientation getPathOrientation(Entity entity, int orientationIndex) {
        int entityIndex = -1;
        for(int i = 0; i < entities.size(); ++i){
            if(entities.get(i) == entity){
                entityIndex = i;
                break;
            }

        }
        return paths.get(entityIndex).getOrientationAtIndex(orientationIndex);
    }

    public void nextAction(World world) {
        for (int i = 0; i < paths.size(); ++i) {
            if (!entities.get(i).isAllowedToMove()) {
                continue;
            }
            if (isConfused(i) || !isAwareOfPlayer(world, entities.get(i))) {
                this.nextMove(entities.get(i), paths.get(i));
            }
            else {
                if (isPlayerInRange(world, entities.get(i))) {
                    facePlayer(world, entities.get(i));
                    attackPlayer(entities.get(i));
                }
                else {
                    moveToPlayer(world, entities.get(i));
                }
            }
        }

    }

    private boolean isAwareOfPlayer(World world, Entity entity) {
        return !player.isNoiseLevelZero() && player.getNoiseLevel() >= world.getInfluenceRadius(entity, player);
    }

    public boolean isPlayerDetected(World world) {
        for(Entity entity: entities) {
            if(isAwareOfPlayer(world, entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean isConfused(int index) {
        return confused.get(index);
    }

    public void toggleConfused(Entity entity) {
        int entityIndex = -1;
        for(int i = 0; i < entities.size(); ++i) {
            if(entity == entities.get(i)) {
                entityIndex = i;
                break;
            }
        }
        if(entityIndex != -1) {
            confused.set(entityIndex, !confused.get(entityIndex));
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

    private void moveToPlayer(World world, Entity entity) {
        Orientation orientation = chasePlayer(world, entity);
        if (orientation != Orientation.NULL) {
            entity.setOrientation(orientation);
            entity.move();
        }
    }

    private void attackPlayer(Entity entity) {
        entity.useWeaponSkill();
    }

    private Orientation chasePlayer(World world, Entity entity) {
        Point npcPoint = world.getEntityLocation(entity);
        Point playerPoint = world.getEntityLocation(player);
        return getShortestPathNextOrientation(world, npcPoint, playerPoint);
    }

    private boolean isPlayerInRange(World world, Entity entity) {
        int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1};
        for(int i = 0; i < dx.length; ++i) {
            Point neighbor = new Point(world.getEntityLocation(entity).x + dx[i], world.getEntityLocation(entity).y + dy[i]);
            if(world.getEntity(neighbor) == player) {
                return true;
            }
        }
        return false;
    }

    private void facePlayer(World world, Entity entity) {
        int playerdx = world.getEntityLocation(player).x - world.getEntityLocation(entity).x;
        int playerdy = world.getEntityLocation(player).y - world.getEntityLocation(entity).y;
        entity.setOrientation(getOrientation(playerdx, playerdy));
    }

    private Orientation getShortestPathNextOrientation(World world, Point npcPoint, Point playerPoint){


        PriorityQueue<Vertex> unusedPoints = new PriorityQueue<>();
        HashSet<Point> solvedPoints = new HashSet<>();
        unusedPoints.add(new Vertex(npcPoint, 0, npcPoint));

        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE,
                minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        for(Point point: world.getAllTerrainPoints()){
            if(point.x > maxX)
                maxX = point.x;
            if(point.y > maxY)
                maxY = point.y;
            if(point.x < minX)
                minX = point.x;
            if(point.y < minY)
                minY = point.y;
        }
        Point[][] predecessors = new Point[maxY-minY+1][maxX-minX+1];

        Point endPoint = null;
        while(endPoint == null && unusedPoints.size() != 0) {
            Vertex currVertex = unusedPoints.poll();
            if(solvedPoints.contains(currVertex.getPoint()))
                continue;
            solvedPoints.add(currVertex.getPoint());
            if(currVertex.getPoint() == null){
                return Orientation.NULL;
            }
            predecessors[currVertex.getPoint().y - minY][currVertex.getPoint().x - minX] = currVertex.getPredecessor();

            int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1};
            int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1};
            for(int i = 0; i < dx.length; ++i){
                Point neighbor = new Point(currVertex.getPoint().x + dx[i], currVertex.getPoint().y + dy[i]);

                if(neighbor.x == playerPoint.x && neighbor.y == playerPoint.y){
                    endPoint = currVertex.getPoint();
                    break;
                }
                if(solvedPoints.contains(neighbor) || !world.isValidMove(neighbor))
                    continue;

                int newDistance = currVertex.getDistance() + 1;
                int heuristic = Math.abs(currVertex.getPoint().x - playerPoint.x)
                        + Math.abs(currVertex.getPoint().y - playerPoint.y)
                        + dx[i] + dy[i];
                newDistance += heuristic;

                unusedPoints.add(new Vertex(neighbor, newDistance, currVertex.getPoint()));
            }
        }

        /*for(int i = 0; i < maxX-minX+1; ++i) {
            for(int j = 0; j < maxY-minY+1; ++j) {
                if(predecessors[i][j] == null)
                    System.out.print("[   ] ");
                else
                    System.out.print("[" + predecessors[i][j].y + " " + predecessors[i][j].x + "] ");
            }
            System.out.println();
        }
        System.out.println();*/

        Point pointToMoveTo = endPoint;
        if(pointToMoveTo == null) {
            return Orientation.NULL;
        }
        Point predecessor = predecessors[pointToMoveTo.y-minY][pointToMoveTo.x-minX];
        while(predecessor.x != npcPoint.x || predecessor.y != npcPoint.y){
            pointToMoveTo = predecessor;
            predecessor = predecessors[pointToMoveTo.y-minY][pointToMoveTo.x-minX];
        }
        return getOrientation(pointToMoveTo.x-npcPoint.x, pointToMoveTo.y-npcPoint.y);
    }

    private Orientation getOrientation(int dx, int dy) {
        return dxdyToOrientation[dy+1][dx+1];
    }

    private class Vertex implements Comparable<Vertex> {
                private Point point;
                private int distance;
                private Point predecessor;

                Vertex(Point point, int distance, Point predecessor){
                    this.point = point;
                    this.distance = distance;
                    this.predecessor = predecessor;
                }

                Point getPoint() {
                    return point;
        }

        int getDistance() {
            return distance;
        }

        Point getPredecessor() {
            return predecessor;
        }

        void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public int compareTo(Vertex v) {
            return distance - v.distance;
        }
    }

    public void accept(Visitor v) {
        v.visitHostileNpcController(this);
    }
}
