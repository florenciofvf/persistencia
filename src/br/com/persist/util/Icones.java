package br.com.persist.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icones {
	private static final Map<String, Icon> MAPA_ICONES = new HashMap<>();

	public static final Icon ALINHA_ESQUERDO = criarImagem("alinha_esquerdo");
	public static final Icon ALINHA_DIREITO = criarImagem("alinha_direito");
	public static final Icon BANCO_DESCONECTA = criarImagem("dbdisconn");
	public static final Icon DESC_NUMERO = criarImagem("desc_numero");
	public static final Icon HORIZONTAL = criarImagem("horizontal");
	public static final Icon ASC_NUMERO = criarImagem("asc_numero");
	public static final Icon DESC_TEXTO = criarImagem("desc_texto");
	public static final Icon ASC_TEXTO = criarImagem("asc_texto");
	public static final Icon CENTRALIZAR = criarImagem("section");
	public static final Icon VERTICAL = criarImagem("vertical");
	public static final Icon ATUALIZAR = criarImagem("refresh");
	public static final Icon UM_PIXEL = criarImagem("um_pixel");
	public static final Icon FRAGMENTO = criarImagem("feature");
	public static final Icon SINCRONIZAR = criarImagem("sync");
	public static final Icon ARRASTAR = criarImagem("synonym");
	public static final Icon LARGURA = criarImagem("largura");
	public static final Icon EXCLUIR = criarImagem("excluir");
	public static final Icon CONECTA = criarImagem("connect");
	public static final Icon SUCESSO = criarImagem("sucesso");
	public static final Icon SALVARC = criarImagem("saveas");
	public static final Icon BAIXAR2 = criarImagem("bottom");
	public static final Icon TABELA = criarImagem("tabela");
	public static final Icon UPDATE = criarImagem("update");
	public static final Icon CONFIG = criarImagem("config");
	public static final Icon FILTRO = criarImagem("filtro");
	public static final Icon BAIXAR = criarImagem("baixar");
	public static final Icon CURSOR = criarImagem("cursor");
	public static final Icon PANEL3 = criarImagem("panel3");
	public static final Icon SALVAR = criarImagem("save1");
	public static final Icon CRIAR = criarImagem("create");
	public static final Icon PANEL = criarImagem("panel");
	public static final Icon CUBO2 = criarImagem("cubo2");
	public static final Icon ORDEM = criarImagem("ordem");
	public static final Icon BANCO = criarImagem("banco");
	public static final Icon ASPAS = criarImagem("aspas");
	public static final Icon LABEL = criarImagem("label");
	public static final Icon COLAR = criarImagem("paste");
	public static final Icon VAZIO = criarImagem("empty");
	public static final Icon COPIA = criarImagem("copy");
	public static final Icon ABRIR = criarImagem("open");
	public static final Icon TEXTO = criarImagem("text");
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
	public static final Icon TOP = criarImagem("top");
	public static final Icon VAR = criarImagem("var");
	public static final Icon KEY = criarImagem("key");

	private static ImageIcon criarImagem(String nome) {
		try {
			URL url = Icones.class.getResource("/resources/" + nome + ".png");
			return new ImageIcon(url, nome);
		} catch (Exception e) {
			throw new IllegalStateException("Erro imagem! " + nome);
		}
	}

	public static Icon getIcon(String nome) {
		Icon icon = MAPA_ICONES.get(nome);

		if (icon == null) {
			icon = criarImagem(nome);
			MAPA_ICONES.put(nome, icon);
		}

		return icon;
	}
}