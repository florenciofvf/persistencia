package br.com.persist.plugins.instrucao.cmpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;

public class InstrucaoMontador {
	private InstrucaoMontador() {
	}

	public static boolean compilar(String arquivo, AtomicReference<List<Atom>> ref)
			throws IOException, InstrucaoException {
		File file = new File(CacheBiblioteca.ROOT, arquivo);
		if (!file.isFile()) {
			return false;
		}
		String script = conteudo(file);
		if (Util.estaVazio(script)) {
			return false;
		}
		InstrucaoAtom lexico = new InstrucaoAtom(script);
		List<Atom> atoms = lexico.getListaAtom();
		if (atoms.isEmpty()) {
			return false;
		}
		InstrucaoGramatica gramatica = new InstrucaoGramatica(atoms);
		List<Metodo> metodos = gramatica.montarMetodos();
		if (metodos.isEmpty()) {
			return false;
		}
		File destino = new File(CacheBiblioteca.ROOT, arquivo + Biblioteca.EXTENSAO);
		try (PrintWriter pw = new PrintWriter(destino)) {
			for (Metodo metodo : metodos) {
				pw.println();
				metodo.print(pw);
			}
		}
		List<Atom> all = new ArrayList<>(atoms);
		all.addAll(lexico.getComentarios());
		if (ref != null) {
			ref.set(all);
		}
		return true;
	}

	public static String conteudo(File file) throws IOException {
		if (file != null && file.exists()) {
			StringBuilder sb = new StringBuilder();
			try (InputStream is = new FileInputStream(file)) {
				int i = is.read();
				while (i != -1) {
					sb.append((char) i);
					i = is.read();
				}
			}
			return sb.toString();
		}
		return "";
	}
}