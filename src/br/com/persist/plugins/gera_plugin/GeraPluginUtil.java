package br.com.persist.plugins.gera_plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class GeraPluginUtil {
	private GeraPluginUtil() {
	}

	static void mensagensProp(Config config) throws IOException {
		File file = new File(config.destino, "mensagens.properties");
		gerar(config, "mensagens_prop", file);
	}

	static void preferencias(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Preferencia.java");
		gerar(config, "Preferencia", file);
	}

	static void constantes(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Constantes.java");
		gerar(config, "Constantes", file);
	}

	static void mensagens(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Mensagens.java");
		gerar(config, "Mensagens", file);
	}

	static void formulario(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Formulario.java");
		gerar(config, "Formulario", file);
	}

	static void dialogo(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Dialogo.java");
		gerar(config, "Dialogo", file);
	}

	static void fabricaDialogo(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Fabrica.java");
		gerarFabrica(config, "FabricaDialogo", file);
	}

	static void fabrica(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Fabrica.java");
		gerarFabrica(config, "Fabrica", file);
	}

	static void containerDialogo(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Container.java");
		gerar(config, "ContainerDialogo", file);
	}

	static void container(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Container.java");
		gerar(config, "Container", file);
	}

	private static void gerarFabrica(Config config, String template, File file) throws IOException {
		try (PrintWriter pw = new PrintWriter(file)) {
			BufferedReader br = criarBufferedReader(template);
			String linha = br.readLine();
			while (linha != null) {
				if ("###recurso###".equals(linha)) {
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
			pw.println("\t\tbr.com.persist.assistencia.Preferencias.addOutraPreferencia(" + config.nomeCap
					+ "Preferencia.class);");
		}
		if (config.comRecurso()) {
			pw.println("\t\tbr.com.persist.assistencia.Util.criarDiretorio(" + config.nomeCap + "Constantes."
					+ config.recurso + ");");
		}
		pw.println("\t}");
		pw.println();
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

	static void exception(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Exception.java");
		gerar(config, "Exception", file);
	}

	static void listener(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Listener.java");
		gerar(config, "Listener", file);
	}

	static void provedor(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Provedor.java");
		gerar(config, "Provedor", file);
	}

	static void modelo(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Modelo.java");
		gerar(config, "Modelo", file);
	}

	static void handler(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Handler.java");
		gerar(config, "Handler", file);
	}

	static void util(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Util.java");
		gerar(config, "Util", file);
	}
}