package br.com.persist.plugins.anexo;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Imagens;

public class AnexoModelo implements TreeModel {
	private static final File anexosRaiz = new File(AnexoConstantes.ANEXOS);
	private final EventListenerList listenerList = new EventListenerList();
	private static final Map<String, Anexo> anexos = new HashMap<>();
	public static final File anexosInfo = new File(anexosRaiz, "A");
	private static final Logger LOG = Logger.getGlobal();
	private final Anexo raiz;

	public AnexoModelo() {
		this(new Anexo(anexosRaiz));
	}

	public AnexoModelo(Anexo raiz) {
		this.raiz = Objects.requireNonNull(raiz);
		inicializar();
		raiz.inflar(new StringBuilder());
	}

	public void abrirVisivel(AnexoTree anexo) {
		raiz.abrirVisivel(anexo);
	}

	private void inicializar() {
		anexos.clear();
		iniIgnorados();
		if (anexosInfo.isFile()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(anexosInfo), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				Anexo selecionado = null;
				while (linha != null) {
					if (linha.startsWith(Constantes.SEP)) {
						selecionado = new Anexo(new File(linha));
						anexos.put(linha, selecionado);
					} else {
						configurar(selecionado, linha);
					}
					linha = br.readLine();
				}
			} catch (Exception e) {
				LOG.log(Level.FINEST, "AnexoModelo.inicializar");
			}
		}
	}

	private void iniIgnorados() {
		ArquivoUtil.lerArquivo(AnexoConstantes.ANEXOS, new File(anexosRaiz, AnexoConstantes.IGNORADOS));
	}

	private void configurar(Anexo selecionado, String linha) throws AssistenciaException {
		if (selecionado == null || linha == null) {
			return;
		}
		if (linha.startsWith(Constantes.ICONE)) {
			String nome = linha.substring(Constantes.ICONE.length());
			Icon icone = Imagens.getIcon(nome);
			selecionado.setIcone(icone, nome);
		} else if (linha.startsWith(AnexoConstantes.ABRIR_VISIVEL)) {
			String abrirVisivel = linha.substring(AnexoConstantes.ABRIR_VISIVEL.length());
			selecionado.setAbrirVisivel(Boolean.parseBoolean(abrirVisivel));
		} else if (linha.startsWith(AnexoConstantes.PADRAO_ABRIR)) {
			String padraoAbrir = linha.substring(AnexoConstantes.PADRAO_ABRIR.length());
			selecionado.setPadraoAbrir(Boolean.parseBoolean(padraoAbrir));
		} else if (linha.startsWith(Constantes.COR_FONTE)) {
			String cor = linha.substring(Constantes.COR_FONTE.length());
			selecionado.setCorFonte(new Color(Integer.parseInt(cor)));
		}
	}

	public static Map<String, Anexo> getAnexos() {
		return anexos;
	}

	public static void putAnexo(Anexo anexo) {
		if (anexo != null) {
			anexos.put(anexo.criarChave(new StringBuilder()).toString(), anexo);
		}
	}

	@Override
	public Object getRoot() {
		return raiz;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((Anexo) parent).getAnexo(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((Anexo) parent).getTotal();
	}

	@Override
	public boolean isLeaf(Object parent) {
		return ((Anexo) parent).estaVazio();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((Anexo) parent).getIndice((Anexo) child);
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