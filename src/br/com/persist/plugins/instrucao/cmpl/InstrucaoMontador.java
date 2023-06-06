package br.com.persist.plugins.instrucao.cmpl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class InstrucaoMontador {
	private InstrucaoMontador() {
	}

	public static boolean compilar(File file) throws IOException, InstrucaoException {
		if (!file.isFile()) {
			return false;
		}
		String script = Util.conteudo(file);
		InstrucaoAtom lexico = new InstrucaoAtom(script);
		List<Atom> atoms = lexico.getListaAtom();
		InstrucaoGramatica gramatica = new InstrucaoGramatica(atoms);
		List<Metodo> metodos = gramatica.montarMetodos();
		File destino = new File(file.getParent(), file.getName() + ".txt");
		try (PrintWriter pw = new PrintWriter(destino)) {
			for (Metodo metodo : metodos) {
				pw.println();
				metodo.print(pw);
			}
		}
		return true;
	}
}