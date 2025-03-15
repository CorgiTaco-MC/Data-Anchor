# Client -> Server (S2C) Networking
* This page will cover creating packets that can be sent from the server to the client.
* The acronym S2C stands for Server to Client.


* TLDR: We will be creating a NetworkContainer that can register packets to our mod id/namespace and registering packets to the container.

# 1. Create a S2C NetworkContainer
* In order to begin we will start by creating a S2CNetworkContainer. This will ensure that our packets are correctly registered to the right namespace/mod id and will track all our packets for our mod.
* One S2CNetworkContainer can be used for registering client to server packets.
* One S2CNetworkContainer can only exist per namespace/mod id. `S2CNetworkContainer.of(modid)` will always return the same instance for the provided mod id.

```java
// Other imports
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;

// Some other code
public static final S2CNetworkContainer NETWORK_CONTAINER = S2CNetworkContainer.of("modid");
```

# 2. Creating a packet
* Now that we have our NetworkContainer we can create a packet.
* Your packet class will need to implement the `Packet` interface. The interface will require you to implement the following methods:
    * `write(PacketByteBuf buf)`: This will to write your data to the provided `PacketByteBuf` object.
    * `read(PacketByteBuf buf)`: You will need to read your data from the `PacketByteBuf` object.
    * `handle(Level level, Player player)`: You will execute the functionality of your packet here.
        * You can check what side your packet is on by doing the following:
        * This method is executed off the main thread.
      * I would heavily suggest you make a utility class to run your code on the main client thread:
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

## Example S2C Packet
* Here is an example of a packet that sends a basic string from the server to the client:

### Java Example
```java
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ExampleS2CStringPacket(String s) implements Packet {

    // Used to create our packet received from the network.
    public ExampleS2CStringPacket(FriendlyByteBuf buf) {
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
      if (level instanceof ClientLevel clientLevel) { // Checks if our client level is null for us and gives us access to a ClientLevel which we may need for ClientLevel specific functions.
          ClientUtil.execute(() -> {
              System.out.println("Received string: " + this.s);
          });
        }
      }
    }
}
```

# 3. Registering the packet
* With our packet created we can now register it to our S2CNetworkContainer
* YOU MUST REGISTER YOUR PACKET(S) ANYTIME BEFORE:
    * FORGE'S `FMLCommonSetupEvent` event fires.
    * FABRIC'S `DedicatedServerModInitializer#onInitializeServer` / `ClientModInitializer#onInitializeClient` methods run.
    * If you register your packets too late, your network container will be locked and throw an `IllegalArgumentException`.
* You can register your packet like so(we will use our example packet from above):
  ```java
  public static void registerPackets() {
          NETWORK_CONTAINER.registerPacketHandler(
                  "example_string", // Don't worry we are registering this to our namespace / mod id.
                  new Packet.Handler<>(
                          ExampleS2CStringPacket.class,
                          ExampleS2CStringPacket::write,
                          ExampleS2CStringPacket::new,
                          ExampleS2CStringPacket::handle)
          );
      }
  ```

# 4. Sending the packet
* Now that we have our packet registered we can send it! We will use the example packet from above.
    * You can send your packet in various ways! Here are a few examples:
      * To send a packet to all connected players on a server regardless of their dimension:
        ```java
        S2CPacketBroadcaster.S2C.sendToAllPlayers(new ExampleBidirectionalStringPacket("Meow!"));
        ```
      * To send to all players in a given dimension:
        ```java
        S2CPacketBroadcaster.S2C.sendToAllPlayersInDimension(new ExampleBidirectionalStringPacket("Meow!"), Level.OVERWORLD/*This is a ResourceKey<Level>*/);
        ```
      * To send to a specific player:
        ```java
        S2CPacketBroadcaster.S2C.sendToPlayer(new ExampleBidirectionalStringPacket("Meow!"), serverPlayer);
        ```
      * To send to all players tracking a specific entity:
        ```java
        S2CPacketBroadcaster.S2C.trackingEntity(new ExampleBidirectionalStringPacket("Meow!"), entity);
        ```
      * To send to all players tracking a specific entity and themselves(if the entity is a player):
        ```java
        S2CPacketBroadcaster.S2C.trackingEntityAndSelf(new ExampleBidirectionalStringPacket("Meow!"), entity);
        ```
      * To send to all players tracking a specific entity and themselves(if the entity is a player):
        ```java
        S2CPacketBroadcaster.S2C.trackingChunk(new ExampleBidirectionalStringPacket("Meow!"), levelChunk/*Must be a levelchunk so we can guaranteed its loaded, use its level, and its chunk position.*/);
        ```
      * To send to all players tracking a specific entity and themselves(if the entity is a player):
        ```java
        S2CPacketBroadcaster.S2C.sendNearPositionInDimension(new ExampleBidirectionalStringPacket("Meow!"), levelChunk/*Must be a levelchunk so we can guaranteed its loaded, use its level, and its chunk position.*/);
        ```
      * To send to all players within the radius of a given position:
        ```java
        S2CPacketBroadcaster.S2C.sendNearPositionInDimension(new ExampleBidirectionalStringPacket("Meow!"), Level.OVERWORLD/*This is a ResourceKey<Level>*/, 0.5, 0.5, 0.5, 100.0)
        ```