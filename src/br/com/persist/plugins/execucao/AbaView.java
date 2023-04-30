package br.com.persist.plugins.execucao;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLHandler;
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

	static Action acaoMenu(String chave, Icon icon) {
		return Action.acaoMenu(ExecucaoMensagens.getString(chave), icon);
	}

	static Action acaoMenu(String chave) {
		return acaoMenu(chave, null);
	}
}

class Handler extends XMLHandler {
	private Container selecionado;
	private Container raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (raiz == null) {
			raiz = new Container();
			raiz.lerAtributos(attributes);
			selecionado = raiz;
		} else {
			Container container = new Container();
			container.lerAtributos(attributes);
			selecionado.adicionar(container);
			selecionado = container;
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
	private final TextArea textArea = new TextArea();
	private static final long serialVersionUID = 1L;

	PanelLog() {
		add(BorderLayout.NORTH, new ToolbarPesquisa(textArea));
		add(BorderLayout.CENTER, new JScrollPane(textArea));
	}

	void processar(Container container, boolean confirmar, Component comp) {
		if (container == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		container.processar(sb, confirmar, comp, null);
		textArea.setText(sb.toString());
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
		textArea.setText(sb.toString());
	}
}