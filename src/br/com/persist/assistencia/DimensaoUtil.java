package br.com.persist.assistencia;

import javax.swing.Timer;

public class DimensaoUtil {
	private static final Timer timer = new Timer(10, e -> processar());
	private static Dimensao dimensao;

	private DimensaoUtil() {
	}

	private static void processar() {
		if (dimensao == null) {
			timer.stop();
			return;
		}
		if (dimensao.contemScrollVertical()) {
			dimensao.somarAAltura(10);
		} else if (dimensao.contemScrollHorizontal()) {
			dimensao.somarALargura(10);
		} else {
			dimensao.checarMaximoVertical();
			dimensao.checarMaximoHorizontal();
			dimensao = null;
			timer.stop();
		}
	}

	public static void ajustar(Dimensao d) {
		if (d == null) {
			timer.stop();
			return;
		}
		dimensao = d;
		timer.start();
	}
}