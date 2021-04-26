package br.com.persist.plugins.objeto;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.DesktopLargura;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.Vetor;
import br.com.persist.componente.Acao;
import br.com.persist.componente.Action;
import br.com.persist.componente.Label;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.componente.Popup;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.arquivo.ArquivoProvedor;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoConstantes;
import br.com.persist.plugins.metadado.MetadadoEvento;
import br.com.persist.plugins.objeto.circular.CircularContainer.Tipo;
import br.com.persist.plugins.objeto.circular.CircularDialogo;
import br.com.persist.plugins.objeto.config.ObjetoDialogo;
import br.com.persist.plugins.objeto.config.RelacaoDialogo;
import br.com.persist.plugins.objeto.internal.ExternalFormulario;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalContainer;
import br.com.persist.plugins.objeto.internal.InternalForm;
import br.com.persist.plugins.objeto.internal.InternalFormulario;
import br.com.persist.plugins.objeto.internal.InternalTransferidor;
import br.com.persist.plugins.objeto.macro.MacroDialogo;
import br.com.persist.plugins.objeto.macro.MacroProvedor;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.plugins.persistencia.Persistencia;
import br.com.persist.plugins.persistencia.PersistenciaModelo;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class ObjetoSuperficie extends Desktop implements ObjetoListener {
	private static final long serialVersionUID = 1L;
	private final transient Vinculacao vinculacao = new Vinculacao();
	private final transient Inversao inversao = new Inversao();
	private SuperficiePopup2 popup2 = new SuperficiePopup2();
	private SuperficiePopup popup = new SuperficiePopup();
	private static final Logger LOG = Logger.getGlobal();
	private final transient Linha linha = new Linha();
	private final transient Area area = new Area();
	private transient Relacao selecionadoRelacao;
	private transient Objeto selecionadoObjeto;
	private final ObjetoContainer container;
	private transient Relacao[] relacoes;
	private final Formulario formulario;
	private transient Objeto[] objetos;
	private String arquivoVinculo;
	private byte estado;
	private int ultX;
	private int ultY;

	public ObjetoSuperficie(Formulario formulario, ObjetoContainer container) {
		super(true);
		configEstado(ObjetoConstantes.SELECAO);
		this.formulario = formulario;
		this.container = container;
		configurar();
		limpar();
	}

	private void configurar() {
		inputMap().put(getKeyStroke(KeyEvent.VK_T), "thread_processar");
		inputMap().put(getKeyStroke(KeyEvent.VK_Y), "thread_desativar");
		inputMap().put(getKeyStroke(KeyEvent.VK_N), "macro_lista");
		inputMap().put(getKeyStroke(KeyEvent.VK_Z), "zoom_menos");
		inputMap().put(getKeyStroke(KeyEvent.VK_X), "zoom_mais");
		inputMap().put(getKeyStroke(KeyEvent.VK_M), "macro");
		getActionMap().put("thread_processar", threadProcessar);
		getActionMap().put("thread_desativar", threadDesativar);
		getActionMap().put("macro_lista", macroLista);
		getActionMap().put("zoom_menos", zoomMenos);
		getActionMap().put("zoom_mais", zoomMais);
		getActionMap().put("macro", macro);
	}

	public void configurarLargura(Dimension dimension) {
		if (isAjusteLarguraForm()) {
			setSize(dimension);
			larguras.configurar(DesktopLargura.TOTAL_A_DIREITA);
		}
	}

	public void checarLargura(InternalContainer invocador) {
		if (isAjusteLarguraForm()) {
			setSize(container.getSize());
			for (JInternalFrame frame : getAllFrames()) {
				if (frame instanceof InternalFormulario) {
					InternalFormulario interno = (InternalFormulario) frame;
					if (interno.getInternalContainer() == invocador) {
						larguras.configurar(DesktopLargura.TOTAL_A_DIREITA, frame);
					}
				}
			}
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (popup != null) {
			SwingUtilities.updateComponentTreeUI(popup);
		}
		if (popup2 != null) {
			SwingUtilities.updateComponentTreeUI(popup2);
		}
	}

	private transient javax.swing.Action threadProcessar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : objetos) {
				if (objeto.isSelecionado()) {
					objeto.setProcessar(true);
					objeto.ativar();
				}
			}
			repaint();
		}
	};

	private transient javax.swing.Action threadDesativar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : objetos) {
				if (objeto.isSelecionado()) {
					objeto.desativar();
				}
			}
			repaint();
		}
	};

	private transient javax.swing.Action macroLista = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (MacroProvedor.isEmpty()) {
				return;
			}
			MacroDialogo.criar(container.getFrame());
		}
	};

	private transient javax.swing.Action macro = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			List<MacroProvedor.Instrucao> instrucoes = MacroProvedor.getInstrucoes();
			if (instrucoes.isEmpty()) {
				return;
			}
			macroObjetos(instrucoes);
			macroRelacoes(instrucoes);
			repaint();
		}

		private void macroObjetos(List<MacroProvedor.Instrucao> instrucoes) {
			for (Objeto objeto : objetos) {
				if (objeto.isSelecionado()) {
					for (MacroProvedor.Instrucao instrucao : instrucoes) {
						instrucao.executar(objeto);
					}
				}
			}
		}

		private void macroRelacoes(List<MacroProvedor.Instrucao> instrucoes) {
			for (Relacao relacao : relacoes) {
				if (relacao.isSelecionado()) {
					for (MacroProvedor.Instrucao instrucao : instrucoes) {
						instrucao.executar(relacao);
					}
				}
			}
		}
	};

	private transient javax.swing.Action zoomMenos = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : objetos) {
				objeto.zoomMenos();
			}
			repaint();
		}
	};

	private transient javax.swing.Action zoomMais = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : objetos) {
				objeto.zoomMais();
			}
			repaint();
		}
	};

	public void alinharNomes() {
		for (Objeto objeto : objetos) {
			alinharNome(objeto);
		}
	}

	public void alinharNome(Objeto objeto) {
		if (objeto != null) {
			Font font = getFont();
			if (font != null) {
				FontMetrics fm = getFontMetrics(font);
				if (fm != null) {
					objeto.alinhar(fm);
				}
			}
		}
	}

	public void limparSelecao() {
		inversao.ultimo = null;
		for (Objeto objeto : objetos) {
			objeto.setSelecionado(false);
		}
	}

	public List<Objeto> getSelecionados() {
		List<Objeto> resp = new ArrayList<>();
		for (Objeto objeto : objetos) {
			if (objeto.isSelecionado()) {
				resp.add(objeto);
			}
		}
		return resp;
	}

	public JComboBox<Objeto> criarComboObjetosSel() {
		return new JComboBox<>(new ObjetoComboModelo(getSelecionados()));
	}

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
					selecionadoObjeto.setControlado(false);
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
				limparSelecao();
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
			limparSelecao();
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
			Relacao relacao = getRelacao(origem, destino);
			if (relacao == null) {
				relacao = new Relacao(origem, false, destino, !shift);
				addRelacao(relacao);
			}
			repaint();
			if (!shift) {
				RelacaoDialogo.criar(container.getFrame(), ObjetoSuperficie.this, relacao);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				duploClick(e);
			}
		}
	};

	private transient MouseAdapter mouseAdapterSelecao = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			selecionadoRelacao = null;
			selecionadoObjeto = null;
			int x = e.getX();
			int y = e.getY();
			area.x1 = x;
			area.y1 = y;
			ultX = x;
			ultY = y;
			for (Relacao relacao : relacoes) {
				relacao.setSelecionado(false);
			}
			for (Objeto objeto : objetos) {
				if (objeto.contem(x, y)) {
					objeto.setSelecionado(true);
					selecionadoObjeto = objeto;
					if (!objeto.isControlado()) {
						objeto.setControlado(e.isShiftDown());
					}
					break;
				}
			}
			if (e.isControlDown()) {
				selecionadoObjeto = null;
			}
			if (selecionadoObjeto != null) {
				if (selecionadoObjeto.isControlado()) {
					for (Objeto objeto : objetos) {
						if (objeto.isSelecionado()) {
							objeto.setControlado(true);
						}
					}
				} else {
					for (Objeto objeto : objetos) {
						if (objeto != selecionadoObjeto) {
							objeto.setSelecionado(false);
							inversao.anular(objeto);
						}
					}
				}
			} else {
				limparSelecao();
				for (Relacao relacao : relacoes) {
					if (relacao.contem(x, y)) {
						relacao.setSelecionado(true);
						selecionadoRelacao = relacao;
						break;
					}
				}
			}
			repaint();
			if (e.isPopupTrigger() && (selecionadoObjeto != null || selecionadoRelacao != null)) {
				popup.preShow(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(ObjetoSuperficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.xLocal = x;
				popup2.yLocal = y;
				popup2.preShow(getAllFrames().length > 0);
				popup2.show(ObjetoSuperficie.this, x, y);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			boolean shift = e.isShiftDown();
			boolean alt = e.isAltDown();
			boolean movimentou = false;
			int recX = e.getX();
			int recY = e.getY();
			for (Objeto objeto : objetos) {
				if (objeto.isSelecionado()) {
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
			ultX = recX;
			ultY = recY;
			if (!movimentou) {
				area.x2 = recX;
				area.y2 = recY;
				area.calc();
			}
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if (area.largura > Objeto.DIAMETRO && area.altura > Objeto.DIAMETRO) {
				for (Objeto objeto : objetos) {
					if (area.contem(objeto)) {
						objeto.setSelecionado(true);
						objeto.setControlado(true);
					}
				}
			}
			area.ini();
			area.calc();
			repaint();
			if (e.isPopupTrigger() && (selecionadoObjeto != null || selecionadoRelacao != null)) {
				popup.preShow(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(ObjetoSuperficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.xLocal = x;
				popup2.yLocal = y;
				popup2.preShow(getAllFrames().length > 0);
				popup2.show(ObjetoSuperficie.this, x, y);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isShiftDown()) {
				inversao.inverterSelecao(selecionadoObjeto);
			}
			if (selecionadoObjeto != null && !selecionadoObjeto.isSelecionado()) {
				selecionadoObjeto = null;
			}
			repaint();
			if (e.getClickCount() >= Constantes.DOIS) {
				if (selecionadoObjeto != null) {
					abrirObjeto(selecionadoObjeto);
				} else if (selecionadoRelacao != null) {
					popup.configuracaoAcao.actionPerformed(null);
				}
			}
		}

		private void abrirObjeto(Objeto objeto) {
			if (Util.estaVazio(objeto.getTabela2())) {
				popup.configuracaoAcao.actionPerformed(null);
			} else {
				Conexao conexao = null;
				InternalFormulario interno = getInternalFormulario(objeto);
				if (interno != null) {
					conexao = interno.getInternalContainer().getConexao();
				}
				if (conexao == null) {
					conexao = container.getConexaoPadrao();
				}
				setComplemento(conexao, objeto);
				if (Util.estaVazio(objeto.getArquivo())) {
					Frame frame = container.getFrame();
					formularioDados(conexao, objeto, frame);
				} else {
					abrirArquivo(conexao, objeto, interno);
				}
			}
		}

		private void abrirArquivo(Conexao conexao, Objeto objeto, InternalFormulario interno) {
			InternalConfig config = new InternalConfig(conexao.getNome(), objeto.getGrupo(), objeto.getTabela2());
			config.setGraphics(getGraphics());
			if (interno != null) {
				config.setComplemento(interno.getComplementoChaves());
			}
			ObjetoFabrica.abrirNoFormulario(formulario, objeto.getArquivo().trim(), getGraphics(), config);
		}
	};

	private InternalFormulario getInternalFormulario(Objeto objeto) {
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

	private void formularioDados(Conexao conexao, Objeto objeto, Frame frame) {
		ExternalFormulario form = ExternalFormulario.criar2(conexao, objeto, getGraphics());
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
	}

	public static void setComplemento(Conexao conexao, Objeto objeto) {
		if (conexao != null && objeto != null && Util.estaVazio(objeto.getComplemento())) {
			objeto.setComplemento(conexao.getFinalComplemento());
		}
	}

	private class Inversao {
		Objeto ultimo;
		boolean sel;

		void inverterSelecao(Objeto objeto) {
			if (ultimo != null && ultimo == objeto) {
				objeto.setSelecionado(!sel);
			}
			ultimo = objeto;
			if (ultimo != null) {
				sel = ultimo.isSelecionado();
			}
		}

		void anular(Objeto objeto) {
			if (ultimo == objeto) {
				ultimo = null;
			}
		}
	}

	public Formulario getFormulario() {
		return formulario;
	}

	private class Area {
		int largura;
		int altura;
		int x1;
		int y1;
		int x2;
		int y2;
		int x;
		int y;

		void ini() {
			x = y = largura = altura = 0;
			x1 = y1 = x2 = y2 = 0;
		}

		void desenhar(Graphics2D g2) {
			if (largura > 2 && altura > 2) {
				g2.drawRect(x, y, largura, altura);
			}
		}

		void calc() {
			largura = Math.abs(x1 - x2);
			altura = Math.abs(y1 - y2);
			x = Math.min(x1, x2);
			y = Math.min(y1, y2);
		}

		boolean contem(Objeto objeto) {
			return objeto.visivel && (objeto.x >= this.x && objeto.x + Objeto.DIAMETRO <= this.x + largura)
					&& (objeto.y >= this.y && objeto.y + Objeto.DIAMETRO <= this.y + altura);
		}
	}

	private class Linha {
		int x1;
		int y1;
		int x2;
		int y2;

		void ini() {
			x1 = y1 = x2 = y2 = 0;
		}

		void desenhar(Graphics2D g2) {
			g2.drawLine(x1, y1, x2, y2);
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		Stroke stroke = g2.getStroke();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (estado == ObjetoConstantes.RELACAO) {
			linha.desenhar(g2);
		}
		for (Relacao relacao : relacoes) {
			relacao.desenhar(g2, stroke);
		}
		for (Objeto objeto : objetos) {
			objeto.desenhar(this, g2, stroke);
		}
		area.desenhar(g2);
	}

	public void excluirSelecionados() {
		Objeto objeto = getPrimeiroObjetoSelecionado();
		boolean confirmado = false;
		if (objeto != null) {
			if (Util.confirmaExclusao(ObjetoSuperficie.this, true)) {
				confirmado = true;
			} else {
				return;
			}
		}
		while (objeto != null) {
			excluir(objeto);
			objeto = getPrimeiroObjetoSelecionado();
		}
		Relacao relacao = getPrimeiroRelacaoSelecionado();
		if (relacao != null && !confirmado && !Util.confirmaExclusao(ObjetoSuperficie.this, true)) {
			return;
		}
		while (relacao != null) {
			excluir(relacao);
			relacao = getPrimeiroRelacaoSelecionado();
		}
		repaint();
	}

	private Objeto getPrimeiroObjetoSelecionado() {
		for (Objeto objeto : objetos) {
			if (objeto.isSelecionado()) {
				return objeto;
			}
		}
		return null;
	}

	private Relacao getPrimeiroRelacaoSelecionado() {
		for (Relacao relacao : relacoes) {
			if (relacao.isSelecionado()) {
				return relacao;
			}
		}
		return null;
	}

	public void addObjeto(Objeto obj) {
		if (obj == null || contem(obj)) {
			return;
		}
		Objeto[] bkp = objetos;
		objetos = new Objeto[bkp.length + 1];
		System.arraycopy(bkp, 0, objetos, 0, bkp.length);
		objetos[bkp.length] = obj;
		obj.setListener(this);
	}

	public void excluir(Objeto obj) {
		int indice = getIndice(obj);
		if (indice >= 0) {
			objetos[indice].setListener(null);
			objetos[indice].desativar();
			objetos[indice] = null;
			Objeto[] bkp = objetos;
			objetos = new Objeto[0];
			for (int i = 0; i < bkp.length; i++) {
				if (bkp[i] != null) {
					addObjeto(bkp[i]);
				}
			}
			Relacao relacao = getRelacao(obj);
			while (relacao != null) {
				excluir(relacao);
				relacao = getRelacao(obj);
			}
		}
	}

	public void addRelacao(Relacao obj) {
		if (obj == null || contem(obj)) {
			return;
		}
		Relacao[] bkp = relacoes;
		relacoes = new Relacao[bkp.length + 1];
		System.arraycopy(bkp, 0, relacoes, 0, bkp.length);
		relacoes[bkp.length] = obj;
	}

	public void excluir(Relacao obj) {
		int indice = getIndice(obj);
		if (indice >= 0) {
			relacoes[indice] = null;
			Relacao[] bkp = relacoes;
			relacoes = new Relacao[0];
			for (int i = 0; i < bkp.length; i++) {
				if (bkp[i] != null) {
					addRelacao(bkp[i]);
				}
			}
		}
	}

	public Relacao getRelacao(Objeto obj) {
		if (obj != null) {
			for (Relacao relacao : relacoes) {
				if (relacao.contem(obj)) {
					return relacao;
				}
			}
		}
		return null;
	}

	public Set<String> getIdOrigens() {
		Set<String> set = new HashSet<>();
		for (Relacao relacao : relacoes) {
			set.add(relacao.getOrigem().getId());
		}
		return set;
	}

	public void pontoOrigem(boolean b) {
		for (Relacao relacao : relacoes) {
			relacao.setPontoOrigem(b);
		}
		repaint();
	}

	public void pontoDestino(boolean b) {
		for (Relacao relacao : relacoes) {
			relacao.setPontoDestino(b);
		}
		repaint();
	}

	public List<Relacao> getRelacoes(Objeto obj) {
		List<Relacao> lista = new ArrayList<>();
		if (obj != null) {
			for (Relacao relacao : relacoes) {
				if (relacao.contem(obj)) {
					lista.add(relacao);
				}
			}
		}
		return lista;
	}

	public Relacao getRelacao(Objeto obj1, Objeto obj2) {
		if (obj1 != null && obj2 != null) {
			Relacao temp = new Relacao(obj1, obj2);
			for (Relacao relacao : relacoes) {
				if (relacao.equals(temp)) {
					return relacao;
				}
			}
		}
		return null;
	}

	public boolean contemId(Objeto obj) {
		for (int i = 0; i < objetos.length; i++) {
			Objeto objeto = objetos[i];
			if (objeto != obj && objeto.equalsId(obj)) {
				return true;
			}
		}
		return false;
	}

	public boolean contem(Objeto obj) {
		return getIndice(obj) >= 0;
	}

	public boolean contem(Relacao obj) {
		return getIndice(obj) >= 0;
	}

	public int getIndice(Objeto obj) {
		if (obj != null) {
			for (int i = 0; i < objetos.length; i++) {
				if (objetos[i] == obj || objetos[i].equals(obj)) {
					return i;
				}
			}
		}
		return -1;
	}

	public int getIndice(Relacao obj) {
		if (obj != null) {
			for (int i = 0; i < relacoes.length; i++) {
				if (relacoes[i] == obj || relacoes[i].equals(obj)) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean contemObjetoComTabela(String nomeTabela) {
		for (Objeto objeto : objetos) {
			if (objeto.getTabela2().equalsIgnoreCase(nomeTabela)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean processadoMetadado(Metadado metadado, Point point, boolean labelDireito, boolean checarNomear) {
		if (metadado == null) {
			return false;
		}
		Objeto novo = new Objeto(point.x, point.y);
		novo.setChaves(metadado.getChaves());
		String id = metadado.getDescricao();
		novo.setTabela(id);
		if (labelDireito) {
			novo.setDeslocamentoXId(Objeto.DIAMETRO);
			novo.setDeslocamentoYId(Objeto.DIAMETRO / 2);
		}
		if (checarNomear
				&& Util.confirmar(ObjetoSuperficie.this, ObjetoMensagens.getString("msg.nomear_arrasto"), false)) {
			Object resp = Util.getValorInputDialog(ObjetoSuperficie.this, "label.id", id, id);
			if (resp != null && !Util.estaVazio(resp.toString())) {
				id = resp.toString();
			}
		}
		novo.setId(id);
		boolean contem = contem(novo);
		while (contem) {
			novo.setId(id + "-" + Objeto.novaSequencia());
			contem = contem(novo);
		}
		addObjeto(novo);
		repaint();
		return true;
	}

	public static class CopiarColar {
		private static final List<Objeto> copiados = new ArrayList<>();

		private CopiarColar() {
		}

		public static void copiar(ObjetoSuperficie objetoSuperficie) {
			copiados.clear();
			for (Objeto objeto : objetoSuperficie.getSelecionados()) {
				copiados.add(objeto.clonar());
			}
		}

		public static void colar(ObjetoSuperficie superficie, boolean b, int x, int y) {
			superficie.limparSelecao();
			for (Objeto objeto : copiados) {
				Objeto clone = get(objeto, superficie);
				superficie.addObjeto(clone);
				clone.setSelecionado(true);
				clone.setControlado(true);
				if (b) {
					clone.setX(x);
					clone.setY(y);
				}
			}
			superficie.repaint();
		}

		public static boolean copiadosIsEmpty() {
			return copiados.isEmpty();
		}

		private static Objeto get(Objeto objeto, ObjetoSuperficie superficie) {
			Objeto o = objeto.clonar();
			o.deltaX(Objeto.DIAMETRO);
			o.deltaY(Objeto.DIAMETRO);
			o.setId(objeto.getId() + "-" + Objeto.getSequencia());
			boolean contem = superficie.contem(o);
			while (contem) {
				o.setId(objeto.getId() + "-" + Objeto.novaSequencia());
				contem = superficie.contem(o);
			}
			return o;
		}
	}

	@Override
	protected boolean contemReferencia(Objeto objeto) {
		for (Objeto obj : objetos) {
			if (obj == objeto) {
				return true;
			}
		}
		return false;
	}

	public void limpar() {
		relacoes = new Relacao[0];
		objetos = new Objeto[0];
		repaint();
	}

	private class SuperficiePopup extends Popup {
		private static final long serialVersionUID = 1L;
		private MenuItem itemDistribuiHorizontal = new MenuItem(new DistribuicaoAcao(true, "label.horizontal"));
		private MenuItem itemDistribuiVertical = new MenuItem(new DistribuicaoAcao(false, "label.vertical"));
		private MenuItem itemAlinhaHorizontal = new MenuItem(new AlinhamentoAcao(true, "label.horizontal"));
		private MenuItem itemAlinhaVertical = new MenuItem(new AlinhamentoAcao(false, "label.vertical"));
		private Action configuracaoAcao = Action.actionMenu("label.configuracoes", Icones.CONFIG);
		private Action excluirAcao = actionMenu("label.excluir_selecionado", Icones.EXCLUIR);
		private Action copiarAcao = Action.actionMenu("label.copiar", Icones.COPIA);
		private Action dadosAcao = Action.actionMenu("label.dados", null);
		private Menu menuDistribuicao = new Menu("label.distribuicao");
		private MenuItem itemPartir = new MenuItem(new PartirAcao());
		private Menu menuAlinhamento = new Menu("label.alinhamento");
		private MenuDestacar menuDestacar = new MenuDestacar();
		private MenuCircular menuCircular = new MenuCircular();
		private MenuItem itemDados = new MenuItem(dadosAcao);

		private SuperficiePopup() {
			menuDistribuicao.add(itemDistribuiHorizontal);
			menuDistribuicao.add(itemDistribuiVertical);
			menuAlinhamento.add(itemAlinhaHorizontal);
			menuAlinhamento.add(itemAlinhaVertical);
			add(menuAlinhamento);
			add(true, menuDistribuicao);
			addMenuItem(true, copiarAcao);
			add(true, menuDestacar);
			add(true, menuCircular);
			addMenuItem(true, excluirAcao);
			add(true, itemPartir);
			add(true, itemDados);
			addMenuItem(true, configuracaoAcao);
			eventos();
		}

		private class MenuCircular extends Menu {
			private static final long serialVersionUID = 1L;
			private Action exportacaoAcao = Action.actionMenu("label.exportacao", null);
			private Action importacaoAcao = Action.actionMenu("label.importacao", null);
			private Action normalAcao = Action.actionMenu("label.normal", null);

			private MenuCircular() {
				super(Constantes.LABEL_CIRCULAR);
				addMenuItem(exportacaoAcao);
				addMenuItem(importacaoAcao);
				addMenuItem(normalAcao);
				exportacaoAcao.setActionListener(e -> abrirModal(Tipo.EXPORTACAO));
				importacaoAcao.setActionListener(e -> abrirModal(Tipo.IMPORTACAO));
				normalAcao.setActionListener(e -> abrirModal(Tipo.NORMAL));
			}

			private void abrirModal(Tipo tipo) {
				if (getSelecionados().size() > Constantes.UM) {
					CircularDialogo.criar(container.getFrame(), ObjetoSuperficie.this, tipo);
				}
			}
		}

		private class MenuDestacar extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;
			Action proprioAcao = actionMenu("label.proprio_container");
			Action desktopAcao = Action.actionMenuDesktop();

			private MenuDestacar() {
				super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR, false);
				addMenuItem(desktopAcao);
				addMenuItem(true, proprioAcao);
				formularioAcao.setActionListener(
						e -> destacar(container.getConexaoPadrao(), ObjetoConstantes.TIPO_CONTAINER_FORMULARIO, null));
				ficharioAcao.setActionListener(
						e -> destacar(container.getConexaoPadrao(), ObjetoConstantes.TIPO_CONTAINER_FICHARIO, null));
				desktopAcao.setActionListener(
						e -> destacar(container.getConexaoPadrao(), ObjetoConstantes.TIPO_CONTAINER_DESKTOP, null));
				proprioAcao.setActionListener(e -> destacarProprioContainer());
				formularioAcao.text(ObjetoMensagens.getString("label.abrir_sel_em_formulario"));
				ficharioAcao.text(ObjetoMensagens.getString("label.abrir_sel_em_fichario"));
				desktopAcao.text(ObjetoMensagens.getString("label.abrir_sel_em_desktop"));
			}

			private void destacarProprioContainer() {
				List<Objeto> lista = getSelecionados();
				if (getContinua(lista)) {
					String ajustes = nomeObjetosAjusteAuto(lista);
					if (!Util.estaVazio(ajustes) && !Util.confirmar(ObjetoSuperficie.this,
							ObjetoMensagens.getString("msb.objeto_com_ajuste_auto", ajustes), false)) {
						return;
					}
					destacar(container.getConexaoPadrao(), ObjetoConstantes.TIPO_CONTAINER_PROPRIO, null);
				}
			}

			private String nomeObjetosAjusteAuto(List<Objeto> lista) {
				StringBuilder sb = new StringBuilder();
				for (Objeto objeto : lista) {
					if (objeto.isAjusteAutoForm()) {
						sb.append(objeto.getId() + Constantes.QL);
					}
				}
				return sb.toString();
			}
		}

		private void eventos() {
			dadosAcao.setActionListener(e -> {
				Object object = itemDados.getObject();
				if (object instanceof Objeto) {
					abrirObjeto((Objeto) object);
				}
			});
			excluirAcao.setActionListener(e -> excluirSelecionados());
			configuracaoAcao.setActionListener(e -> {
				Frame frame = container.getFrame();
				if (selecionadoObjeto != null) {
					ObjetoDialogo.criar(frame, ObjetoSuperficie.this, selecionadoObjeto);
				} else if (selecionadoRelacao != null) {
					RelacaoDialogo.criar(frame, ObjetoSuperficie.this, selecionadoRelacao);
				}
			});
			inputMap().put(getKeyStroke(KeyEvent.VK_C), copiarAcao.getChave());
			ObjetoSuperficie.this.getActionMap().put(copiarAcao.getChave(), copiarAcao);
			copiarAcao.setActionListener(e -> CopiarColar.copiar(ObjetoSuperficie.this));
		}

		private void abrirObjeto(Objeto objeto) {
			Conexao conexao = container.getConexaoPadrao();
			setComplemento(conexao, objeto);
			Frame frame = container.getFrame();
			formularioDados(conexao, objeto, frame);
		}

		private void preShow(boolean objetoSelecionado) {
			itemDados.setEnabled(
					objetoSelecionado && selecionadoObjeto != null && !Util.estaVazio(selecionadoObjeto.getTabela2()));
			itemDados.setObject(itemDados.isEnabled() ? selecionadoObjeto : null);
			menuDistribuicao.setEnabled(objetoSelecionado);
			menuAlinhamento.setEnabled(objetoSelecionado);
			menuDestacar.setEnabled(objetoSelecionado);
			menuCircular.setEnabled(objetoSelecionado);
			itemPartir.setEnabled(!objetoSelecionado);
			copiarAcao.setEnabled(objetoSelecionado);
		}

		private class PartirAcao extends Acao {
			private static final long serialVersionUID = 1L;

			private PartirAcao() {
				super(true, ObjetoMensagens.getString("label.partir"), false, Icones.PARTIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecionadoRelacao != null) {
					Objeto novo = selecionadoRelacao.criarObjetoMeio();
					Objeto destino = selecionadoRelacao.getDestino();
					Objeto origem = selecionadoRelacao.getOrigem();
					boolean contem = contem(novo);
					while (contem) {
						novo.setId(Constantes.VAZIO + Objeto.novaSequencia());
						contem = contem(novo);
					}
					addObjeto(novo);
					selecionadoRelacao.setSelecionado(false);
					excluir(selecionadoRelacao);
					selecionadoRelacao = null;
					addRelacao(new Relacao(origem, false, novo, false));
					addRelacao(new Relacao(novo, false, destino, false));
					ObjetoSuperficie.this.repaint();
				}
			}
		}

		private class AlinhamentoAcao extends Acao {
			private static final long serialVersionUID = 1L;
			private final boolean horizontal;

			private AlinhamentoAcao(boolean horizontal, String chave) {
				super(true, chave, true, horizontal ? Icones.HORIZONTAL : Icones.VERTICAL);
				this.horizontal = horizontal;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecionadoObjeto != null) {
					MacroProvedor.limpar();
					if (horizontal) {
						MacroProvedor.yLocal(selecionadoObjeto.y);
					} else {
						MacroProvedor.xLocal(selecionadoObjeto.x);
					}
					macro.actionPerformed(null);
				}
			}
		}

		private class DistribuicaoAcao extends Acao {
			private static final long serialVersionUID = 1L;
			private final boolean horizontal;

			private DistribuicaoAcao(boolean horizontal, String chave) {
				super(true, chave, true, horizontal ? Icones.HORIZONTAL : Icones.VERTICAL);
				this.horizontal = horizontal;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecionadoObjeto != null) {
					List<Objeto> lista = getSelecionados();
					if (lista.size() < 3) {
						return;
					}
					Collections.sort(lista, new Compara());
					if (horizontal) {
						int totalDifX = lista.get(lista.size() - 1).x - lista.get(0).x;
						int fragmentoX = totalDifX / (lista.size() - 1);
						int x = lista.get(0).x;
						for (int i = 1; i < lista.size(); i++) {
							Objeto objeto = lista.get(i);
							x += fragmentoX;
							objeto.x = x;
						}
					} else {
						int totalDifY = lista.get(lista.size() - 1).y - lista.get(0).y;
						int fragmentoY = totalDifY / (lista.size() - 1);
						int y = lista.get(0).y;
						for (int i = 1; i < lista.size(); i++) {
							Objeto objeto = lista.get(i);
							y += fragmentoY;
							objeto.y = y;
						}
					}
					ObjetoSuperficie.this.repaint();
				}
			}

			private class Compara implements Comparator<Objeto> {
				@Override
				public int compare(Objeto o1, Objeto o2) {
					return horizontal ? o1.x - o2.x : o1.y - o2.y;
				}
			}
		}
	}

	static Action actionMenu(String chave, Icon icon) {
		return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
	}

	static Action actionMenu(String chave) {
		return actionMenu(chave, null);
	}

	private class SuperficiePopup2 extends Popup {
		private static final long serialVersionUID = 1L;
		private Action atualizarFormulariosAcao = actionMenu("label.atualizar_forms", Icones.ATUALIZAR);
		private Action limparFormulariosAcao = actionMenu("label.limpar_formularios", Icones.NOVO);
		private Action criarObjetoAcao = actionMenu("label.criar_objeto", Icones.CRIAR);
		private Action propriedadesAcao = Action.actionMenu("label.propriedades", null);
		private Action colarAcao = Action.actionMenu("label.colar", Icones.COLAR);
		int xLocal;
		int yLocal;

		private SuperficiePopup2() {
			addMenuItem(criarObjetoAcao);
			addMenuItem(true, colarAcao);
			add(true, menuAjustar);
			addMenuItem(true, atualizarFormulariosAcao);
			addMenuItem(limparFormulariosAcao);
			add(true, menuLargura);
			add(true, menuAjuste);
			addMenuItem(true, propriedadesAcao);
			eventos();
		}

		private void eventos() {
			criarObjetoAcao.setActionListener(e -> criarNovoObjeto(popup2.xLocal, popup2.yLocal));
			atualizarFormulariosAcao.setActionListener(e -> atualizarFormularios());
			propriedadesAcao.setActionListener(e -> propriedades());
			limparFormulariosAcao.setActionListener(e -> limpar2());
			colarAcao.setActionListener(
					e -> CopiarColar.colar(ObjetoSuperficie.this, true, popup2.xLocal, popup2.yLocal));
		}

		private void preShow(boolean contemFrames) {
			colarAcao.setEnabled(!CopiarColar.copiadosIsEmpty());
			atualizarFormulariosAcao.setEnabled(contemFrames);
			limparFormulariosAcao.setEnabled(contemFrames);
			menuLargura.habilitar(contemFrames);
			menuAjustar.habilitar(contemFrames);
			menuAjuste.habilitar(contemFrames);
		}

		private void propriedades() {
			StringBuilder sbi = new StringBuilder();
			int invisiveis = 0;
			int visiveis = 0;
			for (Objeto objeto : objetos) {
				if (objeto.visivel) {
					visiveis++;
				} else {
					sbi.append(objeto.getId() + Constantes.QL);
					invisiveis++;
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append(ObjetoMensagens.getString("label.total_objetos") + " " + (visiveis + invisiveis) + Constantes.QL);
			sb.append(ObjetoMensagens.getString("label.objetos_visiveis", visiveis) + Constantes.QL);
			sb.append(ObjetoMensagens.getString("label.objetos_invisiveis", invisiveis) + Constantes.QL);
			if (sbi.length() > 0) {
				sb.append(sbi);
			}
			File file = container.getArquivo();
			if (file != null) {
				sb.append("------------------" + Constantes.QL);
				sb.append(ObjetoMensagens.getString("label.local_absoluto_arquivo") + " " + file.getAbsolutePath()
						+ Constantes.QL);
				sb.append(ObjetoMensagens.getString("label.local_relativo_arquivo") + " "
						+ ArquivoProvedor.criarStringPersistencia(file) + Constantes.QL);
			}
			Util.mensagem(formulario, sb.toString());
		}
	}

	public void selecionarConexao(Conexao conexao) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				interno.selecionarConexao(conexao);
			}
		}
	}

	public void criarNovoObjeto(int x, int y) {
		Objeto novo = new Objeto(x, y);
		checagemId(novo, Constantes.VAZIO, Constantes.VAZIO);
		addObjeto(novo);
		limparSelecao();
		repaint();
	}

	private void checagemId(Objeto objeto, String id, String sep) {
		boolean contem = contemId(objeto);
		while (contem) {
			objeto.setId(id + sep + Objeto.novaSequencia());
			contem = contemId(objeto);
		}
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public static KeyStroke getKeyStroke(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	public void salvar(File file, Conexao conexao) throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		salvarAtributos(conexao, util);
		util.fecharTag();
		salvarObjetos(util);
		util.ql();
		salvarRelacoes(util);
		util.ql();
		salvarForms(util);
		util.finalizarTag("fvf");
		util.close();
	}

	private void salvarAtributos(Conexao conexao, XMLUtil util) {
		util.abrirTag("fvf");
		util.atributo("ajusteAutoForm", isAjusteAutomaticoForm());
		util.atributo("ajusteLarguraForm", isAjusteLarguraForm());
		util.atributo("largura", getWidth());
		util.atributo("altura", getHeight());
		util.atributo("arquivoVinculo", getArquivoVinculo());
		if (conexao != null) {
			util.atributo("conexao", Util.escapar(conexao.getNome()));
		}
	}

	private void salvarObjetos(XMLUtil util) {
		for (Objeto objeto : objetos) {
			objeto.salvar(util);
		}
	}

	private void salvarRelacoes(XMLUtil util) {
		for (Relacao relacao : relacoes) {
			relacao.salvar(util);
		}
	}

	private void salvarForms(XMLUtil util) {
		JInternalFrame[] frames = getAllFrames();
		for (int i = frames.length - 1; i >= 0; i--) {
			InternalFormulario interno = (InternalFormulario) frames[i];
			InternalForm form = new InternalForm();
			form.copiar(interno);
			form.salvar(util);
		}
	}

	public void abrir(ObjetoColetor coletor) {
		limpar();
		for (Objeto objeto : coletor.getObjetos()) {
			addObjeto(objeto);
		}
		for (Relacao relacao : coletor.getRelacoes()) {
			addRelacao(relacao);
		}
		removeAll();
		alinharNomes();
		repaint();
		setPreferredSize(coletor.getDimension());
		SwingUtilities.updateComponentTreeUI(getParent());
		for (Objeto objeto : coletor.getObjetos()) {
			objeto.ativar();
		}
		arquivoVinculo = coletor.getArquivoVinculo();
		vinculacao.abrir(arquivoVinculo, ObjetoSuperficie.this);
		for (Objeto objeto : objetos) {
			vinculacao.processar(objeto);
		}
	}

	public void desenharDesc(boolean b) {
		for (Relacao relacao : relacoes) {
			relacao.setDesenharDescricao(b);
		}
		repaint();
	}

	public void selecaoGeral(boolean b) {
		for (Objeto objeto : objetos) {
			objeto.setSelecionado(b);
			if (b) {
				objeto.setControlado(true);
			}
		}
		repaint();
	}

	public void desenharIds(boolean b) {
		for (Objeto objeto : objetos) {
			objeto.setDesenharId(b);
		}
		repaint();
	}

	public void transparente(boolean b) {
		for (Objeto objeto : objetos) {
			objeto.setTransparente(b);
		}
		repaint();
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
			limparSelecao();
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

	public void excluido() {
		for (Objeto objeto : objetos) {
			objeto.setListener(null);
			objeto.desativar();
		}
	}

	@Override
	public void pesquisar(Conexao conexao, Pesquisa pesquisa, String argumentos) {
		super.pesquisar(conexao, pesquisa, argumentos);
		if (ObjetoPreferencia.isAbrirAuto()) {
			limparSelecao();
			processarReferencias(conexao, pesquisa, argumentos);
			if (getPrimeiroObjetoSelecionado() != null) {
				destacar(conexao != null ? conexao : container.getConexaoPadrao(),
						ObjetoPreferencia.getTipoContainerPesquisaAuto(), null);
			}
		}
	}

	private void processarReferencias(Conexao conexao, Pesquisa pesquisa, String argumentos) {
		for (Referencia referencia : pesquisa.getReferencias()) {
			if (!referencia.isProcessado()) {
				pesquisarReferencia(conexao, referencia, argumentos);
			}
		}
	}

	private void pesquisarReferencia(Conexao conexao, Referencia referencia, String argumentos) {
		Objeto objeto = null;
		for (Objeto obj : objetos) {
			if (referencia.igual(obj)) {
				objeto = obj;
				break;
			}
		}
		if (objeto != null && objeto.isAbrirAuto()) {
			pesquisarReferencia(conexao, referencia, argumentos, objeto);
		}
	}

	private void pesquisarReferencia(Conexao conexao, Referencia referencia, String argumentos, Objeto objeto) {
		objeto.setComplemento("AND " + referencia.getCampo() + " IN (" + argumentos + ")");
		objeto.setReferenciaPesquisa(referencia);
		if (ObjetoPreferencia.isAbrirAutoDestacado()) {
			Objeto clone = objeto.clonar();
			clone.setReferenciaPesquisa(referencia);
			criarExternalFormulario(clone, conexao != null ? conexao : container.getConexaoPadrao());
		} else {
			objeto.setSelecionado(true);
		}
		referencia.setProcessado(true);
	}

	private void criarExternalFormulario(Objeto objeto, Conexao conexao) {
		ExternalFormulario form = ExternalFormulario.criar2(conexao, objeto, getGraphics());
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		Formulario.posicionarJanela(formulario, form);
	}

	public void atualizarTotal(Conexao conexao, MenuItem menuItem, Label label) {
		if (conexao != null) {
			int total = preTotalRecente(label);
			if (total > 0) {
				new ThreadTotal(conexao, menuItem, label, total).start();
			}
		}
	}

	private int preTotalRecente(Label label) {
		int total = 0;
		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				objeto.setCorFonte(ObjetoPreferencia.getCorAntesTotalRecente());
				total++;
			}
		}
		label.limpar();
		repaint();
		return total;
	}

	private class ThreadTotal extends Thread {
		final MenuItem menuItem;
		final Conexao conexao;
		final Label label;
		final int total;

		private ThreadTotal(Conexao conexao, MenuItem menuItem, Label label, int total) {
			this.menuItem = menuItem;
			this.conexao = conexao;
			this.label = label;
			this.total = total;
		}

		@Override
		public void run() {
			label.setForeground(ObjetoPreferencia.getCorTotalAtual());
			label.setText("0 / " + total);
			boolean processado = false;
			menuItem.setEnabled(false);
			int atual = 0;
			for (Objeto objeto : objetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					try {
						Connection conn = ConexaoProvedor.getConnection(conexao);
						String aposFROM = PersistenciaModelo.prefixarEsquema(conexao, objeto.getPrefixoNomeTabela(),
								objeto.getTabela2());
						int i = Persistencia.getTotalRegistros(conn, aposFROM);
						objeto.setCorFonte(ObjetoPreferencia.getCorTotalAtual());
						label.setText(++atual + " / " + total);
						processado = true;
						objeto.setTag(i);
						repaint();
						sleep(ObjetoPreferencia.getIntervaloComparacao());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("TOTAL", ex, ObjetoSuperficie.this);
					}
				}
			}
			if (processado) {
				label.setText(ObjetoMensagens.getString("label.threadTotalAtual"));
			}
			menuItem.setEnabled(true);
		}
	}

	public void excluirSemTabela() {
		boolean contem = false;
		for (Objeto objeto : objetos) {
			if (Util.estaVazio(objeto.getTabela2())) {
				contem = true;
				break;
			}
		}
		if (contem && Util.confirmaExclusao(ObjetoSuperficie.this, true)) {
			for (Objeto objeto : objetos) {
				if (Util.estaVazio(objeto.getTabela2())) {
					excluir(objeto);
				}
			}
		}
		repaint();
	}

	public void compararRecent(Conexao conexao, MenuItem menuItem, Label label) {
		if (conexao == null) {
			return;
		}
		Font font = getFont();
		if (font != null) {
			FontMetrics fm = getFontMetrics(font);
			if (fm != null) {
				int total = preTotalRecente(label);
				if (total > 0) {
					new ThreadRecente(conexao, fm, menuItem, label, total).start();
				}
			}
		}
	}

	private class ThreadRecente extends Thread {
		final MenuItem menuItem;
		final Conexao conexao;
		final FontMetrics fm;
		final Label label;
		final int total;

		private ThreadRecente(Conexao conexao, FontMetrics fm, MenuItem menuItem, Label label, int total) {
			this.menuItem = menuItem;
			this.conexao = conexao;
			this.label = label;
			this.total = total;
			this.fm = fm;
		}

		@Override
		public void run() {
			label.setForeground(ObjetoPreferencia.getCorComparaRec());
			label.setText("0 / " + total);
			boolean processado = false;
			menuItem.setEnabled(false);
			int atual = 0;
			for (Objeto objeto : objetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					try {
						Connection conn = ConexaoProvedor.getConnection(conexao);
						String aposFROM = PersistenciaModelo.prefixarEsquema(conexao, objeto.getPrefixoNomeTabela(),
								objeto.getTabela2());
						int i = Persistencia.getTotalRegistros(conn, aposFROM);
						label.setText(++atual + " / " + total);
						processarRecente(objeto, i, fm);
						processado = true;
						repaint();
						sleep(ObjetoPreferencia.getIntervaloComparacao());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("RECENTE", ex, ObjetoSuperficie.this);
					}
				}
			}
			if (processado) {
				label.setText(ObjetoMensagens.getString("label.threadRecente"));
			}
			menuItem.setEnabled(true);
		}

		private void processarRecente(Objeto objeto, int recente, FontMetrics fm) {
			objeto.setCorFonte(ObjetoPreferencia.getCorComparaRec());
			long diff = recente - objeto.getTag();
			if (diff == 0) {
				return;
			}
			int largura = fm.stringWidth(objeto.getId());
			Objeto info = new Objeto(objeto.x + largura + Objeto.DIAMETRO, objeto.y, diff > 0 ? "create2" : "delete");
			info.setId(diff + " - " + recente + " - " + Objeto.novaSequencia());
			info.setDeslocamentoXId(objeto.getDeslocamentoXId());
			info.setDeslocamentoYId(objeto.getDeslocamentoYId());
			info.setCorFonte(objeto.getCorFonte());
			info.setTransparente(true);
			addObjeto(info);
		}
	}

	public void exportarMetadadoRaiz(Metadado metadado) {
		int y = 20;
		for (int i = 0; i < metadado.getTotal(); i++) {
			Metadado filho = metadado.getMetadado(i);
			processadoMetadado(filho, new Point(10, y), true, false);
			y += 30;
		}
		setPreferredSize(new Dimension(0, y));
		SwingUtilities.updateComponentTreeUI(getParent());
	}

	public void adicionarHierarquico(Objeto objeto) {
		Map<String, Object> args = new HashMap<>();
		args.put(MetadadoEvento.GET_METADADO_OBJETO, objeto.getTabela2());
		formulario.processar(args);
		Metadado metadado = (Metadado) args.get(MetadadoConstantes.METADADO);
		if (metadado == null) {
			Util.mensagem(ObjetoSuperficie.this,
					ObjetoMensagens.getString("msb.inexistente_get_metadado", objeto.getId()));
		} else {
			criarObjetoHierarquico(objeto, metadado);
		}
	}

	private void criarObjetoHierarquico(Objeto principal, Metadado tabela) {
		Controle controle = new Controle(principal);
		controle.raiz = tabela.getPai();
		controle.checkInicialPesquisa();
		processarDetalhes(tabela, controle);
		if (controle.erro) {
			return;
		}
		controle.checkFinalPesquisa();
		controle.localizarObjeto();
		if (!controle.circular) {
			destacar(null, ObjetoConstantes.TIPO_CONTAINER_PROPRIO, null);
		}
		Util.mensagemFormulario(formulario, controle.sb.toString());
	}

	public void abrirExportacaoImportacaoMetadado(Metadado tabela, boolean exportacao, boolean circular) {
		Variaveis variaveis = criarVariaveis(exportacao, circular);
		processarPrincipal(tabela, variaveis);
		variaveis.checkInicialPesquisa();
		processarDetalhes(tabela, variaveis);
		variaveis.checkFinalPesquisa();
		variaveis.localizarObjetos();
		if (!variaveis.circular) {
			destacar(null, ObjetoConstantes.TIPO_CONTAINER_PROPRIO, null);
		}
		Util.mensagemFormulario(formulario, variaveis.sb.toString());
	}

	private Variaveis criarVariaveis(boolean exportacao, boolean circular) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Variaveis variaveis = new Variaveis(exportacao, circular);
		variaveis.definirCentros(d);
		variaveis.criarVetor(d);
		return variaveis;
	}

	private void processarPrincipal(Metadado tabela, Variaveis variaveis) {
		variaveis.iniciarPrincipal(tabela);
		addObjeto(variaveis.principal);
	}

	private void processarDetalhes(Metadado tabela, Variaveis variaveis) {
		List<Metadado> campos = tabela.getListaCampoExportacaoImportacao(variaveis.exportacao);
		processarLista(variaveis, campos);
	}

	private void processarLista(Variaveis variaveis, List<Metadado> campos) {
		for (Metadado campo : campos) {
			variaveis.campoProcessado = campo;
			Objeto objeto = criarEAdicionar(variaveis);
			Relacao relacao = criarEAdicionarRelacao(variaveis, objeto);
			processarIdTabelaGrupoExportImport(variaveis, objeto);
			processarChaves(variaveis, objeto, relacao);
		}
	}

	private Objeto criarEAdicionar(Variaveis variaveis) {
		Objeto objeto = new Objeto(0, 0);
		variaveis.objetos.add(objeto);
		addObjeto(objeto);
		return objeto;
	}

	private Relacao criarEAdicionarRelacao(Variaveis variaveis, Objeto objeto) {
		Relacao relacao = new Relacao(variaveis.principal, !variaveis.exportacao, objeto, variaveis.exportacao);
		relacao.setQuebrado(!variaveis.circular);
		if (!variaveis.circular) {
			relacao.setPontoDestino(false);
			relacao.setPontoOrigem(false);
		}
		addRelacao(relacao);
		return relacao;
	}

	private void processarChaves(Variaveis variaveis, Objeto objeto, Relacao relacao) {
		Metadado tabelaRef = variaveis.campoProcessado.getTabelaReferencia();
		Metadado tabela = variaveis.raiz.getMetadado(tabelaRef.getNomeTabela());
		if (tabela != null) {
			processarChaves(variaveis, objeto, tabela, relacao);
		}
	}

	private void processarChaves(Variaveis variaveis, Objeto objeto, Metadado tabela, Relacao relacao) {
		Metadado tabelaRef = variaveis.campoProcessado.getTabelaReferencia();
		relacao.setChaveDestino(tabelaRef.getNomeCampo());
		objeto.setChaves(tabela.getChaves());
		if (variaveis.exportacao) {
			refNaPesquisaPrincipal(variaveis, objeto, relacao);
		} else {
			pesquisaIndividualDetalhe(variaveis, objeto, relacao);
		}
	}

	private void refNaPesquisaPrincipal(Variaveis variaveis, Objeto objeto, Relacao relacao) {
		Metadado tabelaRef = variaveis.campoProcessado.getTabelaReferencia();
		variaveis.ref(tabelaRef.getNomeTabela(), tabelaRef.getNomeCampo(), objeto.getGrupo(), true);
		relacao.setChaveOrigem(variaveis.principal.getChaves());
	}

	private void pesquisaIndividualDetalhe(Variaveis variaveis, Objeto objeto, Relacao relacao) {
		Metadado campoDetalhe = variaveis.campoProcessado;
		Metadado tabelaRef = campoDetalhe.getTabelaReferencia();
		variaveis.pesquisaDetalhe(tabelaRef.getNomeTabela(), tabelaRef.getNomeCampo(), objeto.getGrupo(),
				variaveis.principal.getTabela2(), campoDetalhe.getDescricao());
		relacao.setChaveOrigem(campoDetalhe.getDescricao());
	}

	private void processarIdTabelaGrupoExportImport(Variaveis variaveis, Objeto objeto) {
		Metadado tabelaRef = variaveis.campoProcessado.getTabelaReferencia();
		if (variaveis.exportacao) {
			processarIdTabelaGrupoExportacao(objeto, tabelaRef);
		} else {
			processarIdTabelaGrupoImportacao(variaveis, objeto, tabelaRef);
		}
		objeto.setTabela(tabelaRef.getNomeTabela());
	}

	private void processarIdTabelaGrupoExportacao(Objeto objeto, Metadado tabelaRef) {
		String nomeTabela = tabelaRef.getNomeTabela();
		if (contemObjetoComTabela(nomeTabela)) {
			String id = nomeTabela + Constantes.SEP2 + tabelaRef.getNomeCampo();
			objeto.setId(id);
			checagemId(objeto, id, Constantes.SEP2);
			objeto.setGrupo(tabelaRef.getNomeCampo());
		} else {
			objeto.setId(nomeTabela);
		}
	}

	private void processarIdTabelaGrupoImportacao(Variaveis variaveis, Objeto objeto, Metadado tabelaRef) {
		String nomeTabela = tabelaRef.getNomeTabela();
		if (contemObjetoComTabela(nomeTabela)) {
			String id = nomeTabela + Constantes.SEP2 + variaveis.campoProcessado.getDescricao();
			objeto.setId(id);
			checagemId(objeto, id, Constantes.SEP2);
			objeto.setGrupo(variaveis.campoProcessado.getDescricao());
		} else {
			objeto.setId(nomeTabela);
		}
	}

	private void processarCampo(Controle controle, Metadado campo) {
		controle.campoProcessado = campo;
		criarEAdicionar(controle);
		criarEAdicionarRelacao(controle);
		processarIdTabelaGrupoExportImport(controle);
		processarChaves(controle);
	}

	private void criarEAdicionar(Controle controle) {
		Objeto objeto = new Objeto(0, 0);
		controle.objeto = objeto;
		addObjeto(objeto);
	}

	private void criarEAdicionarRelacao(Controle controle) {
		Relacao relacao = new Relacao(controle.principal, !controle.exportacao, controle.objeto, controle.exportacao);
		relacao.setQuebrado(!controle.circular);
		if (!controle.circular) {
			relacao.setPontoDestino(false);
			relacao.setPontoOrigem(false);
		}
		controle.relacao = relacao;
		addRelacao(relacao);
	}

	private void processarIdTabelaGrupoExportImport(Controle controle) {
		Metadado tabelaRef = controle.campoProcessado.getTabelaReferencia();
		if (controle.exportacao) {
			processarIdTabelaGrupoExportacao(controle.objeto, tabelaRef);
		} else {
			processarIdTabelaGrupoImportacao(controle, tabelaRef);
		}
		controle.objeto.setTabela(tabelaRef.getNomeTabela());
	}

	private void processarIdTabelaGrupoImportacao(Controle controle, Metadado tabelaRef) {
		String nomeTabela = tabelaRef.getNomeTabela();
		if (contemObjetoComTabela(nomeTabela)) {
			String id = nomeTabela + Constantes.SEP2 + controle.campoProcessado.getDescricao();
			controle.objeto.setId(id);
			checagemId(controle.objeto, id, Constantes.SEP2);
			controle.objeto.setGrupo(controle.campoProcessado.getDescricao());
		} else {
			controle.objeto.setId(nomeTabela);
		}
	}

	private void processarChaves(Controle controle) {
		Metadado tabelaRef = controle.campoProcessado.getTabelaReferencia();
		Metadado tabela = controle.raiz.getMetadado(tabelaRef.getNomeTabela());
		if (tabela != null) {
			processarChaves(controle, tabela);
		}
	}

	private void processarChaves(Controle controle, Metadado tabela) {
		Metadado tabelaRef = controle.campoProcessado.getTabelaReferencia();
		controle.relacao.setChaveDestino(tabelaRef.getNomeCampo());
		controle.objeto.setChaves(tabela.getChaves());
		if (controle.exportacao) {
			refNaPesquisaPrincipal(controle);
		} else {
			pesquisaIndividualDetalhe(controle);
		}
	}

	private void refNaPesquisaPrincipal(Controle controle) {
		Metadado tabelaRef = controle.campoProcessado.getTabelaReferencia();
		controle.ref(tabelaRef.getNomeTabela(), tabelaRef.getNomeCampo(), controle.objeto.getGrupo(), true);
		controle.relacao.setChaveOrigem(controle.principal.getChaves());
	}

	private void pesquisaIndividualDetalhe(Controle controle) {
		Metadado campoDetalhe = controle.campoProcessado;
		Metadado tabelaRef = campoDetalhe.getTabelaReferencia();
		controle.pesquisaDetalhe(tabelaRef.getNomeTabela(), tabelaRef.getNomeCampo(), controle.objeto.getGrupo(),
				controle.principal.getTabela2(), campoDetalhe.getDescricao());
		controle.relacao.setChaveOrigem(campoDetalhe.getDescricao());
	}

	private void processarDetalhes(Metadado tabela, Controle controle) {
		List<Metadado> campos = tabela.getListaCampoExportacaoImportacao(controle.exportacao);
		Coletor coletor = new Coletor();
		SetLista.view(ObjetoMensagens.getString("label.adicionar_hierarquico"), nomeCampos(campos), coletor,
				ObjetoSuperficie.this, true);
		if (coletor.size() == 1) {
			String nomeCampo = coletor.get(0);
			processarCampo(controle, getCampo(campos, nomeCampo));
		} else {
			controle.erro = true;
		}
	}

	private Metadado getCampo(List<Metadado> campos, String nomeCampo) {
		for (Metadado metadado : campos) {
			if (metadado.getChaveTabelaReferencia().equals(nomeCampo)) {
				return metadado;
			}
		}
		return null;
	}

	private List<String> nomeCampos(List<Metadado> campos) {
		return campos.stream().map(Metadado::getChaveTabelaReferencia).collect(Collectors.toList());
	}

	private void abrirPesquisaBuilder(StringBuilder sb, String nome, String tabela, String campo) {
		sb.append(Constantes.QL + "\t<pesquisa");
		sb.append(" nome=" + citar(nome));
		append(sb, tabela, campo);
		sb.append(">");
	}

	private void append(StringBuilder sb, String tabela, String campo) {
		sb.append(" tabela=" + citar(tabela));
		sb.append(" campo=" + citar(campo));
	}

	private class Controle {
		private StringBuilder sb = new StringBuilder();
		Metadado campoProcessado;
		final Objeto principal;
		boolean exportacao;
		boolean circular;
		Relacao relacao;
		Objeto objeto;
		Metadado raiz;
		boolean erro;

		private Controle(Objeto principal) {
			this.principal = principal;
			exportacao = true;
			circular = false;
		}

		private void checkInicialPesquisa() {
			if (exportacao) {
				abrirPesquisa(Mensagens.getString("label.andamento"), principal.getTabela2(), principal.getChaves());
			}
		}

		private void abrirPesquisa(String nome, String tabela, String campo) {
			abrirPesquisaBuilder(sb, nome, tabela, campo);
		}

		private void ref(String tabela, String campo, String grupo, boolean invisivel) {
			sb.append(Constantes.QL + "\t\t<ref");
			append(sb, tabela, campo);
			if (!Util.estaVazio(grupo)) {
				sb.append(" grupo=" + citar(grupo));
			}
			if (invisivel) {
				sb.append(" vazio=" + citar("invisivel"));
			}
			sb.append(" />");
		}

		private void pesquisaDetalhe(String tabelaPrincipal, String campoPrincipal, String grupoPrincipal,
				String tabelaDetalhe, String campoDetalhe) {
			abrirPesquisa(tabelaPrincipal, tabelaDetalhe, campoDetalhe);
			ref(tabelaPrincipal, campoPrincipal, grupoPrincipal, false);
			fecharPesquisa();
		}

		private void fecharPesquisa() {
			sb.append(Constantes.QL + "\t</pesquisa>");
		}

		private void checkFinalPesquisa() {
			if (exportacao) {
				fecharPesquisa();
			}
		}

		private void localizarObjeto() {
			InternalFormulario interno = getInternalFormulario(principal);
			if (interno != null) {
				objeto.x = principal.x + Constantes.VINTE_CINCO;
				objeto.y = interno.getY() + Constantes.TRINTA;
				principal.setSelecionado(false);
				objeto.setDeslocamentoXId(28);
				objeto.setDeslocamentoYId(24);
				objeto.setChecarLargura(true);
				objeto.setSelecionado(true);
			}
		}
	}

	private String citar(String s) {
		return "\"" + s + "\"";
	}

	private class Variaveis {
		final List<Objeto> objetos = new ArrayList<>();
		private StringBuilder sb = new StringBuilder();
		final Objeto principal = new Objeto(0, 0);
		final boolean exportacao;
		Metadado campoProcessado;
		final boolean circular;
		Metadado raiz;
		Vetor vetor;
		int centroX;
		int centroY;
		int graus;
		int y;

		private Variaveis(boolean exportacao, boolean circular) {
			this.exportacao = exportacao;
			this.circular = circular;
		}

		private void definirCentros(Dimension d) {
			centroY = d.height / 2 - 25;
			centroX = d.width / 2;
		}

		private void criarVetor(Dimension d) {
			int comprimento = Math.min(d.width, d.height) / 2 - 50;
			vetor = new Vetor(comprimento, 0);
		}

		private void iniciarPrincipal(Metadado metadado) {
			principal.setTabela(metadado.getDescricao());
			principal.setChaves(metadado.getChaves());
			principal.setId(metadado.getDescricao());
			raiz = metadado.getPai();
		}

		private void checkInicialPesquisa() {
			if (exportacao) {
				abrirPesquisa(Mensagens.getString("label.andamento"), principal.getTabela2(), principal.getChaves());
			}
		}

		private void abrirPesquisa(String nome, String tabela, String campo) {
			abrirPesquisaBuilder(sb, nome, tabela, campo);
		}

		private void ref(String tabela, String campo, String grupo, boolean invisivel) {
			sb.append(Constantes.QL + "\t\t<ref");
			append(sb, tabela, campo);
			if (!Util.estaVazio(grupo)) {
				sb.append(" grupo=" + citar(grupo));
			}
			if (invisivel) {
				sb.append(" vazio=" + citar("invisivel"));
			}
			sb.append(" />");
		}

		private void pesquisaDetalhe(String tabelaPrincipal, String campoPrincipal, String grupoPrincipal,
				String tabelaDetalhe, String campoDetalhe) {
			abrirPesquisa(tabelaPrincipal, tabelaDetalhe, campoDetalhe);
			ref(tabelaPrincipal, campoPrincipal, grupoPrincipal, false);
			fecharPesquisa();
		}

		private void fecharPesquisa() {
			sb.append(Constantes.QL + "\t</pesquisa>");
		}

		private void checkFinalPesquisa() {
			if (exportacao) {
				fecharPesquisa();
			}
		}

		private void localizarObjetos() {
			if (circular) {
				principal.x = centroX;
				principal.y = centroY;
				if (!objetos.isEmpty()) {
					localizacaoCircular();
				}
			} else {
				localizacaoHierarquica();
			}
		}

		private void localizacaoCircular() {
			graus = 360 / objetos.size();
			for (Objeto objeto : objetos) {
				objeto.x = centroX + (int) vetor.getX();
				objeto.y = centroY + (int) vetor.getY();
				vetor.rotacionar(graus);
			}
		}

		private void localizacaoHierarquica() {
			configuracaoPrincipal();
			localizarHierarquico();
			configuracaoPrincipalFinal();
			atualizarSuperficie();
			principal.setSelecionado(true);
			for (Objeto objeto : objetos) {
				objeto.setSelecionado(true);
			}
		}

		private void configuracaoPrincipal() {
			principal.x = Constantes.VINTE;
			principal.y = Constantes.VINTE;
			deslocamentos(principal);
			y = principal.y;
			if (exportacao) {
				incY();
			}
		}

		private void deslocamentos(Objeto objeto) {
			objeto.setDeslocamentoXId(28);
			objeto.setDeslocamentoYId(24);
		}

		private void incY() {
			y += Constantes.CEM;
		}

		private void localizarHierarquico() {
			for (Objeto obj : objetos) {
				localizacaoDetalhe(obj);
				if (exportacao) {
					obj.x += Constantes.VINTE_CINCO;
				}
			}
		}

		private void localizacaoDetalhe(Objeto objeto) {
			objeto.x = Constantes.VINTE;
			deslocamentos(objeto);
			objeto.y = y;
			incY();
		}

		private void configuracaoPrincipalFinal() {
			if (!exportacao) {
				if (!objetos.isEmpty()) {
					principal.x += Constantes.VINTE_CINCO;
				}
				principal.y = y;
				incY();
			}
		}

		private void atualizarSuperficie() {
			if (!circular) {
				setPreferredSize(new Dimension(0, y));
				SwingUtilities.updateComponentTreeUI(getParent());
			}
		}
	}

	public void prefixoNomeTabela(String prefixoNomeTabela) {
		for (Objeto objeto : objetos) {
			objeto.setPrefixoNomeTabela(prefixoNomeTabela);
		}
	}

	private boolean getContinua(List<Objeto> lista) {
		for (Objeto objeto : lista) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				return true;
			}
		}
		return false;
	}

	private void destacar(Conexao conexao, int tipoContainer, InternalConfig config) {
		List<Objeto> lista = getSelecionados();
		if (getContinua(lista)) {
			List<Objeto> selecionados = montarSelecionados(lista,
					tipoContainer == ObjetoConstantes.TIPO_CONTAINER_PROPRIO);
			destacar(conexao, tipoContainer, config, selecionados);
		}
	}

	private List<Objeto> montarSelecionados(List<Objeto> lista, boolean proprioContainer) {
		List<Objeto> selecionados = new ArrayList<>();
		for (Objeto objeto : lista) {
			montarSelecionado(proprioContainer, selecionados, objeto);
		}
		return selecionados;
	}

	private void montarSelecionado(boolean proprioContainer, List<Objeto> selecionados, Objeto objeto) {
		if (objeto.isClonarAoDestacar()) {
			if (proprioContainer) {
				montarSelecionadoProprio(selecionados, objeto);
			} else {
				selecionados.add(objeto.clonar());
				objeto.setSelecionado(false);
			}
		} else {
			selecionados.add(objeto);
		}
	}

	private void montarSelecionadoProprio(List<Objeto> selecionados, Objeto objeto) {
		InternalFormulario interno = getInternalFormulario(objeto);
		if (interno == null) {
			selecionados.add(objeto);
		} else {
			selecionados.add(objeto.clonar());
			objeto.setSelecionado(false);
		}
	}

	private void destacar(Conexao conexao, int tipoContainer, InternalConfig config, List<Objeto> selecionados) {
		if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_FORMULARIO) {
			destacarDesktopFormulario(selecionados, conexao, config);
		} else if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_DESKTOP) {
			destacarDeskopPagina(selecionados, conexao, config);
		} else if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_FICHARIO) {
			destacarObjetoPagina(selecionados, conexao);
		} else if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_PROPRIO) {
			destacarPropriaSuperficie(selecionados, conexao, config);
		}
	}

	private void destacarDesktopFormulario(List<Objeto> objetos, Conexao conexao, InternalConfig config) {
		DesktopFormulario form = DesktopFormulario.criar(formulario);
		int x = 10;
		int y = 10;
		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				Object[] array = InternalTransferidor.criarArray(conexao, objeto);
				form.getDesktop().montarEAdicionarInternalFormulario(array, new Point(x, y), null, false, config);
				x += 25;
				y += 25;
			}
		}
		Formulario.posicionarJanela(formulario, form);
	}

	private void destacarDeskopPagina(List<Objeto> objetos, Conexao conexao, InternalConfig config) {
		Desktop desktop = new Desktop(false);
		int x = 10;
		int y = 10;
		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				Object[] array = InternalTransferidor.criarArray(conexao, objeto);
				desktop.montarEAdicionarInternalFormulario(array, new Point(x, y), null, false, config);
				x += 25;
				y += 25;
			}
		}
		SwingUtilities.invokeLater(() -> desktop.getDistribuicao().distribuir(-Constantes.VINTE));
		formulario.adicionarPagina(desktop);
	}

	private void destacarObjetoPagina(List<Objeto> listaObjetos, Conexao conexao) {
		for (Objeto objeto : listaObjetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				setComplemento(conexao, objeto);
				formulario.adicionarPagina(new InternalContainer(null, conexao, objeto, getGraphics(), false));
			}
		}
	}

	private void destacarPropriaSuperficie(List<Objeto> objetos, Conexao conexao, InternalConfig config) {
		Variavel variavelDeltaX = VariavelProvedor.getVariavel(ObjetoConstantes.DELTA_X_AJUSTE_FORM_OBJETO);
		Variavel variavelDeltaY = VariavelProvedor.getVariavel(ObjetoConstantes.DELTA_Y_AJUSTE_FORM_OBJETO);
		boolean salvar = false;
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
		checarAtualizarVariavelProvedorSuperficie(salvar);
		int x = variavelDeltaX.getInteiro(Constantes.TRINTA);
		int y = variavelDeltaY.getInteiro(Constantes.TRINTA);
		processarInternalFormulario(objetos, conexao, config, x, y);
		repaint();
	}

	private void processarInternalFormulario(List<Objeto> objetos, Conexao conexao, InternalConfig config, int x,
			int y) {
		Variavel variavelLargura = VariavelProvedor.getVariavel(ObjetoConstantes.DESTACAR_PROPRIO_LARGURA_INTERNAL);
		Variavel variavelAltura = VariavelProvedor.getVariavel(ObjetoConstantes.DESTACAR_PROPRIO_ALTURA_INTERNAL);
		boolean salvar = false;
		if (variavelLargura == null) {
			variavelLargura = new Variavel(ObjetoConstantes.DESTACAR_PROPRIO_LARGURA_INTERNAL,
					Constantes.VAZIO + Constantes.QUATROCENTOS);
			VariavelProvedor.adicionar(variavelLargura);
			salvar = true;
		}
		if (variavelAltura == null) {
			variavelAltura = new Variavel(ObjetoConstantes.DESTACAR_PROPRIO_ALTURA_INTERNAL,
					Constantes.VAZIO + Constantes.DUZENTOS);
			VariavelProvedor.adicionar(variavelAltura);
			salvar = true;
		}
		checarAtualizarVariavelProvedorSuperficie(salvar);
		int largura = variavelLargura.getInteiro(Constantes.QUATROCENTOS);
		int altura = variavelAltura.getInteiro(Constantes.DUZENTOS);
		Dimension dimension = new Dimension(largura, altura);
		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				Object[] array = InternalTransferidor.criarArray(conexao, objeto, dimension);
				montarEAdicionarInternalFormulario(array, new Point(objeto.getX() + x, objeto.getY() + y), null, false,
						config);
			}
		}
	}

	private void checarAtualizarVariavelProvedorSuperficie(boolean salvar) {
		if (salvar) {
			try {
				VariavelProvedor.salvar();
				VariavelProvedor.inicializar();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}

	public String getArquivoVinculo() {
		if (Util.estaVazio(arquivoVinculo)) {
			arquivoVinculo = Constantes.VAZIO;
		}
		return arquivoVinculo;
	}

	public void setArquivoVinculo(String arquivoVinculo) {
		this.arquivoVinculo = arquivoVinculo;
	}

	public void selecionarCampo(Objeto objeto, Coletor coletor, Component c) {
		Conexao conexao = container.getConexaoPadrao();
		if (conexao != null && objeto != null && !Util.estaVazio(objeto.getTabela2())) {
			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				MemoriaModelo modelo = Persistencia.criarModeloMetaDados(conn, conexao, objeto.getTabela2());
				SetLista.view(objeto.getId(), modelo.getLista(2), coletor, c, true);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("META-DADOS", ex, ObjetoSuperficie.this);
			}
		}
	}
}