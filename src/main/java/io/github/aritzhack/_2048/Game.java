package io.github.aritzhack._2048;

import com.google.common.collect.Maps;
import io.github.aritzhack.aritzh.awt.gameEngine.CanvasEngine;
import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.gameEngine.test.AbstractGame;
import io.github.aritzhack.aritzh.awt.gameEngine.test.TestEngine;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Random;

/**
 * @author Aritz Lopez
 */
public class Game extends AbstractGame {

    private TestEngine engine;
    private Mat4i matrix;

    private static final int SPRITE_SIZE = 64;
    private static final int PADDING = 10;

    private static final Map<Integer, Sprite> sprites = Maps.newHashMap();

    static {
        sprites.put(0, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFAAAAAA));
        sprites.put(2, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF110000));
        sprites.put(4, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF330000));
        sprites.put(8, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF550000));
        sprites.put(16, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF770000));
        sprites.put(32, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF990000));
        sprites.put(64, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFBB00));
        sprites.put(128, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFDD0000));
        sprites.put(256, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFFF0000));
        sprites.put(512, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFFF2200));
        sprites.put(1024, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFFF4400));
        sprites.put(2048, new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFFF6600));
    }

    public Game() {
        engine = new TestEngine(this, getCoord(4), getCoord(4));
        matrix = Mat4i.empty();

        resetMatrix();
    }

    private void resetMatrix() {
        for(int i = 0; i<16; i++) matrix.set(i, getRand());
    }

    @Override
    public void onRender() {
        super.onRender();

        IRender r = this.engine.getRender();

        r.draw(0, 0, new Sprite(800, 600, 0xFFff9999));

        r.draw(0, 0, new Sprite(getCoord(4), getCoord(4), 0xFF000000));

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                r.draw(getCoord(x), getCoord(y), getSprite(y, x));
            }
        }
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
                int v = matrix.get(x, y);
                if (v != 0) {
                    drawCenteredString(String.valueOf(v), g, getCoord(x) + SPRITE_SIZE / 2, getCoord(y) + SPRITE_SIZE / 2);
                }
            }
        }

    }

    private int getRand() {
        return (int) Math.pow(2, new Random().nextInt(sprites.size()));
    }

    private void drawCenteredString(String s, Graphics g, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(s, g);

        g.drawString(s, (int) (x - bounds.getWidth() / 2), y + fm.getAscent() - (fm.getAscent() + fm.getDescent()) / 2);
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

        if(ih.wasKeyTyped(KeyEvent.VK_R)) {
            resetMatrix();
        }
    }

    @Override
    public String getGameName() {
        return "2048";
    }
}
