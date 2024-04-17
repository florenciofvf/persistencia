package br.com.persist.plugins.gera_plugin;

import br.com.persist.geradores.Arquivo;
import br.com.persist.geradores.ClassePrivada;
import br.com.persist.geradores.ClassePublica;
import br.com.persist.geradores.ConstrutorPrivado;
import br.com.persist.geradores.Funcao;
import br.com.persist.geradores.Parametros;
import br.com.persist.geradores.Variavel;

public class FabricaBuilder extends Builder {
	protected FabricaBuilder(Config config) {
		super("Fabrica", "extends AbstratoFabricaContainer", config);
	}

	@Override
	void templateImport(Arquivo arquivo) {
		arquivo.addImport("java.util.ArrayList");
		arquivo.addImport("java.util.Arrays");
		arquivo.addImport("java.util.List").newLine();
		arquivo.addImport("javax.swing.JMenu");
		arquivo.addImport("javax.swing.JMenuItem").newLine();
		arquivo.addImport("br.com.persist.abstrato.AbstratoFabricaContainer");
		arquivo.addImport("br.com.persist.abstrato.AbstratoServico");
		arquivo.addImport("br.com.persist.abstrato.Servico");
		arquivo.addImport("br.com.persist.assistencia.Constantes");
		arquivo.addImport("br.com.persist.assistencia.Icones");
		if (config.comConfiguracao) {
			arquivo.addImport("br.com.persist.abstrato.AbstratoConfiguracao");
			arquivo.addImport("br.com.persist.assistencia.Preferencias");
		}
		if (config.comRecurso()) {
			arquivo.addImport("br.com.persist.assistencia.Util");
		}
		arquivo.addImport("br.com.persist.componente.MenuPadrao1");
		arquivo.addImport("br.com.persist.fichario.Pagina");
		arquivo.addImport("br.com.persist.fichario.PaginaServico");
		arquivo.addImport("br.com.persist.formulario.Formulario").newLine();

		arquivo.addComentario(
				"\t\t<menu classeFabrica=\"" + config.pacote + "." + config.nameCapFabrica() + "\" ativo=\"true\" />");
	}

	@Override
	void templateClass(ClassePublica classe) {
		if (config.comConfiguracao || config.comRecurso()) {
			classe.addOverride(false);
			Funcao funcao = classe.criarFuncaoPublica("void", "inicializar");
			if (config.comConfiguracao) {
				funcao.addInstrucao("Preferencias.addOutraPreferencia(" + config.nameCapPreferencia() + ".class)");
			}
			if (config.comRecurso()) {
				funcao.addInstrucao("Util.criarDiretorio(" + config.nameCapConstantes() + "." + config.recurso + ")");
			}
			if (config.comConfiguracao) {
				classe.addOverride(true);
				funcao = classe.criarFuncaoPublica("AbstratoConfiguracao", "getConfiguracao",
						new Parametros(new Variavel("Formulario", "formulario")));
				funcao.addReturn("new " + config.nameCapConfiguracao() + "(formulario)");
			}
		}

		classe.addOverride(true);
		Funcao funcao = classe.criarFuncaoPublica("PaginaServico", "getPaginaServico");
		funcao.addReturn("new " + config.nameCapPaginaServico() + "()");

		classe.newLine();
		ClassePrivada classePrivada = classe
				.criarClassePrivada(config.nameCapPaginaServico() + " implements PaginaServico");
		classePrivada.addOverride(false);
		funcao = classePrivada.criarFuncaoPublica("Pagina", "criarPagina",
				new Parametros("Formulario formulario, String stringPersistencia"));
		if (config.comFichario) {
			funcao.addReturn("new " + config.nameCapContainer() + "(null, formulario, null, stringPersistencia)");
		} else {
			funcao.addReturn("new " + config.nameCapContainer() + "(null, formulario)");
		}

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("List<Servico>", "getServicos", new Parametros("Formulario formulario"));
		funcao.addReturn("Arrays.asList(new " + config.nameCap + "Servico())");

		classe.newLine();
		classe.criarClassePrivada(config.nameCapServico() + " extends AbstratoServico");

		classe.addOverride(true);
		funcao = classe.criarFuncaoPublica("List<JMenuItem>", "criarMenuItens",
				new Parametros("Formulario formulario, JMenu menu"));

		funcao.addInstrucao("List<JMenuItem> lista = new ArrayList<>()");
		funcao.addInstrucao("lista.add(new Menu" + config.nameCap + "(formulario))");
		funcao.addReturn("lista");

		classe.newLine();
		classePrivada = classe.criarClassePrivada("Menu" + config.nameCap + " extends MenuPadrao1");
		classePrivada.addInstrucao("private static final long serialVersionUID = 1L").newLine();
		ConstrutorPrivado construtor = classePrivada.criarConstrutorPrivado("Menu" + config.nameCap,
				new Parametros("Formulario formulario"));

		if (config.comDialogo) {
			construtor.addInstrucao("super(Constantes.LABEL_VAZIO, Icones." + config.icone + ")");
		} else {
			construtor.addInstrucao("super(Constantes.LABEL_VAZIO, Icones." + config.icone + ", false)");
		}

		construtor.addInstrucao("setText(" + config.nameCapMensagens() + ".getString(" + config.nameCapConstantes()
				+ ".LABEL_" + config.nameUpper + "))");
		if (config.comFichario) {
			construtor.addInstrucao("ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new "
					+ config.nameCapContainer() + "(null, formulario, null, null)))");
			construtor.addInstrucao("formularioAcao.setActionListener(e -> " + config.nameCapFormulario()
					+ ".criar(formulario, null, null))");
		} else {
			construtor.addInstrucao("ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new "
					+ config.nameCapContainer() + "(null, formulario)))");
			construtor.addInstrucao(
					"formularioAcao.setActionListener(e -> " + config.nameCapFormulario() + ".criar(formulario))");
		}
		if (config.comDialogo) {
			construtor.addInstrucao(
					"dialogoAcao.setActionListener(e -> " + config.nameCapDialogo() + ".criar(formulario))");
		}
	}
}