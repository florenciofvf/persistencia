package br.com.persist.assistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArquivoUtil {
	private static final Map<String, List<String>> map = new HashMap<>();
	private static final Logger LOG = Logger.getGlobal();

	private ArquivoUtil() {
	}

	public static void reiniciar(String chave) {
		map.put(chave, null);
	}

	public static void lerArquivo(String chave, File file) {
		if (chave == null || file == null) {
			return;
		}
		map.put(chave, lerArquivo(file));
	}

	public static List<String> lerArquivo(File file) {
		List<String> lista = new ArrayList<>();
		if (file != null && file.isFile() && file.canRead()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					if (!Util.estaVazio(linha)) {
						lista.add(linha);
					}
					linha = br.readLine();
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "lerArquivo(file)");
			}
		}
		return lista;
	}

	public static boolean contem(String chave, String string) {
		if (chave == null || string == null) {
			return false;
		}
		List<String> lista = map.get(chave);
		return lista != null && lista.contains(string);
	}
}