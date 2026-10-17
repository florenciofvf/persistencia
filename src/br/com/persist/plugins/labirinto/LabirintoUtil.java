package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.util.Random;

public class LabirintoUtil {
	private static Random random = new Random();

	private LabirintoUtil() {
	}

	static Color createColor() {
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		return new Color(r, g, b);
	}
}