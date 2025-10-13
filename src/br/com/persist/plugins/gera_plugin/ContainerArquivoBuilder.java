package br.com.persist.plugins.gera_plugin;

import br.com.persist.abstrato.PluginArquivo;
import br.com.persist.geradores.Arquivo;
import br.com.persist.geradores.Catch;
import br.com.persist.geradores.ClassePrivada;
import br.com.persist.geradores.ClassePublica;
import br.com.persist.geradores.Container;
import br.com.persist.geradores.Else;
import br.com.persist.geradores.ElseIf;
import br.com.persist.geradores.For;
import br.com.persist.geradores.Funcao;
import br.com.persist.geradores.FuncaoDefault;
import br.com.persist.geradores.If;
import br.com.persist.geradores.Parametros;
import br.com.persist.geradores.RetornoClasseAnonima;
import br.com.persist.geradores.Try;

public class ContainerArquivoBuilder extends Builder implements PluginArquivo {
	private static final String ARQUIVO_TREE_GET_OBJETO_SELECIONADO = "Arquivo arquivo = arquivoTree.getObjetoSelecionado()";
	private static final String THIS_ARQUIVO_GET_FILE = ".this, arquivo.getFile())";
	private static final String EXCLUIR_CONTAINER = ".excluirContainer()";
	private static final String ARQUIVO_DIFF_NULL = "arquivo != null";
	private static final String UTIL_MSG = "Util.mensagem(";
	private static final String GET_STRING = ".getString(";
	private static final String DIFF_NULL = " != null";
	private static final String DOT_THIS = ".this)";
	private static final String STRING = "String";
	private static final String LABEL = "LABEL_";

	protected ContainerArquivoBuilder(Config config) {
		super("Container", "extends AbstratoContainer implements ArquivoTreeListener, PluginArquivo", config);
	}

