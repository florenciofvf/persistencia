package br.com.persist.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	private Imagens() {
	}

	public static Icon getIcon(String nome) {
		return MAPA_ICONES.computeIfAbsent(nome, n -> criarImagem(n));
	}

	public static List<Map.Entry<String, Icon>> getIcones() {
		List<Map.Entry<String, Icon>> lista = new ArrayList<>(MAPA_ICONES.entrySet());
		Collections.sort(lista, new Comparador());
		return lista;
	}

	public static void ini() {
		File[] files = file.listFiles();
		MAPA_ICONES.clear();

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

	private static class Comparador implements Comparator<Map.Entry<String, Icon>> {
		@Override
		public int compare(Entry<String, Icon> o1, Entry<String, Icon> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	}
}