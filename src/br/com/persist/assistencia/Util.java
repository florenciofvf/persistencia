package br.com.persist.assistencia;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import br.com.persist.componente.SeparadorDialogo;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.mensagem.MensagemDialogo;
import br.com.persist.mensagem.MensagemFormulario;

public class Util {
	private static final Logger LOG = Logger.getGlobal();
	private static Random random;

	private Util() {
	}

	public static boolean estaVazio(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static synchronized int getRandomInt(int bound) {
		try {
			if (random == null) {
				random = SecureRandom.getInstanceStrong();
			}
			return random.nextInt(bound);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException();
		}
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

	public static List<String> getValoresLinha(JTable table, int coluna) {
		TableModel modelo = table.getModel();
		List<Integer> linhas = Util.getIndicesLinha(table);
		List<String> resposta = new ArrayList<>();
		for (int i : linhas) {
			Object obj = modelo.getValueAt(i, coluna);
			if (obj != null && !Util.estaVazio(obj.toString())) {
				resposta.add(obj.toString());
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

	public static void copiarNomeColunas(String titulo, JTable table) {
		if (table == null) {
			return;
		}
		Coletor coletor = new Coletor();
		JTableHeader tableHeader = table.getTableHeader();
		TableColumnModel columnModel = tableHeader.getColumnModel();
		SetLista.view(titulo, nomeColunas(columnModel), coletor, table, new SetLista.Config(true, false));
		if (!coletor.estaVazio()) {
			SeparadorDialogo.criar(table, titulo, null, 0, false, coletor.getLista());
		}
	}

	public static void copiarNomeColunas(String titulo, Component c, List<String> nomes) {
		Coletor coletor = new Coletor();
		SetLista.view(titulo, nomes, coletor, c, new SetLista.Config(true, false));
		if (!coletor.estaVazio()) {
			SeparadorDialogo.criar(c, titulo, null, 0, false, coletor.getLista());
		}
	}

	public static void copiarColunaUnicaString(String titulo, JTable table, boolean comAspas) {
		if (table == null) {
			return;
		}
		TableModel model = table.getModel();
		if (model == null || model.getColumnCount() < 1 || model.getRowCount() < 1) {
			return;
		}
		Coletor coletor = new Coletor();
		JTableHeader tableHeader = table.getTableHeader();
		TableColumnModel columnModel = tableHeader.getColumnModel();
		List<String> listaNomes = nomeColunas(columnModel);
		if (listaNomes.size() == 1) {
			coletor.getLista().add(listaNomes.get(0));
		} else {
			SetLista.view(titulo, listaNomes, coletor, table, new SetLista.Config(true, true));
		}
		if (coletor.size() == 1) {
			copiarColunaUnicaString(titulo, table, columnModel, comAspas, coletor);
		}
	}

	private static void copiarColunaUnicaString(String titulo, JTable table, TableColumnModel columnModel,
			boolean comAspas, Coletor coletor) {
		ColunaSel sel = colunasSelecionadas(coletor, columnModel).get(0);
		SeparadorDialogo.criar(table, titulo, table, sel.indiceModel, comAspas, null);
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
		JTableHeader tableHeader = table.getTableHeader();
		TableColumnModel columnModel = tableHeader.getColumnModel();
		SetLista.view("Colunas", nomeColunas(columnModel), coletor, table, new SetLista.Config(true, false));
		if (coletor.estaVazio()) {
			return new TransferidorTabular(Constantes.VAZIO, Constantes.VAZIO);
		}
		return criarTransferidorTabular(columnModel, model, indices, coletor, table);
	}

	private static TransferidorTabular criarTransferidorTabular(TableColumnModel columnModel, TableModel model,
			List<Integer> indices, Coletor coletor, JTable table) {
		List<ColunaSel> selecionadas = colunasSelecionadas(coletor, columnModel);
		StringBuilder tabular = new StringBuilder();
		StringBuilder html = new StringBuilder();
		iniciar(html);
		if (confirmar(table, "msg.com_cabecalho")) {
			cabecalho(html, tabular, columnModel, selecionadas);
		}
		conteudo(html, tabular, model, indices, selecionadas);
		finalizar(html, tabular);
		return new TransferidorTabular(html.toString(), tabular.toString());
	}

	private static void iniciar(StringBuilder html) {
		html.append("<html>").append(Constantes.QL);
		html.append("<head>").append(Constantes.QL);
		html.append("<meta charset='utf-8'>").append(Constantes.QL);
		html.append("</head>").append(Constantes.QL);
		html.append("<body>").append(Constantes.QL);
		html.append("<table>").append(Constantes.QL);
	}

	private static List<String> nomeColunas(TableColumnModel columnModel) {
		List<String> lista = new ArrayList<>();
		int colunas = columnModel.getColumnCount();
		for (int i = 0; i < colunas; i++) {
			TableColumn column = columnModel.getColumn(i);
			lista.add(nomeColuna(column));
		}
		return lista;
	}

	private static List<ColunaSel> colunasSelecionadas(Coletor coletor, TableColumnModel columnModel) {
		int colunas = columnModel.getColumnCount();
		List<ColunaSel> selecionadas = new ArrayList<>();
		for (int i = 0; i < colunas; i++) {
			TableColumn column = columnModel.getColumn(i);
			String coluna = nomeColuna(column);
			if (coletor.contem(coluna)) {
				selecionadas.add(new ColunaSel(i, column.getModelIndex()));
			}
		}
		return selecionadas;
	}

	private static class ColunaSel {
		final int indiceHeader;
		final int indiceModel;

		private ColunaSel(int indiceHeader, int indice) {
			this.indiceHeader = indiceHeader;
			this.indiceModel = indice;
		}
	}

	private static String nomeColuna(TableColumn column) {
		Object object = column.getHeaderValue();
		return object == null ? Constantes.VAZIO : object.toString();
	}

	private static void cabecalho(StringBuilder html, StringBuilder tabular, TableColumnModel columnModel,
			List<ColunaSel> selecionadas) {
		html.append("<tr>").append(Constantes.QL);
		for (ColunaSel sel : selecionadas) {
			TableColumn column = columnModel.getColumn(sel.indiceHeader);
			String coluna = nomeColuna(column);
			html.append("<th>" + coluna + "</th>").append(Constantes.QL);
			tabular.append(coluna + Constantes.TAB);
		}
		html.append("</tr>").append(Constantes.QL);
		tabular.deleteCharAt(tabular.length() - 1);
		tabular.append(Constantes.QL);
	}

	private static void conteudo(StringBuilder html, StringBuilder tabular, TableModel model, List<Integer> indices,
			List<ColunaSel> selecionadas) {
		for (Integer i : indices) {
			html.append("<tr>").append(Constantes.QL);
			for (ColunaSel sel : selecionadas) {
				Object obj = model.getValueAt(i, sel.indiceModel);
				String val = obj == null ? Constantes.VAZIO : obj.toString();
				tabular.append(val + Constantes.TAB);
				html.append("<td>" + val + "</td>");
				html.append(Constantes.QL);
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

	public static void configSizeLocation(Window parent, Window child, Component c) {
		if (parent != null) {
			if (child.getWidth() > parent.getWidth() || child.getHeight() > parent.getHeight()) {
				Dimension size = parent.getSize();
				child.setSize((int) (size.width * .9), (int) (size.height * .9));
			}
			child.setLocationRelativeTo(parent);
		} else if (c != null) {
			child.setSize(c.getSize());
			child.setLocationRelativeTo(c);
		} else {
			child.setLocationRelativeTo(null);
		}
	}

	public static Component getViewParent(Component componente) {
		Component view = componente;
		while (view != null) {
			if (view instanceof Frame || view instanceof Dialog) {
				break;
			}
			view = view.getParent();
		}
		return view;
	}

	public static Frame getViewParentFrame(Component componente) {
		Component view = componente;
		while (view != null) {
			if (view instanceof Frame) {
				return (Frame) view;
			}
			view = view.getParent();
		}
		return null;
	}

	public static Dialog getViewParentDialog(Component componente) {
		Component view = componente;
		while (view != null) {
			if (view instanceof Dialog) {
				return (Dialog) view;
			}
			view = view.getParent();
		}
		return null;
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
		return showConfirmDialog(componente, msgEhChave ? Mensagens.getString(msg) : msg,
				Mensagens.getString(Constantes.LABEL_ATENCAO), JOptionPane.YES_OPTION) == JOptionPane.OK_OPTION;
	}

	public static class Config {
		final int messageType;
		final Icon icon;
		final Object[] options;
		final Object initialValue;

		public Config(int messageType, Icon icon, Object[] options, Object initialValue) {
			this.messageType = messageType;
			this.icon = icon;
			this.options = options;
			this.initialValue = initialValue;
		}

		public Config(int messageType) {
			this(messageType, null, null, null);
		}
	}

	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) {
		return showConfirmDialog(parentComponent, message, title, optionType, JOptionPane.QUESTION_MESSAGE);
	}

	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType,
			int messageType) {
		return showConfirmDialog(parentComponent, message, title, optionType, messageType, null);
	}

	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType,
			int messageType, Icon icon) {
		return showOptionDialog(parentComponent, message, title, optionType, new Config(messageType, icon, null, null));
	}

	public static int showOptionDialog(Component parentComponent, Object message, String title, int optionType,
			Config config) {
		JOptionPane pane = new JOptionPane(message, config.messageType, optionType, config.icon, config.options,
				config.initialValue);
		pane.setInitialValue(config.initialValue);
		pane.setComponentOrientation(
				((parentComponent == null) ? JOptionPane.getRootFrame() : parentComponent).getComponentOrientation());
		JDialog dialog = pane.createDialog(parentComponent, title);
		pane.selectInitialValue();
		configLocation(dialog, parentComponent);
		dialog.setVisible(true);
		dialog.dispose();
		return returnShowOptionDialog(config.options, pane);
	}

	private static void configLocation(JDialog dialog, Component componente) {
		if (componente != null) {
			Point pDialog = dialog.getLocation();
			Point pComp = componente.getLocationOnScreen();
			if (pDialog.y <= pComp.y) {
				dialog.setLocation(dialog.getX(), dialog.getY() + 100);
			}
		}
	}

	private static int returnShowOptionDialog(Object[] options, JOptionPane pane) {
		Object selectedValue = pane.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if (options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	private static String[] getArraySimNaoContinuar() {
		String sim = Mensagens.getString("label.sim");
		String nao = Mensagens.getString("label.nao");
		String sem = Mensagens.getString("label.nao_exibir");
		return new String[] { sim, nao, sem };
	}

	public static boolean confirmar(Component componente, String chaveMsg) {
		return confirmar(componente, chaveMsg, true);
	}

	public static boolean confirmar2(Component parent, String mensagem, AtomicBoolean atom) {
		String[] botoes = getArraySimNaoContinuar();
		int i = JOptionPane.showOptionDialog(parent, mensagem, Mensagens.getString("label.atencao"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, botoes, botoes[0]);
		if (i < 0 || botoes[1].equals(botoes[i])) {
			return false;
		}
		if (botoes[2].equals(botoes[i])) {
			atom.set(false);
			return true;
		}
		return botoes[0].equals(botoes[i]);
	}

	public static boolean confirmar3(Component componente, String chaveMsg) {
		String titulo = Mensagens.getString(Constantes.LABEL_ATENCAO);
		String mensagem = Mensagens.getString(chaveMsg);
		int messageType = JOptionPane.QUESTION_MESSAGE;
		int optionType = JOptionPane.YES_OPTION;
		return showOptionDialog(componente, mensagem, titulo, optionType,
				new Config(messageType)) == JOptionPane.OK_OPTION;
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

	public static boolean confirmaSalvarMsg(Component componente, int confirmacoes, String msg) {
		int total = 0;
		while (total < confirmacoes && confirmar(componente, msg, false)) {
			total++;
		}
		return total >= confirmacoes;
	}

	public static Object showInputDialog(Component parent, String titulo, String mensagem, String valorPadrao) {
		return JOptionPane.showInputDialog(parent, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE, null, null,
				valorPadrao);
	}

	public static Object getValorInputDialog(Component parent, String chaveTitulo, String mensagem,
			String valorPadrao) {
		return JOptionPane.showInputDialog(parent, mensagem, Mensagens.getString(chaveTitulo),
				JOptionPane.INFORMATION_MESSAGE, null, null, valorPadrao);
	}

	public static Object getValorInputDialog(Component parent, String chaveTitulo, String mensagem, String valorPadrao,
			String[] opcoes) {
		return JOptionPane.showInputDialog(parent, mensagem, Mensagens.getString(chaveTitulo),
				JOptionPane.INFORMATION_MESSAGE, null, opcoes, valorPadrao);
	}

	public static String getValorInputDialog(Component parent, String[] botoes) {
		return getValorInputDialog(parent, null, botoes);
	}

	public static String getValorInputDialog(Component parent, String mensagem, String[] botoes) {
		return getValorInputDialog2(parent, mensagem, botoes);
	}

	public static String getValorInputDialog2(Component parent, String mensagem, String[] botoes) {
		String msg = estaVazio(mensagem) ? Mensagens.getString("label.selecione_opcao") : mensagem;
		String titulo = Mensagens.getString(Constantes.LABEL_ATENCAO);
		int messageType = JOptionPane.INFORMATION_MESSAGE;
		int optionType = JOptionPane.DEFAULT_OPTION;
		int i = showOptionDialog(parent, msg, titulo, optionType, new Config(messageType, null, botoes, botoes[0]));
		if (i < 0) {
			return null;
		}
		return botoes[i];
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

	public static String getStringLista(List<String> lista, String separador, boolean quebrarLinha,
			boolean apostrofes) {
		StringBuilder sb = new StringBuilder();
		for (String string : lista) {
			if (estaVazio(string)) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(separador);
				if (quebrarLinha) {
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
		return new String(getArrayBytes(is), StandardCharsets.UTF_8);
	}

	public static String getString(byte[] is) {
		return new String(is, StandardCharsets.UTF_8);
	}

	public static byte[] getArrayBytes(InputStream is) throws IOException {
		if (is == null) {
			return new byte[0];
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		int lidos = is.read(bytes);
		while (lidos > 0) {
			baos.write(bytes, 0, lidos);
			lidos = is.read(bytes);
		}
		return baos.toByteArray();
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

	public static void getContentTransfered(JTextComponent area, boolean numeros, boolean letras) {
		String string = getContentTransfered();
		if (area == null || estaVazio(string)) {
			return;
		}
		string = getString(string, numeros, letras);
		insertStringArea(area, string);
	}

	public static void insertStringArea(JTextComponent area, String string) {
		if (string == null) {
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

	public static String getString(String string, boolean numeros, boolean letras) {
		if (numeros) {
			string = soNumeros(string);
		}
		if (letras) {
			string = soLetras(string);
		}
		return string;
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

	public static void destacar(StyledDocument doc, String pesquisado) {
		try {
			String string = doc.getText(0, doc.getLength());
			if (estaVazio(string) || estaVazio(pesquisado)) {
				return;
			}
			MutableAttributeSet att = new SimpleAttributeSet();
			StyleConstants.setBackground(att, Color.CYAN);
			int pos = string.indexOf(pesquisado);
			int len = pesquisado.length();
			int indice = 0;
			while (pos != -1) {
				doc.setCharacterAttributes(pos, len, att, true);
				indice = pos + len;
				pos = string.indexOf(pesquisado, indice);
			}
		} catch (BadLocationException e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static Selecao getSelecao(JTextComponent component, Selecao selecao, String string) {
		if (selecao == null) {
			return new Selecao(component, string);
		} else if (selecao.igual(string)) {
			return selecao;
		}
		return new Selecao(component, string);
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

	public static String pesquisar(File file, String pesquisar) {
		if (file != null && file.exists()) {
			pesquisar = pesquisar.toUpperCase();
			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();
				int contador = 0;
				while (linha != null) {
					contador++;
					if (linha.toUpperCase().indexOf(pesquisar) != -1) {
						sb.append(contador + ": " + linha + Constantes.QL);
					}
					linha = br.readLine();
				}
			} catch (IOException ex) {
				return null;
			}
			return sb.toString();
		}
		return null;
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

	public static String gerarNomeBackup(File parent, String nome) {
		int contador = 0;
		String string = nome + "-" + contador;
		while (new File(parent, string).exists()) {
			string = nome + "-" + (++contador);
		}
		return string;
	}

	public static List<String> listarNomeBackup(File parent, String nome) {
		List<String> resposta = new ArrayList<>();
		int contador = 0;
		String string = nome + "-" + contador;
		while (new File(parent, string).exists()) {
			resposta.add(string);
			string = nome + "-" + (++contador);
		}
		return resposta;
	}

	public static String getDataHora() {
		return new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new Date());
	}

	public static String ltrim(String s) {
		if (Util.estaVazio(s)) {
			return s;
		}
		int i = 0;
		while (s.charAt(i) <= ' ') {
			i++;
		}
		return s.substring(i);
	}
}