package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.level.Level;

public class FloorTile extends Tile {
	protected Material type;
	private Sprite sprite;

	public FloorTile(Material type) {
		super((type == Material.Wood ? "Wood Planks" : type == Material.Obsidian ? "Obsidian" : type.name + " Bricks"), (Sprite) null);
		this.type = type;
		maySpawn = true;
		if (type == Material.Wood) {
			sprite = new Sprite(5, 14, 2, 2, 1, 0);
		} else if (type == Material.Stone) {
			sprite = new Sprite(15, 14, 2, 2, 1, 0);
		} else if (type == Material.Obsidian) {
			sprite = new Sprite(25, 14, 2, 2, 1, 0);
		}
		super.sprite = sprite;
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == type.getRequiredTool()) {
				if (player.payStamina(4 - ToolItem.LEVELS.get(tool.level)) && tool.payDurability()) {
					if (level.depth == 1) {
						level.setTile(xt, yt, Tiles.get("Cloud"));
					} else {
						level.setTile(xt, yt, Tiles.get("Hole"));
					}
					Item drop;
					if (type == Material.Wood) {
						drop = Items.get("Plank");
					} else {
						drop = Items.get(type.name + " Brick");
					}
					Sound.monsterHurt.play();
					level.dropItem(xt * 16 + 8, yt * 16 + 8, drop);
					return true;
				}
			}
		}
		return false;
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return true;
	}
}
