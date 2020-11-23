package br.com.persist.plugins.arquivo;

import java.awt.Rectangle;
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
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;

public class ArquivoTree extends Tree {
	private static final long serialVersionUID = 1L;
	private final transient List<ArquivoTreeListener> ouvintes;
	private ArquivoPopup arvorePopup = new ArquivoPopup();

	public ArquivoTree(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new ArquivoRenderer());
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
	}

	public void adicionarOuvinte(ArquivoTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
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
			if (!e.isPopupTrigger() || popupDesabilitado || getObjetoSelecionado() == null) {
				return;
			}
			TreePath arquivoClicado = getClosestPathForLocation(e.getX(), e.getY());
			TreePath arquivoSelecionado = getSelectionPath();
			popupTrigger = true;
			if (arquivoClicado == null || arquivoSelecionado == null) {
				setSelectionPath(null);
				return;
			}
			Rectangle rectangle = getPathBounds(arquivoClicado);
			if (rectangle == null || !rectangle.contains(e.getX(), e.getY())) {
				setSelectionPath(null);
				return;
			}
			if (arquivoClicado.equals(arquivoSelecionado)) {
				if (arquivoSelecionado.getLastPathComponent() instanceof Arquivo) {
					Arquivo arquivo = (Arquivo) arquivoSelecionado.getLastPathComponent();
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
			if (e.getClickCount() >= Constantes.DOIS) {
				ouvintes.forEach(o -> o.abrirArquivoFichario(ArquivoTree.this));
			} else {
				TreePath pathClicado = getClosestPathForLocation(e.getX(), e.getY());
				if (pathClicado != null) {
					Arquivo arquivo = (Arquivo) pathClicado.getLastPathComponent();
					if (arquivo == null || !arquivo.isFile()) {
						return;
					}
					ouvintes.forEach(o -> o.clickArquivo(ArquivoTree.this));
				}
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class ArquivoPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private Action fecharAcao = Action.actionMenu(Constantes.LABEL_FECHAR, Icones.FECHAR);
		private Action selecionarAcao = Action.actionMenu("label.selecionar", Icones.CURSOR);
		private Action atualizarAcao = Action.actionMenu("label.status", Icones.ATUALIZAR);
		private Action excluirAcao = Action.actionMenu("label.excluir2", Icones.EXCLUIR);
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
		}

		private class MenuAbrir extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;
			private Action diretorioAcao = Action.actionMenu("label.diretorio", Icones.ABRIR);
			private Action conteudoAcao = Action.actionMenu("label.conteudo", null);

			private MenuAbrir() {
				super("label.abrir", Icones.ABRIR, false);
				addSeparator();
				addMenuItem(diretorioAcao);
				addMenuItem(conteudoAcao);
				formularioAcao
						.setActionListener(e -> ouvintes.forEach(o -> o.abrirArquivoFormulario(ArquivoTree.this)));
				ficharioAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirArquivoFichario(ArquivoTree.this)));
				diretorioAcao.setActionListener(e -> ouvintes.forEach(o -> o.diretorioArquivo(ArquivoTree.this)));
				conteudoAcao.setActionListener(e -> ouvintes.forEach(o -> o.conteudoArquivo(ArquivoTree.this)));
			}
		}
	}
}