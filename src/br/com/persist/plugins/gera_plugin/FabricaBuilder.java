package br.com.persist.plugins.gera_plugin;

import java.io.IOException;
import java.io.PrintWriter;

public class FabricaBuilder extends Builder {
	protected FabricaBuilder(Config config) {
		super("Fabrica", "extends AbstratoFabricaContainer", config);
	}

	@Override
	void templateImport(PrintWriter pw) throws IOException {
		importar(pw, "java.util.ArrayList");
		importar(pw, "java.util.Arrays");
		importar(pw, "java.util.List");
		pw.println();
		importar(pw, "javax.swing.JMenu");
		importar(pw, "javax.swing.JMenuItem");
		pw.println();
		importar(pw, "br.com.persist.abstrato.AbstratoFabricaContainer");
		importar(pw, "br.com.persist.abstrato.AbstratoServico");
		importar(pw, "br.com.persist.abstrato.Servico");
		importar(pw, "br.com.persist.assistencia.Constantes");
		importar(pw, "br.com.persist.assistencia.Icones");
		if (config.configuracao) {
			importar(pw, "br.com.persist.abstrato.AbstratoConfiguracao");
			importar(pw, "br.com.persist.assistencia.Preferencias");
		}
		if (config.comRecurso()) {
			importar(pw, "br.com.persist.assistencia.Util");
		}
		importar(pw, "br.com.persist.componente.MenuPadrao1");
		importar(pw, "br.com.persist.fichario.Pagina");
		importar(pw, "br.com.persist.fichario.PaginaServico");
		importar(pw, "br.com.persist.formulario.Formulario");
		pw.println();
		pw.println("//\t\t<menu classeFabrica=\"" + config.pacote + "." + config.nomeCapitalizado + objeto
				+ "\" ativo=\"true\" />");
	}

	@Override
	void templateClass(PrintWriter pw) throws IOException {
		if (config.configuracao && config.comRecurso()) {
			override(pw);
			publicMethod(pw, "void inicializar()");
			if (config.configuracao) {
				instrucao(pw, "Preferencias.addOutraPreferencia(" + config.nomeCapitalizado + "Preferencia.class)");
			}
			if (config.comRecurso()) {
				instrucao(pw, "Util.criarDiretorio(" + config.nomeCapitalizado + "Constantes." + config.recurso + ")");
			}
			finalMethod(pw);

			if (config.configuracao) {
				override(pw);
				publicMethod(pw, "AbstratoConfiguracao getConfiguracao(Formulario formulario)");
				returnMethod(pw, "new " + config.nomeCapitalizado + "Configuracao(formulario)");
				finalMethod(pw);
			}
		}

		override(pw);
		publicMethod(pw, "PaginaServico getPaginaServico()");
		returnMethod(pw, "new " + config.nomeCapitalizado + "PaginaServico()");
		finalMethod(pw);

		privateClass(pw, config.nomeCapitalizado + "PaginaServico implements PaginaServico");
		override(pw);
		publicMethod(pw, "Pagina criarPagina(Formulario formulario, String stringPersistencia)");
		returnMethod(pw, "new " + config.nomeCapitalizado + "Container(null, formulario)");
		finalMethod(pw, false);
		finalMethod(pw);

		override(pw);
		publicMethod(pw, "List<Servico> getServicos(Formulario formulario)");
		returnMethod(pw, "Arrays.asList(new " + config.nomeCapitalizado + "Servico())");
		finalMethod(pw);

		privateClass(pw, config.nomeCapitalizado + "Servico extends AbstratoServico");
		finalMethod(pw);

		override(pw);
		publicMethod(pw, "List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu)");
		pw.println("\t\tif (menu.getItemCount() > 0) {");
		pw.println("\t\t\tmenu.addSeparator();");
		pw.println("\t\t}");
		instrucao(pw, "List<JMenuItem> lista = new ArrayList<>()");
		instrucao(pw, "lista.add(new Menu" + config.nomeCapitalizado + "(formulario))");
		returnMethod(pw, "lista");
		finalMethod(pw);

		privateClass(pw, "Menu" + config.nomeCapitalizado + " extends MenuPadrao1");
		pw.println("\t\tprivate static final long serialVersionUID = 1L;");
		pw.println();
		privateMethod(pw, "Menu" + config.nomeCapitalizado + "(Formulario formulario)");
		if (config.comDialogo) {
			instrucao(pw, "super(Constantes.LABEL_VAZIO, Icones." + config.icone + ")");
		} else {
			instrucao(pw, "super(Constantes.LABEL_VAZIO, Icones." + config.icone + ", false)");
		}
		instrucao(pw, "setText(" + config.nomeCapitalizado + "Mensagens.getString(" + config.nomeCapitalizado
				+ "Constantes.LABEL_" + config.nomeCaixaAlta + "))");
		instrucao(pw, "ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new " + config.nomeCapitalizado
				+ "Container(null, formulario)))");
		instrucao(pw,
				"formularioAcao.setActionListener(e -> " + config.nomeCapitalizado + "Formulario.criar(formulario))");
		if (config.comDialogo) {
			instrucao(pw,
					"dialogoAcao.setActionListener(e -> " + config.nomeCapitalizado + "Dialogo.criar(formulario))");
		}
		finalMethod(pw, false);
		finalMethod(pw, false);
	}
}