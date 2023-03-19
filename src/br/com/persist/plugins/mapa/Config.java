package br.com.persist.plugins.mapa;

public class Config {
	private Config() {
	}

	private static int velocidadeMaximaRotacao = 10000;
	private static int velocidadeMinimaRotacao = 100;
	private static int intervaloRotacao = velocidadeMinimaRotacao;
	private static int intervaloDuploClick = 400;
	private static int diametroObjetoCentro = 60;
	private static boolean desenharObjetoCentro;
	private static boolean desenharAtributos;
	private static int distanciaCentro = 200;
	private static int diametroObjeto = 30;

	public static int getVelocidadeMaximaRotacao() {
		return velocidadeMaximaRotacao;
	}

	public static void setVelocidadeMaximaRotacao(int velocidadeMaximaRotacao) {
		Config.velocidadeMaximaRotacao = velocidadeMaximaRotacao;
	}

	public static int getVelocidadeMinimaRotacao() {
		return velocidadeMinimaRotacao;
	}

	public static void setVelocidadeMinimaRotacao(int velocidadeMinimaRotacao) {
		Config.velocidadeMinimaRotacao = velocidadeMinimaRotacao;
	}

	public static int getIntervaloRotacao() {
		return intervaloRotacao;
	}

	public static void setIntervaloRotacao(int intervaloRotacao) {
		Config.intervaloRotacao = intervaloRotacao;
	}

	public static int getIntervaloDuploClick() {
		return intervaloDuploClick;
	}

	public static void setIntervaloDuploClick(int intervaloDuploClick) {
		Config.intervaloDuploClick = intervaloDuploClick;
	}

	public static int getDiametroObjetoCentro() {
		return diametroObjetoCentro;
	}

	public static void setDiametroObjetoCentro(int diametroObjetoCentro) {
		Config.diametroObjetoCentro = diametroObjetoCentro;
	}

	public static int getDistanciaCentro() {
		return distanciaCentro;
	}

	public static void setDistanciaCentro(int distanciaCentro) {
		Config.distanciaCentro = distanciaCentro;
	}

	public static int getDiametroObjeto() {
		return diametroObjeto;
	}

	public static void setDiametroObjeto(int diametroObjeto) {
		Config.diametroObjeto = diametroObjeto;
	}

	public static boolean isDesenharObjetoCentro() {
		return desenharObjetoCentro;
	}

	public static void setDesenharObjetoCentro(boolean desenharObjetoCentro) {
		Config.desenharObjetoCentro = desenharObjetoCentro;
	}

	public static boolean isDesenharAtributos() {
		return desenharAtributos;
	}

	public static void setDesenharAtributos(boolean desenharAtributos) {
		Config.desenharAtributos = desenharAtributos;
	}
}