package br.com.persist.anexo;

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

import br.com.persist.Arquivo;
import br.com.persist.comp.ItemCheckBox;
import br.com.persist.comp.Menu;
import br.com.persist.comp.Popup;
import br.com.persist.comp.Tree;
import br.com.persist.listener.AnexoListener;
import br.com.persist.modelo.AnexoModelo;
import br.com.persist.renderer.AnexoTreeCellRenderer;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;

public class Anexo extends Tree {
	private static final long serialVersionUID = 1L;
	private final transient List<AnexoListener> ouvintes;
	private AnexoPopup anexoPopup = new AnexoPopup();

	public Anexo(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new AnexoTreeCellRenderer());
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
	}

	public void adicionarOuvinte(AnexoListener listener) {
		if (listener == null) {
			return;
		}

		ouvintes.add(listener);
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
		if (arquivo == null) {
			return;
		}

		AnexoUtil.selecionarObjeto(this, arquivo);
	}

	public void excluirSelecionado() {
		Arquivo selecionado = getObjetoSelecionado();

		if (selecionado == null) {
			return;
		}

		AnexoUtil.excluirEstrutura(this, selecionado);
	}

	private transient KeyAdapter keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				mouseListenerInner.mouseClicked(new MouseEvent(Anexo.this, 0, 0, 0, 0, 0, Constantes.DOIS, false));
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

			TreePath anexoCli = getClosestPathForLocation(e.getX(), e.getY());
			TreePath anexoSel = getSelectionPath();
			popupTrigger = true;

			if (anexoCli == null || anexoSel == null) {
				setSelectionPath(null);
				return;
			}

			Rectangle rect = getPathBounds(anexoCli);

			if (rect == null || !rect.contains(e.getX(), e.getY())) {
				setSelectionPath(null);
				return;
			}

			if (anexoCli.equals(anexoSel)) {
				if (anexoSel.getLastPathComponent() instanceof Arquivo) {
					Arquivo arquivo = (Arquivo) anexoSel.getLastPathComponent();
					anexoPopup.preShow(arquivo);
					anexoPopup.show(Anexo.this, e.getX(), e.getY());
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
				Arquivo arquivo = getObjetoSelecionado();

				if (arquivo == null) {
					return;
				}

				if (arquivo.isFile()) {
					if (arquivo.isPadraoAbrir()) {
						ouvintes.forEach(o -> o.abrirArquivo(Anexo.this));
					} else {
						ouvintes.forEach(o -> o.editarArquivo(Anexo.this));
					}
				} else if (arquivo.isDirectory()) {
					ouvintes.forEach(o -> o.abrirArquivo(Anexo.this));
				}
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class AnexoPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private ItemCheckBox chkAbrirVisivel = new ItemCheckBox("label.abrir_visivel", Icones.HIERARQUIA);
		private ItemCheckBox chkPadraoAbrir = new ItemCheckBox("label.padrao_abrir", Icones.EXECUTAR);
		private Action excluirAcao = Action.actionMenu("label.excluir2", Icones.EXCLUIR);
		private Action renomearAcao = Action.actionMenu("label.renomear", Icones.RULE);
		private Action corFonteAcao = Action.actionMenu("label.cor_fonte", Icones.COR);
		private Action iconeAcao = Action.actionMenu("label.icone", Icones.ICON);

		public AnexoPopup() {
			add(new MenuAbrir());
			add(true, chkPadraoAbrir);
			add(chkAbrirVisivel);
			addMenuItem(true, renomearAcao);
			addMenuItem(true, excluirAcao);
			addMenuItem(true, corFonteAcao);
			addMenuItem(true, iconeAcao);

			renomearAcao.setActionListener(e -> ouvintes.forEach(o -> o.renomearArquivo(Anexo.this)));
			corFonteAcao.setActionListener(e -> ouvintes.forEach(o -> o.corFonteArquivo(Anexo.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirArquivo(Anexo.this)));
			iconeAcao.setActionListener(e -> ouvintes.forEach(o -> o.iconeArquivo(Anexo.this)));
			chkAbrirVisivel.addActionListener(e -> abrirVisivel(chkAbrirVisivel.isSelected()));
			chkPadraoAbrir.addActionListener(e -> padraoAbrir(chkPadraoAbrir.isSelected()));
		}

		private void preShow(Arquivo arquivo) {
			renomearAcao.setEnabled(arquivo.getPai() != null && !AnexoModelo.anexosInfo.equals(arquivo.getFile()));
			excluirAcao.setEnabled(arquivo.getPai() != null && !AnexoModelo.anexosInfo.equals(arquivo.getFile()));
			chkPadraoAbrir.setSelected(arquivo.isPadraoAbrir());
			chkAbrirVisivel.setEnabled(arquivo.isFile());
			chkPadraoAbrir.setEnabled(arquivo.isFile());
		}

		private void padraoAbrir(boolean b) {
			Arquivo arquivo = getObjetoSelecionado();

			if (arquivo != null) {
				arquivo.setPadraoAbrir(b);
				AnexoModelo.putArquivo(arquivo);
			}
		}

		private void abrirVisivel(boolean b) {
			Arquivo arquivo = getObjetoSelecionado();

			if (arquivo != null) {
				arquivo.setAbrirVisivel(b);
				AnexoModelo.putArquivo(arquivo);
			}
		}

		class MenuAbrir extends Menu {
			private static final long serialVersionUID = 1L;
			private Action imprimirAcao = Action.actionMenu("label.imprimir", Icones.PRINT);
			private Action pastaAcao = Action.actionMenu("label.diretorio", Icones.ABRIR);
			private Action editarAcao = Action.actionMenu("label.editar", Icones.EDIT);
			private Action abrirAcao = Action.actionMenu("label.abrir", Icones.ABRIR);

			MenuAbrir() {
				super("label.opcoes", Icones.CONFIG);
				addMenuItem(abrirAcao);
				addMenuItem(editarAcao);
				addMenuItem(imprimirAcao);
				addSeparator();
				addMenuItem(pastaAcao);

				imprimirAcao.setActionListener(e -> ouvintes.forEach(o -> o.imprimirArquivo(Anexo.this)));
				editarAcao.setActionListener(e -> ouvintes.forEach(o -> o.editarArquivo(Anexo.this)));
				abrirAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirArquivo(Anexo.this)));
				pastaAcao.setActionListener(e -> ouvintes.forEach(o -> o.pastaArquivo(Anexo.this)));
			}
		}
	}
}