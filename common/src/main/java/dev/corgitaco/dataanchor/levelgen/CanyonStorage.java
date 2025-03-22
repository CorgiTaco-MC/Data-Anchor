package dev.corgitaco.dataanchor.levelgen;

import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.datastructure.Target;
import dev.corgitaco.dataanchor.datastructure.impl.QuadTreeNearestPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

import java.util.*;
import java.util.function.Predicate;

public class CanyonStorage {

    private final QuadTreeNearestPoint<Vec3i> loadedChunksStorage = new QuadTreeNearestPoint<>();
    private final QuadTreeNearestPoint<Vec3i> canyonAnchorStorage = new QuadTreeNearestPoint<>();
    private final QuadTreeNearestPoint<Vec3i> canyonPointStorage = new QuadTreeNearestPoint<>();

    public CanyonStorage() {

    }


    public void worldGenChunkLoad(ProtoChunk protoChunk) {
        BlockPos worldPosition = protoChunk.getPos().getWorldPosition();
        int canyonPosX = toCanyonPos(worldPosition.getX());
        int canyonPosZ = toCanyonPos(worldPosition.getZ());
        this.loadedChunksStorage.setPoint(protoChunk.getPos().getWorldPosition());

        int radius = 2;

        loadAnchors(radius, canyonPosX, canyonPosZ);
        fillPoints(canyonPosX, canyonPosZ);
    }

    private void fillPoints(int canyonPosX, int canyonPosZ) {
        int minX = fromCanyonPos(canyonPosX);
        int minZ = fromCanyonPos(canyonPosZ);

        int maxX = fromCanyonPos(canyonPosX + 1) - 1;
        int maxZ = fromCanyonPos(canyonPosZ + 1) - 1;

        RandomSource randomSource = RandomSource.create(ChunkPos.asLong(canyonPosX, canyonPosZ));
        Collection<Vec3i> anchorsInBox = canyonAnchorStorage.getPointsInBox(new Vec3i(minX, 0, minZ), new Vec3i(maxX, 0, maxZ));

        Collection<Vec3i> pointsInBox = canyonPointStorage.getPointsInBox(new Vec3i(minX, 0, minZ), new Vec3i(maxX, 0, maxZ));
        if (pointsInBox.isEmpty() && !anchorsInBox.isEmpty()) {
            for (Vec3i firstPoint : anchorsInBox) {
                for (Target<Vec3i, Vec3i> nearbyPointData : canyonAnchorStorage.getNearbyTargets(firstPoint, 10, Point::distSqr)) {
                    Vec3i moving = firstPoint;

                    Vec3i endPoint = nearbyPointData.point();

                    while (!moving.closerThan(endPoint, 10)) {
                        double angleRadians = Math.atan2(endPoint.getZ() - moving.getZ(), endPoint.getX() - moving.getX());
                        double angleDegrees = Math.toDegrees(angleRadians);
                        double randomDistance = Mth.randomBetweenInclusive(randomSource, 10, 25);

                        double angleRangeDegrees = Mth.randomBetween(randomSource, (float) (angleDegrees - 90), (float) (angleDegrees + 90));
                        double angleRangeRadians = Math.toRadians(angleRangeDegrees);

                        int randomX = moving.getX() + (int) (randomDistance * Math.cos(angleRangeRadians));
                        int randomZ = moving.getZ() + (int) (randomDistance * Math.sin(angleRangeRadians));
                        moving = new Vec3i(randomX, 0, randomZ);
                        canyonPointStorage.setPoint(moving);
                    }
                }
            }
        }
    }

    private void loadAnchors(int radius, int canyonPosX, int canyonPosZ) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int offsetCanyonPosX = canyonPosX + x;
                int offsetCanyonPosZ = canyonPosZ + z;
                int offsetMinX = fromCanyonPos(offsetCanyonPosX);
                int offsetMinZ = fromCanyonPos(offsetCanyonPosZ);

                int offsetMaxX = fromCanyonPos(offsetCanyonPosX + 1) - 1;
                int offsetMaxZ = fromCanyonPos(offsetCanyonPosZ + 1) - 1;

