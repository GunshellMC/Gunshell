package com.jazzkuh.gunshell.compatibility.versions;

import com.google.common.base.Preconditions;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import net.minecraft.server.v1_12_R1.MovingObjectPosition;
import net.minecraft.server.v1_12_R1.Vec3D;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class v1_12_2 implements CompatibilityLayer {
    @Override
    public Entity getRayTrace(Player player, int range) {
        RayTraceResult rayTraceResult = rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), range);
        if (rayTraceResult == null) return null;
        return rayTraceResult.getHitEntity();
    }

    private RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance) {
        return this.rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, false);
    }

    private RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
        Validate.notNull(start, "Start location is null!");
        Validate.isTrue(this.equals(start.getWorld()), "Start location is from different world!");
        start.checkFinite();

        Validate.notNull(direction, "Direction is null!");
        direction.checkFinite();

        Validate.isTrue(direction.lengthSquared() > 0, "Direction's magnitude is 0!");
        Validate.notNull(fluidCollisionMode, "Fluid collision mode is null!");

        if (maxDistance < 0.0D) {
            return null;
        }

        Vector dir = direction.clone().normalize().multiply(maxDistance);
        Vec3D startPos = new Vec3D(start.getX(), start.getY(), start.getZ());
        Vec3D endPos = new Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
        CraftWorld craftWorld = (CraftWorld) start.getWorld();
        MovingObjectPosition nmsHitResult = craftWorld.getHandle().rayTrace(startPos, endPos, ignorePassableBlocks, false, false);

        return new CraftRayTraceResult().fromNMS(nmsHitResult);
    }

    private static class RayTraceResult {

        private final Vector hitPosition;

        private final Block hitBlock;
        private final BlockFace hitBlockFace;
        private final Entity hitEntity;

        private RayTraceResult(@NotNull Vector hitPosition, @Nullable Block hitBlock, @Nullable BlockFace hitBlockFace, @Nullable Entity hitEntity) {
            Preconditions.checkArgument(hitPosition != null, "Hit position is null!");
            this.hitPosition = hitPosition.clone();
            this.hitBlock = hitBlock;
            this.hitBlockFace = hitBlockFace;
            this.hitEntity = hitEntity;
        }

        /**
         * Creates a RayTraceResult.
         *
         * @param hitPosition the hit position
         */
        public RayTraceResult(@NotNull Vector hitPosition) {
            this(hitPosition, null, null, null);
        }

        /**
         * Creates a RayTraceResult.
         *
         * @param hitPosition  the hit position
         * @param hitBlockFace the hit block face
         */
        public RayTraceResult(@NotNull Vector hitPosition, @Nullable BlockFace hitBlockFace) {
            this(hitPosition, null, hitBlockFace, null);
        }

        /**
         * Creates a RayTraceResult.
         *
         * @param hitPosition  the hit position
         * @param hitBlock     the hit block
         * @param hitBlockFace the hit block face
         */
        public RayTraceResult(@NotNull Vector hitPosition, @Nullable Block hitBlock, @Nullable BlockFace hitBlockFace) {
            this(hitPosition, hitBlock, hitBlockFace, null);
        }

        /**
         * Creates a RayTraceResult.
         *
         * @param hitPosition the hit position
         * @param hitEntity   the hit entity
         */
        public RayTraceResult(@NotNull Vector hitPosition, @Nullable Entity hitEntity) {
            this(hitPosition, null, null, hitEntity);
        }

        /**
         * Creates a RayTraceResult.
         *
         * @param hitPosition  the hit position
         * @param hitEntity    the hit entity
         * @param hitBlockFace the hit block face
         */
        public RayTraceResult(@NotNull Vector hitPosition, @Nullable Entity hitEntity, @Nullable BlockFace hitBlockFace) {
            this(hitPosition, null, hitBlockFace, hitEntity);
        }

        /**
         * Gets the exact position of the hit.
         *
         * @return a copy of the exact hit position
         */
        @NotNull
        public Vector getHitPosition() {
            return hitPosition.clone();
        }

        /**
         * Gets the hit block.
         *
         * @return the hit block, or <code>null</code> if not available
         */
        @Nullable
        public Block getHitBlock() {
            return hitBlock;
        }

        /**
         * Gets the hit block face.
         *
         * @return the hit block face, or <code>null</code> if not available
         */
        @Nullable
        public BlockFace getHitBlockFace() {
            return hitBlockFace;
        }

        /**
         * Gets the hit entity.
         *
         * @return the hit entity, or <code>null</code> if not available
         */
        @Nullable
        public Entity getHitEntity() {
            return hitEntity;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + hitPosition.hashCode();
            result = prime * result + ((hitBlock == null) ? 0 : hitBlock.hashCode());
            result = prime * result + ((hitBlockFace == null) ? 0 : hitBlockFace.hashCode());
            result = prime * result + ((hitEntity == null) ? 0 : hitEntity.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof org.bukkit.util.RayTraceResult)) return false;
            org.bukkit.util.RayTraceResult other = (org.bukkit.util.RayTraceResult) obj;
            if (!hitPosition.equals(other.getHitPosition())) return false;
            if (!Objects.equals(hitBlock, other.getHitBlock())) return false;
            if (!Objects.equals(hitBlockFace, other.getHitBlockFace())) return false;
            if (!Objects.equals(hitEntity, other.getHitEntity())) return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("RayTraceResult [hitPosition=");
            builder.append(hitPosition);
            builder.append(", hitBlock=");
            builder.append(hitBlock);
            builder.append(", hitBlockFace=");
            builder.append(hitBlockFace);
            builder.append(", hitEntity=");
            builder.append(hitEntity);
            builder.append("]");
            return builder.toString();
        }
    }

    private enum FluidCollisionMode {
        /**
         * Ignore fluids.
         */
        NEVER,
        /**
         * Only collide with source fluid blocks.
         */
        SOURCE_ONLY,
        /**
         * Collide with all fluids.
         */
        ALWAYS;
    }

    private static final class CraftFluidCollisionMode {

        private CraftFluidCollisionMode() {}

        public FluidCollisionOption toNMS(FluidCollisionMode fluidCollisionMode) {
            if (fluidCollisionMode == null) return null;

            switch (fluidCollisionMode) {
                case ALWAYS:
                    return FluidCollisionOption.ANY;
                case SOURCE_ONLY:
                    return FluidCollisionOption.SOURCE_ONLY;
                case NEVER:
                    return FluidCollisionOption.NONE;
                default:
                    return null;
            }
        }
    }

    private enum FluidCollisionOption {
        /**
         * Ignore fluids.
         */
        NONE,
        /**
         * Only collide with source fluid blocks.
         */
        SOURCE_ONLY,
        /**
         * Collide with all fluids.
         */
        ANY;
    }

    public static final class CraftRayTraceResult {

        private CraftRayTraceResult() {}

        public RayTraceResult fromNMS(MovingObjectPosition nmsHitResult) {
            if (nmsHitResult == null || nmsHitResult.type == MovingObjectPosition.EnumMovingObjectType.MISS) return null;

            Vec3D nmsHitPos = nmsHitResult.pos;
            Vector hitPosition = new Vector(nmsHitPos.x, nmsHitPos.y, nmsHitPos.z);

            if (nmsHitResult.type == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                Entity hitEntity = nmsHitResult.entity.getBukkitEntity();
                return new RayTraceResult(hitPosition, hitEntity, null);
            }

            return new RayTraceResult(hitPosition, null, null, null);
        }
    }
}
