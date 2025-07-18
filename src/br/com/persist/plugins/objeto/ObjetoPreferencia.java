package br.com.persist.plugins.objeto;

import java.awt.Color;
import java.util.prefs.Preferences;

import br.com.persist.formulario.Formulario;

public class ObjetoPreferencia {
	private static boolean mouseWheelTitleFormInternalTopDown;
	private static boolean habilitadoEsquemaTabelaAlter;
	private static boolean moverTopoFormOrigemPesquisa;
	private static boolean habilitadoInnerJoinsObjeto;
	private static boolean pesquisaFormInternalLazy;
	private static boolean exibirTotalColunasTabela;
	private static int tipoContainerPesquisaAuto;
	private static int tipoDestaqueFormulario;
	private static boolean abrirAutoDestacado;
	private static Color corAntesTotalRecente;
	private static int intervaloPesquisaAuto;
	private static float nivelTransparencia;
	private static int intervaloComparacao;
	private static Color corTotalAtual;
	private static Color corComparaRec;
	private static boolean abrirAuto;

	private ObjetoPreferencia() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		tipoDestaqueFormulario = pref.getInt("tipo_destaque_formulario_pesquisado",
				ObjetoConstantes.TIPO_DESTAC_FORM_VISIBILIDADE);
		tipoContainerPesquisaAuto = pref.getInt("tipo_container_pesquisa_auto",
				ObjetoConstantes.TIPO_CONTAINER_FORMULARIO);
		mouseWheelTitleFormInternalTopDown = pref.getBoolean("mouseWheelTitleFormInternalTopDown", true);
		corAntesTotalRecente = new Color(pref.getInt("cor_antes_total_recente", Color.BLACK.getRGB()));
		habilitadoEsquemaTabelaAlter = pref.getBoolean("habilitado_esquema_tabela_alter", false);
		moverTopoFormOrigemPesquisa = pref.getBoolean("mover_topo_form_origem_pesquisa", false);
		habilitadoInnerJoinsObjeto = pref.getBoolean("habilitado_inner_joins_objeto", false);
		exibirTotalColunasTabela = pref.getBoolean("exibir_total_colunas_tabela", false);
		pesquisaFormInternalLazy = pref.getBoolean("pesquisa_form_internal_lazy", false);
		corTotalAtual = new Color(pref.getInt("cor_total_atual", Color.ORANGE.getRGB()));
		corComparaRec = new Color(pref.getInt("cor_compara_rec", Color.CYAN.getRGB()));
		intervaloPesquisaAuto = pref.getInt("intervalo_pesquisa_auto", 5000);
		abrirAutoDestacado = pref.getBoolean("abrir_auto_destacado", false);
		nivelTransparencia = pref.getFloat("nivel_transparencia", 1.0f);
		intervaloComparacao = pref.getInt("intervalo_comparacao", 5);
		abrirAuto = pref.getBoolean("abrir_auto", false);
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("mouseWheelTitleFormInternalTopDown", mouseWheelTitleFormInternalTopDown);
		pref.putBoolean("habilitado_esquema_tabela_alter", habilitadoEsquemaTabelaAlter);
		pref.putBoolean("mover_topo_form_origem_pesquisa", moverTopoFormOrigemPesquisa);
		pref.putBoolean("habilitado_inner_joins_objeto", habilitadoInnerJoinsObjeto);
		pref.putInt("tipo_destaque_formulario_pesquisado", tipoDestaqueFormulario);
		pref.putBoolean("pesquisa_form_internal_lazy", pesquisaFormInternalLazy);
		pref.putBoolean("exibir_total_colunas_tabela", exibirTotalColunasTabela);
		pref.putInt("tipo_container_pesquisa_auto", tipoContainerPesquisaAuto);
		pref.putInt("cor_antes_total_recente", corAntesTotalRecente.getRGB());
		pref.putInt("intervalo_pesquisa_auto", intervaloPesquisaAuto);
		pref.putBoolean("abrir_auto_destacado", abrirAutoDestacado);
		pref.putFloat("nivel_transparencia", nivelTransparencia);
		pref.putInt("intervalo_comparacao", intervaloComparacao);
		pref.putInt("cor_total_atual", corTotalAtual.getRGB());
		pref.putInt("cor_compara_rec", corComparaRec.getRGB());
		pref.putBoolean("abrir_auto", abrirAuto);
	}

	public static boolean isHabilitadoInnerJoinsObjeto() {
		return habilitadoInnerJoinsObjeto;
	}

	public static void setHabilitadoInnerJoinsObjeto(boolean habilitadoInnerJoinsObjeto) {
		ObjetoPreferencia.habilitadoInnerJoinsObjeto = habilitadoInnerJoinsObjeto;
	}

	public static boolean isHabilitadoEsquemaTabelaAlter() {
		return habilitadoEsquemaTabelaAlter;
	}

	public static void setHabilitadoEsquemaTabelaAlter(boolean habilitadoEsquemaTabelaAlter) {
		ObjetoPreferencia.habilitadoEsquemaTabelaAlter = habilitadoEsquemaTabelaAlter;
	}

	public static Color getCorAntesTotalRecente() {
		return corAntesTotalRecente;
	}

	public static void setCorAntesTotalRecente(Color corAntesTotalRecente) {
		ObjetoPreferencia.corAntesTotalRecente = corAntesTotalRecente;
	}

	public static Color getCorTotalAtual() {
		return corTotalAtual;
	}

	public static void setCorTotalAtual(Color corTotalAtual) {
		ObjetoPreferencia.corTotalAtual = corTotalAtual;
	}

	public static Color getCorComparaRec() {
		return corComparaRec;
	}

	public static void setCorComparaRec(Color corComparaRec) {
		ObjetoPreferencia.corComparaRec = corComparaRec;
	}

	public static boolean isAbrirAuto() {
		return abrirAuto;
	}

	public static void setAbrirAuto(boolean abrirAuto) {
		ObjetoPreferencia.abrirAuto = abrirAuto;
	}

	public static int getIntervaloPesquisaAuto() {
		return intervaloPesquisaAuto;
	}

	public static void setIntervaloPesquisaAuto(int intervaloPesquisaAuto) {
		ObjetoPreferencia.intervaloPesquisaAuto = intervaloPesquisaAuto;
	}

	public static int getTipoContainerPesquisaAuto() {
		return tipoContainerPesquisaAuto;
	}

	public static void setTipoContainerPesquisaAuto(int tipoContainerPesquisaAuto) {
		ObjetoPreferencia.tipoContainerPesquisaAuto = tipoContainerPesquisaAuto;
	}

	public static boolean isAbrirAutoDestacado() {
		return abrirAutoDestacado;
	}

	public static void setAbrirAutoDestacado(boolean abrirAutoDestacado) {
		ObjetoPreferencia.abrirAutoDestacado = abrirAutoDestacado;
	}

	public static int getIntervaloComparacao() {
		return intervaloComparacao;
	}

	public static void setIntervaloComparacao(int intervaloComparacao) {
		ObjetoPreferencia.intervaloComparacao = intervaloComparacao;
	}

	public static boolean isPesquisaFormInternalLazy() {
		return pesquisaFormInternalLazy;
	}

	public static void setPesquisaFormInternalLazy(boolean pesquisaFormInternalLazy) {
		ObjetoPreferencia.pesquisaFormInternalLazy = pesquisaFormInternalLazy;
	}

	public static boolean isMoverTopoFormOrigemPesquisa() {
		return moverTopoFormOrigemPesquisa;
	}

	public static void setMoverTopoFormOrigemPesquisa(boolean moverTopoFormOrigemPesquisa) {
		ObjetoPreferencia.moverTopoFormOrigemPesquisa = moverTopoFormOrigemPesquisa;
	}

	public static boolean isExibirTotalColunasTabela() {
		return exibirTotalColunasTabela;
	}

	public static void setExibirTotalColunasTabela(boolean exibirTotalColunasTabela) {
		ObjetoPreferencia.exibirTotalColunasTabela = exibirTotalColunasTabela;
	}

	public static float getNivelTransparencia() {
		return nivelTransparencia;
	}

	public static void setNivelTransparencia(float nivelTransparencia) {
		ObjetoPreferencia.nivelTransparencia = nivelTransparencia;
	}

	public static int getTipoDestaqueFormulario() {
		return tipoDestaqueFormulario;
	}

	public static void setTipoDestaqueFormulario(int tipoDestaqueFormulario) {
		ObjetoPreferencia.tipoDestaqueFormulario = tipoDestaqueFormulario;
	}

	public static boolean isMouseWheelTitleFormInternalTopDown() {
		return mouseWheelTitleFormInternalTopDown;
	}

	public static void setMouseWheelTitleFormInternalTopDown(boolean mouseWheelTitleFormInternalTopDown) {
		ObjetoPreferencia.mouseWheelTitleFormInternalTopDown = mouseWheelTitleFormInternalTopDown;
	}
}