                if (canyonAnchorStorage.getPointsInBox(new Vec3i(offsetMinX, 0, offsetMinZ), new Vec3i(offsetMaxX, 0, offsetMaxZ)).isEmpty()) {
                    loadCanyonAnchors(offsetCanyonPosX, offsetCanyonPosZ);
                }
            }
        }
    }

    private void loadCanyonAnchors(int canyonPosX, int canyonPosZ) {
        long key = ChunkPos.asLong(canyonPosX, canyonPosZ);

        int minX = fromCanyonPos(canyonPosX);
        int minZ = fromCanyonPos(canyonPosZ);

        int maxX = fromCanyonPos(canyonPosX + 1) - 1;
        int maxZ = fromCanyonPos(canyonPosZ + 1) - 1;

        RandomSource randomSource = RandomSource.create(key);

        // Generate Canyon Anchors to create worms between
        for (int i = 0; i < 2; i++) {
            int x = randomSource.nextInt(minX, maxX);
            int z = randomSource.nextInt(minZ, maxZ);
            this.canyonAnchorStorage.setPoint(new BlockPos(x, 0, z));
        }


    }


    public void worldGenChunkUnload(ProtoChunk protoChunk) {
        BlockPos worldPosition = protoChunk.getPos().getWorldPosition();
        int canyonPosX = toCanyonPos(worldPosition.getX());
        int canyonPosZ = toCanyonPos(worldPosition.getZ());
        long key = ChunkPos.asLong(canyonPosX, canyonPosZ);

        int minX = fromCanyonPos(canyonPosX);
        int minZ = fromCanyonPos(canyonPosZ);

        int maxX = fromCanyonPos(canyonPosX + 1) - 1;
        int maxZ = fromCanyonPos(canyonPosZ + 1) - 1;

        loadedChunksStorage.removePoint(protoChunk.getPos().getWorldPosition());

//        if (loadedChunksStorage.getTargetsInBox(new Vec3i(minX, 0, minZ), new Vec3i(maxX, 0, maxZ)).isEmpty()) {
//            canyonAnchorStorage.removePointsInBox(new Vec3i(minX, 0, minZ), new Vec3i(maxX, 0, maxZ));
//            canyonPointStorage.removePointsInBox(new Vec3i(minX, 0, minZ), new Vec3i(maxX, 0, maxZ));
//        }
    }

    public void afterSurface(ProtoChunk protoChunk, WorldGenRegion region) {
        postProcess(protoChunk, region);
    }


    public int toCanyonPos(int worldCoord) {
        return worldCoord >> 10;
    }

    public int fromCanyonPos(int canyonCoord) {
        return canyonCoord << 10;
    }


    public void postProcess(ProtoChunk chunk, WorldGenRegion region) {
        ChunkPos chunkPos = chunk.getPos();






        if (true) {
            BlockPos worldPosition = chunkPos.getWorldPosition();

            BoundingBox box = BoundingBox.fromCorners(new Vec3i(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ()), new Vec3i(chunkPos.getMaxBlockX(), 0, chunkPos.getMaxBlockZ())).inflatedBy(100, 0, 100);

            List<Vec3i> pointsInBox = new ArrayList<>(canyonPointStorage.getPointsInBox(new Vec3i(box.minX(), box.minY(), box.minZ()), new Vec3i(box.maxX(), box.maxY(), box.maxZ())).stream().toList());
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(0, 0, 0);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int blockX = chunkPos.getBlockX(x);
                    int blockZ = chunkPos.getBlockZ(z);
                    mutable.set(blockX, 0, blockZ);
                    chunk.setBlockState(mutable, Blocks.STONE.defaultBlockState(), false);
                    if (pointsInBox.isEmpty()) {
                        continue;
                    }

                    Collections.sort(pointsInBox, Comparator.comparingDouble(mutable::distSqr));
                    Vec3i first = pointsInBox.getFirst();
                    if (first.getX() == blockX && first.getZ() == blockZ) {
                        chunk.setBlockState(mutable.above(), Blocks.EMERALD_BLOCK.defaultBlockState(), false);
                    }


                    if (pointsInBox.size() == 1) {
                        if (first.closerThan(mutable, 5)) {
                            chunk.setBlockState(mutable, Blocks.EMERALD_BLOCK.defaultBlockState(), false);
                        }

                    } else {
                        double distanceToClosestPoint = Math.sqrt(getDistanceToClosestPoint(first, pointsInBox.get(1), blockX, blockZ));
                        if (distanceToClosestPoint <= 25) {
                            chunk.setBlockState(mutable, Blocks.DIAMOND_BLOCK.defaultBlockState(), false);

                        } else {
                            chunk.setBlockState(mutable, Blocks.STONE.defaultBlockState(), false);

                        }
                    }

                }
            }

            return;
        }

        long seed = region.getSeed();
        WorldgenRandom worldgenRandom = new WorldgenRandom(new XoroshiroRandomSource(seed));

        int distance = 100;


        BlockState[] bands = makeBands(worldgenRandom);


        BlendingFunction[] functions = new BlendingFunction[]{
                BlendingFunction.EaseInOutCirc.INSTANCE,
                BlendingFunction.EaseInCirc.INSTANCE,
                BlendingFunction.EaseOutCubic.INSTANCE,
                BlendingFunction.EaseOutQuint.INSTANCE
        };


        int layerMinY = 63;
        for (int layer = 0; layer <= worldgenRandom.nextIntBetweenInclusive(4, 15); layer++) {
            ImprovedNoise layerNoise = new ImprovedNoise(new WorldgenRandom(new XoroshiroRandomSource(seed + layer)));
            WorldgenRandom layerRandom = new WorldgenRandom(new XoroshiroRandomSource(seed + layer));

            BlendingFunction blendingFunction = functions[layerRandom.nextInt(functions.length)];
            int layerIncrement = layerRandom.nextIntBetweenInclusive(5, 30);
            layerMinY += layerIncrement;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int blockX = chunkPos.getBlockX(x);
                    int blockZ = chunkPos.getBlockZ(z);

                    List<Target<Vec3i, Vec3i>> list = canyonPointStorage.getNearbyTargets(new Vec3i(blockX, 0, blockZ), 4, Point::distSqr).stream().toList();

                    double distSqr = Math.sqrt(getDistanceToClosestPoint(list.get(0).point(), list.get(1).point(), blockX, blockZ));


                    buildLayer(blendingFunction, distSqr, distance, blockX, blockZ, layerMinY, 100, 150, pos1 -> {
                        BlockState state = bands[Math.floorMod(pos1.getY() + Math.round((layerNoise.noise(pos1.getX() * 0.05F, 0, pos1.getZ() * 0.05F) + 1) * 6), bands.length - 1)];
                        if (chunk.getBlockState(pos1).canBeReplaced()) {
                            chunk.setBlockState(pos1, state, false);
                        } else {
                            return true;
                        }
                        return false;
                    });
                }
            }
        }
    }

    private static BlockState @NotNull [] makeBands(WorldgenRandom worldgenRandom) {
        BlockState[] bandStates = new BlockState[]{
                Blocks.TERRACOTTA.defaultBlockState(),
                Blocks.WHITE_TERRACOTTA.defaultBlockState(),
                Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(),
                Blocks.WHITE_TERRACOTTA.defaultBlockState(),
                Blocks.ORANGE_TERRACOTTA.defaultBlockState()
        };

        BlockState[] bands = new BlockState[25];


        for (int i = 0; i < bands.length; ) {
            BlockState selectedState = bandStates[worldgenRandom.nextInt(bandStates.length)];

            int fillerSize = worldgenRandom.nextInt(1, 5);
            for (int filler = 0; filler < fillerSize; filler++) {
                if (i >= bands.length) {
                    break;
                }

                bands[i] = selectedState;
                i++;
            }
        }
        return bands;
    }


    private static double getDistanceToClosestPoint(Vec3i first, Vec3i second, int worldX, int worldZ) {
        Vector2d point = new Vector2d(worldX, worldZ);
        Vector2d nearest = new Vector2d(first.getX(), first.getZ());
        Vector2d secondNearest = new Vector2d(second.getX(), second.getZ());

        Vector2d line = new Vector2d(secondNearest).sub(nearest);

        if (line.length() == 0) {
            return point.distanceSquared(nearest);
        }

        line = line.normalize();

        double dotProduct = new Vector2d(point).sub(nearest).dot(line);
        dotProduct = Mth.clamp(dotProduct, 0, line.length());
        Vector2d result = new Vector2d(nearest).add(new Vector2d(line).mul(dotProduct));
        return result.distanceSquared(point);
    }


    private static void buildLayer(BlendingFunction function, double distSqr, int distance, int blockX, int blockZ, int minY, int min, int max, Predicate<BlockPos> blockPosPredicate) {
        double apply = function.apply(Mth.clampedLerp(0, 1, (distSqr) / (distance)), min, max);


        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int y = (int) Math.round(apply); y >= minY; y--) {

            mutableBlockPos.set(blockX, y, blockZ);
            if (blockPosPredicate.test(mutableBlockPos)) {
                break;
            }
        }
    }

    private static void setTopBlocks(ProtoChunk protoChunk, ChunkPos chunkPos, int[] topYs, BlockPos.MutableBlockPos mutable) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int blockX = chunkPos.getBlockX(x);
                int blockZ = chunkPos.getBlockZ(z);
                int idx = x + z * 16;

                int topY = topYs[idx];
                if (topY != Integer.MIN_VALUE) {
                    mutable.set(blockX, topY, blockZ);

                    if (!protoChunk.getBlockState(mutable.move(Direction.DOWN)).isAir()) {
                        mutable.move(Direction.UP);
                        protoChunk.setBlockState(mutable, Blocks.GRASS_BLOCK.defaultBlockState(), false);
                    }

                    mutable.move(Direction.DOWN);
                    if (!protoChunk.getBlockState(mutable.move(Direction.DOWN)).isAir()) {
                        mutable.move(Direction.UP);
                        protoChunk.setBlockState(mutable, Blocks.DIRT.defaultBlockState(), false);
                    }
                }
            }
        }
    }
}

