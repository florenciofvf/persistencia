package br.com.persist.plugins.expressao.compilador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
		return builder.toString();
	}

	public List<Token> getTokens() {
		return tokens;
	}

	private String getString(File file) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] bloco = new byte[512];
			int i = fis.read(bloco);
			while (i > 0) {
				baos.write(bloco, 0, i);
				i = fis.read(bloco);
			}
		}
		return new String(baos.toByteArray());
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

		TokenManager tokenManager = new TokenManager(getString(file));
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