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
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.persist.arquivo.ArquivoUtil;
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
		periodos[0] = criarPeriodo(1, 300, 10);
		periodos[1] = criarPeriodo(2, 300, 60);
		periodos[2] = criarPeriodo(3, 300, 110);
		periodos[3] = criarPeriodo(4, 300, 160);
		configProximo();
	}

	private Periodo criarPeriodo(int id, int x, int y) {
		Periodo p = new Periodo(id, this);
		p.setX(x);
		p.setY(y);
		return p;
	}

	private void configProximo() {
		List<Ponto> pontos = new ArrayList<>();
		for (Periodo p : periodos) {
			p.addPontos(pontos);
		}
		Iterator<Ponto> it = pontos.iterator();
		Ponto ult = null;
		while (it.hasNext()) {
			Ponto p = it.next();
			if (ult != null) {
				ult.setProximo(p);
			}
			ult = p;
		}
		if (ult != null) {
			ult.setProximo(pontos.get(0));
		}
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

		int horaAtual = HoraUtil.getHoraAtualInt();
		g2.drawString(HoraUtil.formatar(horaAtual), x, y);
		g2.drawString("<<< HORA ATUAL", x2, y);
		y += 120;

		int faltando = HoraUtil.OITO_HORAS - segundos;
		if (faltando > 0) {
			g2.drawString(HoraUtil.formatar(faltando), x, y);
			g2.drawString("<<< FALTANDO", x2, y);
			y += 120;

			g2.drawString(HoraUtil.formatar(horaAtual + faltando), x, y);
			g2.drawString("<<< FINAL", x2, y);
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

	void abrir(File file) {
		List<String> lista = ArquivoUtil.lerArquivo(file);
		for (Periodo p : periodos) {
			p.limpar();
			p.abrir(lista);
		}
	}

	void limpar() {
		for (Periodo p : periodos) {
			p.limpar();
		}
		repaint();
	}

	void salvar(File file) {
		try {
			PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name());
			for (Periodo p : periodos) {
				p.salvar(pw);
			}
			pw.close();
		} catch (Exception ex) {
			//
		}
	}
}