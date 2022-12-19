package br.com.persist.plugins.anotacao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.SplitPane;

class AnotacaoSplit extends SplitPane {
	private static final long serialVersionUID = 1L;
	private final File fileRoot;
	private ArquivoTree tree;
	private Panel panel;

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
		setLeftComponent(tree);
		panel = new Panel();
		setRightComponent(panel);
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
		}
	};
}