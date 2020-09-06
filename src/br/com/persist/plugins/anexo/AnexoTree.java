package br.com.persist.plugins.anexo;

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

import br.com.persist.componente.Action;
import br.com.persist.componente.CheckBoxItem;
import br.com.persist.componente.Menu;
import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;

public class AnexoTree extends Tree {
	private static final long serialVersionUID = 1L;
	private final transient List<AnexoTreeListener> ouvintes;
	private AnexoPopup anexoPopup = new AnexoPopup();

	public AnexoTree(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new AnexoRenderer());
		addMouseListener(mouseListenerInner);
		addKeyListener(keyListenerInner);
		ouvintes = new ArrayList<>();
	}

	public void adicionarOuvinte(AnexoTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
		}
	}

	public Anexo getObjetoSelecionado() {
		TreePath path = getSelectionPath();

		if (path == null) {
			return null;
		}

		if (path.getLastPathComponent() instanceof Anexo) {
			return (Anexo) path.getLastPathComponent();
		}

		return null;
	}

	public void selecionarAnexo(Anexo anexo) {
		if (anexo != null) {
			AnexoTreeUtil.selecionarObjeto(this, anexo);
		}
	}

	public void excluirSelecionado() {
		Anexo selecionado = getObjetoSelecionado();

		if (selecionado != null) {
			AnexoTreeUtil.excluirEstrutura(this, selecionado);
		}
	}

	private transient KeyAdapter keyListenerInner = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				mouseListenerInner.mouseClicked(new MouseEvent(AnexoTree.this, 0, 0, 0, 0, 0, Constantes.DOIS, false));
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

			TreePath anexoClicado = getClosestPathForLocation(e.getX(), e.getY());
			TreePath anexoSelecionado = getSelectionPath();
			popupTrigger = true;

			if (anexoClicado == null || anexoSelecionado == null) {
				setSelectionPath(null);
				return;
			}

			Rectangle rectangle = getPathBounds(anexoClicado);

			if (rectangle == null || !rectangle.contains(e.getX(), e.getY())) {
				setSelectionPath(null);
				return;
			}

			if (anexoClicado.equals(anexoSelecionado)) {
				if (anexoSelecionado.getLastPathComponent() instanceof Anexo) {
					Anexo anexo = (Anexo) anexoSelecionado.getLastPathComponent();
					anexoPopup.preShow(anexo);
					anexoPopup.show(AnexoTree.this, e.getX(), e.getY());
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
				Anexo anexo = getObjetoSelecionado();

				if (anexo == null) {
					return;
				}

				if (anexo.isFile()) {
					if (anexo.isPadraoAbrir()) {
						ouvintes.forEach(o -> o.abrirAnexo(AnexoTree.this));
					} else {
						ouvintes.forEach(o -> o.editarAnexo(AnexoTree.this));
					}
				} else if (anexo.isDirectory()) {
					ouvintes.forEach(o -> o.abrirAnexo(AnexoTree.this));
				}
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class AnexoPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private CheckBoxItem chkAbrirVisivel = new CheckBoxItem("label.abrir_visivel", Icones.HIERARQUIA);
		private CheckBoxItem chkPadraoAbrir = new CheckBoxItem("label.padrao_abrir", Icones.EXECUTAR);
		private Action copiarAcao = Action.actionMenu("label.copiar_atributos", Icones.COPIA);
		private Action colarAcao = Action.actionMenu("label.colar_atributos", Icones.COLAR);
		private Action excluirAcao = Action.actionMenu("label.excluir2", Icones.EXCLUIR);
		private Action renomearAcao = Action.actionMenu("label.renomear", Icones.RULE);
		private Action corFonteAcao = Action.actionMenu("label.cor_fonte", Icones.COR);
		private Action iconeAcao = Action.actionMenu("label.icone", Icones.ICON);

		private AnexoPopup() {
			add(new MenuAbrir());
			add(true, chkPadraoAbrir);
			add(chkAbrirVisivel);
			addMenuItem(true, renomearAcao);
			addMenuItem(true, excluirAcao);
			addMenuItem(true, corFonteAcao);
			addMenuItem(true, iconeAcao);
			addMenuItem(true, copiarAcao);
			addMenuItem(colarAcao);

			copiarAcao.setActionListener(e -> ouvintes.forEach(o -> o.copiarAtributosAnexo(AnexoTree.this)));
			colarAcao.setActionListener(e -> ouvintes.forEach(o -> o.colarAtributosAnexo(AnexoTree.this)));
			renomearAcao.setActionListener(e -> ouvintes.forEach(o -> o.renomearAnexo(AnexoTree.this)));
			corFonteAcao.setActionListener(e -> ouvintes.forEach(o -> o.corFonteAnexo(AnexoTree.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirAnexo(AnexoTree.this)));
			iconeAcao.setActionListener(e -> ouvintes.forEach(o -> o.iconeAnexo(AnexoTree.this)));
			chkAbrirVisivel.addActionListener(e -> abrirVisivel(chkAbrirVisivel.isSelected()));
			chkPadraoAbrir.addActionListener(e -> padraoAbrir(chkPadraoAbrir.isSelected()));
		}

		private void preShow(Anexo anexo) {
			boolean renomearExcluir = anexo.getPai() != null && !AnexoModelo.anexosInfo.equals(anexo.getFile());
			chkAbrirVisivel.setSelected(anexo.isAbrirVisivel());
			chkPadraoAbrir.setSelected(anexo.isPadraoAbrir());
			chkAbrirVisivel.setEnabled(anexo.isDirectory());
			chkPadraoAbrir.setEnabled(anexo.isFile());
			renomearAcao.setEnabled(renomearExcluir);
			excluirAcao.setEnabled(renomearExcluir);
		}

		private void padraoAbrir(boolean b) {
			Anexo anexo = getObjetoSelecionado();

			if (anexo != null) {
				anexo.setPadraoAbrir(b);
				AnexoModelo.putAnexo(anexo);
			}
		}

		private void abrirVisivel(boolean b) {
			Anexo anexo = getObjetoSelecionado();

			if (anexo != null) {
				anexo.setAbrirVisivel(b);
				AnexoModelo.putAnexo(anexo);
			}
		}

		private class MenuAbrir extends Menu {
			private static final long serialVersionUID = 1L;
			private Action diretorioAcao = Action.actionMenu("label.diretorio", Icones.ABRIR);
			private Action imprimirAcao = Action.actionMenu("label.imprimir", Icones.PRINT);
			private Action editarAcao = Action.actionMenu("label.editar", Icones.EDIT);
			private Action abrirAcao = Action.actionMenu("label.abrir", Icones.ABRIR);

			private MenuAbrir() {
				super("label.opcoes", Icones.CONFIG);
				addMenuItem(abrirAcao);
				addMenuItem(editarAcao);
				addMenuItem(imprimirAcao);
				addSeparator();
				addMenuItem(diretorioAcao);

				diretorioAcao.setActionListener(e -> ouvintes.forEach(o -> o.diretorioAnexo(AnexoTree.this)));
				imprimirAcao.setActionListener(e -> ouvintes.forEach(o -> o.imprimirAnexo(AnexoTree.this)));
				editarAcao.setActionListener(e -> ouvintes.forEach(o -> o.editarAnexo(AnexoTree.this)));
				abrirAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirAnexo(AnexoTree.this)));
			}
		}
	}
}