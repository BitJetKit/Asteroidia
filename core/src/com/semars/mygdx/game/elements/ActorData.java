package com.semars.mygdx.game.elements;

/**
 * Created by semar on 7/17/15.
 */
public class ActorData {

    int collisionGroup;
    int actorIndex;

    public ActorData(int actorIndex, int collisiongroup){
        setInfo(actorIndex, collisiongroup);
    }

    public void setInfo(int actorIndex, int collisiongroup){
        this.actorIndex = actorIndex;
        this.collisionGroup = collisiongroup;
    }

    public int getActorIndex() {
        return this.actorIndex;
    }

    public int getCollisionGroup() {
        return this.collisionGroup;
    }
}
