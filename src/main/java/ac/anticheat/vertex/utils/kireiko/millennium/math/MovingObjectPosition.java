package ac.anticheat.vertex.utils.kireiko.millennium.math;

import ac.anticheat.vertex.utils.kireiko.millennium.vectors.Vec3;

public final class MovingObjectPosition {
    /**
     * What type of ray trace hit was this? 0 = block, 1 = entity
     */
    public MovingObjectType typeOfHit;
    public EnumFacing sideHit;
    /**
     * The vector position of the hit
     */
    public Vec3 hitVec;
    private BlockPos blockPos;


    public MovingObjectPosition(Vec3 hitVecIn, EnumFacing facing, BlockPos blockPosIn) {
        this(MovingObjectType.BLOCK, hitVecIn, facing, blockPosIn);
    }

    public MovingObjectPosition(Vec3 vec3, EnumFacing facing) {
        this(MovingObjectType.BLOCK, vec3, facing, BlockPos.ORIGIN);
    }

    public MovingObjectPosition(MovingObjectType typeOfHitIn, Vec3 hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
        this.typeOfHit = typeOfHitIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Vec3(hitVecIn.xCoord, hitVecIn.yCoord, hitVecIn.zCoord);
    }

//    public MovingObjectPosition(Entity entityHitIn, Vec3 hitVecIn)
//    {
//        this.typeOfHit = MovingObjectType.ENTITY;
//        this.entityHit = entityHitIn;
//        this.hitVec = hitVecIn;
//    }

    public MovingObjectPosition(Vec3 hitVecIn) {
        this.typeOfHit = MovingObjectType.ENTITY;
        this.hitVec = hitVecIn;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public String toString() {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + '}';
    }

    public enum MovingObjectType {
        MISS,
        BLOCK,
        ENTITY
    }
}
