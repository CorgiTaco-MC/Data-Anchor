# Client -> Server (C2S) Networking
* This page will cover creating packets that can be sent from the client to the server. 
* The acronym C2S stands for Client to Server.


* TLDR: We will be creating a NetworkContainer that can register packets to our mod id/namespace and registering packets to the container.

# 1. Create a C2S NetworkContainer
* In order to begin we will start by creating a C2SNetworkContainer. This will ensure that our packets are correctly registered to the right namespace/mod id and will track all our packets for our mod.
* One C2SNetworkContainer can be used for registering client to server packets.
* One C2SNetworkContainer can only exist per namespace/mod id. `C2SNetworkContainer.of(modid)` will always return the same instance for the provided mod id.

```java
// Other imports
import dev.corgitaco.dataanchor.network.C2SNetworkContainer;

// Some other code
public static final C2SNetworkContainer NETWORK_CONTAINER = C2SNetworkContainer.of("modid");
```

# 2. Creating a packet
* Now that we have our NetworkContainer we can create a packet.
* Your packet class will need to implement the `Packet` interface. The interface will require you to implement the following methods:
    * `write(PacketByteBuf buf)`: This will to write your data to the provided `PacketByteBuf` object.
    * `read(PacketByteBuf buf)`: You will need to read your data from the `PacketByteBuf` object.
    * `handle(Level level, Player player)`: You will execute the functionality of your packet here.
        * You can check what side your packet is on by doing the following:
        * This method is executed off the main thread.
            * To execute code on the main server thread you can use the following:
              ```java
                level.getServer().execute(() -> { /* your code here */ });
              ```
        * If you need a server level, you can safely cast the level to a `ServerLevel` like so `ServerLevel serverLevel = (ServerLevel) level;`

## Example C2S Packet
* Here is an example of a packet that sends a basic string from the client to the server:

### Java Example
```java
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ExampleC2SStringPacket(String s) implements Packet {

    // Used to create our packet received from the network.
    public ExampleC2SStringPacket(FriendlyByteBuf buf) {
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
      if (level instanceof ServerLevel serverLevel) { // Checks if our server is null for us and gives us access to a ServerLevel which we may need for ServerLevel specific functions.
          level.getServer().execute(() -> {
              System.out.println("Received string from client: " + this.s);
          });
        }
      }
    }
}
```

# 3. Registering the packet
* With our packet created we can now register it to our C2SNetworkContainer
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
                          ExampleC2SStringPacket.class,
                          ExampleC2SStringPacket::write,
                          ExampleC2SStringPacket::new,
                          ExampleC2SStringPacket::handle)
          );
      }
  ```

# 4. Sending the packet
* Now that we have our packet registered we can send it! We will use the example packet from above.
    * You can send your packet in various ways! Here are a few examples:
        * To send a packet to the server from the client:
          ```java
          C2SPacketBroadcaster.C2S.sendToServer(new ExampleBidirectionalStringPacket("Meow!"));
          ```