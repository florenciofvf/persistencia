package br.com.persist.plugins.instrucao.biblionativo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.logging.Logger;

import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;
import br.com.persist.plugins.instrucao.compilador.Compilador;

public class GeraBiblio {
	private static final java.lang.String PACOTE = "br_com_persist_plugins_instrucao_biblionativo_";
	private static final java.lang.String PREFIXO = "function_native";

	public static void main(java.lang.String[] args) throws Exception {
		Class<Map> klass = Map.class;
		processar(klass);
		Compilador compilador = new Compilador();
		BibliotecaContexto biblio = compilador.compilar(klass.getSimpleName().toLowerCase());
		Logger.getGlobal().info("Processado >>> " + biblio.getNome());
	}

	private static void processar(Class<?> classe) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("instrucao/" + classe.getSimpleName().toLowerCase());
		Method[] methods = classe.getDeclaredMethods();
		for (Method m : methods) {
			Biblio biblio = getBiblio(m);
			if (biblio != null) {
				imprimir(pw, classe, m);
			}
		}
		pw.close();
	}

	private static Biblio getBiblio(Method m) {
		return m.getAnnotation(Biblio.class);
	}

	private static void imprimir(PrintWriter pw, Class<?> classe, Method m) {
		pw.print(PREFIXO + " " + PACOTE + classe.getSimpleName());
		pw.println(" " + m.getName() + "(" + getArgs(m) + ")");
		pw.println();
	}

	private static java.lang.String getArgs(Method m) {
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