package br.com.persist.plugins.requisicao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.CellRenderer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.OrdemModel;
import br.com.persist.componente.OrdemTable;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Parser;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.conteudo.ConteudoHTML;
import br.com.persist.plugins.requisicao.conteudo.ConteudoImagem;
import br.com.persist.plugins.requisicao.conteudo.ConteudoJSON;
import br.com.persist.plugins.requisicao.conteudo.ConteudoTexto;
import br.com.persist.plugins.requisicao.conteudo.RequisicaoConteudo;
import br.com.persist.plugins.requisicao.conteudo.RequisicaoHeader;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class RequisicaoPagina extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient Map<Integer, RequisicaoConteudo> mapaConteudo = new HashMap<>();
	private final ToolbarParametro toolbarParametro = new ToolbarParametro();
	private final ToolbarResultado toolbarResultado = new ToolbarResultado();
	private final JTabbedPane tabbedPane = new JTabbedPane();
	public final JTextPane areaParametros = new JTextPane();
	private final Tabela tabela = new Tabela();
	private ScrollPane scrollPane;
	private JSplitPane split;
	private int tipoConteudo;
	private final File file;

	public RequisicaoPagina(File file) {
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_IMAGEM, new ConteudoImagem());
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_TEXTO, new ConteudoTexto());
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_JSON, new ConteudoJSON());
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_HTML, new ConteudoHTML());
		this.file = file;
		montarLayout();
		abrir();
	}

	class Tabela extends OrdemTable {
		private static final long serialVersionUID = 1L;

		public Tabela() {
			super(new OrdemModel(new RequisicaoModelo()));
		}

		Requisicao getRequisicao() {
			int[] linhas = getSelectedRows();
			if (linhas != null && linhas.length == 1) {
				int indice = ((OrdemModel) getModel()).getRowIndex(linhas[0]);
				return getModelo().getRequisicao(indice);
			}
			return null;
		}

		RequisicaoModelo getModelo() {
			return (RequisicaoModelo) ((OrdemModel) getModel()).getModel();
		}
	}

	private void montarLayout() {
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanelParametro(), criarPanelResultado());
		split.setDividerLocation(Constantes.SIZE.height / 2);
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanelParametro() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarParametro);
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, areaParametros);
		scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
		return panel;
	}

	private int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	private void setValueScrollPane(int value) {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarResultado);
		panel.add(BorderLayout.CENTER, tabbedPane);
		return panel;
	}

	static Action actionMenu(String chave) {
		return Action.acaoMenu(RequisicaoMensagens.getString(chave), null);
	}

	private class ToolbarParametro extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private Action vAccessTokenAcao = actionMenu("label.atualizar_access_token_var");
		private final TextField txtPesquisa = new TextField(35);
		private CheckBox chkModoTabela = new CheckBox();
		private transient Selecao selecao;

		private ToolbarParametro() {
			super.ini(null, LIMPAR, BAIXAR, COPIAR, COLAR);
			buttonColar.addSeparator();
			buttonColar.addItem(vAccessTokenAcao);
			vAccessTokenAcao.setActionListener(e -> atualizarVar());
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
			add(chkModoTabela);
			chkModoTabela.setToolTipText(RequisicaoMensagens.getString("label.modo_tabela"));
			chkModoTabela.addActionListener(e -> modoTabelaHandler(chkModoTabela.isSelected()));
		}

		private void modoTabelaHandler(boolean modoTabela) {
			if (modoTabela) {
				configModoTabela();
			} else {
				configModoTexto();
			}
		}

		private void configModoTabela() {
			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, toolbarParametro);
			tabela.setModel(new OrdemModel(criarRequisicaoModelo()));
			tabela.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
			scrollPane.getViewport().setView(tabela);
			panel.add(BorderLayout.CENTER, scrollPane);
			split.setLeftComponent(panel);
			split.setDividerLocation(Constantes.SIZE.height / 2);
			Util.ajustar(tabela, getGraphics());
		}

		private RequisicaoModelo criarRequisicaoModelo() {
			RequisicaoModelo modelo = new RequisicaoModelo();
			Fragmento frag = new Fragmento(areaParametros.getText());
			String string = frag.proximo();
			while (string.length() > 0) {
				Requisicao req = criar(string);
				modelo.adicionar(req);
				string = frag.proximo();
			}
			return modelo;
		}

		private Requisicao criar(String string) {
			if (Util.estaVazio(string)) {
				return null;
			}
			try {
				Parser parser = new Parser();
				Tipo tipo = parser.parse(string);
				return new Requisicao(tipo);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
			return null;
		}

		private void configModoTexto() {
			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, toolbarParametro);
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, areaParametros);
			scrollPane.getViewport().setView(panelArea);
			panel.add(BorderLayout.CENTER, scrollPane);
			split.setLeftComponent(panel);
			split.setDividerLocation(Constantes.SIZE.height / 2);
		}

		private void atualizarVar() {
			String string = Util.getContentTransfered();
			if (!Util.estaVazio(string)) {
				RequisicaoHeader.setAccesToken(string);
			}
		}

		@Override
		protected void limpar() {
			areaParametros.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(areaParametros);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			areaParametros.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(areaParametros, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				selecao = Util.getSelecao(areaParametros, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}

	private class ToolbarResultado extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtPesquisa = new TextField(35);
		private transient Selecao selecao;

		private ToolbarResultado() {
			super.ini(null, LIMPAR, COPIAR, COLAR);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		@Override
		protected void limpar() {
			//areaResultados.setText(Constantes.VAZIO);
		}

		@Override
		protected void copiar() {
//			String string = Util.getString(areaResultados);
//			Util.setContentTransfered(string);
//			copiarMensagem(string);
//			areaResultados.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
//			Util.getContentTransfered(areaResultados, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
//			if (!Util.estaVazio(txtPesquisa.getText())) {
//				selecionarAbaJSON();
//				selecao = Util.getSelecao(areaResultados, selecao, txtPesquisa.getText());
//				selecao.selecionar(label);
//			} else {
//				label.limpar();
//			}
		}
	}

	public String getConteudo() {
		return areaParametros.getText();
	}

	public String getNome() {
		return file.getName();
	}

	private void abrir() {
		areaParametros.setText(Constantes.VAZIO);
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				StringBuilder sb = new StringBuilder();
				int value = getValueScrollPane();
				String linha = br.readLine();
				while (linha != null) {
					sb.append(linha + Constantes.QL);
					linha = br.readLine();
				}
				areaParametros.setText(sb.toString());
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
		}
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, Constantes.TRES,
				RequisicaoMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			pw.print(areaParametros.getText());
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	public void formatar() {
//		if (!Util.estaVazio(areaParametros.getText())) {
//			String string = Util.getString(areaParametros);
//			areaResultados.setText(Constantes.VAZIO);
//			formatar(string);
//		}
	}

	private void formatar(String string) {
//		try {
//			Parser parser = new Parser();
//			Tipo json = parser.parse(string);
//			StyledDocument styledDoc = areaResultados.getStyledDocument();
//			if (styledDoc instanceof AbstractDocument) {
//				AbstractDocument doc = (AbstractDocument) styledDoc;
//				json.toString(doc, false, 0);
//			}
//			selecionarAbaJSON();
//			areaParametros.requestFocus();
//		} catch (Exception ex) {
//			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
//		}
	}

	public void base64() {
//		if (!Util.estaVazio(areaParametros.getText())) {
//			String string = Util.getString(areaParametros);
//			areaResultados.setText(Constantes.VAZIO);
//			base64(string);
//		}
	}

	public void retornar64() {
//		if (!Util.estaVazio(areaParametros.getText())) {
//			String string = Util.getString(areaParametros);
//			areaResultados.setText(Constantes.VAZIO);
//			retornar64(string);
//		}
	}

	public void variaveis() {
		StringBuilder sb = new StringBuilder();
		Properties properties = System.getProperties();
		Set<String> chaves = properties.stringPropertyNames();
		for (String chave : chaves) {
			Object valor = properties.get(chave);
			sb.append(chave + "=" + (valor != null ? valor.toString() : "") + Constantes.QL);
		}
//		selecionarAbaJSON();
//		areaResultados.setText(sb.toString());
	}

	private void base64(String string) {
//		try {
//			selecionarAbaJSON();
//			areaResultados.setText(Base64Util.criarBase64(string));
//			areaParametros.requestFocus();
//		} catch (Exception ex) {
//			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
//		}
	}

	private void retornar64(String string) {
//		try {
//			selecionarAbaJSON();
//			areaResultados.setText(Base64Util.retornarBase64(string));
//			areaParametros.requestFocus();
//		} catch (Exception ex) {
//			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
//		}
	}

	public void atualizar() {
		if (toolbarParametro.chkModoTabela.isSelected()) {
			Requisicao req = tabela.getRequisicao();
			if (req != null) {
				String string = req.getString();
				//areaResultados.setText(Constantes.VAZIO);
				atualizar(string);
			}
		} else {
			if (!Util.estaVazio(areaParametros.getText())) {
				String string = Util.getString(areaParametros);
//				areaResultados.setText(Constantes.VAZIO);
				atualizar(string);
			}
		}
	}

	private void atualizar(String string) {
		try {
			Parser parser = new Parser();
			Variavel vAccessToken = VariavelProvedor.getVariavel(RequisicaoConstantes.VAR_ACCESS_TOKEN);
			if (vAccessToken != null) {
				string = substituir(string, vAccessToken);
			}
			Tipo parametros = parser.parse(string);
			AtomicReference<Map<String, List<String>>> mapHeader = new AtomicReference<>();
			InputStream is = requisicao(parametros, mapHeader);
			configConteudo(mapHeader);
			processarResposta(is);
			areaParametros.requestFocus();
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private void configConteudo(AtomicReference<Map<String, List<String>>> mapHeader) {
		tipoConteudo = RequisicaoConstantes.CONTEUDO_TEXTO;
		Map<String, List<String>> map = mapHeader.get();
		if (map != null) {
			List<String> list = getList(map);
			if (list != null) {
				configConteudo(list);
			}
		}
	}

	private void configConteudo(List<String> list) {
		for (String string : list) {
			if (!Util.estaVazio(string)) {
				String s = string.toLowerCase();
				if (s.indexOf("image/") != -1) {
					tipoConteudo = RequisicaoConstantes.CONTEUDO_IMAGEM;
				} else if (s.indexOf("json") != -1) {
					tipoConteudo = RequisicaoConstantes.CONTEUDO_JSON;
				} else if (s.indexOf("html") != -1) {
					tipoConteudo = RequisicaoConstantes.CONTEUDO_HTML;
				}
			}
		}
	}

	private List<String> getList(Map<String, List<String>> map) {
		List<String> list = map.get("Content-Type");
		if (list == null) {
			list = map.get("content-type");
		}
		if (list == null) {
			list = map.get("CONTENT-TYPE");
		}
		return list;
	}

	private void processarResposta(InputStream resposta) throws Exception {
		RequisicaoConteudo conteudo = mapaConteudo.get(tipoConteudo);
		Component view = conteudo.exibir(resposta);
		tabbedPane.addTab(conteudo.titulo(), conteudo.icone(), view);
	}

	private InputStream requisicao(Tipo parametros, AtomicReference<Map<String, List<String>>> mapHeaderResult)
			throws IOException {
		if (parametros instanceof Objeto) {
			Objeto objeto = (Objeto) parametros;
			Tipo tipoUrl = objeto.getValor("url");
			String url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
			Map<String, String> mapHeader = getMapHeader(objeto);
			String bodyParams = getBodyParams(objeto);
			return requisicao(url, mapHeader, bodyParams, mapHeaderResult);
		}
		return null;
	}

	private String getBodyParams(Objeto objeto) {
		Tipo tipoBody = objeto.getValor("body");
		String bodyParams = null;
		if (tipoBody instanceof Objeto) {
			Objeto objBody = (Objeto) tipoBody;
			Tipo params = objBody.getValor("parameters");
			bodyParams = params instanceof Texto ? params.toString() : null;
		}
		return bodyParams;
	}

	private Map<String, String> getMapHeader(Objeto objeto) {
		Map<String, String> mapHeader = null;
		Tipo tipoHeader = objeto.getValor("header");
		if (tipoHeader instanceof Objeto) {
			Objeto objHeader = (Objeto) tipoHeader;
			mapHeader = objHeader.getAtributosString();
		}
		return mapHeader;
	}

	private InputStream requisicao(String url, Map<String, String> header, String parametros,
			AtomicReference<Map<String, List<String>>> mapHeader) throws IOException {
		if(Util.estaVazio(url)) {
			return null;
		}
		URL url2 = new URL(url);
		URLConnection conn = url2.openConnection();
		String verbo = setRequestPropertyAndGetVerbo(header, conn);
		checarDoOutput(parametros, conn, verbo);
		conn.connect();
		sePost(parametros, conn, verbo);
		if (mapHeader != null) {
			mapHeader.set(conn.getHeaderFields());
		}
		return conn.getInputStream();
	}

	private void sePost(String parametros, URLConnection conn, String verbo) throws IOException {
		if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(parametros);
			osw.flush();
		}
	}

	private void checarDoOutput(String parametros, URLConnection conn, String verbo) {
		if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
			conn.setDoOutput(true);
		}
	}

	private String setRequestPropertyAndGetVerbo(Map<String, String> header, URLConnection conn) {
		String verbo = null;
		if (header != null) {
			verbo = header.get("Request-Method");
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return verbo;
	}

	private String substituir(String instrucao, Variavel v) {
		if (instrucao == null) {
			instrucao = Constantes.VAZIO;
		}
		return instrucao.replaceAll("#" + v.getNome() + "#", v.getValor());
	}
}