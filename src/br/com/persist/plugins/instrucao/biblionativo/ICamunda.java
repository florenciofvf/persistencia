package br.com.persist.plugins.instrucao.biblionativo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicBoolean;

public class ICamunda {
	private ICamunda() {
	}

	@Biblio(1)
	public static Object getConsulta() {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT NAME_, VAR_TYPE_, TEXT_, LONG_");
		sb.append("\nFROM ACT_HI_VARINST");
		sb.append("\nWHERE PROC_INST_ID_ = 'processInstanceId'");
		sb.append("\nORDER BY 1");

		return sb.toString();
	}

	@Biblio(2)
	public static Object montarPutVariable(Object absoluto) throws IOException {
		StringBuilder sb = new StringBuilder();
		if (absoluto == null) {
			return sb.toString();
		}
		String file = absoluto.toString();
		AtomicBoolean atomico = new AtomicBoolean(false);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String linha = br.readLine();
			while (linha != null) {
				if (linha.trim().length() > 0) {
					String[] strings = linha.split("\t");
					Variavel v = new Variavel(strings[Variavel.COL_NOME], strings[Variavel.COL_TIPO]);
					v.processar(sb, strings, atomico);
					sb.append("\n");
				}
				linha = br.readLine();
			}
		}
		return sb.toString();
	}
}

class Variavel {
	final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	static final byte COL_NOME = 0;
	static final byte COL_TIPO = 1;
	static final byte COL_STR = 2;
	static final byte COL_LON = 3;
	final String nome;
	final String tipo;

	public Variavel(String nome, String tipo) {
		this.nome = nome;
		this.tipo = tipo;
	}

	void processar(StringBuilder sb, String[] strings, AtomicBoolean atomico) {
		if (atomico.get()) {
			sb.append("---");
		}
		sb.append(nome);
		sb.append(", " + tipo.substring(0, 1).toUpperCase() + tipo.substring(1));
		sb.append(", " + getValor(strings));
		atomico.set(true);
	}

	String getValor(String[] strings) {
		String string = strings[COL_STR];
		String longVa = strings.length == 4 ? strings[COL_LON] : null;
		if ("string".equals(tipo)) {
			return string;
		} else if ("long".equals(tipo)) {
			return longVa;
		} else if ("boolean".equals(tipo)) {
			boolean b = "1".equals(longVa);
			return Boolean.toString(b);
		} else if ("date".equals(tipo)) {
			return fmt(new Date(Long.parseLong(longVa)));
		}
		throw new IllegalStateException();
	}

	String fmt(Date data) {
		return format.format(data) + "T00:00:00.000-0300";
	}
}