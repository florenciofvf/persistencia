package br.com.persist.plugins.objeto;

import java.awt.Component;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoDesktop;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Popup;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.auto.GrupoBuscaAuto;
import br.com.persist.plugins.objeto.auto.GrupoBuscaAutoApos;
import br.com.persist.plugins.objeto.auto.GrupoLinkAuto;
import br.com.persist.plugins.objeto.auto.TabelaBuscaAuto;
import br.com.persist.plugins.objeto.auto.TabelaBuscaAutoApos;
import br.com.persist.plugins.objeto.auto.TabelaLinkAuto;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalContainer;
import br.com.persist.plugins.objeto.internal.InternalFormulario;
import br.com.persist.plugins.objeto.internal.InternalTransferidor;
import br.com.persist.plugins.objeto.vinculo.Grupo;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class Desktop extends AbstratoDesktop implements Pagina {
	private static final long serialVersionUID = 1L;
	private final DesktopPopup popup = new DesktopPopup();
	private static final Logger LOG = Logger.getGlobal();
	private boolean ajusteAutomatico = true;
	private boolean ajusteAutomaticoForm;
	private boolean abortarFecharComESC;

	public Desktop(boolean extensao) {
		if (!extensao) {
			addMouseListener(mouseListenerInner);
		}
		new DropTarget(this, dropTargetListener);
		abortarFecharComESC = Preferencias.isAbortarFecharComESC();
	}

	public void atualizarFormularios() {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				((InternalFormulario) frame).atualizarFormulario();
			}
		}
	}

	public void limpar2() {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				((InternalFormulario) frame).limpar2();
			}
		}
	}

	@Override
	public void empilharFormulariosImpl() {
		JInternalFrame[] frames = getAllFrames();
		if (frames.length > 0) {
			boolean salvar = false;
			Variavel variavelDeltaY = VariavelProvedor.getVariavel(Constantes.DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL);
			if (variavelDeltaY == null) {
				variavelDeltaY = new Variavel(Constantes.DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL,
						Constantes.VAZIO + Constantes.QUARENTA);
				VariavelProvedor.adicionar(variavelDeltaY);
				salvar = true;
			}
			if (salvar) {
				VariavelProvedor.salvar();
				VariavelProvedor.inicializar();
			}
			Arrays.sort(frames, (o1, o2) -> o1.getY() - o2.getY());
			JInternalFrame referencia = primeiroVisivel(frames);
			if (referencia != null) {
				empilhar(frames, referencia, variavelDeltaY.getInteiro(Constantes.QUARENTA));
			}
		}
	}

	private void empilhar(JInternalFrame[] frames, JInternalFrame referencia, int deltaY) {
		int y = referencia.getY() + referencia.getHeight() + deltaY;
		for (JInternalFrame frame : frames) {
			if (!frame.isVisible() || frame == referencia) {
				continue;
			}
			frame.setLocation(frame.getX(), y);
			y = frame.getY() + (frame.isIcon() ? 10 : frame.getHeight()) + deltaY;
		}
	}

	private JInternalFrame primeiroVisivel(JInternalFrame[] frames) {
		for (JInternalFrame frame : frames) {
			if (frame.isVisible()) {
				return frame;
			}
		}
		return null;
	}

	@Override
	public void aproximarObjetoFormularioImpl(boolean objetoAoFormulario, boolean updateTree) {
		boolean salvar = false;
		Variavel variavelDeltaX = VariavelProvedor.getVariavel(Constantes.DELTA_X_AJUSTE_FORM_OBJETO);
		Variavel variavelDeltaY = VariavelProvedor.getVariavel(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO);
		if (variavelDeltaX == null) {
			variavelDeltaX = new Variavel(Constantes.DELTA_X_AJUSTE_FORM_OBJETO, Constantes.VAZIO + Constantes.TRINTA);
			VariavelProvedor.adicionar(variavelDeltaX);
			salvar = true;
		}
		if (variavelDeltaY == null) {
			variavelDeltaY = new Variavel(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO, Constantes.VAZIO + Constantes.TRINTA);
			VariavelProvedor.adicionar(variavelDeltaY);
			salvar = true;
		}
		if (salvar) {
			VariavelProvedor.salvar();
			VariavelProvedor.inicializar();
		}
		for (JInternalFrame frame : getAllFrames()) {
			if (!frame.isVisible()) {
				continue;
			}
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (objetoAoFormulario) {
					interno.aproximarObjetoAoFormulario(variavelDeltaX.getInteiro(Constantes.TRINTA),
							variavelDeltaY.getInteiro(Constantes.TRINTA));
				} else {
					interno.aproximarFormularioAoObjeto(variavelDeltaX.getInteiro(Constantes.TRINTA),
							variavelDeltaY.getInteiro(Constantes.TRINTA));
				}
			}
		}
		if (updateTree) {
			SwingUtilities.updateComponentTreeUI(getParent());
		} else {
			repaint();
		}
	}

	private transient MouseAdapter mouseListenerInner = new MouseAdapter() {
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

	private transient DropTargetListener dropTargetListener = new DropTargetListener() {
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
			if (InternalTransferidor.flavor.equals(flavor)) {
				try {
					Object[] array = (Object[]) transferable.getTransferData(flavor);
					Objeto objeto = (Objeto) array[InternalTransferidor.ARRAY_INDICE_OBJ];
					if (!contemReferencia(objeto)) {
						montarEAdicionarInternalFormulario(array, e.getLocation(), null,
								(String) array[InternalTransferidor.ARRAY_INDICE_APE], false, null);
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

	public void montarEAdicionarInternalFormulario(Object[] array, Point point, Graphics g, String apelido,
			boolean buscaAuto, InternalConfig config) {
		Dimension dimension = (Dimension) array[InternalTransferidor.ARRAY_INDICE_DIM];
		Conexao conexao = (Conexao) array[InternalTransferidor.ARRAY_INDICE_CON];
		Objeto objeto = (Objeto) array[InternalTransferidor.ARRAY_INDICE_OBJ];
		if (g == null) {
			g = getGraphics();
		}
		InternalFormulario internnalFormulario = new InternalFormulario(conexao, objeto, g, buscaAuto);
		internnalFormulario.setAbortarFecharComESC(abortarFecharComESC);
		internnalFormulario.getApelidoListener().setApelido(apelido);
		internnalFormulario.setLocation(point);
		internnalFormulario.setSize(dimension);
		internnalFormulario.setVisible(true);
		internnalFormulario.aplicarConfigArquivo(config);
		add(internnalFormulario);
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

	public void buscaAutomatica(GrupoBuscaAuto grupo, String argumentos, InternalContainer container) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				List<TabelaBuscaAuto> tabelas = grupo.getTabelas();
				interno.setProcessadoBuscaAutomatica(false);
				for (TabelaBuscaAuto tabela : tabelas) {
					if (interno.ehTabela(tabela)) {
						interno.getInternalContainer().getObjeto().setTabelaBuscaAuto(tabela);
						interno.buscaAutomatica(tabela.getCampo(), argumentos);
						interno.setProcessadoBuscaAutomatica(true);
						tabela.setProcessado(true);
					}
				}
			}
		}
	}

	public void pesquisar(Grupo grupo, String argumentos) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				List<Referencia> referencias = grupo.getReferencias();
				interno.setProcessadoBuscaAutomatica(false);
				for (Referencia referencia : referencias) {
					if (interno.ehReferencia(referencia)) {
						interno.getInternalContainer().getObjeto().setReferenciaPesquisa(referencia);
						interno.pesquisar(referencia.getCampo(), argumentos);
						interno.setProcessadoBuscaAutomatica(true);
						referencia.setProcessado(true);
					}
				}
			}
		}
	}

	public void pesquisarLink(Grupo grupo, String argumentos) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				List<Referencia> referencias = grupo.getReferenciasLink();
				for (Referencia referencia : referencias) {
					if (interno.ehReferencia(referencia)) {
						interno.pesquisarLink(referencia.getCampo(), argumentos);
					}
				}
			}
		}
	}

	public void pesquisarLink(Referencia ref, String argumentos) {
		Referencia referencia = ref.getGrupo().getReferencia();
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (interno.ehReferencia(referencia)) {
					interno.pesquisarLink(referencia.getCampo(), argumentos);
				}
			}
		}
	}

	public void pesquisarApos(Grupo grupo) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (!interno.isProcessadoBuscaAutomatica()) {
					for (Referencia referencia : grupo.getReferenciasApos()) {
						if (interno.ehReferencia(referencia)) {
							interno.pesquisarApos();
						}
					}
				}
			}
		}
	}

	protected InternalFormulario getInternalFormulario(Objeto objeto) {
		if (objeto == null) {
			return null;
		}
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (interno.ehObjeto(objeto) && interno.ehTabela(objeto)) {
					return interno;
				}
			}
		}
		return null;
	}

	public void buscaAutomaticaApos(InternalContainer container, GrupoBuscaAutoApos grupoApos) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (!interno.isProcessadoBuscaAutomatica()) {
					for (TabelaBuscaAutoApos tabela : grupoApos.getTabelas()) {
						if (executarBuscaAutomaticaApos(interno, tabela, container, false)) {
							interno.buscaAutomaticaApos();
						}
					}
				}
			}
		}
	}

	private boolean executarBuscaAutomaticaApos(InternalFormulario interno, TabelaBuscaAutoApos tabela,
			InternalContainer container, boolean limparFormulariosRestantes) {
		return interno.ehTabela(tabela) || (limparFormulariosRestantes && interno.getInternalContainer() != container);
	}

	public void linkAutomatico(GrupoLinkAuto link, String argumento, InternalContainer container) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
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

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		executarAoAbrirParent();
	}

	public void executarAoAbrirParent() {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				((InternalFormulario) frame).executarAoAbrirFormulario();
			}
		}
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		LOG.log(Level.FINEST, "processar");
	}

	@Override
	public void excluindoDoFichario(Fichario fichario) {
		LOG.log(Level.FINEST, "excluindoDoFichario");
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return DesktopFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return Mensagens.getString(Constantes.LABEL_DESKTOP_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_DESKTOP);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_DESKTOP);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL2;
			}
		};
	}

	@Override
	public File getFile() {
		return null;
	}
}