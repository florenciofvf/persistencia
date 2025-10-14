package br.com.persist.plugins.sistema;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginArquivo;
import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoPesquisa;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoTreeUtil;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class SistemaContainer extends AbstratoContainer implements ArquivoTreeListener, PluginArquivo {
	private final ArquivoTree arquivoTree = new ArquivoTree(new ArquivoModelo());
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private SistemaFormulario sistemaFormulario;
	private SistemaDialogo sistemaDialogo;

	public SistemaContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public SistemaDialogo getSistemaDialogo() {
		return sistemaDialogo;
	}

	public void setSistemaDialogo(SistemaDialogo sistemaDialogo) {
		this.sistemaDialogo = sistemaDialogo;
		if (sistemaDialogo != null) {
			sistemaFormulario = null;
		}
	}

	public SistemaFormulario getSistemaFormulario() {
		return sistemaFormulario;
	}

	public void setSistemaFormulario(SistemaFormulario sistemaFormulario) {
		this.sistemaFormulario = sistemaFormulario;
		if (sistemaFormulario != null) {
			sistemaDialogo = null;
		}
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

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient ArquivoPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(chkPorParte);
			add(chkPsqConteudo);
			add(label);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					arquivoTree.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(SistemaContainer.this, Util.getString(set));
				} else {
					pesquisa = arquivoTree.getPesquisa(pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(SistemaContainer.this)) {
				SistemaFormulario.criar(formulario, SistemaContainer.this);
			} else if (sistemaDialogo != null) {
				sistemaDialogo.excluirContainer();
				SistemaFormulario.criar(formulario, SistemaContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (sistemaFormulario != null) {
				sistemaFormulario.excluirContainer();
				formulario.adicionarPagina(SistemaContainer.this);
			} else if (sistemaDialogo != null) {
				sistemaDialogo.excluirContainer();
				formulario.adicionarPagina(SistemaContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (sistemaDialogo != null) {
				sistemaDialogo.excluirContainer();
			}
			SistemaFormulario.criar(formulario);
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
		protected void baixar() {
			ArquivoModelo modelo = new ArquivoModelo();
			arquivoTree.setModel(modelo);
			pesquisa = null;
			label.limpar();
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
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return SistemaFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void focusInputPesquisar(ArquivoTree arquivoTree) {
		toolbar.focusInputPesquisar();
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
			ArquivoUtil.diretorio(arquivo.getFile());
		} catch (IOException ex) {
			Util.mensagem(SistemaContainer.this, ex.getMessage());
		}
	}

	@Override
	public void renomearArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			String nome = ArquivoUtil.getNome(SistemaContainer.this, arquivo.getName());
			if (nome != null && arquivo.renomear(nome)) {
				ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo);
			}
		}
	}

	@Override
	public void excluirArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null && Util.confirmar(SistemaContainer.this, "msg.confirma_exclusao")) {
			arquivo.excluir();
			ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo);
		}
	}

	@Override
	public void novoDiretorio(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (valido(arquivo)) {
			File file = ArquivoUtil.novoDiretorio(SistemaContainer.this, arquivo.getFile());
			adicionar(arquivoTree, arquivo, file);
		}
	}

	private boolean valido(Arquivo arquivo) {
		return arquivo != null && arquivo.isDirectory();
	}

	private void adicionar(ArquivoTree arquivoTree, Arquivo arquivo, File file) {
		if (file != null && arquivo != null) {
			Arquivo novo = arquivo.adicionar(file);
			if (novo != null) {
				arquivo.ordenar();
				ArquivoTreeUtil.atualizarEstrutura(arquivoTree, arquivo);
				requestFocus();
				ArquivoTreeUtil.selecionarObjeto(arquivoTree, novo);
				arquivoTree.repaint();
			}
		}
	}

	@Override
	public void clonarArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			clonar(arquivoTree, arquivo);
		}
	}

	private void clonar(ArquivoTree arquivoTree, Arquivo arquivo) {
		try {
			AtomicReference<File> ref = new AtomicReference<>();
			String resp = Util.clonar(SistemaContainer.this, arquivo.getFile(), ref);
			if (Preferencias.isExibirTotalBytesClonados()) {
				Util.mensagem(SistemaContainer.this, resp);
			}
			adicionar(arquivoTree, arquivo.getPai(), ref.get());
		} catch (IOException ex) {
			Util.mensagem(SistemaContainer.this, ex.getMessage());
		}
	}

	@Override
	public void abrirArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (arquivo != null) {
			Util.abrir(SistemaContainer.this, arquivo.getFile());
		}
	}

	@Override
	public void novoArquivo(ArquivoTree arquivoTree) {
		Arquivo arquivo = arquivoTree.getObjetoSelecionado();
		if (valido(arquivo)) {
			File file = ArquivoUtil.novoArquivo(SistemaContainer.this, arquivo.getFile());
			adicionar(arquivoTree, arquivo, file);
		}
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return SistemaMensagens.getString(SistemaConstantes.LABEL_SISTEMA_MIN);
			}

			@Override
			public String getTitulo() {
				return SistemaMensagens.getString(SistemaConstantes.LABEL_SISTEMA);
			}

			@Override
			public String getHint() {
				return SistemaMensagens.getString(SistemaConstantes.LABEL_SISTEMA);
			}

			@Override
			public Icon getIcone() {
				return Icones.FIELDS;
			}
		};
	}
}