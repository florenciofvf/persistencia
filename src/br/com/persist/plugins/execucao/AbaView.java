package br.com.persist.plugins.execucao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLHandler;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelColetor;
import br.com.persist.plugins.variaveis.VariavelDialogo;

public class AbaView extends Panel implements ContainerTreeListener {
	private ContainerTree tree = new ContainerTree();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private PanelLog log = new PanelLog();
	private final Formulario formulario;
	private final File file;

	public AbaView(File file, Formulario formulario) {
		this.formulario = formulario;
		this.file = file;
		montarLayout();
		tree.adicionarOuvinte(this);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ScrollPane(tree), log);
		add(BorderLayout.CENTER, split);
	}

	void carregar(File file) {
		try {
			Handler handler = new Handler();
			if (file.exists() && file.canRead()) {
				XML.processar(file, handler);
			}
			tree.setModel(new ContainerModelo(handler.getRaiz()));
		} catch (Exception ex) {
			Util.stackTraceAndMessage(ExecucaoConstantes.PAINEL_EXECUCAO, ex, AbaView.this);
		}
		SwingUtilities.updateComponentTreeUI(this);
	}

	@Override
	public void executar(ContainerTree tree, boolean confirmar) {
		log.processar(tree.getObjetoSelecionado(), confirmar, tree);
	}

	@Override
	public void executarVar(ContainerTree tree) {
		log.processarVar(tree.getObjetoSelecionado(), tree, formulario);
	}

	@Override
	public void executarMemoria(ContainerTree tree) {
		log.processarMemoria(tree.getObjetoSelecionado(), tree);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		private Toolbar() {
			super.ini(new Nil(), BAIXAR);
		}

		@Override
		protected void baixar() {
			carregar(file);
		}
	}
}

class Handler extends XMLHandler {
	private Container selecionado;
	private Container raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (raiz == null) {
			try {
				raiz = new Container();
				raiz.lerAtributos(qName, attributes);
				selecionado = raiz;
			} catch (ExecucaoException | ArgumentoException ex) {
				throw new SAXException(ex);
			}
		} else {
			try {
				Container container = new Container();
				container.lerAtributos(qName, attributes);
				selecionado.adicionar(container);
				selecionado = container;
			} catch (ExecucaoException | ArgumentoException ex) {
				throw new SAXException(ex);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		selecionado = selecionado.getPai();
	}

	public Container getRaiz() {
		return raiz;
	}
}

class PanelLog extends Panel {
	private static final long serialVersionUID = 1L;
	private final Editor editor = new Editor();

	PanelLog() {
		add(BorderLayout.NORTH, new ToolbarPesquisa(editor));
		add(BorderLayout.CENTER, new JScrollPane(editor));
	}

	void processar(Container container, boolean confirmar, Component comp) {
		if (container == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		container.processar(sb, confirmar, comp, null);
		text(container, sb);
	}

	void processarVar(Container container, Component comp, Formulario formulario) {
		if (container == null) {
			return;
		}
		VariavelColetor coletor = new VariavelColetor();
		Frame frame = Util.getViewParentFrame(comp);
		VariavelDialogo dialogo = VariavelDialogo.criar(frame, formulario, coletor);
		Util.configSizeLocation(formulario, dialogo, comp);
		dialogo.setVisible(true);
		StringBuilder sb = new StringBuilder();
		container.processar(sb, false, comp, coletor.getLista());
		text(container, sb);
	}

	void processarMemoria(Container container, Component comp) {
		if (container == null) {
			return;
		}
		try {
			StringBuilder sb = new StringBuilder();
			List<Variavel> lista = new ArrayList<>();
			Variavel v = new Variavel("tmp", Util.getContentTransfered());
			lista.add(v);
			container.processar(sb, false, comp, lista);
			text(container, sb);
		} catch (ArgumentoException ex) {
			Util.mensagem(PanelLog.this, ex.getMessage());
		}
	}

	private void text(Container container, StringBuilder sb) {
		String chaveEditor = container.getChaveEditor();
		EditorCor editorCor = ExecucaoContainer.getEditorCor(chaveEditor);
		if (editorCor != null) {
			editorCor.processar(editor, sb);
		} else {
			editor.setText(sb.toString());
		}
	}
}