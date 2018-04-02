package com.MeanTeam.gamemodel.tile.effectcommand;

import com.MeanTeam.gamemodel.tile.entities.Entity;

public abstract class ToggleableEventCommand implements EventCommand {

    private boolean hasFired;

    public void trigger(Entity entity) {
        if(hasFired) {
            unExecute(entity);
            hasFired = false;
        } else {
            execute(entity);
            hasFired = true;
        }
    }

    protected void setHasNotFired() {
        this.hasFired = false;
    }

    public boolean getHasFired() {
        return hasFired;
    }

    public void setHasFired(boolean value) {
        this.hasFired = value;
    }

    protected abstract void execute(Entity entity);

    protected abstract void unExecute(Entity entity);
}
