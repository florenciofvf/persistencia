package br.com.persist.desktop;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.Metadado;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.busca_auto.TabelaBuscaAuto;
import br.com.persist.comp.Label;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.container.CircularContainer.Tipo;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.dialogo.CircularDialogo;
import br.com.persist.dialogo.ConsultaDialogo;
import br.com.persist.dialogo.MacroDialogo;
import br.com.persist.dialogo.ObjetoConfigDialogo;
import br.com.persist.dialogo.RelacaoDialogo;
import br.com.persist.dialogo.UpdateDialogo;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.ObjetoContainerFormulario;
import br.com.persist.formulario.ObjetoContainerFormularioInterno;
import br.com.persist.formulario.UpdateFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Macro.Instrucao;
import br.com.persist.util.Mensagens;
import br.com.persist.util.MenuPadrao1;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
import br.com.persist.util.Vetor;
import br.com.persist.xml.XMLUtil;

public class Superficie extends Desktop {
	private static final long serialVersionUID = 1L;
	private final SuperficiePopup2 popup2 = new SuperficiePopup2();
	private final SuperficiePopup popup = new SuperficiePopup();
	private final transient Inversao inversao = new Inversao();
	private static final Logger LOG = Logger.getGlobal();
	private final transient Linha linha = new Linha();
	private final transient Area area = new Area();
	private transient Relacao selecionadoRelacao;
	private transient Objeto selecionadoObjeto;
	private transient Relacao[] relacoes;
	private transient Objeto[] objetos;
	private final Container container;
	private byte estado;
	private int ultX;
	private int ultY;

	public Superficie(Formulario formulario, Container container) {
		super(formulario, true);
		configEstado(Constantes.SELECAO);
		this.container = container;
		config();
		limpar();
	}

	private void config() {
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

	transient javax.swing.Action threadProcessar = new AbstractAction() {
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

	transient javax.swing.Action threadDesativar = new AbstractAction() {
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

	transient javax.swing.Action macroLista = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Formulario.macro.isEmpty()) {
				return;
			}

			Frame frame = formulario;

			if (container.getContainerFormulario() != null) {
				frame = container.getContainerFormulario();
			}

			MacroDialogo form = new MacroDialogo(frame);
			form.setLocationRelativeTo(frame);
			form.setVisible(true);
		}
	};

