package br.com.persist.formulario;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.dialogo.MacroDialogo;
import br.com.persist.dialogo.ObjetoDialogo;
import br.com.persist.dialogo.RelacaoDialogo;
import br.com.persist.objeto.FormularioExterno;
import br.com.persist.objeto.FormularioInterno;
import br.com.persist.objeto.FormularioSelect;
import br.com.persist.objeto.PainelObjeto;
import br.com.persist.util.Acao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Macro.Instrucao;
import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.BuscaAuto.Tabela;

public class Superficie extends Desktop {
	private static final long serialVersionUID = 1L;
	private final SuperficiePopup2 popup2 = new SuperficiePopup2();
	private final SuperficiePopup popup = new SuperficiePopup();
	private final Inversao inversao = new Inversao();
	private final Linha linha = new Linha();
	private final Area area = new Area();
	private Relacao selecionadoRelacao;
	private final Container container;
	private Objeto selecionadoObjeto;
	private Relacao[] relacoes;
	private Objeto[] objetos;
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

	Action threadProcessar = new AbstractAction() {
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

	Action threadDesativar = new AbstractAction() {
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

	Action macroLista = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Formulario.macro.isEmpty()) {
				return;
			}

			Frame frame = formulario;

			if (container.getFormularioSuperficie() != null) {
				frame = container.getFormularioSuperficie();
			}

			new MacroDialogo(frame, Superficie.this);
		}
	};

	Action macro = new AbstractAction() {
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

	Action zoomMenos = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : objetos) {
				objeto.zoomMenos();
			}

			repaint();
		}
	};

	Action zoomMais = new AbstractAction() {
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

	private MouseAdapter mouseAdapterRotulos = new MouseAdapter() {
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
				if (alt & !shift) {
					selecionadoObjeto.deslocamentoXId += recX - ultX;
				} else if (!alt & shift) {
					selecionadoObjeto.deslocamentoYId += recY - ultY;
				} else {
					selecionadoObjeto.deslocamentoXId += recX - ultX;
					selecionadoObjeto.deslocamentoYId += recY - ultY;
				}
			} else if (selecionadoRelacao != null) {
				if (alt & !shift) {
					selecionadoRelacao.deslocamentoXDesc += recX - ultX;
				} else if (!alt & shift) {
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

	private MouseAdapter mouseAdapterArrasto = new MouseAdapter() {
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
				if (alt & !shift) {
					objeto.x += recX - ultX;
				} else if (!alt & shift) {
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
		};
	};

	private MouseAdapter mouseAdapterRelacao = new MouseAdapter() {
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
				linha.x1 = origem.x + Objeto.diametro / 2;
				linha.y1 = origem.y + Objeto.diametro / 2;
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

				if (container.getFormularioSuperficie() != null) {
					frame = container.getFormularioSuperficie();
				}

				new RelacaoDialogo(frame, Superficie.this, relacao);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				duploClick(e);
			}
		};
	};

	private MouseAdapter mouseAdapterSelecao = new MouseAdapter() {
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
				popup.configItens(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(Superficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.x = x;
				popup2.y = y;
				popup2.configItens(getAllFrames().length > 0);
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
					if (alt & !shift) {
						objeto.x += recX - ultX;
					} else if (!alt & shift) {
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

			if (area.largura > Objeto.diametro && area.altura > Objeto.diametro) {
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
				popup.configItens(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(Superficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.x = x;
				popup2.y = y;
				popup2.configItens(getAllFrames().length > 0);
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

				if (container.getFormularioSuperficie() != null) {
					frame = container.getFormularioSuperficie();
				}

				if (selecionadoObjeto != null) {
					if (!Util.estaVazio(selecionadoObjeto.getTabela2())) {
						Conexao conexao = container.getConexaoPadrao();
						setComplemento(conexao, selecionadoObjeto);
						new FormularioExterno(formulario, frame, selecionadoObjeto, getGraphics(), conexao, false);
					} else {
						popup.new ConfiguracaoAcao().actionPerformed(null);
					}
				} else if (selecionadoRelacao != null) {
					popup.new ConfiguracaoAcao().actionPerformed(null);
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
		int x, y, largura, altura;
		int x1, y1, x2, y2;

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
			return (objeto.x >= this.x && objeto.x + Objeto.diametro <= this.x + largura)
					&& (objeto.y >= this.y && objeto.y + Objeto.diametro <= this.y + altura);
		}
	}

	private class Linha {
		int x1, y1, x2, y2;

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

	public void limpar() {
		relacoes = new Relacao[0];
		objetos = new Objeto[0];
		repaint();
	}

	private class SuperficiePopup extends Popup {
		private static final long serialVersionUID = 1L;
		MenuItem itemDistribuiHorizontal = new MenuItem(new DistribuicaoAcao(true, "label.horizontal"));
		MenuItem itemDistribuiVertical = new MenuItem(new DistribuicaoAcao(false, "label.vertical"));
		MenuItem itemAlinhaHorizontal = new MenuItem(new AlinhamentoAcao(true, "label.horizontal"));
		MenuItem itemAlinhaVertical = new MenuItem(new AlinhamentoAcao(false, "label.vertical"));
		MenuItem itemFormularioSel = new MenuItem(new FormularioSelectAcao());
		MenuItem itemFormulario = new MenuItem(new FormularioAcao());
		MenuItem itemDestacar = new MenuItem(new DestacarAcao());
		Menu menuDistribuicao = new Menu("label.distribuicao");
		MenuItem itemCopiar = new MenuItem(new CopiarAcao());
		MenuItem itemPartir = new MenuItem(new PartirAcao());
		Menu menuAlinhamento = new Menu("label.alinhamento");

		SuperficiePopup() {
			menuDistribuicao.add(itemDistribuiHorizontal);
			menuDistribuicao.add(itemDistribuiVertical);
			menuAlinhamento.add(itemAlinhaHorizontal);
			menuAlinhamento.add(itemAlinhaVertical);

			add(menuAlinhamento);
			addSeparator();
			add(menuDistribuicao);
			addSeparator();
			add(itemCopiar);
			addSeparator();
			add(itemDestacar);
			add(itemFormulario);
			add(itemFormularioSel);
			addSeparator();
			add(new MenuItem(new ExcluirAcao()));
			addSeparator();
			add(itemPartir);
			addSeparator();
			add(new MenuItem(new ConfiguracaoAcao()));
		}

		void configItens(boolean objetoSelecionado) {
			menuDistribuicao.setEnabled(objetoSelecionado);
			menuAlinhamento.setEnabled(objetoSelecionado);
			itemFormulario.setEnabled(objetoSelecionado);
			itemDestacar.setEnabled(objetoSelecionado);
			itemPartir.setEnabled(!objetoSelecionado);
			itemCopiar.setEnabled(objetoSelecionado);
		}

		class DestacarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			DestacarAcao() {
				super(true, "label.desktop", Icones.CUBO2);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.destacar(container.getConexaoPadrao(), Superficie.this, false);
			}
		}

		class FormularioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			FormularioAcao() {
				super(true, "label.formulario", Icones.PANEL);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.destacar(container.getConexaoPadrao(), Superficie.this, true);
			}
		}

		class FormularioSelectAcao extends Acao {
			private static final long serialVersionUID = 1L;

			FormularioSelectAcao() {
				super(true, "label.consulta", Icones.PANEL3);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Frame frame = formulario;

				if (container.getFormularioSuperficie() != null) {
					frame = container.getFormularioSuperficie();
				}

				new FormularioSelect(formulario, frame, container.getConexaoPadrao());
			}
		}

		class CopiarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			CopiarAcao() {
				super(true, "label.copiar", Icones.COPIA);

				inputMap().put(getKeyStroke(KeyEvent.VK_C), chave);
				Superficie.this.getActionMap().put(chave, this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Formulario.copiar(Superficie.this);
			}
		}

		class ExcluirAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ExcluirAcao() {
				super(true, "label.excluir", Icones.EXCLUIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				excluirSelecionados();
			}
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
						novo.setId("" + Objeto.novoID());
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

		class ConfiguracaoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ConfiguracaoAcao() {
				super(true, "label.configuracoes", Icones.CONFIG);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Frame frame = formulario;

				if (container.getFormularioSuperficie() != null) {
					frame = container.getFormularioSuperficie();
				}

				if (selecionadoObjeto != null) {
					new ObjetoDialogo(frame, Superficie.this, selecionadoObjeto);

				} else if (selecionadoRelacao != null) {
					new RelacaoDialogo(frame, Superficie.this, selecionadoRelacao);
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
						Formulario.macro.y(selecionadoObjeto.y);
					} else {
						Formulario.macro.x(selecionadoObjeto.x);
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
					int total = 0;

					for (Objeto objeto : objetos) {
						if (objeto.isSelecionado()) {
							total++;
						}
					}

					if (total < 3) {
						return;
					}

					List<Objeto> lista = new ArrayList<>();

					for (Objeto objeto : objetos) {
						if (objeto.isSelecionado()) {
							lista.add(objeto);
						}
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
		MenuItem itemAtualizarForms = new MenuItem(new AtualizarFormularioAcao());
		MenuItem itemAlinharEsquerdo = new MenuItem(new AlinharEsquerdoAcao());
		MenuItem itemAlinharDireito = new MenuItem(new AlinharDireitoAcao());
		MenuItem itemMesmaLargura = new MenuItem(new MesmaLarguraAcao());
		MenuItem itemCentralizar = new MenuItem(new CentralizarAcao());
		MenuItem itemCriarObjeto = new MenuItem(new CriarObjetoAcao());
		MenuItem itemDimensoes = new MenuItem(new DimensaoAcao());
		MenuItem itemAjustes = new MenuItem(new AjustarAcao());
		MenuItem itemColar = new MenuItem(new ColarAcao());
		int x, y;

		SuperficiePopup2() {
			add(itemCriarObjeto);
			addSeparator();
			add(itemColar);
			addSeparator();
			add(itemAtualizarForms);
			addSeparator();
			add(itemAlinharEsquerdo);
			add(itemAlinharDireito);
			add(itemMesmaLargura);
			add(itemCentralizar);
			addSeparator();
			add(itemDimensoes);
			add(itemAjustes);
		}

		void configItens(boolean contemFrames) {
			itemColar.setEnabled(!Formulario.COPIADOS.isEmpty());
			itemAlinharEsquerdo.setEnabled(contemFrames);
			itemAtualizarForms.setEnabled(contemFrames);
			itemAlinharDireito.setEnabled(contemFrames);
			itemMesmaLargura.setEnabled(contemFrames);
			itemCentralizar.setEnabled(contemFrames);
		}

		class AlinharEsquerdoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AlinharEsquerdoAcao() {
				super(true, "label.alinhar_esquerdo", Icones.ALINHA_ESQUERDO);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				alinharEsquerdo();
			}
		}

		class AlinharDireitoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AlinharDireitoAcao() {
				super(true, "label.alinhar_direito", Icones.ALINHA_DIREITO);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				alinharDireito();
			}
		}

		class MesmaLarguraAcao extends Acao {
			private static final long serialVersionUID = 1L;

			MesmaLarguraAcao() {
				super(true, "label.mesma_largura", Icones.LARGURA);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				mesmaLargura();
			}
		}

		class CentralizarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			CentralizarAcao() {
				super(true, "label.centralizar", Icones.CENTRALIZAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				centralizar();
			}
		}

		class DimensaoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			DimensaoAcao() {
				super(true, "label.dimensao", Icones.RECT);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				ajusteDimension();
			}
		}

		class AjustarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AjustarAcao() {
				super(true, "label.ajustar", Icones.RECT);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				ajustarDimension();
			}
		}

		class ColarAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ColarAcao() {
				super(true, "label.colar", Icones.COLAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Formulario.colar(Superficie.this, true, popup2.x, popup2.y);
				Superficie.this.repaint();
			}
		}

		class CriarObjetoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			CriarObjetoAcao() {
				super(true, "label.criar_objeto", Icones.CRIAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				criarNovoObjeto(popup2.x, popup2.y);
			}
		}

		class AtualizarFormularioAcao extends Acao {
			private static final long serialVersionUID = 1L;

			AtualizarFormularioAcao() {
				super(true, "label.atualizar_forms", Icones.ATUALIZAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JInternalFrame[] frames = getAllFrames();

				for (JInternalFrame frame : frames) {
					if (frame instanceof FormularioInterno) {
						FormularioInterno interno = (FormularioInterno) frame;
						interno.atualizarFormulario();
					}
				}
			}

		}
	}

	public void criarNovoObjeto(int x, int y) {
		Objeto novo = new Objeto(x, y);

		boolean contem = contem(novo);

		while (contem) {
			novo.setId("" + Objeto.novoID());
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
				FormularioInterno interno = (FormularioInterno) frames[i];
				Form form = new Form();
				form.copiar(interno);
				form.salvar(util);
			}

			util.finalizarTag("fvf");
			util.close();
		} catch (Exception e) {
			e.printStackTrace();
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
	public void buscaAutomatica(Grupo grupo, String argumentos, PainelObjeto painelObjeto, AtomicBoolean processado) {
		super.buscaAutomatica(grupo, argumentos, painelObjeto, processado);

		if (Constantes.abrir_auto) {
			limparSelecao();

			for (Tabela tabela : grupo.getTabelas()) {
				if (!tabela.isProcessado()) {
					Objeto objeto = null;

					for (Objeto obj : objetos) {
						if (tabela.getNome().equalsIgnoreCase(obj.getTabela2())) {
							objeto = obj;
							break;
						}
					}

					if (objeto == null || !objeto.isAbrirAuto()) {
						continue;
					}

					Frame frame = formulario;

					if (container.getFormularioSuperficie() != null) {
						frame = container.getFormularioSuperficie();
					}

					Conexao conexao = container.getConexaoPadrao();
					objeto.setComplemento("AND " + tabela.getCampo() + " IN (" + argumentos + ")");

					if (Constantes.abrir_auto_destacado) {
						new FormularioExterno(formulario, frame, objeto, getGraphics(), conexao, false);
						processado.set(true);
					} else {
						objeto.setSelecionado(true);
					}
				}
			}

			if (getPrimeiroObjetoSelecionado() != null) {
				formulario.destacar(container.getConexaoPadrao(), Superficie.this, true);
				processado.set(true);
			}
		}
	}
}