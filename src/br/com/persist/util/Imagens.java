package br.com.persist.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	public List<Icon> getIcones() {
		return new ArrayList<>(MAPA_ICONES.values());
	}

	public static void ini() {
		MAPA_ICONES.clear();
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				String s = f.getName();
				if (s.endsWith(".png")) {
					s = s.substring(0, s.length() - 4);
					getIcon(s);
				}
			}
		}
	}
}