	transient javax.swing.Action excluirAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			container.excluir();
		}
	};

	transient javax.swing.Action macro = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			List<Instrucao> instrucoes = Formulario.macro.getInstrucoes();

			if (instrucoes.isEmpty()) {
				return;
			}

			for (Objeto objeto : objetos) {
				if (objeto.isSelecionado()) {
					for (Instrucao instrucao : instrucoes) {
						instrucao.executar(objeto);
					}
				}
			}

			for (Relacao relacao : relacoes) {
				if (relacao.isSelecionado()) {
					for (Instrucao instrucao : instrucoes) {
						instrucao.executar(relacao);
					}
				}
			}

			repaint();
		}
	};

	transient javax.swing.Action zoomMenos = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : objetos) {
				objeto.zoomMenos();
			}

			repaint();
		}
	};

	transient javax.swing.Action zoomMais = new AbstractAction() {
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
		Font font = getFont();

		if (font == null) {
			return;
		}

		FontMetrics fm = getFontMetrics(font);

		if (fm == null) {
			return;
		}

		for (Objeto objeto : objetos) {
			objeto.alinhar(fm);
		}
	}

	public void alinharNome(Objeto objeto) {
		if (objeto == null) {
			return;
		}

		Font font = getFont();
		if (font == null) {
			return;
		}

		FontMetrics fm = getFontMetrics(font);
		if (fm == null) {
			return;
		}

		objeto.alinhar(fm);
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
					selecionadoObjeto.deslocamentoXId += recX - ultX;
				} else if (!alt && shift) {
					selecionadoObjeto.deslocamentoYId += recY - ultY;
				} else {
					selecionadoObjeto.deslocamentoXId += recX - ultX;
					selecionadoObjeto.deslocamentoYId += recY - ultY;
				}
			} else if (selecionadoRelacao != null) {
				if (alt && !shift) {
					selecionadoRelacao.deslocamentoXDesc += recX - ultX;
				} else if (!alt && shift) {
					selecionadoRelacao.deslocamentoYDesc += recY - ultY;
				} else {
					selecionadoRelacao.deslocamentoXDesc += recX - ultX;
					selecionadoRelacao.deslocamentoYDesc += recY - ultY;
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
					selecionadoObjeto.controlado = false;
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
				Frame frame = formulario;

				if (container.getContainerFormulario() != null) {
					frame = container.getContainerFormulario();
				}

				RelacaoDialogo form = new RelacaoDialogo(frame, Superficie.this, relacao);
				form.setLocationRelativeTo(frame);
				form.setVisible(true);
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

					if (!objeto.controlado) {
						objeto.controlado = e.isShiftDown();
					}

					break;
				}
			}

			if (selecionadoObjeto != null) {
				if (selecionadoObjeto.controlado) {
					for (Objeto objeto : objetos) {
						if (objeto.isSelecionado()) {
							objeto.controlado = true;
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
				popup.show(Superficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.xLocal = x;
				popup2.yLocal = y;
				popup2.preShow(getAllFrames().length > 0);
				popup2.show(Superficie.this, x, y);
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
						objeto.controlado = true;
					}
				}
			}

			area.ini();
			area.calc();

			repaint();

			if (e.isPopupTrigger() && (selecionadoObjeto != null || selecionadoRelacao != null)) {
				popup.preShow(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(Superficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.xLocal = x;
				popup2.yLocal = y;
				popup2.preShow(getAllFrames().length > 0);
				popup2.show(Superficie.this, x, y);
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
				Frame frame = formulario;

				if (container.getContainerFormulario() != null) {
					frame = container.getContainerFormulario();
				}

				if (selecionadoObjeto != null) {
					if (!Util.estaVazio(selecionadoObjeto.getTabela2())) {
						Conexao conexao = container.getConexaoPadrao();
						setComplemento(conexao, selecionadoObjeto);
						ObjetoContainerFormulario form = new ObjetoContainerFormulario(formulario, conexao,
								selecionadoObjeto, getGraphics());
						form.setLocationRelativeTo(frame);
						form.setVisible(true);
					} else {
						popup.configuracaoAcao.actionPerformed(null);
					}
				} else if (selecionadoRelacao != null) {
					popup.configuracaoAcao.actionPerformed(null);
				}
			}
		}
	};

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
			return (objeto.x >= this.x && objeto.x + Objeto.DIAMETRO <= this.x + largura)
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
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (estado == Constantes.RELACAO) {
			linha.desenhar(g2);
		}

		for (Relacao relacao : relacoes) {
			relacao.desenhar(g2);
		}

		for (Objeto objeto : objetos) {
			objeto.desenhar(this, g2);
		}

		area.desenhar(g2);
	}

	public void excluirSelecionados() {
		Objeto objeto = getPrimeiroObjetoSelecionado();

		if (objeto != null && !Util.confirmaExclusao(Superficie.this)) {
			return;
		}

		while (objeto != null) {
			excluir(objeto);
			objeto = getPrimeiroObjetoSelecionado();
		}

		Relacao relacao = getPrimeiroRelacaoSelecionado();

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
		obj.setSuperficie(this);
	}

	public void excluir(Objeto obj) {
		int indice = getIndice(obj);

		if (indice >= 0) {
			objetos[indice].setSuperficie(null);
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
		if (obj == null) {
			return null;
		}

		for (Relacao relacao : relacoes) {
			if (relacao.contem(obj)) {
				return relacao;
			}
		}

		return null;
	}

	public Relacao getRelacao(Objeto obj1, Objeto obj2) {
		if (obj1 == null || obj2 == null) {
			return null;
		}

		Relacao temp = new Relacao(obj1, obj2);

		for (Relacao relacao : relacoes) {
			if (relacao.equals(temp)) {
				return relacao;
			}
		}

		return null;
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

	@Override
	protected boolean processadoMetadado(Metadado metadado, Point point) {
		if (metadado == null) {
			return false;
		}

		Objeto novo = new Objeto(point.x, point.y);
		novo.setChaves(metadado.getChaves());
		String id = metadado.getDescricao();
		novo.setTabela(id);

		if (Preferencias.isNomearArrasto()) {
			Object resp = Util.getValorInputDialog(Superficie.this, "label.id", id, id);

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
		private Action configuracaoAcao = Action.actionMenu("label.configuracoes", Icones.CONFIG);
		private Action excluirAcao = Action.actionMenu("label.excluir", Icones.EXCLUIR);
		private Action copiarAcao = Action.actionMenu("label.copiar", Icones.COPIA);
		private Action dadosAcao = Action.actionMenu("label.dados", null);

		MenuItem itemDistribuiHorizontal = new MenuItem(new DistribuicaoAcao(true, "label.horizontal"));
		MenuItem itemDistribuiVertical = new MenuItem(new DistribuicaoAcao(false, "label.vertical"));
		MenuItem itemAlinhaHorizontal = new MenuItem(new AlinhamentoAcao(true, "label.horizontal"));
		MenuItem itemAlinhaVertical = new MenuItem(new AlinhamentoAcao(false, "label.vertical"));
		Menu menuDistribuicao = new Menu("label.distribuicao");
		MenuItem itemPartir = new MenuItem(new PartirAcao());
		Menu menuAlinhamento = new Menu("label.alinhamento");
		MenuItem itemCopiar = new MenuItem(copiarAcao);
		MenuDestacar menuDestacar = new MenuDestacar();
		MenuCircular menuCircular = new MenuCircular();
		MenuItem itemDados = new MenuItem(dadosAcao);

		SuperficiePopup() {
			menuDistribuicao.add(itemDistribuiHorizontal);
			menuDistribuicao.add(itemDistribuiVertical);
			menuAlinhamento.add(itemAlinhaHorizontal);
			menuAlinhamento.add(itemAlinhaVertical);

			add(menuAlinhamento);
			add(true, menuDistribuicao);
			add(true, itemCopiar);
			add(true, menuDestacar);
			add(true, menuCircular);
			add(true, new MenuConsulta());
			add(true, new MenuUpdate());
			add(true, new MenuItem(excluirAcao));
			add(true, itemPartir);
			add(true, itemDados);
			add(true, new MenuItem(configuracaoAcao));

			eventos();
		}

		class MenuCircular extends Menu {
			private static final long serialVersionUID = 1L;
			private Action exportacaoAcao = Action.actionMenu("label.exportacao", null);
			private Action importacaoAcao = Action.actionMenu("label.importacao", null);
			private Action normalAcao = Action.actionMenu("label.normal", null);

			MenuCircular() {
				super(Constantes.LABEL_CIRCULAR);

				addMenuItem(exportacaoAcao);
				addMenuItem(importacaoAcao);
				addMenuItem(normalAcao);

				exportacaoAcao.setActionListener(e -> abrirModal(Tipo.EXPORTACAO));
				importacaoAcao.setActionListener(e -> abrirModal(Tipo.IMPORTACAO));
				normalAcao.setActionListener(e -> abrirModal(Tipo.NORMAL));
			}

			private void abrirModal(Tipo tipo) {
				Frame frame = formulario;

				if (container.getContainerFormulario() != null) {
					frame = container.getContainerFormulario();
				}

				if (selecionadoObjeto != null) {
					CircularDialogo form = new CircularDialogo(frame, Superficie.this, tipo, selecionadoObjeto);
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				}
			}
		}

		class MenuConsulta extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuConsulta() {
				super(Constantes.LABEL_CONSULTA, Icones.TABELA);

				formularioAcao.setActionListener(e -> {
					Frame frame = formulario;

					if (container.getContainerFormulario() != null) {
						frame = container.getContainerFormulario();
					}

					ConsultaFormulario form = new ConsultaFormulario(formulario, formulario,
							container.getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					Frame frame = formulario;

					if (container.getContainerFormulario() != null) {
						frame = container.getContainerFormulario();
					}

					ConsultaDialogo form = new ConsultaDialogo(frame, formulario, formulario,
							container.getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(
						e -> formulario.getFichario().getConsulta().nova(formulario, container.getConexaoPadrao()));
			}
		}

		class MenuUpdate extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;

			MenuUpdate() {
				super(Constantes.LABEL_ATUALIZAR, Icones.UPDATE);

				formularioAcao.setActionListener(e -> {
					Frame frame = formulario;

					if (container.getContainerFormulario() != null) {
						frame = container.getContainerFormulario();
					}

					UpdateFormulario form = new UpdateFormulario(formulario, formulario, container.getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				dialogoAcao.setActionListener(e -> {
					Frame frame = formulario;

					if (container.getContainerFormulario() != null) {
						frame = container.getContainerFormulario();
					}

					UpdateDialogo form = new UpdateDialogo(frame, formulario, formulario, container.getConexaoPadrao());
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				});

				ficharioAcao.setActionListener(
						e -> formulario.getFichario().getUpdate().novo(formulario, container.getConexaoPadrao()));
			}
		}

		class MenuDestacar extends MenuPadrao1 {
			private static final long serialVersionUID = 1L;
			Action proprioAcao = Action.actionMenu("label.proprio", null);
			Action desktopAcao = Action.actionMenuDesktop();

			MenuDestacar() {
				super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR, false);
				addMenuItem(desktopAcao);
				addMenuItem(proprioAcao);

				formularioAcao.setActionListener(e -> formulario.destacar(container.getConexaoPadrao(), Superficie.this,
						Constantes.TIPO_CONTAINER_FORMULARIO));
				ficharioAcao.setActionListener(e -> formulario.destacar(container.getConexaoPadrao(), Superficie.this,
						Constantes.TIPO_CONTAINER_FICHARIO));
				desktopAcao.setActionListener(e -> formulario.destacar(container.getConexaoPadrao(), Superficie.this,
						Constantes.TIPO_CONTAINER_DESKTOP));
				proprioAcao.setActionListener(e -> formulario.destacar(container.getConexaoPadrao(), Superficie.this,
						Constantes.TIPO_CONTAINER_PROPRIO));
			}
		}

		private void eventos() {
			dadosAcao.setActionListener(e -> {
				MouseEvent evt = new MouseEvent(Superficie.this, 0, 0, 0, 0, 0, 2, false);
				mouseAdapterSelecao.mouseClicked(evt);
			});

			excluirAcao.setActionListener(e -> excluirSelecionados());

			configuracaoAcao.setActionListener(e -> {
				Frame frame = formulario;

				if (container.getContainerFormulario() != null) {
					frame = container.getContainerFormulario();
				}

				if (selecionadoObjeto != null) {
					ObjetoConfigDialogo form = new ObjetoConfigDialogo(frame, Superficie.this, selecionadoObjeto);
					form.setLocationRelativeTo(frame);
					form.setVisible(true);

				} else if (selecionadoRelacao != null) {
					RelacaoDialogo form = new RelacaoDialogo(frame, Superficie.this, selecionadoRelacao);
					form.setLocationRelativeTo(frame);
					form.setVisible(true);
				}
			});

			inputMap().put(getKeyStroke(KeyEvent.VK_C), copiarAcao.getChave());
			Superficie.this.getActionMap().put(copiarAcao.getChave(), copiarAcao);
			copiarAcao.setActionListener(e -> Formulario.CopiarColar.copiar(Superficie.this));
		}

		void preShow(boolean objetoSelecionado) {
			itemDados.setEnabled(
					objetoSelecionado && selecionadoObjeto != null && !Util.estaVazio(selecionadoObjeto.getTabela2()));
			menuDistribuicao.setEnabled(objetoSelecionado);
			menuAlinhamento.setEnabled(objetoSelecionado);
			menuDestacar.setEnabled(objetoSelecionado);
			menuCircular.setEnabled(objetoSelecionado);
			itemPartir.setEnabled(!objetoSelecionado);
			itemCopiar.setEnabled(objetoSelecionado);
		}

		class PartirAcao extends Acao {
			private static final long serialVersionUID = 1L;

			PartirAcao() {
				super(true, "label.partir", Icones.PARTIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecionadoRelacao != null) {
					Objeto novo = selecionadoRelacao.criarObjetoMeio();
					Objeto destino = selecionadoRelacao.getDestino();
					Objeto origem = selecionadoRelacao.getOrigem();

					boolean contem = contem(novo);

					while (contem) {
						novo.setId("" + Objeto.novaSequencia());
						contem = contem(novo);
					}

					addObjeto(novo);

					selecionadoRelacao.setSelecionado(false);
					excluir(selecionadoRelacao);
					selecionadoRelacao = null;

					addRelacao(new Relacao(origem, false, novo, false));
					addRelacao(new Relacao(novo, false, destino, false));

					Superficie.this.repaint();
				}
			}
		}

		class AlinhamentoAcao extends Acao {
			private static final long serialVersionUID = 1L;
			private final boolean horizontal;

			AlinhamentoAcao(boolean horizontal, String chave) {
				super(true, chave, horizontal ? Icones.HORIZONTAL : Icones.VERTICAL);
				this.horizontal = horizontal;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selecionadoObjeto != null) {
					Formulario.macro.limpar();

					if (horizontal) {
						Formulario.macro.yLocal(selecionadoObjeto.y);
					} else {
						Formulario.macro.xLocal(selecionadoObjeto.x);
					}

					macro.actionPerformed(null);
				}
			}
		}

		class DistribuicaoAcao extends Acao {
			private static final long serialVersionUID = 1L;
			private final boolean horizontal;

			DistribuicaoAcao(boolean horizontal, String chave) {
				super(true, chave, horizontal ? Icones.HORIZONTAL : Icones.VERTICAL);
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

					Superficie.this.repaint();
				}
			}

			class Compara implements Comparator<Objeto> {
				@Override
				public int compare(Objeto o1, Objeto o2) {
					return horizontal ? o1.x - o2.x : o1.y - o2.y;
				}
			}
		}
	}

	private class SuperficiePopup2 extends Popup {
		private static final long serialVersionUID = 1L;
		private Action alinharEsquerdoAcao = Action.actionMenu("label.alinhar_esquerdo", Icones.ALINHA_ESQUERDO);
		private Action alinharDireitoAcao = Action.actionMenu("label.alinhar_direito", Icones.ALINHA_DIREITO);
		private Action larTotalEsqAcao = Action.actionMenu("label.largura_total_esq", Icones.ALINHA_ESQUERDO);
		private Action larTotalDirAcao = Action.actionMenu("label.largura_total_dir", Icones.ALINHA_DIREITO);
		private Action atualizarFormAcao = Action.actionMenu("label.atualizar_forms", Icones.ATUALIZAR);
		private Action centralizarAcao = Action.actionMenu("label.centralizar", Icones.CENTRALIZAR);
		private Action mesmaLarguraAcao = Action.actionMenu("label.mesma_largura", Icones.LARGURA);
		private Action dimensaoAcao4 = Action.actionMenu("label.ajuste_formulario", Icones.RECT);
		private Action larTotalAcao = Action.actionMenu("label.largura_total", Icones.LARGURA);
		private Action distribuirAcao = Action.actionMenu("label.distribuir", Icones.LARGURA);
		private Action dimensaoAcao2 = Action.actionMenu("label.ajuste_objeto", Icones.RECT);
		private Action criarObjAcao = Action.actionMenu("label.criar_objeto", Icones.CRIAR);
		private Action dimensaoAcao3 = Action.actionMenu("label.ajuste_form", Icones.RECT);
		private Action dimensaoAcao = Action.actionMenu("label.dimensao", Icones.RECT);
		private Action ajustarAcao = Action.actionMenu("label.ajustar", Icones.RECT);
		private Action colarAcao = Action.actionMenu("label.colar", Icones.COLAR);

		MenuItem itemAlinharEsquerdo = new MenuItem(alinharEsquerdoAcao);
		MenuItem itemAlinharDireito = new MenuItem(alinharDireitoAcao);
		MenuItem itemAtualizarForms = new MenuItem(atualizarFormAcao);
		MenuItem itemMesmaLargura = new MenuItem(mesmaLarguraAcao);
		MenuItem itemCentralizar = new MenuItem(centralizarAcao);
		MenuItem itemCriarObjeto = new MenuItem(criarObjAcao);
		MenuItem itemDimensoes2 = new MenuItem(dimensaoAcao2);
		MenuItem itemDimensoes3 = new MenuItem(dimensaoAcao3);
		MenuItem itemDimensoes4 = new MenuItem(dimensaoAcao4);
		MenuItem itemDimensoes = new MenuItem(dimensaoAcao);
		MenuItem itemAjustes = new MenuItem(ajustarAcao);
		MenuItem itemColar = new MenuItem(colarAcao);
		int xLocal;
		int yLocal;

		SuperficiePopup2() {
			add(itemCriarObjeto);
			add(true, itemColar);
			add(true, itemAtualizarForms);
			add(true, itemMesmaLargura);
			add(itemAlinharDireito);
			add(itemAlinharEsquerdo);
			addMenuItem(true, larTotalAcao);
			addMenuItem(larTotalDirAcao);
			addMenuItem(larTotalEsqAcao);
			addMenuItem(true, distribuirAcao);
			add(true, itemCentralizar);
			add(true, itemDimensoes4);
			add(itemDimensoes3);
			add(itemDimensoes2);
			add(itemDimensoes);
			add(true, itemAjustes);

			eventos();
		}

		private void eventos() {
			criarObjAcao.setActionListener(e -> criarNovoObjeto(popup2.xLocal, popup2.yLocal));
			dimensaoAcao4.setActionListener(e -> ajuste.ajusteObjetoFormulario(false, false));
			dimensaoAcao2.setActionListener(e -> ajuste.ajusteObjetoFormulario(true, false));
			alinharEsquerdoAcao.setActionListener(e -> alinhamento.esquerdo());
			alinharDireitoAcao.setActionListener(e -> alinhamento.direito());
			dimensaoAcao3.setActionListener(e -> ajuste.ajusteFormulario());
			mesmaLarguraAcao.setActionListener(e -> larguras.mesma());
			larTotalDirAcao.setActionListener(e -> larguras.total(1));
			larTotalEsqAcao.setActionListener(e -> larguras.total(2));
			dimensaoAcao.setActionListener(e -> ajuste.ajusteDesktop());
			ajustarAcao.setActionListener(e -> ajuste.ajustarDesktop());
			centralizarAcao.setActionListener(e -> alinhamento.centralizar());
			distribuirAcao.setActionListener(e -> distribuicao.distribuir(0));
			larTotalAcao.setActionListener(e -> larguras.total(0));

			atualizarFormAcao.setActionListener(e -> {
				JInternalFrame[] frames = getAllFrames();

				for (JInternalFrame frame : frames) {
					if (frame instanceof ObjetoContainerFormularioInterno) {
						ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
						interno.atualizarFormulario();
					}
				}
			});

			colarAcao.setActionListener(
					e -> Formulario.CopiarColar.colar(Superficie.this, true, popup2.xLocal, popup2.yLocal));
		}

		void preShow(boolean contemFrames) {
			itemColar.setEnabled(!Formulario.CopiarColar.copiadosIsEmpty());
			itemAlinharEsquerdo.setEnabled(contemFrames);
			itemAtualizarForms.setEnabled(contemFrames);
			itemAlinharDireito.setEnabled(contemFrames);
			itemMesmaLargura.setEnabled(contemFrames);
			itemCentralizar.setEnabled(contemFrames);
		}
	}

	public void selecionarConexao(Conexao conexao) {
		if (conexao == null) {
			return;
		}

		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				interno.selecionarConexao(conexao);
			}
		}
	}

	public void criarNovoObjeto(int x, int y) {
		Objeto novo = new Objeto(x, y);

		boolean contem = contem(novo);

		while (contem) {
			novo.setId("" + Objeto.novaSequencia());
			contem = contem(novo);
		}

		addObjeto(novo);
		limparSelecao();
		repaint();
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public static KeyStroke getKeyStroke(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	public void salvar(File file, Conexao conexao) {
		try {
			XMLUtil util = new XMLUtil(file);
			util.prologo();

			util.abrirTag("fvf");
			util.atributo("largura", getWidth());
			util.atributo("altura", getHeight());

			if (conexao != null) {
				util.atributo("conexao", Util.escapar(conexao.getNome()));
			}

			util.fecharTag();

			for (Objeto objeto : objetos) {
				objeto.salvar(util);
			}

			util.ql();

			for (Relacao relacao : relacoes) {
				relacao.salvar(util);
			}

			util.ql();

			JInternalFrame[] frames = getAllFrames();

			for (int i = frames.length - 1; i >= 0; i--) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frames[i];
				Form form = new Form();
				form.copiar(interno);
				form.salvar(util);
			}

			util.finalizarTag("fvf");
			util.close();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public void abrir(List<Objeto> objetos, List<Relacao> relacoes, Dimension d) {
		limpar();

		for (Objeto objeto : objetos) {
			addObjeto(objeto);
		}

		for (Relacao relacao : relacoes) {
			addRelacao(relacao);
		}

		removeAll();

		alinharNomes();
		repaint();
		setPreferredSize(d);
		SwingUtilities.updateComponentTreeUI(getParent());

		for (Objeto objeto : objetos) {
			objeto.ativar();
		}
	}

	public void desenharDesc(boolean b) {
		for (Relacao relacao : relacoes) {
			relacao.setDesenharDescricao(b);
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

		if (estado == Constantes.ARRASTO) {
			addMouseMotionListener(mouseAdapterArrasto);
			addMouseListener(mouseAdapterArrasto);
			this.estado = estado;

		} else if (estado == Constantes.ROTULOS) {
			addMouseMotionListener(mouseAdapterRotulos);
			addMouseListener(mouseAdapterRotulos);
			this.estado = estado;

		} else if (estado == Constantes.RELACAO) {
			addMouseMotionListener(mouseAdapterRelacao);
			addMouseListener(mouseAdapterRelacao);
			this.estado = estado;

		} else if (estado == Constantes.SELECAO) {
			addMouseMotionListener(mouseAdapterSelecao);
			addMouseListener(mouseAdapterSelecao);
			this.estado = estado;
		}

		if (relacoes != null) {
			for (Relacao relacao : relacoes) {
				relacao.setSelecionado(false);
			}
		}

		if (objetos != null) {
			limparSelecao();
		}
	}

	public void excluido() {
		for (Objeto objeto : objetos) {
			objeto.setSuperficie(null);
			objeto.desativar();
		}
	}

	@Override
	public void buscaAutomatica(GrupoBuscaAuto grupo, String argumentos, ObjetoContainer objContainer) {
		super.buscaAutomatica(grupo, argumentos, objContainer);

		if (Preferencias.isAbrirAuto()) {
			limparSelecao();

			for (TabelaBuscaAuto tabela : grupo.getTabelas()) {
				if (!tabela.isProcessado()) {
					buscaAutomaticaFinal(tabela, argumentos);
				}
			}

			if (getPrimeiroObjetoSelecionado() != null) {
				formulario.destacar(container.getConexaoPadrao(), Superficie.this,
						Preferencias.getTipoContainerPesquisaAuto());
			}
		}
	}

	private void buscaAutomaticaFinal(TabelaBuscaAuto tabela, String argumentos) {
		Objeto objeto = null;

		for (Objeto obj : objetos) {
			if (Util.estaVazio(tabela.getApelido()) && obj.getTabela2().equalsIgnoreCase(tabela.getNome())) {
				objeto = obj;
				break;
			}
		}

		if (objeto == null || !objeto.isAbrirAuto()) {
			return;
		}

		Frame frame = formulario;

		if (container.getContainerFormulario() != null) {
			frame = container.getContainerFormulario();
		}

		objeto.setComplemento("AND " + tabela.getCampo() + " IN (" + argumentos + ")");
		Conexao conexao = container.getConexaoPadrao();
		objeto.setTabelaBuscaAuto(tabela);

		if (Preferencias.isAbrirAutoDestacado()) {
			ObjetoContainerFormulario form = new ObjetoContainerFormulario(formulario, conexao, objeto, getGraphics());
			form.setLocationRelativeTo(frame);
			form.setVisible(true);
		} else {
			objeto.setSelecionado(true);
		}

		tabela.setProcessado(true);
	}

	public void atualizarTotal(Conexao conexao, MenuItem menuItem, Label label) {
		if (conexao == null) {
			return;
		}

		int total = preTotalRecente(label);

		if (total > 0) {
			new ThreadTotal(conexao, menuItem, label, total).start();
		}
	}

	private int preTotalRecente(Label label) {
		int total = 0;

		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				objeto.setCorFonte(Preferencias.getCorAntesTotalRecente());
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

		public ThreadTotal(Conexao conexao, MenuItem menuItem, Label label, int total) {
			this.menuItem = menuItem;
			this.conexao = conexao;
			this.label = label;
			this.total = total;
		}

		@Override
		public void run() {
			label.setForeground(Preferencias.getCorTotalAtual());
			label.setText("0 / " + total);
			boolean processado = false;
			menuItem.setEnabled(false);
			int atual = 0;

			for (Objeto objeto : objetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					try {
						Connection conn = Conexao.getConnection(conexao);
						int i = Persistencia.getTotalRegistros(conn, objeto, "", conexao);
						objeto.setCorFonte(Preferencias.getCorTotalAtual());
						label.setText(++atual + " / " + total);
						processado = true;
						objeto.setTag(i);
						repaint();
						sleep(Preferencias.getIntervaloComparacao());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("TOTAL", ex, Superficie.this);
					}
				}
			}

			if (processado) {
				label.setText(Mensagens.getString("label.threadTotalAtual"));
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

		if (contem && Util.confirmaExclusao(Superficie.this)) {
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

		if (font == null) {
			return;
		}

		FontMetrics fm = getFontMetrics(font);

		if (fm == null) {
			return;
		}

		int total = preTotalRecente(label);

		if (total > 0) {
			new ThreadRecente(conexao, fm, menuItem, label, total).start();
		}
	}

	private class ThreadRecente extends Thread {
		final MenuItem menuItem;
		final Conexao conexao;
		final FontMetrics fm;
		final Label label;
		final int total;

		ThreadRecente(Conexao conexao, FontMetrics fm, MenuItem menuItem, Label label, int total) {
			this.menuItem = menuItem;
			this.conexao = conexao;
			this.label = label;
			this.total = total;
			this.fm = fm;
		}

		@Override
		public void run() {
			label.setForeground(Preferencias.getCorComparaRec());
			label.setText("0 / " + total);
			boolean processado = false;
			menuItem.setEnabled(false);
			int atual = 0;

			for (Objeto objeto : objetos) {
				if (!Util.estaVazio(objeto.getTabela2())) {
					try {
						Connection conn = Conexao.getConnection(conexao);
						int i = Persistencia.getTotalRegistros(conn, objeto, "", conexao);
						label.setText(++atual + " / " + total);
						processarRecente(objeto, i, fm);
						processado = true;
						repaint();
						sleep(Preferencias.getIntervaloComparacao());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("RECENTE", ex, Superficie.this);
					}
				}
			}

			if (processado) {
				label.setText(Mensagens.getString("label.threadRecente"));
			}

			menuItem.setEnabled(true);
		}

		private void processarRecente(Objeto objeto, int recente, FontMetrics fm) {
			objeto.setCorFonte(Preferencias.getCorComparaRec());
			long diff = recente - objeto.getTag();

			if (diff == 0) {
				return;
			}

			int largura = fm.stringWidth(objeto.getId());
			Objeto info = new Objeto(objeto.x + largura + Objeto.DIAMETRO, objeto.y, diff > 0 ? "create2" : "delete");
			info.setId(diff + " - " + recente + " - " + Objeto.novaSequencia());
			info.deslocamentoXId = objeto.deslocamentoXId;
			info.deslocamentoYId = objeto.deslocamentoYId;
			info.setCorFonte(objeto.getCorFonte());
			info.setTransparente(true);
			addObjeto(info);
		}
	}

	public void abrirExportacaoImportacaoMetadado(Metadado metadado, boolean exportacao, boolean circular) {
		List<String> lista = metadado.getListaStringExpImp(exportacao);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int comprimento = Math.min(d.width, d.height) / 2 - 50;
		Vetor vetor = new Vetor(comprimento, 0);
		int centroY = d.height / 2 - 25;
		int centroX = d.width / 2;
		final int cem = 100;

		Objeto centro = new Objeto(centroX, centroY);
		centro.setTabela(metadado.getDescricao());
		centro.setChaves(metadado.getChaves());
		centro.setId(metadado.getDescricao());
		Metadado raiz = metadado.getPai();
		addObjeto(centro);

		if (!circular) {
			centro.x = 20;
			centro.y = 20;
		}

		if (lista.isEmpty()) {
			return;
		}

		int graus = 360 / lista.size();
		int y = centro.y + cem;

		for (int i = 0; i < lista.size(); i++) {
			String tabelaIds = lista.get(i);

			Objeto objeto = new Objeto(centroX + (int) vetor.getX(), centroY + (int) vetor.getY());
			objeto.setId(tabelaIds);
			addObjeto(objeto);

			Relacao relacao = new Relacao(centro, !exportacao, objeto, exportacao);
			addRelacao(relacao);

			vetor.rotacionar(graus);

			int pos = tabelaIds.indexOf('(');
			String nome = tabelaIds.substring(0, pos);
			objeto.setTabela(nome);

			Metadado tabela = raiz.getMetadado(nome);

			if (tabela != null) {
				objeto.setChaves(tabela.getChaves());
			}

			if (!circular) {
				objeto.x = 20;
				objeto.y = y;
				y += cem;
			}
		}

		if (!circular) {
			setPreferredSize(new Dimension(0, y));
			SwingUtilities.updateComponentTreeUI(getParent());
		}
	}

	public void prefixoNomeTabela(String prefixoNomeTabela) {
		for (Objeto objeto : objetos) {
			objeto.setPrefixoNomeTabela(prefixoNomeTabela);
		}
	}
}