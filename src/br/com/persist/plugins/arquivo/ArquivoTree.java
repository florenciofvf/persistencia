package br.com.persist.plugins.arquivo;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.Action;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;

public class ArquivoTree extends Tree {
	private final transient List<ArquivoTreeListener> ouvintes;
	private ArquivoPopup arvorePopup = new ArquivoPopup();
	private static final long serialVersionUID = 1L;

	public ArquivoTree(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new ArquivoRenderer());
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
		configurar();
	}

	private void configurar() {
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_F), "focus_input_pesquisar");
		getActionMap().put("focus_input_pesquisar", actionFocusPesquisar);
	}

	private transient javax.swing.Action actionFocusPesquisar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ouvintes.forEach(o -> o.focusInputPesquisar(ArquivoTree.this));
		}
	};

	public static KeyStroke getKeyStrokeCtrl(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_FOCUSED);
	}

	public void adicionarOuvinte(ArquivoTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
		}
	}

	public Arquivo getRaiz() {
		ArquivoModelo modelo = (ArquivoModelo) getModel();
		return (Arquivo) modelo.getRoot();
	}

	public void selecionar(File file) {
		Arquivo arquivo = getRaiz().get(file);
		if (arquivo != null) {
			selecionarArquivo(arquivo);
			arquivo.setArquivoAberto(true);
		}
	}

	public Arquivo getObjetoSelecionado() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		}
		if (path.getLastPathComponent() instanceof Arquivo) {
			return (Arquivo) path.getLastPathComponent();
		}
		return null;
	}

	public void selecionarArquivo(Arquivo arquivo) {
		if (arquivo != null) {
			ArquivoTreeUtil.selecionarObjeto(this, arquivo);
		}
	}

	public void excluirSelecionado() {
		Arquivo selecionado = getObjetoSelecionado();
		if (selecionado != null) {
			ArquivoTreeUtil.excluirEstrutura(this, selecionado);
		}
	}

	private transient KeyAdapter keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				popupTrigger = false;
				mouseListenerInner
						.mouseClicked(new MouseEvent(ArquivoTree.this, 0, 0, 0, 0, 0, Constantes.DOIS, false));
			}
		}
	};

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			popupTrigger = false;
			checkPopupTrigger(e);
			if (!e.isPopupTrigger() || getObjetoSelecionado() == null) {
				return;
			}
			TreePath clicado = getClosestPathForLocation(e.getX(), e.getY());
			TreePath selecionado = getSelectionPath();
			popupTrigger = true;
			if (!validos(clicado, selecionado)) {
				setSelectionPath(null);
				return;
			}
			if (!localValido(clicado, e)) {
				setSelectionPath(null);
				return;
			}
			if (clicado.equals(selecionado)) {
				if (selecionado.getLastPathComponent() instanceof Arquivo) {
					Arquivo arquivo = (Arquivo) selecionado.getLastPathComponent();
					arvorePopup.preShow(arquivo);
					arvorePopup.show(ArquivoTree.this, e.getX(), e.getY());
				} else {
					setSelectionPath(null);
				}
			} else {
				setSelectionPath(null);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (popupTrigger) {
				return;
			}
			TreePath clicado = getClosestPathForLocation(e.getX(), e.getY());
			TreePath selecionado = getSelectionPath();
			if (!validos(clicado, selecionado)) {
				setSelectionPath(null);
				return;
			}
			if (!localValido(clicado, e)) {
				setSelectionPath(null);
				return;
			}
			if (e.getClickCount() >= Constantes.DOIS) {
				Arquivo arquivo = getObjetoSelecionado();
				if (arquivo != null && arquivo.isFile()) {
					ouvintes.forEach(o -> o.abrirArquivoFichario(ArquivoTree.this));
				}
			} else {
				Arquivo arquivo = (Arquivo) clicado.getLastPathComponent();
				if (arquivo == null || !arquivo.isFile()) {
					setSelectionPath(null);
					return;
				}
				ouvintes.forEach(o -> o.clickArquivo(ArquivoTree.this));
			}
		}
	};

	private class ArquivoPopup extends Popup {
		private Action fecharAcao = actionMenu(Constantes.LABEL_FECHAR, Icones.FECHAR);
		private Action selecionarAcao = actionMenu("label.selecionar", Icones.CURSOR);
		private Action atualizarAcao = actionMenu("label.status", Icones.ATUALIZAR);
		private Action excluirAcao = Action.actionMenuExcluir();
		private static final long serialVersionUID = 1L;
		private MenuAbrir menuAbrir = new MenuAbrir();

		private ArquivoPopup() {
			add(menuAbrir);
			addMenuItem(true, selecionarAcao);
			addMenuItem(true, fecharAcao);
			addMenuItem(true, excluirAcao);
			addMenuItem(true, atualizarAcao);
			selecionarAcao.setActionListener(e -> ouvintes.forEach(o -> o.selecionarArquivo(ArquivoTree.this)));
			atualizarAcao.setActionListener(e -> ouvintes.forEach(o -> o.atualizarArquivo(ArquivoTree.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirArquivo(ArquivoTree.this)));
			fecharAcao.setActionListener(e -> ouvintes.forEach(o -> o.fecharArquivo(ArquivoTree.this)));
		}

		private void preShow(Arquivo arquivo) {
			boolean ehArquivo = arquivo.isFile();
			excluirAcao.setEnabled(ehArquivo && arquivo.getPai() != null);
			selecionarAcao.setEnabled(ehArquivo);
			atualizarAcao.setEnabled(ehArquivo);
			fecharAcao.setEnabled(ehArquivo);
			menuAbrir.setEnabled(ehArquivo);
			menuAbrir.preShow(arquivo);
		}

		private class MenuAbrir extends MenuPadrao1 {
			private Action diretorioAcao = actionMenu("label.diretorio", Icones.ABRIR);
			private Action conteudoAcao = actionMenu("label.conteudo");
			private Action clonarAcao = Action.actionMenuClonar();
			private static final long serialVersionUID = 1L;

			private MenuAbrir() {
				super("label.abrir", Icones.ABRIR, false);
				addSeparator();
				addMenuItem(diretorioAcao);
				addMenuItem(conteudoAcao);
				addSeparator();
				addMenuItem(clonarAcao);
				formularioAcao
						.setActionListener(e -> ouvintes.forEach(o -> o.abrirArquivoFormulario(ArquivoTree.this)));
				ficharioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirArquivoFichario(ArquivoTree.this)));
				diretorioAcao.setActionListener(e -> ouvintes.forEach(o -> o.diretorioArquivo(ArquivoTree.this)));
				conteudoAcao.setActionListener(e -> ouvintes.forEach(o -> o.conteudoArquivo(ArquivoTree.this)));
				clonarAcao.setActionListener(e -> ouvintes.forEach(o -> o.clonarArquivo(ArquivoTree.this)));
			}

			private void preShow(Arquivo arquivo) {
				diretorioAcao.setEnabled(arquivo.pathValido());
			}
		}
	}

	public void selecionar(String nome, boolean porParte) {
		Arquivo raiz = getRaiz();
		Arquivo arquivo = raiz.getArquivo(nome, porParte);
		if (arquivo != null) {
			ArquivoTreeUtil.selecionarObjeto(this, arquivo);
		} else {
			setSelectionPath(null);
		}
	}

	public void preencher(List<Arquivo> lista, String nome, boolean porParte) {
		Arquivo raiz = getRaiz();
		raiz.preencher(lista, nome, porParte);
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		Arquivo raiz = getRaiz();
		raiz.contemConteudo(set, string, porParte);
	}
}