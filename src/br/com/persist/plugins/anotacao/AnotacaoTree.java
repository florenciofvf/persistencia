package br.com.persist.plugins.anotacao;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.Action;
import br.com.persist.componente.Menu;
import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;

public class AnotacaoTree extends Tree {
	private static final long serialVersionUID = 1L;
	private final transient List<AnotacaoTreeListener> ouvintes;
	private AnotacaoPopup anotacaoPopup = new AnotacaoPopup();

	public AnotacaoTree(TreeModel newModel) {
		super(newModel);
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
	}

	public void adicionarOuvinte(AnotacaoTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
		}
	}

	public Anotacao getRaiz() {
		AnotacaoModelo modelo = (AnotacaoModelo) getModel();
		return (Anotacao) modelo.getRoot();
	}

	public Anotacao getObjetoSelecionado() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		}
		if (path.getLastPathComponent() instanceof Anotacao) {
			return (Anotacao) path.getLastPathComponent();
		}
		return null;
	}

	public void selecionarAnotacao(Anotacao anotacao) {
		if (anotacao != null) {
			AnotacaoTreeUtil.selecionarObjeto(this, anotacao);
		}
	}

	public void excluirSelecionado() {
		Anotacao selecionado = getObjetoSelecionado();
		if (selecionado != null) {
			AnotacaoTreeUtil.excluirEstrutura(this, selecionado);
		}
	}

	private transient KeyAdapter keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				mouseListenerInner.mouseClicked(new MouseEvent(AnotacaoTree.this, 0, 0, 0, 0, 0, Constantes.DOIS, false));
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
				if (selecionado.getLastPathComponent() instanceof Anotacao) {
					Anotacao anotacao = (Anotacao) selecionado.getLastPathComponent();
					anotacaoPopup.preShow(anotacao);
					anotacaoPopup.show(AnotacaoTree.this, e.getX(), e.getY());
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
				Anotacao anotacao = getObjetoSelecionado();
				if (anotacao == null) {
					return;
				}
				if (anotacao.isFile()) {
					ouvintes.forEach(o -> o.abrirAnotacao(AnotacaoTree.this));
				} else if (anotacao.isDirectory()) {
					ouvintes.forEach(o -> o.diretorioAnotacao(AnotacaoTree.this));
				}
			}
		}
	};

	private class AnotacaoPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private Action excluirAcao = Action.actionMenu("label.excluir", Icones.EXCLUIR);
		private Action renomearAcao = Action.actionMenu("label.renomear", Icones.RULE);

		private AnotacaoPopup() {
			add(new MenuAbrir());
			addMenuItem(true, renomearAcao);
			addMenuItem(true, excluirAcao);
			renomearAcao.setActionListener(e -> ouvintes.forEach(o -> o.renomearAnotacao(AnotacaoTree.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirAnotacao(AnotacaoTree.this)));
		}

		private void preShow(Anotacao anotacao) {
			boolean renomearExcluir = anotacao.getPai() != null && !AnotacaoModelo.anotacaoInfo.equals(anotacao.getFile());
			renomearAcao.setEnabled(renomearExcluir);
			excluirAcao.setEnabled(renomearExcluir);
		}

		private class MenuAbrir extends Menu {
			private static final long serialVersionUID = 1L;
			private Action diretorioAcao = Action.actionMenu("label.diretorio", Icones.ABRIR);
			private Action imprimirAcao = Action.actionMenu("label.imprimir", Icones.PRINT);
			private Action editarAcao = Action.actionMenu("label.editar", Icones.EDIT);
			private Action abrirAcao = Action.actionMenu("label.abrir", Icones.ABRIR);
			private Action conteudoAcao = Action.actionMenu("label.conteudo", null);

			private MenuAbrir() {
				super("label.opcoes", Icones.CONFIG);
				addMenuItem(abrirAcao);
				addMenuItem(editarAcao);
				addMenuItem(imprimirAcao);
				addSeparator();
				addMenuItem(diretorioAcao);
				addMenuItem(conteudoAcao);
				diretorioAcao.setActionListener(e -> ouvintes.forEach(o -> o.diretorioAnotacao(AnotacaoTree.this)));
				conteudoAcao.setActionListener(e -> ouvintes.forEach(o -> o.conteudoAnotacao(AnotacaoTree.this)));
				abrirAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirAnotacao(AnotacaoTree.this)));
			}
		}
	}

	public void selecionar(String nome, boolean porParte) {
		Anotacao raiz = getRaiz();
		Anotacao anotacao = raiz.getAnotacao(nome, porParte);
		if (anotacao != null) {
			AnotacaoTreeUtil.selecionarObjeto(this, anotacao);
		} else {
			setSelectionPath(null);
		}
	}

	public void preencher(List<Anotacao> lista, String nome, boolean porParte) {
		Anotacao raiz = getRaiz();
		raiz.preencher(lista, nome, porParte);
	}
}