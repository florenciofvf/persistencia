package br.com.persist.util;

import java.awt.Component;
import java.awt.Dimension;
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.comp.SplitPane;
import br.com.persist.comp.TextArea;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Superficie;
import br.com.persist.fmt.Array;
import br.com.persist.fmt.Texto;
import br.com.persist.fmt.Tipo;
import br.com.persist.modelo.ConexaoComboModelo;
import br.com.persist.modelo.ObjetoComboModelo;
import br.com.persist.modelo.VariaveisModelo;

public class Util {
	private static final Logger LOG = Logger.getGlobal();
	private static final boolean LOG_CONSOLE = false;

	private Util() {
	}

	public static boolean estaVazio(String s) {
		return s == null || s.trim().length() == 0;
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

	public static void mensagem(Component componente, String string) {
		TextArea textArea = new TextArea(string);
		textArea.setPreferredSize(new Dimension(500, 300));

		JOptionPane.showMessageDialog(componente, textArea, Mensagens.getString(Constantes.LABEL_ATENCAO),
				JOptionPane.PLAIN_MESSAGE);
	}

	public static boolean confirmaExclusao(Component componente) {
		return confirmar(componente, "msg.confirma_exclusao");
	}

	public static boolean confirmar(Component componente, String chaveMsg) {
		return JOptionPane.showConfirmDialog(componente, Mensagens.getString(chaveMsg),
				Mensagens.getString(Constantes.LABEL_ATENCAO), JOptionPane.YES_OPTION) == JOptionPane.OK_OPTION;
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
		if (Sistema.getInstancia().isMac()) {
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

	public static String getStringListaSemV(List<String> lista, boolean apostrofes) {
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

	public static Map<String, String> criarMapaCampoChave(String string) {
		Map<String, String> mapa = new HashMap<>();

		if (!estaVazio(string)) {
			String[] strings = string.split(";");

			if (strings != null) {
				for (String chaveValor : strings) {
					String[] stringsCV = chaveValor.split("=");

					if (stringsCV != null && stringsCV.length > 1) {
						mapa.put(stringsCV[0].trim(), stringsCV[1].trim());
					}
				}
			}
		}

		return mapa;
	}

	public static Map<String, String> criarMapaSequencias(String string) {
		Map<String, String> mapa = new HashMap<>();

		if (!estaVazio(string)) {
			String[] strings = string.split(";");

			if (strings != null) {
				for (String chaveValor : strings) {
					String[] stringsCV = chaveValor.split("=");

					if (stringsCV != null && stringsCV.length > 1) {
						mapa.put(stringsCV[0].trim().toLowerCase(), stringsCV[1].trim());
					}
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

	public static JComboBox<Conexao> criarComboConexao(ConexaoProvedor provedor, Conexao padrao) {
		Combo cmbConexao = new Combo(new ConexaoComboModelo(provedor.getConexoes()));

		if (padrao != null) {
			cmbConexao.setSelectedItem(padrao);
		}

		return cmbConexao;
	}

	public static class Combo extends JComboBox<Conexao> implements PopupMenuListener {
		private static final long serialVersionUID = 1L;
		private int total;

		public Combo(ConexaoComboModelo modelo) {
			super(modelo);
			total = modelo.getSize();
			addPopupMenuListener(this);
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			if (total != getModel().getSize()) {
				total = getModel().getSize();
				((ConexaoComboModelo) getModel()).notificarMudancas();
			}
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			LOG.log(Level.FINEST, "popupMenuWillBecomeInvisible");
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			LOG.log(Level.FINEST, "popupMenuCanceled");
		}
	}

	public static JComboBox<Objeto> criarComboObjetosSel(Superficie superficie) {
		return new JComboBox<>(new ObjetoComboModelo(superficie.getSelecionados()));
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
			instrucao = Constantes.VAZIO;
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

		Iterator<ChaveValor> iterator = VariaveisModelo.getLista().iterator();

		while (iterator.hasNext()) {
			ChaveValor cv = iterator.next();
			instrucao = instrucao.replaceAll("#" + cv.getChave() + "#", cv.getValor());
		}

		return instrucao;
	}

	public static String substituir(String instrucao, ChaveValor cv) {
		if (instrucao == null) {
			instrucao = Constantes.VAZIO;
		}

		if (cv != null && cv.getChave() != null) {
			instrucao = instrucao.replaceAll("#" + cv.getChave() + "#", cv.getValor());
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

	public static final byte ARRAY_INDICE_OBJ = 0;
	public static final byte ARRAY_INDICE_CON = 1;
	public static final byte ARRAY_INDICE_DIM = 2;
	public static final byte ARRAY_INDICE_APE = 3;

	public static List<List<String>> comparar(File file1, File file2) {
		List<List<String>> resposta = new ArrayList<>();

		List<String> iguais1 = new ArrayList<>();
		List<String> iguais2 = new ArrayList<>();
		List<String> arquivo1 = new ArrayList<>();
		List<String> arquivo2 = new ArrayList<>();

		resposta.add(iguais1);
		resposta.add(iguais2);
		resposta.add(arquivo1);
		resposta.add(arquivo2);

		List<String> pool1 = criarLista(file1);
		List<String> pool2 = criarLista(file2);

		while (!pool1.isEmpty()) {
			String string1 = pool1.remove(0);

			int pos = pool2.indexOf(string1);

			if (pos >= 0) {
				pool2.remove(pos);
				iguais1.add(string1 + "," + string1);
				iguais2.add(string1 + ",");
			} else {
				arquivo1.add(string1 + ",");
			}
		}

		while (!pool2.isEmpty()) {
			String string2 = pool2.remove(0);

			int pos = pool1.indexOf(string2);

			if (pos >= 0) {
				pool1.remove(pos);
				iguais1.add(string2 + "," + string2);
				iguais2.add(string2 + ",");
			} else {
				arquivo2.add("," + string2);
			}
		}

		final String PREFIXO = "<<<###################";
		final String SUFIXO = ") ###################>>>";

		iguais1.add(0, PREFIXO + " IGUAIS 1 (" + iguais1.size() + SUFIXO);
		iguais2.add(0, PREFIXO + " IGUAIS 2 (" + iguais2.size() + SUFIXO);
		arquivo1.add(0, PREFIXO + " ARQUIVO 1 (" + arquivo1.size() + SUFIXO);
		arquivo2.add(0, PREFIXO + " ARQUIVO 2 (" + arquivo2.size() + SUFIXO);

		return resposta;
	}

	private static List<String> criarLista(File file) {
		List<String> resposta = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String linha = br.readLine();

			while (linha != null) {
				String string = linha.trim();

				if (!string.isEmpty()) {
					resposta.add(string);
				}

				linha = br.readLine();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}

		return resposta;
	}

	public static ProcessBuilder criarProcessBuilder(Tipo parametros) {
		if (parametros instanceof br.com.persist.fmt.Objeto) {
			List<String> comandos = new ArrayList<>();

			br.com.persist.fmt.Objeto objeto = (br.com.persist.fmt.Objeto) parametros;

			Tipo tipoComando = objeto.getValor("comando");
			String comando = tipoComando instanceof Texto ? tipoComando.toString() : null;

			if (estaVazio(comando)) {
				return null;
			}

			comandos.add(comando);

			Tipo tipoParametros = objeto.getValor("parametros");

			if (tipoParametros instanceof Array) {
				Array array = (Array) tipoParametros;

				for (Tipo arg : array.getLista()) {
					comandos.add(" " + arg.toString());
				}
			}

			Tipo tipoVariaveis = objeto.getValor("variaveis");
			Map<String, String> variaveis = null;

			if (tipoVariaveis instanceof br.com.persist.fmt.Objeto) {
				br.com.persist.fmt.Objeto objVariaveis = (br.com.persist.fmt.Objeto) tipoVariaveis;
				variaveis = objVariaveis.getAtributosString();
			}

			Tipo tipoDiretorio = objeto.getValor("diretorio");
			String diretorio = tipoDiretorio instanceof Texto ? tipoDiretorio.toString() : null;

			return criarProcessBuilder(comandos, variaveis, diretorio);
		}

		return null;
	}

	private static ProcessBuilder criarProcessBuilder(List<String> comandos, Map<String, String> variaveis,
			String diretorio) {
		ProcessBuilder builder = new ProcessBuilder(comandos);

		Map<String, String> env = builder.environment();

		if (variaveis != null) {
			for (Entry<String, String> entry : variaveis.entrySet()) {
				env.put(entry.getKey(), entry.getValue());
			}
		}

		if (!estaVazio(diretorio)) {
			builder.directory(new File(diretorio));
		}

		return builder;
	}

	public static String requisicao(Tipo parametros) throws IOException {
		if (parametros instanceof br.com.persist.fmt.Objeto) {
			br.com.persist.fmt.Objeto objeto = (br.com.persist.fmt.Objeto) parametros;

			Tipo tipoUrl = objeto.getValor("url");
			String url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
			Map<String, String> mapHeader = null;

			Tipo tipoHeader = objeto.getValor("header");

			if (tipoHeader instanceof br.com.persist.fmt.Objeto) {
				br.com.persist.fmt.Objeto objHeader = (br.com.persist.fmt.Objeto) tipoHeader;
				mapHeader = objHeader.getAtributosString();
			}

			Tipo tipoBody = objeto.getValor("body");
			String bodyParams = null;

			if (tipoBody instanceof br.com.persist.fmt.Objeto) {
				br.com.persist.fmt.Objeto objBody = (br.com.persist.fmt.Objeto) tipoBody;
				Tipo params = objBody.getValor("parameters");
				bodyParams = params instanceof Texto ? params.toString() : null;
			}

			return requisicao(url, mapHeader, bodyParams);
		}

		return null;
	}

	public static String getAccessToken(Tipo tipo) {
		if (tipo instanceof br.com.persist.fmt.Objeto) {
			br.com.persist.fmt.Objeto objeto = (br.com.persist.fmt.Objeto) tipo;

			Tipo tipoAccessToken = objeto.getValor("access_token");
			return tipoAccessToken instanceof Texto ? tipoAccessToken.toString() : null;
		}

		return null;
	}

	public static String requisicao(String url, Map<String, String> header, String parametros) throws IOException {
		if (estaVazio(url)) {
			return null;
		}

		URL url2 = new URL(url);
		URLConnection conn = url2.openConnection();
		String verbo = null;

		if (header != null) {
			verbo = header.get("Request-Method");

			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		if ("POST".equalsIgnoreCase(verbo) && !estaVazio(parametros)) {
			conn.setDoOutput(true);
		}

		conn.connect();

		if ("POST".equalsIgnoreCase(verbo) && !estaVazio(parametros)) {
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(parametros);
			osw.flush();
		}

		return getString(conn.getInputStream());
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

	public static void checarPos(int pos, String msg) {
		if (pos < 0) {
			throw new IllegalStateException(msg);
		}
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

		area.setText(text + Constantes.QL2 + string);
	}
}