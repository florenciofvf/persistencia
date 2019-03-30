package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import br.com.persist.principal.Formulario;
import br.com.persist.util.Macro;

public class MacroDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private JList<Macro.Instrucao> lista;

	public MacroDialogo(Frame frame) {
		super(frame, "Macro", 0, 0, false);
		lista = new JList<>(new Modelo());
		montarLayout();
		pack();
		setLocationRelativeTo(frame);
		configurar();
		setVisible(true);
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

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				lista.setSelectedIndex(0);
			}
		});
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
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
		}
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, lista);
	}

	protected void processar() {
	}
}