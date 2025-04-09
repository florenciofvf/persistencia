package br.com.persist.assistencia;

import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextPane;

import br.com.persist.arquivo.Arquivo;

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

	public static String getString(File file) throws IOException {
		if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
			return "";
		}
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			StringBuilder sb = new StringBuilder();
			String linha = br.readLine();
			while (linha != null) {
				sb.append(linha + Constantes.QL);
				linha = br.readLine();
			}
			return sb.toString();
		}
	}

	public static void salvar(JTextPane textPane, File file) throws IOException {
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			pw.print(textPane.getText());
		}
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

	public static void ordenar(List<Arquivo> arquivos) {
		for (Arquivo item : arquivos) {
			item.setTag(extrairNumero(item.getName()));
		}
		if (porTag(arquivos)) {
			Collections.sort(arquivos, (a1, a2) -> a1.getTag() - a2.getTag());
		} else {
			Collections.sort(arquivos, (a1, a2) -> a1.getName().compareTo(a2.getName()));
		}
	}

	private static boolean porTag(List<Arquivo> arquivos) {
		for (Arquivo item : arquivos) {
			if (item.getTag() == -1) {
				return false;
			}
		}
		return true;
	}

	public static File[] ordenar(File[] files) {
		if (files == null) {
			return files;
		}
		List<Arquivo> arquivos = new ArrayList<>();
		for (File f : files) {
			arquivos.add(new Arquivo(f, Collections.emptyList()));
		}
		ordenar(arquivos);
		File[] resp = new File[arquivos.size()];
		for (int i = 0; i < arquivos.size(); i++) {
			resp[i] = arquivos.get(i).getFile();
		}
		return resp;
	}

	private static int extrairNumero(String s) {
		if (s == null) {
			return -1;
		}
		if (Constantes.IGNORADOS.equals(s)) {
			return 0;
		}
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
		return -1;
	}

	public static void diretorio(File file) throws IOException {
		if (file == null) {
			return;
		}
		if (Util.isMac()) {
			Runtime.getRuntime().exec("open -R " + file.getAbsolutePath());
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(parent);
			}
		}
	}

	public static File novoDiretorio(Component c, File file) {
		File f = getFile(c, getParent(file), Constantes.VAZIO);
		if (f == null) {
			return null;
		}
		try {
			return f.mkdirs() ? f : null;
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ArquivoUtil.novoDiretorio()", ex, c);
			return null;
		}
	}

	public static File novoArquivo(Component c, File file) {
		File f = getFile(c, getParent(file), Constantes.VAZIO);
		if (f == null) {
			return null;
		}
		try {
			return f.createNewFile() ? f : null;
		} catch (IOException ex) {
			Util.stackTraceAndMessage("ArquivoUtil.novoArquivo()", ex, c);
			return null;
		}
	}

	private static File getParent(File file) {
		File f = file;
		while (f != null) {
			if (f.isDirectory()) {
				return f;
			}
			f = f.getParentFile();
		}
		return f;
	}

	private static File getFile(Component c, File parent, String padrao) {
		if (c == null || parent == null) {
			return null;
		}
		String nome = getNome(c, padrao);
		if (nome == null) {
			return null;
		}
		File f = new File(parent, nome);
		if (f.exists()) {
			return null;
		}
		return f;
	}

	public static String getNome(Component c, String padrao) {
		Object resp = Util.getValorInputDialog(c, "label.id", Mensagens.getString("label.nome_arquivo"), padrao);
		if (resp == null || Util.isEmpty(resp.toString())) {
			return null;
		}
		return resp.toString();
	}

	public static List<String> getIgnorados(File arquivo) {
		return lerArquivo(arquivo);
	}
}