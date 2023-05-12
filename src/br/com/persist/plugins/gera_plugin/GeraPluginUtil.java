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
		gerar(config, "FabricaDialogo", file);
	}

	static void fabrica(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Fabrica.java");
		gerar(config, "Fabrica", file);
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