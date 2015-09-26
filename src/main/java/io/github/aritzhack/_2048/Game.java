package io.github.aritzhack._2048;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.gameEngine.test.AbstractGame;
import io.github.aritzhack.aritzh.awt.gameEngine.test.TestEngine;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;
import io.github.aritzhack.aritzh.math.Vec2i;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author Aritz Lopez
 */
public class Game extends AbstractGame {

	private static final Random RAND = new Random();
	private static final int SPRITE_SIZE = 64;
	private static final int PADDING = 10;
	private static final int BORDER = 2;
	private final boolean messageOnGameOver;
	private final boolean animated;
	private final Map<Vec2i, Animation> animations = Maps.newHashMap();

	private boolean gameOver = false;
	private int points = 0;
	private TestEngine engine;

	private Mat4i matrix;

	private static final Map<Integer, Sprite> sprites = Maps.newHashMap();
	private static final Sprite BG_SPRITE = new Sprite(SPRITE_SIZE+BORDER*2, SPRITE_SIZE+BORDER*2, 0xFF000000);

	static {
		for (int[] i : new int[][]{{0, 0xFF444444}, {2, 0xFF110000}, {4, 0xFF330000}, {8, 0xFF550000}, {16, 0xFF770000}, {32, 0xFF990000}, {64, 0xFFBB0000}, {128, 0xFFDD0000}, {256, 0xFFFF0000}, {512, 0xFFFF2200}, {1024, 0xFFFF4400}, {2048, 0xFFFF6600}}) {
			addSprite(i);
		}
	}

	private static void addSprite(int[] i) {
		sprites.put(i[0], new Sprite(SPRITE_SIZE, SPRITE_SIZE, i[1]));
	}

	public static void main(String[] args) {
		new Game(true, true);
	}

	public Game(boolean messageOnGameOver, boolean animated) {
		this.messageOnGameOver = messageOnGameOver;
		this.animated = animated;
		engine = new TestEngine(this, getCoord(4), getCoord(4));
		resetMatrix();
	}

	public void resetMatrix() {
		matrix = Mat4i.empty();
		matrix.set(RAND.nextInt(4), RAND.nextInt(4), RAND.nextInt(100) < 75 ? 2 : 4);
		gameOver = false;
		points = 0;
	}

