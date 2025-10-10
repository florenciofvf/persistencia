package br.com.persist.plugins.objeto.macro;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JList;

import br.com.persist.abstrato.PluginBasico;
import br.com.persist.componente.Panel;

public class MacroContainer extends Panel implements PluginBasico {
	private static final long serialVersionUID = 1L;
	private JList<MacroProvedor.Instrucao> lista;

	public MacroContainer() {
		lista = new JList<>(new MacroModelo());
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
						MacroProvedor.excluir(indice);
						lista.setModel(new MacroModelo());
						if (!MacroProvedor.isEmpty()) {
							lista.setSelectedIndex(0);
						}
					}
				}
			}
		});
	}

	public void selecionar(int indice) {
		lista.setSelectedIndex(indice);
	}
}