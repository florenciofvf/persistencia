package br.com.persist.plugins.instrucao.biblionativo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.logging.Logger;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;
import br.com.persist.plugins.instrucao.compilador.Compilador;

public class GeraBiblio {
	private static final String PACOTE = "br_com_persist_plugins_instrucao_biblionativo_";
	private static final String PREFIXO = "function_native";

	public static void main(String[] args) throws Exception {
		Class<?>[] classes = { IArquivo.class, IClass.class, IDB.class, ILinha.class, ILinhas.class, IList.class,
				IMap.class, IMethod.class, IParse.class, IRuntime.class, IString.class, ISwing.class, ISystem.class,
				IUtil.class };

		for (Class<?> item : classes) {
			processarObjeto(item);
		}
	}

	private static void processarObjeto(Class<?> klass) throws IOException, InstrucaoException {
		processar(klass);
		Compilador compilador = new Compilador();
		BibliotecaContexto biblio = compilador.compilar(klass.getSimpleName().toLowerCase());
		Logger.getGlobal().info("Processado >>> " + biblio.getNome());
	}

	private static void processar(java.lang.Class<?> classe) throws FileNotFoundException {
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

	private static void imprimir(PrintWriter pw, java.lang.Class<?> classe, Method m) {
		java.lang.Class<?> returnType = m.getReturnType();
		String string = returnType.getCanonicalName();
		String sufixo = "void".equals(string) || "java.lang.Void".equals(string) ? " : void" : "";
		pw.print(PREFIXO + " " + PACOTE + classe.getSimpleName());
		pw.println(" " + m.getName() + "(" + getArgs(m) + ")" + sufixo + ";");
		pw.println();
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