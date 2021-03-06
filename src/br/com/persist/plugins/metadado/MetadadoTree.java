package br.com.persist.plugins.metadado;

import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.componente.Popup;
import br.com.persist.componente.Tree;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.Action;
import br.com.persist.componente.MenuPadrao1;

public class MetadadoTree extends Tree {
	private static final long serialVersionUID = 1L;
	private MetadadosPopup metadadosPopup = new MetadadosPopup();
	private final transient List<MetadadoTreeListener> ouvintes;
	private static final Logger LOG = Logger.getGlobal();
	private boolean padraoClickExportacao = true;

	public MetadadoTree() {
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
			if (!e.isPopupTrigger() || popupDesabilitado || getObjetoSelecionado() == null) {
				return;
			}
			TreePath metadadoClicado = getClosestPathForLocation(e.getX(), e.getY());
			TreePath metadadoSelecionado = getSelectionPath();
			popupTrigger = true;
			if (metadadoClicado == null || metadadoSelecionado == null) {
				setSelectionPath(null);
				return;
			}
			Rectangle rectangle = getPathBounds(metadadoClicado);
			if (rectangle == null || !rectangle.contains(e.getX(), e.getY())) {
				setSelectionPath(null);
				return;
			}
			if (metadadoClicado.equals(metadadoSelecionado)) {
				if (metadadoSelecionado.getLastPathComponent() instanceof Metadado) {
					Metadado metadado = (Metadado) metadadoSelecionado.getLastPathComponent();
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
			if (e.getClickCount() >= Constantes.DOIS) {
				if (padraoClickExportacao) {
					ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(MetadadoTree.this, true));
				} else {
					ouvintes.forEach(o -> o.abrirImportacaoFichArquivo(MetadadoTree.this, true));
				}
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class MetadadosPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private Action constraintInfoAction = Action.acaoMenu(MetadadoMensagens.getString("label.constraint_info"),
				null);
		private MenuAbrirExportacaoH menuAbrirExportacaoH = new MenuAbrirExportacaoH();
		private MenuAbrirImportacaoH menuAbrirImportacaoH = new MenuAbrirImportacaoH();
		private MenuAbrirExportacaoC menuAbrirExportacaoC = new MenuAbrirExportacaoC();
		private MenuAbrirImportacaoC menuAbrirImportacaoC = new MenuAbrirImportacaoC();
		private MenuExportacao menuExportacao = new MenuExportacao();

		private MetadadosPopup() {
			add(menuExportacao);
			add(true, menuAbrirExportacaoH);
			add(menuAbrirImportacaoH);
			add(menuAbrirExportacaoC);
			add(menuAbrirImportacaoC);
			addMenuItem(true, constraintInfoAction);
			constraintInfoAction.setActionListener(e -> constraintInfo());
		}

		private void preShow(Metadado metadado) {
			boolean ehTabela = metadado.isTabela();
			menuExportacao.setEnabled(metadado.getEhRaiz() && !metadado.estaVazio());
			constraintInfoAction.setEnabled(metadado.isConstraint());
			menuAbrirExportacaoH.setEnabled(ehTabela);
			menuAbrirImportacaoH.setEnabled(ehTabela);
			menuAbrirExportacaoC.setEnabled(ehTabela);
			menuAbrirImportacaoC.setEnabled(ehTabela);
		}

		private class MenuExportacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuExportacao() {
				super("label.exportar", Icones.ABRIR, false);
				formularioAcao.setActionListener(e -> ouvintes.forEach(o -> o.exportarFormArquivo(MetadadoTree.this)));
				ficharioAcao.setActionListener(e -> ouvintes.forEach(o -> o.exportarFichArquivo(MetadadoTree.this)));
			}
		}

		private class MenuAbrirExportacaoC extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirExportacaoC() {
				super(MetadadoMensagens.getString("label.abrir_exportacao_c"), false, Icones.ABRIR, false);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFormArquivo(MetadadoTree.this, true)));
				ficharioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(MetadadoTree.this, true)));
			}
		}

		private class MenuAbrirExportacaoH extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirExportacaoH() {
				super(MetadadoMensagens.getString("label.abrir_exportacao_h"), false, Icones.ABRIR, false);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFormArquivo(MetadadoTree.this, false)));
				ficharioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(MetadadoTree.this, false)));
			}
		}

		private class MenuAbrirImportacaoC extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirImportacaoC() {
				super(MetadadoMensagens.getString("label.abrir_importacao_c"), false, Icones.ABRIR, false);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoFormArquivo(MetadadoTree.this, true)));
				ficharioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoFichArquivo(MetadadoTree.this, true)));
			}
		}

		private class MenuAbrirImportacaoH extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirImportacaoH() {
				super(MetadadoMensagens.getString("label.abrir_importacao_h"), false, Icones.ABRIR, false);
				formularioAcao.setActionListener(
						e -> ouvintes.forEach(o -> o.abrirImportacaoFormArquivo(MetadadoTree.this, false)));
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
}