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

	public static List<Bloco> limitar(List<Bloco> blocos, Bloco anterior) {
		while (blocos.size() > 10) {
			blocos.remove(blocos.size() - 1);
		}
		for (Bloco item : blocos) {
			item.setAnterior(anterior);
			anterior = item;
		}
		return blocos;
	}

	public static int largura(Ponto ponto1, Ponto ponto2) {
		return ponto2.x - ponto1.x;
	}

	public static int altura(Ponto ponto1, Ponto ponto2) {
		return ponto2.y - ponto1.y;
	}
}