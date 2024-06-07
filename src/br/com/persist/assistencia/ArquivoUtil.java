package br.com.persist.assistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
					if (!Util.isEmpty(linha)) {
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

	public static String primeiroIniciadoCom(String string, File file) {
		if (string != null && file != null && file.isFile() && file.canRead()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					if (!Util.isEmpty(linha) && linha.startsWith(string)) {
						return linha;
					}
					linha = br.readLine();
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "primeiroIniciadoCom(file)");
			}
		}
		return null;
	}

	public static boolean contem(String chave, String string) {
		if (chave == null || string == null) {
			return false;
		}
		List<String> lista = map.get(chave);
		return lista != null && lista.contains(string);
	}

	public static void copiar(File origem, File destino, long indice, long quantidade) throws IOException {
		try (FileInputStream fis = new FileInputStream(origem)) {
			try (FileOutputStream fos = new FileOutputStream(destino)) {
				FileChannel ci = fis.getChannel();
				FileChannel co = fos.getChannel();
				ci.transferTo(indice, quantidade, co);
			}
		}
	}

	public static File[] ordenar(File[] files) {
		if (files == null) {
			return files;
		}
		List<Ordem> lista = new ArrayList<>();
		for (File f : files) {
			lista.add(new Ordem(f));
		}
		Collections.sort(lista, (a1, a2) -> a1.numero - a2.numero);
		File[] resp = new File[lista.size()];
		for (int i = 0; i < lista.size(); i++) {
			resp[i] = lista.get(i).file;
		}
		return resp;
	}

	static class Ordem {
		final File file;
		final int numero;

		public Ordem(File file) {
			this.file = file;
			numero = extrair(file.getName());
		}

		int extrair(String s) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (i < s.length()) {
				char c = s.charAt(i);
				if (c >= '0' && c <= '9') {
					sb.append(c);
				} else {
					break;
				}
				i++;
			}
			if (sb.length() > 0) {
				return Integer.parseInt(sb.toString());
			}
			return 0;
		}
	}
}