package dev.corgitaco.dataanchor.levelgen;

import com.google.common.util.concurrent.MoreExecutors;
import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.coord.impl.Point2D;
import dev.corgitaco.dataanchor.datastructure.Target;
import dev.corgitaco.dataanchor.datastructure.impl.QuadTreeNearestPoint;
import dev.corgitaco.dataanchor.datastructure.impl.QuadTreeNearestPointData;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ChunkRipper {

    private static final Executor CHUNK_RIPPER_MAIN_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Executor CHUNK_RIPPER_EXECUTORS = makeExecutor("chunk_ripper", Runtime.getRuntime().availableProcessors() - 1);

    private static final ChunkStatusUpdateListener EMPTY_UPDATES = (chunk, status) -> {
    };

    private final ServerChunkCache serverChunkCache;
    private final IdMap<Holder<Biome>> biomeLookup;
    private final ServerLevel level;


    private final QuadTreeNearestPointData<Point2D, ChunkStatus> statusesLoaded = new QuadTreeNearestPointData<>();
    private final QuadTreeNearestPointData<Point2D, VillageInfoContext> villages = new QuadTreeNearestPointData<>();

    private final QuadTreeNearestPoint<Point2D> villagePaths = new QuadTreeNearestPoint<>();
    private final QuadTreeNearestPointData<Point2D, PathingChunkData> pathingChunkData = new QuadTreeNearestPointData<>();

    public ChunkRipper(ServerLevel level) {
        this.level = level;
        serverChunkCache = CompletableFuture.supplyAsync(() -> createFakeServerChunkCache(level), CHUNK_RIPPER_MAIN_EXECUTOR).join();
        this.biomeLookup = level.registryAccess().registryOrThrow(Registries.BIOME).asHolderIdMap();
    }

    private static @NotNull ServerChunkCache createFakeServerChunkCache(ServerLevel level) {
        LevelStorageSource.LevelStorageAccess access = level.getServer().storageSource;
        return new ServerChunkCache(level,
                access,
                level.getServer().getFixerUpper(),
                level.getStructureManager(),
                CHUNK_RIPPER_EXECUTORS,
                level.getChunkSource().getGenerator(),
                1, 1,
                true,
                level.getServer().progressListenerFactory.create(1),
                EMPTY_UPDATES,
                () -> new DimensionDataStorage(access.getDimensionPath(level.dimension()).toFile(), level.getServer().getFixerUpper(), level.registryAccess()));

    }


    public void onChunkLoad(ProtoChunk chunk) {
        ChunkPos pos = chunk.getPos();
        CompletableFuture.supplyAsync(() -> {
            BlockPos worldPosition = pos.getWorldPosition();
            int canyonPosX = toCanyonPos(worldPosition.getX());
            int canyonPosZ = toCanyonPos(worldPosition.getZ());
            int minX = fromCanyonPos(canyonPosX);
            int minZ = fromCanyonPos(canyonPosZ);

            int maxX = fromCanyonPos(canyonPosX + 1) - 1;
            int maxZ = fromCanyonPos(canyonPosZ + 1) - 1;


            int radius = 1;
            ChunkStatus chunkStatus = ChunkStatus.STRUCTURE_STARTS;

            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    loadVillages(chunkStatus, canyonPosX + x, canyonPosZ + z);
                }
            }

            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    loadStatus(ChunkStatus.NOISE, canyonPosX + x, canyonPosZ + z);
                }
            }


            Collection<Target<Point2D, VillageInfoContext>> villagesInRange = this.villages.getTargetsInBox(new Point2D(minX, minZ), new Point2D(maxX, maxZ));
            int yDifference = 5;


            List<CompletableFuture<List<Point2D>>> futures = new ArrayList<>();
            for (Target<Point2D, VillageInfoContext> village : villagesInRange) {
                Point2D villageOnePosition = village.point();
                for (Target<Point2D, VillageInfoContext> neighboringVillage : this.villages.getNearbyTargets(village.point(), 3, Point::distSqr).stream().filter(point2DPoint2DTarget -> !point2DPoint2DTarget.equals(village)).toList()) {

                    Point2D neighboringVillagePosition = neighboringVillage.point();
                    if (village.get().connections().contains(neighboringVillagePosition) || neighboringVillage.get().connections().contains(village.point())) {
                        continue;
                    }

                    if (village.get().failures().contains(neighboringVillagePosition) || neighboringVillage.get().failures().contains(village.point())) {
                        continue;
                    }
                    int stepDistance = 32;

                    Point2D start = villageOnePosition;

                    Point2D end = neighboringVillagePosition;


                    futures.add(CompletableFuture.supplyAsync(() -> {
                        List<Point2D> setPositions = new ArrayList<>();

                        Point2D moving = start;

                        int lastY = getY(moving.getX(), moving.getZ());
                        double angleoffset = 0;

                        while (true) {
                            if (!setPositions.isEmpty()) {
                                lastY = getY(moving.getX(), moving.getZ());
                            }
                            setPositions.add(moving);
                            if (setPositions.size() >= 10000) {
                                village.value().failures().add(neighboringVillagePosition);
                                neighboringVillage.value().failures().add(villageOnePosition);
                                setPositions.clear();
                                break;
                            }

                            if (moving.distSqr(neighboringVillagePosition) < Mth.square(stepDistance - 1)) {
                                village.get().connections().add(neighboringVillagePosition);
                                neighboringVillage.get().connections().add(villageOnePosition);
                                System.out.println("Found path between " + villageOnePosition + " and " + neighboringVillagePosition);
                                break;
                            }


                            double angleRadians = Math.atan2(neighboringVillagePosition.getZ() - moving.getZ(), neighboringVillagePosition.getX() - moving.getX());
                            double angleDegrees = Math.toDegrees(angleRadians);

                            int offsetX = moving.getX() + (int) (stepDistance * Math.cos(angleRadians + Math.toRadians(angleoffset)));
                            int offsetZ = moving.getZ() + (int) (stepDistance * Math.sin(angleRadians + Math.toRadians(angleoffset)));

                            Holder<Biome> attemptedNextBiome = getBiomeHolder(offsetX, offsetZ);

                            boolean badBiomes = attemptedNextBiome.is(BiomeTags.IS_OCEAN) || attemptedNextBiome.is(BiomeTags.IS_MOUNTAIN) || attemptedNextBiome.is(BiomeTags.IS_HILL);
                            boolean greaterThan10 = getAbsoluteDifference(getY(offsetX, offsetZ), lastY) > yDifference;
                            boolean recalculate = badBiomes || greaterThan10;
                            if (recalculate) {
                                int[] angleOffsets = new int[]{-45, 45, -60, 60, -75, 75, -95, 95, -120, 120};
                                boolean failed = true;
                                for (int angleOffset : angleOffsets) {
                                    double angleRadians2 = Math.toRadians(angleDegrees + angleOffset);
                                    int offsetX2 = moving.getX() + (int) (stepDistance * Math.cos(angleRadians2));
                                    int offsetZ2 = moving.getZ() + (int) (stepDistance * Math.sin(angleRadians2));

                                    Holder<Biome> attemptedNextBiome2 = getBiomeHolder(offsetX2, offsetZ2);
                                    boolean isNotBadBiome = !attemptedNextBiome2.is(BiomeTags.IS_OCEAN) && !attemptedNextBiome2.is(BiomeTags.IS_MOUNTAIN) && !attemptedNextBiome2.is(BiomeTags.IS_HILL);
                                    boolean lessThan10 = getAbsoluteDifference(getY(offsetX2, offsetZ2), lastY) < yDifference;
                                    boolean passes = isNotBadBiome && lessThan10;
                                    if (passes) {
                                        moving = new Point2D(offsetX2, offsetZ2);
                                        failed = false;
                                        angleoffset = 0;
                                        break;
                                    }
                                }

                                if (failed) {
                                    angleoffset += 45;
                                    setPositions.remove(moving);
                                    moving = setPositions.getLast();
                                }
                            } else {
                                moving = new Point2D(offsetX, offsetZ);
                            }
                        }
                        return setPositions;
                    }, CHUNK_RIPPER_EXECUTORS));

                }
            }

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

            for (CompletableFuture<List<Point2D>> future : futures) {
                try {
                    List<Point2D> setPositions = future.get();
                    for (Point2D position : setPositions) {
                        villagePaths.setPoint(position, position);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }


            return Unit.INSTANCE;
        }, CHUNK_RIPPER_MAIN_EXECUTOR).join(); // Block chunk workers


        Collection<Target<Point2D, Point2D>> targetsInBox = villagePaths.getTargetsInBox(new Point2D(pos.getMinBlockX(), pos.getMinBlockZ()), new Point2D(pos.getMaxBlockX(), pos.getMaxBlockZ()));


        for (Target<Point2D, Point2D> inBox : targetsInBox) {
            BlockPos pos1 = new BlockPos(inBox.point().getX(), 150, inBox.get().getZ());
            chunk.setBlockState(pos1, Blocks.DIAMOND_BLOCK.defaultBlockState(), false);
        }
    }


    private int getAbsoluteDifference(int num1, int num2) {
        return Math.abs(Math.max(num1, num2) - Math.min(num1, num2));
    }

    private @Nullable Holder<Biome> getBiomeHolder(int x, int z) {
        Point2D chunkPos = new Point2D(SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(x)), SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(z)));

        Target<Point2D, PathingChunkData> nearestTarget = this.pathingChunkData.getPoint(chunkPos);

        int[] seaLevelBiomes = nearestTarget.get().surfaceBiomes();

        int biomeX = QuartPos.fromBlock(x & 15);
        int biomeZ = QuartPos.fromBlock(z & 15);

        Holder<Biome> biome = biomeLookup.byId(seaLevelBiomes[biomeX * 4 + biomeZ]);
        return biome;
    }

    private int getY(int x, int z) {
        Point2D chunkPos = new Point2D(SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(x)), SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(z)));

        Target<Point2D, PathingChunkData> nearestTarget = this.pathingChunkData.getPoint(chunkPos);

        int[] heights = nearestTarget.get().surfaceHeight();

        int localX = x & 15;
        int localZ = z & 15;

        return heights[localX * 16 + localZ];
    }


    private void loadVillages(ChunkStatus chunkStatus, int canyonPosX, int canyonPosZ) {
        int minX = fromCanyonPos(canyonPosX);
        int minZ = fromCanyonPos(canyonPosZ);

        int maxX = fromCanyonPos(canyonPosX + 1) - 1;
        int maxZ = fromCanyonPos(canyonPosZ + 1) - 1;

        Point2D min = new Point2D(minX, minZ);
        Point2D max = new Point2D(maxX, maxZ);
        Collection<Target<Point2D, ChunkStatus>> targetsInBox = statusesLoaded.getTargetsInBox(min, max);
        if (targetsInBox.isEmpty() || targetsInBox.stream().anyMatch(point2DChunkStatusTarget -> point2DChunkStatusTarget.get().isBefore(chunkStatus))) {
            queueChunksForWorldBigChunk(canyonPosX, canyonPosZ, chunkStatus, chunkAccess -> {
                statusesLoaded.setPoint(new Point2D(chunkAccess.getPos().getMinBlockX(), chunkAccess.getPos().getMinBlockZ()), chunkStatus);
                Registry<Structure> structures = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
                structures.getTag(StructureTags.VILLAGE).ifPresent(holders ->
                        holders.forEach(structureHolder -> {
                            StructureStart startForStructure = chunkAccess.getStartForStructure(structureHolder.value());

                            if (startForStructure != null) {
                                if (startForStructure.isValid()) {
                                    villages.setPoint(new Point2D(chunkAccess.getPos().getWorldPosition().getX(), chunkAccess.getPos().getWorldPosition().getZ()), new VillageInfoContext(new HashSet<>(), new HashSet<>()));
                                }
                            }
                        })
                );
            });
        }
    }

    private void loadStatus(ChunkStatus chunkStatus, int canyonPosX, int canyonPosZ) {
        int minX = fromCanyonPos(canyonPosX);
        int minZ = fromCanyonPos(canyonPosZ);

        int maxX = fromCanyonPos(canyonPosX + 1) - 1;
        int maxZ = fromCanyonPos(canyonPosZ + 1) - 1;

        Point2D min = new Point2D(minX, minZ);
        Point2D max = new Point2D(maxX, maxZ);
        if (statusesLoaded.getTargetsInBox(min, max).stream().anyMatch(point2DChunkStatusTarget -> point2DChunkStatusTarget.get().isBefore(chunkStatus))) {
            queueChunksForWorldBigChunk(canyonPosX, canyonPosZ, chunkStatus, chunkAccess -> {
                Point2D point = new Point2D(chunkAccess.getPos().getMinBlockX(), chunkAccess.getPos().getMinBlockZ());
                statusesLoaded.setPoint(point, chunkStatus);


                Heightmap orCreateHeightmapUnprimed = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);

                int[] heights = new int[16 * 16];
                orCreateHeightmapUnprimed.data.unpack(heights);

                int[] seaLevelBiomes = new int[4 * 4];

                for (int x = 0; x < 4; x++) {
                    for (int z = 0; z < 4; z++) {
                        Holder<Biome> noiseBiome = chunkAccess.getNoiseBiome(x, QuartPos.fromBlock(63), z);

                        seaLevelBiomes[x * 4 + z] = biomeLookup.getId(noiseBiome);
                    }
                }
                pathingChunkData.setPoint(point, new PathingChunkData(heights, seaLevelBiomes));
            });
        }
    }

    private void queueChunksForWorldBigChunk(int canyonPosX, int canyonPosZ, ChunkStatus status, Consumer<ChunkAccess> chunk) {
        int minX = fromCanyonPos(canyonPosX);
        int minZ = fromCanyonPos(canyonPosZ);

        int maxX = fromCanyonPos(canyonPosX + 1) - 1;
        int maxZ = fromCanyonPos(canyonPosZ + 1) - 1;

        List<CompletableFuture<ChunkAccess>> futures = new ArrayList<>();
        for (int chunkX = SectionPos.blockToSectionCoord(minX); chunkX <= SectionPos.blockToSectionCoord(maxX); chunkX++) {
            for (int chunkZ = SectionPos.blockToSectionCoord(minZ); chunkZ <= SectionPos.blockToSectionCoord(maxZ); chunkZ++) {
                CompletableFuture<ChunkAccess> chunkAccessCompletableFuture = queueChunk(chunkX, chunkZ, status)
                        .whenComplete((chunkAccess, throwable) -> chunk.accept(chunkAccess));
                futures.add(chunkAccessCompletableFuture);
            }
        }
        for (CompletableFuture<ChunkAccess> future : futures) {
            future.join();
        }
    }


    public CompletableFuture<ChunkAccess> queueChunk(int chunkX, int chunkZ, ChunkStatus status) {
        return this.serverChunkCache.getChunkFuture(chunkX, chunkZ, status, true).thenApply(chunkAccessChunkResult -> chunkAccessChunkResult.orElse(null));
    }

    public int toCanyonPos(int worldCoord) {
        return worldCoord >> 9;
    }

    public int fromCanyonPos(int canyonCoord) {
        return canyonCoord << 9;
    }

    private static ExecutorService makeExecutor(String serviceName, int maxThreads) {
        int i = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, maxThreads);
        ExecutorService executorservice;
        if (i <= 0) {
            executorservice = MoreExecutors.newDirectExecutorService();
        } else {
            AtomicInteger atomicinteger = new AtomicInteger(1);
            executorservice = new ForkJoinPool(i, forkJoinPool -> {
                ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(forkJoinPool) {
                    protected void onTermination(Throwable throwable) {
                        if (throwable != null) {
                            DataAnchor.LOGGER.warn("{} died", this.getName(), throwable);
                        } else {
                            DataAnchor.LOGGER.debug("{} shutdown", this.getName());
                        }

                        super.onTermination(throwable);
                    }
                };
                forkjoinworkerthread.setName("Worker-" + serviceName + "-" + atomicinteger.getAndIncrement());
                return forkjoinworkerthread;
            }, (t, e) -> {
            }, true);
        }

        return executorservice;
    }
}
