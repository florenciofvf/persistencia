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
	private final Formulario formulario;
	private Objeto ultimoSelecionado;
	private Objeto selecionado2;
	private Objeto selecionado;
	private Relacao[] relacoes;
	private Objeto[] objetos;

	public Superficie(Formulario formulario) {
		addMouseMotionListener(mouseMotionListener);
		addMouseListener(mouseListener);
		this.formulario = formulario;
		limpar();
	}

	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (selecionado != null) {
				selecionado.x = e.getX() - Objeto.diametro / 2;
				selecionado.y = e.getY() - Objeto.diametro / 2;
				repaint();
			}
		}
	};

	private MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS && selecionado != null) {
				new FormularioObjeto(formulario, selecionado, getGraphics());
			}
		};

		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			final int x = e.getX();
			final int y = e.getY();
			selecionado2 = null;
			selecionado = null;

			if (!e.isShiftDown()) {
				for (Objeto objeto : objetos) {
					objeto.setSelecionado(false);
				}
			}

			for (Objeto objeto : objetos) {
				if (objeto.contem(x, y)) {
					objeto.setSelecionado(true);
					ultimoSelecionado = objeto;
					break;
				}
			}

			int i = 0;

			for (; i < objetos.length; i++) {
				if (objetos[i].isSelecionado()) {
					selecionado = objetos[i];
					i++;
					break;
				}
			}

			for (; i < objetos.length; i++) {
				if (objetos[i].isSelecionado()) {
					selecionado2 = objetos[i];
					i++;
					break;
				}
			}

			for (; i < objetos.length; i++) {
				objetos[i].setSelecionado(false);
			}

			if (selecionado != null && selecionado2 != null && ultimoSelecionado != selecionado2) {
				Objeto sel1 = selecionado2;
				Objeto sel2 = selecionado;
				selecionado2 = sel2;
				selecionado = sel1;
			}

			if (e.isPopupTrigger() && selecionado != null) {
				popup.show(Superficie.this, x, y);
			}

			repaint();
		}
	};

	public Formulario getFormulario() {
		return formulario;
	}

	public Objeto getSelecionado2() {
		return selecionado2;
	}

	public Objeto getSelecionado() {
		return selecionado;
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
			new ObjetoDialogo(formulario, Superficie.this, selecionado);
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
			if (selecionado == null || selecionado2 == null) {
				return;
			}

			if (horizontal) {
				selecionado2.y = selecionado.y;
			} else {
				selecionado2.x = selecionado.x;
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