package br.com.persist.plugins.gera_plugin;

import br.com.persist.geradores.Arquivo;
import br.com.persist.geradores.ClassePublica;
import br.com.persist.geradores.ConstrutorPrivado;
import br.com.persist.geradores.Funcao;
import br.com.persist.geradores.Parametros;

public class DialogoBuilder extends Builder {
	private static final String CONTAINER_SET = "container.set";
	private static final String CONTAINER = " container";

	protected DialogoBuilder(Config config) {
		super("Dialogo", "extends AbstratoDialogo", config);
	}

	@Override
	void templateImport(Arquivo arquivo) {
		arquivo.addImport("java.awt.BorderLayout");
		arquivo.addImport("java.awt.Dialog");
		arquivo.addImport("java.awt.Frame").newLine();
		arquivo.addImport("br.com.persist.abstrato.AbstratoDialogo");
		arquivo.addImport("br.com.persist.assistencia.Util");
		arquivo.addImport("br.com.persist.formulario.Formulario").newLine();
	}

	@Override
	void templateClass(ClassePublica classe) {
		classe.addInstrucao("private static final long serialVersionUID = 1L");
		classe.addInstrucao("private final " + config.nameCapContainer() + CONTAINER);

		ConstrutorPrivado construtor = classe.criarConstrutorPrivado(config.nameCapDialogo(),
				new Parametros("Frame frame, Formulario formulario"));
		construtor.addInstrucao("super(frame, " + config.nameCapMensagens() + ".getString(" + config.nameCapConstantes()
				+ ".LABEL_" + config.nameUpper + "))");
		if (config.comFichario) {
			construtor.addInstrucao("container = new " + config.nameCapContainer() + "(this, formulario, null, null)");
		} else {
			construtor.addInstrucao("container = new " + config.nameCapContainer() + "(this, formulario)");
		}
		construtor.addInstrucao(CONTAINER_SET + config.nameCapDialogo() + "(this)");
		construtor.addInstrucao("montarLayout()");

		classe.newLine();
		Funcao funcao = classe.criarFuncaoPrivada("void", "montarLayout");
		funcao.addInstrucao("add(BorderLayout.CENTER, container)");

		classe.newLine();
		funcao = classe.criarFuncaoPublica("static void", "criar", new Parametros("Formulario formulario"));
		funcao.addInstrucao(
				config.nameCapDialogo() + " form = new " + config.nameCapDialogo() + "(formulario, formulario)");
		funcao.addInstrucao("Util.configSizeLocation(formulario, form, null)");
		funcao.addInstrucao("form.setVisible(true)");

		classe.newLine();
		funcao = classe.criarFuncaoPublica("void", "excluirContainer");
		funcao.addInstrucao("remove(container)");
		funcao.addInstrucao("container.setJanela(null)");
		funcao.addInstrucao(CONTAINER_SET + config.nameCapDialogo() + "(null)");
		funcao.addInstrucao("fechar()");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("void", "dialogOpenedHandler", new Parametros("Dialog dialog"));
		funcao.addInstrucao("container.dialogOpenedHandler(dialog)");
	}
}