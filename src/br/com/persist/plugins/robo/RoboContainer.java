package br.com.persist.plugins.robo;

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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginFichario;
import br.com.persist.assistencia.ArquivoUtil;
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

public class RoboContainer extends AbstratoContainer implements PluginFichario {
	private static final File file = new File(RoboConstantes.ROBOSCRIPTS);
	private final RoboFichario fichario = new RoboFichario();
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private RoboFormulario roboFormulario;
	private RoboDialogo roboDialogo;

	public RoboContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
	}

	public RoboDialogo getRoboDialogo() {
		return roboDialogo;
	}

	public void setRoboDialogo(RoboDialogo roboDialogo) {
		this.roboDialogo = roboDialogo;
		if (roboDialogo != null) {
			roboFormulario = null;
		}
	}

	public RoboFormulario getRoboFormulario() {
		return roboFormulario;
	}

	public void setRoboFormulario(RoboFormulario roboFormulario) {
		this.roboFormulario = roboFormulario;
		if (roboFormulario != null) {
			roboDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
		fichario.setListener(e -> toolbar.focusInputPesquisar());
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	public String getConteudo() {
		RoboPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		RoboPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservado(String nome) {
		return RoboConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(RoboConstantes.ROBOSCRIPTS, new File(file, RoboConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				files = ArquivoUtil.ordenar(files);
				List<RoboPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if ((ehArquivoReservado(f.getName()) && !RoboPreferencia.isExibirArqIgnorados())
							|| ArquivoUtil.contem(RoboConstantes.ROBOSCRIPTS, f.getName())) {
						continue;
					}
					ordenados.add(new RoboPagina(f, formulario));
				}
				for (RoboPagina pagina : ordenados) {
					fichario.adicionarPagina(pagina);
				}
			}
		}
		fichario.setConteudo(conteudo, idPagina);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action executarAcao = acaoIcon("label.executar_todos", Icones.EXECUTAR);
		private Action excluirAtivoAcao = actionIconExcluir();
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			executarAcao.setActionListener(e -> new Thread(this::executar).start());
			addButton(excluirAtivoAcao);
			addButton(executarAcao);
			add(txtPesquisa);
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
			txtPesquisa.addActionListener(this);
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(RoboMensagens.getString(chave), icon);
		}

		private void executar() {
			try {
				fichario.executarTodos();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, e.getMessage());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				Set<String> set = new LinkedHashSet<>();
				fichario.contemConteudo(set, txtPesquisa.getText());
				Util.mensagem(RoboContainer.this, getString(set));
			}
		}

		private String getString(Set<String> set) {
			StringBuilder sb = new StringBuilder();
			for (String string : set) {
				if (sb.length() > 0) {
					sb.append(Constantes.QL);
				}
				sb.append(string);
			}
			return sb.toString();
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(RoboContainer.this)) {
				RoboFormulario.criar(formulario, RoboContainer.this);
			} else if (roboDialogo != null) {
				roboDialogo.excluirContainer();
				RoboFormulario.criar(formulario, RoboContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (roboFormulario != null) {
				roboFormulario.excluirContainer();
				formulario.adicionarPagina(RoboContainer.this);
			} else if (roboDialogo != null) {
				roboDialogo.excluirContainer();
				formulario.adicionarPagina(RoboContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (roboDialogo != null) {
				roboDialogo.excluirContainer();
			}
			RoboFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (roboDialogo != null) {
				roboDialogo.excluirContainer();
			}
			RoboFormulario.criar(formulario, null, null);
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
			Object resp = Util.getValorInputDialog(RoboContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservado(nome)) {
				Util.mensagem(RoboContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(RoboContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					RoboPagina pagina = new RoboPagina(f, formulario);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(RoboConstantes.PAINEL_ROBO, ex, RoboContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			RoboPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(RoboPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			RoboPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(RoboContainer.this,
					RoboMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
				int indice = fichario.getSelectedIndex();
				ativa.excluir();
				fichario.remove(indice);
			}
		}
	}

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
		RoboPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return RoboFabrica.class;
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
				return RoboMensagens.getString(RoboConstantes.LABEL_ROBO_MIN);
			}

			@Override
			public String getTitulo() {
				return RoboMensagens.getString(RoboConstantes.LABEL_ROBO);
			}

			@Override
			public String getHint() {
				return RoboMensagens.getString(RoboConstantes.LABEL_ROBO);
			}

			@Override
			public Icon getIcone() {
				return Icones.CONFIG;
			}
		};
	}
}