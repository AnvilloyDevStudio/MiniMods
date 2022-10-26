package minimods.mod.core;

import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.spongepowered.asm.mixin.injection.Inject;

import io.gfx.GraphicComp;
import io.gfx.GraphicComp.ModSprite;
import io.github.anvilloystudio.minimods.core.Mods;
import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.gfx.SpriteSheet;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.Recipe;
import minicraft.item.Recipes;
import minicraft.item.StackableItem;
import minicraft.item.TileItem;
import minicraft.item.ToolItem;
import minicraft.level.Level;
import minicraft.level.LevelGen;
import minicraft.level.tile.OreTile;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.level.tile.TreeTile;
import minicraft.level.tile.OreTile.OreType;
import minicraft.screen.Display;
import minimods.mod.core.mixins.PausedDisplayMixin;
import minimods.mod.core.mixins.WorldMixin;

public class Main {
	public static void main(String[] args) {
		System.out.println("THIS JAR IS NOT PURPOSED TO BE EXECUTED DIRECTLY.");
	}

    public static void entry() {
    }
}
