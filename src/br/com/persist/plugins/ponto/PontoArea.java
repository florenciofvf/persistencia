package br.com.persist.plugins.ponto;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.HoraUtil;
import br.com.persist.componente.Panel;

public class PontoArea extends Panel implements PontoListener, AWTEventListener {
	private transient Periodo[] periodos = new Periodo[4];
	private static final long serialVersionUID = 1L;
	private transient Ponto selecionado;
	private int larChar;

	public PontoArea() {
		super(null);
	}

	public void init() {
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		addMouseListener(new MouseListener());
		periodos[0] = criarPeriodo(300, 10);
		periodos[1] = criarPeriodo(300, 60);
		periodos[2] = criarPeriodo(300, 110);
		periodos[3] = criarPeriodo(300, 160);
	}

	private Periodo criarPeriodo(int x, int y) {
		Periodo p = new Periodo(this);
		p.setX(x);
		p.setY(y);
		return p;
	}

	@Override
	public void requestFocus(Ponto p) {
		if (selecionado != null) {
			selecionado.focusOut();
		}
		selecionado = p;
		if (selecionado != null) {
			selecionado.focusIn();
		}
		repaint();
	}

	private class MouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent evento) {
			int x = evento.getX();
			int y = evento.getY();
			Ponto ponto = null;
			for (Periodo p : periodos) {
				Ponto resp = p.get(x, y);
				if (resp != null) {
					ponto = resp;
				}
			}
			if (ponto != null) {
				ponto.requestFocus();
			} else {
				requestFocus(null);
			}
		}
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		if (event instanceof KeyEvent) {
			KeyEvent evento = (KeyEvent) event;
			if (selecionado != null && evento.getID() == KeyEvent.KEY_TYPED) {
				selecionado.setChar(evento.getKeyChar());
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(Constantes.STROKE_PADRAO);
		g2.setFont(PontoConstantes.FONT_PONTO);

		if (larChar == 0) {
			FontMetrics fm = getFontMetrics(PontoConstantes.FONT_PONTO);
			larChar = fm.charWidth(' ');
		}

		int segundos = 0;

		try {
			for (Periodo p : periodos) {
				p.desenhar(g2, larChar);
				segundos += p.getTotalSegundos();
			}
		} catch (Exception ex) {
			//
		}

		int x = 40;
		int y = 300;
		int x2 = 300;

		g2.setFont(PontoConstantes.FONT_INFO);
		g2.setColor(Color.BLACK);

		g2.drawString(HoraUtil.formatar(segundos), x, y);
		g2.drawString("<<< TRABALHANDO", x2, y);
		y += 120;

		g2.drawString(HoraUtil.getHoraAtual(), x, y);
		g2.drawString("<<< HORA ATUAL", x2, y);
		y += 120;

		int faltando = HoraUtil.OITO_HORAS - segundos;
		if (faltando > 0) {
			g2.drawString(HoraUtil.formatar(faltando), x, y);
			g2.drawString("<<< FALTANDO", x2, y);
		} else if (faltando < 0) {
			g2.drawString(HoraUtil.formatar(faltando * -1), x, y);
			g2.drawString("<<< SALDO POSITIVO", x2, y);
		} else {
			g2.drawString(HoraUtil.formatar(faltando), x, y);
			g2.drawString("<<< 8 HORAS", x2, y);
		}
	}

	@Override
	public void desenhar(Ponto p) {
		repaint();
	}
}