package br.com.persist.plugins.atributo;

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

public class AtributoContainer extends AbstratoContainer implements PluginFichario {
	private static final File file = new File(AtributoConstantes.ATRIBUTO);
	private final AtributoFichario fichario = new AtributoFichario();
	private static final long serialVersionUID = 1L;
	private AtributoFormulario atributoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private AtributoDialogo atributoDialogo;

	public AtributoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo, idPagina);
	}

	public AtributoDialogo getAtributoDialogo() {
		return atributoDialogo;
	}

	public void setAtributoDialogo(AtributoDialogo atributoDialogo) {
		this.atributoDialogo = atributoDialogo;
		if (atributoDialogo != null) {
			atributoFormulario = null;
		}
	}

	public AtributoFormulario getAtributoFormulario() {
		return atributoFormulario;
	}

	public void setAtributoFormulario(AtributoFormulario atributoFormulario) {
		this.atributoFormulario = atributoFormulario;
		if (atributoFormulario != null) {
			atributoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
		fichario.setListener(e -> toolbar.focusInputPesquisar());
	}

	public String getConteudo() {
		AtributoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getConteudo();
		}
		return null;
	}

	public String getIdPagina() {
		AtributoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return null;
	}

	public int getIndice() {
		return fichario.getIndiceAtivo();
	}

	static boolean ehArquivoReservado(String nome) {
		return AtributoConstantes.IGNORADOS.equalsIgnoreCase(nome);
	}

	private void abrir(String conteudo, String idPagina) {
		ArquivoUtil.lerArquivo(AtributoConstantes.ATRIBUTO, new File(file, AtributoConstantes.IGNORADOS));
		fichario.excluirPaginas();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				files = ArquivoUtil.ordenar(files);
				List<AtributoPagina> ordenados = new ArrayList<>();
				for (File f : files) {
					if ((ehArquivoReservado(f.getName()) && !AtributoPreferencia.isExibirArqIgnorados())
							|| ArquivoUtil.contem(AtributoConstantes.ATRIBUTO, f.getName())) {
						continue;
					}
					ordenados.add(new AtributoPagina(f));
				}
				for (AtributoPagina pagina : ordenados) {
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
					Util.mensagem(AtributoContainer.this, getString(set));
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

		public FicharioPesquisa getPesquisa(AtributoFichario fichario, FicharioPesquisa pesquisa, String string,
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
			if (formulario.excluirPagina(AtributoContainer.this)) {
				AtributoFormulario.criar(formulario, AtributoContainer.this);
			} else if (atributoDialogo != null) {
				atributoDialogo.excluirContainer();
				AtributoFormulario.criar(formulario, AtributoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (atributoFormulario != null) {
				atributoFormulario.excluirContainer();
				formulario.adicionarPagina(AtributoContainer.this);
			} else if (atributoDialogo != null) {
				atributoDialogo.excluirContainer();
				formulario.adicionarPagina(AtributoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (atributoDialogo != null) {
				atributoDialogo.excluirContainer();
			}
			AtributoFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (atributoDialogo != null) {
				atributoDialogo.excluirContainer();
			}
			AtributoFormulario.criar(formulario, null, null);
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
			Object resp = Util.getValorInputDialog(AtributoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return;
			}
			String nome = resp.toString();
			if (ehArquivoReservado(nome)) {
				Util.mensagem(AtributoContainer.this, Mensagens.getString("label.indentificador_reservado"));
				return;
			}

			File f = new File(file, nome);
			if (f.exists()) {
				Util.mensagem(AtributoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}
			try {
				if (f.createNewFile()) {
					AtributoPagina pagina = new AtributoPagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(AtributoConstantes.PAINEL_ATRIBUTO, ex, AtributoContainer.this);
			}
		}

		@Override
		protected void baixar() {
			abrir(null, getIdPagina());
		}

		@Override
		protected void salvar() {
			AtributoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				salvar(ativa);
			}
		}

		private void salvar(AtributoPagina ativa) {
			AtomicBoolean atomic = new AtomicBoolean(false);
			ativa.salvar(atomic);
			if (atomic.get()) {
				salvoMensagem();
			}
		}

		private void excluirAtivo() {
			AtributoPagina ativa = fichario.getPaginaAtiva();
			if (ativa != null && Util.confirmar(AtributoContainer.this,
					AtributoMensagens.getString("msg.confirmar_excluir_ativa"), false)) {
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
		AtributoPagina ativa = fichario.getPaginaAtiva();
		if (ativa != null) {
			return ativa.getNome();
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return AtributoFabrica.class;
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
				return AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO_MIN);
			}

			@Override
			public String getTitulo() {
				return AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO);
			}

			@Override
			public String getHint() {
				return AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO);
			}

			@Override
			public Icon getIcone() {
				return Icones.REGION;
			}
		};
	}
}