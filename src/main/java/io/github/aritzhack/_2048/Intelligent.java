package io.github.aritzhack._2048;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Aritz Lopez
 */
public class Intelligent {

    private Game game;

    public static void main(String[] args) {
        new Intelligent();
    }

    public Intelligent() {
        game = new Game(false, false);

        //strategy1();
        strategy2();

        game.destroy();
    }

    private void strategy1() {
        Map<Integer, Integer[]> points = Maps.newHashMap();
        for(int i = 0; i<1000; i++) {
            reset();

            int moveCount = 0;
            while (!game.isGameOver()) {
                if (!game.move(Game.Dir.DOWN)) {
                    if (!game.move(Game.Dir.RIGHT)) {
                        if (!game.move(Game.Dir.LEFT)) {
                            if (!game.move(Game.Dir.RIGHT)) {
                                break;
                            }
                        } else {
                            moveCount++;
                            if (!game.move(Game.Dir.RIGHT)) {
                                break;
                            }
                        }
                    }
                }
                moveCount++;
            }
            points.put(game.getPoints(), new Integer[]{game.getPoints(), moveCount});

        }
        Integer[] best = points.get(Collections.max(points.keySet()));
        System.out.println(best[0] + " POINTS IN " + best[1] + " MOVES!");
    }

    private void strategy2() {
        Map<Integer, Integer[]> points = Maps.newHashMap();
        for(int i = 0; i<100000; i++) {
            reset();

            int moveCount = 0;
            while (!game.isGameOver()) {
                if (game.move(Game.Dir.DOWN)) {
                    moveCount++;
                }
                if (game.move(Game.Dir.LEFT)) {
                    moveCount++;
                }
                if (game.move(Game.Dir.UP)) {
                    moveCount++;
                }
                if (game.move(Game.Dir.RIGHT)) {
                    moveCount++;
                }
            }
            points.put(game.getPoints(), new Integer[]{game.getPoints(), moveCount});

        }
        Integer[] best = points.get(Collections.max(points.keySet()));
        System.out.println(best[0] + " POINTS IN " + best[1] + " MOVES!");
    }

    private void reset() {
        game.resetMatrix();
    }


}
