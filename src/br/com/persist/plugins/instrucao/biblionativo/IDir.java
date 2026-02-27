package br.com.persist.plugins.instrucao.biblionativo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IDir {
	private IDir() {
	}

	@Biblio(0)
	public static String criar(Object absoluto) {
		if (absoluto == null) {
			return "Absoluto vazio";
		}
		File file = new File(absoluto.toString());
		if (file.isDirectory()) {
			return "Existente";
		}
		Path path = Paths.get(absoluto.toString());
		try {
			Path resp = Files.createDirectories(path);
			if (resp.toFile().isDirectory()) {
				return "Sucesso";
			}
			return "Erro ao criar >>> " + resp.toString();
		} catch (Exception ex) {
			return "Erro: " + ex.getMessage();
		}
	}
}