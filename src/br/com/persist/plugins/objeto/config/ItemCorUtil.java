package br.com.persist.plugins.objeto.config;

import java.awt.Color;

public class ItemCorUtil {
	private ItemCor[] itens;

	public ItemCorUtil() {
		init();
	}

	private void init() {
		int[] rawValues = initRawValues();
		int[] rawMatrix = initRawMatrix(rawValues);
		int numColors = rawValues.length / 3;
		itens = new ItemCor[numColors];
		for (int i = 0; i < numColors; i++) {
			Color color = new Color(rawValues[(i * 3)], rawValues[(i * 3) + 1], rawValues[(i * 3) + 2]);
			itens[i] = new ItemCor(rawMatrix[(i * 3)], rawMatrix[(i * 3) + 1], color);
		}
	}

	public ItemCor getItem(Color color) {
		if (itens != null) {
			for (ItemCor item : itens) {
				if (item.color.equals(color)) {
					return item;
				}
			}
		}
		return null;
	}

	private int[] initRawValues() {
		return new int[] { 255, 255, 255, 204, 255, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255,
				204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 255, 204, 255, 255, 204, 204,
				255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204,
				255, 204, 204, 255, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204,
				204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 204, 204, 153, 255, 255, 153, 204, 255,
				153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255,
				204, 153, 255, 255, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153,
				255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 204, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153,
				153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 204,
				204, 204, 204, 102, 255, 255, 102, 204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255,
				102, 102, 255, 102, 102, 255, 153, 102, 255, 204, 102, 255, 255, 102, 255, 255, 102, 204, 255, 102, 153,
				255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 153, 102, 255, 204, 102,
				255, 255, 102, 204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102,
				102, 255, 102, 102, 255, 153, 102, 255, 204, 153, 153, 153, 51, 255, 255, 51, 204, 255, 51, 153, 255,
				51, 102, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 51, 255, 255, 51,
				255, 255, 51, 204, 255, 51, 153, 255, 51, 102, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255,
				153, 51, 255, 204, 51, 255, 255, 51, 204, 255, 51, 153, 255, 51, 102, 255, 51, 51, 255, 51, 51, 255, 51,
				51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 153, 153, 153, 0, 255, 255, 0, 204, 255, 0, 153,
				255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255, 255, 0, 255,
				255, 0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255,
				204, 0, 255, 255, 0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 0, 0, 255, 51, 0, 255,
				102, 0, 255, 153, 0, 255, 204, 102, 102, 102, 0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51,
				204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204, 0, 204, 204, 0, 204, 204, 0, 204, 204, 0,
				153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204, 0, 204, 204, 0,
				204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0,
				204, 204, 102, 102, 102, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0, 0, 153, 51,
				0, 153, 102, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153,
				0, 51, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153,
				0, 102, 153, 0, 51, 153, 0, 0, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0, 153, 153, 51, 51, 51, 0,
				102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102, 102, 0,
				102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 51, 0,
				102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0,
				0, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 0, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51,
				0, 51, 51, 0, 51, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0,
				51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0,
				51, 51, 0, 51, 51, 0, 0, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 51, 51, 51 };
	}

	private int[] initRawMatrix(int[] rawValues) {
		int[] rawMatrix = new int[rawValues.length];
		int indice = 0;
		for (int linha = 0; linha < 9; linha++) {
			for (int coluna = 0; coluna < 31; coluna++) {
				rawMatrix[indice] = linha;
				indice++;
				rawMatrix[indice] = coluna;
				indice++;
				rawMatrix[indice] = 0;
				indice++;
			}
		}
		return rawMatrix;
	}
}