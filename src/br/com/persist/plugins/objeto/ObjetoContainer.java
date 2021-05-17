package br.com.persist.plugins.objeto;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR0;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR_COMO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Acao;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.Popup;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.componente.ToggleButton;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XML;
import br.com.persist.plugins.arquivo.ArquivoProvedor;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoEvento;
import br.com.persist.plugins.conexao.ConexaoInfo;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalForm;
import br.com.persist.plugins.objeto.internal.InternalTransferidor;

public class ObjetoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final ToggleButton btnArrasto = new ToggleButton(new ArrastoAcao());
	private final ToggleButton btnRotulos = new ToggleButton(new RotulosAcao());
	private final ToggleButton btnRelacao = new ToggleButton(new RelacaoAcao());
	private final ToggleButton btnSelecao = new ToggleButton(new SelecaoAcao());
	private static final Logger LOG = Logger.getGlobal();
	private final ObjetoSuperficie objetoSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> comboConexao;
	private ObjetoFormulario objetoFormulario;
	private String tituloTemporario;
	private String conexaoFile;
	private File arquivo;

	public ObjetoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		objetoSuperficie = new ObjetoSuperficie(formulario, this);
		comboConexao = ConexaoProvedor.criarComboConexao(null);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public ObjetoFormulario getObjetoFormulario() {
		return objetoFormulario;
	}

	public void setObjetoFormulario(ObjetoFormulario objetoFormulario) {
		this.objetoFormulario = objetoFormulario;
	}

	private void montarLayout() {
		ButtonGroup grupo = new ButtonGroup();
		add(BorderLayout.CENTER, new ScrollPane(objetoSuperficie));
		add(BorderLayout.NORTH, toolbar);
		grupo.add(btnRotulos);
		grupo.add(btnArrasto);
		grupo.add(btnRelacao);
		grupo.add(btnSelecao);
	}

	private void configurar() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				objetoSuperficie.configurarLargura(getSize());
			}
		});
		toolbar.configurar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		checarSelecionarConexao(formulario, args);
		checarConexaoInfo(formulario, args);
	}

	private void checarSelecionarConexao(Formulario formulario, Map<String, Object> args) {
		Conexao conexao = (Conexao) args.get(ConexaoEvento.SELECIONAR_CONEXAO);
		if (conexao != null) {
			comboConexao.setSelectedItem(conexao);
			objetoSuperficie.selecionarConexao(conexao);
		}
	}

	@SuppressWarnings("unchecked")
	private void checarConexaoInfo(Formulario formulario, Map<String, Object> args) {
		List<ConexaoInfo> lista = (List<ConexaoInfo>) args.get(ConexaoEvento.COLETAR_INFO_CONEXAO);
		if (lista != null) {
			Conexao conexao = getConexaoPadrao();
			String conexaoAtual = conexao == null ? "null" : conexao.getNome();
			String nomeAba = arquivo == null ? "null" : arquivo.getAbsolutePath();
			lista.add(new ConexaoInfo(conexaoAtual, conexaoFile == null ? "null" : conexaoFile, nomeAba));
		}
	}

	public void abrirArquivo(File file) {
		if (file != null) {
			if (file.exists() && file.isFile()) {
				arquivo = file;
				toolbar.baixar();
			} else {
				Util.mensagem(ObjetoContainer.this,
						Mensagens.getString("msg.arquivo_invalido", file.getAbsolutePath()));
			}
		}
	}

	public File getArquivo() {
		return arquivo;
	}

	static Action actionMenu(String chave, Icon icon) {
		return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
	}

	static Action actionMenu(String chave) {
		return actionMenu(chave, null);
	}

	static Action actionIcon(String chave, Icon icon) {
		return Action.acaoIcon(ObjetoMensagens.getString(chave), icon);
	}

	static Action actionIcon(String chave) {
		return actionIcon(chave, null);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action excluirAcao = actionIcon("label.excluir_selecionado", Icones.EXCLUIR);
		private Action arquivoVinculadoAcao = actionMenu("label.abrir_criar_arq_vinculado");
		private Action criarObjetoAcao = actionIcon("label.criar_objeto", Icones.CRIAR);
		private TextField txtPrefixoNomeTabela = new TextField(5);
		private TextField txtArquivoVinculo = new TextField(10);
		private CheckBox chkAjusteAutomatico = new CheckBox();
		private CheckBox chkAjusteLarguraFrm = new CheckBox();
		private Popup popupArquivoVinculado = new Popup();
		private Label labelStatus = new Label();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, SALVAR,
					SALVAR_COMO, COPIAR, COLAR0);
			addButton(true, excluirAcao);
			addButton(criarObjetoAcao);
			add(btnRelacao);
			add(true, btnRotulos);
			add(btnArrasto);
			add(btnSelecao);
			add(true, new ButtonStatus());
			add(true, chkAjusteAutomatico);
			add(chkAjusteLarguraFrm);
			add(true, comboConexao);
			add(true, new ButtonInfo());
			add(labelStatus);
			add(true, txtPrefixoNomeTabela);
			add(true, txtArquivoVinculo);
			eventos();
			arquivoVinculadoAcao.setActionListener(e -> abrirArquivoVinculado());
			txtArquivoVinculo.addMouseListener(mouseListenerPopupVinculado);
			popupArquivoVinculado.add(arquivoVinculadoAcao);
		}

		private void configurar() {
			chkAjusteLarguraFrm.setToolTipText(ObjetoMensagens.getString("label.ajuste_largura_form"));
			chkAjusteAutomatico.setToolTipText(ObjetoMensagens.getString("label.ajuste_automatico"));
			txtArquivoVinculo.setToolTipText(ObjetoMensagens.getString("hint.arquivo_vinculado"));
			txtPrefixoNomeTabela.setToolTipText(ObjetoMensagens.getString("label.prefixo_nt"));
			configAtalho(excluirAcao, KeyEvent.VK_D);
			configAtalho(colar0Acao, KeyEvent.VK_V);
		}

		private void eventos() {
			comboConexao.addItemListener(e -> {
				if (ItemEvent.SELECTED == e.getStateChange()) {
					objetoSuperficie.selecionarConexao(getConexaoPadrao());
				}
			});
			txtPrefixoNomeTabela
					.addActionListener(e -> objetoSuperficie.prefixoNomeTabela(txtPrefixoNomeTabela.getText()));
			excluirAcao.setActionListener(e -> objetoSuperficie.excluirSelecionados());
			chkAjusteAutomatico.addActionListener(e -> setAjusteAutomaticoForm());
			chkAjusteLarguraFrm.addActionListener(e -> setAjusteLarguraForm());
			txtArquivoVinculo.addFocusListener(focusListenerArquivoVinculo);
			txtArquivoVinculo.addActionListener(e -> setArquivoVinculo());
			criarObjetoAcao.setActionListener(e -> criarObjeto());
		}

		private void abrirArquivoVinculado() {

		}

		private transient MouseListener mouseListenerPopupVinculado = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				processar(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				processar(e);
			}

			private void processar(MouseEvent e) {
				if (e.isPopupTrigger() && !Util.estaVazio(txtArquivoVinculo.getText())) {
					popupArquivoVinculado.show(txtArquivoVinculo, e.getX(), e.getY());
				}
			}
		};

		private void setAjusteLarguraForm() {
			objetoSuperficie.setAjusteLarguraForm(chkAjusteLarguraFrm.isSelected());
			if (chkAjusteLarguraFrm.isSelected()) {
				objetoSuperficie.configurarLargura(getSize());
			}
		}

		private void setAjusteAutomaticoForm() {
			objetoSuperficie.setAjusteAutomaticoForm(chkAjusteAutomatico.isSelected());
			if (chkAjusteAutomatico.isSelected()) {
				objetoSuperficie.getAjuste().empilharFormularios();
				objetoSuperficie.getAjuste().aproximarObjetoFormulario(true, true);
				objetoSuperficie.getAjustar().usarFormularios(false);
			}
		}

		private transient FocusListener focusListenerArquivoVinculo = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setArquivoVinculo();
			}
		};

		private void setArquivoVinculo() {
			objetoSuperficie.setArquivoVinculo(txtArquivoVinculo.getText());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.liberarPagina(ObjetoContainer.this)) {
				ObjetoFormulario.criar(formulario, ObjetoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (objetoFormulario != null) {
				objetoFormulario.excluirContainer();
				formulario.adicionarPagina(ObjetoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (getArquivo() != null) {
				ObjetoFabrica.abrirNoFormulario(formulario, getArquivo());
			} else {
				Util.mensagem(ObjetoContainer.this, Mensagens.getString("msg.arquivo_inexistente"));
			}
		}

		void formularioVisivel() {
			buttonDestacar.estadoFormulario();
			estadoSelecao();
		}

		void paginaVisivel() {
			buttonDestacar.estadoFichario();
			estadoSelecao();
		}

		@Override
		protected void baixar() {
			if (arquivo == null) {
				btnSelecao.click();
				return;
			}
			reabrirArquivo();
		}

		private void reabrirArquivo() {
			try {
				excluido();
				ObjetoColetor objetoColetor = new ObjetoColetor();
				XML.processar(arquivo, new ObjetoHandler(objetoColetor));
				abrir(arquivo, objetoColetor, null, null);
				txtPrefixoNomeTabela.limpar();
				tituloTemporario = null;
				labelStatus.limpar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("BAIXAR: " + arquivo.getAbsolutePath(), ex, formulario);
			}
		}

		@Override
		protected void salvar() {
			if (arquivo != null) {
				if (Util.confirmaSalvar(ObjetoContainer.this, Constantes.UM)) {
					salvar(arquivo);
				}
			} else {
				salvarComo();
			}
		}

		private void salvar(File file) {
			try {
				objetoSuperficie.salvar(file, getConexaoPadrao());
				tituloTemporario = null;
				salvoMensagem();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}

		@Override
		protected void salvarComo() {
			JFileChooser fileChooser = Util.criarFileChooser(arquivo, false);
			int opcao = fileChooser.showSaveDialog(formulario);
			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					salvar(file);
					arquivo = file;
					setTitulo();
				}
			}
		}

		@Override
		protected void copiar() {
			ObjetoSuperficie.CopiarColar.copiar(objetoSuperficie);
		}

		@Override
		protected void colar0() {
			ObjetoSuperficie.CopiarColar.colar(objetoSuperficie, false, 0, 0);
		}

		private class ButtonStatus extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action desenharDescAcao = Action.actionMenu("label.desenhar_desc", Icones.TAG);
			private Action transparenteAcao = Action.actionMenu("label.transparente", Icones.RECT);
			private Action desenharIdAcao = Action.actionMenu("label.desenhar_id", Icones.LABEL);
			private Action selecaoGeralAcao = actionMenu("label.selecao_todos", Icones.TAG2);
			private Action pontoDestinoAcao = actionMenu("label.ponto_destino", Icones.RECT);
			private Action pontoOrigemAcao = actionMenu("label.ponto_origem", Icones.RECT);

			private ButtonStatus() {
				super("label.status", Icones.TAG2);
				addItem(new JCheckBoxMenuItem(selecaoGeralAcao));
				addItem(new JCheckBoxMenuItem(desenharDescAcao));
				addItem(new JCheckBoxMenuItem(desenharIdAcao));
				addItem(new JCheckBoxMenuItem(transparenteAcao));
				addItem(new JCheckBoxMenuItem(pontoOrigemAcao));
				addItem(new JCheckBoxMenuItem(pontoDestinoAcao));
				eventos();
			}

			private void eventos() {
				selecaoGeralAcao.setActionListener(
						e -> objetoSuperficie.selecaoGeral(((JCheckBoxMenuItem) e.getSource()).isSelected()));
				desenharDescAcao.setActionListener(
						e -> objetoSuperficie.desenharDesc(((JCheckBoxMenuItem) e.getSource()).isSelected()));
				transparenteAcao.setActionListener(
						e -> objetoSuperficie.transparente(((JCheckBoxMenuItem) e.getSource()).isSelected()));
				pontoDestinoAcao.setActionListener(
						e -> objetoSuperficie.pontoDestino(((JCheckBoxMenuItem) e.getSource()).isSelected()));
				pontoOrigemAcao.setActionListener(
						e -> objetoSuperficie.pontoOrigem(((JCheckBoxMenuItem) e.getSource()).isSelected()));
				desenharIdAcao.setActionListener(
						e -> objetoSuperficie.desenharIds(((JCheckBoxMenuItem) e.getSource()).isSelected()));
			}
		}

		private class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action excluirSemTabelaAcao = actionMenu("label.excluir_sem_tabela");
			private Action totalAtualAcao = actionMenu("label.total_atual");
			private Action comparaRecAcao = actionMenu("label.compararRec");
			private MenuItem itemTotalAtual = new MenuItem(totalAtualAcao);
			private MenuItem itemComparaRec = new MenuItem(comparaRecAcao);

			private ButtonInfo() {
				super("label.comparar", Icones.INFO);
				addMenuItem(itemTotalAtual);
				addMenuItem(true, itemComparaRec);
				addMenuItem(true, excluirSemTabelaAcao);
				totalAtualAcao.setActionListener(
						e -> objetoSuperficie.atualizarTotal(getConexaoPadrao(), itemTotalAtual, labelStatus));
				comparaRecAcao.setActionListener(
						e -> objetoSuperficie.compararRecent(getConexaoPadrao(), itemComparaRec, labelStatus));
				excluirSemTabelaAcao.setActionListener(e -> {
					objetoSuperficie.excluirSemTabela();
					labelStatus.limpar();
				});
			}
		}

		private void criarObjeto() {
			objetoSuperficie.criarNovoObjeto(40, 40);
			btnSelecao.setSelected(true);
			btnSelecao.click();
		}

		private void setTitulo() {
			if (objetoFormulario == null) {
				int indice = formulario.getIndicePagina(ObjetoContainer.this);
				if (indice != -1) {
					formulario.setHintTitlePagina(indice, arquivo.getAbsolutePath(), arquivo.getName());
				}
			} else {
				objetoFormulario.setTitle(arquivo.getName());
			}
			labelStatus.limpar();
		}

		private void configAtalho(Acao acao, int tecla) {
			ObjetoContainer.this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(ObjetoSuperficie.getKeyStroke(tecla),
					acao.getChave());
			ObjetoContainer.this.getActionMap().put(acao.getChave(), acao);
		}
	}

	public Conexao getConexaoPadrao() {
		return (Conexao) comboConexao.getSelectedItem();
	}

	public void estadoSelecao() {
		btnSelecao.click();
	}

	public void abrirExportacaoImportacaoMetadado(Conexao conexao, Metadado metadado, boolean exportacao,
			boolean circular) {
		if (conexao != null) {
			comboConexao.setSelectedItem(conexao);
		}
		objetoSuperficie.abrirExportacaoImportacaoMetadado(conexao, metadado, exportacao, circular);
		btnSelecao.click();
	}

	public void exportarMetadadoRaiz(Metadado metadado) {
		objetoSuperficie.exportarMetadadoRaiz(metadado);
		btnSelecao.click();
	}

	public void abrir(File file, ObjetoColetor coletor, Graphics g, InternalConfig config) {
		toolbar.txtArquivoVinculo.setText(coletor.getArquivoVinculo());
		objetoSuperficie.abrir(coletor);
		arquivo = file;
		btnSelecao.click();
		Conexao conexaoSel = selecionarConexao(coletor, config);
		Conexao conexao = getConexaoPadrao();
		if (conexao != null && conexaoSel != null && conexaoSel.equals(conexao)) {
			adicionarInternalFormulario(conexao, coletor, g, config);
		}
		toolbar.chkAjusteLarguraFrm.setSelected(coletor.getAjusteLarguraForm().get());
		objetoSuperficie.setAjusteAutomaticoForm(coletor.getAjusteAutoForm().get());
		objetoSuperficie.setAjusteLarguraForm(coletor.getAjusteLarguraForm().get());
		toolbar.chkAjusteAutomatico.setSelected(coletor.getAjusteAutoForm().get());
		SwingUtilities.invokeLater(() -> {
			objetoSuperficie.configurarLargura(getSize());
			objetoSuperficie.repaint();
		});
	}

	private Conexao selecionarConexao(ObjetoColetor coletor, InternalConfig config) {
		Conexao conexaoSel = null;
		if (!Util.estaVazio(coletor.getSbConexao().toString())) {
			conexaoFile = coletor.getSbConexao().toString();
			conexaoSel = getConexaoSel(conexaoFile);
			if (conexaoSel != null) {
				comboConexao.setSelectedItem(conexaoSel);
			}
		}
		if (config != null && !Util.estaVazio(config.getConexao())) {
			conexaoSel = getConexaoSel(config.getConexao());
			if (conexaoSel != null) {
				comboConexao.setSelectedItem(conexaoSel);
			}
		}
		return conexaoSel;
	}

	private Conexao getConexaoSel(String nome) {
		for (int i = 0; i < comboConexao.getItemCount(); i++) {
			Conexao c = comboConexao.getItemAt(i);
			if (nome.equalsIgnoreCase(c.getNome())) {
				return c;
			}
		}
		return null;
	}

	private void adicionarInternalFormulario(Conexao conexao, ObjetoColetor coletor, Graphics g,
			InternalConfig config) {
		for (InternalForm form : coletor.getForms()) {
			Objeto instancia = null;
			for (Objeto objeto : coletor.getObjetos()) {
				if (form.getObjeto().equals(objeto.getId())) {
					instancia = objeto;
				}
			}
			if (instancia != null) {
				Object[] array = InternalTransferidor.criarArray(conexao, instancia,
						new Dimension(form.getLargura(), form.getAltura()));
				objetoSuperficie.montarEAdicionarInternalFormulario(array, new Point(form.getX(), form.getY()), g, true,
						config);
			}
		}
	}

	private class ArrastoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private ArrastoAcao() {
			super(false, ObjetoMensagens.getString("label.arrastar"), false, Icones.MAO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnArrasto.isSelected()) {
				objetoSuperficie.configEstado(ObjetoConstantes.ARRASTO);
				objetoSuperficie.repaint();
			}
		}
	}

	private class RotulosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private RotulosAcao() {
			super(false, ObjetoMensagens.getString("label.rotulos"), false, Icones.TEXTO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnRotulos.isSelected()) {
				objetoSuperficie.configEstado(ObjetoConstantes.ROTULOS);
				objetoSuperficie.repaint();
			}
		}
	}

	private class RelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private RelacaoAcao() {
			super(false, ObjetoMensagens.getString("label.criar_relacao"), false, Icones.SETA);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnRelacao.isSelected()) {
				objetoSuperficie.configEstado(ObjetoConstantes.RELACAO);
				objetoSuperficie.repaint();
			}
		}
	}

	private class SelecaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private SelecaoAcao() {
			super(false, ObjetoMensagens.getString("label.selecao"), false, Icones.CURSOR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnSelecao.isSelected()) {
				objetoSuperficie.configEstado(ObjetoConstantes.SELECAO);
				objetoSuperficie.repaint();
			}
		}
	}

	public Frame getFrame() {
		if (objetoFormulario != null) {
			return objetoFormulario;
		}
		return formulario;
	}

	public void excluido() {
		objetoSuperficie.excluido();
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
		objetoSuperficie.adicionadoAoFichario(fichario);
	}

	@Override
	public void excluindoDoFichario(Fichario fichario) {
		excluido();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
		objetoSuperficie.executarAoAbrirParent();
	}

	public void formularioFechado() {
		excluido();
	}

	@Override
	public String getStringPersistencia() {
		return ArquivoProvedor.criarStringPersistencia(getArquivo());
	}

	public String getTituloTemporario() {
		return tituloTemporario;
	}

	public void setTituloTemporario(String tituloTemporario) {
		this.tituloTemporario = tituloTemporario;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ObjetoFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public File getFile() {
		return getArquivo();
	}

	public String criarHint() {
		return arquivo != null ? arquivo.getAbsolutePath() : Constantes.NOVO;
	}

	public String criarTitulo() {
		if (tituloTemporario != null) {
			return tituloTemporario;
		}
		return arquivo != null ? arquivo.getName() : Constantes.NOVO;
	}

	public String getFileName() {
		return arquivo != null ? arquivo.getName() : null;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return criarTitulo();
			}

			@Override
			public String getTitulo() {
				return criarTitulo();
			}

			@Override
			public String getHint() {
				return criarHint();
			}

			@Override
			public Icon getIcone() {
				return Icones.CUBO;
			}
		};
	}
}