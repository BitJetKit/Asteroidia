package com.semars.mygdx.game.elements;

/**
 * Created by semar on 7/17/15.
 */
public class ActorData {

    CollisionGroup collisionGroup;
    int actorIndex;

    public ActorData(int actorIndex, CollisionGroup collisiongroup){
        setInfo(actorIndex, collisiongroup);
    }

    public void setInfo(int actorIndex, CollisionGroup collisiongroup){
        this.actorIndex = actorIndex;
        this.collisionGroup = collisiongroup;
    }

    public int getActorIndex() {
        return this.actorIndex;
    }

    public CollisionGroup getCollisionGroup() {
        return this.collisionGroup;
    }
}
