# Block Entity Tracked Data
* Before beginning, you need to think decide whether your data will be server side only or synced with all clients that can see the block entity you've attached to.

## 1. Create your Tracked Data class.
* If you'd like to your tracked data to be **server side only**, create a class that extends `ServerBlockEntityTrackedData`. 
* If you'd like to **sync your tracked data with all clients that can see the block entity**, create a class that extends `SyncedBlockEntityTrackedData`.
* For our example we will extend `SyncedBlockEntityTrackedData`.
    ```java
    public class ModIDBlockEntityTrackedData extends SyncedBlockEntityTrackedData {
        
        public ModIDBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
            super(trackedDataKey, blockEntity);
        }
    }
    ```
## 2. Creating your variables to store.
* You can add whatever variables of your choice to your class. Whether primitives or objects you can basically track any java type.
* For any variables you care about saving to disk, you will need to create a setter method so we can notify block entity that it has changed data, and it is time to save. In your setter(s) you will need to call the method `markDirty()` after setting your variable in the setter. This will notify Minecraft that it is time to save the given block entity.
* For any variables you care about syncing to the client, you will need to call the method `sync()` after setting your variable in the setter. This will send our changes to every player who has this block entity loaded.
    ```java
        // Some other code
        private int myInt; // Save me
        private String myString; // Save me
        public transient boolean myBoolean; // We will not save this boolean for example, so feel free to set the variable to public. Mark transient so it won't be saved via reflection if we don't implement save/load.
        private List<ItemStack> storedItems = new ArrayList<>(); // Save me
  
        public void setMyInt(int myInt) {
            this.myInt = myInt;
            // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
            markDirty(); 
            // Sync our changes to all clients who can view this block entity. 
            // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
            sync();
        }
        
        public void setMyString(String myString) {
            this.myString = myString;
            // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
            markDirty(); 
            // Sync our changes to all clients who can view this block entity. 
            // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
            sync();
        }
  
        public void addItemStack(ItemStack itemStack) {
            this.storedItems.add(itemStack);
            // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
            markDirty(); 
            // Sync our changes to all clients who can view this block entity. 
            // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
            sync();
        }
  
        public void removeItemStack(ItemStack itemStack) {
            this.storedItems.remove(itemStack);
            // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
            markDirty();
            // Sync our changes to all clients who can view this block entity. 
            // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
            sync();
        }
    ```


