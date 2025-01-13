package br.com.persist.painel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Separador extends JSplitPane implements FicharioListener {
	private transient Setor nor = new Setor(Setor.NORTE, Setor.ALPHA_7);
	private transient Setor les = new Setor(Setor.LESTE, Setor.ALPHA_7);
	private transient Setor oes = new Setor(Setor.OESTE, Setor.ALPHA_7);
	private transient Setor sul = new Setor(Setor.SUL, Setor.ALPHA_7);
	private static final long serialVersionUID = 1L;
	Component substituto;

	public Separador(int orientation, Component left, Component right) {
		super(orientation, get(left), get(right));
		new DropTarget(this, dropTargetListener);
		setBorder(BorderFactory.createEmptyBorder());
		setOneTouchExpandable(true);
		setContinuousLayout(true);
		SwingUtilities.invokeLater(() -> setDividerLocation(0.5));
	}

	private static Component get(Component c) {
		if (c instanceof Transferivel) {
			Transferivel aba = (Transferivel) c;
			Fichario fichario = new Fichario();
			fichario.addTab(aba.getTitle(), aba);
			c = fichario;
		}
		return c;
	}

	public static Separador horizontal(Component left, Component right) {
		return new Separador(HORIZONTAL_SPLIT, left, right);
	}

	public static Separador vertical(Component left, Component right) {
		return new Separador(VERTICAL_SPLIT, left, right);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		nor.paint(g);
		sul.paint(g);
		les.paint(g);
		oes.paint(g);
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
				nor.localizar(Separador.this);
				sul.localizar(Separador.this);
				les.localizar(Separador.this);
				oes.localizar(Separador.this);
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
					Setor setor = Setor.get(e, nor, sul, les, oes);
					if (valido(objeto, setor)) {
						e.acceptDrop(Transferivel.ACAO_VALIDA);
						setor.dropTarget = Separador.this;
						objeto.setSetor(setor);
						e.dropComplete(true);
					} else {
						e.rejectDrop();
					}
					invalidar();
				} catch (Exception ex) {
					e.rejectDrop();
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Separador.this);
				}
			} else {
				e.rejectDrop();
			}
		}

		private boolean valido(Transferivel objeto, Setor setor) {
			return objeto != null && setor != null;
		}
	};

	@Override
	public void ficharioVazio(Fichario fichario) throws SeparadorException {
		if (leftComponent == fichario) {
			setLeftComponent(null);
			substituirPor(rightComponent);
		} else if (rightComponent == fichario) {
			setRightComponent(null);
			substituirPor(leftComponent);
		} else {
			throw new SeparadorException();
		}
	}

	@Override
	public void abaSelecionada(Fichario fichario, Transferivel transferivel) {
		//
	}

	private void substituirPor(Component novo) {
		setFicharioListener(novo, null);
		Container parent = getParent();
		substituto = novo;
		if (parent instanceof Separador) {
			Separador separador = (Separador) parent;
			if (separador.getLeftComponent() == this) {
				separador.setLeftComponent(novo);
			} else {
				separador.setRightComponent(novo);
			}
		} else {
			parent.remove(this);
			parent.add(novo);
		}
		SwingUtilities.updateComponentTreeUI(parent);
	}

	@Override
	public void setLeftComponent(Component comp) {
		super.setLeftComponent(comp);
		setFicharioListener(comp, this);
	}

	@Override
	public void setRightComponent(Component comp) {
		super.setRightComponent(comp);
		setFicharioListener(comp, this);
	}

	private void setFicharioListener(Component comp, FicharioListener listener) {
		if (comp instanceof Fichario) {
			((Fichario) comp).setFicharioListener(listener);
		}
	}

	public Fichario getFichario(Transferivel objeto) {
		if (leftComponent instanceof Fichario && ((Fichario) leftComponent).contem(objeto)) {
			return (Fichario) leftComponent;
		}

		if (rightComponent instanceof Fichario && ((Fichario) rightComponent).contem(objeto)) {
			return (Fichario) rightComponent;
		}

		Fichario fichario = null;

		if (leftComponent instanceof Separador) {
			fichario = ((Separador) leftComponent).getFichario(objeto);
		}

		if (fichario != null) {
			return fichario;
		}

		if (rightComponent instanceof Separador) {
			fichario = ((Separador) rightComponent).getFichario(objeto);
		}

		return fichario;
	}

	public Transferivel getTransferivel(File file) {
		Transferivel objeto = null;
		if (leftComponent instanceof Fichario) {
			objeto = ((Fichario) leftComponent).getTransferivel(file);
		}

		if (objeto != null) {
			return objeto;
		}

		if (rightComponent instanceof Fichario) {
			objeto = ((Fichario) rightComponent).getTransferivel(file);
		}

		if (objeto != null) {
			return objeto;
		}

		if (leftComponent instanceof Separador) {
			objeto = ((Separador) leftComponent).getTransferivel(file);
		}

		if (objeto != null) {
			return objeto;
		}

		if (rightComponent instanceof Separador) {
			objeto = ((Separador) rightComponent).getTransferivel(file);
		}
		return objeto;
	}

	public void processar(Map<String, Object> map) {
		if (leftComponent instanceof Fichario) {
			((Fichario) leftComponent).processar(map);
		}

		if (leftComponent instanceof Separador) {
			((Separador) leftComponent).processar(map);
		}

		if (rightComponent instanceof Fichario) {
			((Fichario) rightComponent).processar(map);
		}

		if (rightComponent instanceof Separador) {
			((Separador) rightComponent).processar(map);
		}
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("separador").atributo("orientacao", getOrientation()).fecharTag();
		final String left = "left";
		final String right = "right";
		if (leftComponent instanceof Fichario) {
			util.abrirTag2(left);
			((Fichario) leftComponent).salvar(util);
			util.finalizarTag(left);
		}

		if (leftComponent instanceof Separador) {
			util.abrirTag2(left);
			((Separador) leftComponent).salvar(util);
			util.finalizarTag(left);
		}

		if (rightComponent instanceof Fichario) {
			util.abrirTag2(right);
			((Fichario) rightComponent).salvar(util);
			util.finalizarTag(right);
		}

		if (rightComponent instanceof Separador) {
			util.abrirTag2(right);
			((Separador) rightComponent).salvar(util);
			util.finalizarTag(right);
		}
		util.finalizarTag("separador");
	}

	public Fichario getFicharioSelecionado() {
		if (leftComponent instanceof Fichario && ((Fichario) leftComponent).estaSelecionado()) {
			return (Fichario) leftComponent;
		}

		if (rightComponent instanceof Fichario && ((Fichario) rightComponent).estaSelecionado()) {
			return (Fichario) rightComponent;
		}

		Fichario fichario = null;

		if (leftComponent instanceof Separador) {
			fichario = ((Separador) leftComponent).getFicharioSelecionado();
		}

		if (fichario != null) {
			return fichario;
		}

		if (rightComponent instanceof Separador) {
			fichario = ((Separador) rightComponent).getFicharioSelecionado();
		}

		return fichario;
	}

	public Fichario getFicharioPrimeiro() {
		if (leftComponent instanceof Fichario) {
			return (Fichario) leftComponent;
		}

		if (rightComponent instanceof Fichario) {
			return (Fichario) rightComponent;
		}

		Fichario fichario = null;

		if (leftComponent instanceof Separador) {
			fichario = ((Separador) leftComponent).getFicharioPrimeiro();
		}

		if (fichario != null) {
			return fichario;
		}

		if (rightComponent instanceof Separador) {
			fichario = ((Separador) rightComponent).getFicharioPrimeiro();
		}

		return fichario;
	}
}