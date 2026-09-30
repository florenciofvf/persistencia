package br.com.persist.assistencia;

public class DimensaoUtil {
	private DimensaoUtil() {
	}

	public static void ajustar(Dimensao d) {
		if (d == null) {
			return;
		}
		if (d.contemScrollVertical()) {
			d.somarAAltura(10);
			SwingUtilitario.invokeLater(() -> ajustar(d));
		} else if (d.contemScrollHorizontal()) {
			d.somarALargura(10);
			SwingUtilitario.invokeLater(() -> ajustar(d));
		}
	}
}