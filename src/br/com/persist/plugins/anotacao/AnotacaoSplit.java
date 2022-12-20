package br.com.persist.plugins.anotacao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import javax.swing.SwingUtilities;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.arquivo.ArquivoModelo;
import br.com.persist.arquivo.ArquivoTree;
import br.com.persist.arquivo.ArquivoTreeListener;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.SplitPane;
import br.com.persist.componente.TextField;
import br.com.persist.componente.TextPane;
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
		int indice = fichario.getTabCount() - 1;
		fichario.setSelectedIndex(indice);
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
	private final TextPane textArea = new TextPane();
	private final Toolbar toolbar = new Toolbar();
	final transient Arquivo arquivo;

	public Aba(Arquivo arquivo) {
		this.arquivo = Objects.requireNonNull(arquivo);
		toolbar.ini();
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(textArea));
	}

	private void abrir() {
		textArea.limpar();
		if (arquivo.getFile().exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(arquivo.getFile()), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtPesquisa = new TextField(35);
		private transient Selecao selecao;

		public void ini() {
			super.ini(new Nil(), BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea, numeros, letras);
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(Aba.this, Constantes.TRES)) {
				salvarArquivo(arquivo.getFile());
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("Aba", ex, Aba.this);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
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