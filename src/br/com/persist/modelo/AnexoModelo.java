package br.com.persist.modelo;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.persist.Arquivo;
import br.com.persist.anexo.Anexo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Imagens;

public class AnexoModelo implements TreeModel {
	private final EventListenerList listenerList = new EventListenerList();
	private static final Map<String, Arquivo> arquivos = new HashMap<>();
	private static final File anexosRaiz = new File("anexos");
	public static final File anexosInfo = new File(anexosRaiz, "A");
	private static final Logger LOG = Logger.getGlobal();
	private final Arquivo raiz;

	public AnexoModelo(boolean anexos) {
		this(new Arquivo(anexosRaiz), anexos);
	}

	public AnexoModelo(Arquivo raiz, boolean anexos) {
		Objects.requireNonNull(raiz);
		this.raiz = raiz;
		inicializar(anexos);
		raiz.inflar(anexos, new StringBuilder());
	}

	public void abrirVisivel(Anexo anexo) {
		raiz.abrirVisivel(anexo);
	}

	private void inicializar(boolean anexos) {
		if (!anexos) {
			return;
		}

		arquivos.clear();

		if (anexosInfo.isFile()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(anexosInfo), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				Arquivo sel = null;

				while (linha != null) {
					if (linha.startsWith(Constantes.SEP)) {
						sel = new Arquivo(new File(linha));
						arquivos.put(linha, sel);

					} else {
						configurar(sel, linha);
					}

					linha = br.readLine();
				}
			} catch (Exception e) {
				LOG.log(Level.FINEST, "AnexoModelo.inicializar");
			}
		}
	}

	private void configurar(Arquivo sel, String linha) {
		if (sel == null || linha == null) {
			return;
		}

		if (linha.startsWith(Constantes.ICONE)) {
			String nome = linha.substring(Constantes.ICONE.length());
			Icon icone = Imagens.getIcon(nome);
			sel.setIcone(icone, nome);

		} else if (linha.startsWith(Constantes.ABRIR_VISIVEL)) {
			String abrirVisivel = linha.substring(Constantes.ABRIR_VISIVEL.length());
			sel.setAbrirVisivel(Boolean.parseBoolean(abrirVisivel));

		} else if (linha.startsWith(Constantes.PADRAO_ABRIR)) {
			String padraoAbrir = linha.substring(Constantes.PADRAO_ABRIR.length());
			sel.setPadraoAbrir(Boolean.parseBoolean(padraoAbrir));

		} else if (linha.startsWith(Constantes.COR_FONTE)) {
			String cor = linha.substring(Constantes.COR_FONTE.length());
			sel.setCorFonte(new Color(Integer.parseInt(cor)));
		}
	}

	public static Map<String, Arquivo> getArquivos() {
		return arquivos;
	}

	public static void putArquivo(Arquivo arquivo) {
		if (arquivo != null) {
			arquivos.put(arquivo.criarChave(new StringBuilder()).toString(), arquivo);
		}
	}

	public void listar(List<Arquivo> lista) {
		raiz.listar(lista);
	}

	@Override
	public Object getRoot() {
		return raiz;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((Arquivo) parent).getArquivo(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((Arquivo) parent).getTotal();
	}

	@Override
	public boolean isLeaf(Object parent) {
		return ((Arquivo) parent).estaVazio();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((Arquivo) parent).getIndice((Arquivo) child);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		LOG.log(Level.FINEST, "valueForPathChanged");
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	public void treeStructureChanged(TreeModelEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(event);
			}
		}
	}

	public void treeNodesRemoved(TreeModelEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(event);
			}
		}
	}

	public void treeNodesChanged(TreeModelEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(event);
			}
		}
	}
}