package com.semars.mygdx.game.elements;

/**
 * Created by semar on 7/18/15.
 */
public enum CollisionGroup {
    PLAYER((short) 0x0002, (short) (0x0004 | 0x0010)),
    ENEMY((short) 0x0004, (short) (0x0002 | 0x0008 | 0x0012)),
    PLAYER_SHOT((short) 0x0008, (short) (0x0004)),
    POWERUP((short) 0x0010, (short) (0x0002)),
    SHIELD((short) 0x0012, (short) (0x0004));

    private short categoryBits;
    private short maskBits;

    CollisionGroup(short categoryBits, short maskBits) {
        this.categoryBits = categoryBits;
        this.maskBits = maskBits;
    }

    public void setCategoryBits(short categoryBits) {
        this.categoryBits = categoryBits;
    }

    public short getCategoryBits() {
        return categoryBits;
    }

    public void setMaskBits(short maskBits) {
        this.maskBits = maskBits;
    }

    public short getMaskBits() {
        return maskBits;
    }
}


