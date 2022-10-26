package io.github.anvilloystudio.minimods.mod.core.ores.tiles;

import java.io.IOException;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.ModTileGen;
import io.github.anvilloystudio.minimods.api.OreTypeMixinEnumUtil;
import io.github.anvilloystudio.minimods.api.mixins.OreTileMixin;
import io.github.anvilloystudio.minimods.api.mixins.TilesMixin;
import minicraft.item.Items;
import minicraft.level.tile.OreTile;
import minicraft.level.tile.Tiles;
import minicraft.level.tile.OreTile.OreType;

public class OreTiles {
	public static void init() {
		// Copper Ore
		OreTypeMixinEnumUtil.addVariant(new OreTypeMixinEnumUtil.OreTypeMixinEnumData("Copper", () -> Items.get("Copper Ore"), 0));
		new ModTileGen(-1, (map, data, layer, w, h, random) -> {
			int r = 2;
			for (int i = 0; i < w * h / 300; i++) { // How many times attemping to generate.
				int x = random.nextInt(w); // Random select a location.
				int y = random.nextInt(h);
				for (int j = 0; j < 40; j++) { // How large for each ore vein.
					int xx = x + random.nextInt(7) - random.nextInt(7); // Random select a tile for the vein.
					int yy = y + random.nextInt(7) - random.nextInt(7);
					if (xx >= r && yy >= r && xx < w - r && yy < h - r) { // If the location of the tile is valid.
						if (map[xx + yy * w] == Tiles.get("rock").id) {
							map[xx + yy * w] = (byte) ((Tiles.get("Copper Ore").id & 0xff));
						} else if (map[xx + yy * w] == Tiles.get("Iron Ore").id && random.nextInt(5) == 0) {
							map[xx + yy * w] = (byte) ((Tiles.get("Copper Ore").id & 0xff));
						}
					}
				}
			}
		});
		// Tin Ore
		OreTypeMixinEnumUtil.addVariant(new OreTypeMixinEnumUtil.OreTypeMixinEnumData("Tin", () -> Items.get("Tin Ore"), 0));
		new ModTileGen(-2, (map, data, layer, w, h, random) -> {
			int r = 2;
			for (int i = 0; i < w * h / 100; i++) {
				int x = random.nextInt(w);
				int y = random.nextInt(h);
				for (int j = 0; j < 10; j++) {
					int xx = x + random.nextInt(3) - random.nextInt(3);
					int yy = y + random.nextInt(3) - random.nextInt(3);
					if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
						if (map[xx + yy * w] == Tiles.get("rock").id) {
							map[xx + yy * w] = (byte) ((Tiles.get("Tin Ore").id & 0xff));
						}
					}
				}
			}
		});
		// Lead Ore
		OreTypeMixinEnumUtil.addVariant(new OreTypeMixinEnumUtil.OreTypeMixinEnumData("Lead", () -> Items.get("Lead Ore"), 0));
		new ModTileGen(-3, (map, data, layer, w, h, random) -> {
			int r = 2;
			for (int i = 0; i < w * h / 100; i++) {
				int x = random.nextInt(w);
				int y = random.nextInt(h);
				for (int j = 0; j < 10; j++) {
					int xx = x + random.nextInt(3) - random.nextInt(3);
					int yy = y + random.nextInt(3) - random.nextInt(3);
					if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
						if (map[xx + yy * w] == Tiles.get("rock").id) {
							map[xx + yy * w] = (byte) ((Tiles.get("Tin Ore").id & 0xff));
						}
					}
				}
			}
		});
		// Silver Ore
		OreTypeMixinEnumUtil.addVariant(new OreTypeMixinEnumUtil.OreTypeMixinEnumData("Silver", () -> Items.get("Silver Ore"), 0));
		new ModTileGen(-2, (map, data, layer, w, h, random) -> {
			int r = 2;
			for (int i = 0; i < w * h / 50; i++) {
				int x = random.nextInt(w);
				int y = random.nextInt(h);
				for (int j = 0; j < 10; j++) {
					int xx = x + random.nextInt(3) - random.nextInt(3);
					int yy = y + random.nextInt(3) - random.nextInt(3);
					if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
						if (map[xx + yy * w] == Tiles.get("rock").id) {
							map[xx + yy * w] = (byte) ((Tiles.get("Silver Ore").id & 0xff));
						}
					}
				}
			}
		});
	}

	public static void postInit() {
		try {
			// Copper Ore
			OreTile copperTile = OreTileMixin.invokeInit(OreType.valueOf("Copper"));
			((OreTileMixin) copperTile).setSprite(GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(OreTiles.class.getResourceAsStream("/assets/textures/tiles/copper_ore.png"))));
			TilesMixin.invokeAdd(50, copperTile);
			// Tin Ore
			OreTile tinTile = OreTileMixin.invokeInit(OreType.valueOf("Tin"));
			((OreTileMixin) tinTile).setSprite(GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(OreTiles.class.getResourceAsStream("/assets/textures/tiles/tin_ore.png"))));
			TilesMixin.invokeAdd(51, tinTile);
			// Lead Ore
			OreTile leadTile = OreTileMixin.invokeInit(OreType.valueOf("Lead"));
			((OreTileMixin) leadTile).setSprite(GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(OreTiles.class.getResourceAsStream("/assets/textures/tiles/lead_ore.png"))));
			TilesMixin.invokeAdd(52, leadTile);
			// Silver Ore
			OreTile silverTile = OreTileMixin.invokeInit(OreType.valueOf("Silver"));
			((OreTileMixin) silverTile).setSprite(GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(OreTiles.class.getResourceAsStream("/assets/textures/tiles/silver_ore.png"))));
			TilesMixin.invokeAdd(53, silverTile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to initialize tile(s).", e);
		}
	}
}
