package br.com.persist.formulario;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.dialogo.ObjetoDialogo;
import br.com.persist.util.Acao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;
import br.com.persist.xml.XML;

public class Superficie extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	private SuperficiePopup popup = new SuperficiePopup();
	private final Inversao inversao = new Inversao();
	private final Area area = new Area();
	private final Formulario formulario;
	private Objeto selecionadoObjeto;
	private Relacao[] relacoes;
	private Objeto[] objetos;
	private int ultX;
	private int ultY;

	public Superficie(Formulario formulario) {
		addMouseMotionListener(mouseMotionListener);
		addMouseListener(mouseListener);
		this.formulario = formulario;
		limpar();
	}

	public void limparSelecao() {
		inversao.ultimo = null;

		for (Objeto objeto : objetos) {
			objeto.setSelecionado(false);
		}
	}

	public Objeto[] getSelecionados() {
		List<Objeto> lista = new ArrayList<>();

		for (Objeto objeto : objetos) {
			if (objeto.isSelecionado()) {
				lista.add(objeto);
			}
		}

		return lista.toArray(new Objeto[0]);
	}

	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			boolean movimentou = false;
			int recX = e.getX();
			int recY = e.getY();

			for (Objeto objeto : objetos) {
				if (objeto.isSelecionado()) {
					objeto.x += recX - ultX;
					objeto.y += recY - ultY;
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
	};

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			selecionadoObjeto = null;
			int x = e.getX();
			int y = e.getY();
			area.x1 = x;
			area.y1 = y;
			ultX = x;
			ultY = y;

			for (Objeto objeto : objetos) {
				if (objeto.contem(x, y)) {
					if (!objeto.controlado) {
						objeto.controlado = e.isShiftDown();
					}

					objeto.setSelecionado(true);
					selecionadoObjeto = objeto;
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
			}

			repaint();

			if (e.isPopupTrigger() && selecionadoObjeto != null) {
				popup.show(Superficie.this, x, y);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
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

			if (e.isPopupTrigger() && selecionadoObjeto != null) {
				popup.show(Superficie.this, e.getX(), e.getY());
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (e.isShiftDown()) {
				inversao.inverterSelecao(selecionadoObjeto);
			}

			if (selecionadoObjeto != null && !selecionadoObjeto.isSelecionado()) {
				selecionadoObjeto = null;
			}

			repaint();

			if (e.getClickCount() >= Constantes.DOIS && selecionadoObjeto != null
					&& !Util.estaVazio(selecionadoObjeto.getTabela())) {
				new FormularioObjeto(formulario, selecionadoObjeto, getGraphics());
			}
		}
	};

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
		int x1;
		int y1;
		int x2;
		int y2;

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

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (Relacao relacao : relacoes) {
			relacao.desenhar(g2);
		}

		for (Objeto objeto : objetos) {
			objeto.desenhar(this, g2);
		}

		area.desenhar(g2);
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

	public void excluirSelecionados() {
		Objeto objeto = getPrimeiroSelecionado();

		while (objeto != null) {
			excluir(objeto);
			objeto = getPrimeiroSelecionado();
		}

		repaint();
	}

	private Objeto getPrimeiroSelecionado() {
		for (Objeto objeto : objetos) {
			if (objeto.isSelecionado()) {
				return objeto;
			}
		}

		return null;
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

		for (Relacao relacao : relacoes) {
			Relacao temp = new Relacao(obj1, obj2);
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

		SuperficiePopup() {
			add(new MenuItem(new AlinhamentoAcao(true, "label.horizontal")));
			add(new MenuItem(new AlinhamentoAcao(false, "label.vertical")));
			addSeparator();
			add(new MenuItem(new ConfiguracaoAcao()));
		}
	}

	private class ConfiguracaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ConfiguracaoAcao() {
			super(true, "label.configuracoes", Icones.CONFIG);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new ObjetoDialogo(formulario, Superficie.this, selecionadoObjeto);
		}
	}

	private class AlinhamentoAcao extends Acao {
		private static final long serialVersionUID = 1L;
		private final boolean horizontal;

		public AlinhamentoAcao(boolean horizontal, String chave) {
			super(true, chave, null);
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

			util.abrirTag2("exemplo");

			for (Objeto objeto : objetos) {
				objeto.salvar(util);
			}

			util.ql();

			for (Relacao relacao : relacoes) {
				relacao.salvar(util);
			}

			util.finalizarTag("exemplo");
			util.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void abrir(File file) {
		limpar();

		try {
			List<Objeto> objetos = new ArrayList<>();
			List<Relacao> relacoes = new ArrayList<>();
			XML.processar(file, objetos, relacoes);

			for (Objeto objeto : objetos) {
				addObjeto(objeto);
			}

			for (Relacao relacao : relacoes) {
				addRelacao(relacao);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, this);
		}

		repaint();
	}

	public void desenharIds(boolean b) {
		for (Objeto objeto : objetos) {
			objeto.setDesenharId(b);
		}

		repaint();
	}
}