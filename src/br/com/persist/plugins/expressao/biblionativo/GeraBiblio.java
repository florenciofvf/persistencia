package br.com.persist.plugins.expressao.biblionativo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class GeraBiblio {
	private static final String PACKAGE = "br.com.persist.plugins.expressao.biblionativo;";
	private static final String PACOTE = "br.com.persist.plugins.expressao.biblionativo.";
	private static final String PREFIXO = ExpressaoConstantes.DEFUN_NATIVE;
	private static final String ROOT = "expressoes" + File.separator;

	public static void main(String[] args) throws Exception {
		Class<?>[] classes = { List.class, Map.class };

		for (Class<?> item : classes) {
			processarBiblioteca(item);
		}
	}

	private static void processarBiblioteca(Class<?> klass) throws IOException, ExpressaoException {
		gerarBiblioteca(klass);
		Compilacao compilacao = new Compilacao();
		String nomeBiblioteca = klass.getSimpleName().toLowerCase();
		File biblioteca = new File(ROOT + nomeBiblioteca);
		compilacao.compilar(biblioteca);
		CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
		Biblioteca biblio = cacheBiblioteca.getBiblioteca(PACOTE + nomeBiblioteca);
		Logger.getGlobal().info("PROCESSADO >>> " + biblio.getNomeAbsoluto());
	}

	private static void gerarBiblioteca(Class<?> classe) throws IOException {
		Method[] methods = classe.getDeclaredMethods();
		java.util.List<Item> itens = new ArrayList<>();
		for (Method m : methods) {
			Biblio biblio = getBiblio(m);
			if (biblio != null) {
				itens.add(new Item(biblio.value(), m));
			}
		}
		itens.sort((o1, o2) -> o1.ordem - o2.ordem);
		PrintWriter pw = new PrintWriter(ROOT + classe.getSimpleName().toLowerCase(), StandardCharsets.UTF_8.name());
		pw.println("package " + PACKAGE);
		pw.println();
		for (Item item : itens) {
			imprimir(pw, classe, item.m);
		}
		pw.close();
	}

	private static Biblio getBiblio(Method m) {
		return m.getAnnotation(Biblio.class);
	}

	static class Item {
		final int ordem;
		final Method m;

		Item(int ordem, Method m) {
			this.ordem = ordem;
			this.m = m;
		}
	}

	private static void imprimir(PrintWriter pw, Class<?> classe, Method m) {
		checarTipo(m.getParameters(), m);
		Class<?> returnType = m.getReturnType();
		String string = returnType.getCanonicalName();
		String sufixo = "void".equals(string) || "java.lang.Void".equals(string) ? " void" : "";
		pw.print(PREFIXO + " " + PACOTE + classe.getSimpleName());
		pw.println(" " + m.getName() + "(" + getArgs(m) + ")" + sufixo + ";");
		pw.println();
	}

	private static void checarTipo(Parameter[] parameters, Method m) {
		for (Parameter p : parameters) {
			Class<?> type = p.getType();
			if (!"java.lang.Object".equals(type.getCanonicalName())) {
				throw new IllegalStateException("Param >>> " + type.getSimpleName() + " Function >>> " + m);
			}
		}
	}

	private static String getArgs(Method m) {
		StringBuilder sb = new StringBuilder();
		Parameter[] parametros = m.getParameters();
		for (Parameter p : parametros) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(p.getName());
		}
		return sb.toString();
	}
}