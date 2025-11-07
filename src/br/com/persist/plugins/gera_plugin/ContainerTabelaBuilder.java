package br.com.persist.plugins.gera_plugin;

import br.com.persist.abstrato.PluginTabela;
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

public class ContainerTabelaBuilder extends Builder implements PluginTabela {
	private static final String FIRE_TABLE_DATA_CHANGED = ".fireTableDataChanged()";
	private static final String EXCLUIR_CONTAINER = ".excluirContainer()";
	private static final String AJUSTAR_TABELA = "ajustarTabela()";
	private static final String UTIL_MSG = "Util.mensagem(";
	private static final String GET_STRING = ".getString(";
	private static final String DIFF_NULL = " != null";
	private static final String DOT_THIS = ".this)";
	private static final String STRING = "String";
	private static final String MODELO = "Modelo";
	private static final String LABEL = "LABEL_";

	protected ContainerTabelaBuilder(Config config) {
		super("Container", "extends AbstratoContainer implements PluginTabela", config);
	}

	@Override
	void templateImport(Arquivo arquivo) {
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.BAIXAR");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.COPIAR");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO");
		arquivo.addImport("static br.com.persist.componente.BarraButtonEnum.EXCLUIR");
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
		arquivo.addImport("java.util.logging.Level");
		arquivo.addImport("java.util.logging.Logger").newLine();
		arquivo.addImport("javax.swing.Icon");
		arquivo.addImport("javax.swing.JTable").newLine();
		arquivo.addImport("br.com.persist.abstrato.AbstratoContainer");
		arquivo.addImport("br.com.persist.abstrato.AbstratoTitulo");
		arquivo.addImport("br.com.persist.abstrato.PluginTabela");
		arquivo.addImport("br.com.persist.assistencia.ArgumentoException");
		arquivo.addImport("br.com.persist.assistencia.CellRenderer");
		arquivo.addImport("br.com.persist.assistencia.Constantes");
		arquivo.addImport("br.com.persist.assistencia.Icones");
		arquivo.addImport("br.com.persist.assistencia.Mensagens");
		arquivo.addImport("br.com.persist.assistencia.TabelaPesquisa");
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
		classe.addInstrucao("private final " + config.nameCapModelo() + " " + config.nameDecap + "Modelo = new "
				+ config.nameCapModelo() + "()");
		classe.addInstrucao("private final JTable tabela = new JTable(" + config.nameDecap + "Modelo)");
		classe.addInstrucao("private static final Logger LOG = Logger.getGlobal()");
		classe.addInstrucao("private " + config.nameCapFormulario() + " " + config.nameDecapFormulario());
		classe.addInstrucao("private static final long serialVersionUID = 1L");
		classe.addInstrucao("private final Toolbar toolbar = new Toolbar()");

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
		funcao.addInstrucao("add(BorderLayout.CENTER, new ScrollPane(tabela))");

