package br.com.persist.plugins.anexo;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.CheckBoxItem;
import br.com.persist.componente.Menu;
import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;

public class AnexoTree extends Tree {
	private final transient List<AnexoTreeListener> ouvintes;
	private AnexoPopup anexoPopup = new AnexoPopup();
	private static final long serialVersionUID = 1L;

	public AnexoTree(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new AnexoRenderer());
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
			ouvintes.forEach(o -> o.focusInputPesquisar(AnexoTree.this));
		}
	};

	public static KeyStroke getKeyStrokeCtrl(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_FOCUSED);
	}

	public AnexoPesquisa getPesquisa(AnexoPesquisa pesquisa, String string, boolean porParte) {
		if (pesquisa == null) {
			return new AnexoPesquisa(this, string, porParte);
		} else if (pesquisa.igual(string, porParte)) {
			return pesquisa;
		}
		return new AnexoPesquisa(this, string, porParte);
	}

	public void adicionarOuvinte(AnexoTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
		}
	}

	public Anexo getRaiz() {
		AnexoModelo modelo = (AnexoModelo) getModel();
		return (Anexo) modelo.getRoot();
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
				if (selecionado.getLastPathComponent() instanceof Anexo) {
					Anexo anexo = (Anexo) selecionado.getLastPathComponent();
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

	private class AnexoPopup extends Popup {
		private CheckBoxItem chkPadraoAbrir = new CheckBoxItem("label.padrao_desktop_open", Icones.EXECUTAR);
		private CheckBoxItem chkAbrirVisivel = new CheckBoxItem("label.abrir_visivel", Icones.HIERARQUIA);
		private Action copiarAcao = actionMenu("label.copiar_atributos", Icones.COPIA);
		private Action colarAcao = actionMenu("label.colar_atributos", Icones.COLAR);
		private Action renomearAcao = actionMenu("label.renomear", Icones.RULE);
		private Action corFonteAcao = actionMenu("label.cor_fonte", Icones.COR);
		private Action iconeAcao = actionMenu("label.icone", Icones.ICON);
		private Action excluirAcao = Action.actionMenuExcluir();
		private static final long serialVersionUID = 1L;
		private MenuAbrir menuAbrir = new MenuAbrir();

		private AnexoPopup() {
			add(menuAbrir);
			add(true, chkPadraoAbrir);
			add(chkAbrirVisivel);
			addMenuItem(true, renomearAcao);
			addMenuItem(true, excluirAcao);
			addMenuItem(true, corFonteAcao);
			addMenuItem(true, iconeAcao);
			addMenuItem(true, copiarAcao, AnexoMensagens.getString("hint.copiar_atributos"));
			addMenuItem(colarAcao, AnexoMensagens.getString("hint.colar_atributos"));
			copiarAcao.setActionListener(e -> ouvintes.forEach(o -> o.copiarAtributosAnexo(AnexoTree.this)));
			colarAcao.setActionListener(e -> colarAtributosAnexo());
			renomearAcao.setActionListener(e -> ouvintes.forEach(o -> o.renomearAnexo(AnexoTree.this)));
			corFonteAcao.setActionListener(e -> ouvintes.forEach(o -> o.corFonteAnexo(AnexoTree.this)));
			excluirAcao.setActionListener(e -> ouvintes.forEach(o -> o.excluirAnexo(AnexoTree.this)));
			iconeAcao.setActionListener(e -> ouvintes.forEach(o -> o.iconeAnexo(AnexoTree.this)));
			chkAbrirVisivel.addActionListener(e -> abrirVisivel(chkAbrirVisivel.isSelected()));
			chkPadraoAbrir.addActionListener(e -> padraoAbrir(chkPadraoAbrir.isSelected()));
			chkAbrirVisivel.setToolTipText(AnexoMensagens.getString("hint.abrir_visivel"));
			chkPadraoAbrir.setToolTipText(AnexoMensagens.getString("hint.padrao_abrir"));
		}

		private void colarAtributosAnexo() {
			for (AnexoTreeListener item : ouvintes) {
				try {
					item.colarAtributosAnexo(AnexoTree.this);
				} catch (AssistenciaException ex) {
					Util.mensagem(AnexoTree.this, ex.getMessage());
				}
			}
		}

		private void preShow(Anexo anexo) {
			boolean renomearExcluir = anexo.getPai() != null && !AnexoModelo.anexosInfo.equals(anexo.getFile());
			chkAbrirVisivel.setSelected(anexo.isAbrirVisivel());
			chkPadraoAbrir.setSelected(anexo.isPadraoAbrir());
			chkAbrirVisivel.setEnabled(anexo.isDirectory());
			chkPadraoAbrir.setEnabled(anexo.isFile());
			renomearAcao.setEnabled(renomearExcluir);
			excluirAcao.setEnabled(renomearExcluir);
			menuAbrir.preShow(anexo);
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
			private Action diretorioAcao = actionMenu("label.diretorio", Icones.ABRIR);
			private Action imprimirAcao = actionMenu("label.imprimir", Icones.PRINT);
			private Action editarAcao = actionMenu("label.editar", Icones.EDIT);
			private Action abrirAcao = actionMenu("label.abrir", Icones.ABRIR);
			private Action conteudoAcao = actionMenu("label.conteudo");
			private Action clonarAcao = Action.actionMenuClonar();
			private static final long serialVersionUID = 1L;

			private MenuAbrir() {
				super("label.opcoes", Icones.CONFIG);
				addMenuItem(abrirAcao);
				addMenuItem(editarAcao);
				addMenuItem(imprimirAcao);
				addSeparator();
				addMenuItem(diretorioAcao);
				addMenuItem(conteudoAcao);
				addSeparator();
				addMenuItem(clonarAcao);
				diretorioAcao.setActionListener(e -> ouvintes.forEach(o -> o.diretorioAnexo(AnexoTree.this)));
				conteudoAcao.setActionListener(e -> ouvintes.forEach(o -> o.conteudoAnexo(AnexoTree.this)));
				imprimirAcao.setActionListener(e -> ouvintes.forEach(o -> o.imprimirAnexo(AnexoTree.this)));
				editarAcao.setActionListener(e -> ouvintes.forEach(o -> o.editarAnexo(AnexoTree.this)));
				clonarAcao.setActionListener(e -> ouvintes.forEach(o -> o.clonarAnexo(AnexoTree.this)));
				abrirAcao.setActionListener(e -> ouvintes.forEach(o -> o.abrirAnexo(AnexoTree.this)));
			}

			private void preShow(Anexo anexo) {
				diretorioAcao.setEnabled(anexo.pathValido());
			}
		}
	}

	public void selecionar(String nome, boolean porParte) {
		Anexo raiz = getRaiz();
		Anexo anexo = raiz.getAnexo(nome, porParte);
		if (anexo != null) {
			AnexoTreeUtil.selecionarObjeto(this, anexo);
		} else {
			setSelectionPath(null);
		}
	}

	public void preencher(List<Anexo> lista, String nome, boolean porParte) {
		Anexo raiz = getRaiz();
		raiz.preencher(lista, nome, porParte);
	}
}