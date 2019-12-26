package br.com.persist.fmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FmtParser {

	private FmtParser() {
	}

	public static Valor parse(File file) throws IOException {
		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String linha = br.readLine();

			while (linha != null) {
				sb.append(linha.trim());
				linha = br.readLine();
			}
		}

		return parse(sb.toString());
	}

	public static Valor parse(String string) {
		if (string != null) {
			string = string.trim();
		} else {
			return null;
		}

		Objeto objeto = null;
		Array array = null;

		if (string.charAt(0) == '[') {
			array = new Array();

		} else if (string.charAt(0) == '{') {
			objeto = new Objeto();
		}

		return objeto != null ? objeto : array;
	}
}