package br.com.persist.metadado;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.Metadado;
import br.com.persist.comp.Popup;
import br.com.persist.comp.Tree;
import br.com.persist.listener.MetadadosListener;
import br.com.persist.modelo.MetadadoModelo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.MenuPadrao1;

public class Metadados extends Tree {
	private static final long serialVersionUID = 1L;
	private MetadadosPopup metadadosPopup = new MetadadosPopup();
	private final transient List<MetadadosListener> ouvintes;
	private static final Logger LOG = Logger.getGlobal();
	private boolean padraoClickExportacao;

	public Metadados() {
		this(new MetadadoModelo());
	}

	public Metadados(TreeModel newModel) {
		super(newModel);
		addMouseListener(mouseListenerInner);
		ouvintes = new ArrayList<>();
		configurar();
	}

	public void adicionarOuvinte(MetadadosListener listener) {
		if (listener == null) {
			return;
		}

		ouvintes.add(listener);
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

			TreePath arvoreCli = getClosestPathForLocation(e.getX(), e.getY());
			TreePath arvoreSel = getSelectionPath();
			popupTrigger = true;

			if (arvoreCli == null || arvoreSel == null) {
				setSelectionPath(null);
				return;
			}

			Rectangle rect = getPathBounds(arvoreCli);

			if (rect == null || !rect.contains(e.getX(), e.getY())) {
				setSelectionPath(null);
				return;
			}

			if (arvoreCli.equals(arvoreSel)) {
				if (arvoreSel.getLastPathComponent() instanceof Metadado) {
					Metadado metadado = (Metadado) arvoreSel.getLastPathComponent();
					metadadosPopup.preShow(metadado);
					metadadosPopup.show(Metadados.this, e.getX(), e.getY());
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
					ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(Metadados.this));
				} else {
					ouvintes.forEach(o -> o.abrirImportacaoFichArquivo(Metadados.this));
				}
			}
		}
	};

	public void desabilitarPopup() {
		popupDesabilitado = true;
	}

	private class MetadadosPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private MenuAbrirExportacao menuAbrirExportacao = new MenuAbrirExportacao();
		private MenuAbrirImportacao menuAbrirImportacao = new MenuAbrirImportacao();

		public MetadadosPopup() {
			add(menuAbrirExportacao);
			add(menuAbrirImportacao);
		}

		private void preShow(Metadado metadado) {
			boolean ehTabela = metadado.isTabela();
			menuAbrirExportacao.setEnabled(ehTabela);
			menuAbrirImportacao.setEnabled(ehTabela);
		}

		class MenuAbrirExportacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirExportacao() {
				super("label.abrir_exportacao", Icones.ABRIR, false);

				formularioAcao
						.setActionListener(e -> ouvintes.forEach(o -> o.abrirExportacaoFormArquivo(Metadados.this)));
				ficharioAcao
						.setActionListener(e -> ouvintes.forEach(o -> o.abrirExportacaoFichArquivo(Metadados.this)));
			}
		}

		class MenuAbrirImportacao extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuAbrirImportacao() {
				super("label.abrir_importacao", Icones.ABRIR, false);

				formularioAcao
						.setActionListener(e -> ouvintes.forEach(o -> o.abrirImportacaoFormArquivo(Metadados.this)));
				ficharioAcao
						.setActionListener(e -> ouvintes.forEach(o -> o.abrirImportacaoFichArquivo(Metadados.this)));
			}
		}
	}

	public Metadado getRaiz() {
		MetadadoModelo modelo = (MetadadoModelo) getModel();
		return (Metadado) modelo.getRoot();
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

	public String ordemExpImp(boolean exp) {
		return getRaiz().ordemExpImp(exp);
	}
}