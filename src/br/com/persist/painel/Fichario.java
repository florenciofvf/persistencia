package br.com.persist.painel;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.Popup;
import br.com.persist.marca.XMLUtil;

public class Fichario extends JTabbedPane {
	private transient Setor nor = new Setor(Setor.NORTE, Setor.ALPHA_3);
	private transient Setor les = new Setor(Setor.LESTE, Setor.ALPHA_3);
	private transient Setor oes = new Setor(Setor.OESTE, Setor.ALPHA_3);
	private transient Setor sul = new Setor(Setor.SUL, Setor.ALPHA_3);
	private static final Logger LOG = Logger.getGlobal();
	private transient FicharioListener ficharioListener;
	private transient Inclusao inc = new Inclusao();
	private transient Deslocar des = new Deslocar();
	private static final long serialVersionUID = 1L;
	private final PopupFichario popupFichario;
	private static Fichario selecionado;

	public Fichario() {
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		new DropTarget(this, dropTargetListener);
		addMouseListener(mouseListenerFichario);
		addChangeListener(changeListenerInner);
		popupFichario = new PopupFichario();
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this, Transferivel.ACAO_VALIDA, dge -> {
			int indice = getSelectedIndex();
			if (indice != -1) {
				Transferivel aba = (Transferivel) getComponentAt(indice);
				aba.setHint(getToolTipTextAt(indice));
				aba.setTitle(getTitleAt(indice));
				aba.setIndex(indice);
				dge.startDrag(null, aba, dragSourceListener);
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		nor.paint(g);
		sul.paint(g);
		les.paint(g);
		oes.paint(g);
		inc.paint(g);
		des.paint(g);
	}

	private transient DropTargetListener dropTargetListener = new DropTargetListener() {
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!Transferivel.acaoValida(e.getDropAction())) {
				e.rejectDrag();
				invalidar();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!Transferivel.acaoValida(e.getDropAction())) {
				e.rejectDrag();
				invalidar();
			} else {
				nor.localizar(Fichario.this);
				sul.localizar(Fichario.this);
				les.localizar(Fichario.this);
				oes.localizar(Fichario.this);
				inc.localizar(Fichario.this);
				des.localizar(Fichario.this);
				repaint();
			}
		}

		@Override
		public void dragOver(DropTargetDragEvent e) {
			Point p = e.getLocation();
			int x = p.x;
			int y = p.y;
			nor.selecionado = nor.contem(x, y);
			sul.selecionado = sul.contem(x, y);
			les.selecionado = les.contem(x, y);
			oes.selecionado = oes.contem(x, y);
			inc.selecionado = inc.contem(x, y);
			des.selecionado = des.contem(x, y);
			repaint();
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			invalidar();
		}

		private void invalidar() {
			nor.valido = false;
			sul.valido = false;
			les.valido = false;
			oes.valido = false;
			inc.valido = false;
			des.valido = false;
			repaint();
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			if (!Transferivel.acaoValida(e.getDropAction())) {
				e.rejectDrop();
				invalidar();
			} else {
				Transferable transferable = e.getTransferable();
				if (transferable != null) {
					DataFlavor[] flavors = transferable.getTransferDataFlavors();
					if (flavors != null && flavors.length > 0) {
						processarArrastado(e, transferable, flavors[0]);
					}
				}
			}
		}

		private void processarArrastado(DropTargetDropEvent e, Transferable transferable, DataFlavor flavor) {
			if (Transferivel.flavor.equals(flavor)) {
				try {
					Transferivel objeto = (Transferivel) transferable.getTransferData(flavor);
					Setor setor = Setor.get(e, nor, sul, les, oes, inc, des);
					if (valido(objeto, setor)) {
						e.acceptDrop(Transferivel.ACAO_VALIDA);
						setor.dropTarget = Fichario.this;
						setor.point = e.getLocation();
						objeto.setSetor(setor);
						e.dropComplete(true);
					} else {
						e.rejectDrop();
					}
					invalidar();
				} catch (Exception ex) {
					e.rejectDrop();
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Fichario.this);
				}
			} else {
				e.rejectDrop();
			}
		}

		private boolean valido(Transferivel objeto, Setor setor) {
			if (objeto == null || setor == null) {
				return false;
			}
			if (Setor.INCLUIR == setor.local && contemObjeto(objeto)) {
				return false;
			}
			if (Setor.DESLOCAR == setor.local && contemObjeto(objeto) && getTabCount() > 1) {
				return true;
			}
			return !(getTabCount() == 1 && getComponentAt(0) == objeto);
		}

