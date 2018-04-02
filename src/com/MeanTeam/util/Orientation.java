package com.MeanTeam.util;

public enum Orientation {
    NULL,
    NORTH,
    NORTHWEST,
    WEST,
    SOUTHWEST,
    SOUTH,
    SOUTHEAST,
    EAST,
    NORTHEAST;

    private Orientation left90;
    static {
        NULL.left90 = NULL;
        NORTH.left90 = WEST;
        NORTHWEST.left90 = SOUTHWEST;
        WEST.left90 = SOUTH;
        SOUTHWEST.left90 = SOUTHEAST;
        SOUTH.left90 = EAST;
        SOUTHEAST.left90 = NORTHEAST;
        EAST.left90 = NORTH;
        NORTHEAST.left90 = NORTHWEST;
    }

    private Orientation right90;
    static {
        NULL.right90 = NULL;
        NORTH.right90 = EAST;
        NORTHWEST.right90 = NORTHEAST;
        WEST.right90 = NORTH;
        SOUTHWEST.right90 = NORTHWEST;
        SOUTH.right90 = WEST;
        SOUTHEAST.right90 = SOUTHWEST;
        EAST.right90 = SOUTH;
        NORTHEAST.right90 = SOUTHEAST;
    }

    private Orientation left45;
    static {
        NULL.left45 = NULL;
        NORTH.left45 = NORTHWEST;
        NORTHWEST.left45 = WEST;
        WEST.left45 = SOUTHWEST;
        SOUTHWEST.left45 = SOUTH;
        SOUTH.left45 = SOUTHEAST;
        SOUTHEAST.left45 = EAST;
        EAST.left45 = NORTHEAST;
        NORTHEAST.left45 = NORTH;
    }

    private Orientation right45;
    static {
        NULL.right45 = NULL;
        NORTH.right45 = NORTHEAST;
        NORTHWEST.right45 = NORTH;
        WEST.right45 = NORTHWEST;
        SOUTHWEST.right45 = WEST;
        SOUTH.right45 = SOUTHWEST;
        SOUTHEAST.right45 = SOUTH;
        EAST.right45 = SOUTHEAST;
        NORTHEAST.right45 = EAST;
    }

    private int dx;
    static {
        NULL.dx = 0;
        NORTH.dx = 0;
        NORTHWEST.dx = -1;
        WEST.dx = -1;
        SOUTHWEST.dx = -1;
        SOUTH.dx = 0;
        SOUTHEAST.dx = 1;
        EAST.dx = 1;
        NORTHEAST.dx = 1;

    }

    private int dy;
    static {
        NULL.dy = 0;
        NORTH.dy = -1;
        NORTHWEST.dy = -1;
        WEST.dy = 0;
        SOUTHWEST.dy = 1;
        SOUTH.dy = 1;
        SOUTHEAST.dy = 1;
        EAST.dy = 0;
        NORTHEAST.dy = -1;
    }

    public Orientation getLeft90() {
        return left90;
    }

    public Orientation getRight90() {
        return right90;
    }

    public Orientation getLeft45() {
        return left45;
    }

    public Orientation getRight45() {
        return right45;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}
