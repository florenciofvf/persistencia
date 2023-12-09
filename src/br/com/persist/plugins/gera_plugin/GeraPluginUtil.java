package br.com.persist.plugins.gera_plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import br.com.persist.assistencia.Constantes;

public class GeraPluginUtil {
	private GeraPluginUtil() {
	}

	static void objeto(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCapitalizado + ".java");
		gerar(config, "Objeto", file);
	}

	static void mensagensProp(Config config) throws IOException {
		File file = new File(config.destino, "mensagens.properties");
		gerar(config, "mensagens_prop", file);
	}

	static void preferencias(Config config) throws IOException {
		transferir(config, "Configuracao");
		transferir(config, "Preferencia");
	}

	static void constantes(Config config) throws IOException {
		transferir(config, "Constantes");
	}

	static void mensagens(Config config) throws IOException {
		transferir(config, "Mensagens");
	}

	static void formulario(Config config) throws IOException {
		transferir(config, "Formulario");
	}

	static void dialogo(Config config) throws IOException {
		transferir(config, "Dialogo");
	}

	static void exception(Config config) throws IOException {
		transferir(config, "Exception");
	}

	static void listener(Config config) throws IOException {
		transferir(config, "Listener");
	}

	static void provedor(Config config) throws IOException {
		transferir(config, "Provedor");
	}

	static void modelo(Config config) throws IOException {
		transferir(config, "Modelo");
	}

	static void handler(Config config) throws IOException {
		transferir(config, "Handler");
	}

	static void util(Config config) throws IOException {
		transferir(config, "Util");
	}

	static void fabricaDialogo(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCapitalizado + "Fabrica.java");
		gerarFabrica(config, "FabricaDialogo", file);
	}

	static void fabrica(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCapitalizado + "Fabrica.java");
		gerarFabrica(config, "Fabrica", file);
	}

	static void containerDialogo(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCapitalizado + "Container.java");
		gerar(config, "ContainerDialogo", file);
	}

	static void container(Config config) throws IOException {
		transferir(config, "Container");
	}

	private static void gerarFabrica(Config config, String template, File file) throws IOException {
		final String chaveRecurso = Constantes.SEP + "recurso" + Constantes.SEP;
		try (PrintWriter pw = new PrintWriter(file)) {
			BufferedReader br = criarBufferedReader(template);
			String linha = br.readLine();
			while (linha != null) {
				if (chaveRecurso.equals(linha)) {
					configRecurso(config, pw);
				} else {
					linha = config.processar(linha);
					pw.println(linha);
				}
				linha = br.readLine();
			}
			br.close();
		}
	}

	private static void configRecurso(Config config, PrintWriter pw) {
		if (!config.configuracao && !config.comRecurso()) {
			return;
		}
		pw.println("\t@Override");
		pw.println("\tpublic void inicializar() {");
		if (config.configuracao) {
			pw.println("\t\tbr.com.persist.assistencia.Preferencias.addOutraPreferencia(" + config.nomeCapitalizado
					+ "Preferencia.class);");
		}
		if (config.comRecurso()) {
			pw.println("\t\tbr.com.persist.assistencia.Util.criarDiretorio(" + config.nomeCapitalizado + "Constantes."
					+ config.recurso + ");");
		}
		pw.println("\t}");
		pw.println();

		if (config.configuracao) {
			pw.println("\t@Override");
			pw.println(
					"\tpublic br.com.persist.abstrato.AbstratoConfiguracao getConfiguracao(Formulario formulario) {");
			pw.println("\t\treturn new " + config.nomeCapitalizado + "Configuracao(formulario);");
			pw.println("\t}");
			pw.println();
		}
	}

	private static void transferir(Config config, String objeto) throws IOException {
		File file = new File(config.destino, config.nomeCapitalizado + objeto + ".java");
		gerar(config, objeto, file);
	}

	private static void gerar(Config config, String template, File file) throws IOException {
		try (PrintWriter pw = new PrintWriter(file)) {
			BufferedReader br = criarBufferedReader(template);
			String linha = br.readLine();
			while (linha != null) {
				linha = config.processar(linha);
				pw.println(linha);
				linha = br.readLine();
			}
			br.close();
		}
	}

	private static BufferedReader criarBufferedReader(String template) {
		InputStream is = GeraPluginContainer.class.getResourceAsStream(template);
		InputStreamReader isr = new InputStreamReader(is);
		return new BufferedReader(isr);
	}
}