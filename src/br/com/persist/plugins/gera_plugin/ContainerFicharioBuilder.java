package br.com.persist.plugins.gera_plugin;

import br.com.persist.abstrato.PluginFichario;
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

public class ContainerFicharioBuilder extends Builder implements PluginFichario {
	private static final String ATIVA_PAGINA_ATIVA = " ativa = fichario.getPaginaAtiva()";
	private static final String EXCLUIR_CONTAINER = ".excluirContainer()";
	private static final String ATIVA_DIFF_NULL = "ativa != null";
	private static final String UTIL_MSG = "Util.mensagem(";
	private static final String GET_STRING = ".getString(";
	private static final String DIFF_NULL = " != null";
	private static final String DOT_THIS = ".this)";
	private static final String STRING = "String";
	private static final String SALVAR = "salvar";
	private static final String LABEL = "LABEL_";

	protected ContainerFicharioBuilder(Config config) {
		super("Container", "extends AbstratoContainer implements PluginFichario", config);
	}

	@Override
	void templateImport(Arquivo arquivo) {
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.BAIXAR");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.NOVO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.SALVAR").newLine();
		arquivo.addImport("java.awt.BorderLayout");
		arquivo.addImport("java.awt.Component");
		if (config.comDialogo) {
			arquivo.addImport("java.awt.Dialog");
		}
		arquivo.addImport("java.awt.Window");
		arquivo.addImport("java.awt.event.ActionEvent");
		arquivo.addImport("java.awt.event.ActionListener");
		arquivo.addImport("java.util.LinkedHashSet");
		arquivo.addImport("java.util.Set");
		arquivo.addImport("java.io.File");
		arquivo.addImport("java.io.IOException");
		arquivo.addImport("java.util.ArrayList");
		arquivo.addImport("java.util.List");
		arquivo.addImport("java.util.concurrent.atomic.AtomicBoolean").newLine();
		arquivo.addImport("javax.swing.Icon").newLine();
		arquivo.addImport("br.com.persist.abstrato.AbstratoContainer");
		arquivo.addImport("br.com.persist.abstrato.AbstratoTitulo");
		arquivo.addImport("br.com.persist.abstrato.PluginFichario");
		arquivo.addImport("br.com.persist.assistencia.ArquivoUtil");
		arquivo.addImport("br.com.persist.assistencia.Constantes");
		arquivo.addImport("br.com.persist.assistencia.Icones");
		arquivo.addImport("br.com.persist.assistencia.Mensagens");
		arquivo.addImport("br.com.persist.assistencia.Util");
		arquivo.addImport("br.com.persist.componente.Action");
		arquivo.addImport("br.com.persist.componente.BarraButton");
		arquivo.addImport("br.com.persist.componente.FicharioPesquisa");
		arquivo.addImport("br.com.persist.componente.Janela");
		arquivo.addImport("br.com.persist.fichario.Fichario");
		arquivo.addImport("br.com.persist.fichario.Titulo");
		arquivo.addImport("br.com.persist.formulario.Formulario").newLine();
	}

	@Override
	void templateClass(ClassePublica classe) {
		classe.addInstrucao(
				"private static final File file = new File(" + config.nameCapConstantes() + "." + config.recurso + ")");
		classe.addInstrucao("private static final long serialVersionUID = 1L");
		classe.addInstrucao("private final Toolbar toolbar = new Toolbar()");
		classe.addInstrucao("private " + config.nameCapFormulario() + " " + config.nameDecapFormulario());
		classe.addInstrucao("private final " + config.nameCapFichario() + " fichario");
		if (config.comDialogo) {
			classe.addInstrucao("private " + config.nameCapDialogo() + " " + config.nameDecapDialogo());
		}
		classe.newLine();
		Container construtor = null;
		construtor = classe.criarConstrutorPublico(config.nameCapContainer(),
				new Parametros("Janela janela, Formulario formulario, String conteudo, String idPagina"));
		construtor.addInstrucao("super(formulario)");
		construtor.addInstrucao("fichario = new " + config.nameCapFichario() + "(this)");

		construtor.addInstrucao("toolbar.ini(janela)");
		construtor.addInstrucao("montarLayout()");

		construtor.addInstrucao("abrir(conteudo, idPagina)");

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
		funcao.addInstrucao("add(BorderLayout.CENTER, fichario)");
		funcao.addInstrucao("fichario.setListener(e -> toolbar.focusInputPesquisar())");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "setJanela", new Parametros("Janela janela"));
		funcao.addInstrucao("toolbar.setJanela(janela)");

		templateFichario(classe);
		templateToolbar(classe);
		finalizar(classe);
		titulo(classe);
	}

	private void templateFichario(ClassePublica classe) {
		classe.newLine();
		Funcao funcao = classe.criarFuncaoPublica(STRING, "getConteudo");
		funcao.addInstrucao(config.nameCapPagina() + ATIVA_PAGINA_ATIVA);
		If se = funcao.criarIf(ATIVA_DIFF_NULL, null);
		se.addInstrucao("return ativa.getConteudo()");
		funcao.addReturn("null");

		classe.newLine();
		funcao = classe.criarFuncaoPublica(STRING, "getIdPagina");
		funcao.addInstrucao(config.nameCapPagina() + ATIVA_PAGINA_ATIVA);
		If se2 = funcao.criarIf(ATIVA_DIFF_NULL, null);
		se2.addReturn("ativa.getNome()");
		funcao.addReturn("null");

		classe.newLine();
		funcao = classe.criarFuncaoPublica("void", SALVAR);
		funcao.addInstrucao("toolbar.salvar()");

		classe.newLine();
		funcao = classe.criarFuncaoPublica("int", "getIndice");
		funcao.addReturn("fichario.getIndiceAtivo()");

		classe.newLine();
		FuncaoDefault funcaoDefault = classe.criarFuncaoDefault("static boolean", "ehArquivoReservado",
				new Parametros("String nome"));
		funcaoDefault.addReturn(config.nameCapConstantes() + ".IGNORADOS.equalsIgnoreCase(nome)");

		templateAbrir(classe);
	}

	private void templateAbrir(ClassePublica classe) {
		classe.newLine();
		Funcao funcao = classe.criarFuncaoPrivada("void", "abrir", new Parametros("String conteudo, String idPagina"));
		funcao.addInstrucao("ArquivoUtil.lerArquivo(" + config.nameCapConstantes() + "." + config.recurso
				+ ", new File(file, " + config.nameCapConstantes() + ".IGNORADOS))");
		funcao.addInstrucao("fichario.excluirPaginas()");

		If se1 = funcao.criarIf("file.isDirectory()", null);
		se1.addInstrucao("File[] files = file.listFiles()");

		If se2 = se1.criarIf("files != null", null);
		se2.addInstrucao("files = ArquivoUtil.ordenar(files)");
		se2.addInstrucao("List<" + config.nameCapPagina() + "> ordenados = new ArrayList<>()");

		For loop1 = se2.criarFor("File f : files");

		If se3 = loop1.criarIf("(ehArquivoReservado(f.getName()) && !" + config.nameCap
				+ "Preferencia.isExibirArqIgnorados()) || ArquivoUtil.contem(" + config.nameCapConstantes() + "."
				+ config.recurso + ", f.getName())", null);
		se3.addInstrucao("continue");
		loop1.addInstrucao("ordenados.add(new " + config.nameCapPagina() + "(fichario, f))");

		For loop2 = se2.criarFor(config.nameCapPagina() + " pagina : ordenados");
		loop2.addInstrucao("fichario.adicionarPagina(pagina)");

		funcao.addInstrucao("fichario.setConteudo(conteudo, idPagina)");
	}

	private void templateToolbar(ClassePublica classe) {
		classe.newLine();
		ClassePrivada classePrivada = null;

		classePrivada = classe.criarClassePrivada("Toolbar extends BarraButton implements ActionListener");
		classePrivada.addInstrucao("private Action excluirAtivoAcao = actionIconExcluir()");
		classePrivada.addInstrucao("private static final long serialVersionUID = 1L");
		classePrivada.addInstrucao("private transient FicharioPesquisa pesquisa").newLine();

		Funcao funcao = classePrivada.criarFuncaoPublica("void", "ini", new Parametros("Janela janela"));
		funcao.addInstrucao(
				"super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR)");
		funcao.addInstrucao("addButton(excluirAtivoAcao)");
		funcao.addInstrucao("add(txtPesquisa)");
		funcao.addInstrucao("add(chkPorParte)");
		funcao.addInstrucao("chkPsqConteudo.setTag(Constantes.FICHARIO)");
		funcao.addInstrucao("add(chkPsqConteudo)");
		funcao.addInstrucao("add(label)");
		funcao.addInstrucao("excluirAtivoAcao.setActionListener(e -> excluirAtivo())");
		funcao.addInstrucao("txtPesquisa.addActionListener(this)");

		contemConteudo(classePrivada);
		destacar(classePrivada);
		retornar(classePrivada);
		clonar(classePrivada);
		abrir(classePrivada);
		windowOpended(classePrivada);
		if (config.comDialogo) {
			dialogOpened(classePrivada);
		}
		adicionadoAoFichario(classePrivada);
		novo(classePrivada);
		baixar(classePrivada);
		salvar(classePrivada);
		excluir(classePrivada);
	}

	private void contemConteudo(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "actionPerformed", new Parametros("ActionEvent e"));

		Else elseExterno = new Else();
		elseExterno.addInstrucao("label.limpar()");
		If ifExterno = funcao.criarIf("!Util.isEmpty(txtPesquisa.getText())", elseExterno);

		Else elseInterno = new Else();
		elseInterno.addInstrucao(
				"pesquisa = fichario.getPesquisa(pesquisa, txtPesquisa.getText(), chkPorParte.isSelected())");
		elseInterno.addInstrucao("pesquisa.selecionar(label)");

		If ifInterno = ifExterno.criarIf("chkPsqConteudo.isSelected()", elseInterno);
		ifInterno.addInstrucao("Set<String> set = new LinkedHashSet<>()");
		ifInterno.addInstrucao("fichario.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected())");
		ifInterno.addInstrucao(UTIL_MSG + config.nameCapContainer() + ".this, Util.getString(set))");
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

	private void clonar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "clonarEmFormulario");

		if (config.comDialogo) {
			If se = funcao.criarIf(config.nameDecapDialogo() + DIFF_NULL, null);
			se.addInstrucao(config.nameDecapDialogo() + EXCLUIR_CONTAINER);
		}

		funcao.addInstrucao(config.nameCapFormulario() + ".criar(formulario, getConteudo(), getIdPagina())");
	}

	private void abrir(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "abrirEmFormulario");

		if (config.comDialogo) {
			If se = funcao.criarIf(config.nameDecapDialogo() + DIFF_NULL, null);
			se.addInstrucao(config.nameDecapDialogo() + EXCLUIR_CONTAINER);
		}

		funcao.addInstrucao(config.nameCapFormulario() + ".criar(formulario, null, null)");
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

	private void novo(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "novo");
		funcao.addInstrucao("Object resp = Util.getValorInputDialog(" + config.nameCapContainer()
				+ ".this, \"label.id\", Mensagens.getString(\"label.nome_arquivo\"), Constantes.VAZIO)");

		If se = funcao.criarIf("resp == null || Util.isEmpty(resp.toString())", null);
		se.addReturn();

		funcao.addInstrucao("String nome = resp.toString()");
		se = funcao.criarIf("ehArquivoReservado(nome)", null);
		se.addInstrucao(UTIL_MSG + config.nameCapContainer()
				+ ".this, Mensagens.getString(\"label.indentificador_reservado\"))");
		se.addReturn();
		funcao.newLine();

		funcao.addInstrucao("File f = new File(file, nome)");
		se = funcao.criarIf("f.exists()", null);
		se.addInstrucao(UTIL_MSG + config.nameCapContainer()
				+ ".this, Mensagens.getString(\"label.indentificador_ja_existente\"))");
		se.addReturn();

		Catch catche = new Catch("IOException ex");
		catche.addInstrucao("Util.stackTraceAndMessage(" + config.nameCapConstantes() + ".PAINEL_" + config.nameUpper
				+ ", ex, " + config.nameCapContainer() + DOT_THIS);

		Try tre = funcao.criarTry(catche);
		se = tre.criarIf("f.createNewFile()", null);
		se.addInstrucao(config.nameCapPagina() + " pagina = new " + config.nameCapPagina() + "(fichario, f)");
		se.addInstrucao("fichario.adicionarPagina(pagina)");
	}

	private void baixar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "baixar");
		funcao.addInstrucao("abrir(null, getIdPagina())");
	}

	private void salvar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", SALVAR);
		funcao.addInstrucao(config.nameCapPagina() + ATIVA_PAGINA_ATIVA);

		If se = funcao.criarIf(ATIVA_DIFF_NULL, null);
		se.addInstrucao("salvar(ativa)");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("void", SALVAR, new Parametros(config.nameCapPagina() + " ativa"));
		funcao.addInstrucao("AtomicBoolean atomic = new AtomicBoolean(false)");
		funcao.addInstrucao("ativa.salvar(atomic)");
		se = funcao.criarIf("atomic.get()", null);
		se.addInstrucao("salvoMensagem()");
	}

	private void excluir(ClassePrivada classe) {
		classe.newLine();
		Funcao funcao = classe.criarFuncaoPrivada("void", "excluirAtivo");
		funcao.addInstrucao(config.nameCapPagina() + ATIVA_PAGINA_ATIVA);

		If se = funcao.criarIf("ativa != null && Util.confirmar(" + config.nameCapContainer() + ".this, "
				+ config.nameCap + "Mensagens.getString(\"msg.confirmar_excluir_ativa\"), false)", null);
		se.addInstrucao("int indice = fichario.getSelectedIndex()");
		se.addInstrucao("ativa.excluir()");
		se.addInstrucao("fichario.remove(indice)");
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
		funcao.addInstrucao(config.nameCapPagina() + ATIVA_PAGINA_ATIVA);
		If se = funcao.criarIf(ATIVA_DIFF_NULL, null);
		se.addReturn("ativa.getNome()");
		funcao.addReturn("Constantes.VAZIO");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("Class<?>", "getClasseFabrica");
		funcao.addReturn(config.nameCapFabrica() + ".class");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("Component", "getComponent");
		funcao.addReturn("this");
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