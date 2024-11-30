package br.com.persist.plugins.instrucao;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.CacheBiblioteca;

public class InstrucaoMetadados {
	private InstrucaoMetadados() {
	}

	public static void abrir(Component comp, String biblioteca, MetaDialogoListener listener, Point location)
			throws InstrucaoException {
		CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
		Biblioteca biblio = cacheBiblioteca.getBiblioteca(biblioteca);
		MetaProvedor.init(biblio);
		MetaDialogo.criar(Util.getViewParentFrame(comp), listener, location);
	}
}

interface MetaListener {
	void setFragmento(String string);

	void dispose();
}

class MetaContainer extends Panel {
	private final transient MetaListener metaListener;
	private static final long serialVersionUID = 1L;
	private JList<String> lista;

	public MetaContainer(MetaListener metaListener) {
		this.metaListener = Objects.requireNonNull(metaListener);
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
					processar();
				}
			}
		});
		lista.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= Constantes.DOIS) {
					processar();
				}
			}
		});
	}

	private void processar() {
		int indice = lista.getSelectedIndex();
		if (indice != -1) {
			String fragmento = lista.getSelectedValue();
			metaListener.setFragmento(fragmento);
			metaListener.dispose();
		}
	}

	public void selecionar(int indice) {
		if (indice >= 0 && indice < lista.getModel().getSize()) {
			lista.setSelectedIndex(indice);
		}
	}
}

interface MetaDialogoListener {
	void setFragmento(String string);
}

class MetaDialogo extends AbstratoDialogo implements MetaListener {
	private final transient MetaDialogoListener listener;
	private static final long serialVersionUID = 1L;
	private final MetaContainer container;

	private MetaDialogo(Frame frame, MetaDialogoListener listener) {
		super(frame, "Metadados");
		this.listener = Objects.requireNonNull(listener);
		container = new MetaContainer(this);
		montarLayout();
		pack();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static MetaDialogo criar(Frame frame, MetaDialogoListener listener, Point location) {
		MetaDialogo form = new MetaDialogo(frame, listener);
		form.setLocation(location);
		form.setVisible(true);
		return form;
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.selecionar(0);
	}

	@Override
	public void setFragmento(String string) {
		listener.setFragmento(string);
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

	public static void init(Biblioteca biblioteca) {
		List<String> list = new ArrayList<>();
		list.addAll(biblioteca.getNomeConstantes());
		list.addAll(biblioteca.getNomeFuncoes());
		lista.clear();
		lista.addAll(list);
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