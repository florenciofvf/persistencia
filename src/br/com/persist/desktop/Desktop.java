package br.com.persist.desktop;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.busca_auto.GrupoBuscaAutoApos;
import br.com.persist.busca_auto.TabelaBuscaAuto;
import br.com.persist.busca_auto.TabelaBuscaAutoApos;
import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.componente.Popup;
import br.com.persist.conexao.Conexao;
import br.com.persist.fichario.Fichario;
import br.com.persist.link_auto.GrupoLinkAuto;
import br.com.persist.link_auto.TabelaLinkAuto;
import br.com.persist.metadado.Metadado;
import br.com.persist.objeto.Objeto;
import br.com.persist.objeto.ObjetoContainer;
import br.com.persist.objeto.ObjetoContainerFormularioInterno;
import br.com.persist.principal.Formulario;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.IIni;
import br.com.persist.util.Transferidor;
import br.com.persist.util.Util;
import br.com.persist.variaveis.VariaveisModelo;

public class Desktop extends AbstratoDesktop implements IIni, Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final DesktopPopup popup = new DesktopPopup();
	private static final Logger LOG = Logger.getGlobal();
	private boolean ajusteAutomatico = true;
	protected final Formulario formulario;
	private boolean ajusteAutomaticoForm;
	private boolean abortarFecharComESC;

	public Desktop(Formulario formulario, boolean superficie) {
		if (!superficie) {
			addMouseListener(mouseAdapter);
		}
		new DropTarget(this, listener);
		this.formulario = formulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	@Override
	public void ini(Graphics graphics) {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				interno.ini(graphics);
			}
		}
	}

	public void atualizarFormularios() {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				interno.atualizarFormulario();
			}
		}
	}

	public void limpar2() {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				interno.limpar2();
			}
		}
	}

	@Override
	public void empilharFormulariosImpl() {
		JInternalFrame[] frames = getAllFrames();

		if (frames.length > 0) {
			boolean salvar = false;

			ChaveValor cvDeltaY = VariaveisModelo.get(Constantes.DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL);

			if (cvDeltaY == null) {
				cvDeltaY = new ChaveValor(Constantes.DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL,
						Constantes.VAZIO + Constantes.QUARENTA);
				VariaveisModelo.adicionar(cvDeltaY);
				salvar = true;
			}

			if (salvar) {
				VariaveisModelo.salvar();
				VariaveisModelo.inicializar();
			}

			Arrays.sort(frames, (o1, o2) -> o1.getY() - o2.getY());

			JInternalFrame frame = frames[0];
			int deltaY = cvDeltaY.getInteiro(Constantes.QUARENTA);
			int y = frame.getY() + frame.getHeight() + deltaY;

			for (int i = 1; i < frames.length; i++) {
				frame = frames[i];
				frame.setLocation(frame.getX(), y);
				y = frame.getY() + (frame.isIcon() ? 10 : frame.getHeight()) + deltaY;
			}
		}
	}

	@Override
	public void aproximarObjetoFormularioImpl(boolean objetoAoFormulario, boolean updateTree) {
		JInternalFrame[] frames = getAllFrames();

		boolean salvar = false;

		ChaveValor cvDeltaX = VariaveisModelo.get(Constantes.DELTA_X_AJUSTE_FORM_OBJETO);
		ChaveValor cvDeltaY = VariaveisModelo.get(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO);

		if (cvDeltaX == null) {
			cvDeltaX = new ChaveValor(Constantes.DELTA_X_AJUSTE_FORM_OBJETO, Constantes.VAZIO + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvDeltaX);
			salvar = true;
		}

		if (cvDeltaY == null) {
			cvDeltaY = new ChaveValor(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO, Constantes.VAZIO + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvDeltaY);
			salvar = true;
		}

		if (salvar) {
			VariaveisModelo.salvar();
			VariaveisModelo.inicializar();
		}

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;

				if (objetoAoFormulario) {
					interno.aproximarObjetoAoFormulario(cvDeltaX.getInteiro(Constantes.TRINTA),
							cvDeltaY.getInteiro(Constantes.TRINTA));
				} else {
					interno.aproximarFormularioAoObjeto(cvDeltaX.getInteiro(Constantes.TRINTA),
							cvDeltaY.getInteiro(Constantes.TRINTA));
				}
			}
		}

		if (updateTree) {
			SwingUtilities.updateComponentTreeUI(getParent());
		} else {
			repaint();
		}
	}

	private transient MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && getAllFrames().length > 0) {
				popup.show(Desktop.this, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && getAllFrames().length > 0) {
				popup.show(Desktop.this, e.getX(), e.getY());
			}
		}
	};

	private transient DropTargetListener listener = new DropTargetListener() {
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrag();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrag();
			}
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			LOG.log(Level.FINEST, "dragOver");
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			LOG.log(Level.FINEST, "dragExit");
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrop();
				return;
			}

			Transferable transferable = e.getTransferable();

			if (transferable == null) {
				return;
			}

			DataFlavor[] flavors = transferable.getTransferDataFlavors();

			if (flavors == null || flavors.length < 1) {
				return;
			}

			DataFlavor flavor = flavors[0];
			boolean completado = false;

			if (Transferidor.flavor.equals(flavor)) {
				try {
					Object[] array = (Object[]) transferable.getTransferData(flavor);
					Objeto objeto = (Objeto) array[Util.ARRAY_INDICE_OBJ];

					if (!contemReferencia(objeto)) {
						addForm(array, e.getLocation(), null, (String) array[Util.ARRAY_INDICE_APE], false, null);
						completado = true;
					}
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
				}
			} else if (Metadado.flavor.equals(flavor)) {
				try {
					Metadado metadado = (Metadado) transferable.getTransferData(flavor);

					if (processadoMetadado(metadado, e.getLocation(), false)) {
						completado = true;
					}
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
				}
			}

			if (completado) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
				e.dropComplete(true);
			} else {
				e.rejectDrop();
			}
		}

		private boolean validoSoltar(DropTargetDragEvent e) {
			return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
		}

		private boolean validoSoltar(DropTargetDropEvent e) {
			return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
		}
	};

	protected boolean contemReferencia(Objeto objeto) {
		return false;
	}

	protected boolean processadoMetadado(Metadado metadado, Point point, boolean labelDireito) {
		return false;
	}

	public void addForm(Object[] array, Point point, Graphics g, String apelido, boolean buscaAuto,
			ConfigArquivo config) {
		Dimension dimension = (Dimension) array[Util.ARRAY_INDICE_DIM];
		Conexao conexao = (Conexao) array[Util.ARRAY_INDICE_CON];
		Objeto objeto = (Objeto) array[Util.ARRAY_INDICE_OBJ];

		if (g == null) {
			g = getGraphics();
		}

		ObjetoContainerFormularioInterno form = new ObjetoContainerFormularioInterno(formulario, conexao, objeto, g,
				buscaAuto);
		form.setAbortarFecharComESC(abortarFecharComESC);
		form.getApelidoListener().setApelido(apelido);
		form.setLocation(point);
		form.setSize(dimension);
		form.setVisible(true);
		form.aplicarConfigArquivo(config);

		add(form);
	}

	private class DesktopPopup extends Popup {
		private static final long serialVersionUID = 1L;

		private DesktopPopup() {
			add(menuAlinhamento);
			add(true, menuLargura);
			add(true, menuAjustar);
			add(true, menuAjuste);
		}
	}

	public void buscaAutomatica(GrupoBuscaAuto grupo, String argumentos, ObjetoContainer container) {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				List<TabelaBuscaAuto> tabelas = grupo.getTabelas();

				for (TabelaBuscaAuto tabela : tabelas) {
					if (interno.ehTabela(tabela)) {
						interno.getObjetoContainer().getObjeto().setTabelaBuscaAuto(tabela);
						interno.buscaAutomatica(tabela.getCampo(), argumentos);
						tabela.setProcessado(true);
					}
				}
			}
		}
	}

	protected ObjetoContainerFormularioInterno getObjetoContainerFormularioInterno(Objeto objeto) {
		if (objeto == null) {
			return null;
		}

		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;

				if (interno.ehObjeto(objeto) && interno.ehTabela(objeto)) {
					return interno;
				}
			}
		}

		return null;
	}

	public void buscaAutomaticaApos(GrupoBuscaAutoApos grupoApos, ObjetoContainer container) {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				List<TabelaBuscaAutoApos> tabelas = grupoApos.getTabelas();

				for (TabelaBuscaAutoApos tabela : tabelas) {
					if (interno.ehTabela(tabela)) {
						interno.buscaAutomaticaApos();
					}
				}
			}
		}
	}

	public void linkAutomatico(GrupoLinkAuto link, String argumento, ObjetoContainer container) {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				List<TabelaLinkAuto> tabelas = link.getTabelas();

				for (TabelaLinkAuto tabela : tabelas) {
					if (interno.ehTabela(tabela)) {
						interno.linkAutomatico(tabela.getCampo(), argumento);
					}
				}
			}
		}
	}

	public boolean isAbortarFecharComESC() {
		return abortarFecharComESC;
	}

	public void setAbortarFecharComESC(boolean abortarFecharComESC) {
		this.abortarFecharComESC = abortarFecharComESC;
	}

	public boolean isAjusteAutomatico() {
		return ajusteAutomatico;
	}

	public void setAjusteAutomatico(boolean ajusteAutomatico) {
		this.ajusteAutomatico = ajusteAutomatico;
	}

	public boolean isAjusteAutomaticoForm() {
		return ajusteAutomaticoForm;
	}

	public void setAjusteAutomaticoForm(boolean ajusteAutomaticoForm) {
		this.ajusteAutomaticoForm = ajusteAutomaticoForm;
	}
}