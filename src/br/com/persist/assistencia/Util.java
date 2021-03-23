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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import br.com.persist.componente.SetLista;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.mensagem.MensagemDialogo;
import br.com.persist.mensagem.MensagemFormulario;

public class Util {
	private static final Logger LOG = Logger.getGlobal();

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

	public static String copiarColunaUnicaString(JTable table, List<Integer> indices, boolean comAspas, String titulo) {
		if (table == null || indices == null) {
			return Constantes.VAZIO;
		}
		TableModel model = table.getModel();
		if (model == null || model.getColumnCount() < 1 || model.getRowCount() < 1) {
			return Constantes.VAZIO;
		}
		Coletor coletor = new Coletor();
		SetLista.view(titulo, nomeColunas(model), coletor, table, true);
		if (coletor.size() == 1) {
			return copiarColunaUnicaString(model, indices, comAspas, coletor);
		}
		return Constantes.VAZIO;
	}

	private static String copiarColunaUnicaString(TableModel model, List<Integer> indices, boolean comAspas,
			Coletor coletor) {
		StringBuilder sb = new StringBuilder();
		boolean[] selecionadas = colunasSelecionadas(coletor, model);
		conteudo(sb, model, indices, selecionadas, comAspas);
		return sb.toString();
	}

	private static void conteudo(StringBuilder sb, TableModel model, List<Integer> indices, boolean[] selecionadas,
			boolean comAspas) {
		int colunas = model.getColumnCount();
		for (Integer i : indices) {
			for (int j = 0; j < colunas; j++) {
				if (selecionadas[j]) {
					Object obj = model.getValueAt(i, j);
					String val = obj == null ? Constantes.VAZIO : obj.toString();
					conteudo(sb, comAspas, val);
				}
			}
		}
	}

	private static void conteudo(StringBuilder sb, boolean comAspas, String val) {
		if (!estaVazio(val)) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(comAspas ? citar(val) : val);
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
		Coletor coletor = new Coletor();
		SetLista.view("Colunas", nomeColunas(model), coletor, table);
		if (coletor.estaVazio()) {
			return new TransferidorTabular(Constantes.VAZIO, Constantes.VAZIO);
		}
		return criarTransferidorTabular(indices, model, coletor, table);
	}

	private static TransferidorTabular criarTransferidorTabular(List<Integer> indices, TableModel model,
			Coletor coletor, JTable table) {
		boolean[] selecionadas = colunasSelecionadas(coletor, model);
		StringBuilder tabular = new StringBuilder();
		StringBuilder html = new StringBuilder();
		iniciar(html);
		if (confirmar(table, "msg.com_cabecalho")) {
			cabecalho(html, tabular, model, selecionadas);
		}
		conteudo(html, tabular, model, indices, selecionadas);
		finalizar(html, tabular);
		return new TransferidorTabular(html.toString(), tabular.toString());
	}

	private static boolean[] colunasSelecionadas(Coletor coletor, TableModel model) {
		int colunas = model.getColumnCount();
		boolean[] selecionadas = new boolean[colunas];
		for (int i = 0; i < colunas; i++) {
			String coluna = model.getColumnName(i);
			selecionadas[i] = coletor.contem(coluna);
		}
		return selecionadas;
	}

	private static void iniciar(StringBuilder html) {
		html.append("<html>").append(Constantes.QL);
		html.append("<head>").append(Constantes.QL);
		html.append("<meta charset='utf-8'>").append(Constantes.QL);
		html.append("</head>").append(Constantes.QL);
		html.append("<body>").append(Constantes.QL);
		html.append("<table>").append(Constantes.QL);
	}

	private static List<String> nomeColunas(TableModel model) {
		List<String> lista = new ArrayList<>();
		int colunas = model.getColumnCount();
		for (int i = 0; i < colunas; i++) {
			lista.add(model.getColumnName(i));
		}
		return lista;
	}

	private static void cabecalho(StringBuilder html, StringBuilder tabular, TableModel model, boolean[] selecionadas) {
		int colunas = model.getColumnCount();
		html.append("<tr>").append(Constantes.QL);
		for (int i = 0; i < colunas; i++) {
			if (selecionadas[i]) {
				String coluna = model.getColumnName(i);
				html.append("<th>" + coluna + "</th>").append(Constantes.QL);
				tabular.append(coluna + Constantes.TAB);
			}
		}
		html.append("</tr>").append(Constantes.QL);
		tabular.deleteCharAt(tabular.length() - 1);
		tabular.append(Constantes.QL);
	}

	private static void conteudo(StringBuilder html, StringBuilder tabular, TableModel model, List<Integer> indices,
			boolean[] selecionadas) {
		int colunas = model.getColumnCount();
		for (Integer i : indices) {
			html.append("<tr>").append(Constantes.QL);
			for (int j = 0; j < colunas; j++) {
				if (selecionadas[j]) {
					Object obj = model.getValueAt(i, j);
					String val = obj == null ? Constantes.VAZIO : obj.toString();
					tabular.append(val + Constantes.TAB);
					html.append("<td>" + val + "</td>");
					html.append(Constantes.QL);
				}
			}
			html.append("</tr>").append(Constantes.QL);
			tabular.deleteCharAt(tabular.length() - 1);
			tabular.append(Constantes.QL);
		}
	}

