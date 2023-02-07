package br.com.persist.assistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuebraArquivo {
	private static final String ORIGEM = "/Users/florenciovieirafilho/Desktop/log/server";
	private static final Logger LOG = Logger.getGlobal();
	private static final String SUFIXO_BLOCO = ".log";
	private static final int TOTAL_BLOCOS = 10;

	public static void main(String[] args) throws Exception {
		File arquivo = new File(ORIGEM);
		long tamanho = arquivo.length();
		info("TAMANHO DO ARQUIVO: ", ORIGEM, " -> ", tamanho);

		long tamanhoBloco = tamanho / TOTAL_BLOCOS;
		int contador = 0;
		int indice = 0;

		while (contador < TOTAL_BLOCOS) {
			copiar(arquivo, new File(ORIGEM + "_" + contador + "_" + SUFIXO_BLOCO), indice, tamanhoBloco);
			indice += tamanhoBloco;
			contador++;
		}

		info("Finalizado");
	}

	private static void copiar(File origem, File destino, long indice, long quantidade) throws IOException {
		try (FileInputStream fis = new FileInputStream(origem)) {
			try (FileOutputStream fos = new FileOutputStream(destino)) {
				FileChannel ci = fis.getChannel();
				FileChannel co = fos.getChannel();
				ci.transferTo(indice, quantidade, co);
				info("ARQUIVO GERADO(", destino, ") TAMANHO(", co.size(), ") POSICAO(", indice, ")");
			}
		}
	}

	private static void info(Object... objects) {
		LOG.log(Level.INFO, getString(objects));
	}

	private static String getString(Object[] objects) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : objects) {
			sb.append(obj.toString());
		}
		return sb.toString();
	}
}