		private boolean contemObjeto(Transferivel objeto) {
			for (int i = 0; i < getTabCount(); i++) {
				if (getComponentAt(i) == objeto) {
					return true;
				}
			}
			return false;
		}
	};

	public void processar(Map<String, Object> map) {
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) instanceof Transferivel) {
				((Transferivel) getComponentAt(i)).processar(this, i, map);
			}
		}
	}

	public Transferivel getTransferivel(File file) {
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) instanceof Transferivel && ((Transferivel) getComponentAt(i)).associadoA(file)) {
				return (Transferivel) getComponentAt(i);
			}
		}
		return null;
	}

	public void salvar(XMLUtil util) {
		util.abrirTag2("fichario");
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) instanceof Transferivel) {
				((Transferivel) getComponentAt(i)).salvar(util);
			}
		}
		util.finalizarTag("fichario");
	}

	public void excluir(Transferivel objeto) throws SeparadorException {
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) == objeto) {
				removeTabAt(i);
				checarVazio();
				break;
			}
		}
	}

	public boolean contem(Transferivel objeto) {
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) == objeto) {
				return true;
			}
		}
		return false;
	}

	private transient DragSourceListener dragSourceListener = new DragSourceListener() {
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
			if (dsde.getDropSuccess()) {
				DragSourceContext context = (DragSourceContext) dsde.getSource();
				Transferivel objeto = (Transferivel) context.getTransferable();
				Setor setor = objeto.getSetor();
				objeto.setSetor(null);
				if (setor != null) {
					try {
						if (Setor.DESLOCAR != setor.local) {
							remove(objeto);
							checarVazio();
						}
						setor.processar(objeto);
					} catch (SeparadorException ex) {
						Util.mensagem(Fichario.this, ex.getMessage());
					}
				}
			}
		}
	};

	public void checarVazio() throws SeparadorException {
		if (getTabCount() == 0 && ficharioListener != null) {
			ficharioListener.ficharioVazio(this);
		}
	}

	public FicharioListener getFicharioListener() {
		return ficharioListener;
	}

	public void setFicharioListener(FicharioListener ficharioListener) {
		this.ficharioListener = ficharioListener;
	}

	private transient MouseListener mouseListenerFichario = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupFichario.show(Fichario.this, e.getX(), e.getY());
			}
		}
	};

	private transient ChangeListener changeListenerInner = e -> Fichario.setSelecionado(Fichario.this);

	public boolean estaSelecionado() {
		return Fichario.selecionado == this;
	}

	public static Fichario getSelecionado() {
		return selecionado;
	}

	public static void setSelecionado(Fichario selecionado) {
		Fichario.selecionado = selecionado;
	}

	private class PopupFichario extends Popup {
		private Action fechar = actionMenu("label.fechar");
		private static final long serialVersionUID = 1L;

		PopupFichario() {
			addMenuItem(fechar);
			fechar.setActionListener(e -> fechar());
		}

		private void fechar() {
			try {
				int indice = getSelectedIndex();
				if (indice != -1) {
					removeTabAt(indice);
					checarVazio();
				}
			} catch (SeparadorException ex) {
				Util.mensagem(Fichario.this, ex.getMessage());
			}
		}
	}
}

class Inclusao extends Setor {
	Inclusao() {
		super(INCLUIR, 0.4f);
	}

	@Override
	void localizar(Component c) {
		y = metade + 2;
		valido = true;
	}

	@Override
	void paint(Graphics g) {
		if (!valido) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.drawRect(x, y, larguraAltura, larguraAltura);
		if (selecionado) {
			g.fillRect(x, y, larguraAltura, larguraAltura);
		}
	}

	@Override
	void processar(Transferivel objeto) {
		Fichario fichario = (Fichario) dropTarget;
		fichario.addTab(objeto.getTitle(), objeto);
		int indice = fichario.getTabCount() - 1;
		fichario.setSelectedIndex(indice);
		fichario.setToolTipTextAt(indice, objeto.getHint());
		dropTarget = null;
	}
}

class Deslocar extends Setor {
	Deslocar() {
		super(DESLOCAR, 0.4f);
	}

	@Override
	void localizar(Component c) {
		super.localizar(c);
		valido = true;
	}

	@Override
	boolean contem(int posX, int posY) {
		return (posX >= x && posX <= x + dimension.width) && (posY >= y && posY <= y + metade);
	}

	@Override
	void paint(Graphics g) {
		if (!valido) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2.drawRect(x, y, dimension.width, metade);
		if (selecionado) {
			g.fillRect(x, y, dimension.width, metade);
		}
	}

	@Override
	void processar(Transferivel objeto) {
		Fichario fichario = (Fichario) dropTarget;
		int destino = fichario.indexAtLocation(point.x, point.y);
		int origem = objeto.getIndex();
		if (origem != -1 && destino != -1 && origem != destino) {
			inverter(origem, destino, fichario);
		}
		dropTarget = null;
		fichario.repaint();
	}

	private void inverter(int origem, int destino, Fichario dropTarget) {
		Component tab = dropTarget.getTabComponentAt(origem);
		Component cmp = dropTarget.getComponentAt(origem);
		String hint = dropTarget.getToolTipTextAt(origem);
		String titulo = dropTarget.getTitleAt(origem);
		Icon icon = dropTarget.getIconAt(origem);
		dropTarget.remove(origem);
		dropTarget.insertTab(titulo, icon, cmp, hint, destino);
		dropTarget.setTabComponentAt(destino, tab);
		dropTarget.setSelectedIndex(destino);
	}
}