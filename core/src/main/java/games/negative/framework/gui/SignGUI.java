package games.negative.framework.gui;

import games.negative.framework.base.gui.sign.SignVersionWrapper;
import games.negative.framework.util.version.ServerVersion;
import games.negative.framework.util.version.VersionChecker;
import games.negative.framework.v1_10.SignWrapperProvider1_10;
import games.negative.framework.v1_11.SignWrapperProvider1_11;
import games.negative.framework.v1_12.SignWrapperProvider1_12;
import games.negative.framework.v1_13.SignWrapperProvider1_13;
import games.negative.framework.v1_14.SignWrapperProvider1_14;
import games.negative.framework.v1_15.SignWrapperProvider1_15;
import games.negative.framework.v1_16.SignWrapperProvider1_16;
import games.negative.framework.v1_17.SignWrapperProvider1_17;
import games.negative.framework.v1_18.SignWrapperProvider1_18;
import games.negative.framework.v1_19.SignWrapperProvider1_19;
import games.negative.framework.v1_8.SignWrapperProvider1_8;
import games.negative.framework.v1_9.SignWrapperProvider1_9;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SignGUI {


    private static SignVersionWrapper WRAPPER = null;
    private static final List<Material> signTypes;
    private static final String availableSignTypes;

    static {
        ServerVersion version = new VersionChecker().getServerVersion();
        switch (version) {
            case V1_20: {

            }

            case V1_19: {
                WRAPPER = new SignWrapperProvider1_19();
                break;
            }
            case V1_18: {
                WRAPPER = new SignWrapperProvider1_18();
                break;
            }

            case V1_17: {
                WRAPPER = new SignWrapperProvider1_17();
                break;
            }
            case V1_16: {
                WRAPPER = new SignWrapperProvider1_16();
                break;
            }
            case V1_15: {
                WRAPPER = new SignWrapperProvider1_15();
                break;
            }
            case V1_14: {
                WRAPPER = new SignWrapperProvider1_14();
                break;
            }
            case V1_13: {
                WRAPPER = new SignWrapperProvider1_13();
                break;
            }
            case V1_12: {
                WRAPPER = new SignWrapperProvider1_12();
                break;
            }
            case V1_11: {
                WRAPPER = new SignWrapperProvider1_11();
                break;
            }
            case V1_10: {
                WRAPPER = new SignWrapperProvider1_10();
                break;
            }
            case V1_9: {
                WRAPPER = new SignWrapperProvider1_9();
                break;
            }
            case V1_8: {
                WRAPPER = new SignWrapperProvider1_8();
                break;
            }
            default: {
                System.out.println("SignGUI does not support the server version \"" + version + "\"");
            }
        }

        signTypes = WRAPPER.getSignTypes();
        availableSignTypes = signTypes.stream().map(Material::toString).collect(Collectors.joining(", "));
    }

    /**
     * The lines to show.
     */
    private String[] lines;

    /**
     * The sign type.
     */
    private Material type;

    /**
     * The color of the sign (1.14+)
     */
    private DyeColor color;

    /**
     * If enabled, the returned lines will not have any colors.
     */
    private boolean stripColor;

    /**
     * The location where the sign should be placed. Can be null for default. See {@link SignVersionWrapper#getLocation(org.bukkit.entity.Player, int)}
     */
    private Location signLoc;

    /**
     * The {@link java.util.function.BiFunction} which will be executed when the editing is finished. See {@link SignGUI#onFinish(java.util.function.BiFunction)}
     */
    private BiFunction<Player, String[], String[]> function;

    /**
     * Constructs a new SignGUI.
     */
    public SignGUI() {
        lines = new String[4];
        type = WRAPPER.getDefaultType();
        color = DyeColor.BLACK;
        stripColor = false;
    }

    /**
     * Sets the lines that are shown on the sign.
     *
     * @param lines The lines, may be less than 4.
     * @return The {@link SignGUI} instance
     */
    public SignGUI lines(String... lines) {
        this.lines = Arrays.copyOf(lines, 4);
        return this;
    }

    /**
     * Sets a specific line that is shown on the sign.
     *
     * @param index The index of the line.
     * @param line  The line.
     * @return The {@link SignGUI} instance
     * @throws java.lang.IllegalArgumentException If the index is below 0 or above 4.
     */
    public SignGUI line(int index, String line) {
        Validate.isTrue(index >= 0 && index <= 3, "Index out of range");
        lines[index] = line;
        return this;
    }

    /**
     * Sets the type of the sign.
     *
     * @param type The type. Must be a sign type.
     * @return The {@link SignGUI} instance
     */
    public SignGUI type(Material type) {
        Validate.notNull(type, "Type cannot be null");
        Validate.isTrue(signTypes.contains(type), type + " is not a sign type. Available sign types: " + availableSignTypes);
        this.type = type;
        return this;
    }

    /**
     * Sets the color of the sign (1.14+)
     *
     * @param color The new color
     * @return The {@link SignGUI} instance
     */
    public SignGUI color(DyeColor color) {
        Validate.notNull(color, "The color cannot be null");
        this.color = color;
        return this;
    }

    /**
     * Sets stripColor to true. See {@link SignGUI#stripColor(boolean)}
     *
     * @return The {@link SignGUI} instance
     */
    public SignGUI stripColor() {
        stripColor = true;
        return this;
    }

    /**
     * If enabled, the returned lines will not have any colors.
     *
     * @return The {@link SignGUI} instance
     */
    public SignGUI stripColor(boolean stripColor) {
        this.stripColor = stripColor;
        return this;
    }

    /**
     * Sets the location where the sign should be placed. Can be null for default. See {@link SignVersionWrapper#getLocation(org.bukkit.entity.Player, int)}
     * The sign will only be visible for the player. It won't be placed on the server or be visible for other players.
     * Warning: Placing the sign out of the chunks visible for the player will cause problems to occur. Preferably place it in the same chunk as the player.
     *
     * @param signLoc The new location.
     * @return The {@link SignGUI} instance
     */
    public SignGUI signLocation(Location signLoc) {
        this.signLoc = signLoc;
        return this;
    }


    /**
     * Sets the {@link java.util.function.Function} which will be executed when the editing is finished. If new lines are returned, the new lines are opened to edit.
     * Will override {@link SignGUI#onFinish(java.util.function.BiFunction)}
     * <p>
     * Please note that due to packet listening the function will be executed asynchronously.
     * If you want to execute synchronous actions such as inventory handling or block placing, you have to do that in a Bukkit task.
     *
     * @param function The function.
     * @return The {@link SignGUI} instance
     */
    public SignGUI onFinish(Function<String[], String[]> function) {
        Validate.notNull(function, "The function cannot be null.");
        this.function = (player, lines) -> function.apply(lines);
        return this;
    }

    /**
     * Sets the {@link java.util.function.BiFunction} which will be executed when the editing is finished. If new lines are returned, the new lines are opened to edit.
     * Will override {@link SignGUI#onFinish(java.util.function.Function)}
     * <p>
     * Please note that due to packet listening the function will be executed asynchronously.
     * If you want to execute synchronous actions such as inventory handling or block placing, you have to do that in a Bukkit task.
     *
     * @param function The function.
     * @return The {@link SignGUI} instance
     */
    public SignGUI onFinish(BiFunction<Player, String[], String[]> function) {
        Validate.notNull(function, "The function cannot be null.");
        this.function = function;
        return this;
    }

    /**
     * Opens the sign gui for the player.
     *
     * @param player The player.
     * @return The {@link SignGUI} instance
     */
    public SignGUI open(Player player) {
        Validate.notNull(player, "The player cannot be null");
        Validate.notNull(type, "Type cannot be null");
        Validate.isTrue(signTypes.contains(type), type + " is not a sign type. Available sign types: " + availableSignTypes);
        Validate.notNull(color, "The color cannot be null");
        Validate.notNull(function, "The function cannot be null.");
        try {
            WRAPPER.openSignEditor(player, lines, type, color, signLoc,
                    stripColor ? (p, lines) -> function.apply(p, Arrays.stream(lines).map(ChatColor::stripColor).toArray(String[]::new)) : function);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

}
