package br.com.persist.plugins.ponto;

import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.List;

import br.com.persist.assistencia.HoraUtil;
import br.com.persist.assistencia.HoraUtilException;

public class Periodo {
	private final Hora esquerdo;
	private final Hora direito;
	private final int id;

	public Periodo(int id, PontoListener listener) {
		esquerdo = new Hora(listener);
		direito = new Hora(listener);
		this.id = id;
	}

	public Ponto get(int x, int y) {
		Ponto resp = esquerdo.get(x, y);
		if (resp != null) {
			return resp;
		}
		return direito.get(x, y);
	}

	public void desenhar(Graphics2D g2, int larChar) throws HoraUtilException {
		esquerdo.desenhar(g2, larChar);
		direito.desenhar(g2, larChar);

		int segundos = getTotalSegundos();
		if (segundos != 0) {
			g2.drawString(HoraUtil.formatar(segundos), esquerdo.getLargura() + 25,
					esquerdo.getY() + esquerdo.getAltura() - 5);
			int segundos2 = direito.getTotalSegundos();
			if (segundos2 == 0) {
				segundos2 = HoraUtil.getSegundos(HoraUtil.getHoraAtual());
				g2.drawString(HoraUtil.formatar(segundos2), direito.getLargura() + 25,
						direito.getY() + direito.getAltura() - 5);
			}
		}
	}

	public int getTotalSegundos() throws HoraUtilException {
		int segundos = esquerdo.getTotalSegundos();
		if (segundos != 0) {
			int segundos2 = direito.getTotalSegundos();
			if (segundos2 == 0) {
				segundos2 = HoraUtil.getSegundos(HoraUtil.getHoraAtual());
			}
			return HoraUtil.getDiff(segundos2, segundos);
		}
		return 0;
	}

	public void setX(int x) {
		esquerdo.setX(x);
		direito.setX(x + 300);
	}

	public void setY(int y) {
		esquerdo.setY(y);
		direito.setY(y);
	}

	public void abrir(List<String> lista) {
		String prefixo = id + ">>>";
		for (String string : lista) {
			if (string.startsWith(prefixo)) {
				abrir(string.substring(prefixo.length()));
			}
		}
	}

	private void abrir(String string) {
		int pos = string.indexOf("D");
		esquerdo.abrir(string.substring(0, pos));
		direito.abrir(string.substring(pos + 1));
	}

	public void limpar() {
		esquerdo.limpar();
		direito.limpar();
	}

	public void salvar(PrintWriter pw) {
		pw.println(id + ">>>E" + esquerdo.getString() + "D" + direito.getString());
	}

	public void addPontos(List<Ponto> pontos) {
		esquerdo.addPontos(pontos);
		direito.addPontos(pontos);
	}
}