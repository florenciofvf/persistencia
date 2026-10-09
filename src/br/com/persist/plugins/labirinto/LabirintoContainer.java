package br.com.persist.plugins.labirinto;

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
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginFichario;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.FicharioPesquisa;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class LabirintoContainer extends AbstratoContainer implements PluginFichario {
	private static final File file = new File(LabirintoConstantes.LABIRINTO);
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private LabirintoFormulario labirintoFormulario;
	private final LabirintoFichario fichario;
	private LabirintoDialogo labirintoDialogo;

	public LabirintoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		fichario = new LabirintoFichario(this);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
	}

	public LabirintoDialogo getLabirintoDialogo() {
		return labirintoDialogo;
	}

	public void setLabirintoDialogo(LabirintoDialogo labirintoDialogo) {
		this.labirintoDialogo = labirintoDialogo;
		if (labirintoDialogo != null) {
			labirintoFormulario = null;
		}
	}

	public LabirintoFormulario getLabirintoFormulario() {
		return labirintoFormulario;
	}

	public void setLabirintoFormulario(LabirintoFormulario labirintoFormulario) {
		this.labirintoFormulario = labirintoFormulario;
		if (labirintoFormulario != null) {
			labirintoDialogo = null;
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
		LabirintoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		LabirintoPagina ativa = fichario.getPaginaAtiva();
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

	static boolean ehArquivoReservado(String nome) {
		return LabirintoConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerIgnorados(LabirintoConstantes.LABIRINTO, new File(file, LabirintoConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				files = ArquivoUtil.ordenar(files);
				List<LabirintoPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if ((ehArquivoReservado(f.getName()) && !LabirintoPreferencia.isExibirArqIgnorados())
							|| ArquivoUtil.contem(LabirintoConstantes.LABIRINTO, f.getName())) {
						continue;
					}
					ordenados.add(new LabirintoPagina(fichario, f));
				}
				for (LabirintoPagina pagina : ordenados) {
					fichario.adicionarPagina(pagina);
				}
			}
		}
		fichario.setConteudo(conteudo, idPagina);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action excluirAtivoAcao = actionIconExcluir();
		private static final long serialVersionUID = 1L;
		private transient FicharioPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, SALVAR);
			addButton(excluirAtivoAcao);
			add(txtPesquisa);
			add(chkPorParte);
			chkPsqConteudo.setTag(Constantes.FICHARIO);
			add(chkPsqConteudo);
			add(label);
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
			txtPesquisa.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					fichario.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(LabirintoContainer.this, Util.getString(set));
				} else {
					pesquisa = fichario.getPesquisa(pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(LabirintoContainer.this)) {
				LabirintoFormulario.criar(formulario, LabirintoContainer.this);
			} else if (labirintoDialogo != null) {
				labirintoDialogo.excluirContainer();
				LabirintoFormulario.criar(formulario, LabirintoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (labirintoFormulario != null) {
				labirintoFormulario.excluirContainer();
				formulario.adicionarPagina(LabirintoContainer.this);
			} else if (labirintoDialogo != null) {
				labirintoDialogo.excluirContainer();
				formulario.adicionarPagina(LabirintoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (labirintoDialogo != null) {
				labirintoDialogo.excluirContainer();
			}
			LabirintoFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (labirintoDialogo != null) {
				labirintoDialogo.excluirContainer();
			}
			LabirintoFormulario.criar(formulario, null, null);
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
			Object resp = Util.getValorInputDialog(LabirintoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservado(nome)) {
				Util.mensagem(LabirintoContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(LabirintoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					LabirintoPagina pagina = new LabirintoPagina(fichario, f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(LabirintoConstantes.PAINEL_LABIRINTO, ex, LabirintoContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			LabirintoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(LabirintoPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			LabirintoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(LabirintoContainer.this,
					LabirintoMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
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
		LabirintoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return LabirintoFabrica.class;
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
				return LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO_MIN);
			}

			@Override
			public String getTitulo() {
				return LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO);
			}

			@Override
			public String getHint() {
				return LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO);
			}

			@Override
			public Icone getIcone() {
				return Icones.VERTICAL;
			}
		};
	}
}