## 3. Saving And Read Disk Data
* *If your class contains only primitive types and/or basic collection implementations, your class will automatically save and load via reflection.*
* If you choose to not save any information to disk, you can make `save()` return null and have `load()` be empty.
* If your class contains any complex types or the need to find a complex type(ie from a string and registry, you will need to implement the `save()` and `load()` methods yourself. You can use the following code as a template:


* If we are only saving primitives and/or basic collection implementations, we can use the following code:
  ```java
      private int myInt; // Save me
      private String myString; // Save me
      private transient boolean myBoolean; // We will not save this boolean for example, so feel free to set the variable to public. Mark transient so it won't be saved via reflection if we don't implement save/load.
  
      // If we want this to be saved with a different name from our field.
      // Would consider this if using proguard or any other obfuscation tool on our mod.
      @com.google.gson.annotations.SerializedName("someBytevalue") 
      private byte myByte; // Save me
      
      // Some other code
      
      // No need to override, purely for demonstration purposes only
      @Override 
      public @Nullable CompoundTag save() {
          return super.save();
      }
  
      // No need to override, purely for demonstration purposes only
      @Override
      public void load(CompoundTag tag) {
          super.load(tag);
      }
      
      // Some Other code
  ```
* If we are saving complex data types that for example might require a registry look up, we will need to override save and load and input our data as needed.
  ```java
  // Imports
  import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
  import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
  import dev.corgitaco.dataanchor.data.type.blockentity.ServerBlockEntityTrackedData;
  import net.minecraft.nbt.*;
  import net.minecraft.world.item.ItemStack;
  import net.minecraft.world.level.block.entity.BlockEntity;
  import org.jetbrains.annotations.Nullable;
  
  import java.util.ArrayList;
  import java.util.List;
  import java.util.UUID;
  
  // Other code and class declaration
      private int myInt; // Save me
      private String myString; // Save me
      private boolean myBoolean; // Don't save me.
  
      // If we want this to be saved with a different name from our field. Annotate it with Gson's SerializedName
      // Would consider this if using proguard or any other obfuscation tool on our mod.
      @com.google.gson.annotations.SerializedName("someBytevalue") 
      private byte myByte; // Save me
      
      private List<ItemStack> storedItems = new ArrayList<>(); // Save me
      private List<UUID> uuids = new ArrayList<>(); // Save me

      // Some other code
      
      @Override 
      public @Nullable CompoundTag save() {
          CompoundTag tag = new CompoundTag();
          tag.putInt("myInt", this.myInt);
          tag.putString("myString", this.myString);
          tag.putByte("myByte", this.myByte);
          if (!storedItems.isEmpty()) {
              tag.put("storedItems", ItemStack.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.storedItems).getOrThrow(false, s -> {}));
          }

          if (!uuids.isEmpty()) {
             ListTag tags = new ListTag();
             for (UUID uuid : uuids) {
                 tags.add(NbtUtils.createUUID(uuid));
             }
             tag.put("uuids", tags);
          }
          return tag;
      }
  
      @Override
      public void load(CompoundTag tag) {
          this.myInt = tag.getInt("myInt");
          this.myString = tag.getString("myString");
          this.myByte = tag.getByte("myByte");
          if (tag.contains("storedItems", Tag.TAG_COMPOUND)) {
            List<ItemStack> storedItemsOnDisk = ItemStack.CODEC.listOf().decode(NbtOps.INSTANCE, tag.getList("storeditems", Tag.TAG_COMPOUND)).getOrThrow(false, s -> {}).getFirst();
            this.storedItems.clear();
            this.storedItems.addAll(storedItemsOnDisk);
        }
        if (tag.contains("uuids", Tag.TAG_LIST)) {
            ListTag tags = tag.getList("uuids", Tag.TAG_INT_ARRAY);
            for (Tag value : tags) {
                this.uuids.add(NbtUtils.loadUUID(value));
            }
        }
      }
      // Some Other code
  ``` 

# 4. Reading and Writing Data to network
* **SKIP THIS STEP IF NOT SYNCING / EXTENDING ServerBlockEntityTrackedData**
* For most developers, you can skip this stage, as the underlying interface will read and write to the network by utilizing the save and load methods.
* If you need to modify the way data is read and written to the network, you can override the `writeToNetwork()` and `readFromNetwork(CompoundTag tag)` methods and do whatever special logic here.
  * The super of these methods in the interface will use the load and save methods to read and write data to the network.
    ```java
      @Override
      public void readFromNetwork(CompoundTag tag) {
          super.readFromNetwork(tag);
          // Some special code here.
      }
  
      @Override
      public CompoundTag writeToNetwork() {
          // Some special code here.
          return super.writeToNetwork();
      }
    ```


## 5. Tracked Data Type Example classes
* Now that we've handled the basics of creating a tracked data class, let's look at our completed examples!

### Tracking and Saving Basic Data Types
  ```java
  import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
  import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
  import dev.corgitaco.dataanchor.data.type.blockentity.ServerBlockEntityTrackedData;
  import net.minecraft.world.level.block.entity.BlockEntity;
  
  public class ModIDBasicDataTypesBlockEntityTrackedData extends SyncedBlockEntityTrackedData {
  
      private int myInt; // Save me
      private String myString; // Save me
      public transient boolean myBoolean; // Don't save me.
      
      public ModIDBasicDataTypesBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
          super(trackedDataKey, blockEntity);
      }
  
      public void setMyInt(int myInt) {
          this.myInt = myInt;
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
  
      public void setMyString(String myString) {
          this.myString = myString;
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
      
      public int getMyInt() {
          return myInt;
      }
      
      public String getMyString() {
          return myString;
      }
  }
  ```

### Tracking and Saving Complex Data Types
  ```java
  import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
  import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
  import dev.corgitaco.dataanchor.data.type.blockentity.ServerBlockEntityTrackedData;
  import net.minecraft.nbt.*;
  import net.minecraft.world.item.ItemStack;
  import net.minecraft.world.level.block.entity.BlockEntity;
  
  import javax.annotation.Nullable;
  import java.util.ArrayList;
  import java.util.List;
  import java.util.UUID;
  
  public class ModIDComplexDataBlockEntityTrackedData extends SyncedBlockEntityTrackedData {
  
      private int myInt; // Save me
      private String myString; // Save me
      public boolean myBoolean; // Don't save me.
      private byte myByte; // Save me
  
      private List<ItemStack> storedItems = new ArrayList<>(); // Save me
      private List<UUID> uuids = new ArrayList<>(); // Save me
      
      public ModIDComplexDataBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
          super(trackedDataKey, blockEntity);
      }
      
      @Override
      public @Nullable CompoundTag save() {
          CompoundTag tag = new CompoundTag();
          tag.putInt("myInt", this.myInt);
          tag.putString("myString", this.myString);
          tag.putByte("myByte", this.myByte);
          if (!storedItems.isEmpty()) {
              tag.put("storedItems", ItemStack.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.storedItems).getOrThrow(false, s -> {}));
          }
  
          if (!uuids.isEmpty()) {
              ListTag tags = new ListTag();
              for (UUID uuid : uuids) {
                  tags.add(NbtUtils.createUUID(uuid));
              }
              tag.put("uuids", tags);
          }
          return tag;
      }
  
      @Override
      public void load(CompoundTag tag) {
          this.myInt = tag.getInt("myInt");
          this.myString = tag.getString("myString");
          this.myByte = tag.getByte("myByte");
          if (tag.contains("storedItems", Tag.TAG_COMPOUND)) {
              List<ItemStack> storedItemsOnDisk = ItemStack.CODEC.listOf().decode(NbtOps.INSTANCE, tag.getList("storeditems", Tag.TAG_COMPOUND)).getOrThrow(false, s -> {}).getFirst();
              this.storedItems.clear();
              this.storedItems.addAll(storedItemsOnDisk);
          }
          if (tag.contains("uuids", Tag.TAG_LIST)) {
              ListTag tags = tag.getList("uuids", Tag.TAG_INT_ARRAY);
              for (Tag value : tags) {
                  this.uuids.add(NbtUtils.loadUUID(value));
              }
          }
      }
  
      public void setMyInt(int myInt) {
          this.myInt = myInt;
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
  
      public void setMyString(String myString) {
          this.myString = myString;
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
  
      public void addItemStack(ItemStack itemStack) {
          this.storedItems.add(itemStack);
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
  
      public void removeItemStack(ItemStack itemStack) {
          this.storedItems.remove(itemStack);
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
      
      public void addUUID(UUID uuid) {
          this.uuids.add(uuid);
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
      public void removeUUID(UUID uuid) {
          this.uuids.remove(uuid);
          // Tell Minecraft to save this block entity next time the chunk that contains this block entity saves.
          markDirty();
          // Sync our changes to all clients who can view this block entity. 
          // This method only exists in SyncedBlockEntityTrackedData. Skip if extending ServerBlockEntityTrackedData.
          sync();
      }
  
      public byte getMyByte() {
          return myByte;
      }
  
      public int getMyInt() {
          return myInt;
      }
  
      public String getMyString() {
          return myString;
      }
  
      public List<ItemStack> getStoredItems() {
          return storedItems;
      }
  
      public List<UUID> getUuids() {
          return uuids;
      }
  }
  ```
### Attaching data to a block entity without saving.
  ```java
  import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
  import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
  import dev.corgitaco.dataanchor.data.type.blockentity.ServerBlockEntityTrackedData;
  import net.minecraft.nbt.CompoundTag;
  import net.minecraft.world.level.block.entity.BlockEntity;
  import javax.annotation.Nullable;
  
  public class ModIDComplexDataBlockEntityTrackedData extends SyncedBlockEntityTrackedData {
  
      public boolean someBoolean = false;
      public int someInt = 0;
  
      public ModIDComplexDataBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
          super(trackedDataKey, blockEntity);
      }
  
      @Override
      public @Nullable CompoundTag save() {
          return null;
      }
  
      @Override
      public void load(CompoundTag tag) {
      }
  }
  ```

## 7. Registering Your Tracked data
* Now that we have our tracked data class, we need to register it with the `TrackedDataRegistry` so that it can be loaded by the `BlockEntity` class and accessed by us anywhere a block entity is available!
  ```java
  import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
  import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
  
        // Class constructor here and other code.
  
      public static final TrackedDataKey<ModIDComplexDataBlockEntityTrackedData> COMPLEX_DATA = TrackedDataRegistries.BLOCK_ENTITY.register(
          new ResourceLocation("modid", "complex_data"),
          ModIDComplexDataBlockEntityTrackedData.class,
          ModIDComplexDataBlockEntityTrackedData::new
  );
  ```
* If you're interested in only attaching to a specify block entity based off a condition, you can use the factory to add your check(s), for this example let's only attach ourselves to all block entities that extend `ChestBlockEntity`:
  ```java
      public static final TrackedDataKey<ModIDComplexDataBlockEntityTrackedData> COMPLEX_DATA = TrackedDataRegistries.BLOCK_ENTITY.register(
            new ResourceLocation("modid", "chest_complex_data"),
            ModIDComplexDataBlockEntityTrackedData.class,
            (trackedDataKey, blockEntity) -> blockEntity instanceof ChestBlockEntity ? new ModIDComplexDataBlockEntityTrackedData(trackedDataKey, blockEntity) : null
    );
  ```

## 8. Accessing Your Tracked Data
* Now that we've registered our tracked data, we can try and access it anywhere a block entity is available! 
* I say **try** because depending on if we synced our data to client and if our block entity meets the requirements of our filter, we may not be able to access it for the given block entity. In this case we'll either get an `Optional<ModIDComplexDataBlockEntityTrackedData>` that is empty or present with a `ModIDComplexDataBlockEntityTrackedData` object:
  ```java
     Optional<ModIDComplexDataBlockEntityTrackedData> modIDComplexDataBlockEntityTrackedData = TrackedDataRegistries.BLOCK_ENTITY.get(COMPLEX_DATA, blockEntity);
     modIDComplexDataBlockEntityTrackedData.ifPresent(trackedData -> {
        trackedData.setMyInt(25);
        System.out.println(trackedData.getMyInt());
     });

  ```
  
# Handy Interfaces
* Data anchor offers some handy utility interfaces that may be implemented on your tracked data class to make your life easier and code cleaner.

## `TickableTrackedData`
* If you implement this interface, you will be able to override the `tick()` method to run code every tick.

## `PendingBlockEntityTick`
* If you implement this interface, you will be able to override the `blockEntityTick()` to run code after a block entity runs its pending ticks.