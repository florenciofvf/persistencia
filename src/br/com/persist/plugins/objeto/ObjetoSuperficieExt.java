package br.com.persist.plugins.objeto;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.InputMap;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.config.RelacaoDialogo;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalFormulario;

public class ObjetoSuperficieExt extends ObjetoSuperficie {
	final transient HierarquicoVisivelManager hierarquicoVisivelManager;
	final transient HierarquicoAvulsoManager hierarquicoAvulsoManager;
	private static final long serialVersionUID = 1L;
	final transient ThreadManager threadManager;
	final transient MacroManager macroManager;
	final SuperficiePopup2 popup2;
	final SuperficiePopup popup;

	public ObjetoSuperficieExt(Formulario formulario, ObjetoContainer container) {
		super(formulario, container);
		hierarquicoVisivelManager = new HierarquicoVisivelManager(this);
		hierarquicoAvulsoManager = new HierarquicoAvulsoManager(this);
		threadManager = new ThreadManager(this);
		configEstado(ObjetoConstantes.SELECAO);
		macroManager = new MacroManager(this);
		popup2 = new SuperficiePopup2(this);
		popup = new SuperficiePopup(this);
		configurar();
	}

	private void configurar() {
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_T), "thread_processar");
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_Y), "thread_desativar");
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_N), "macro_lista");
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_Z), "zoom_menos");
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_X), "zoom_mais");
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_M), "macro");
		getActionMap().put("thread_processar", macroManager.threadProcessar);
		getActionMap().put("thread_desativar", macroManager.threadDesativar);
		getActionMap().put("macro_lista", macroManager.macroLista);
		getActionMap().put("zoom_menos", macroManager.zoomMenos);
		getActionMap().put("zoom_mais", macroManager.zoomMais);
		getActionMap().put("macro", macroManager.macro);
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (popup != null) {
			Util.updateComponentTreeUI(popup);
		}
		if (popup2 != null) {
			Util.updateComponentTreeUI(popup2);
		}
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public HierarquicoVisivelManager getHierarquicoVisivelManager() {
		return hierarquicoVisivelManager;
	}

	public HierarquicoAvulsoManager getHierarquicoAvulsoManager() {
		return hierarquicoAvulsoManager;
	}

	@Override
	public ThreadManager getThreadManager() {
		return threadManager;
	}

	public MacroManager getMacroManager() {
		return macroManager;
	}

	private transient MouseAdapter mouseAdapterArrasto = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				mousePressedPopup(e);
			} else {
				ultX = e.getX();
				ultY = e.getY();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			boolean shift = e.isShiftDown();
			boolean alt = e.isAltDown();
			int recX = e.getX();
			int recY = e.getY();
			for (Objeto objeto : objetos) {
				if (alt && !shift) {
					objeto.x += recX - ultX;
				} else if (!alt && shift) {
					objeto.y += recY - ultY;
				} else {
					objeto.x += recX - ultX;
					objeto.y += recY - ultY;
				}
			}
			ultX = recX;
			ultY = recY;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				mouseReleasedPopup(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				duploClick(e);
			}
		}
	};

	private transient MouseAdapter mouseAdapterRelacao = new MouseAdapter() {
		Objeto destino;
		Objeto origem;

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				mousePressedPopup(e);
				return;
			}
			for (Relacao relacao : relacoes) {
				relacao.setSelecionado(false);
			}
			ObjetoSuperficieUtil.limparSelecao(ObjetoSuperficieExt.this);
			ultX = e.getX();
			ultY = e.getY();
			origem = null;
			for (Objeto objeto : objetos) {
				if (objeto.contem(ultX, ultY)) {
					objeto.setSelecionado(true);
					origem = objeto;
					break;
				}
			}
			if (origem != null) {
				linha.x1 = origem.x + Objeto.DIAMETRO / 2;
				linha.y1 = origem.y + Objeto.DIAMETRO / 2;
				linha.x2 = linha.x1;
				linha.y2 = linha.y1;
			} else {
				linha.ini();
			}
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int recX = e.getX();
			int recY = e.getY();
			if (origem != null) {
				linha.x2 = recX;
				linha.y2 = recY;
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				mouseReleasedPopup(e);
				return;
			}
			boolean shift = e.isShiftDown();
			boolean alt = e.isAltDown();
			boolean ctrl = alt && shift;
			ultX = e.getX();
			ultY = e.getY();
			destino = null;
			linha.ini();
			for (Objeto objeto : objetos) {
				if (objeto.contem(ultX, ultY)) {
					objeto.setSelecionado(true);
					destino = objeto;
					break;
				}
			}
			if (origem == null || destino == null || origem == destino) {
				repaint();
				return;
			}
			try {
				Relacao relacao = ObjetoSuperficieUtil.getRelacao(ObjetoSuperficieExt.this, origem, destino);
				if (relacao == null) {
					relacao = new Relacao(origem, false, destino, !ctrl);
					addRelacao(relacao);
				}
				repaint();
				if (!ctrl) {
					RelacaoDialogo.criar(container.getFrame(), ObjetoSuperficieExt.this, relacao);
				}
			} catch (ObjetoException | AssistenciaException ex) {
				Util.mensagem(ObjetoSuperficieExt.this, ex.getMessage());
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				duploClick(e);
			}
		}
	};

	private transient MouseAdapter mouseAdapterRotulos = new MouseAdapter() {
		Relacao selecionadoRelacao;
		Objeto selecionadoObjeto;

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				mousePressedPopup(e);
			} else {
				ultX = e.getX();
				ultY = e.getY();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			boolean shift = e.isShiftDown();
			boolean alt = e.isAltDown();
			int recX = e.getX();
			int recY = e.getY();
			if (selecionadoObjeto != null) {
				if (alt && !shift) {
					selecionadoObjeto.deslocamentoXIdDelta(recX - ultX);
				} else if (!alt && shift) {
					selecionadoObjeto.deslocamentoYIdDelta(recY - ultY);
				} else {
					selecionadoObjeto.deslocamentoXIdDelta(recX - ultX);
					selecionadoObjeto.deslocamentoYIdDelta(recY - ultY);
				}
			} else if (selecionadoRelacao != null) {
				if (alt && !shift) {
					selecionadoRelacao.deslocamentoXDescDelta(recX - ultX);
				} else if (!alt && shift) {
					selecionadoRelacao.deslocamentoYDescDelta(recY - ultY);
				} else {
					selecionadoRelacao.deslocamentoXDescDelta(recX - ultX);
					selecionadoRelacao.deslocamentoYDescDelta(recY - ultY);
				}
			}
			ultX = recX;
			ultY = recY;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				mouseReleasedPopup(e);
			} else {
				if (selecionadoObjeto != null) {
					selecionadoObjeto.setSelecionado(false);
				}
				if (selecionadoRelacao != null) {
					selecionadoRelacao.setSelecionado(false);
				}
				selecionadoRelacao = null;
				selecionadoObjeto = null;
				repaint();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				duploClick(e);
			} else {
				int x = e.getX();
				int y = e.getY();
				ObjetoSuperficieUtil.limparSelecao(ObjetoSuperficieExt.this);
				for (Objeto objeto : objetos) {
					if (objeto.contem(x, y)) {
						objeto.setSelecionado(true);
						selecionadoObjeto = objeto;
						break;
					}
				}
				if (selecionadoObjeto == null) {
					for (Relacao relacao : relacoes) {
						if (relacao.contem(x, y)) {
							relacao.setSelecionado(true);
							selecionadoRelacao = relacao;
							break;
						}
					}
				}
				repaint();
			}
		}
	};

	private transient MouseAdapter mouseAdapterSelecao = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			AtomicBoolean sel = new AtomicBoolean(false);
			boolean shift = e.isShiftDown();
			boolean alt = e.isAltDown();
			boolean ctrl = alt && shift;
			selecionadoRelacao = null;
			selecionadoObjeto = null;
			validoArrastar = false;
			totalArrastado = 0;
			int x = e.getX();
			int y = e.getY();
			area.x1 = x;
			area.y1 = y;
			ultX = x;
			ultY = y;
			ObjetoSuperficieUtil.deselRelacoes(ObjetoSuperficieExt.this);
			pressedObjeto(ctrl, x, y, sel);
			if (selecionadoObjeto != null) {
				pressedObjetoFinal(ctrl, sel.get());
				validoArrastar = true;
			} else {
				pressedRelacao(ctrl, x, y);
			}
			repaint();
			if (e.isPopupTrigger() && (selecionadoObjeto != null || selecionadoRelacao != null)) {
				popup.preShow(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(ObjetoSuperficieExt.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.xLocal = x;
				popup2.yLocal = y;
				popup2.preShow(getAllFrames().length > 0);
				popup2.show(ObjetoSuperficieExt.this, x, y);
			}
		}

		private void pressedObjeto(boolean ctrl, int x, int y, AtomicBoolean atom) {
			for (Objeto objeto : objetos) {
				if (objeto.contem(x, y)) {
					if (ctrl) {
						objeto.setSelecionado(!objeto.isSelecionado());
					} else {
						atom.set(objeto.isSelecionado());
						objeto.setSelecionado(true);
					}
					if (objeto.isSelecionado()) {
						selecionadoObjeto = objeto;
					}
					break;
				}
			}
		}

		private void pressedObjetoFinal(boolean ctrl, boolean sel) {
			if (!ctrl && !sel) {
				for (Objeto objeto : objetos) {
					if (objeto != selecionadoObjeto) {
						objeto.setSelecionado(false);
					}
				}
			}
		}

		private void pressedRelacao(boolean ctrl, int x, int y) {
			if (!ctrl) {
				ObjetoSuperficieUtil.limparSelecao(ObjetoSuperficieExt.this);
				for (Relacao relacao : relacoes) {
					if (relacao.contem(x, y)) {
						relacao.setSelecionado(true);
						selecionadoRelacao = relacao;
						break;
					}
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			boolean shift = e.isShiftDown();
			boolean alt = e.isAltDown();
			boolean movimentou = false;
			int recX = e.getX();
			int recY = e.getY();
			if (validoArrastar) {
				totalArrastado = 0;
				for (Objeto objeto : objetos) {
					if (objeto.isSelecionado()) {
						totalArrastado++;
						if (alt && !shift) {
							objeto.x += recX - ultX;
						} else if (!alt && shift) {
							objeto.y += recY - ultY;
						} else {
							objeto.x += recX - ultX;
							objeto.y += recY - ultY;
						}
						movimentou = true;
					}
				}
			}
			ultX = recX;
			ultY = recY;
			if (!movimentou) {
				area.x2 = recX;
				area.y2 = recY;
				area.calc();
			}
			repaint();
		}

		private void localizarSe(Objeto objeto) {
			final int raio = Objeto.DIAMETRO / 2;
			int restoX = (objeto.x + raio) % Constantes.GRADE;
			int restoY = (objeto.y + raio) % Constantes.GRADE;
			int gradeM = Constantes.GRADE / 2;
			if (restoX == 0 && restoY == 0) {
				return;
			}
			if (restoX != 0) {
				if (restoX <= gradeM) {
					objeto.x -= restoX;
				} else {
					objeto.x += (Constantes.GRADE - restoX);
				}
			}
			if (restoY != 0) {
				if (restoY <= gradeM) {
					objeto.y -= restoY;
				} else {
					objeto.y += (Constantes.GRADE - restoY);
				}
			}
			localizarInternalFormulario(objeto);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (totalArrastado == 1) {
				for (Objeto objeto : objetos) {
					if (objeto.isSelecionado()) {
						localizarSe(objeto);
					}
				}
			}
			totalArrastado = 0;
			int x = e.getX();
			int y = e.getY();
			if (area.largura > Objeto.DIAMETRO && area.altura > Objeto.DIAMETRO) {
				for (Objeto objeto : objetos) {
					if (area.contem(objeto)) {
						objeto.setSelecionado(true);
					}
				}
			}
			area.ini();
			area.calc();
			repaint();
			if (e.isPopupTrigger() && (selecionadoObjeto != null || selecionadoRelacao != null)) {
				popup.preShow(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(ObjetoSuperficieExt.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.xLocal = x;
				popup2.yLocal = y;
				popup2.preShow(getAllFrames().length > 0);
				popup2.show(ObjetoSuperficieExt.this, x, y);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			totalArrastado = 0;
			if (selecionadoObjeto != null && !selecionadoObjeto.isSelecionado()) {
				selecionadoObjeto = null;
			}
			repaint();
			if (e.getClickCount() >= Constantes.DOIS) {
				if (selecionadoObjeto != null) {
					try {
						abrirObjeto(selecionadoObjeto);
					} catch (ObjetoException ex) {
						Util.mensagem(ObjetoSuperficieExt.this, ex.getMessage());
					}
				} else if (selecionadoRelacao != null) {
					popup.configuracaoAcao.actionPerformed(null);
				}
			}
		}

		private void abrirObjeto(Objeto objeto) throws ObjetoException {
			if (Util.isEmpty(objeto.getTabela())) {
				popup.configuracaoAcao.actionPerformed(null);
			} else {
				Conexao conexao = null;
				InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(ObjetoSuperficieExt.this,
						objeto);
				if (interno != null) {
					conexao = interno.getInternalContainer().getConexao();
				}
				if (conexao == null) {
					conexao = container.getConexaoPadrao();
				}
				if (Util.isEmpty(objeto.getArquivo())) {
					ObjetoSuperficieUtil.criarExternalFormulario(ObjetoSuperficieExt.this, conexao, objeto);
				} else {
					abrirArquivo(conexao, objeto, interno);
				}
			}
		}

		private void abrirArquivo(Conexao conexao, Objeto objeto, InternalFormulario interno) throws ObjetoException {
			setComplemento(conexao, objeto);
			InternalConfig config = new InternalConfig(conexao.getNome(), objeto.getGrupo(), objeto.getTabela());
			config.setGraphics(getGraphics());
			if (interno != null) {
				config.setComplemento(interno.getComplementoChaves(true, conexao));
			}
			ObjetoFabrica.abrirNoFormulario(formulario, objeto.getArquivo().trim(), config);
		}
	};

	private void mousePressedPopup(MouseEvent e) {
		container.estadoSelecao();
		mouseAdapterSelecao.mousePressed(e);
	}

	private void mouseReleasedPopup(MouseEvent e) {
		container.estadoSelecao();
		mouseAdapterSelecao.mouseReleased(e);
	}

	private void duploClick(MouseEvent e) {
		container.estadoSelecao();
		mouseAdapterSelecao.mousePressed(e);
		mouseAdapterSelecao.mouseClicked(e);
	}

	public void configEstado(byte estado) {
		removeMouseMotionListener(mouseAdapterRotulos);
		removeMouseListener(mouseAdapterRotulos);
		removeMouseMotionListener(mouseAdapterArrasto);
		removeMouseListener(mouseAdapterArrasto);
		removeMouseMotionListener(mouseAdapterRelacao);
		removeMouseListener(mouseAdapterRelacao);
		removeMouseMotionListener(mouseAdapterSelecao);
		removeMouseListener(mouseAdapterSelecao);
		this.estado = -1;
		configEstadoImp(estado);
		if (relacoes != null) {
			for (Relacao relacao : relacoes) {
				relacao.setSelecionado(false);
			}
		}
		if (objetos != null) {
			ObjetoSuperficieUtil.limparSelecao(this);
		}
	}

	private void configEstadoImp(byte estado) {
		if (estado == ObjetoConstantes.ARRASTO) {
			addMouseMotionListener(mouseAdapterArrasto);
			addMouseListener(mouseAdapterArrasto);
			this.estado = estado;
		} else if (estado == ObjetoConstantes.ROTULOS) {
			addMouseMotionListener(mouseAdapterRotulos);
			addMouseListener(mouseAdapterRotulos);
			this.estado = estado;
		} else if (estado == ObjetoConstantes.RELACAO) {
			addMouseMotionListener(mouseAdapterRelacao);
			addMouseListener(mouseAdapterRelacao);
			this.estado = estado;
		} else if (estado == ObjetoConstantes.SELECAO) {
			addMouseMotionListener(mouseAdapterSelecao);
			addMouseListener(mouseAdapterSelecao);
			this.estado = estado;
		}
	}
}