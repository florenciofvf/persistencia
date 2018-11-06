package br.com.persist.formulario;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.dialogo.ObjetoDialogo;
import br.com.persist.dialogo.RelacaoDialogo;
import br.com.persist.objeto.FormularioExterno;
import br.com.persist.objeto.FormularioInterno;
import br.com.persist.util.Acao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;

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
		limpar();
	}

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

	private MouseAdapter mouseAdapterRotulos = new MouseAdapter() {
		Relacao selecionadoRelacao;
		Objeto selecionadoObjeto;

		@Override
		public void mousePressed(MouseEvent e) {
			ultX = e.getX();
			ultY = e.getY();
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

		@Override
		public void mouseClicked(MouseEvent e) {
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
	};

	private MouseAdapter mouseAdapterArrasto = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			ultX = e.getX();
			ultY = e.getY();
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
	};

	private MouseAdapter mouseAdapterRelacao = new MouseAdapter() {
		Objeto destino;
		Objeto origem;

		@Override
		public void mousePressed(MouseEvent e) {
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
				new RelacaoDialogo(formulario, Superficie.this, relacao);
			}
		}
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
				popup.popupObjeto(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(Superficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.x = x;
				popup2.y = y;
				popup2.itemCentralizar.setEnabled(getAllFrames().length > 0);
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
				popup.popupObjeto(selecionadoObjeto != null && selecionadoRelacao == null);
				popup.show(Superficie.this, x, y);
			} else if (e.isPopupTrigger()) {
				popup2.x = x;
				popup2.y = y;
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
				if (selecionadoObjeto != null && !Util.estaVazio(selecionadoObjeto.getTabela())) {
					Conexao conexao = container.getConexaoPadrao();
					setComplemento(conexao, selecionadoObjeto);
					new FormularioExterno(formulario, selecionadoObjeto, getGraphics(), conexao);

				} else if (selecionadoRelacao != null) {
					new RelacaoDialogo(formulario, Superficie.this, selecionadoRelacao);
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
	}

	public void excluir(Objeto obj) {
		int indice = getIndice(obj);

		if (indice >= 0) {
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
		MenuItem itemHorizontal = new MenuItem(new AlinhamentoAcao(true, "label.horizontal"));
		MenuItem itemVertical = new MenuItem(new AlinhamentoAcao(false, "label.vertical"));
		MenuItem itemFormulario = new MenuItem(new FormularioAcao());
		MenuItem itemCopiarCor = new MenuItem(new CopiarCorAcao());
		MenuItem itemDestacar = new MenuItem(new DestacarAcao());
		MenuItem itemColarCor = new MenuItem(new ColarCorAcao());
		MenuItem itemCopiar = new MenuItem(new CopiarAcao());

		SuperficiePopup() {
			add(itemHorizontal);
			add(itemVertical);
			addSeparator();
			add(itemCopiar);
			add(itemCopiarCor);
			addSeparator();
			add(itemColarCor);
			addSeparator();
			add(itemDestacar);
			add(itemFormulario);
			addSeparator();
			add(new MenuItem(new ExcluirAcao()));
			addSeparator();
			add(new MenuItem(new ConfiguracaoAcao()));
		}

		void popupObjeto(boolean b) {
			itemFormulario.setEnabled(b);
			itemHorizontal.setEnabled(b);
			itemVertical.setEnabled(b);
			itemDestacar.setEnabled(b);
			itemCopiar.setEnabled(b);
		}
	}

	private class DestacarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public DestacarAcao() {
			super(true, "label.desktop", Icones.CUBO2);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			formulario.destacar(container.getConexaoPadrao(), Superficie.this, false);
		}
	}

	private class FormularioAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FormularioAcao() {
			super(true, "label.formulario", Icones.PANEL);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			formulario.destacar(container.getConexaoPadrao(), Superficie.this, true);
		}
	}

	private class CopiarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CopiarAcao() {
			super(true, "label.copiar", Icones.COPIA);

			inputMap().put(getKeyStroke(KeyEvent.VK_C), chave);
			getActionMap().put(chave, this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			formulario.copiar(Superficie.this);
		}
	}

	private class CopiarCorAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CopiarCorAcao() {
			super(true, "label.copiar_cor", Icones.COPIA);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selecionadoObjeto != null) {
				formulario.setCor(selecionadoObjeto.getCor());
			} else if (selecionadoRelacao != null) {
				formulario.setCor(selecionadoRelacao.getCor());
			}
		}
	}

	private class ColarCorAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ColarCorAcao() {
			super(true, "label.colar_cor", Icones.COLAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selecionadoObjeto != null && formulario.getCor() != null) {
				selecionadoObjeto.setCor(formulario.getCor());
				repaint();
			} else if (selecionadoRelacao != null && formulario.getCor() != null) {
				selecionadoRelacao.setCor(formulario.getCor());
				repaint();
			}
		}
	}

	private class SuperficiePopup2 extends Popup {
		private static final long serialVersionUID = 1L;
		MenuItem itemCentralizar = new MenuItem(new CentralizarAcao());
		MenuItem itemDimensoes = new MenuItem(new DimensaoAcao());
		MenuItem itemAjustes = new MenuItem(new AjustarAcao());
		int x, y;

		SuperficiePopup2() {
			add(new MenuItem(new ColarAcao()));
			addSeparator();
			add(itemCentralizar);
			addSeparator();
			add(itemDimensoes);
			add(itemAjustes);
		}
	}

	private class CentralizarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CentralizarAcao() {
			super(true, "label.centralizar", Icones.CENTRALIZAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			centralizar();
		}
	}

	private class DimensaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public DimensaoAcao() {
			super(true, "label.dimensao", Icones.RECT);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			configDimension();
		}
	}

	private class AjustarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public AjustarAcao() {
			super(true, "label.ajustar", Icones.RECT);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ajustarDimension();
		}
	}

	private class ColarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ColarAcao() {
			super(true, "label.colar", Icones.COLAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			formulario.colar(Superficie.this, true, popup2.x, popup2.y);
			repaint();
		}
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public static KeyStroke getKeyStroke(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private class ExcluirAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirAcao() {
			super(true, "label.excluir", Icones.EXCLUIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			excluirSelecionados();
		}
	}

	private class ConfiguracaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ConfiguracaoAcao() {
			super(true, "label.configuracoes", Icones.CONFIG);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selecionadoObjeto != null) {
				new ObjetoDialogo(formulario, Superficie.this, selecionadoObjeto);

			} else if (selecionadoRelacao != null) {
				new RelacaoDialogo(formulario, Superficie.this, selecionadoRelacao);
			}
		}
	}

	private class AlinhamentoAcao extends Acao {
		private static final long serialVersionUID = 1L;
		private final boolean horizontal;

		public AlinhamentoAcao(boolean horizontal, String chave) {
			super(true, chave, horizontal ? Icones.HORIZONTAL : Icones.VERTICAL);
			this.horizontal = horizontal;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Objeto primeiro = null;
			int i = 0;

			for (; i < objetos.length; i++) {
				if (objetos[i].isSelecionado()) {
					primeiro = objetos[i];
					i++;
					break;
				}
			}

			if (primeiro != null) {
				for (; i < objetos.length; i++) {
					Objeto objeto = objetos[i];

					if (objeto.isSelecionado()) {
						if (horizontal) {
							objeto.y = primeiro.y;
						} else {
							objeto.x = primeiro.x;
						}
					}
				}
			}

			repaint();
		}
	}

	public void salvar(File file) {
		try {
			XMLUtil util = new XMLUtil(file);
			util.prologo();

			util.abrirTag("fvf");
			util.atributo("largura", getWidth());
			util.atributo("altura", getHeight()).fecharTag();

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

		JInternalFrame[] frames = getAllFrames();

		for (int i = 0; i < frames.length; i++) {
			JInternalFrame f = frames[i];
			remove(f);
		}

		alinharNomes();
		repaint();
		setPreferredSize(d);
		SwingUtilities.updateComponentTreeUI(getParent());
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
}