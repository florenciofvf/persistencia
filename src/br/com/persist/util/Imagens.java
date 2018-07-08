package br.com.persist.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Imagens {
	private static final Map<String, Icon> MAPA_ICONES = new HashMap<>();
	private static final File file = new File("imagens");

	private static ImageIcon criarImagem(String nome) {
		try {
			return new ImageIcon(file.getAbsolutePath() + File.separator + nome + ".png");
		} catch (Exception e) {
			throw new IllegalStateException("Erro imagem! " + nome);
		}
	}

	public static Icon getIcon(String nome) {
		Icon icon = MAPA_ICONES.get(nome);

		if (icon == null) {
			icon = criarImagem(nome);
			MAPA_ICONES.put(nome, icon);
		}

		return icon;
	}
}