package br.com.persist.plugins.legado;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Util;

public abstract class Legado {
	protected static final Map<String, String> map;
	protected final List<String> metas;
	protected boolean valido;
	protected String column;
	protected String type;
	protected String name;

	protected Legado() {
		metas = new ArrayList<>();
	}

	public void aplicar(Attributes attr) {
		valido = true;
		type = typeMap(attr.getValue("type"));
		int pos = type.indexOf("#");
		if (pos != -1) {
			String[] array = type.substring(pos + 1).split("#");
			for (String str : array) {
				String s = Util.replaceAll(str.trim(), "'", "\"");
				metas.add(s);
			}
			type = type.substring(0, pos);
		}
		type = typeNormal(type);
		column = attr.getValue("column");
		name = attr.getValue("name");
		String strValido = attr.getValue("valido");
		if (!Util.isEmpty(strValido)) {
			valido = "true".equalsIgnoreCase(strValido);
		}
	}

	public void gerar(PrintWriter pw) {
		if (!valido) {
			return;
		}
		pw.println();
		gerarImpl(pw);
	}

	protected abstract void gerarImpl(PrintWriter pw);

	protected void println(PrintWriter pw, String string) {
		pw.println("\t" + string);
	}

	protected void printDeclaracao(PrintWriter pw) {
		println(pw, "private " + type + " " + name + ";");
	}

	protected void checarMetas(PrintWriter pw) {
		for (String meta : metas) {
			println(pw, meta);
		}
	}

	protected String citar(String string) {
		return Util.citar2(string);
	}

	protected String typeMap(String chave) {
		String string = map.get(chave);
		if (string == null) {
			return chave;
		}
		return string;
	}

	protected String typeNormal(String string) {
		int pos = string.lastIndexOf(".");
		if (pos != -1) {
			string = string.substring(pos + 1);
		}
		return string;
	}

	static {
		map = new HashMap<>();
		map.put("char", "String");
		map.put("byte", "Byte");
		map.put("short", "Short");
		map.put("int", "Long");
		map.put("long", "Long");
		map.put("float", "BigDecimal");
		map.put("double", "BigDecimal");
		map.put("boolean", "Boolean");
		map.put("java.sql.Timestamp", "Date#@Temporal(TemporalType.TIMESTAMP)");
	}
}