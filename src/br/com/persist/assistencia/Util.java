package br.com.persist.assistencia;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextArea;
import br.com.persist.mensagem.MensagemDialogo;

public class Util {
	private static final Logger LOG = Logger.getGlobal();
	private static final boolean LOG_CONSOLE = false;

	private Util() {
	}

	public static boolean estaVazio(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static String soNumeros(String s) {
		if (estaVazio(s)) {
			return Constantes.VAZIO;
		}
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c >= '0' && c <= '9') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static boolean igual(Object o1, Object o2) {
		return o1 != null ? o1.equals(o2) : o2 == null;
	}

	public static String soLetras(String s) {
		if (estaVazio(s)) {
			return Constantes.VAZIO;
		}
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static List<Integer> getIndicesLinha(JTable table) {
		List<Integer> resposta = new ArrayList<>();
		int[] linhas = table.getSelectedRows();
		int total = table.getRowCount();
		if (linhas == null || linhas.length == 0) {
			for (int i = 0; i < total; i++) {
				resposta.add(i);
			}
		} else {
			for (int i : linhas) {
				resposta.add(i);
			}
		}
		return resposta;
	}

	public static void ajustar(JTable table, Graphics graphics) {
		if (table == null || graphics == null) {
			return;
		}
		DefaultTableColumnModel columnModel = (DefaultTableColumnModel) table.getColumnModel();
		FontMetrics fontMetrics = graphics.getFontMetrics();
		for (int col = 0; col < table.getColumnCount(); col++) {
			String coluna = table.getColumnName(col);
			int largura = fontMetrics.stringWidth(coluna);
			for (int lin = 0; lin < table.getRowCount(); lin++) {
				TableCellRenderer renderer = table.getCellRenderer(lin, col);
				Component component = renderer.getTableCellRendererComponent(table, table.getValueAt(lin, col), false,
						false, lin, col);
				largura = Math.max(largura, component.getPreferredSize().width);
			}
			TableColumn column = columnModel.getColumn(col);
			column.setPreferredWidth(largura + 40);
		}
	}

	public static TransferidorTabular criarTransferidorTabular(JTable table, List<Integer> indices) {
		if (table == null || indices == null) {
			return null;
		}
		TableModel model = table.getModel();
		if (model == null || model.getColumnCount() < 1 || model.getRowCount() < 1) {
			return null;
		}
		StringBuilder tabular = new StringBuilder();
		StringBuilder html = new StringBuilder();
		html.append("<html>").append(Constantes.QL);
		html.append("<body>").append(Constantes.QL);
		html.append("<table>").append(Constantes.QL);
		html.append("<tr>").append(Constantes.QL);

		int colunas = model.getColumnCount();

		for (int i = 0; i < colunas; i++) {
			String coluna = model.getColumnName(i);

			html.append("<th>" + coluna + "</th>").append(Constantes.QL);
			tabular.append(coluna + Constantes.TAB);
		}

		html.append("</tr>").append(Constantes.QL);
		tabular.deleteCharAt(tabular.length() - 1);
		tabular.append(Constantes.QL);

		for (Integer i : indices) {
			html.append("<tr>").append(Constantes.QL);

			for (int j = 0; j < colunas; j++) {
				Object obj = model.getValueAt(i, j);
				String val = obj == null ? Constantes.VAZIO : obj.toString();

				tabular.append(val + Constantes.TAB);
				html.append("<td>" + val + "</td>");
				html.append(Constantes.QL);
			}

			html.append("</tr>").append(Constantes.QL);
			tabular.deleteCharAt(tabular.length() - 1);
			tabular.append(Constantes.QL);
		}

		html.append("</table>").append(Constantes.QL);
		html.append("</body>").append(Constantes.QL);
		html.append("</html>");
		tabular.deleteCharAt(tabular.length() - 1);

		return new TransferidorTabular(html.toString(), tabular.toString());
	}

	public static void mensagem(Component componente, String string) {
		Component view = componente;

		while (view != null) {
			if (view instanceof Frame || view instanceof Dialog) {
				break;
			}

			view = view.getParent();
		}

		String titulo = Mensagens.getString(Constantes.LABEL_ATENCAO);
		Dimension dimension = new Dimension(500, 300);
		MensagemDialogo mensagem = null;

		if (view instanceof Frame) {
			Frame frame = (Frame) view;
			mensagem = MensagemDialogo.criar(frame, titulo, string);
			mensagem.setSize(dimension);
			mensagem.setLocationRelativeTo(frame);
			mensagem.setVisible(true);

		} else if (view instanceof Dialog) {
			Dialog dialog = (Dialog) view;
			mensagem = MensagemDialogo.criar(dialog, titulo, string);
			mensagem.setSize(dimension);
			mensagem.setLocationRelativeTo(dialog);
			mensagem.setVisible(true);

		} else {
			TextArea textArea = new TextArea(string);
			textArea.setPreferredSize(dimension);

			JOptionPane.showMessageDialog(componente, textArea, titulo, JOptionPane.PLAIN_MESSAGE);
		}
	}

	public static boolean confirmaExclusao(Component componente, boolean objetos) {
		return confirmar(componente, objetos ? "msg.confirma_exclusao_objetos" : "msg.confirma_exclusao_registros");
	}

	public static boolean confirmar(Component componente, String msg, boolean msgEhChave) {
		return JOptionPane.showConfirmDialog(componente, msgEhChave ? Mensagens.getString(msg) : msg,
				Mensagens.getString(Constantes.LABEL_ATENCAO), JOptionPane.YES_OPTION) == JOptionPane.OK_OPTION;
	}

	public static boolean confirmar(Component componente, String chaveMsg) {
		return confirmar(componente, chaveMsg, true);
	}

	public static boolean confirmaSalvar(Component componente, int confirmacoes) {
		int total = 0;

		while (total < confirmacoes && confirmar(componente, Constantes.LABEL_CONFIRMA_SALVAR)) {
			total++;
		}

		return total >= confirmacoes;
	}

	public static Object getValorInputDialog(Component parent, String chaveTitulo, String mensagem,
			String valorPadrao) {
		return JOptionPane.showInputDialog(parent, mensagem, Mensagens.getString(chaveTitulo),
				JOptionPane.PLAIN_MESSAGE, null, null, valorPadrao);
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
				LOG.log(Level.SEVERE, Constantes.ERRO, ex);
			} else {
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
			}
		}

		return sw.toString();
	}

