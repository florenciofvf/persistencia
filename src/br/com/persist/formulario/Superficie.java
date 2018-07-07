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
import br.com.persist.util.Icones;
import br.com.persist.util.XMLUtil;
import br.com.persist.xml.XML;

public class Superficie extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	private SuperficiePopup popup = new SuperficiePopup();
	private final Formulario formulario;
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
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			selecionado = null;
			int x = e.getX();
			int y = e.getY();

			for (Objeto objeto : objetos) {
				objeto.setSelecionado(false);
			}

			for (Objeto objeto : objetos) {
				if (objeto.contem(x, y)) {
					objeto.setSelecionado(true);
					selecionado = objeto;
					break;
				}
			}

			if (e.isPopupTrigger()) {
				if (selecionado != null) {
					popup.show(Superficie.this, e.getX(), e.getY());
				}
			}

			repaint();
		}
	};

	public Formulario getFormulario() {
		return formulario;
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

	public void addObjeto(Objeto objeto) {
		if (objeto == null) {
			return;
		}

		Objeto[] bkp = objetos;
		objetos = new Objeto[bkp.length + 1];
		System.arraycopy(bkp, 0, objetos, 0, bkp.length);
		objetos[bkp.length] = objeto;
	}

	public void addRelacao(Relacao relacao) {
		if (relacao == null) {
			return;
		}

		Relacao[] bkp = relacoes;
		relacoes = new Relacao[bkp.length + 1];
		System.arraycopy(bkp, 0, relacoes, 0, bkp.length);
		relacoes[bkp.length] = relacao;
	}

	public void limpar() {
		relacoes = new Relacao[0];
		objetos = new Objeto[0];
	}

	private class SuperficiePopup extends Popup {
		private static final long serialVersionUID = 1L;

		SuperficiePopup() {
			add(new MenuItem(new PropriedadeAcao()));
		}
	}

	private class PropriedadeAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public PropriedadeAcao() {
			super(true, "label.propriedades", Icones.BANCO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new ObjetoDialogo(formulario, Superficie.this, selecionado);
		}
	}

	public void salvar() {
		try {
			XMLUtil util = new XMLUtil(new File("exemplo.xml"));
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

	public void abrir() {
		limpar();

		try {
			List<Objeto> objetos = new ArrayList<>();
			List<Relacao> relacoes = new ArrayList<>();
			XML.processar(new File("exemplo.xml"), objetos, relacoes);

			for (Objeto objeto : objetos) {
				addObjeto(objeto);
			}

			for (Relacao relacao : relacoes) {
				addRelacao(relacao);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		repaint();
	}
}