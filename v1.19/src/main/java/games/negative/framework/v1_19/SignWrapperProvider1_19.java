package games.negative.framework.v1_19;

import games.negative.framework.base.gui.sign.SignVersionWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.entity.TileEntitySign;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class SignWrapperProvider1_19 implements SignVersionWrapper {


    /**
     * {@inheritDoc}
     */
    @Override
    public Material getDefaultType() {
        return Material.OAK_SIGN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Material> getSignTypes() {
        return Arrays.asList(Material.OAK_SIGN, Material.BIRCH_SIGN, Material.SPRUCE_SIGN, Material.JUNGLE_SIGN,
                Material.ACACIA_SIGN, Material.DARK_OAK_SIGN, Material.CRIMSON_SIGN, Material.WARPED_SIGN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openSignEditor(Player player, String[] lines, Material type, DyeColor color, Location signLoc, BiFunction<Player, String[], String[]> function) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        PlayerConnection conn = p.b;
        Location loc = signLoc != null ? signLoc : getLocation(player, -63);
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        TileEntitySign sign = new TileEntitySign(pos, null);
        sign.a(EnumColor.valueOf(color.toString()));
        for (int i = 0; i < lines.length; i++)
            sign.a(i, IChatBaseComponent.a(lines[i]));

        player.sendBlockChange(loc, type.createBlockData());
        conn.a(sign.c());
        conn.a(new PacketPlayOutOpenSignEditor(pos));

        ChannelPipeline pipeline = conn.b.m.pipeline();
        if (pipeline.names().contains("SignGUI"))
            pipeline.remove("SignGUI");
        pipeline.addAfter("decoder", "SignGUI", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext chc, Packet<?> packet, List<Object> out) {
                try {
                    if (packet instanceof PacketPlayInUpdateSign) {
                        PacketPlayInUpdateSign updateSign = (PacketPlayInUpdateSign) packet;
                        if (updateSign.b().equals(pos)) {
                            String[] response = function.apply(player, updateSign.c());
                            if (response != null) {
                                String[] newLines = Arrays.copyOf(response, 4);
                                for (int i = 0; i < newLines.length; i++)
                                    sign.a(i, IChatBaseComponent.a(newLines[i]));
                                conn.a(sign.c());
                                conn.a(new PacketPlayOutOpenSignEditor(pos));
                            } else {
                                pipeline.remove("SignGUI");
                                player.sendBlockChange(loc, loc.getBlock().getBlockData());
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
