package br.com.persist.arquivo;

import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;

public class ArquivoUtil {
	private static final Logger LOG = Logger.getGlobal();

	private ArquivoUtil() {
	}

	public static List<String> getIgnorados(File arquivo) {
		List<String> lista = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
			String linha = br.readLine();
			while (linha != null) {
				if (!Util.estaVazio(linha)) {
					lista.add(linha);
				}
				linha = br.readLine();
			}
		} catch (Exception e) {
			LOG.log(Level.FINEST, "ArquivoUtil.getIgnorados()");
		}
		return lista;
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

	public static boolean novoDiretorio(Component c, File parent) {
		File f = getFile(c, parent, Constantes.VAZIO);
		if (f == null) {
			return false;
		}
		try {
			return f.mkdirs();
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ArquivoUtil.novoDiretorio()", ex, c);
			return false;
		}
	}

	public static boolean novoArquivo(Component c, File parent) {
		File f = getFile(c, parent, Constantes.VAZIO);
		if (f == null) {
			return false;
		}
		try {
			return f.createNewFile();
		} catch (IOException ex) {
			Util.stackTraceAndMessage("ArquivoUtil.novoArquivo()", ex, c);
			return false;
		}
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
		if (resp == null || Util.estaVazio(resp.toString())) {
			return null;
		}
		return resp.toString();
	}
}