package br.com.persist.plugins.projeto;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.assistencia.Icones;

public class MapaSufixos {
	private static final List<ChaveIcone> lista = new ArrayList<>();

	private MapaSufixos() {
	}

	public static Icon getIcon(Arquivo arquivo) {
		String nome = arquivo.getName();
		int pos = nome.lastIndexOf('_');
		if (pos != -1) {
			nome = nome.substring(pos);
			ChaveIcone chaveIcone = get(nome);
			if (chaveIcone != null) {
				return chaveIcone.icone;
			}
		}
		if (arquivo.isFile()) {
			return Icones.TEXTO;
		}
		return null;
	}

	private static ChaveIcone get(String string) {
		for (ChaveIcone item : lista) {
			if (item.chave.equals(string)) {
				return item;
			}
		}
		return null;
	}

	public static List<ChaveIcone> getLista() {
		return lista;
	}

	static {
		lista.add(new ChaveIcone("exception", Icones.EXCEPTION));
		lista.add(new ChaveIcone("database", Icones.DATABASE));
		lista.add(new ChaveIcone("endpoint", Icones.URL));
		lista.add(new ChaveIcone("question", Icones.QUESTION));
		lista.add(new ChaveIcone("estados", Icones.ESTRELA));
		lista.add(new ChaveIcone("service", Icones.CONFIG));
		lista.add(new ChaveIcone("refresh", Icones.ATUALIZAR));
		lista.add(new ChaveIcone("usuario", Icones.USUARIO));
		lista.add(new ChaveIcone("pessoas", Icones.PESSOAS));
		lista.add(new ChaveIcone("target2", Icones.TARGET2));
		lista.add(new ChaveIcone("target3", Icones.TARGET3));
		lista.add(new ChaveIcone("parent", Icones.MODULO));
		lista.add(new ChaveIcone("target", Icones.TARGET));
		lista.add(new ChaveIcone("pessoa", Icones.PESSOA));
		lista.add(new ChaveIcone("server", Icones.SERVER));
		lista.add(new ChaveIcone("cifrao", Icones.CIFRAO));
		lista.add(new ChaveIcone("empty", Icones.VAZIO));
		lista.add(new ChaveIcone("timer", Icones.TIMER));
		lista.add(new ChaveIcone("anexo", Icones.ANEXO));
		lista.add(new ChaveIcone("start", Icones.START));
		lista.add(new ChaveIcone("check", Icones.SUCESSO));
		lista.add(new ChaveIcone("xhtml", Icones.ELEMENTO));
		lista.add(new ChaveIcone("queue", Icones.QUEUE));
		lista.add(new ChaveIcone("email", Icones.EMAIL));
		lista.add(new ChaveIcone("auto", Icones.CONFIG2));
		lista.add(new ChaveIcone("exec", Icones.EXECUTAR));
		lista.add(new ChaveIcone("info", Icones.INFO));
		lista.add(new ChaveIcone("seta", Icones.SETA));
		lista.add(new ChaveIcone("desc", Icones.RULE));
		lista.add(new ChaveIcone("sinc", Icones.SINCRONIZAR));
		lista.add(new ChaveIcone("file", Icones.NOVO));
		lista.add(new ChaveIcone("down", Icones.BAIXAR));
		lista.add(new ChaveIcone("html", Icones.ELEMENTO));
		lista.add(new ChaveIcone("boss", Icones.BOSS));
		lista.add(new ChaveIcone("url", Icones.URL));
		lista.add(new ChaveIcone("xml", Icones.ELEMENTO));
		lista.add(new ChaveIcone("bug", Icones.BUG));
		lista.add(new ChaveIcone("css", Icones.CSS));
		lista.add(new ChaveIcone("up", Icones.UPDATE));
		lista.add(new ChaveIcone("js", Icones.JS));
	}
}