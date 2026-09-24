package br.com.persist.assistencia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

public class Imagens {
	private static final Map<String, Icone> MAPA_ICONES = new HashMap<>();
	private static final File file = new File(Constantes.IMAGENS);

	private Imagens() {
	}

	private static Icone criarIcone(String nome) throws AssistenciaException {
		try {
			return new Icone(new ImageIcon(file.getAbsolutePath() + File.separator + nome + ".png"));
		} catch (Exception e) {
			throw new AssistenciaException("Erro imagem! " + nome);
		}
	}

	public static Icone getIcone(String nome) throws AssistenciaException {
		Icone icone = criarIcone(nome);
		return MAPA_ICONES.computeIfAbsent(nome, t -> icone);
	}

	public static List<Map.Entry<String, Icone>> getIcones() {
		List<Map.Entry<String, Icone>> lista = new ArrayList<>(MAPA_ICONES.entrySet());
		Collections.sort(lista, new Comparador());
		return lista;
	}

	public static void ini() throws AssistenciaException {
		File[] files = file.listFiles();
		MAPA_ICONES.clear();
		if (files != null) {
			for (File f : files) {
				String s = f.getName();
				if (s.endsWith(".png")) {
					s = s.substring(0, s.length() - 4);
					getIcone(s);
				}
			}
		}
	}

	private static class Comparador implements Comparator<Map.Entry<String, Icone>> {
		@Override
		public int compare(Entry<String, Icone> o1, Entry<String, Icone> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	}
}