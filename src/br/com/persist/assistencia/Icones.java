package br.com.persist.assistencia;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class Icones {
	private static final Map<String, Icone> MAPA_ICONES = new HashMap<>();
	public static final Icone ALINHA_ESQUERDO = criarIcone("alinha_esquerdo");
	public static final Icone ALINHA_DIREITO = criarIcone("alinha_direito");
	public static final Icone BOLA_VERMELHA = criarIcone("bola_vermelha");
	public static final Icone BANCO_DESCONECTA = criarIcone("dbdisconn");
	public static final Icone BOLA_AMARELA = criarIcone("bola_amarela");
	public static final Icone DESC_NUMERO = criarIcone("desc_numero");
	public static final Icone BOLA_VERDE = criarIcone("bola_verde");
	public static final Icone HORIZONTAL = criarIcone("horizontal");
	public static final Icone ASC_NUMERO = criarIcone("asc_numero");
	public static final Icone DESC_TEXTO = criarIcone("desc_texto");
	public static final Icone HIERARQUIA = criarIcone("hierarchy");
	public static final Icone REFERENCIA = criarIcone("reference");
	public static final Icone ASC_TEXTO = criarIcone("asc_texto");
	public static final Icone CENTRALIZAR = criarIcone("section");
	public static final Icone SEPARADOR = criarIcone("separador");
	public static final Icone EXCEPTION = criarIcone("exception");
	public static final Icone ARRASTAR2 = criarIcone("synonym2");
	public static final Icone GLOBO_GIF = criarIconeGIF("globo");
	public static final Icone EXECUTAR = criarIcone("executar");
	public static final Icone EXPANDIR = criarIcone("expandir");
	public static final Icone VERTICAL = criarIcone("vertical");
	public static final Icone FAVORITO = criarIcone("favorito");
	public static final Icone ATUALIZAR = criarIcone("refresh");
	public static final Icone UM_PIXEL = criarIcone("um_pixel");
	public static final Icone FRAGMENTO = criarIcone("feature");
	public static final Icone DATABASE = criarIcone("database");
	public static final Icone QUESTION = criarIcone("question");
	public static final Icone SINCRONIZAR = criarIcone("sync");
	public static final Icone ELEMENTO = criarIcone("element");
	public static final Icone ARRASTAR = criarIcone("synonym");
	public static final Icone ESTRELA = criarIcone("estrela");
	public static final Icone LARGURA = criarIcone("largura");
	public static final Icone EXCLUIR = criarIcone("excluir");
	public static final Icone CONECTA = criarIcone("connect");
	public static final Icone SUCESSO = criarIcone("sucesso");
	public static final Icone CONFIG2 = criarIcone("config2");
	public static final Icone TAG2 = criarIcone("tag_yellow");
	public static final Icone USUARIO = criarIcone("usuario");
	public static final Icone PESSOAS = criarIcone("pessoas");
	public static final Icone TARGET2 = criarIcone("target2");
	public static final Icone TARGET3 = criarIcone("target3");
	public static final Icone SALVARC = criarIcone("saveas");
	public static final Icone BAIXAR2 = criarIcone("bottom");
	public static final Icone CRIAR2 = criarIcone("create2");
	public static final Icone MODULO = criarIcone("module");
	public static final Icone CAMPOS = criarIcone("campos");
	public static final Icone RESUME = criarIcone("resume");
	public static final Icone TABELA = criarIcone("tabela");
	public static final Icone UPDATE = criarIcone("update");
	public static final Icone TARGET = criarIcone("target");
	public static final Icone CONFIG = criarIcone("config");
	public static final Icone FILTRO = criarIcone("filtro");
	public static final Icone BAIXAR = criarIcone("baixar");
	public static final Icone CURSOR = criarIcone("cursor");
	public static final Icone PANEL3 = criarIcone("panel3");
	public static final Icone TABLE2 = criarIcone("table2");
	public static final Icone PANEL4 = criarIcone("panel4");
	public static final Icone PARTIR = criarIcone("partir");
	public static final Icone FIELDS = criarIcone("fields");
	public static final Icone PANEL2 = criarIcone("panel2");
	public static final Icone REGION = criarIcone("region");
	public static final Icone BACKUP = criarIcone("backup");
	public static final Icone PESSOA = criarIcone("pessoa");
	public static final Icone CIFRAO = criarIcone("cifrao");
	public static final Icone SERVER = criarIcone("server");
	public static final Icone SALVAR = criarIcone("save1");
	public static final Icone CRIAR = criarIcone("create");
	public static final Icone FECHAR = criarIcone("close");
	public static final Icone PANEL = criarIcone("panel");
	public static final Icone ORDEM = criarIcone("ordem");
	public static final Icone START = criarIcone("start");
	public static final Icone BANCO = criarIcone("banco");
	public static final Icone ASPAS = criarIcone("aspas");
	public static final Icone ANEXO = criarIcone("anexo");
	public static final Icone PRINT = criarIcone("print");
	public static final Icone LABEL = criarIcone("label");
	public static final Icone COLAR = criarIcone("paste");
	public static final Icone TIMER = criarIcone("timer");
	public static final Icone VAZIO = criarIcone("empty");
	public static final Icone CLONAR = criarIcone("copy");
	public static final Icone EMAIL = criarIcone("email");
	public static final Icone QUEUE = criarIcone("queue");
	public static final Icone COPIA = criarIcone("copy");
	public static final Icone ABRIR = criarIcone("open");
	public static final Icone TEXTO = criarIcone("text");
	public static final Icone ICON = criarIcone("icons");
	public static final Icone COR = criarIcone("color");
	public static final Icone EDIT = criarIcone("edit");
	public static final Icone RULE = criarIcone("rule");
	public static final Icone CALC = criarIcone("calc");
	public static final Icone CUBO = criarIcone("cubo");
	public static final Icone RECT = criarIcone("rect");
	public static final Icone INFO = criarIcone("info");
	public static final Icone NOVO = criarIcone("novo");
	public static final Icone SAIR = criarIcone("sair");
	public static final Icone SETA = criarIcone("seta");
	public static final Icone SOMA = criarIcone("soma");
	public static final Icone BOSS = criarIcone("boss");
	public static final Icone PKEY = criarIcone("pkey");
	public static final Icone OLHO = criarIcone("eye");
	public static final Icone MAO = criarIcone("mao");
	public static final Icone PDF = criarIcone("pdf");
	public static final Icone TOP = criarIcone("top");
	public static final Icone VAR = criarIcone("var");
	public static final Icone KEY = criarIcone("key");
	public static final Icone TAG = criarIcone("tag");
	public static final Icone URL = criarIcone("url");
	public static final Icone BUG = criarIcone("bug");
	public static final Icone CSS = criarIcone("css");
	public static final Icone JS = criarIcone("js");

	private Icones() {
	}

	public static URL getURL(String nome) {
		return getURL(nome, "png");
	}

	private static URL getURL(String nome, String ext) {
		return Icones.class.getResource("/resources/" + nome + "." + ext);
	}

	private static Icone criarIcone(String nome) {
		URL url = getURL(nome, "png");
		return new Icone(new ImageIcon(url, nome));
	}

	private static Icone criarIconeGIF(String nome) {
		URL url = getURL(nome, "gif");
		return new Icone(new ImageIcon(url, nome));
	}

	public static Icone getIcone(String nome) {
		return MAPA_ICONES.computeIfAbsent(nome, Icones::criarIcone);
	}
}