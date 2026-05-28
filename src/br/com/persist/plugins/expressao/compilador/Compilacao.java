package br.com.persist.plugins.expressao.compilador;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;

public class Compilacao {
	private final List<String> alertas = new ArrayList<>();
	private final List<Token> tokens = new ArrayList<>();

	public List<String> getAlertas() {
		return alertas;
	}

	public String getStringAlerta() {
		if (alertas.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (String item : alertas) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append(item);
		}
		builder.insert(0, '\n');
		return builder.toString();
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public static String conteudo(File file) throws IOException {
		if (file != null && file.exists()) {
			StringBuilder sb = new StringBuilder();
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				int i = reader.read();
				while (i != -1) {
					char c = (char) i;
					if (c != '\r') {
						sb.append(c);
					}
					i = reader.read();
				}
			}
			return sb.toString();
		}
		return "";
	}

	public BibliotecaContexto compilar(File file) throws IOException, ExpressaoException {
		if (file == null) {
			throw new ExpressaoException("File null >>> Compilacao.compilar(...)");
		}

		if (!file.isFile()) {
			throw new ExpressaoException("Inexistente >>> " + file.toString(), false);
		}

		if (!CacheBiblioteca.COMPILADOS.isDirectory() && !CacheBiblioteca.COMPILADOS.mkdir()) {
			throw new ExpressaoException(CacheBiblioteca.COMPILADOS.toString(), false);
		}

		TokenManager tokenManager = new TokenManager(conteudo(file));
		BibliotecaContexto biblioteca = new BibliotecaContexto(file);
		tokenManager.selecionar(biblioteca);
		tokenManager.montarHierarquia();

		if (tokenManager.getSelecionado() != biblioteca) {
			throw new ExpressaoException("erro.compilacao");
		}

		biblioteca.transferirConstantes();
		biblioteca.checarPackage();
		biblioteca.checarAlias();

		File destino = getCompilado(biblioteca);

		try (PrintWriter pw = new PrintWriter(destino, StandardCharsets.UTF_8.name())) {
			biblioteca.salvarEstruturas(pw);
		}

		tokens.clear();
		tokens.addAll(tokenManager.getTokens());

		alertas.clear();
		alertas.addAll(tokenManager.getAlertas());

		return biblioteca;
	}

	public static File getCompilado(BibliotecaContexto biblio) throws ExpressaoException {
		return CacheBiblioteca.getArquivoCompilado(biblio);
	}
}