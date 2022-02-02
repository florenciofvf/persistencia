package br.com.persist.plugins.requisicao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import br.com.persist.assistencia.Base64Util;
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
import br.com.persist.componente.Popup;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.parser.Array;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.ObjetoUtil;
import br.com.persist.parser.Parser;
import br.com.persist.parser.Tipo;
import br.com.persist.parser.TipoUtil;
import br.com.persist.plugins.requisicao.conteudo.ConteudoHTML;
import br.com.persist.plugins.requisicao.conteudo.ConteudoImagem;
import br.com.persist.plugins.requisicao.conteudo.ConteudoJSON;
import br.com.persist.plugins.requisicao.conteudo.ConteudoTexto;
import br.com.persist.plugins.requisicao.conteudo.RequisicaoConteudo;
import br.com.persist.plugins.requisicao.conteudo.RequisicaoHeader;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class RequisicaoPagina extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient Map<Integer, RequisicaoConteudo> mapaConteudo = new HashMap<>();
	private final ToolbarParametro toolbarParametro = new ToolbarParametro();
	private final PopupFichario popupFichario = new PopupFichario();
	private final List<String> requisicoes = new ArrayList<>();
	private final JTabbedPane tabbedPane = new JTabbedPane();
	public final JTextPane areaParametros = new JTextPane();
	private static final Logger LOG = Logger.getGlobal();
	private final Tabela tabela = new Tabela();
	private ScrollPane scrollPane;
	private JSplitPane split;
	private int tipoConteudo;
	private final File file;
	private int sleep;

	public RequisicaoPagina(File file) {
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_IMAGEM, new ConteudoImagem());
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_TEXTO, new ConteudoTexto());
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_JSON, new ConteudoJSON());
		mapaConteudo.put(RequisicaoConstantes.CONTEUDO_HTML, new ConteudoHTML());
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addMouseListener(mouseListenerFichario);
		this.file = file;
		montarLayout();
		abrir();
	}

	private class PopupFichario extends Popup {
		private static final long serialVersionUID = 1L;
		private Action fecharEsquerda = actionMenu("label.fechar_todas_esquerda");
		private Action fecharDireita = actionMenu("label.fechar_todas_direita");
		private Action fecharOutras = actionMenu("label.fechar_outras_abas");
		private Action fecharTodas = actionMenu("label.fechar_todas_abas");
		private Action fechar = actionMenu("label.fechar_aba_ativa");

		PopupFichario() {
			addMenuItem(fechar);
			addMenuItem(fecharOutras);
			addMenuItem(true, fecharEsquerda);
			addMenuItem(fecharDireita);
			addMenuItem(true, fecharTodas);
			fecharEsquerda.setActionListener(e -> fecharEsquerda());
			fecharDireita.setActionListener(e -> fecharDireita());
			fecharOutras.setActionListener(e -> fecharOutras());
			fecharTodas.setActionListener(e -> fecharTodas());
			fechar.setActionListener(e -> fechar());
		}

		private void preShow() {
			int indice = tabbedPane.getSelectedIndex();
			int count = tabbedPane.getTabCount();
			fecharEsquerda.setEnabled(indice > 0 && count > 1);
			fecharDireita.setEnabled(indice != -1 && count > 1 && indice < count - 1);
			fechar.setEnabled(getAbaAtiva() != null);
			fecharOutras.setEnabled(count > 1);
			fecharTodas.setEnabled(count > 1);
		}

		private void fecharEsquerda() {
			Component ativa = getAbaAtiva();
			if (ativa != null) {
				while (tabbedPane.getTabCount() > 0) {
					Component aba = getAba(0);
					if (aba == ativa) {
						break;
					} else {
						tabbedPane.removeTabAt(0);
					}
				}
			}
		}

		private void fecharDireita() {
			Component ativa = getAbaAtiva();
			if (ativa != null) {
				while (tabbedPane.getTabCount() > 0) {
					Component aba = getAba(tabbedPane.getTabCount() - 1);
					if (aba == ativa) {
						break;
					} else {
						tabbedPane.removeTabAt(tabbedPane.getTabCount() - 1);
					}
				}
			}
		}

		private void fecharOutras() {
			Component ativa = getAbaAtiva();
			if (ativa != null) {
				while (tabbedPane.getTabCount() != 1) {
					excluirMenos(ativa);
				}
			}
		}

		private void excluirMenos(Component ativa) {
			for (int i = 0; i < tabbedPane.getTabCount(); i++) {
				Component aba = getAba(i);
				if (aba != ativa) {
					tabbedPane.removeTabAt(i);
				}
			}
		}

		private void fecharTodas() {
			while (tabbedPane.getTabCount() > 0) {
				tabbedPane.removeTabAt(0);
			}
		}

		private void fechar() {
			int indice = tabbedPane.getSelectedIndex();
			if (indice != -1) {
				tabbedPane.removeTabAt(indice);
			}
		}

		private Component getAbaAtiva() {
			int indice = tabbedPane.getSelectedIndex();
			if (indice != -1) {
				return tabbedPane.getComponentAt(indice);
			}
			return null;
		}

		private Component getAba(int indice) {
			return tabbedPane.getComponentAt(indice);
		}
	}

	private transient MouseListener mouseListenerFichario = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupFichario.preShow();
				popupFichario.show(tabbedPane, e.getX(), e.getY());
			}
		}
	};

	private class Tabela extends OrdemTable {
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

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.CENTER, tabbedPane);
		return panel;
	}

	private int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	private void setValueScrollPane(int value) {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
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
		if (!Util.estaVazio(areaParametros.getText())) {
			String string = Util.getString(areaParametros);
			conteudoJson(string);
			areaParametros.requestFocus();
		}
	}

	public void base64() {
		if (!Util.estaVazio(areaParametros.getText())) {
			String string = Util.getString(areaParametros);
			conteudoTexto(Base64Util.criarBase64(string));
			areaParametros.requestFocus();
		}
	}

	public void retornar64() {
		if (!Util.estaVazio(areaParametros.getText())) {
			String string = Util.getString(areaParametros);
			try {
				conteudoTexto(Base64Util.retornarBase64(string));
			} catch (Exception ex) {
				conteudoTexto(ex.getMessage());
			}
			areaParametros.requestFocus();
		}
	}

	public void variaveis() {
		StringBuilder sb = new StringBuilder();
		Properties properties = System.getProperties();
		Set<String> chaves = properties.stringPropertyNames();
		for (String chave : chaves) {
			Object valor = properties.get(chave);
			sb.append(chave + "=" + (valor != null ? valor.toString() : "") + Constantes.QL);
		}
		conteudoTexto(sb.toString());
	}

	private void conteudoTexto(String string) {
		if (Util.estaVazio(string)) {
			return;
		}
		try {
			configConteudo(new AtomicReference<Map<String, List<String>>>(), null);
			processarResposta(new ByteArrayInputStream(string.getBytes()), null);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private void conteudoJson(String string) {
		if (Util.estaVazio(string)) {
			return;
		}
		try {
			tipoConteudo = RequisicaoConstantes.CONTEUDO_JSON;
			processarResposta(new ByteArrayInputStream(string.getBytes()), null);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	public void processar() {
		if (toolbarParametro.chkModoTabela.isSelected()) {
			Requisicao req = tabela.getRequisicao();
			if (req != null) {
				String string = req.getString();
				processar(string);
			}
		} else {
			if (!Util.estaVazio(areaParametros.getText())) {
				String string = Util.getString(areaParametros);
				processar(string);
			}
		}
	}

	public void adicionarRota(RequisicaoRota rota) {
		if (toolbarParametro.chkModoTabela.isSelected()) {
			Requisicao req = tabela.getRequisicao();
			if (req != null) {
				String string = req.getString();
				adicionarRota(rota, string);
			}
		} else {
			if (!Util.estaVazio(areaParametros.getText())) {
				String string = Util.getString(areaParametros);
				adicionarRota(rota, string);
			}
		}
	}

	private void adicionarRota(RequisicaoRota rota, String string) {
		try {
			Parser parser = new Parser();
			Tipo tipo = parser.parse(string);
			Requisicao req = new Requisicao(tipo);
			rota.adicionar(req.getDesc(), req.getString());
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private void processar(String string) {
		requisicoes.clear();
		sleep = 0;
		try {
			Parser parser = new Parser();
			Tipo tipo = parser.parse(string);
			iniciarRequisicoes(tipo);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
		for (int i = 0; i < requisicoes.size(); i++) {
			String str = requisicoes.get(i);
			atualizar(str);
			try {
				Thread.sleep(sleep);
			} catch (Exception ex) {
				LOG.log(Level.SEVERE, Constantes.ERRO, ex);
			}
		}
	}

	private void iniciarRequisicoes(Tipo tipo) {
		if (tipo instanceof Objeto) {
			iniciarRequisicoesObjeto(tipo);
		} else if (tipo instanceof Array) {
			iniciarRequisicoesArray(tipo);
		}
	}

	private void iniciarRequisicoesObjeto(Tipo tipo) {
		int total = 1;
		if (ObjetoUtil.contemAtributo(tipo, RequisicaoConstantes.TENTATIVAS)) {
			String s = ObjetoUtil.getValorAtributo(tipo, RequisicaoConstantes.TENTATIVAS);
			total = Integer.parseInt(s);
		}
		if (ObjetoUtil.contemAtributo(tipo, RequisicaoConstantes.SLEEP)) {
			String s = ObjetoUtil.getValorAtributo(tipo, RequisicaoConstantes.SLEEP);
			sleep = Integer.parseInt(s);
		}
		for (int i = 1; i <= total; i++) {
			requisicoes.add(TipoUtil.toString(tipo));
		}
	}

	private void iniciarRequisicoesArray(Tipo tipo) {
		List<String> lista = new ArrayList<>();
		Array array = (Array) tipo;
		int total = 1;
		for (Tipo item : array.getLista()) {
			if (ObjetoUtil.contemAtributo(item, RequisicaoConstantes.TENTATIVAS)) {
				String s = ObjetoUtil.getValorAtributo(item, RequisicaoConstantes.TENTATIVAS);
				total = Integer.parseInt(s);
			}
			if (ObjetoUtil.contemAtributo(item, RequisicaoConstantes.SLEEP)) {
				String s = ObjetoUtil.getValorAtributo(item, RequisicaoConstantes.SLEEP);
				sleep = Integer.parseInt(s);
			}
			lista.add(TipoUtil.toString(item));
		}
		for (int i = 1; i <= total; i++) {
			requisicoes.addAll(lista);
		}
	}

	private void atualizar(String string) {
		try {
			Parser parser = new Parser();
			string = VariavelProvedor.substituir(string);
			Tipo parametros = parser.parse(string);
			AtomicReference<Map<String, List<String>>> mapResponseHeader = new AtomicReference<>();
			InputStream is = RequisicaoUtil.requisicao(parametros, mapResponseHeader);
			String varCookie = RequisicaoUtil.getAtributoVarCookie(parametros);
			configConteudo(mapResponseHeader, varCookie);
			processarResposta(is, parametros);
			areaParametros.requestFocus();
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private void configConteudo(AtomicReference<Map<String, List<String>>> mapHeader, String varCookie) {
		tipoConteudo = RequisicaoConstantes.CONTEUDO_TEXTO;
		Map<String, List<String>> map = mapHeader.get();
		if (map != null) {
			List<String> list = RequisicaoUtil.getList(map);
			if (list != null) {
				configConteudo(list);
			}
			RequisicaoHeader.setVarCookie(map, varCookie);
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

	private void processarResposta(InputStream resposta, Tipo parametros)
			throws RequisicaoException, IOException, BadLocationException {
		RequisicaoConteudo conteudo = mapaConteudo.get(tipoConteudo);
		Component view = conteudo.exibir(resposta, parametros);
		tabbedPane.addTab(conteudo.titulo(), conteudo.icone(), view);
		int ultimoIndice = tabbedPane.getTabCount() - 1;
		tabbedPane.setSelectedIndex(ultimoIndice);
		resposta.close();
		if (RequisicaoUtil.getAutoSaveVar(parametros)) {
			try {
				VariavelProvedor.salvar();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
		if (ObjetoUtil.contemAtributo(parametros, RequisicaoConstantes.TENTATIVAS)) {
			requisicoes.clear();
		}
	}
}