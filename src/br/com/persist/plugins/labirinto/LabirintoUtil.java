package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.util.List;
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

	public static List<Bloco> limitar(List<Bloco> blocos) {
		while (blocos.size() > 10) {
			blocos.remove(blocos.size() - 1);
		}
		int indice = 0;
		for (Bloco item : blocos) {
			item.indiceTemp = indice++;
		}
		return blocos;
	}

	public static Bloco primeiro(List<Bloco> blocos) {
		for (Bloco item : blocos) {
			if (!item.vazio) {
				return item;
			}
		}
		return null;
	}
}