package br.com.persist.arquivo;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;

public class ArquivoUtil {
	private ArquivoUtil() {
	}

	public static List<String> getIgnorados(File arquivo) {
		return br.com.persist.assistencia.ArquivoUtil.lerArquivo(arquivo);
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
}