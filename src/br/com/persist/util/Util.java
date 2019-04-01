package br.com.persist.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.TextArea;
import br.com.persist.desktop.Superficie;
import br.com.persist.listener.PainelObjetoListener;
import br.com.persist.modelo.ConexaoComboModelo;
import br.com.persist.principal.Formulario;

public class Util {
	private static final Logger LOG = Logger.getGlobal();
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

	public static String getValorInputDialog(Component parent, String chaveMensagem, String valorPadrao) {
		return JOptionPane.showInputDialog(parent, Mensagens.getString(chaveMensagem), valorPadrao);
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
				LOG.log(Level.SEVERE, "ERRO", ex);
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

	public static String getContentTransfered() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		if (clipboard != null) {
			Transferable transferable = clipboard.getContents(null);

			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					return (String) transferable.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "ERRO", e);
				}
			}
		}

		return null;
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

	public static Map<String, List<String>> criarMapaCampoNomes(String string) {
		Map<String, List<String>> mapa = new HashMap<>();

		if (!estaVazio(string)) {
			String[] strings = string.split(";");

			if (strings != null) {
				for (String s : strings) {
					aux(s, mapa);
				}
			}
		}

		return mapa;
	}

	private static void aux(String string, Map<String, List<String>> mapa) {
		String[] strings = string.split("=");

		if (strings != null && strings.length > 1) {
			String campo = strings[0].trim();

			List<String> lista = mapa.computeIfAbsent(campo, t -> new ArrayList<>());

			String nomes = strings[1];
			String[] strNomes = nomes.split(",");

			for (String nome : strNomes) {
				lista.add(nome.trim());
			}
		}
	}

	public static String normalizar(String string, boolean substituir) {
		StringBuilder sb = new StringBuilder();

		if (string != null) {
			string = string.trim();

			for (char c : string.toCharArray()) {
				if (c == '\r' || c == '\n' || c == '\t') {
					if (substituir) {
						sb.append(' ');
					}

					continue;
				}

				sb.append(c);
			}
		}

		return sb.toString();
	}

	public static JComboBox<Conexao> criarComboConexao(Formulario formulario) {
		return new JComboBox<>(new ConexaoComboModelo(formulario.getConexoes()));
	}

	public static JComboBox<Conexao> criarComboConexao(PainelObjetoListener listener) {
		return new JComboBox<>(new ConexaoComboModelo(listener.getConexoes()));
	}

	public static Object[] criarArray(Conexao conexao, Objeto objeto, String apelido) {
		return criarArray(conexao, objeto, new Dimension(400, 250), apelido);
	}

	public static Object[] criarArray(Conexao conexao, Objeto objeto, Dimension dimension, String apelido) {
		Superficie.setComplemento(conexao, objeto);
		return new Object[] { objeto, conexao, dimension, apelido };
	}

	public static String substituir(String instrucao, Map<String, String> mapaChaveValor) {
		if (instrucao == null) {
			instrucao = "";
		}

		if (mapaChaveValor == null || mapaChaveValor.isEmpty()) {
			return instrucao;
		}

		Iterator<Map.Entry<String, String>> it = mapaChaveValor.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			instrucao = instrucao.replaceAll("#" + entry.getKey().toUpperCase() + "#", entry.getValue());
			instrucao = instrucao.replaceAll("#" + entry.getKey().toLowerCase() + "#", entry.getValue());
			instrucao = instrucao.replaceAll("#" + entry.getKey() + "#", entry.getValue());
		}

		return instrucao;
	}

	public static JFileChooser criarFileChooser(File arquivo, boolean multiSelection) {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setPreferredSize(Constantes.DIMENSION_FILE_CHOOSER);
		fileChooser.setMultiSelectionEnabled(multiSelection);

		if (arquivo != null) {
			fileChooser.setCurrentDirectory(arquivo);
		}

		return fileChooser;
	}

	public static final byte ARRAY_INDICE_OBJ = 0;
	public static final byte ARRAY_INDICE_CON = 1;
	public static final byte ARRAY_INDICE_DIM = 2;
	public static final byte ARRAY_INDICE_APE = 3;
}