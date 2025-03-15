# Client <-> Server / Bidirectional Networking
* This page will cover creating bidirectional packets that can be interchangeably sent from the server to client and client to server.


* TLDR: We will be creating a NetworkContainer that can register packets to our mod id/namespace and registering packets to the container.

# 1. Create a Bidirectional NetworkContainer
* In order to begin we will start by creating a Bidirectional NetworkContainer. This will ensure that our packets are correctly registered to the right namespace/mod id and will track all our packets for our mod.
* One BiDirectionalNetworkContainer can be used for registering both client and server packets.
* One BidirectionalNetworkContainer can only exist per namespace/mod id. `BiDirectionalNetworkContainer.of(modid)` will always return the same instance for the provided mod id.

```java
// Other imports
import dev.corgitaco.dataanchor.network.BiDirectionalNetworkContainer;

// Some other code
public static final BiDirectionalNetworkContainer NETWORK_CONTAINER = BiDirectionalNetworkContainer.of("modid");
```

# 2. Creating a packet
* Now that we have our NetworkContainer we can create a packet.
* Your packet class will need to implement the `Packet` interface. The interface will require you to implement the following methods:
  * `write(PacketByteBuf buf)`: This will to write your data to the provided `PacketByteBuf` object.
  * `read(PacketByteBuf buf)`: You will need to read your data from the `PacketByteBuf` object.
  * `handle(Level level, Player player)`: You will execute the functionality of your packet here. 
    * You can check what side your packet is on by doing the following:
      ```java
      if (level.isClientSide()) {
          // Client side code
      } else {
          // Server side code
      }
      ```
  * This method is executed off the main thread.
    * If you are on the server side and would like to execute code on the main server thread you can use the following:
      ```java
        level.getServer().execute(() -> { /* your code here */ });
      ```
    * If you are on the client side you will need a bit more boilerplate to run your code on the main client thread. You can use the following:
      * Make a utility class to run your code on the main client thread:
        ```java
        public static class ClientUtil {
            public static void execute(Runnable runnable) {
                Minecraft.getInstance().execute(runnable);
            }
        }
        ```
      * Then you can call your utility class here to run your code on the main client thread:
        ```java
        ClientUtil.execute(() -> { /* your code here. Make sure NOT to call client classes here! */ });
        ```
## Example Bidirectional Packet
* Here is an example of a packet that sends a basic string between the client and server:

### Java Example
```java
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ExampleBidirectionalStringPacket(String s) implements Packet {

    // Used to create our packet received from the network.
    public ExampleBidirectionalStringPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }
  
    // Used to write our packet to the network.
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.s);
    }

    // Used to handle our packet when it is received.
    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
      if (level != null) {
        if (level.isClientSide) {
            ClientUtil.execute(() -> {
                System.out.println("Received string: " + this.s);
            });
        } else {
          level.getServer().execute(() -> {
              System.out.println("Received string from client: " + this.s);
          });
        }
      }
    }

    // Handy utility to run a task on the client thread without exploding on the server.
    private static class ClientUtil {
        private static void execute(Runnable runnable) {
            Minecraft.getInstance().execute(runnable);
        }
    }
}
```

# 3. Registering the packet
* With our packet created we can now register it to our BiDirectionalNetworkContainer
* YOU MUST REGISTER YOUR PACKET(S) ANYTIME BEFORE:
  * FORGE'S `FMLCommonSetupEvent` event fires.
  * FABRIC'S `DedicatedServerModInitializer#onInitializeServer` / `ClientModInitializer#onInitializeClient` methods run.
  * If you register your packets too late, your network container will be locked and throw an `IllegalArgumentException`.
* You can register your packet like so(we will use our example packet from above):
  ```java
  public static void registerPackets() {
          NETWORK_CONTAINER.registerPacketHandler("example_string", // Don't worry we are registering this to our namespace / mod id.
                  new Packet.Handler<>(
                          ExampleBidirectionalStringPacket.class,
                          ExampleBidirectionalStringPacket::write,
                          ExampleBidirectionalStringPacket::new,
                          ExampleBidirectionalStringPacket::handle)
          );
      }
  ```
  
# 4. Sending the packet
* Now that we have our packet registered we can send it! We will use the example packet from above.
  * You can send your packet in various ways! Here are a few examples:
    * To send a packet to the server from the client:
      ```java
      BiDirectionalPacketBroadcaster.BI.sendToServer(new ExampleBidirectionalStringPacket("Meow!"));
      ```
    * To send a packet to all connected players on a server regardless of their dimension:
      ```java
      BiDirectionalPacketBroadcaster.BI.sendToAllPlayers(new ExampleBidirectionalStringPacket("Meow!"));
      ```
    * To send to all players in a given dimension:
      ```java
      BiDirectionalPacketBroadcaster.BI.sendToAllPlayersInDimension(new ExampleBidirectionalStringPacket("Meow!"), Level.OVERWORLD/*This is a ResourceKey<Level>*/);
      ```
    * To send to a specific player:
      ```java
      BiDirectionalPacketBroadcaster.BI.sendToPlayer(new ExampleBidirectionalStringPacket("Meow!"), serverPlayer);
      ```
    * To send to all players tracking a specific entity:
      ```java
      BiDirectionalPacketBroadcaster.BI.sendToPlayer(new ExampleBidirectionalStringPacket("Meow!"), entity);
      ```
    * To send to all players tracking a specific entity and themselves(if the entity is a player):
      ```java
      BiDirectionalPacketBroadcaster.BI.trackingEntityAndSelf(new ExampleBidirectionalStringPacket("Meow!"), entity);
      ```
    * To send to all players tracking a specific entity and themselves(if the entity is a player):
      ```java
      BiDirectionalPacketBroadcaster.BI.trackingChunk(new ExampleBidirectionalStringPacket("Meow!"), levelChunk/*Must be a levelchunk so we can guaranteed its loaded, use its level, and its chunk position.*/);
      ```
    * To send to all players tracking a specific entity and themselves(if the entity is a player):
      ```java
      BiDirectionalPacketBroadcaster.BI.sendNearPositionInDimension(new ExampleBidirectionalStringPacket("Meow!"), levelChunk/*Must be a levelchunk so we can guaranteed its loaded, use its level, and its chunk position.*/);
      ```
    * To send to all players within the radius of a given position:
      ```java
      BiDirectionalPacketBroadcaster.BI.sendNearPositionInDimension(new ExampleBidirectionalStringPacket("Meow!"), Level.OVERWORLD/*This is a ResourceKey<Level>*/, 0.5, 0.5, 0.5, 100.0)
      ```