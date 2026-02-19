package br.com.persist.assistencia;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icones {
	private static final Map<String, Icon> MAPA_ICONES = new HashMap<>();
	public static final Icon ALINHA_ESQUERDO = criarImagem("alinha_esquerdo");
	public static final Icon ALINHA_DIREITO = criarImagem("alinha_direito");
	public static final Icon BOLA_VERMELHA = criarImagem("bola_vermelha");
	public static final Icon BANCO_DESCONECTA = criarImagem("dbdisconn");
	public static final Icon BOLA_AMARELA = criarImagem("bola_amarela");
	public static final Icon DESC_NUMERO = criarImagem("desc_numero");
	public static final Icon BOLA_VERDE = criarImagem("bola_verde");
	public static final Icon HORIZONTAL = criarImagem("horizontal");
	public static final Icon ASC_NUMERO = criarImagem("asc_numero");
	public static final Icon DESC_TEXTO = criarImagem("desc_texto");
	public static final Icon HIERARQUIA = criarImagem("hierarchy");
	public static final Icon REFERENCIA = criarImagem("reference");
	public static final Icon ASC_TEXTO = criarImagem("asc_texto");
	public static final Icon CENTRALIZAR = criarImagem("section");
	public static final Icon SEPARADOR = criarImagem("separador");
	public static final Icon EXCEPTION = criarImagem("exception");
	public static final Icon ARRASTAR2 = criarImagem("synonym2");
	public static final Icon GLOBO_GIF = criarImagemGIF("globo");
	public static final Icon EXECUTAR = criarImagem("executar");
	public static final Icon EXPANDIR = criarImagem("expandir");
	public static final Icon VERTICAL = criarImagem("vertical");
	public static final Icon FAVORITO = criarImagem("favorito");
	public static final Icon ATUALIZAR = criarImagem("refresh");
	public static final Icon UM_PIXEL = criarImagem("um_pixel");
	public static final Icon FRAGMENTO = criarImagem("feature");
	public static final Icon SINCRONIZAR = criarImagem("sync");
	public static final Icon ELEMENTO = criarImagem("element");
	public static final Icon ARRASTAR = criarImagem("synonym");
	public static final Icon ESTRELA = criarImagem("estrela");
	public static final Icon LARGURA = criarImagem("largura");
	public static final Icon EXCLUIR = criarImagem("excluir");
	public static final Icon CONECTA = criarImagem("connect");
	public static final Icon SUCESSO = criarImagem("sucesso");
	public static final Icon CONFIG2 = criarImagem("config2");
	public static final Icon TAG2 = criarImagem("tag_yellow");
	public static final Icon SALVARC = criarImagem("saveas");
	public static final Icon BAIXAR2 = criarImagem("bottom");
	public static final Icon CRIAR2 = criarImagem("create2");
	public static final Icon MODULO = criarImagem("module");
	public static final Icon CAMPOS = criarImagem("campos");
	public static final Icon RESUME = criarImagem("resume");
	public static final Icon TABELA = criarImagem("tabela");
	public static final Icon UPDATE = criarImagem("update");
	public static final Icon TARGET = criarImagem("target");
	public static final Icon CONFIG = criarImagem("config");
	public static final Icon FILTRO = criarImagem("filtro");
	public static final Icon BAIXAR = criarImagem("baixar");
	public static final Icon CURSOR = criarImagem("cursor");
	public static final Icon PANEL3 = criarImagem("panel3");
	public static final Icon TABLE2 = criarImagem("table2");
	public static final Icon PANEL4 = criarImagem("panel4");
	public static final Icon PARTIR = criarImagem("partir");
	public static final Icon FIELDS = criarImagem("fields");
	public static final Icon PANEL2 = criarImagem("panel2");
	public static final Icon REGION = criarImagem("region");
	public static final Icon BACKUP = criarImagem("backup");
	public static final Icon PESSOA = criarImagem("pessoa");
	public static final Icon SALVAR = criarImagem("save1");
	public static final Icon CRIAR = criarImagem("create");
	public static final Icon FECHAR = criarImagem("close");
	public static final Icon PANEL = criarImagem("panel");
	public static final Icon ORDEM = criarImagem("ordem");
	public static final Icon BANCO = criarImagem("banco");
	public static final Icon ASPAS = criarImagem("aspas");
	public static final Icon ANEXO = criarImagem("anexo");
	public static final Icon PRINT = criarImagem("print");
	public static final Icon LABEL = criarImagem("label");
	public static final Icon COLAR = criarImagem("paste");
	public static final Icon TIMER = criarImagem("timer");
	public static final Icon VAZIO = criarImagem("empty");
	public static final Icon CLONAR = criarImagem("copy");
	public static final Icon COPIA = criarImagem("copy");
	public static final Icon ABRIR = criarImagem("open");
	public static final Icon TEXTO = criarImagem("text");
	public static final Icon ICON = criarImagem("icons");
	public static final Icon COR = criarImagem("color");
	public static final Icon EDIT = criarImagem("edit");
	public static final Icon RULE = criarImagem("rule");
	public static final Icon CALC = criarImagem("calc");
	public static final Icon CUBO = criarImagem("cubo");
	public static final Icon RECT = criarImagem("rect");
	public static final Icon INFO = criarImagem("info");
	public static final Icon NOVO = criarImagem("novo");
	public static final Icon SAIR = criarImagem("sair");
	public static final Icon SETA = criarImagem("seta");
	public static final Icon SOMA = criarImagem("soma");
	public static final Icon PKEY = criarImagem("pkey");
	public static final Icon OLHO = criarImagem("eye");
	public static final Icon MAO = criarImagem("mao");
	public static final Icon PDF = criarImagem("pdf");
	public static final Icon TOP = criarImagem("top");
	public static final Icon VAR = criarImagem("var");
	public static final Icon KEY = criarImagem("key");
	public static final Icon TAG = criarImagem("tag");
	public static final Icon URL = criarImagem("url");
	public static final Icon JS = criarImagem("js");

	private Icones() {
	}

	public static URL getURL(String nome) {
		return getURL(nome, "png");
	}

	private static URL getURL(String nome, String ext) {
		return Icones.class.getResource("/resources/" + nome + "." + ext);
	}

	private static ImageIcon criarImagem(String nome) {
		URL url = getURL(nome, "png");
		return new ImageIcon(url, nome);
	}

	private static ImageIcon criarImagemGIF(String nome) {
		URL url = getURL(nome, "gif");
		return new ImageIcon(url, nome);
	}

	public static Icon getIcon(String nome) {
		return MAPA_ICONES.computeIfAbsent(nome, Icones::criarImagem);
	}
}