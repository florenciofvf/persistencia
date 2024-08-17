package br.com.persist.plugins.objeto;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
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
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JInternalFrame;

import br.com.persist.abstrato.AbstratoDesktop;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.DesktopLargura;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Popup;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.FicharioHandler;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.internal.Argumento;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalContainer;
import br.com.persist.plugins.objeto.internal.InternalFormulario;
import br.com.persist.plugins.objeto.internal.InternalTransferidor;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class Desktop extends AbstratoDesktop implements IDesktop, Pagina, FicharioHandler {
	private final DesktopPopup popup = new DesktopPopup();
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final ScrollPane scrollPane;

	public Desktop(Formulario formulario, boolean extensao) {
		super(formulario);
		if (!extensao) {
			addMouseListener(mouseListenerInner);
		}
		new DropTarget(this, dropTargetListener);
		scrollPane = new ScrollPane(this);
	}

	public void configurarLargura(Dimension dimension) {
		if (isAjusteAutoLarguraForm()) {
			setSize(dimension);
			larguras.configurar(DesktopLargura.TOTAL_A_DIREITA);
		}
	}

	public void atualizarFormularios() {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				((InternalFormulario) frame).atualizarFormulario();
			}
		}
	}

	public void limpar3() {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				((InternalFormulario) frame).limpar3();
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
	public boolean ajustarLargura(JInternalFrame frame) {
		if (frame instanceof InternalFormulario) {
			return ((InternalFormulario) frame).isAjustarLargura();
		}
		return true;
	}

	@Override
	public void nivelTransparenciaFormsIgnorados() {
		LOG.log(Level.FINEST, "nivelTransparenciaFormsIgnorados");
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
	public void aproximarObjetoFormularioImpl(boolean objetoAoFormulario, boolean updateTree, JInternalFrame frame) {
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
		aproximar(objetoAoFormulario, variavelDeltaX, variavelDeltaY, frame);
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

	private void aproximar(boolean objetoAoFormulario, Variavel variavelDeltaX, Variavel variavelDeltaY,
			JInternalFrame internalFrame) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame.isVisible() && frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (internalFrame != null && internalFrame != frame) {
					continue;
				}
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
			Util.updateComponentTreeUI(getParent());
		} else {
			repaint();
		}
	}

	private transient MouseAdapter mouseListenerInner = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && getAllFrames().length > 0) {
				popup.preShow();
				popup.show(Desktop.this, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && getAllFrames().length > 0) {
				popup.preShow();
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
					montarEAdicionarInternalFormulario(array, e.getLocation(), false, null);
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

	protected void internoPesquisarAntes(Objeto pesquisador, Objeto pesquisado) throws ObjetoException {
		if (pesquisador == null && pesquisado == null) {
			throw new ObjetoException("pesquisador == null && pesquisado == null");
		}
		if (pesquisador != null) {
			LOG.log(Level.FINEST, pesquisador.getId());
		}
		if (pesquisado != null) {
			LOG.log(Level.FINEST, pesquisado.getId());
		}
	}

	protected boolean contemReferencia(Objeto objeto) {
		if (objeto != null) {
			LOG.log(Level.FINEST, objeto.getId());
		}
		return false;
	}

	public boolean processadoMetadado(Metadado metadado, Point point, boolean labelDireito, boolean checarNomear)
			throws AssistenciaException {
		if (metadado == null) {
			LOG.finest("processadoMetadado(): metadado null.");
		}
		if (point == null) {
			LOG.finest("processadoMetadado(): point null.");
		}
		if (labelDireito) {
			LOG.finest("processadoMetadado(): labelDireito.");
		}
		if (checarNomear) {
			LOG.finest("processadoMetadado(): checarNomear.");
		}
		return false;
	}

	public void montarEAdicionarInternalFormulario(Object[] array, Point point, boolean buscaAuto,
			InternalConfig config) {
		Dimension dimension = (Dimension) array[InternalTransferidor.ARRAY_INDICE_DIM];
		Conexao conexao = (Conexao) array[InternalTransferidor.ARRAY_INDICE_CON];
		Objeto objeto = (Objeto) array[InternalTransferidor.ARRAY_INDICE_OBJ];
		criarAdicionarInternaFormulario(point, buscaAuto, config, dimension, conexao, objeto);
	}

	public static void setComplemento(Conexao conexao, Objeto objeto) {
		if (conexao != null && objeto != null && configComplemento(conexao, objeto)) {
			objeto.setComplemento(conexao.getFiltro());
		}
	}

	private static boolean configComplemento(Conexao conexao, Objeto objeto) {
		return !Util.isEmpty(conexao.getFiltro()) && Util.isEmpty(objeto.getComplemento());
	}

	private void criarAdicionarInternaFormulario(Point point, boolean buscaAuto, InternalConfig config,
			Dimension dimension, Conexao conexao, Objeto objeto) {
		setComplemento(conexao, objeto);
		objeto.setChecarLargura(true);
		InternalFormulario internal = new InternalFormulario(formulario, conexao, objeto, buscaAuto);
		internal.setInternalConfig(config);
		internal.setLocation(point);
		internal.setSize(dimension);
		internal.setVisible(true);
		add(internal);
	}

	private class DesktopPopup extends Popup {
		private static final long serialVersionUID = 1L;

		private DesktopPopup() {
			add(menuAjustar);
			add(true, menuLargura);
			add(true, menuAjuste);
		}

		private void preShow() {
			menuLargura.setTotalDireitoAuto(isAjusteAutoLarguraForm());
			menuAjuste.setNivelTranspFormsIgnorados(false);
		}
	}

	public void pesquisar(Conexao conexao, Pesquisa pesquisa, Argumento argumento, boolean soTotal, boolean emForms)
			throws ObjetoException, AssistenciaException {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				List<Referencia> referencias = pesquisa.getReferencias();
				interno.setProcessadoPesquisa(false);
				pesquisar(conexao, pesquisa, argumento, interno, referencias, soTotal, emForms);
			}
		}
	}

	private void pesquisar(Conexao conexao, Pesquisa pesquisa, Argumento argumento, InternalFormulario interno,
			List<Referencia> referencias, boolean soTotal, boolean emForms) throws ObjetoException {
		for (Referencia referencia : referencias) {
			if (interno.ehReferencia(referencia)) {
				interno.setReferenciaPesquisa(referencia);
				internoPesquisarAntes(pesquisa.getObjeto(), interno.getInternalContainer().getObjeto());
				interno.pesquisar(conexao, pesquisa, referencia, argumento, soTotal, emForms);
				interno.setProcessadoPesquisa(true);
				referencia.setProcessado(true);
			}
		}
	}

	public void pesquisarApos(Objeto fonte, Pesquisa pesquisa) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (!interno.isProcessadoPesquisa()) {
					pesquisarApos(fonte, pesquisa, interno);
				}
			}
		}
	}

	private void pesquisarApos(Objeto fonte, Pesquisa pesquisa, InternalFormulario interno) {
		if (fonte == interno.getInternalContainer().getObjeto()) {
			return;
		}
		for (Referencia referencia : pesquisa.getReferenciasApos()) {
			if (interno.ehReferencia(referencia) || interno.coringa(referencia)) {
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

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		getLarguras().configurar(DesktopLargura.TOTAL);
		getAjustar().usarFormularios(true);
		getAjuste().empilharFormularios();
	}

	@Override
	public void windowActivatedHandler(Window window) {
		for (JInternalFrame frame : getAllFrames()) {
			try {
				frame.setSelected(true);
			} catch (PropertyVetoException e) {
				LOG.log(Level.FINEST, "{0}", e.getMessage());
			}
		}
		adicionadoAoFichario(null);
	}

	@Override
	public void tabActivatedHandler(Fichario fichario) {
		windowActivatedHandler(null);
	}

	@Override
	public void windowOpenedHandler(Window window) {
		LOG.log(Level.FINEST, "windowOpenedHandler");
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		LOG.log(Level.FINEST, "processar");
	}

	@Override
	public void invertidoNoFichario(Fichario fichario) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				((InternalFormulario) frame).invertidoNoFichario(fichario);
			}
		}
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
		return scrollPane;
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