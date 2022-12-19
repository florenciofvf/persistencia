package br.com.persist.plugins.anotacao;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.swing.SwingUtilities;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.SplitPane;
import br.com.persist.painel.Fichario;
import br.com.persist.painel.Separador;
import br.com.persist.painel.Transferivel;

class AnotacaoSplit extends SplitPane {
	private static final long serialVersionUID = 1L;
	private final File fileRoot;
	private ArquivoTree tree;
	private PanelRoot panel;

	AnotacaoSplit() {
		super(HORIZONTAL_SPLIT);
		fileRoot = new File(AnotacaoConstantes.ANOTACOES);
	}

	void inicializar() {
		File ignore = new File(fileRoot, "ignore");
		List<String> ignorados = ArquivoUtil.getIgnorados(ignore);
		Arquivo raiz = new Arquivo(fileRoot, ignorados);
		tree = new ArquivoTree(new ArquivoModelo(raiz));
		tree.adicionarOuvinte(treeListener);
		panel = new PanelRoot();
		setLeftComponent(tree);
		setRightComponent(panel);
	}

	void abrir(Arquivo arquivo) {
		if (arquivo == null) {
			return;
		}
		Fichario fichario = panel.getFicharioSelecionado();
		if (fichario != null) {
			novaAba(fichario, arquivo);
		} else {
			fichario = panel.getFicharioPrimeiro();
			if (fichario != null) {
				novaAba(fichario, arquivo);
			} else {
				fichario = novoFichario(arquivo);
				panel.setRoot(fichario);
			}
		}
		SwingUtilities.updateComponentTreeUI(panel);
	}

	private Fichario novoFichario(Arquivo arquivo) {
		Fichario fichario = new Fichario();
		novaAba(fichario, arquivo);
		return fichario;
	}

	private void novaAba(Fichario fichario, Arquivo arquivo) {
		fichario.addTab(arquivo.getName(), new Aba(arquivo));
	}

	public ArquivoTree getTree() {
		return tree;
	}

	private ArquivoTreeListener treeListener = new ArquivoTreeListener() {
		@Override
		public void renomearArquivo(ArquivoTree arquivoTree) {
		}

		@Override
		public void excluirArquivo(ArquivoTree arquivoTree) {
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
			} catch (IOException e) {
				Util.mensagem(AnotacaoSplit.this, e.getMessage());
			}
		}

		@Override
		public void abrirArquivo(ArquivoTree arquivoTree) {
			abrir(arquivoTree.getObjetoSelecionado());
		}

		@Override
		public void novoDiretorio(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && ArquivoUtil.novoDiretorio(AnotacaoSplit.this, arquivo.getFile())) {
				inicializar();
			}
		}

		@Override
		public void novoArquivo(ArquivoTree arquivoTree) {
			Arquivo arquivo = arquivoTree.getObjetoSelecionado();
			if (arquivo != null && ArquivoUtil.novoArquivo(AnotacaoSplit.this, arquivo.getFile())) {
				inicializar();
			}
		}
	};
}

class Aba extends Transferivel {
	private static final long serialVersionUID = 1L;
	final transient Arquivo arquivo;

	public Aba(Arquivo arquivo) {
		this.arquivo = Objects.requireNonNull(arquivo);
	}

	public Arquivo getArquivo() {
		return arquivo;
	}
}

class PanelRoot extends Panel {
	private static final long serialVersionUID = 1L;

	Fichario getFicharioSelecionado() {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return (Fichario) getComponent(0);
		}
		return ((Separador) getComponent(0)).getFicharioSelecionado();
	}

	Fichario getFicharioPrimeiro() {
		if (getComponentCount() == 0) {
			return null;
		}
		if (getComponent(0) instanceof Fichario) {
			return (Fichario) getComponent(0);
		}
		return ((Separador) getComponent(0)).getFicharioPrimeiro();
	}

	void setRoot(Component c) {
		if (getComponentCount() > 0) {
			throw new IllegalStateException();
		}
		add(c);
	}
}