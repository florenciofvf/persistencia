package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import br.com.persist.comp.Panel;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Macro;

public class MacroContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getGlobal();
	private JList<Macro.Instrucao> lista;

	public MacroContainer() {
		lista = new JList<>(new Modelo());
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, lista);
	}

	private void configurar() {
		lista.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D) {
					int indice = lista.getSelectedIndex();

					if (indice != -1) {
						Formulario.macro.excluir(indice);
						lista.setModel(new Modelo());

						if (!Formulario.macro.isEmpty()) {
							lista.setSelectedIndex(0);
						}
					}
				}
			}
		});
	}

	public JList<Macro.Instrucao> getLista() {
		return lista;
	}

	private class Modelo implements ListModel<Macro.Instrucao> {
		@Override
		public int getSize() {
			return Formulario.macro.getInstrucoes().size();
		}

		@Override
		public br.com.persist.util.Macro.Instrucao getElementAt(int index) {
			return Formulario.macro.getInstrucoes().get(index);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			LOG.log(Level.FINEST, "addListDataListener");
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			LOG.log(Level.FINEST, "removeListDataListener");
		}
	}
}