	public static void configWindowC(Window window) {
		if (isMac()) {
			try {
				Class<?> classe = Class.forName("com.apple.eawt.FullScreenUtilities");
				Method method = classe.getMethod("setWindowCanFullScreen", Window.class, Boolean.TYPE);
				method.invoke(classe, window, true);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(window.getClass().getName() + ".setWindowCanFullScreen()", ex, window);
			}
		}
	}

	public static String getStringLista(List<String> lista, boolean apostrofes, boolean emLinhas) {
		StringBuilder sb = new StringBuilder();

		for (String string : lista) {
			if (estaVazio(string)) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(", ");

				if (emLinhas) {
					sb.append(Constantes.QL);
				}
			}

			sb.append(apostrofes ? citar(string) : string);
		}

		return sb.toString();
	}

	public static String getStringListaSemVirgula(List<String> lista, boolean apostrofes) {
		StringBuilder sb = new StringBuilder();

		for (String string : lista) {
			if (estaVazio(string)) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(Constantes.QL);
			}

			sb.append(apostrofes ? citar(string) : string);
		}

		return sb.toString();
	}

	public static String citar(String string) {
		return "'" + string + "'";
	}

	public static void setContentTransfered(String string) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		if (clipboard != null) {
			clipboard.setContents(new StringSelection(string), null);
		}
	}

	public static void setTransfered(Transferable transferable) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		if (clipboard != null) {
			clipboard.setContents(transferable, null);
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
					LOG.log(Level.SEVERE, Constantes.ERRO, e);
				}
			}
		}

		return null;
	}

	public static String escapar(String s) {
		if (s == null) {
			return Constantes.VAZIO;
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

	public static JFileChooser criarFileChooser(File arquivo, boolean multiSelection) {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setPreferredSize(Constantes.DIMENSION_FILE_CHOOSER);
		fileChooser.setMultiSelectionEnabled(multiSelection);

		if (arquivo != null) {
			fileChooser.setCurrentDirectory(arquivo);
		}

		return fileChooser;
	}

	public static SplitPane splitPaneVertical(Component left, Component right, int local) {
		SplitPane splitPane = criarSplitPane(SplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(local);
		splitPane.setRightComponent(right);
		splitPane.setLeftComponent(left);

		return splitPane;
	}

	public static SplitPane splitPaneHorizontal(Component left, Component right, int local) {
		SplitPane splitPane = criarSplitPane(SplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(local);
		splitPane.setRightComponent(right);
		splitPane.setLeftComponent(left);

		return splitPane;
	}

	public static SplitPane criarSplitPane(int orientacao) {
		return new SplitPane(orientacao);
	}

	public static String getString(InputStream is) throws IOException {
		if (is == null) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		int lidos = is.read(bytes);

		while (lidos > 0) {
			baos.write(bytes, 0, lidos);
			lidos = is.read(bytes);
		}

		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}

	public static boolean iguais(Class<?> klass, String nome) {
		return klass.getName().equals(nome);
	}

	public static void selecionarTexto(JTextComponent area) {
		if (area == null) {
			return;
		}

		Caret caret = area.getCaret();

		if (caret == null) {
			return;
		}

		String string = area.getText();
		int ini = getIni(caret.getDot(), string);
		int fim = getFim(caret.getDot(), string);

		if (ini < fim) {
			area.setSelectionStart(ini);
			area.setSelectionEnd(fim);
		}
	}

	private static int getIni(int pos, String string) {
		int ini = pos;

		if (ini == string.length() && !string.isEmpty()) {
			ini--;
		}

		while (ini >= 0 && ini < string.length()) {
			char c = string.charAt(ini);

			if (ini - 1 >= 0) {
				char d = string.charAt(ini - 1);

				if (c == '\n' && d == '\n') {
					break;
				}
			}

			ini--;
		}

		return ini;
	}

	private static int getFim(int pos, String string) {
		int fim = pos;

		while (fim < string.length()) {
			char c = string.charAt(fim);

			if (fim + 1 < string.length()) {
				char d = string.charAt(fim + 1);

				if (c == '\n' && d == '\n') {
					break;
				}
			}

			fim++;
		}

		return fim;
	}

	public static String getString(JTextComponent area) {
		if (area == null) {
			return Constantes.VAZIO;
		}

		String string = area.getSelectedText();

		if (estaVazio(string)) {
			selecionarTexto(area);
		}

		string = area.getSelectedText();

		if (estaVazio(string)) {
			string = area.getText();
		}

		return string;
	}

	public static void getContentTransfered(JTextComponent area) {
		String string = getContentTransfered();

		if (area == null || estaVazio(string)) {
			return;
		}

		String text = area.getText();

		area.setText(text + Constantes.QL + string);
	}

	public static String replaceAll(String string, String atual, String novo) {
		if (estaVazio(string) || atual == null || novo == null) {
			return null;
		}

		int indice = 0;
		int pos = string.indexOf(atual);

		while (pos != -1) {
			String antes = string.substring(0, pos);
			String apos = string.substring(pos + atual.length());
			string = antes + novo + apos;
			indice = antes.length() + novo.length();
			pos = string.indexOf(atual, indice);
		}

		return string;
	}

	public static double menorEmPorcentagem(double menor, double maior) {
		return (menor * 100) / maior;
	}

	public static double porcentagemEmValor(double porcentagem, double maior) {
		return (porcentagem * maior) / 100;
	}

	public static boolean isMac() {
		String s = System.getProperty("os.name");
		return s != null && s.startsWith("Mac OS");
	}
}