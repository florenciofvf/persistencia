package br.com.persist.arquivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Util;

public class ArquivoUtil {
	private static final Logger LOG = Logger.getGlobal();

	private ArquivoUtil() {
	}

	public static List<String> getIgnorados(File arquivo) {
		List<String> lista = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
			String linha = br.readLine();
			while (linha != null) {
				if (!Util.estaVazio(linha)) {
					lista.add(linha);
				}
				linha = br.readLine();
			}
		} catch (Exception e) {
			LOG.log(Level.FINEST, "ArquivoUtil.getIgnorados()");
		}
		return lista;
	}
}