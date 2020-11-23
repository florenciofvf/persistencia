package br.com.persist.plugins.arquivo;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class ArquivoContainer extends AbstratoContainer implements ArquivoTreeListener {
	private static final long serialVersionUID = 1L;
	private final ArquivoTree arquivoTree = new ArquivoTree(new ArquivoModelo());
	private final CheckBox chkDuplicar = new CheckBox();
	private final CheckBox chkLinkAuto = new CheckBox();
	private final Toolbar toolbar = new Toolbar();
	private ArquivoFormulario arquivoFormulario;

	public ArquivoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public ArquivoFormulario getArquivoFormulario() {
		return arquivoFormulario;
	}

	public void setArquivoFormulario(ArquivoFormulario arquivoFormulario) {
		this.arquivoFormulario = arquivoFormulario;
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(arquivoTree));
	}

	private void configurar() {
		arquivoTree.adicionarOuvinte(this);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action fecharAcao = Action.actionIcon("label.fechar_todos", Icones.FECHAR);
		private Action statusAcao = Action.actionIcon("label.abertos", Icones.HIERARQUIA);
		private final CheckBox chkSempreTopForm = new CheckBox();
		private final CheckBox chkSempreTopArq = new CheckBox();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR);
			chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
			chkSempreTopArq.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopArqu"));
			chkLinkAuto.setToolTipText(Mensagens.getString("msg.arquivo.link_auto"));
			chkDuplicar.setToolTipText(Mensagens.getString("msg.arquivo.duplicar"));
			addButton(statusAcao);
			add(chkSempreTopArq);
			add(chkSempreTopForm);
			add(chkLinkAuto);
			add(chkDuplicar);
			add(fecharAcao);
			chkLinkAuto.setSelected(true);
			eventos();
		}

		private void eventos() {
			chkSempreTopArq.addActionListener(e -> arquivoFormulario.setAlwaysOnTop(chkSempreTopArq.isSelected()));
			fecharAcao.setActionListener(e -> formulario.fecharTodos());
			chkSempreTopForm.addActionListener(e -> topFormulario());
			statusAcao.setActionListener(e -> statusArquivo());
		}

		private void topFormulario() {
			formulario.setAlwaysOnTop(chkSempreTopForm.isSelected());
			if (chkSempreTopForm.isSelected()) {
				formulario.setExtendedState(Formulario.MAXIMIZED_BOTH);
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(ArquivoContainer.this)) {
				ArquivoFormulario.criar(formulario, ArquivoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (arquivoFormulario != null) {
				arquivoFormulario.excluirContainer();
				formulario.adicionarPagina(ArquivoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			ArquivoFormulario.criar(formulario);
		}

		void formularioVisivel() {
			buttonDestacar.estadoFormulario();
			chkSempreTopArq.setEnabled(arquivoFormulario != null);
		}

		void paginaVisivel() {
			buttonDestacar.estadoFichario();
			chkSempreTopArq.setEnabled(arquivoFormulario != null);
		}

		@Override
		public void baixar() {
			ArquivoModelo modelo = new ArquivoModelo();
			arquivoTree.setModel(modelo);
			baixar(modelo);
		}

		private void baixar(ArquivoModelo modelo) {
			List<Arquivo> lista = new ArrayList<>();
			modelo.listar(lista);
			for (Arquivo arquivo : lista) {
				arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			}
		}

		private void statusArquivo() {
			ArquivoModelo modelo = (ArquivoModelo) arquivoTree.getModel();
			List<Arquivo> lista = statusArquivo(modelo);
			statusArquivoSelecionar(lista);
		}

		private List<Arquivo> statusArquivo(ArquivoModelo modelo) {
			List<Arquivo> lista = new ArrayList<>();
			modelo.listar(lista);
			for (Arquivo arquivo : lista) {
				arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
				if (arquivo.isArquivoAberto()) {
					ArquivoTreeUtil.selecionarObjeto(arquivoTree, arquivo);
				} else {
					ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
				}
			}
			return lista;
		}

		private void statusArquivoSelecionar(List<Arquivo> lista) {
			arquivoTree.clearSelection();
			for (Arquivo arquivo : lista) {
				if (formulario.isAtivo(arquivo.getFile())) {
					ArquivoTreeUtil.selecionarObjeto(arquivoTree, arquivo);
				}
			}
		}
	}

	@Override
	public void abrirArquivoFormulario(ArquivoTree arauivoTree) {
		Arquivo arquivo = arauivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			formulario.processar(criarArgs(arquivo.getFile(), false));
		}
	}

	private Map<String, Object> criarArgs(File file, Boolean fichario) {
		Map<String, Object> args = new HashMap<>();
		args.put(ArquivoEvento.ABRIR_ARQUIVO, file);
		args.put(ArquivoEvento.FICHARIO, fichario);
		return args;
	}

	@Override
	public void conteudoArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			conteudo(arquivo);
		}
	}

	private void conteudo(Arquivo arquivo) {
		try {
			Util.conteudo(ArquivoContainer.this, arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(ArquivoContainer.this, e.getMessage());
		}
	}

	@Override
	public void diretorioArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			desktopOpen(arquivo);
		}
	}

	private void desktopOpen(Arquivo arquivo) {
		try {
			processar(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(ArquivoContainer.this, e.getMessage());
		}
	}

	private void processar(File file) throws IOException {
		if (Util.isMac()) {
			Runtime.getRuntime().exec("open -R " + file.getAbsolutePath());
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(parent);
			}
		}
	}

	@Override
	public void abrirArquivoFichario(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			if (chkDuplicar.isSelected()) {
				formulario.processar(criarArgs(arquivo.getFile(), true));
			} else {
				if (!formulario.isAberto(arquivo.getFile())) {
					formulario.processar(criarArgs(arquivo.getFile(), true));
				}
			}
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			formulario.selecionarPagina(arquivo.getFile());
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
		}
	}

	@Override
	public void selecionarArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			formulario.selecionarPagina(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
			if (arquivo.isFile()) {
				formulario.toFront();
			}
		}
	}

	@Override
	public void clickArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null && chkLinkAuto.isSelected()) {
			formulario.selecionarPagina(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
			if (arquivo.isFile()) {
				formulario.toFront();
			}
		}
	}

	@Override
	public void excluirArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		File root = ArquivoTreeUtil.getRoot(this.arquivoTree);
		if (arquivo != null && arquivo.getPai() != null && root != null && !root.equals(arquivo.getFile())
				&& Util.confirmaExclusao(ArquivoContainer.this, false)) {
			arquivo.excluir();
			ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
		}
	}

	@Override
	public void atualizarArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
		}
	}

	@Override
	public void fecharArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			formulario.fecharArquivo(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ArquivoFabrica.class;
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
				return Mensagens.getString(Constantes.LABEL_ARQUIVOS_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_ARQUIVOS);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_ARQUIVOS);
			}

			@Override
			public Icon getIcone() {
				return Icones.EXPANDIR;
			}
		};
	}
}