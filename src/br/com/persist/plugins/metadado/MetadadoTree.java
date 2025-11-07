package br.com.persist.plugins.metadado;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.componente.Popup;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.componente.Tree;

public class MetadadoTree extends Tree {
	private MetadadosPopup metadadosPopup = new MetadadosPopup();
	private final transient List<MetadadoTreeListener> ouvintes;
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private boolean padraoClickExportacao = true;

	public MetadadoTree() throws ArgumentoException {
		this(new MetadadoModelo());
	}

	public MetadadoTree(TreeModel newModel) {
		super(newModel);
		addMouseListener(mouseListenerInner);
		ouvintes = new ArrayList<>();
		configurar();
	}

	public void adicionarOuvinte(MetadadoTreeListener listener) {
		if (listener != null) {
			ouvintes.add(listener);
		}
	}

	private void configurar() {
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dge -> {
			Metadado metadado = getObjetoSelecionado();
			if (metadado.isTabela()) {
				dge.startDrag(null, metadado, listenerArrasto);
			}
		});

		getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, actionAtualizarArvore);

		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_F), "focus_input_pesquisar");
		getActionMap().put("focus_input_pesquisar", actionFocusPesquisar);
	}

	private transient javax.swing.Action actionAtualizarArvore = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ouvintes.forEach(o -> o.atualizarArvore(MetadadoTree.this));
		}
	};

	private transient javax.swing.Action actionFocusPesquisar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ouvintes.forEach(o -> o.focusInputPesquisar(MetadadoTree.this));
		}
	};

	public static KeyStroke getKeyStrokeCtrl(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_FOCUSED);
	}

	public MetadadoPesquisa getPesquisa(MetadadoPesquisa pesquisa, String string, boolean porParte) {
		if (pesquisa == null) {
			return new MetadadoPesquisa(this, string, porParte);
		} else if (pesquisa.igual(string, porParte)) {
			return pesquisa;
		}
		return new MetadadoPesquisa(this, string, porParte);
	}

	private transient DragSourceListener listenerArrasto = new DragSourceListener() {
		@Override
		public void dropActionChanged(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dropActionChanged");
		}

		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dragEnter");
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dragOver");
		}

		@Override
		public void dragExit(DragSourceEvent dse) {
			LOG.log(Level.FINEST, "dragExit");
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			LOG.log(Level.FINEST, "dragDropEnd");
		}
	};

	public Metadado getObjetoSelecionado() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		}
		if (path.getLastPathComponent() instanceof Metadado) {
			return (Metadado) path.getLastPathComponent();
		}
		return null;
	}

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
				if (selecionado.getLastPathComponent() instanceof Metadado) {
					Metadado metadado = (Metadado) selecionado.getLastPathComponent();
					metadadosPopup.preShow(metadado);
					metadadosPopup.show(MetadadoTree.this, e.getX(), e.getY());
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
				Metadado m = getObjetoSelecionado();
				if (m != null && m.isConstraint()) {
					constraintInfo();
					return;
				}
				if (padraoClickExportacao) {
					ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(MetadadoTree.this, true));
				} else {
					ouvintes.forEach(o -> o.abrirImportacaoFichArquivo(MetadadoTree.this, true));
				}
			}
		}
	};

	private class MetadadosPopup extends Popup {
		private Action copiarDescricaoAcao = Action.acaoMenu(MetadadoMensagens.getString("label.copiar_descricao"),
				Icones.COPIA);
		private Action constraintInfoAction = Action.acaoMenu(MetadadoMensagens.getString("label.constraint_info"),
				null);
		private MenuAbrirExportacaoH menuAbrirExportacaoH = new MenuAbrirExportacaoH();
		private MenuAbrirImportacaoH menuAbrirImportacaoH = new MenuAbrirImportacaoH();
		private MenuAbrirExportacaoC menuAbrirExportacaoC = new MenuAbrirExportacaoC();
		private MenuAbrirImportacaoC menuAbrirImportacaoC = new MenuAbrirImportacaoC();
		private Action dadosAcao = actionMenu("label.dados", Icones.TABELA);
		private MenuExportacao menuExportacao = new MenuExportacao();
		private Action destacarAcao = actionMenu("label.destacar");
		private MenuItem itemDados = new MenuItem(dadosAcao);
		private static final long serialVersionUID = 1L;

		private MetadadosPopup() {
			add(menuExportacao);
			add(true, menuAbrirExportacaoH);
			add(menuAbrirImportacaoH);
			add(menuAbrirExportacaoC);
			add(menuAbrirImportacaoC);
			add(true, itemDados);
			addMenuItem(true, copiarDescricaoAcao);
			addMenuItem(destacarAcao, getString("hint.destacar"));
			addMenuItem(true, constraintInfoAction);
			constraintInfoAction.setActionListener(e -> constraintInfo());
			copiarDescricaoAcao.setActionListener(e -> copiarDescricao());
			destacarAcao.setActionListener(e -> destacarItem());
			dadosAcao.setActionListener(e -> abrirObjeto());
		}

		private void abrirObjeto() {
			Metadado metadado = getObjetoSelecionado();
			if (metadado.isTabela()) {
				for (MetadadoTreeListener item : ouvintes) {
					try {
						item.registros(MetadadoTree.this);
					} catch (AssistenciaException ex) {
						Util.mensagem(MetadadoTree.this, ex.getMessage());
					}
				}
			}
		}

		String getString(String chave) {
			return MetadadoMensagens.getString(chave);
		}

		private void copiarDescricao() {
			Metadado metadado = getObjetoSelecionado();
			if (metadado != null) {
				metadado.copiarDescricao();
			}
		}

		private void destacarItem() {
			Metadado metadado = getObjetoSelecionado();
			if (metadado != null) {
				metadado.destacar();
				MetadadoTreeUtil.selecionarObjeto(MetadadoTree.this, metadado);
			}
		}

		private void preShow(Metadado metadado) {
			boolean ehTabela = metadado.isTabela();
			menuExportacao.setEnabled(metadado.getEhRaiz() && !metadado.estaVazio());
			constraintInfoAction.setEnabled(metadado.isConstraint());
			menuAbrirExportacaoH.setEnabled(ehTabela);
			menuAbrirImportacaoH.setEnabled(ehTabela);
			menuAbrirExportacaoC.setEnabled(ehTabela);
			menuAbrirImportacaoC.setEnabled(ehTabela);
			itemDados.setEnabled(ehTabela);
		}

		private class MenuExportacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuExportacao() {
				super("label.exportar", Icones.ABRIR);
				formularioAcao.setActionListener(e -> initExportacao(Constantes.FORM));
				dialogoAcao.setActionListener(e -> initExportacao(Constantes.DIALOG));
				ficharioAcao.setActionListener(e -> initExportacao(Constantes.FICHA));
			}

			private void initExportacao(String tipoContainer) {
				Metadado metadado = getObjetoSelecionado();
				if (metadado == null) {
					return;
				}
				List<String> nomes = new ArrayList<>();
				for (int i = 0; i < metadado.getTotal(); i++) {
					Metadado meta = metadado.getMetadado(i);
					nomes.add(meta.getDescricao());
					meta.setSelecionado(true);
				}
				Coletor coletor = new Coletor();
				SetLista.view(getTitulo(tipoContainer), nomes, coletor, MetadadoTree.this,
						new SetLista.Config(true, false));
				if (!coletor.estaVazio()) {
					exportar(coletor, metadado, tipoContainer);
				}
			}

			private String getTitulo(String tipoContainer) {
				if (Constantes.FORM.equals(tipoContainer)) {
					return MetadadoMensagens.getString("label.exportar_objetos_form");
				} else if (Constantes.DIALOG.equals(tipoContainer)) {
					return MetadadoMensagens.getString("label.exportar_objetos_dialog");
				} else if (Constantes.FICHA.equals(tipoContainer)) {
					return MetadadoMensagens.getString("label.exportar_objetos_fich");
				}
				return "???";
			}

			private void exportar(Coletor coletor, Metadado metadado, String tipoContainer) {
				for (int i = 0; i < metadado.getTotal(); i++) {
					Metadado meta = metadado.getMetadado(i);
					meta.setSelecionado(coletor.contem(meta.getDescricao()));
				}
				if (Constantes.FORM.equals(tipoContainer)) {
					ouvintes.forEach(o -> o.exportarFormArquivo(MetadadoTree.this));
				} else if (Constantes.DIALOG.equals(tipoContainer)) {
					ouvintes.forEach(o -> o.exportarDialogArquivo(MetadadoTree.this));
				} else if (Constantes.FICHA.equals(tipoContainer)) {
					ouvintes.forEach(o -> o.exportarFichArquivo(MetadadoTree.this));
				}
			}
		}

		private class MenuAbrirExportacaoC extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirExportacaoC() {
				super(MetadadoMensagens.getString("label.abrir_exportacao_c"), false, Icones.ABRIR);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFormArquivo(MetadadoTree.this, true)));
				dialogoAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoDialogArquivo(MetadadoTree.this, true)));
				ficharioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(MetadadoTree.this, true)));
			}
		}

		private class MenuAbrirExportacaoH extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirExportacaoH() {
				super(MetadadoMensagens.getString("label.abrir_exportacao_h"), false, Icones.ABRIR);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFormArquivo(MetadadoTree.this, false)));
				dialogoAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoDialogArquivo(MetadadoTree.this, false)));
				ficharioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(MetadadoTree.this, false)));
			}
		}

		private class MenuAbrirImportacaoC extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirImportacaoC() {
				super(MetadadoMensagens.getString("label.abrir_importacao_c"), false, Icones.ABRIR);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoFormArquivo(MetadadoTree.this, true)));
				dialogoAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoDialogArquivo(MetadadoTree.this, true)));
				ficharioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoFichArquivo(MetadadoTree.this, true)));
			}
		}

		private class MenuAbrirImportacaoH extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirImportacaoH() {
				super(MetadadoMensagens.getString("label.abrir_importacao_h"), false, Icones.ABRIR);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoFormArquivo(MetadadoTree.this, false)));
				dialogoAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoDialogArquivo(MetadadoTree.this, false)));
				ficharioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoFichArquivo(MetadadoTree.this, false)));
			}
		}
	}

	public Metadado getRaiz() {
		MetadadoModelo modelo = (MetadadoModelo) getModel();
		return (Metadado) modelo.getRoot();
	}

	public void constraintInfo() {
		ouvintes.forEach(o -> o.constraintInfo(MetadadoTree.this));
	}

	public String pksMultiplaExport() {
		return getRaiz().pksMultiplasExport();
	}

	public String pksMultipla() {
		return getRaiz().pksMultiplas();
	}

	public String pksAusente() {
		return getRaiz().pksAusente();
	}

	public String queExportam() {
		return getRaiz().queExportam();
	}

	public String naoExportam() {
		return getRaiz().naoExportam();
	}

	public String getOrdenadosExportacaoImportacao(boolean exp) {
		return getRaiz().getOrdenadosExportacaoImportacao(exp);
	}

	public String getOrdenadosCampos() {
		return getRaiz().getOrdenadosCampos();
	}

	public Map<String, Set<String>> localizarCampo(String nome) {
		return getRaiz().localizarCampo(nome);
	}

	public void selecionar(String nome, boolean porParte) {
		Metadado raiz = getRaiz();
		Metadado metadado = raiz.getMetadado(nome, porParte);
		if (metadado != null) {
			MetadadoTreeUtil.selecionarObjeto(this, metadado);
		} else {
			setSelectionPath(null);
		}
	}

	public void preencher(List<Metadado> lista, String nome, boolean porParte) {
		Metadado raiz = getRaiz();
		raiz.preencher(lista, nome, porParte);
	}
}