package io.github.aritzhack._2048;

import io.github.aritzhack.aritzh.math.Vec2i;

/**
 * @author Aritz Lopez
 */
public class Animation {

	private static final int speed = 15;

	private Vec2i source;
	private Vec2i dest;
	private int value;
	private float percent;

	public Animation(int x1, int y1, int x2, int y2, int value) {
		this(new Vec2i(x1, y1), new Vec2i(x2, y2), value);
	}

	public Animation(Vec2i source, Vec2i dest, int value) {
		this.source = source;
		this.dest = dest;
		this.value = value;
		this.percent = 0;
	}

	public void update() {
		percent += speed;
		if(percent >= 100) percent = 100;
	}

	public Vec2i getPosition() {
		return source.add(dest.add(source.negate()).times(percent/100));
	}

	public boolean isDone() {
		return percent >= 100;
	}

	public int getValue() {
		return value;
	}
}
