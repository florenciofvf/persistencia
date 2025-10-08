package br.com.persist.plugins.gera_plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class GeraPluginUtil {
	private GeraPluginUtil() {
	}

	static void objeto(Config config) throws IOException {
		File file = new File(config.diretorioDestino, config.nameCap + ".java");
		gerar(config, "Objeto", file);
	}

	static void mensagensProp(Config config) throws IOException {
		File file = new File(config.diretorioDestino, "mensagens.properties");
		gerar(config, "mensagens_prop", file);
	}

	static void preferencias(Config config) throws IOException {
		transferir(config, "Configuracao");
		transferir(config, "Preferencia");
	}

	static void fichario(Config config) throws IOException {
		transferir(config, "Fichario");
		transferir(config, "Pagina");
	}

	static void mensagens(Config config) throws IOException {
		transferir(config, "Mensagens");
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

	static void tabela(Config config) throws IOException {
		transferir(config, "Editor");
	}

	static void modelo(Config config) throws IOException {
		transferir(config, "Modelo");
	}

	static void handler(Config config) throws IOException {
		transferir(config, "XMLHandler");
	}

	static void split(Config config) throws IOException {
		transferir(config, "Split");
	}

	static void util(Config config) throws IOException {
		transferir(config, "Util");
	}

	private static void transferir(Config config, String objeto) throws IOException {
		File file = new File(config.diretorioDestino, config.nameCap + objeto + ".java");
		gerar(config, objeto, file);
	}

	private static void gerar(Config config, String template, File file) throws IOException {
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
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