	@Override
	void templateImport(Arquivo arquivo) {
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.BAIXAR");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO").newLine();
		arquivo.addImport("java.awt.BorderLayout");
		arquivo.addImport("java.awt.Component");
		if (config.comDialogo) {
			arquivo.addImport("java.awt.Dialog");
		}
		arquivo.addImport("java.awt.Window");
		arquivo.addImport("java.awt.event.ActionEvent");
		arquivo.addImport("java.awt.event.ActionListener");
		arquivo.addImport("java.io.File");
		arquivo.addImport("java.io.IOException");
		arquivo.addImport("java.util.LinkedHashSet");
		arquivo.addImport("java.util.Set");
		arquivo.addImport("java.util.concurrent.atomic.AtomicReference").newLine();
		arquivo.addImport("javax.swing.Icon").newLine();
		arquivo.addImport("br.com.persist.abstrato.AbstratoContainer");
		arquivo.addImport("br.com.persist.abstrato.AbstratoTitulo");
		arquivo.addImport("br.com.persist.abstrato.PluginArquivo");
		arquivo.addImport("br.com.persist.arquivo.Arquivo");
		arquivo.addImport("br.com.persist.arquivo.ArquivoModelo");
		arquivo.addImport("br.com.persist.arquivo.ArquivoPesquisa");
		arquivo.addImport("br.com.persist.arquivo.ArquivoTree");
		arquivo.addImport("br.com.persist.arquivo.ArquivoTreeListener");
		arquivo.addImport("br.com.persist.arquivo.ArquivoTreeUtil");
		arquivo.addImport("br.com.persist.assistencia.ArquivoUtil");
		arquivo.addImport("br.com.persist.assistencia.Constantes");
		arquivo.addImport("br.com.persist.assistencia.Icones");
		arquivo.addImport("br.com.persist.assistencia.Preferencias");
		arquivo.addImport("br.com.persist.assistencia.Util");
		arquivo.addImport("br.com.persist.componente.BarraButton");
		arquivo.addImport("br.com.persist.componente.Janela");
		arquivo.addImport("br.com.persist.componente.ScrollPane");
		arquivo.addImport("br.com.persist.fichario.Fichario");
		arquivo.addImport("br.com.persist.fichario.Titulo");
		arquivo.addImport("br.com.persist.formulario.Formulario").newLine();
	}

	@Override
	void templateClass(ClassePublica classe) {
		classe.addInstrucao("private final ArquivoTree arquivoTree = new ArquivoTree(new ArquivoModelo())");
		classe.addInstrucao("private static final long serialVersionUID = 1L");
		classe.addInstrucao("private final Toolbar toolbar = new Toolbar()");
		classe.addInstrucao("private " + config.nameCapFormulario() + " " + config.nameDecapFormulario());

		if (config.comDialogo) {
			classe.addInstrucao("private " + config.nameCapDialogo() + " " + config.nameDecapDialogo());
		}
		classe.newLine();
		Container construtor = null;
		construtor = classe.criarConstrutorPublico(config.nameCapContainer(),
				new Parametros("Janela janela, Formulario formulario"));
		construtor.addInstrucao("super(formulario)");
		construtor.addInstrucao("toolbar.ini(janela)");
		construtor.addInstrucao("montarLayout()");
		construtor.addInstrucao("configurar()");

		if (config.comDialogo) {
			classe.newLine();
			Funcao funcao = classe.criarFuncaoPublica(config.nameCapDialogo(), "get" + config.nameCapDialogo());
			funcao.addReturn(config.nameDecapDialogo());

			classe.newLine();
			funcao = classe.criarFuncaoPublica("void", "set" + config.nameCapDialogo(),
					new Parametros(config.nameCapDialogo() + " " + config.nameDecapDialogo()));
			funcao.addInstrucao("this." + config.nameDecapDialogo() + " = " + config.nameDecapDialogo());
			If se = funcao.criarIf(config.nameDecapDialogo() + DIFF_NULL, null);
			se.addInstrucao(config.nameDecapFormulario() + " = null");
		}

		classe.newLine();
		Funcao funcao = classe.criarFuncaoPublica(config.nameCapFormulario(), "get" + config.nameCapFormulario());
		funcao.addReturn(config.nameDecapFormulario());

		classe.newLine();
		funcao = classe.criarFuncaoPublica("void", "set" + config.nameCapFormulario(),
				new Parametros(config.nameCapFormulario() + " " + config.nameDecapFormulario()));
		funcao.addInstrucao("this." + config.nameDecapFormulario() + " = " + config.nameDecapFormulario());

		if (config.comDialogo) {
			If se = funcao.criarIf(config.nameDecapFormulario() + DIFF_NULL, null);
			se.addInstrucao(config.nameDecapDialogo() + " = null");
		}

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("void", "montarLayout");
		funcao.addInstrucao("add(BorderLayout.NORTH, toolbar)");
		funcao.addInstrucao("add(BorderLayout.CENTER, new ScrollPane(arquivoTree))");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("void", "configurar");
		funcao.addInstrucao("arquivoTree.adicionarOuvinte(this)");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "setJanela", new Parametros("Janela janela"));
		funcao.addInstrucao("toolbar.setJanela(janela)");

		templateToolbar(classe);
		finalizar(classe);
		metodos(classe);
		titulo(classe);
	}

	private void templateToolbar(ClassePublica classe) {
		classe.newLine();
		ClassePrivada classePrivada = null;

		classePrivada = classe.criarClassePrivada("Toolbar extends BarraButton implements ActionListener");
		classePrivada.addInstrucao("private static final long serialVersionUID = 1L");
		classePrivada.addInstrucao("private transient ArquivoPesquisa pesquisa").newLine();

		Funcao funcao = classePrivada.criarFuncaoPublica("void", "ini", new Parametros("Janela janela"));
		funcao.addInstrucao(
				"super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR)");
		funcao.addInstrucao("txtPesquisa.addActionListener(this)");
		funcao.addInstrucao("add(txtPesquisa)");
		funcao.addInstrucao("add(chkPorParte)");
		funcao.addInstrucao("add(chkPsqConteudo)");
		funcao.addInstrucao("add(label)");

		actionPerf(classePrivada);
		destacar(classePrivada);
		retornar(classePrivada);
		abrir(classePrivada);
		windowOpended(classePrivada);
		if (config.comDialogo) {
			dialogOpened(classePrivada);
		}
		adicionadoAoFichario(classePrivada);
		baixar(classePrivada);
	}

	private void actionPerf(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "actionPerformed", new Parametros("ActionEvent e"));

		Else elseExterno = new Else();
		elseExterno.addInstrucao("label.limpar()");
		If ifExterno = funcao.criarIf("!Util.isEmpty(txtPesquisa.getText())", elseExterno);

		Else elseInterno = new Else();
		elseInterno.addInstrucao(
				"pesquisa = getPesquisa(arquivoTree, pesquisa, txtPesquisa.getText(), chkPorParte.isSelected())");
		elseInterno.addInstrucao("pesquisa.selecionar(label)");

		If ifInterno = ifExterno.criarIf("chkPsqConteudo.isSelected()", elseInterno);
		ifInterno.addInstrucao("Set<String> set = new LinkedHashSet<>()");
		ifInterno.addInstrucao("arquivoTree.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected())");
		ifInterno.addInstrucao(UTIL_MSG + config.nameCapContainer() + ".this, getString(set))");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada(STRING, "getString", new Parametros("Set<String> set"));
		funcao.addInstrucao("StringBuilder sb = new StringBuilder()");
		For loop = funcao.criarFor("String string : set");
		ifExterno = loop.criarIf("sb.length() > 0", null);
		ifExterno.addInstrucao("sb.append(Constantes.QL)");
		loop.addInstrucao("sb.append(string)");
		funcao.addReturn("sb.toString()");

		classe.newLine();
		funcao = classe.criarFuncaoPublica("ArquivoPesquisa", "getPesquisa",
				new Parametros("ArquivoTree arquivoTree, ArquivoPesquisa pesquisa, String string, boolean porParte"));
		If if3 = funcao.criarIf("pesquisa == null", null);
		if3.addReturn("new ArquivoPesquisa(arquivoTree, string, porParte)");
		ElseIf elseIf = if3.criarElseIf("pesquisa.igual(string, porParte)");
		elseIf.addReturn("pesquisa");
		funcao.addReturn("new ArquivoPesquisa(arquivoTree, string, porParte)");
	}

	private void destacar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "destacarEmFormulario");

		If se = funcao.criarIf("formulario.excluirPagina(" + config.nameCapContainer() + DOT_THIS, null);
		se.addInstrucao(config.nameCapFormulario() + ".criar(formulario, " + config.nameCapContainer() + DOT_THIS);

		if (config.comDialogo) {
			ElseIf elseIf = se.criarElseIf(config.nameDecapDialogo() + DIFF_NULL);
			elseIf.addInstrucao(config.nameDecapDialogo() + EXCLUIR_CONTAINER);
			elseIf.addInstrucao(
					config.nameCapFormulario() + ".criar(formulario, " + config.nameCapContainer() + DOT_THIS);
		}
	}

	private void retornar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "retornarAoFichario");

		If se = funcao.criarIf(config.nameDecapFormulario() + DIFF_NULL, null);
		se.addInstrucao(config.nameDecapFormulario() + EXCLUIR_CONTAINER);
		se.addInstrucao("formulario.adicionarPagina(" + config.nameCapContainer() + DOT_THIS);

		if (config.comDialogo) {
			ElseIf elseIf = se.criarElseIf(config.nameDecapDialogo() + DIFF_NULL);
			elseIf.addInstrucao(config.nameDecapDialogo() + EXCLUIR_CONTAINER);
			elseIf.addInstrucao("formulario.adicionarPagina(" + config.nameCapContainer() + DOT_THIS);
		}
	}

	private void abrir(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "abrirEmFormulario");

		if (config.comDialogo) {
			If se = funcao.criarIf(config.nameDecapDialogo() + DIFF_NULL, null);
			se.addInstrucao(config.nameDecapDialogo() + EXCLUIR_CONTAINER);
		}

		funcao.addInstrucao(config.nameCapFormulario() + ".criar(formulario)");
	}

	private void windowOpended(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "windowOpenedHandler", new Parametros("Window window"));
		funcao.addInstrucao("buttonDestacar.estadoFormulario()");
	}

	private void dialogOpened(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "dialogOpenedHandler", new Parametros("Dialog dialog"));
		funcao.addInstrucao("buttonDestacar.estadoDialogo()");
	}

	private void adicionadoAoFichario(ClassePrivada classe) {
		classe.newLine();
		FuncaoDefault funcao = classe.criarFuncaoDefault("void", "adicionadoAoFichario");
		funcao.addInstrucao("buttonDestacar.estadoFichario()");
	}

	private void baixar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "baixar");
		funcao.addInstrucao("ArquivoModelo modelo = new ArquivoModelo()");
		funcao.addInstrucao("arquivoTree.setModel(modelo)");
		funcao.addInstrucao("pesquisa = null");
		funcao.addInstrucao("label.limpar()");
	}

	private void finalizar(ClassePublica classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "adicionadoAoFichario", new Parametros("Fichario fichario"));
		funcao.addInstrucao("toolbar.adicionadoAoFichario()");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "windowOpenedHandler", new Parametros("Window window"));
		funcao.addInstrucao("toolbar.windowOpenedHandler(window)");

		if (config.comDialogo) {
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica("void", "dialogOpenedHandler", new Parametros("Dialog dialog"));
			funcao.addInstrucao("toolbar.dialogOpenedHandler(dialog)");
		}

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica(STRING, "getStringPersistencia");
		funcao.addReturn("Constantes.VAZIO");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("Class<?>", "getClasseFabrica");
		funcao.addReturn(config.nameCapFabrica() + ".class");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("Component", "getComponent");
		funcao.addReturn("this");
	}

	private void metodos(ClassePublica classe) {
		Parametros parametros = new Parametros("ArquivoTree arquivoTree");
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "focusInputPesquisar", parametros);
		funcao.addInstrucao("toolbar.focusInputPesquisar()");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "diretorioArquivo", parametros);
		funcao.addInstrucao(ARQUIVO_TREE_GET_OBJETO_SELECIONADO);
		If se = funcao.criarIf(ARQUIVO_DIFF_NULL, null);
		se.addInstrucao("desktopOpen(arquivo)");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("void", "desktopOpen", new Parametros("Arquivo arquivo"));
		Catch catche = new Catch("IOException ex");
		catche.addInstrucao(UTIL_MSG + config.nameCapContainer() + ".this, ex.getMessage())");
		Try tre = funcao.criarTry(catche);
		tre.addInstrucao("ArquivoUtil.diretorio(arquivo.getFile())");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "renomearArquivo", parametros);
		funcao.addInstrucao(ARQUIVO_TREE_GET_OBJETO_SELECIONADO);
		se = funcao.criarIf(ARQUIVO_DIFF_NULL, null);
		se.addInstrucao("String nome = ArquivoUtil.getNome(" + config.nameCapContainer() + ".this, arquivo.getName())");
		se = se.criarIf("nome != null && arquivo.renomear(nome)", null);
		se.addInstrucao("ArquivoTreeUtil.refreshEstrutura(arquivoTree, arquivo)");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "excluirArquivo", parametros);
		funcao.addInstrucao(ARQUIVO_TREE_GET_OBJETO_SELECIONADO);
		se = funcao.criarIf(
				"arquivo != null && Util.confirmar(" + config.nameCapContainer() + ".this, \"msg.confirma_exclusao\")",
				null);
		se.addInstrucao("arquivo.excluir()");
		se.addInstrucao("ArquivoTreeUtil.excluirEstrutura(arquivoTree, arquivo)");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "novoDiretorio", parametros);
		funcao.addInstrucao(ARQUIVO_TREE_GET_OBJETO_SELECIONADO);
		se = funcao.criarIf("valido(arquivo)", null);
		se.addInstrucao("File file = ArquivoUtil.novoDiretorio(" + config.nameCapContainer() + THIS_ARQUIVO_GET_FILE);
		se.addInstrucao("adicionar(arquivoTree, arquivo, file)");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("boolean", "valido", new Parametros("Arquivo arquivo"));
		funcao.addReturn("arquivo != null && arquivo.isDirectory()");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("void", "adicionar",
				new Parametros("ArquivoTree arquivoTree, Arquivo arquivo, File file"));
		se = funcao.criarIf("file != null && arquivo != null", null);
		se.addInstrucao("Arquivo novo = arquivo.adicionar(file)");
		se = se.criarIf("novo != null", null);
		se.addInstrucao("arquivo.ordenar()");
		se.addInstrucao("ArquivoTreeUtil.atualizarEstrutura(arquivoTree, arquivo)");
		se.addInstrucao("requestFocus()");
		se.addInstrucao("ArquivoTreeUtil.selecionarObjeto(arquivoTree, novo)");
		se.addInstrucao("arquivoTree.repaint()");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "clonarArquivo", parametros);
		funcao.addInstrucao(ARQUIVO_TREE_GET_OBJETO_SELECIONADO);
		se = funcao.criarIf(ARQUIVO_DIFF_NULL, null);
		se.addInstrucao("clonar(arquivoTree, arquivo)");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("void", "clonar",
				new Parametros("ArquivoTree arquivoTree, Arquivo arquivo"));
		tre = funcao.criarTry(catche);
		tre.addInstrucao("AtomicReference<File> ref = new AtomicReference<>()");
		tre.addInstrucao("String resp = Util.clonar(" + config.nameCapContainer() + ".this, arquivo.getFile(), ref)");
		se = tre.criarIf("Preferencias.isExibirTotalBytesClonados()", null);
		se.addInstrucao(UTIL_MSG + config.nameCapContainer() + ".this, resp)");
		tre.addInstrucao("adicionar(arquivoTree, arquivo.getPai(), ref.get())");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "abrirArquivo", parametros);
		funcao.addInstrucao(ARQUIVO_TREE_GET_OBJETO_SELECIONADO);
		se = funcao.criarIf(ARQUIVO_DIFF_NULL, null);
		se.addInstrucao("Util.abrir(" + config.nameCapContainer() + THIS_ARQUIVO_GET_FILE);

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "novoArquivo", parametros);
		funcao.addInstrucao(ARQUIVO_TREE_GET_OBJETO_SELECIONADO);
		se = funcao.criarIf("valido(arquivo)", null);
		se.addInstrucao("File file = ArquivoUtil.novoArquivo(" + config.nameCapContainer() + THIS_ARQUIVO_GET_FILE);
		se.addInstrucao("adicionar(arquivoTree, arquivo, file)");
	}

	private void titulo(ClassePublica classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("Titulo", "getTitulo");
		RetornoClasseAnonima anonima = funcao.criarRetornoClasseAnonima("AbstratoTitulo");

		anonima.addOverride(false);
		funcao = anonima.criarFuncaoPublica(STRING, "getTituloMin");
		funcao.addReturn(config.nameCapMensagens() + GET_STRING + config.nameCapConstantes() + "."
				+ config.nameUpperEntre(LABEL, "_MIN") + ")");

		anonima.addOverride(true);
		funcao = anonima.criarFuncaoPublica(STRING, "getTitulo");
		funcao.addReturn(config.nameCapMensagens() + GET_STRING + config.nameCapConstantes() + "."
				+ config.nameUpperApos(LABEL) + ")");

		anonima.addOverride(true);
		funcao = anonima.criarFuncaoPublica(STRING, "getHint");
		funcao.addReturn(config.nameCapMensagens() + GET_STRING + config.nameCapConstantes() + "."
				+ config.nameUpperApos(LABEL) + ")");

		anonima.addOverride(true);
		funcao = anonima.criarFuncaoPublica("Icon", "getIcone");
		funcao.addReturn("Icones." + config.icone);
	}
}