		classe.newLine();
		funcao = classe.criarFuncaoPrivada("void", "configurar");
		funcao.addInstrucao("tabela.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer())");
		funcao.addInstrucao("tabela.getColumnModel().getColumn(1).setCellEditor(new " + config.nameCap + "Editor())");
		funcao.addInstrucao("tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)");
		funcao.addInstrucao("toolbar.baixar()");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "setJanela", new Parametros("Janela janela"));
		funcao.addInstrucao("toolbar.setJanela(janela)");

		templateToolbar(classe);
		finalizar(classe);
		titulo(classe);
		ajustarTabela(classe);
	}

	private void templateToolbar(ClassePublica classe) {
		classe.newLine();
		ClassePrivada classePrivada = null;

		classePrivada = classe.criarClassePrivada("Toolbar extends BarraButton implements ActionListener");
		classePrivada.addInstrucao("private static final long serialVersionUID = 1L");
		classePrivada.addInstrucao("private transient TabelaPesquisa pesquisa").newLine();

		Funcao funcao = classePrivada.criarFuncaoPublica("void", "ini", new Parametros("Janela janela"));
		funcao.addInstrucao(
				"super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR, EXCLUIR, COPIAR)");
		funcao.addInstrucao("txtPesquisa.addActionListener(this)");
		funcao.addInstrucao("add(txtPesquisa)");
		funcao.addInstrucao("add(chkPorParte)");
		funcao.addInstrucao("chkPsqConteudo.setTag(Constantes.TABELA)");
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
		novo(classePrivada);
		baixar(classePrivada);
		salvar(classePrivada);
		copiar(classePrivada);
		excluir(classePrivada);
	}

	private void actionPerf(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "actionPerformed", new Parametros("ActionEvent e"));

		Else elseExterno = new Else();
		elseExterno.addInstrucao("label.limpar()");
		If ifExterno = funcao.criarIf("!Util.isEmpty(txtPesquisa.getText())", elseExterno);

		Else elseInterno = new Else();
		elseInterno.addInstrucao(
				"pesquisa = Util.getTabelaPesquisa(tabela, pesquisa, 0, txtPesquisa.getText(), chkPorParte.isSelected())");
		elseInterno.addInstrucao("pesquisa.selecionar(label)");

		If ifInterno = ifExterno.criarIf("chkPsqConteudo.isSelected()", elseInterno);
		ifInterno.addInstrucao("Set<String> set = new LinkedHashSet<>()");
		ifInterno.addInstrucao(
				config.nameDecap + "Modelo.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected())");
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

	private void novo(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "novo");
		funcao.addInstrucao("String nome = getValor(Constantes.VAZIO)");

		If se = funcao.criarIf("nome != null", null);

		Catch catche = new Catch("ArgumentoException ex");
		catche.addInstrucao(UTIL_MSG + config.nameCapContainer() + ".this, ex.getMessage())");

		Try tre = se.criarTry(catche);
		tre.addInstrucao("adicionar(new " + config.nameCap + "(nome))");

		getValor(classe);
		adicionar(classe);
	}

	private void getValor(ClassePrivada classe) {
		Funcao funcao = classe.criarFuncaoPrivada(STRING, "getValor", new Parametros("String padrao"));
		funcao.addInstrucao("Object resp = Util.getValorInputDialog(" + config.nameCapContainer()
				+ ".this, \"label.id\", " + config.nameCapMensagens() + ".getString(\"label.nome\"), padrao)");
		If se = funcao.criarIf("resp == null || Util.isEmpty(resp.toString())", null);
		se.addReturn("null");
		funcao.addReturn("resp.toString()");
	}

	private void adicionar(ClassePrivada classe) {
		Funcao funcao = classe.criarFuncaoPrivada("void", "adicionar", new Parametros(config.declaracao()));
		If se = funcao.criarIf(config.nameCapProvedor() + ".contem(" + config.nameDecap + ")", null);
		se.addInstrucao(UTIL_MSG + config.nameCapContainer()
				+ ".this, Mensagens.getString(\"label.indentificador_ja_existente\") + \" \" + " + config.nameDecap
				+ ".getNome())");
		se.addReturn();

		funcao.addInstrucao(config.nameCapProvedor() + ".adicionar(" + config.nameDecap + ")");
		funcao.addInstrucao(config.nameDecap + MODELO + FIRE_TABLE_DATA_CHANGED);
		funcao.addInstrucao(AJUSTAR_TABELA);
	}

	private void baixar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "baixar");
		funcao.addInstrucao(config.nameCapProvedor() + ".inicializar()");
		funcao.addInstrucao(config.nameDecap + MODELO + FIRE_TABLE_DATA_CHANGED);
		funcao.addInstrucao(AJUSTAR_TABELA);
	}

	private void salvar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "salvar");
		Catch catche = new Catch("Exception ex");
		catche.addInstrucao("LOG.log(Level.SEVERE, Constantes.ERRO, ex)");

		Try tre = funcao.criarTry(catche);
		tre.addInstrucao(config.nameCapProvedor() + ".salvar()");
		tre.addInstrucao("salvoMensagem()");
	}

	private void copiar(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "copiar");
		funcao.addInstrucao("int[] linhas = tabela.getSelectedRows()");
		If se = funcao.criarIf("linhas != null", null);
		For fro = se.criarFor("int i : linhas");
		fro.addInstrucao(config.nameCap + " item = " + config.nameCapProvedor() + ".get" + config.nameCap + "(i)");
		fro.addInstrucao("String nome = getValor(item.getNome())");

		If se2 = fro.criarIf("nome != null", null);

		Catch catche = new Catch("ArgumentoException ex");
		catche.addInstrucao(UTIL_MSG + config.nameCapContainer() + ".this, ex.getMessage())");

		Try tre = se2.criarTry(catche);
		tre.addInstrucao("adicionar(item.clonar(nome))");
	}

	private void excluir(ClassePrivada classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoProtegida("void", "excluir");
		funcao.addInstrucao("int[] linhas = tabela.getSelectedRows()");

		If se = funcao.criarIf("linhas != null && linhas.length > 0 && Util.confirmaExclusao("
				+ config.nameCapContainer() + ".this, false)", null);
		se.addInstrucao(config.nameCapProvedor() + ".excluir(linhas)");
		se.addInstrucao(config.nameDecap + MODELO + FIRE_TABLE_DATA_CHANGED);
	}

	private void finalizar(ClassePublica classe) {
		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("void", "adicionadoAoFichario", new Parametros("Fichario fichario"));
		funcao.addInstrucao("toolbar.adicionadoAoFichario()");
		funcao.addInstrucao(AJUSTAR_TABELA);

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "windowOpenedHandler", new Parametros("Window window"));
		funcao.addInstrucao("toolbar.windowOpenedHandler(window)");
		funcao.addInstrucao(AJUSTAR_TABELA);

		if (config.comDialogo) {
			classe.addOverride(true);
			funcao = classe.criarFuncaoPublica("void", "dialogOpenedHandler", new Parametros("Dialog dialog"));
			funcao.addInstrucao("toolbar.dialogOpenedHandler(dialog)");
			funcao.addInstrucao(AJUSTAR_TABELA);
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

	private void ajustarTabela(ClassePublica classe) {
		classe.newLine();
		Funcao funcao = classe.criarFuncaoPrivada("void", "ajustarTabela");
		funcao.addInstrucao("Util.ajustar(tabela, getGraphics())");
	}
}