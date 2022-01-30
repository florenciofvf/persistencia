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
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.parser.ParserDialogo;
import br.com.persist.parser.ParserListener;
import br.com.persist.parser.Tipo;

public class RequisicaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final RequisicaoFichario fichario = new RequisicaoFichario();
	private static final File file = new File("requisicoes");
	private static final Logger LOG = Logger.getGlobal();
	private RequisicaoFormulario requisicaoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private RequisicaoDialogo requisicaoDialogo;

	public RequisicaoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
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
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
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

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	private void abrir(String conteudo, String idPagina) {
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					RequisicaoPagina pagina = new RequisicaoPagina(f);
					fichario.adicionarPagina(pagina);
				}
			}
		}
		fichario.setConteudo(conteudo, idPagina);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	static Action actionMenu(String chave) {
		return Action.acaoMenu(RequisicaoMensagens.getString(chave), null);
	}

	static Action actionIcon(String chave, Icon icon) {
		return Action.acaoIcon(RequisicaoMensagens.getString(chave), icon);
	}

	static Action actionIcon(String chave) {
		return actionIcon(chave, null);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action variaveisAcao = actionIcon("label.variaveis_sistema", Icones.BOLA_AMARELA);
		private Action retornar64Acao = actionIcon("label.retornar_base64", Icones.BOLA_AMARELA);
		private Action formatarAcao = actionIcon("label.formatar_frag_json", Icones.BOLA_VERDE);
		private Action excluirAtivoAcao = Action.actionIcon("label.excluir", Icones.EXCLUIR);
		private Action base64Acao = actionIcon("label.criar_base64", Icones.BOLA_AMARELA);
		private Action modeloAcao = Action.actionIcon("label.modelo", Icones.BOLA_VERDE);
		private Action atualizarAcao = Action.actionIcon("label.requisicao", Icones.URL);

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			addButton(true, atualizarAcao);
			addButton(true, formatarAcao);
			addButton(modeloAcao);
			addButton(true, base64Acao);
			addButton(retornar64Acao);
			addButton(true, variaveisAcao);
			eventos();
		}

		private void eventos() {
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
			retornar64Acao.setActionListener(e -> retornar64());
			variaveisAcao.setActionListener(e -> variaveis());
			atualizarAcao.setActionListener(e -> atualizar());
			formatarAcao.setActionListener(e -> formatar());
			base64Acao.setActionListener(e -> base64());
			modeloAcao.setActionListener(e -> modelo());
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

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		@Override
		protected void novo() {
			Object resp = Util.getValorInputDialog(RequisicaoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(RequisicaoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					RequisicaoPagina pagina = new RequisicaoPagina(f);
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

		private void formatar() {
			RequisicaoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.formatar();
			}
		}

		private void modelo() {
			Component comp = Util.getViewParent(RequisicaoContainer.this);
			ParserDialogo form = null;
			if (comp instanceof Frame) {
				form = ParserDialogo.criar((Frame) comp, parserListener);
				Util.configSizeLocation((Frame) comp, form, RequisicaoContainer.this);
			} else if (comp instanceof Dialog) {
				form = ParserDialogo.criar((Dialog) comp, parserListener);
				Util.configSizeLocation((Dialog) comp, form, RequisicaoContainer.this);
			} else {
				form = ParserDialogo.criar((Dialog) null, parserListener);
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

	private transient ParserListener parserListener = new ParserListener() {
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