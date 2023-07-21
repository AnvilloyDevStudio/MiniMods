package minicraft.item;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class FishingRodItem extends Item {

    protected static ArrayList<Item> getAllInstances() {
        ArrayList<Item> items = new ArrayList<>();

        items.add(new FishingRodItem("Wood"));
        items.add(new FishingRodItem("Iron"));
        items.add(new FishingRodItem("Gold"));
        items.add(new FishingRodItem("Gem"));

        return items;
    }
    private int uses = 0; // The more uses, the higher the chance of breaking
    public String level; // The higher the level the lower the chance of breaking

    private Random random = new Random();

    /* These numbers are a bit confusing, so here's an explanation
    * If you want to know the percent chance of a category (let's say tool, which is third)
    * You have to subtract 1 + the "tool" number from the number before it (for the first number subtract from 100)*/
    private static final int[][] LEVEL_CHANCES = {
            {44, 14, 9, 4}, // They're in the order "fish", "junk", "tools", "rare"
            {24, 14, 9, 4}, // Iron has very high chance of fish
            {59, 49, 9, 4}, // Gold has very high chance of tools
            {79, 69, 59, 4} // Gem has very high chance of rare items
    };

    public static final HashMap<String, Integer> LEVELS = new HashMap<String, Integer>() {{
        put("Wood", 0);
        put("Iron", 1);
        put("Gold", 2);
        put("Gem", 3);
    }};

    public FishingRodItem(String level) { this(level, new Sprite(LEVELS.get(level), 11, 0)); }
    public FishingRodItem(String level, Sprite sprite) {
        super(level + " Fishing Rod", sprite);
        this.level = level;
    }

    public static int getChance(int idx, int level) {
        return LEVEL_CHANCES[level][idx];
    }

    @Override
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
        if (tile == Tiles.get("water") && !player.isSwimming()) { // Make sure not to use it if swimming
            uses++;
            player.isFishing = true;
            player.fishingLevel = LEVELS.get(this.level);
            return true;
        }

        return false;
    }

    @Override
    public boolean canAttack() { return false; }

    @Override
    public boolean isDepleted() {
        if (random.nextInt(100) > 120 - uses + LEVELS.get(level) * 6) { // Breaking is random, the lower the level, and the more times you use it, the higher the chance
            Game.notifications.add("Your Fishing rod broke.");
            return true;
        }
        return false;
    }

    @Override
    public Item clone() {
        FishingRodItem item = new FishingRodItem(this.level);
        item.uses = this.uses;
        return item;
    }
}
