package br.com.persist.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.JOptionPane;

import br.com.persist.comp.TextArea;

public class Util {
	private static final boolean LOG_CONSOLE = false;

	private Util() {
	}

	public static boolean estaVazio(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static void mensagem(Component componente, String string) {
		TextArea textArea = new TextArea(string);
		textArea.setPreferredSize(new Dimension(500, 300));

		JOptionPane.showMessageDialog(componente, textArea, Mensagens.getString("label.atencao"),
				JOptionPane.PLAIN_MESSAGE);
	}

	public static boolean confirmaExclusao(Component componente) {
		return JOptionPane.showConfirmDialog(componente, Mensagens.getString("msg.confirma_exclusao"),
				Mensagens.getString("label.atencao"), JOptionPane.YES_OPTION) == JOptionPane.OK_OPTION;
	}

	public static void stackTraceAndMessage(String tipo, Exception ex, Component componente) {
		String msg = getStackTrace(tipo, ex);
		mensagem(componente, msg);
	}

	private static String getStackTrace(String info, Exception ex) {
		StringWriter sw = new StringWriter();
		sw.append(info + "\r\n\r\n");

		if (ex != null) {
			if (LOG_CONSOLE) {
				ex.printStackTrace();
			} else {
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
			}
		}

		return sw.toString();
	}

	public static String getStringLista(List<String> lista, boolean apostrofes) {
		StringBuilder sb = new StringBuilder();

		for (String string : lista) {
			if (estaVazio(string)) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(apostrofes ? citar(string) : string);
		}

		return sb.toString();
	}

	private static String citar(String string) {
		return "'" + string + "'";
	}

	public static void setContentTransfered(String string) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		if (clipboard != null) {
			clipboard.setContents(new StringSelection(string), null);
		}
	}

	public static String escapar(String s) {
		if (s == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		for (char c : s.toCharArray()) {
			if (c == '\'') {
				builder.append("&apos;");
			} else if (c == '\"') {
				builder.append("&quot;");
			} else if (c == '&') {
				builder.append("&amp;");
			} else if (c == '>') {
				builder.append("&gt;");
			} else if (c == '<') {
				builder.append("&lt;");
			} else if (c > 0x7e) {
				builder.append("&#" + ((int) c) + ";");
			} else {
				builder.append(c);
			}
		}

		return builder.toString();
	}

	public static int getInt(String string, int padrao) {
		if (Util.estaVazio(string)) {
			return padrao;
		}

		try {
			return Integer.parseInt(string.trim());
		} catch (Exception e) {
			return padrao;
		}
	}
}