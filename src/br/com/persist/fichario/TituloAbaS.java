package br.com.persist.fichario;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicButtonUI;

import br.com.persist.comp.Button;
import br.com.persist.comp.Panel;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class TituloAbaS extends Panel {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Fichario fichario;
	private final File arquivo;

	public TituloAbaS(Fichario fichario, File arquivo) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		Objects.requireNonNull(fichario);
		this.fichario = fichario;
		this.arquivo = arquivo;
		setOpaque(false);
		add(new Icone());
	}

	public void salvarAberto(PrintWriter pw) {
		if (arquivo != null) {
			pw.print(arquivo.getAbsolutePath() + Constantes.QL2);
		}
	}

	private class Icone extends Button implements ActionListener {
		private static final long serialVersionUID = 1L;

		Icone() {
			setToolTipText(Mensagens.getString(Constantes.LABEL_FECHAR));
			addMouseListener(TituloAba.mouseListenerInner);
			setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(17, 17));
			setContentAreaFilled(false);
			setUI(new BasicButtonUI());
			setRolloverEnabled(true);
			setBorderPainted(false);
			addActionListener(this);
			setFocusable(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int i = fichario.indexOfTabComponent(TituloAbaS.this);

			if (i != -1) {
				fichario.remove(i);
			}
		}

		@Override
		public void updateUI() {
			LOG.log(Level.FINEST, "updateUI");
		}
	}
}