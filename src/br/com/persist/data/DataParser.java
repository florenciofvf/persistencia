package br.com.persist.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataParser {
	public Tipo parse(File file) throws IOException, DataException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String linha = br.readLine();
			while (linha != null) {
				sb.append(linha);
				linha = br.readLine();
			}
		}
		return parse(sb.toString());
	}

	public Tipo parse(String string) throws DataException {
		return DataGramatica.criarTipo(string);
	}
}