package br.com.persist.plugins.gera_plugin;

import br.com.persist.geradores.Arquivo;
import br.com.persist.geradores.ClassePublica;
import br.com.persist.geradores.ConstrutorPrivado;
import br.com.persist.geradores.Funcao;
import br.com.persist.geradores.Parametros;

public class FormularioBuilder extends Builder {
	private static final String CONTAINER_SET = "container.set";
	private static final String STATIC_VOID = "static void";
	private static final String NEW_FORM = " form = new ";
	private static final String CONTAINER = " container";
	private static final String CRIAR = "criar";

	protected FormularioBuilder(Config config) {
		super("Formulario", "extends AbstratoFormulario", config);
	}

	@Override
	void templateImport(Arquivo arquivo) {
		arquivo.addImport("java.awt.BorderLayout");
		arquivo.addImport("java.awt.Window").newLine();
		arquivo.addImport("br.com.persist.abstrato.AbstratoFormulario");
		arquivo.addImport("br.com.persist.formulario.Formulario").newLine();
	}

	@Override
	void templateClass(ClassePublica classe) {
		classe.addInstrucao("private static final long serialVersionUID = 1L");
		classe.addInstrucao("private final " + config.nameCapContainer() + CONTAINER).newLine();

		ConstrutorPrivado construtor = null;
		if (config.comFichario) {
			construtor = classe.criarConstrutorPrivado(config.nameCapFormulario(),
					new Parametros("Formulario formulario, String conteudo, String idPagina"));
		} else {
			construtor = classe.criarConstrutorPrivado(config.nameCapFormulario(),
					new Parametros("Formulario formulario"));
		}
		construtor.addInstrucao("super(formulario, " + config.nameCapMensagens() + ".getString("
				+ config.nameCapConstantes() + ".LABEL_" + config.nameUpper + "))");
		if (config.comFichario) {
			construtor.addInstrucao(
					"container = new " + config.nameCapContainer() + "(this, formulario, conteudo, idPagina)");
		} else {
			construtor.addInstrucao("container = new " + config.nameCapContainer() + "(this, formulario)");
		}
		construtor.addInstrucao(CONTAINER_SET + config.nameCapFormulario() + "(this)");
		construtor.addInstrucao("montarLayout()");

		classe.newLine();
		construtor = classe.criarConstrutorPrivado(config.nameCapFormulario(),
				new Parametros(config.nameCapContainer() + CONTAINER));
		construtor.addInstrucao("super(container.getFormulario(), " + config.nameCapMensagens() + ".getString("
				+ config.nameCapConstantes() + ".LABEL_" + config.nameUpper + "))");
		construtor.addInstrucao(CONTAINER_SET + config.nameCapFormulario() + "(this)");
		construtor.addInstrucao("this.container = container");
		construtor.addInstrucao("container.setJanela(this)");
		construtor.addInstrucao("montarLayout()");

		classe.newLine();
		Funcao funcao = classe.criarFuncaoPrivada("void", "montarLayout");
		funcao.addInstrucao("add(BorderLayout.CENTER, container)");

		classe.newLine();
		funcao = classe.criarFuncaoPublica(STATIC_VOID, CRIAR,
				new Parametros("Formulario formulario, " + config.nameCapContainer() + CONTAINER));
		funcao.addInstrucao(config.nameCapFormulario() + NEW_FORM + config.nameCapFormulario() + "(container)");
		funcao.addInstrucao("Formulario.posicionarJanela(formulario, form)");

		classe.newLine();
		if (config.comFichario) {
			funcao = classe.criarFuncaoPublica(STATIC_VOID, CRIAR,
					new Parametros("Formulario formulario, String conteudo, String idPagina"));
			funcao.addInstrucao(config.nameCapFormulario() + NEW_FORM + config.nameCapFormulario()
					+ "(formulario, conteudo, idPagina)");
		} else {
			funcao = classe.criarFuncaoPublica(STATIC_VOID, CRIAR, new Parametros("Formulario formulario"));
			funcao.addInstrucao(config.nameCapFormulario() + NEW_FORM + config.nameCapFormulario() + "(formulario)");
		}
		funcao.addInstrucao("Formulario.posicionarJanela(formulario, form)");

		classe.newLine();
		funcao = classe.criarFuncaoPublica("void", "excluirContainer");
		funcao.addInstrucao("remove(container)");
		funcao.addInstrucao("container.setJanela(null)");
		funcao.addInstrucao(CONTAINER_SET + config.nameCapFormulario() + "(null)");
		funcao.addInstrucao("fechar()");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "windowOpenedHandler", new Parametros("Window window"));
		funcao.addInstrucao("container.windowOpenedHandler(window)");
	}
}