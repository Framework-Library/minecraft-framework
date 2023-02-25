package games.negative.framework.v1_14;

import games.negative.framework.base.gui.sign.SignVersionWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class SignWrapperProvider1_14 implements SignVersionWrapper {

    private final boolean IS_1_14_1 = Pattern.compile("1\\.14\\.\\d").matcher(Bukkit.getBukkitVersion()).find();


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
                Material.ACACIA_SIGN, Material.DARK_OAK_SIGN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openSignEditor(Player player, String[] lines, Material type, DyeColor color, Location signLoc, BiFunction<Player, String[], String[]> function) throws NoSuchFieldException, IllegalAccessException {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        PlayerConnection conn = p.playerConnection;
        Location loc = signLoc != null ? signLoc : getLocation(player, 1);
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        TileEntitySign sign = new TileEntitySign();
        sign.setPosition(pos);

        Field field = sign.getClass().getDeclaredField(IS_1_14_1 ? "color" : "l");
        field.setAccessible(true);
        field.set(sign, EnumColor.valueOf(color.toString()));
        field.setAccessible(false);

        for (int i = 0; i < lines.length; i++)
            sign.a(i, new ChatComponentText(lines[i] != null ? lines[i] : ""));

        player.sendBlockChange(loc, type.createBlockData());
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
                        if (updateSign.b().equals(pos)) {
                            String[] response = function.apply(player, updateSign.c());
                            if (response != null) {
                                String[] newLines = Arrays.copyOf(response, 4);
                                for (int i = 0; i < newLines.length; i++)
                                    sign.a(i, new ChatComponentText(newLines[i] != null ? newLines[i] : ""));
                                conn.sendPacket(sign.getUpdatePacket());
                                conn.sendPacket(new PacketPlayOutOpenSignEditor(pos));
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
