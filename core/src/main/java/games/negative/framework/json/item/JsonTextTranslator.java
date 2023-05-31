package games.negative.framework.json.item;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Deprecated
public enum JsonTextTranslator {

    BLACK("&0"),
    DARK_BLUE("&1"),
    DARK_GREEN("&2"),
    DARK_AQUA("&3"),
    DARK_RED("&4"),
    DARK_PURPLE("&5"),
    GOLD("&6"),
    GRAY("&7"),
    DARK_GRAY("&8"),
    BLUE("&9"),
    GREEN("&a"),
    AQUA("&b"),
    RED("&c"),
    LIGHT_PURPLE("&d"),
    YELLOW("&e"),
    WHITE("&f"),
    MAGIC("&k"),
    BOLD("&l"),
    STRIKETHROUGH("&m"),
    UNDERLINE("&n"),
    ITALIC("&o"),
    RESET("&r");

    private final String code;

    public static String translate(String text) {
        for (JsonTextTranslator value : values()) {
            String name = value.toString();
            String code = value.code;
            text = text.replaceAll("%" + name.toLowerCase() + "%", code);
        }
        return text;
    }

    public static List<String> translate(List<String> text) {
        text.replaceAll(JsonTextTranslator::translate);
        return text;
    }


}
