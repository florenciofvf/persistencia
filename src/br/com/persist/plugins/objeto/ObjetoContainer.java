package br.com.persist.plugins.objeto;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
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
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.componente.Acao;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.componente.ToggleButton;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.plugins.arquivo.ArquivoProvedor;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalForm;
import br.com.persist.plugins.objeto.internal.InternalTransferidor;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class ObjetoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final ToggleButton btnArrasto = new ToggleButton(new ArrastoAcao());
	private final ToggleButton btnRotulos = new ToggleButton(new RotulosAcao());
	private final ToggleButton btnRelacao = new ToggleButton(new RelacaoAcao());
	private final ToggleButton btnSelecao = new ToggleButton(new SelecaoAcao());
	private final ObjetoSuperficie objetoSuperficie;
	private boolean abortarFecharComESCSuperficie;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> comboConexao;
	private ObjetoFormulario objetoFormulario;
	private File arquivo;

	public ObjetoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		objetoSuperficie = new ObjetoSuperficie(formulario, this);
		objetoSuperficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
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
		toolbar.configurar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	public void abrirArquivo(File file) {
		if (file != null) {
			arquivo = file;
			toolbar.baixar();
		}
	}

	public File getArquivo() {
		return arquivo;
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action desenharDescAcao = Action.actionIcon("label.desenhar_desc", Icones.TAG);
		private Action transparenteAcao = Action.actionIcon("label.transparente", Icones.RECT);
		private Action criarObjetoAcao = Action.actionIcon("label.criar_objeto", Icones.CRIAR);
		private Action desenharIdAcao = Action.actionIcon("label.desenhar_id", Icones.LABEL);
		private Action excluirAcao = Action.actionIcon("label.excluir_sel", Icones.EXCLUIR);
		private TextField txtPrefixoNomeTabela = new TextField(10);
		private CheckBox chkAjusteAutomatico = new CheckBox();
		private Label labelStatus = new Label();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, SALVAR,
					SALVAR_COMO, COPIAR, COLAR);
			addButton(true, excluirAcao);
			addButton(criarObjetoAcao);
			add(btnRelacao);
			add(true, btnRotulos);
			add(btnArrasto);
			add(btnSelecao);
			add(true, new ToggleButton(desenharIdAcao));
			add(new ToggleButton(desenharDescAcao));
			add(true, new ToggleButton(transparenteAcao));
			add(chkAjusteAutomatico);
			add(true, comboConexao);
			add(true, new ButtonInfo());
			add(true, labelStatus);
			add(true, txtPrefixoNomeTabela);

			eventos();
		}

		private void configurar() {
			chkAjusteAutomatico.setToolTipText(Mensagens.getString("label.ajuste_automatico"));
			txtPrefixoNomeTabela.setToolTipText(Mensagens.getString("label.prefixo_nt"));
			configAtalho(excluirAcao, KeyEvent.VK_D);
			configAtalho(colarAcao, KeyEvent.VK_V);
		}

		private void eventos() {
			comboConexao.addItemListener(e -> {
				if (ItemEvent.SELECTED == e.getStateChange()) {
					objetoSuperficie.selecionarConexao(getConexaoPadrao());
				}
			});

			desenharDescAcao
					.setActionListener(e -> objetoSuperficie.desenharDesc(((ToggleButton) e.getSource()).isSelected()));
			desenharIdAcao
					.setActionListener(e -> objetoSuperficie.desenharIds(((ToggleButton) e.getSource()).isSelected()));
			transparenteAcao
					.setActionListener(e -> objetoSuperficie.transparente(((ToggleButton) e.getSource()).isSelected()));
			chkAjusteAutomatico
					.addActionListener(e -> objetoSuperficie.setAjusteAutomaticoForm(chkAjusteAutomatico.isSelected()));
			txtPrefixoNomeTabela
					.addActionListener(e -> objetoSuperficie.prefixoNomeTabela(txtPrefixoNomeTabela.getText()));
			excluirAcao.setActionListener(e -> objetoSuperficie.excluirSelecionados());
			criarObjetoAcao.setActionListener(e -> criarObjeto());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(ObjetoContainer.this)) {
				ObjetoFormulario.criar(formulario, ObjetoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (objetoFormulario != null) {
				objetoFormulario.excluirContainer();
				objetoSuperficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
				setAbortarFecharComESCSuperficie(true);
				formulario.adicionarPagina(ObjetoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (getArquivo() != null) {
				ObjetoFormulario.criar(formulario, getArquivo());
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
				labelStatus.limpar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("BAIXAR: " + arquivo.getAbsolutePath(), ex, formulario);
			}
		}

		@Override
		protected void salvar() {
			if (!Util.confirmaSalvar(ObjetoContainer.this, Constantes.UM)) {
				return;
			}

			if (arquivo != null) {
				objetoSuperficie.salvar(arquivo, getConexaoPadrao());
			} else {
				salvarComo();
			}
		}

		@Override
		protected void salvarComo() {
			JFileChooser fileChooser = Util.criarFileChooser(arquivo, false);
			int opcao = fileChooser.showSaveDialog(formulario);

			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				if (file != null) {
					objetoSuperficie.salvar(file, getConexaoPadrao());
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
		protected void colar() {
			ObjetoSuperficie.CopiarColar.colar(objetoSuperficie, false, 0, 0);
		}

		private class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action excluirSemTabelaAcao = Action.actionMenu("label.excluir_sem_tabela", null);
			private Action totalAtualAcao = Action.actionMenu("label.total_atual", null);
			private Action comparaRecAcao = Action.actionMenu("label.compararRec", null);
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

	public void abrirExportacaoImportacaoMetadado(Metadado metadado, boolean exportacao, boolean circular) {
		if (abortarFecharComESCSuperficie) {
			objetoSuperficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		}

		objetoSuperficie.abrirExportacaoImportacaoMetadado(metadado, exportacao, circular);
		btnSelecao.click();
	}

	public void exportarMetadadoRaiz(Metadado metadado) {
		if (abortarFecharComESCSuperficie) {
			objetoSuperficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		}

		objetoSuperficie.exportarMetadadoRaiz(metadado);
		btnSelecao.click();
	}

	public void abrir(File file, ObjetoColetor coletor, Graphics g, InternalConfig config) {
		if (abortarFecharComESCSuperficie) {
			objetoSuperficie.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		}

		objetoSuperficie.setAjusteAutomaticoForm(coletor.getAjusteAutoForm().get());
		toolbar.chkAjusteAutomatico.setSelected(coletor.getAjusteAutoForm().get());
		objetoSuperficie.abrir(coletor);
		Conexao conexaoSel = null;
		arquivo = file;
		btnSelecao.click();

		if (!Util.estaVazio(coletor.getSbConexao().toString())) {
			String conexaoFile = coletor.getSbConexao().toString();

			for (int i = 0; i < comboConexao.getItemCount(); i++) {
				Conexao c = comboConexao.getItemAt(i);

				if (conexaoFile.equalsIgnoreCase(c.getNome())) {
					conexaoSel = c;
					break;
				}
			}

			if (conexaoSel != null) {
				comboConexao.setSelectedItem(conexaoSel);
			}
		}

		Conexao conexao = getConexaoPadrao();

		if (conexao != null && conexaoSel != null && conexaoSel.equals(conexao)) {
			adicionarInternalFormulario(conexao, coletor, g, config);
		}
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
						new Dimension(form.getLargura(), form.getAltura()), form.getApelido());
				objetoSuperficie.montarEAdicionarInternalFormulario(array, new Point(form.getX(), form.getY()), g,
						(String) array[InternalTransferidor.ARRAY_INDICE_APE], true, config);
			}
		}
	}

	private class ArrastoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private ArrastoAcao() {
			super(false, "label.arrastar", Icones.MAO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnArrasto.isSelected()) {
				objetoSuperficie.configEstado(Constantes.ARRASTO);
				objetoSuperficie.repaint();
			}
		}
	}

	private class RotulosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private RotulosAcao() {
			super(false, "label.rotulos", Icones.TEXTO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnRotulos.isSelected()) {
				objetoSuperficie.configEstado(Constantes.ROTULOS);
				objetoSuperficie.repaint();
			}
		}
	}

	private class RelacaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private RelacaoAcao() {
			super(false, "label.criar_relacao", Icones.SETA);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnRelacao.isSelected()) {
				objetoSuperficie.configEstado(Constantes.RELACAO);
				objetoSuperficie.repaint();
			}
		}
	}

	private class SelecaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private SelecaoAcao() {
			super(false, "label.selecao", Icones.CURSOR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (btnSelecao.isSelected()) {
				objetoSuperficie.configEstado(Constantes.SELECAO);
				objetoSuperficie.repaint();
			}
		}
	}

	public void setAbortarFecharComESCSuperficie(boolean b) {
		this.abortarFecharComESCSuperficie = b;
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

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	@Override
	public String getStringPersistencia() {
		return ArquivoProvedor.criarStringPersistencia(getArquivo());
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