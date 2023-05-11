package br.com.persist.plugins.gera_plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class GeraPluginUtil {
	private GeraPluginUtil() {
	}

	static void criarMensagens(Config config) throws FileNotFoundException {
		File file = new File(config.destino, "mensagens.properties");
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.println("label." + config.nomeDecap + "=" + config.nomeCap);
			pw.println("label." + config.nomeDecap + "_min=" + config.nomeMin);
		}
	}

	static void criarConstantes(Config config) throws IOException {
		File file = new File(config.destino, config.nomeCap + "Constantes.java");
		gerar(config, "Constantes", file);
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