	private static void finalizar(StringBuilder html, StringBuilder tabular) {
		html.append("</table>").append(Constantes.QL);
		html.append("</body>").append(Constantes.QL);
		html.append("</html>");
		tabular.deleteCharAt(tabular.length() - 1);
	}

	public static void mensagem(Component componente, String string) {
		mensagem(componente, string, null);
	}

	public static void mensagem(Component componente, String string, File file) {
		Component view = getViewParent(componente);
		if (view instanceof Frame) {
			mensagemFrame(string, file, view);
		} else if (view instanceof Dialog) {
			mensagemDialogo(string, file, view);
		} else {
			messageDialog(componente, string);
		}
	}

	private static void messageDialog(Component componente, String string) {
		TextArea textArea = new TextArea(string);
		textArea.setPreferredSize(new Dimension(500, 300));
		JOptionPane.showMessageDialog(componente, textArea, Mensagens.getString(Constantes.LABEL_ATENCAO),
				JOptionPane.PLAIN_MESSAGE);
	}

	private static void mensagemDialogo(String string, File file, Component view) {
		Dialog dialog = (Dialog) view;
		MensagemDialogo mensagem = MensagemDialogo.criar(dialog, Mensagens.getString(Constantes.LABEL_ATENCAO), string,
				file);
		mensagem.setSize(new Dimension(500, 300));
		mensagem.setLocationRelativeTo(dialog);
		mensagem.setVisible(true);
	}

	private static void mensagemFrame(String string, File file, Component view) {
		Frame frame = (Frame) view;
		MensagemDialogo mensagem = MensagemDialogo.criar(frame, Mensagens.getString(Constantes.LABEL_ATENCAO), string,
				file);
		mensagem.setSize(new Dimension(500, 300));
		mensagem.setLocationRelativeTo(frame);
		mensagem.setVisible(true);
	}

	private static Component getViewParent(Component componente) {
		Component view = componente;
		while (view != null) {
			if (view instanceof Frame || view instanceof Dialog) {
				break;
			}
			view = view.getParent();
		}
		return view;
	}

	public static void mensagemFormulario(Component componente, String string) {
		mensagemFormulario(componente, string, null);
	}

	public static void mensagemFormulario(Component componente, String string, File file) {
		Component view = getViewParent(componente);
		String titulo = Mensagens.getString(Constantes.LABEL_ATENCAO);
		MensagemFormulario mensagem = MensagemFormulario.criar(titulo, string, file);
		mensagem.setSize(new Dimension(500, 300));
		mensagem.setLocationRelativeTo(view);
		mensagem.setVisible(true);
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
		return confirmaSalvar(componente, confirmacoes, null);
	}

	public static boolean confirmaSalvar(Component componente, int confirmacoes, String chaveMsg) {
		int total = 0;
		while (total < confirmacoes
				&& confirmar(componente, chaveMsg != null ? chaveMsg : Constantes.LABEL_CONFIRMA_SALVAR)) {
			total++;
		}
		return total >= confirmacoes;
	}

	public static Object getValorInputDialog(Component parent, String chaveTitulo, String mensagem,
			String valorPadrao) {
		return JOptionPane.showInputDialog(parent, mensagem, Mensagens.getString(chaveTitulo),
				JOptionPane.PLAIN_MESSAGE, null, null, valorPadrao);
	}

	public static Object getValorInputDialog(Component parent, String chaveTitulo, String mensagem, String valorPadrao,
			String[] opcoes) {
		return JOptionPane.showInputDialog(parent, mensagem, Mensagens.getString(chaveTitulo),
				JOptionPane.PLAIN_MESSAGE, null, opcoes, valorPadrao);
	}

	public static void stackTraceAndMessage(String tipo, Exception ex, Component componente) {
		String msg = getStackTrace(tipo, ex);
		mensagem(componente, msg);
	}

	private static String getStackTrace(String info, Exception ex) {
		StringWriter sw = new StringWriter();
		sw.append(info + "\r\n\r\n");
		if (ex != null) {
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
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
			int posAnt = ini - 1;
			if (posAnt >= 0) {
				char d = string.charAt(posAnt);
				boolean parar = false;
				if (c == '\n' && d == '\n') {
					parar = true;
					ini++;
				} else if (d == '\n' && posAnt == 0) {
					parar = true;
				}
				if (parar) {
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
		Caret caret = area.getCaret();
		if (caret == null) {
			append(area, string);
		} else {
			int pos = caret.getDot();
			if (pos >= 0) {
				try {
					area.getDocument().insertString(pos, string, null);
				} catch (BadLocationException ex) {
					LOG.log(Level.SEVERE, Constantes.ERRO, ex);
				}
			} else {
				append(area, string);
			}
		}
	}

	private static void append(JTextComponent area, String string) {
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

	public static void conteudo(Component componente, File file) throws IOException {
		if (file != null && file.exists()) {
			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();
				while (linha != null) {
					sb.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			}
			mensagem(componente, sb.toString(), file);
		}
	}

	public static boolean contemNoArray(String string, String[] strings) {
		if (strings != null) {
			for (String s : strings) {
				if (s != null && s.equalsIgnoreCase(string)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean criarDiretorio(String string) {
		if (!estaVazio(string)) {
			File file = new File(string);
			if (file.isDirectory()) {
				return true;
			}
			return file.mkdir();
		}
		return false;
	}
}