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
import java.util.concurrent.atomic.AtomicBoolean;
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
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Popup;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalContainer;
import br.com.persist.plugins.objeto.internal.InternalFormulario;
import br.com.persist.plugins.objeto.internal.InternalTransferidor;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class Desktop extends AbstratoDesktop implements Pagina {
	private static final long serialVersionUID = 1L;
	private final DesktopPopup popup = new DesktopPopup();
	private static final Logger LOG = Logger.getGlobal();
	private boolean ajusteAutomatico = true;
	private boolean ajusteAutomaticoForm;
	private boolean ajusteLarguraForm;

	public Desktop(boolean extensao) {
		if (!extensao) {
			addMouseListener(mouseListenerInner);
		}
		new DropTarget(this, dropTargetListener);
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

	public void limparOutros(InternalContainer invocador) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				((InternalFormulario) frame).limparOutros(invocador);
			}
		}
	}

	@Override
	public void empilharFormulariosImpl() {
		JInternalFrame[] frames = getAllFrames();
		if (frames.length > 0) {
			boolean salvar = false;
			Variavel variavelDeltaY = VariavelProvedor
					.getVariavel(ObjetoConstantes.DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL);
			if (variavelDeltaY == null) {
				variavelDeltaY = new Variavel(ObjetoConstantes.DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL,
						Constantes.VAZIO + Constantes.QUARENTA);
				VariavelProvedor.adicionar(variavelDeltaY);
				salvar = true;
			}
			checarAtualizarVariavelProvedor(salvar);
			Arrays.sort(frames, (o1, o2) -> o1.getY() - o2.getY());
			JInternalFrame referencia = primeiroVisivel(frames);
			if (referencia != null) {
				empilhar(frames, referencia, variavelDeltaY.getInteiro(Constantes.QUARENTA));
			}
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

	@Override
	public void aproximarObjetoFormularioImpl(boolean objetoAoFormulario, boolean updateTree) {
		boolean salvar = false;
		Variavel variavelDeltaX = VariavelProvedor.getVariavel(ObjetoConstantes.DELTA_X_AJUSTE_FORM_OBJETO);
		Variavel variavelDeltaY = VariavelProvedor.getVariavel(ObjetoConstantes.DELTA_Y_AJUSTE_FORM_OBJETO);
		if (variavelDeltaX == null) {
			variavelDeltaX = new Variavel(ObjetoConstantes.DELTA_X_AJUSTE_FORM_OBJETO,
					Constantes.VAZIO + Constantes.TRINTA);
			VariavelProvedor.adicionar(variavelDeltaX);
			salvar = true;
		}
		if (variavelDeltaY == null) {
			variavelDeltaY = new Variavel(ObjetoConstantes.DELTA_Y_AJUSTE_FORM_OBJETO,
					Constantes.VAZIO + Constantes.TRINTA);
			VariavelProvedor.adicionar(variavelDeltaY);
			salvar = true;
		}
		checarAtualizarVariavelProvedor(salvar);
		aproximar(objetoAoFormulario, variavelDeltaX, variavelDeltaY);
		updateOuRepaint(updateTree);
	}

	private void checarAtualizarVariavelProvedor(boolean salvar) {
		if (salvar) {
			try {
				VariavelProvedor.salvar();
				VariavelProvedor.inicializar();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}

	private void aproximar(boolean objetoAoFormulario, Variavel variavelDeltaX, Variavel variavelDeltaY) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame.isVisible() && frame instanceof InternalFormulario) {
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
	}

	private void updateOuRepaint(boolean updateTree) {
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
			if (transferable != null) {
				DataFlavor[] flavors = transferable.getTransferDataFlavors();
				if (flavors != null && flavors.length > 0) {
					processarArrastado(e, transferable, flavors);
				}
			}
		}

		private boolean validoSoltar(DropTargetDropEvent e) {
			return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
		}

		private boolean validoSoltar(DropTargetDragEvent e) {
			return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
		}

		private void processarArrastado(DropTargetDropEvent e, Transferable transferable, DataFlavor[] flavors) {
			DataFlavor flavor = flavors[0];
			AtomicBoolean completado = new AtomicBoolean(false);
			processarTransferable(e, transferable, flavor, completado);
			processarTransferableFinal(e, completado);
		}

		private void processarTransferable(DropTargetDropEvent e, Transferable transferable, DataFlavor flavor,
				AtomicBoolean completado) {
			if (InternalTransferidor.flavor.equals(flavor)) {
				processarInternal(e, transferable, flavor, completado);
			} else if (Metadado.flavor.equals(flavor)) {
				processarMetadado(e, transferable, flavor, completado);
			}
		}

		private void processarTransferableFinal(DropTargetDropEvent e, AtomicBoolean completado) {
			if (completado.get()) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
				e.dropComplete(true);
			} else {
				e.rejectDrop();
			}
		}

		private void processarInternal(DropTargetDropEvent e, Transferable transferable, DataFlavor flavor,
				AtomicBoolean processado) {
			try {
				Object[] array = (Object[]) transferable.getTransferData(flavor);
				Objeto objeto = (Objeto) array[InternalTransferidor.ARRAY_INDICE_OBJ];
				if (!contemReferencia(objeto)) {
					montarEAdicionarInternalFormulario(array, e.getLocation(), null, false, null);
					processado.set(true);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
			}
		}

		private void processarMetadado(DropTargetDropEvent e, Transferable transferable, DataFlavor flavor,
				AtomicBoolean processado) {
			try {
				Metadado metadado = (Metadado) transferable.getTransferData(flavor);
				if (processadoMetadado(metadado, e.getLocation(), false, true)) {
					processado.set(true);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
			}
		}
	};

	protected boolean contemReferencia(Objeto objeto) {
		return false;
	}

	protected boolean processadoMetadado(Metadado metadado, Point point, boolean labelDireito, boolean checarNomear) {
		return false;
	}

	public void montarEAdicionarInternalFormulario(Object[] array, Point point, Graphics g, boolean buscaAuto,
			InternalConfig config) {
		Dimension dimension = (Dimension) array[InternalTransferidor.ARRAY_INDICE_DIM];
		Conexao conexao = (Conexao) array[InternalTransferidor.ARRAY_INDICE_CON];
		Objeto objeto = (Objeto) array[InternalTransferidor.ARRAY_INDICE_OBJ];
		if (g == null) {
			g = getGraphics();
		}
		criarAdicionarInternaFormulario(point, g, buscaAuto, config, dimension, conexao, objeto);
	}

	public static void setComplemento(Conexao conexao, Objeto objeto) {
		if (conexao != null && objeto != null) {
			if (configComplemento(conexao, objeto)) {
				objeto.setComplemento(conexao.getFiltro());
			} else if (configFinalConsulta(conexao, objeto)) {
				objeto.setFinalConsulta(conexao.getFinalConsulta());
			}
		}
	}

	private static boolean configComplemento(Conexao conexao, Objeto objeto) {
		return !Util.estaVazio(conexao.getFiltro()) && Util.estaVazio(objeto.getComplemento());
	}

	private static boolean configFinalConsulta(Conexao conexao, Objeto objeto) {
		return !Util.estaVazio(conexao.getFinalConsulta()) && Util.estaVazio(objeto.getFinalConsulta());
	}

	private void criarAdicionarInternaFormulario(Point point, Graphics g, boolean buscaAuto, InternalConfig config,
			Dimension dimension, Conexao conexao, Objeto objeto) {
		setComplemento(conexao, objeto);
		InternalFormulario internal = new InternalFormulario(conexao, objeto, g, buscaAuto);
		internal.setLocation(point);
		internal.setSize(dimension);
		internal.setVisible(true);
		add(internal);
		internal.aplicar(config);
	}

	private class DesktopPopup extends Popup {
		private static final long serialVersionUID = 1L;

		private DesktopPopup() {
			add(menuAjustar);
			add(true, menuLargura);
			add(true, menuAjuste);
		}
	}

	public void pesquisar(Conexao conexao, Pesquisa pesquisa, String argumentos) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				List<Referencia> referencias = pesquisa.getReferencias();
				interno.setProcessadoPesquisa(false);
				pesquisar(conexao, argumentos, interno, referencias);
			}
		}
	}

	private void pesquisar(Conexao conexao, String argumentos, InternalFormulario interno,
			List<Referencia> referencias) {
		for (Referencia referencia : referencias) {
			if (interno.ehReferencia(referencia)) {
				interno.setReferenciaPesquisa(referencia);
				interno.pesquisar(conexao, referencia, argumentos);
				interno.setProcessadoPesquisa(true);
				referencia.setProcessado(true);
			}
		}
	}

	public void pesquisarApos(Pesquisa pesquisa) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (!interno.isProcessadoPesquisa()) {
					pesquisarApos(pesquisa, interno);
				}
			}
		}
	}

	private void pesquisarApos(Pesquisa pesquisa, InternalFormulario interno) {
		for (Referencia referencia : pesquisa.getReferenciasApos()) {
			if (interno.ehReferencia(referencia)) {
				interno.pesquisarApos();
			}
		}
	}

	public void pesquisarLink(List<Referencia> referencias, String argumentos) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame.isVisible() && (frame instanceof InternalFormulario)) {
				InternalFormulario interno = (InternalFormulario) frame;
				pesquisarLink(referencias, argumentos, interno);
			}
		}
	}

	private void pesquisarLink(List<Referencia> referencias, String argumentos, InternalFormulario interno) {
		for (Referencia referencia : referencias) {
			if (interno.ehReferencia(referencia)) {
				interno.pesquisarLink(referencia, argumentos);
			}
		}
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

	public boolean isAjusteLarguraForm() {
		return ajusteLarguraForm;
	}

	public void setAjusteLarguraForm(boolean ajusteLarguraForm) {
		this.ajusteLarguraForm = ajusteLarguraForm;
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
				return ObjetoMensagens.getString(ObjetoConstantes.LABEL_DESKTOP_MIN);
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