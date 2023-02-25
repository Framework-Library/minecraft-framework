package games.negative.framework.v1_11;

import games.negative.framework.base.gui.sign.SignVersionWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class SignWrapperProvider1_11 implements SignVersionWrapper {

    /**
     * {@inheritDoc}
     */
    @Override
    public Material getDefaultType() {
        return Material.SIGN_POST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Material> getSignTypes() {
        return Arrays.asList(Material.SIGN_POST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openSignEditor(Player player, String[] lines, Material type, DyeColor color, Location signLoc, BiFunction<Player, String[], String[]> function) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        PlayerConnection conn = p.playerConnection;
        Location loc = signLoc != null ? signLoc : getLocation(player, 1);
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        TileEntitySign sign = new TileEntitySign();
        sign.setPosition(pos);
        for (int i = 0; i < lines.length; i++)
            sign.lines[i] = new ChatComponentText(lines[i] != null ? lines[i] : "");

        player.sendBlockChange(loc, type, (byte) 0);
        conn.sendPacket(sign.getUpdatePacket());
        conn.sendPacket(new PacketPlayOutOpenSignEditor(pos));

        ChannelPipeline pipeline = conn.networkManager.channel.pipeline();
        if (pipeline.names().contains("SignGUI"))
            pipeline.remove("SignGUI");
        pipeline.addAfter("decoder", "SignGUI", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext chc, Packet<?> packet, List<Object> out) {
                try {
                    if (packet instanceof PacketPlayInUpdateSign) {
                        PacketPlayInUpdateSign updateSign = (PacketPlayInUpdateSign) packet;
                        if (updateSign.a().equals(pos)) {
                            String[] response = function.apply(player, updateSign.b());
                            if (response != null) {
                                String[] newLines = Arrays.copyOf(response, 4);
                                for (int i = 0; i < newLines.length; i++)
                                    sign.lines[i] = new ChatComponentText(newLines[i] != null ? newLines[i] : "");
                                conn.sendPacket(sign.getUpdatePacket());
                                conn.sendPacket(new PacketPlayOutOpenSignEditor(pos));
                            } else {
                                pipeline.remove("SignGUI");
                                player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                out.add(packet);
            }
        });
    }

}
