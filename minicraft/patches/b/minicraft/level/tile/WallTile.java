package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.level.Level;

public class WallTile extends Tile {

	private static final String obrickMsg = "The airwizard must be defeated first.";
	protected Material type;
	private ConnectorSprite sprite;

	public WallTile(Material type) {
		super(type.name + " Wall", (ConnectorSprite) null);
		this.type = type;
		if (type == Material.Wood) {
			sprite = new ConnectorSprite(WallTile.class, new Sprite(0, 14, 3, 3, 1, 3), new Sprite(3, 14, 2, 2, 1, 3), new Sprite(1, 15, 2, 2, 1, 0, true));
		} else if (type == Material.Stone) {
			sprite = new ConnectorSprite(WallTile.class, new Sprite(10, 14, 3, 3, 1, 3), new Sprite(13, 14, 2, 2, 1, 3), new Sprite(11, 15, 2, 2, 1, 0, true));
		} else if (type == Material.Obsidian) {
			sprite = new ConnectorSprite(WallTile.class, new Sprite(20, 14, 3, 3, 1, 3), new Sprite(23, 14, 2, 2, 1, 3), new Sprite(21, 15, 2, 2, 1, 0, true));
		}
		csprite = sprite;
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		if (Game.isMode("Creative") || level.depth != -3 || type != Material.Obsidian || AirWizard.beaten) {
			hurt(level, x, y, random.nextInt(6) / 6 * dmg / 2);
			return true;
		} else {
			Game.notifications.add(obrickMsg);
			return false;
		}
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (Game.isMode("Creative"))
			return false; // Go directly to hurt method
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == type.getRequiredTool()) {
				if (level.depth != -3 || type != Material.Obsidian || AirWizard.beaten) {
					if (player.payStamina(4 - ToolItem.LEVELS.get(tool.level)) && tool.payDurability()) {
						hurt(level, xt, yt, random.nextInt(10) + (ToolItem.LEVELS.get(tool.level)) * 5 + 10);
						return true;
					}
				} else {
					Game.notifications.add(obrickMsg);
				}
			}
		}
		return false;
	}

	public void hurt(Level level, int x, int y, int dmg) {
		int damage = level.getData(x, y) + dmg;
		int sbwHealth = 100;
		if (Game.isMode("Creative")) dmg = damage = sbwHealth;

		level.add(new SmashParticle(x * 16, y * 16));
		Sound.monsterHurt.play();

		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
		if (damage >= sbwHealth) {
			String itemName = "", tilename = "";
			// Get what tile to set and what item to drop
			if (type == Material.Wood) {
				itemName = "Plank";
				tilename = "Wood Planks";
			} else if (type == Material.Stone) {
				itemName = "Stone Brick";
				tilename = "Stone Bricks";
			} else if (type == Material.Obsidian) {
				itemName = "Obsidian Brick";
				tilename = "Obsidian";
			}

			level.dropItem(x * 16 + 8, y * 16 + 8, 1, id < 3 ? 3 - type.id : 2, Items.get(itemName));
			level.setTile(x, y, Tiles.get(tilename));
		} else {
			level.setData(x, y, damage);
		}
	}

	public boolean tick(Level level, int xt, int yt) {
		int damage = level.getData(xt, yt);
		if (damage > 0) {
			level.setData(xt, yt, damage - 1);
			return true;
		}
		return false;
	}

	public String getName(int data) {
		return Material.getRegistries().stream().filter(v -> v.id == data).findAny().orElse(Material.Wood).name + " Wall";
	}
}
