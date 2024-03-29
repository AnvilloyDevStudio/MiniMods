package minicraft.item;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.screen.BookData;
import minicraft.screen.BookDisplay;

import java.util.ArrayList;

public class BookItem extends Item {

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new BookItem("Book", new Sprite(0, 8, 0), null));
		items.add(new BookItem("Antidious", new Sprite(1, 8, 0), BookData.antVenomBook, true));
		return items;
	}

	protected String book; // TODO this is not saved yet; it could be, for editable books.
	private final boolean hasTitlePage;
	private Sprite sprite;

	public BookItem(String title, Sprite sprite, String book) { this(title, sprite, book, false); }
	public BookItem(String title, Sprite sprite, String book, boolean hasTitlePage) {
		super(title, sprite);
		this.book = book;
		this.hasTitlePage = hasTitlePage;
		this.sprite = sprite;
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		Game.setMenu(new BookDisplay(book, hasTitlePage));
		return true;
	}

	@Override
	public boolean interactsWithWorld() { return false; }

	public BookItem clone() {
		return new BookItem(getName(), sprite, book, hasTitlePage);
	}
}
