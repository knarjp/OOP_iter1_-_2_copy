package com.MeanTeam.util;

import com.MeanTeam.visitors.Visitable;
import com.MeanTeam.visitors.Visitor;

import java.util.ArrayList;

public class Path implements Visitable {
    private ArrayList<Orientation> directions;
    private int counter;

    public Path() {
        directions = new ArrayList<>();
        counter = 0;
    }

    public Path(Path other) {
        directions = other.directions;
        counter = 0;
    }

    public void addOrientation(Orientation orientation) {
        directions.add(orientation);
    }

    public void incrementCounter() {
        ++counter;
        if (counter >= directions.size()) {
            counter = 0;
        }
    }

    public void resetCounter() {
        counter = 0;
    }

    public Orientation getOrientation() {
        if (directions.isEmpty())
            return null;
        else
            return directions.get(counter);
    }

    //For testing purposes only
    public Orientation getOrientationAtIndex(int index) {
        return directions.get(index);
    }

    public boolean isEmpty() {
        return directions.isEmpty();
    }

    public ArrayList<Orientation> getDirections() {
        return directions;
    }

    public int getCounter() {
        return counter;
    }

    public void accept(Visitor v) {
        v.visitPath(this);
    }
}