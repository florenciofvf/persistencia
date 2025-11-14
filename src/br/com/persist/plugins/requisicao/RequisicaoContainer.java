package br.com.persist.plugins.requisicao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginFichario;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.FicharioPesquisa;
import br.com.persist.componente.Janela;
import br.com.persist.data.DataDialogo;
import br.com.persist.data.DataListener;
import br.com.persist.data.Tipo;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.requisicao.visualizador.RequisicaoPoolVisualizador;

public class RequisicaoContainer extends AbstratoContainer implements PluginFichario {
	private static final File file = new File(RequisicaoConstantes.REQUISICOES);
	private final transient RequisicaoPoolVisualizador poolVisualizador;
	private final transient RequisicaoRota rota = new RequisicaoRota();
	private static final Logger LOG = Logger.getGlobal();
	private RequisicaoFormulario requisicaoFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private RequisicaoDialogo requisicaoDialogo;
	private final RequisicaoFichario fichario;

	public RequisicaoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		fichario = new RequisicaoFichario(this);
		toolbar.ini(janela);
		montarLayout();
		configurar();
		poolVisualizador = new RequisicaoPoolVisualizador();
		abrir(conteudo, idPagina);
	}

	public RequisicaoDialogo getRequisicaoDialogo() {
		return requisicaoDialogo;
	}

	public void setRequisicaoDialogo(RequisicaoDialogo requisicaoDialogo) {
		this.requisicaoDialogo = requisicaoDialogo;
		if (requisicaoDialogo != null) {
			requisicaoFormulario = null;
		}
	}

	public RequisicaoFormulario getRequisicaoFormulario() {
		return requisicaoFormulario;
	}

	public void setRequisicaoFormulario(RequisicaoFormulario requisicaoFormulario) {
		this.requisicaoFormulario = requisicaoFormulario;
		if (requisicaoFormulario != null) {
			requisicaoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
		fichario.setListener(e -> toolbar.focusInputPesquisar());
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao2);
	}

	public String getConteudo() {
		RequisicaoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		RequisicaoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public void salvar() {
		toolbar.salvar();
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservadoMimes(String nome) {
		return RequisicaoConstantes.MIMES.equalsIgnoreCase(nome);
	}

	static boolean ehArquivoReservadoIgnorados(String nome) {
		return RequisicaoConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private boolean vetarAdicionarPagina(File file) {
		return (ehArquivoReservadoMimes(file.getName()) && !RequisicaoPreferencia.isExibirArqMimes())
				|| (ehArquivoReservadoIgnorados(file.getName()) && !RequisicaoPreferencia.isExibirArqIgnorados());
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(RequisicaoConstantes.REQUISICOES, new File(file, RequisicaoConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				files = ArquivoUtil.ordenar(files);
				List<RequisicaoPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if (vetarAdicionarPagina(f) || ArquivoUtil.contem(RequisicaoConstantes.REQUISICOES, f.getName())) {
						continue;
					}
					ordenados.add(new RequisicaoPagina(fichario, poolVisualizador, rota, f));
				}
				for (RequisicaoPagina pagina : ordenados) {
					fichario.adicionarPagina(pagina);
				}
			}
		}
		fichario.setConteudo(conteudo, idPagina);
		poolVisualizador.inicializar(formulario);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action atualizarAcao2 = actionIcon("label.requisicao", Icones.URL);
		private Action excluirAtivoAcao = actionIconExcluir();
		private ButtonUtil buttonUtil = new ButtonUtil();
		private ButtonRota buttonRota = new ButtonRota();
		private static final long serialVersionUID = 1L;
		private transient FicharioPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			addButton(true, atualizarAcao2);
			add(true, buttonUtil);
			add(buttonRota);
			add(txtPesquisa);
			add(chkPorParte);
			chkPsqConteudo.setTag(Constantes.FICHARIO);
			add(chkPsqConteudo);
			add(label);
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
			atualizarAcao2.setActionListener(e -> atualizar());
			txtPesquisa.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					fichario.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(RequisicaoContainer.this, Util.getString(set));
				} else {
					pesquisa = fichario.getPesquisa(pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		private class ButtonUtil extends ButtonPopup {
			private Action formatarAcao = acaoMenu("label.formatar_frag_json");
			private Action variaveisAcao = acaoMenu("label.variaveis_sistema");
			private Action retornar64Acao = acaoMenu("label.retornar_base64");
			private Action appendAcao = acaoMenu("label.salvar_req_sel");
			private Action base64Acao = acaoMenu("label.criar_base64");
			private Action modeloAcao = actionMenu("label.modelo");
			private static final long serialVersionUID = 1L;

			private ButtonUtil() {
				super("label.util", Icones.BOLA_VERDE);
				addMenuItem(modeloAcao);
				addMenuItem(formatarAcao);
				addMenuItem(base64Acao);
				addMenuItem(retornar64Acao);
				addMenuItem(variaveisAcao);
				addMenuItem(true, appendAcao);
				retornar64Acao.setActionListener(e -> retornar64());
				variaveisAcao.setActionListener(e -> variaveis());
				appendAcao.setActionListener(e -> salvarReqSel());
				formatarAcao.setActionListener(e -> formatar());
				base64Acao.setActionListener(e -> base64());
				modeloAcao.setActionListener(e -> modelo());
			}

			Action acaoMenu(String chave) {
				return Action.acaoMenu(RequisicaoMensagens.getString(chave), null);
			}

			@Override
			protected void popupPreShow() {
				RequisicaoPagina ativa = fichario.getPaginaAtiva();
				appendAcao.setEnabled(ativa != null && ativa.isModoTabela());
			}

			private void salvarReqSel() {
				RequisicaoPagina ativa = fichario.getPaginaAtiva();
				if (ativa != null) {
					AtomicBoolean atomic = new AtomicBoolean(false);
					ativa.salvarReqSel(atomic);
					if (atomic.get()) {
						salvoMensagem();
					}
				}
			}

			private void formatar() {
				RequisicaoPagina ativa = fichario.getPaginaAtiva();
				if (ativa != null) {
					ativa.formatar();
				}
			}

			private void modelo() {
				Component comp = Util.getViewParent(RequisicaoContainer.this);
				DataDialogo form = null;
				if (comp instanceof Frame) {
					form = DataDialogo.criar((Frame) comp, dataListener);
					Util.configSizeLocation((Frame) comp, form, RequisicaoContainer.this);
				} else if (comp instanceof Dialog) {
					form = DataDialogo.criar((Dialog) comp, dataListener);
					Util.configSizeLocation((Dialog) comp, form, RequisicaoContainer.this);
				} else {
					form = DataDialogo.criar((Dialog) null, dataListener);
					form.setLocationRelativeTo(comp != null ? comp : formulario);
				}
				form.setVisible(true);
			}

			private void base64() {
				RequisicaoPagina ativa = fichario.getPaginaAtiva();
				if (ativa != null) {
					ativa.base64();
				}
			}

			private void retornar64() {
				RequisicaoPagina ativa = fichario.getPaginaAtiva();
				if (ativa != null) {
					ativa.retornar64();
				}
			}

			private void variaveis() {
				RequisicaoPagina ativa = fichario.getPaginaAtiva();
				if (ativa != null) {
					ativa.variaveis();
				}
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(RequisicaoContainer.this)) {
				RequisicaoFormulario.criar(formulario, RequisicaoContainer.this);
			} else if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
				RequisicaoFormulario.criar(formulario, RequisicaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (requisicaoFormulario != null) {
				requisicaoFormulario.excluirContainer();
				formulario.adicionarPagina(RequisicaoContainer.this);
			} else if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
				formulario.adicionarPagina(RequisicaoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
			}
			RequisicaoFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
			}
			RequisicaoFormulario.criar(formulario, null, null);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void novo() {
			Object resp = Util.getValorInputDialog(RequisicaoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservadoMimes(nome)) {
				Util.mensagem(RequisicaoContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(RequisicaoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					RequisicaoPagina pagina = new RequisicaoPagina(fichario, poolVisualizador, rota, f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			RequisicaoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(RequisicaoPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			RequisicaoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(RequisicaoContainer.this,
					RequisicaoMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
				int indice = fichario.getSelectedIndex();
				ativa.excluir();
				fichario.remove(indice);
			}
		}

		@Override
		protected void atualizar() {
			RequisicaoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.processar();
			}
		}

		private class ButtonRota extends ButtonPopup {
			private Action adicionarAcao = actionMenu("label.adicionar");
			private Action exibirAcao = actionMenu("label.exibir");
			private Action limparAcao = actionMenu("label.limpar");
			private static final long serialVersionUID = 1L;

			private ButtonRota() {
				super("label.rotas", Icones.BOLA_AMARELA);
				addMenuItem(adicionarAcao);
				addMenuItem(exibirAcao);
				addMenuItem(limparAcao);
				adicionarAcao.setActionListener(e -> adicionarRota());
				exibirAcao.setActionListener(e -> exibir());
				limparAcao.setActionListener(e -> limpar());
			}

			private void exibir() {
				Util.mensagem(RequisicaoContainer.this, rota.toString());
			}

			private void limpar() {
				rota.limpar();
			}

			private void adicionarRota() {
				RequisicaoPagina ativa = fichario.getPaginaAtiva();
				if (ativa != null) {
					ativa.adicionarRota(rota);
				}
			}
		}
	}

	private transient DataListener dataListener = new DataListener() {
		@Override
		public void setParserTipo(Tipo tipo) {
			LOG.log(Level.FINEST, "setParserTipo");
		}

		@Override
		public boolean somenteModelo() {
			return true;
		}

		@Override
		public String getModelo() {
			return RequisicaoMensagens.getString("requisicao.modelo");
		}

		@Override
		public String getTitle() {
			return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO);
		}
	};

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
	}

	@Override
	public String getStringPersistencia() {
		RequisicaoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return RequisicaoFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO_MIN);
			}

			@Override
			public String getTitulo() {
				return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO);
			}

			@Override
			public String getHint() {
				return RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.URL;
			}
		};
	}
}