package br.com.persist.plugins.legado;

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
import br.com.persist.componente.FicharioPesquisa;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class LegadoContainer extends AbstratoContainer implements PluginFichario {
	private static final File file = new File(LegadoConstantes.LEGADO);
	private final LegadoFichario fichario = new LegadoFichario();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private LegadoFormulario legadoFormulario;
	private LegadoDialogo legadoDialogo;

	public LegadoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
	}

	public LegadoDialogo getLegadoDialogo() {
		return legadoDialogo;
	}

	public void setLegadoDialogo(LegadoDialogo legadoDialogo) {
		this.legadoDialogo = legadoDialogo;
		if (legadoDialogo != null) {
			legadoFormulario = null;
		}
	}

	public LegadoFormulario getLegadoFormulario() {
		return legadoFormulario;
	}

	public void setLegadoFormulario(LegadoFormulario legadoFormulario) {
		this.legadoFormulario = legadoFormulario;
		if (legadoFormulario != null) {
			legadoDialogo = null;
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
		LegadoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		LegadoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservado(String nome) {
		return LegadoConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(LegadoConstantes.LEGADO, new File(file, LegadoConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				files = ArquivoUtil.ordenar(files);
				List<LegadoPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if ((ehArquivoReservado(f.getName()) && !LegadoPreferencia.isExibirArqIgnorados())
							|| ArquivoUtil.contem(LegadoConstantes.LEGADO, f.getName())) {
						continue;
					}
					ordenados.add(new LegadoPagina(f));
				}
				for (LegadoPagina pagina : ordenados) {
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
			chkPsqConteudo.setTag("FICHARIO");
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
					Util.mensagem(LegadoContainer.this, getString(set));
				} else {
					pesquisa = getPesquisa(fichario, pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
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

		public FicharioPesquisa getPesquisa(LegadoFichario fichario, FicharioPesquisa pesquisa, String string,
				boolean porParte) {
			if (pesquisa == null) {
				return new FicharioPesquisa(fichario, string, porParte);
			} else if (pesquisa.igual(string, porParte)) {
				return pesquisa;
			}
			return new FicharioPesquisa(fichario, string, porParte);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(LegadoContainer.this)) {
				LegadoFormulario.criar(formulario, LegadoContainer.this);
			} else if (legadoDialogo != null) {
				legadoDialogo.excluirContainer();
				LegadoFormulario.criar(formulario, LegadoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (legadoFormulario != null) {
				legadoFormulario.excluirContainer();
				formulario.adicionarPagina(LegadoContainer.this);
			} else if (legadoDialogo != null) {
				legadoDialogo.excluirContainer();
				formulario.adicionarPagina(LegadoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (legadoDialogo != null) {
				legadoDialogo.excluirContainer();
			}
			LegadoFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (legadoDialogo != null) {
				legadoDialogo.excluirContainer();
			}
			LegadoFormulario.criar(formulario, null, null);
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
			Object resp = Util.getValorInputDialog(LegadoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservado(nome)) {
				Util.mensagem(LegadoContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(LegadoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					LegadoPagina pagina = new LegadoPagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(LegadoConstantes.PAINEL_LEGADO, ex, LegadoContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			LegadoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(LegadoPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			LegadoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(LegadoContainer.this,
					LegadoMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
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
		LegadoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return LegadoFabrica.class;
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
				return LegadoMensagens.getString(LegadoConstantes.LABEL_LEGADO_MIN);
			}

			@Override
			public String getTitulo() {
				return LegadoMensagens.getString(LegadoConstantes.LABEL_LEGADO);
			}

			@Override
			public String getHint() {
				return LegadoMensagens.getString(LegadoConstantes.LABEL_LEGADO);
			}

			@Override
			public Icon getIcone() {
				return Icones.REFERENCIA;
			}
		};
	}
}