	@Override
	public void onRender() {
		super.onRender();
		IRender r = this.engine.getRender();
		r.draw(0, 0, new Sprite(800, 600, 0xFFff9999));

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if(animations.containsKey(new Vec2i(x, y))) {
					r.draw(getCoord(x) - BORDER, getCoord(y) - BORDER, BG_SPRITE);
					r.draw(getCoord(x), getCoord(y), sprites.get(0));
					continue;
				}
				r.draw(getCoord(x) - BORDER, getCoord(y) - BORDER, BG_SPRITE);
				r.draw(getCoord(x), getCoord(y), getSprite(y, x));
			}
		}

		Set<Vec2i> done = Sets.newHashSet();
		for(Map.Entry<Vec2i, Animation> a : animations.entrySet()) {
			if(a.getValue().isDone()) done.add(a.getKey());
			else {
				final Vec2i pos = a.getValue().getPosition();
				final int v = a.getValue().getValue();
				r.draw(pos.x - BORDER, pos.y - BORDER, BG_SPRITE);
				r.draw(pos.x, pos.y, sprites.containsKey(v) ? sprites.get(v) : sprites.get(0));
			}
		}
		for (Vec2i d : done) animations.remove(d);
	}

	@Override
	public void onPostRender() {
		super.onPostRender();
		Graphics g = this.engine.getEngine().getGraphics();
		Font font = new Font("Arial", Font.PLAIN, 20);
		g.setFont(font);
		g.setColor(Color.WHITE);

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if(animations.containsKey(new Vec2i(x, y))) continue;
				int v = matrix.get(x, y);
				if (v != 0) {
					drawCenteredString(String.valueOf(v), g, getCoord(x) + SPRITE_SIZE / 2, getCoord(y) + SPRITE_SIZE / 2);
				}
			}
		}

		for(Map.Entry<Vec2i, Animation> a : animations.entrySet()) {
			final Vec2i pos = a.getValue().getPosition();
			final int v = a.getValue().getValue();
			drawCenteredString(String.valueOf(v), g, pos.x + SPRITE_SIZE / 2,pos.y + SPRITE_SIZE / 2);
		}
	}

	private void drawCenteredString(String s, Graphics g, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s, (int) (x - fm.getStringBounds(s, g).getWidth() / 2), y + fm.getAscent() - (fm.getAscent() + fm.getDescent()) / 2);
	}

	private Sprite getSprite(int y, int x) {
		int v = matrix.get(x, y);
		return sprites.containsKey(v) ? sprites.get(v) : sprites.get(0);
	}

	private int getCoord(int c) {
		return c * (SPRITE_SIZE + PADDING) + PADDING;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		InputHandler ih = this.engine.getEngine().getInputHandler();

		if (ih.wasKeyTyped(KeyEvent.VK_R)) {
			resetMatrix();
		}

		if(animations.size() > 0) {
			for (Animation a : animations.values()) {
				a.update();
			}
		}

		if(gameOver) return;

		if (ih.wasKeyTyped(KeyEvent.VK_LEFT) || ih.wasKeyTyped(KeyEvent.VK_A)) {
			move(Dir.LEFT);
		} else if (ih.wasKeyTyped(KeyEvent.VK_RIGHT) || ih.wasKeyTyped(KeyEvent.VK_D)) {
			move(Dir.RIGHT);
		} else if (ih.wasKeyTyped(KeyEvent.VK_DOWN) || ih.wasKeyTyped(KeyEvent.VK_S)) {
			move(Dir.DOWN);
		} else if (ih.wasKeyTyped(KeyEvent.VK_UP) || ih.wasKeyTyped(KeyEvent.VK_W)) {
			move(Dir.UP);
		}
	}

	public boolean move(Dir dir) {
		boolean changed = true;
		boolean someChanged = false;

		Set<Vec2i> fused = Sets.newHashSet();
		while (changed) {
			changed = false;
			for (int x = dir.fx; 0 <= x && x < 4; x += dir.dx) {
				for (int y = dir.fy; 0 <= y && y < 4; y += dir.dy) {
					if (matrix.get(x, y) != 0 && matrix.get(x + dir.x, y + dir.y) == 0) {
						if(animated) animations.put(new Vec2i(x + dir.x, y + dir.y), new Animation(getCoord(x), getCoord(y), getCoord(x + dir.x), getCoord(y + dir.y), matrix.get(x, y)));
						matrix.set(x + dir.x, y + dir.y, matrix.get(x, y));
						matrix.set(x, y, 0);
						changed = true;
					} else if (!fused.contains(new Vec2i(x, y)) && !fused.contains(new Vec2i(x + dir.x, y + dir.y)) && matrix.get(x, y) != 0 && matrix.get(x + dir.x, y + dir.y) == matrix.get(x, y)) {
						if(animated) animations.put(new Vec2i(x + dir.x, y + dir.y), new Animation(getCoord(x), getCoord(y), getCoord(x + dir.x), getCoord(y + dir.y), matrix.get(x, y)));
						matrix.set(x + dir.x, y + dir.y, matrix.get(x, y) * 2);
						points += matrix.get(x + dir.x, y + dir.y);
						matrix.set(x, y, 0);
						changed = true;
						fused.add(new Vec2i(x+dir.x, y+dir.y));
					}
				}
			}
			someChanged |= changed;
		}
		List<Vec2i> free = Lists.newArrayList();
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				if (matrix.get(x, y) == 0) free.add(new Vec2i(x, y));
			}
		}
		if (free.size() == 0) {
			gameOver = true;
			if(messageOnGameOver) {
				JOptionPane.showMessageDialog(null, "GAME OVER! YOU GOT " + points + " POINTS!", "GAME OVER!", JOptionPane.ERROR_MESSAGE);
				resetMatrix();
			}
		} else if (someChanged) {
			Vec2i v = free.get(RAND.nextInt(free.size()));
			matrix.set(v.x, v.y, RAND.nextInt(100) < 75 ? 2 : 4);
		}
		return someChanged;
	}

	public void destroy() {
		this.engine.getEngine().stop();
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public int getPoints() {
		return points;
	}

	@Override
	public String getGameName() {
		return "2048";
	}

	public enum Dir {
		LEFT(-1, 0, 1, 0, 1, 1), RIGHT(1, 0, 2, 0, -1, 1), DOWN(0, 1, 0, 2, 1, -1), UP(0, -1, 0, 1, 1, 1);
		public final int x, y, fx, fy, dx, dy;

		Dir(int x, int y, int fx, int fy, int dx, int dy) {
			this.x = x; this.y = y; this.fx = fx; this.fy = fy; this.dx = dx; this.dy = dy;
		}
	}

	private static class Mat4i {

		private int[] values = new int[16];

		private Mat4i() {
		}

		public static Mat4i empty() {
			return new Mat4i();
		}

		public int get(int x, int y) {
			return values[x + y * 4];
		}

		public void set(int x, int y, int value) {
			values[x + y * 4] = value;
		}
	}
}
