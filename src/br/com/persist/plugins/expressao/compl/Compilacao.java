package br.com.persist.plugins.expressao.compl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.biblio.BibliotecaContexto;
import br.com.persist.plugins.expressao.processador.CacheBiblioteca;

public class Compilacao {
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
		if (!CacheBiblioteca.COMPILADOS.isDirectory() && !CacheBiblioteca.COMPILADOS.mkdir()) {
			throw new ExpressaoException(CacheBiblioteca.COMPILADOS.toString(), false);
		}

		if (!file.isFile()) {
			throw new ExpressaoException("Inexistente >>> " + file.toString(), false);
		}

		String string = getString(file);
		Compilador compilador = new Compilador(string);
		BibliotecaContexto biblioteca = new BibliotecaContexto(file);
		compilador.setSelecionado(biblioteca);
		compilador.montarHierarquia();

		if (compilador.getSelecionado() != biblioteca) {
			throw new ExpressaoException("erro.compilacao");
		}

		biblioteca.transferirConstantes();
		biblioteca.configurarSaltos();
		File destino = getCompilado(biblioteca);
		try (PrintWriter pw = new PrintWriter(destino, StandardCharsets.UTF_8.name())) {
			//biblioteca.salvar(this, pw);
			//--biblioteca.salvarEstruturas(pw);
		}
		return biblioteca;

//		AtomicInteger atomic = new AtomicInteger(0);
//		biblioteca.fragmentar(atomic);
//		while (atomic.get() > 0) {
//			atomic.set(0);
//			biblioteca.fragmentar(atomic);
//		}
//		biblioteca.estruturar();
//		biblioteca.indexar();
//		biblioteca.desviar();
//		biblioteca.validar();
//		File destino = getCompilado(biblioteca);
//		try (PrintWriter pw = new PrintWriter(destino, StandardCharsets.UTF_8.name())) {
//			biblioteca.salvar(this, pw);
//		}
//		return biblioteca;
	}

	public static File getCompilado(BibliotecaContexto biblio) throws ExpressaoException {
		return null;
		//return CacheBiblioteca.getArquivo(biblio);
	}
}