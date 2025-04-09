package br.com.persist.plugins.requisicao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Base64Util;
import br.com.persist.assistencia.CellRenderer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.FragmentoUtil;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.RequestResult;
import br.com.persist.assistencia.RequestUtil;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Nil;
import br.com.persist.componente.OrdemModel;
import br.com.persist.componente.OrdemTable;
import br.com.persist.componente.Panel;
import br.com.persist.componente.Popup;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;
import br.com.persist.data.Array;
import br.com.persist.data.DataParser;
import br.com.persist.data.DataUtil;
import br.com.persist.data.Formatador;
import br.com.persist.data.Objeto;
import br.com.persist.data.ObjetoUtil;
import br.com.persist.data.Tipo;
import br.com.persist.plugins.requisicao.visualizador.RequisicaoPoolVisualizador;
import br.com.persist.plugins.requisicao.visualizador.RequisicaoVisualizador;
import br.com.persist.plugins.requisicao.visualizador.RequisicaoVisualizadorHeader;
import br.com.persist.plugins.requisicao.visualizador.RequisicaoVisualizadorListener;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class RequisicaoPagina extends Panel implements RequisicaoVisualizadorListener {
	private final transient RequisicaoPoolVisualizador poolVisualizador;
	private final PopupFichario popupFichario = new PopupFichario();
	public final TextEditorReq textEditorReq = new TextEditorReq();
	private final List<String> requisicoes = new ArrayList<>();
	private String chaveMensagem = "msg.sem_linha_tabela_sel";
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final transient RequisicaoRota requisicaoRota;
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final Tabela tabela = new Tabela();
	private ScrollPane scrollPane;
	private JSplitPane split;
	private final File file;
	private int sleep;

	public RequisicaoPagina(RequisicaoPoolVisualizador poolVisualizador, RequisicaoRota requisicaoRota, File file) {
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addMouseListener(mouseListenerFichario);
		this.poolVisualizador = poolVisualizador;
		this.requisicaoRota = requisicaoRota;
		this.file = file;
		montarLayout();
		abrir(true);
	}

	private class PopupFichario extends Popup {
		private Action fecharEsquerda = acaoMenu("label.fechar_todas_esquerda");
		private Action fecharDireita = acaoMenu("label.fechar_todas_direita");
		private Action fecharOutras = acaoMenu("label.fechar_outras_abas");
		private Action fecharTodas = acaoMenu("label.fechar_todas_abas");
		private Action fechar = acaoMenu("label.fechar_aba_ativa");
		private static final long serialVersionUID = 1L;

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

	class TextEditorReq extends TextEditor {
		private static final long serialVersionUID = 1L;
		private boolean validoSel;

		TextEditorReq() {
			addFocusListener(focusListenerInner);
		}

		private transient FocusListener focusListenerInner = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				validoSel = true;
			}
		};
	}

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

		void clonarSelecionados() {
			int[] linhas = getSelectedRows();
			if (linhas != null) {
				RequisicaoModelo modelo = getModelo();
				int total = modelo.getRowCount();
				List<Integer> lista = new ArrayList<>();
				for (int i : linhas) {
					int indice = ((OrdemModel) getModel()).getRowIndex(i);
					Requisicao req = modelo.getRequisicao(indice);
					if (req != null) {
						lista.add(modelo.adicionar(req.clonar()));
						lista.add(i);
					}
				}
				if (modelo.getRowCount() != total) {
					setModel(new OrdemModel(modelo));
					Util.ajustar(this, RequisicaoPagina.this.getGraphics());
					for (int i : lista) {
						addRowSelectionInterval(i, i);
					}
				}
			}
		}

		RequisicaoModelo getModelo() {
			return (RequisicaoModelo) ((OrdemModel) getModel()).getModel();
		}

		void adicionar(Requisicao req) {
			if (req != null) {
				RequisicaoModelo modelo = getModelo();
				modelo.adicionar(req);
				setModel(new OrdemModel(modelo));
				Util.ajustar(this, RequisicaoPagina.this.getGraphics());
				int i = modelo.getRowCount() - 1;
				addRowSelectionInterval(i, i);
			}
		}

		void selecionar(Requisicao req) {
			if (req != null) {
				RequisicaoModelo modelo = getModelo();
				for (int i = 0; i < modelo.getRowCount(); i++) {
					Requisicao r = modelo.getRequisicao(i);
					if (req.equals(r)) {
						addRowSelectionInterval(i, i);
					}
				}
			}
		}
	}

	private void montarLayout() {
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanel(), criarPanelResultado());
		split.setDividerLocation(Constantes.SIZE.height / 2);
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanel() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbar);
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textEditorReq);
		scrollPane = new ScrollPane(panelArea);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditorReq));
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

	static Action acaoMenu(String chave) {
		return Action.acaoMenu(RequisicaoMensagens.getString(chave), null);
	}

	static Action acaoIcon(String chave, Icon icon) {
		return Action.acaoIcon(RequisicaoMensagens.getString(chave), icon);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action clonarSelAcao = acaoIcon("label.clonar_selecionados", Icones.COPIA);
		private Action vAccessTokenAcao = acaoMenu("label.atualizar_access_token_var");
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private CheckBox chkModoTabela = new CheckBox();
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
			addButton(clonarSelAcao);
			buttonColar.addSeparator();
			buttonColar.addItem(vAccessTokenAcao);
			vAccessTokenAcao.setActionListener(e -> atualizarVar());
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
			add(chkModoTabela);
			clonarSelAcao.setActionListener(e -> clonarSelecionados());
			chkModoTabela.setToolTipText(RequisicaoMensagens.getString("label.modo_tabela"));
			chkModoTabela.addActionListener(e -> modoTabelaHandler(chkModoTabela.isSelected()));
		}

		private void modoTabelaHandler(boolean modoTabela) {
			if (modoTabela) {
				if (!ehArquivoReservadoMimes() && !ehArquivoReservadoIgnorados()) {
					configModoTabela(getRequisicaoTextSel());
				} else {
					Util.mensagem(RequisicaoPagina.this, RequisicaoMensagens.getString("msg.arquivo_reservado"));
					chkModoTabela.setSelected(false);
				}
			} else {
				configModoTexto(tabela.getRequisicao());
			}
		}

		private Requisicao getRequisicaoTextSel() {
			if (!textEditorReq.validoSel) {
				return null;
			}
			String string = Util.getString(textEditorReq);
			Requisicao resp = null;
			if (!Util.isEmpty(string)) {
				FragmentoUtil util = new FragmentoUtil(string);
				List<String> lista = util.fragmentos();
				if (lista.size() == 1) {
					resp = criar(lista.get(0));
				}
			}
			return resp;
		}

		private void clonarSelecionados() {
			if (chkModoTabela.isSelected()) {
				tabela.clonarSelecionados();
			}
		}

		private void configModoTabela(Requisicao req) {
			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, toolbar);
			tabela.setModel(new OrdemModel(criarRequisicaoModelo()));
			tabela.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
			scrollPane.getViewport().setView(tabela);
			panel.add(BorderLayout.CENTER, scrollPane);
			split.setLeftComponent(panel);
			split.setDividerLocation(Constantes.SIZE.height / 2);
			Util.ajustar(tabela, getGraphics());
			tabela.selecionar(req);
		}

		private RequisicaoModelo criarRequisicaoModelo() {
			RequisicaoModelo modelo = new RequisicaoModelo();
			FragmentoUtil frag = new FragmentoUtil(textEditorReq.getText());
			String string = frag.proximo();
			while (string.length() > 0) {
				Requisicao req = criar(string);
				modelo.adicionar(req);
				string = frag.proximo();
			}
			return modelo;
		}

		private Requisicao criar(String string) {
			if (Util.isEmpty(string)) {
				return null;
			}
			try {
				DataParser parser = new DataParser();
				Tipo tipo = parser.parse(string);
				return new Requisicao(tipo);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
			return null;
		}

		private void configModoTexto(Requisicao req) {
			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, toolbar);
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, textEditorReq);
			scrollPane.getViewport().setView(panelArea);
			panel.add(BorderLayout.CENTER, scrollPane);
			split.setLeftComponent(panel);
			split.setDividerLocation(Constantes.SIZE.height / 2);
			if (req != null) {
				Util.selecionarTexto(textEditorReq, req.getUrl());
			}
		}

		private void atualizarVar() {
			String string = Util.getContentTransfered();
			if (!Util.isEmpty(string)) {
				try {
					RequisicaoVisualizadorHeader.setAccesToken(string);
				} catch (ArgumentoException ex) {
					Util.mensagem(RequisicaoPagina.this, ex.getMessage());
				}
			}
		}

		@Override
		protected void limpar() {
			textEditorReq.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir(true);
			selecao = null;
			label.limpar();
		}

		@Override
		protected void copiar() {
			if (chkModoTabela.isSelected()) {
				Requisicao req = tabela.getRequisicao();
				if (req != null) {
					String string = req.getString();
					Util.setContentTransfered(string);
					copiarMensagem(string);
				} else {
					Util.mensagem(RequisicaoPagina.this, RequisicaoMensagens.getString(chaveMensagem));
				}
			} else {
				String string = Util.getString(textEditorReq);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				textEditorReq.requestFocus();
			}
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			if (chkModoTabela.isSelected()) {
				String string = Util.getContentTransfered();
				if (!Util.isEmpty(string)) {
					tabela.adicionar(criar(string));
				}
			} else {
				Util.getContentTransfered(textEditorReq, numeros, letras);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textEditorReq, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}

	public String getConteudo() {
		return textEditorReq.getText();
	}

	public String getNome() {
		return file.getName();
	}

	private boolean ehArquivoReservadoMimes() {
		return RequisicaoContainer.ehArquivoReservadoMimes(getNome());
	}

	private boolean ehArquivoReservadoIgnorados() {
		return RequisicaoContainer.ehArquivoReservadoIgnorados(getNome());
	}

	private void abrir(boolean checarModo) {
		textEditorReq.setText(Constantes.VAZIO);
		if (file.exists()) {
			try {
				int value = getValueScrollPane();
				textEditorReq.setText(ArquivoUtil.getString(file));
				textEditorReq.validoSel = false;
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
			}
		}
		if (checarModo && RequisicaoPreferencia.isAbrirModoTabela() && !ehArquivoReservadoMimes()
				&& !ehArquivoReservadoIgnorados()) {
			SwingUtilities.invokeLater(() -> {
				toolbar.modoTabelaHandler(true);
				toolbar.chkModoTabela.setSelected(true);
			});
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
		try {
			ArquivoUtil.salvar(textEditorReq, file);
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	public void formatar() {
		if (!Util.isEmpty(textEditorReq.getText())) {
			String string = Util.getString(textEditorReq);
			conteudoJson(string, "formatar()");
			textEditorReq.requestFocus();
		}
	}

	public void base64() {
		if (!Util.isEmpty(textEditorReq.getText())) {
			String string = Util.getString(textEditorReq);
			conteudoTexto(Base64Util.criarBase64(string), "base64()");
			textEditorReq.requestFocus();
		}
	}

	public void retornar64() {
		if (!Util.isEmpty(textEditorReq.getText())) {
			String string = Util.getString(textEditorReq);
			try {
				conteudoTexto(Base64Util.retornarBase64(string), "retornar64()");
			} catch (Exception ex) {
				conteudoTexto(ex.getMessage(), "retornar64()");
			}
			textEditorReq.requestFocus();
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
		conteudoTexto(sb.toString(), "variaveis()");
	}

	private void conteudoTexto(String string, String uri) {
		if (Util.isEmpty(string)) {
			return;
		}
		try {
			processarResposta(new ByteArrayInputStream(string.getBytes()), null, uri, "TEXTO",
					RequisicaoPoolVisualizador.VISUALIZADOR_TEXTO);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private void conteudoJson(String string, String uri) {
		if (Util.isEmpty(string)) {
			return;
		}
		try {
			processarResposta(new ByteArrayInputStream(string.getBytes()), null, uri, "JSON",
					RequisicaoPoolVisualizador.VISUALIZADOR_JSON);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	public boolean isModoTabela() {
		return toolbar.chkModoTabela.isSelected();
	}

	public void processar() {
		if (isModoTabela()) {
			Requisicao req = tabela.getRequisicao();
			if (req != null) {
				String string = req.getString();
				processar(string);
			} else {
				Util.mensagem(RequisicaoPagina.this, RequisicaoMensagens.getString(chaveMensagem));
			}
		} else {
			if (!Util.isEmpty(textEditorReq.getText())) {
				String string = Util.getString(textEditorReq);
				processar(string);
			}
		}
	}

	public void salvarReqSel(AtomicBoolean atomic) {
		if (toolbar.chkModoTabela.isSelected()) {
			Requisicao req = tabela.getRequisicao();
			if (req != null) {
				boolean salvarFormatado = Util.confirmar(this,
						RequisicaoMensagens.getString("msg.salvar_objeto_formatado"), false);
				boolean bkp = Formatador.isTabHabilitado();
				Formatador.setTabHabilitado(salvarFormatado);
				String string = req.getString();
				Formatador.setTabHabilitado(bkp);
				try {
					if (salvarFormatado) {
						appendString(file, string);
					} else {
						string = Util.replaceAll(string, "\n", Constantes.VAZIO);
						string = Util.replaceAll(string, "\r", Constantes.VAZIO);
						appendString(file, string);
					}
					atomic.set(true);
				} catch (Exception ex) {
					Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, RequisicaoPagina.this);
				}
			} else {
				Util.mensagem(RequisicaoPagina.this, RequisicaoMensagens.getString(chaveMensagem));
			}
		}
	}

	private void appendString(File file, String string) throws IOException {
		string = Constantes.QL2 + string;
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			raf.seek(raf.length());
			raf.writeBytes(string);
			abrir(false);
		}
	}

	@Override
	public void processarRota(String rota, String link) {
		if (Util.isEmpty(rota) || Util.isEmpty(link)) {
			return;
		}
		String valor = requisicaoRota.getValor(rota);
		if (Util.isEmpty(valor)) {
			return;
		}
		try {
			DataParser parser = new DataParser();
			Tipo tipo = parser.parse(valor);
			Requisicao req = new Requisicao(tipo);
			req.setUrl(checkUrl(req.getUrl(), link));
			processar(req.getString());
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private String checkUrl(String url, String link) {
		if (url.endsWith("/") && link.startsWith("/")) {
			return url + link.substring(1);
		}
		if (url.endsWith("\\") && link.startsWith("\\")) {
			return url + link.substring(1);
		}
		return url + link;
	}

	public void adicionarRota(RequisicaoRota rota) {
		if (toolbar.chkModoTabela.isSelected()) {
			Requisicao req = tabela.getRequisicao();
			if (req != null) {
				String string = req.getString();
				adicionarRota(rota, string);
			} else {
				Util.mensagem(RequisicaoPagina.this, RequisicaoMensagens.getString(chaveMensagem));
			}
		} else {
			if (!Util.isEmpty(textEditorReq.getText())) {
				String string = Util.getString(textEditorReq);
				adicionarRota(rota, string);
			}
		}
	}

	private void adicionarRota(RequisicaoRota rota, String string) {
		try {
			DataParser parser = new DataParser();
			Tipo tipo = parser.parse(string);
			Requisicao req = new Requisicao(tipo);
			if (req.getRota() != null) {
				rota.adicionar(req.getRota(), req.getString());
			} else {
				Util.mensagem(RequisicaoPagina.this, RequisicaoMensagens.getString("msg.objeto_sem_atributo_rota"));
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private void processar(String string) {
		requisicoes.clear();
		sleep = 0;
		try {
			DataParser parser = new DataParser();
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
			} catch (InterruptedException ex) {
				LOG.log(Level.SEVERE, Constantes.ERRO, ex);
				Thread.currentThread().interrupt();
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
			requisicoes.add(DataUtil.toString(tipo));
		}
	}

	private void iniciarRequisicoesArray(Tipo tipo) {
		List<String> lista = new ArrayList<>();
		Array array = (Array) tipo;
		int total = 1;
		for (Tipo item : array.getElementos()) {
			if (ObjetoUtil.contemAtributo(item, RequisicaoConstantes.TENTATIVAS)) {
				String s = ObjetoUtil.getValorAtributo(item, RequisicaoConstantes.TENTATIVAS);
				total = Integer.parseInt(s);
			}
			if (ObjetoUtil.contemAtributo(item, RequisicaoConstantes.SLEEP)) {
				String s = ObjetoUtil.getValorAtributo(item, RequisicaoConstantes.SLEEP);
				sleep = Integer.parseInt(s);
			}
			lista.add(DataUtil.toString(item));
		}
		for (int i = 1; i <= total; i++) {
			requisicoes.addAll(lista);
		}
	}

	private void atualizar(String string) {
		try {
			DataParser parser = new DataParser();
			string = VariavelProvedor.substituir(string);
			Objeto parametros = (Objeto) parser.parse(string);
			RequestResult result = RequestUtil.processar(parametros);
			String varCookie = RequisicaoUtil.getAtributoVarCookie(parametros);
			RequisicaoVisualizadorHeader.setVarCookie(varCookie, result.getCookie());
			processarResposta(result.getInputStream(), parametros, result.getUrl(), result.getMime(), null);
			textEditorReq.requestFocus();
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, this);
		}
	}

	private void processarResposta(InputStream resposta, Tipo parametros, String uri, String mime,
			RequisicaoVisualizador outro) throws IOException {
		RequisicaoPanelBytes panelBytes = new RequisicaoPanelBytes(this, resposta, parametros);
		panelBytes.setRequisicaoVisualizadorListener(this);
		panelBytes.setRequisicaoRota(requisicaoRota);
		panelBytes.configuracoes(uri, mime, outro);
		tabbedPane.addTab(panelBytes.getTitulo(), panelBytes.getIcone(), panelBytes);
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

	public void associarMimeVisualizador(String mime, RequisicaoVisualizador visualizador) {
		poolVisualizador.associar(this, mime, visualizador);
		SwingUtilities.invokeLater(() -> {
			int indice = tabbedPane.getSelectedIndex();
			if (indice != -1) {
				tabbedPane.setTitleAt(indice, visualizador.getTitulo());
				tabbedPane.setIconAt(indice, visualizador.getIcone());
			}
		});
	}

	public RequisicaoVisualizador getVisualizador(String mime) {
		return poolVisualizador.getVisualizador(mime);
	}

	public RequisicaoVisualizador[] getVisualizadores() {
		return poolVisualizador.getVisualizadores();
	}
}