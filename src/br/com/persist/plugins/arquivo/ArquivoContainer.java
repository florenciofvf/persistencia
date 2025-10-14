package br.com.persist.plugins.arquivo;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginArquivo;
import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoPesquisa;
import br.com.persist.arquivo.ArquivoTreeUtil;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.FicharioEvento;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class ArquivoContainer extends AbstratoContainer implements ArquivoTreeListener, PluginArquivo {
	public static final File file = new File(ArquivoConstantes.ARQUIVOS);
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private ArquivoFormulario arquivoFormulario;
	private final ArquivoTreeExt arquivoTree;

	public ArquivoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		File ignorados = new File(file, ArquivoConstantes.IGNORADOS);
		Arquivo raiz = new Arquivo(file, ArquivoUtil.lerArquivo(ignorados));
		arquivoTree = new ArquivoTreeExt(new ArquivoModelo(raiz));
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
		arquivoTree.adicionarOuvinteExt(this);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action fecharAcao = actionIcon("label.fechar_todos", Icones.FECHAR);
		private Action statusAcao = actionIcon("label.abertos", Icones.HIERARQUIA);
		private final CheckBox chkSempreTopForm = new CheckBox();
		private final CheckBox chkSempreTopArq = new CheckBox();
		private final CheckBox chkDuplicar = new CheckBox();
		private final CheckBox chkLinkAuto = new CheckBox();
		private static final long serialVersionUID = 1L;
		private transient ArquivoPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR);
			chkSempreTopArq.setToolTipText(ArquivoMensagens.getString("msg.arquivo.sempreTopArqu"));
			chkSempreTopForm.setToolTipText(Mensagens.getString("msg.arquivo.sempreTopForm"));
			chkLinkAuto.setToolTipText(ArquivoMensagens.getString("msg.arquivo.link_auto"));
			chkDuplicar.setToolTipText(ArquivoMensagens.getString("msg.arquivo.duplicar"));
			chkLinkAuto.setSelected(true);
			addButton(statusAcao);
			add(chkSempreTopArq);
			add(chkSempreTopForm);
			add(chkLinkAuto);
			add(chkDuplicar);
			add(fecharAcao);
			add(txtPesquisa);
			add(chkPorParte);
			add(chkPsqConteudo);
			add(label);
			eventos();
		}

		private void eventos() {
			chkSempreTopArq.addActionListener(e -> arquivoFormulario.setAlwaysOnTop(chkSempreTopArq.isSelected()));
			fecharAcao.setActionListener(e -> formulario.fecharTodos());
			chkSempreTopForm.addActionListener(e -> topFormulario());
			statusAcao.setActionListener(e -> statusArquivo());
			txtPesquisa.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					arquivoTree.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(ArquivoContainer.this, Util.getString(set));
				} else {
					pesquisa = getPesquisa(arquivoTree, pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		public ArquivoPesquisa getPesquisa(ArquivoTreeExt arquivoTree, ArquivoPesquisa pesquisa, String string,
				boolean porParte) {
			if (pesquisa == null) {
				return new ArquivoPesquisa(arquivoTree, string, porParte);
			} else if (pesquisa.igual(string, porParte)) {
				return pesquisa;
			}
			return new ArquivoPesquisa(arquivoTree, string, porParte);
		}

		private void topFormulario() {
			formulario.setAlwaysOnTop(chkSempreTopForm.isSelected());
			if (chkSempreTopForm.isSelected()) {
				formulario.setExtendedState(Frame.MAXIMIZED_BOTH);
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

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
			chkSempreTopArq.setEnabled(arquivoFormulario != null);
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
			chkSempreTopArq.setEnabled(arquivoFormulario != null);
		}

		@Override
		public void baixar() {
			ArquivoModelo modelo = new ArquivoModelo();
			arquivoTree.setModel(modelo);
			baixar(modelo);
			pesquisa = null;
			label.limpar();
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
	public void abrirArquivoFormulario(ArquivoTreeExt arauivoTree) {
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
	public void processar(Formulario formulario, Map<String, Object> args) {
		Pagina pagina = (Pagina) args.get(FicharioEvento.PAGINA_SELECIONADA);
		if (pagina == null || pagina == this || pagina.getFile() == null || !toolbar.chkLinkAuto.isSelected()) {
			return;
		}
		File fileSel = pagina.getFile();
		arquivoTree.selecionar(fileSel);
	}

	@Override
	public void conteudoArquivo(ArquivoTreeExt arquivoTree) {
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
	public void clonarArquivo(ArquivoTreeExt arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			clonar(arquivoTree, arquivo);
		}
	}

	private void clonar(ArquivoTreeExt arquivoTree, Arquivo arquivo) {
		try {
			AtomicReference<File> ref = new AtomicReference<>();
			String resp = Util.clonar(ArquivoContainer.this, arquivo.getFile(), ref);
			if (Preferencias.isExibirTotalBytesClonados()) {
				Util.mensagem(ArquivoContainer.this, resp);
			}
			adicionar(arquivoTree, arquivo.getPai(), ref.get());
		} catch (IOException e) {
			Util.mensagem(ArquivoContainer.this, e.getMessage());
		}
	}

	private void adicionar(ArquivoTreeExt arquivoTree, Arquivo arquivo, File file) {
		if (file != null && arquivo != null) {
			Arquivo novo = arquivo.adicionar(file);
			if (novo != null) {
				ArquivoTreeUtil.atualizarEstrutura(arquivoTree, arquivo);
				requestFocus();
				ArquivoTreeUtil.selecionarObjeto(arquivoTree, novo);
				arquivoTree.repaint();
			}
		}
	}

	@Override
	public void focusInputPesquisar(ArquivoTreeExt arquivoTree) {
		toolbar.focusInputPesquisar();
	}

	@Override
	public void diretorioArquivo(ArquivoTreeExt arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			desktopOpen(arquivo);
		}
	}

	private void desktopOpen(Arquivo arquivo) {
		try {
			ArquivoUtil.diretorio(arquivo.getFile());
		} catch (IOException e) {
			Util.mensagem(ArquivoContainer.this, e.getMessage());
		}
	}

	@Override
	public void abrirArquivoFichario(ArquivoTreeExt arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			if (toolbar.chkDuplicar.isSelected()) {
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
	public void selecionarArquivo(ArquivoTreeExt arquivoTree) {
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
	public void clickArquivo(ArquivoTreeExt arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null && toolbar.chkLinkAuto.isSelected()) {
			formulario.selecionarPagina(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
			if (arquivo.isFile()) {
				formulario.toFront();
			}
		}
	}

	@Override
	public void excluirArquivo(ArquivoTreeExt arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		File root = ArquivoTreeUtil.getRoot(this.arquivoTree);
		if (arquivo != null && arquivo.getPai() != null && root != null && !root.equals(arquivo.getFile())
				&& Util.confirmaExclusao(ArquivoContainer.this, false)) {
			arquivo.excluir();
			ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
		}
	}

	@Override
	public void atualizarArquivo(ArquivoTreeExt arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
		}
	}

	@Override
	public void fecharArquivo(ArquivoTreeExt arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			formulario.fecharArquivo(arquivo.getFile());
			arquivo.setArquivoAberto(formulario.isAberto(arquivo.getFile()));
			ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
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
				return ArquivoMensagens.getString(ArquivoConstantes.LABEL_ARQUIVOS_MIN);
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