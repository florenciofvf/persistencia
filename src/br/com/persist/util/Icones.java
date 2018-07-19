package br.com.persist.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icones {
	private static final Map<String, Icon> MAPA_ICONES = new HashMap<>();

	public static final Icon DESC_NUMERO = criarImagem("desc_numero");
	public static final Icon ASC_NUMERO = criarImagem("asc_numero");
	public static final Icon DESC_TEXTO = criarImagem("desc_texto");
	public static final Icon ASC_TEXTO = criarImagem("asc_texto");
	public static final Icon ATUALIZAR = criarImagem("refresh");
	public static final Icon EXCLUIR2 = criarImagem("excluir2");
	public static final Icon UM_PIXEL = criarImagem("um_pixel");
	public static final Icon SINCRONIZAR = criarImagem("sync");
	public static final Icon EXCLUIR = criarImagem("excluir");
	public static final Icon SUCESSO = criarImagem("sucesso");
	public static final Icon SALVARC = criarImagem("saveas");
	public static final Icon CRIAR2 = criarImagem("create2");
	public static final Icon CONFIG = criarImagem("config");
	public static final Icon FILTRO = criarImagem("filtro");
	public static final Icon BAIXAR = criarImagem("baixar");
	public static final Icon SALVAR = criarImagem("save1");
	public static final Icon CRIAR = criarImagem("create");
	public static final Icon ORDEM = criarImagem("ordem");
	public static final Icon BANCO = criarImagem("banco");
	public static final Icon LABEL = criarImagem("label");
	public static final Icon ABRIR = criarImagem("open");
	public static final Icon NOVO = criarImagem("novo");
	public static final Icon SAIR = criarImagem("sair");
	public static final Icon OLHO = criarImagem("eye");
	public static final Icon MAO = criarImagem("mao");

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