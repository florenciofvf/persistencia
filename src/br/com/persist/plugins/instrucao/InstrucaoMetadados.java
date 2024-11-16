package br.com.persist.plugins.instrucao;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;

public class InstrucaoMetadados {
	private InstrucaoMetadados() {
	}

	public static void abrir(Component comp, String biblioteca) {
		MetaProvedor.init(biblioteca);
		MetaDialogo.criar(Util.getViewParentFrame(comp));
	}
}

class MetaContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private JList<String> lista;

	public MetaContainer() {
		lista = new JList<>(new MetaModelo());
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
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					int indice = lista.getSelectedIndex();
					if (indice != -1) {
						//
					}
				}
			}
		});
	}

	public void selecionar(int indice) {
		lista.setSelectedIndex(indice);
	}
}

class MetaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MetaContainer container;

	private MetaDialogo(Frame frame) {
		super(frame, "Metadados");
		container = new MetaContainer();
		montarLayout();
		pack();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static MetaDialogo criar(Frame frame) {
		MetaDialogo form = new MetaDialogo(frame);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.selecionar(0);
	}
}

class MetaModelo implements ListModel<String> {
	private static final Logger LOG = Logger.getGlobal();

	@Override
	public int getSize() {
		return MetaProvedor.getSize();
	}

	@Override
	public String getElementAt(int index) {
		return MetaProvedor.get(index);
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

class MetaProvedor {
	private static final List<String> lista = new ArrayList<>();

	private MetaProvedor() {
	}

	public static void init(String biblioteca) {
		lista.clear();
	}

	public static List<String> getLista() {
		return lista;
	}

	public static String get(int i) {
		return lista.get(i);
	}

	public static int getSize() {
		return lista.size();
	}
}