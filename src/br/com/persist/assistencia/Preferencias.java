package br.com.persist.assistencia;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import br.com.persist.formulario.Formulario;

public class Preferencias {
	private static boolean aplicarLarguraAoAbrirArquivoObjeto;
	private static boolean aplicarAlturaAoAbrirArquivoObjeto;
	private static final String ARQ_PREF = "preferencias";
	private static boolean habilitadoInnerJoinsObjeto;
	private static boolean areaTransTabelaRegistros;
	private static boolean exibiuMensagemConnection;
	private static boolean fecharComESCFormulario;
	private static int tipoContainerPesquisaAuto;
	private static boolean fecharComESCInternal;
	private static int porcHorizontalLocalForm;
	private static boolean erroCriarConnection;
	private static boolean fecharComESCDialogo;
	private static boolean monitorPreferencial;
	private static boolean ficharioComRolagem;
	private static Color corAntesTotalRecente;
	private static boolean abrirAutoDestacado;
	private static int intervaloPesquisaAuto;
	private static int porcVerticalLocalForm;
	private static int intervaloComparacao;
	private static String formFichaDialogo;
	private static int posicaoAbaFichario;
	private static Color corFonteCopiado;
	private static boolean tituloAbaMin;
	private static boolean desconectado;
	private static Color corTotalAtual;
	private static Color corComparaRec;
	private static String formDialogo;
	private static int layoutAbertura;
	private static boolean abrirAuto;
	private static String formFicha;
	private static Color corCopiado;

	private Preferencias() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		tipoContainerPesquisaAuto = pref.getInt("tipo_container_pesquisa_auto", Constantes.TIPO_CONTAINER_FORMULARIO);
		aplicarLarguraAoAbrirArquivoObjeto = pref.getBoolean("aplicar_largura_abrir_arquivo_objeto", false);
		aplicarAlturaAoAbrirArquivoObjeto = pref.getBoolean("aplicar_altura_abrir_arquivo_objeto", false);
		corAntesTotalRecente = new Color(pref.getInt("cor_antes_total_recente", Color.BLACK.getRGB()));
		habilitadoInnerJoinsObjeto = pref.getBoolean("habilitado_inner_joins_objeto", false);
		corTotalAtual = new Color(pref.getInt("cor_total_atual", Color.ORANGE.getRGB()));
		areaTransTabelaRegistros = pref.getBoolean("area_trans_tabela_registros", false);
		corComparaRec = new Color(pref.getInt("cor_compara_rec", Color.CYAN.getRGB()));
		fecharComESCFormulario = pref.getBoolean("fechar_com_ESC_formulario", false);
		fecharComESCInternal = pref.getBoolean("fechar_com_ESC_internal", false);
		porcHorizontalLocalForm = pref.getInt("porc_horizontal_local_form", 70);
		formFichaDialogo = pref.get("form_ficha_dialogo", "FORM,FICHA,DIALOG");
		fecharComESCDialogo = pref.getBoolean("fechar_com_ESC_dialogo", false);
		intervaloPesquisaAuto = pref.getInt("intervalo_pesquisa_auto", 5000);
		monitorPreferencial = pref.getBoolean("monitor_preferencial", false);
		porcVerticalLocalForm = pref.getInt("porc_vertical_local_form", 70);
		abrirAutoDestacado = pref.getBoolean("abrir_auto_destacado", false);
		ficharioComRolagem = pref.getBoolean("fichario_com_rolagem", true);
		intervaloComparacao = pref.getInt("intervalo_comparacao", 5);
		posicaoAbaFichario = pref.getInt("posicao_aba_fichario", 1);
		tituloAbaMin = pref.getBoolean("titulo_aba_min", false);
		formDialogo = pref.get("form_dialogo", "FORM,DIALOG");
		layoutAbertura = pref.getInt("layout_abertura", 1);
		abrirAuto = pref.getBoolean("abrir_auto", false);
		formFicha = pref.get("form_ficha", "FORM,FICHA");
		if (Util.estaVazio(formFichaDialogo)) {
			formFichaDialogo = "FORM,FICHA,DIALOG";
		}
		if (Util.estaVazio(formDialogo)) {
			formDialogo = "FORM,DIALOG";
		}
		if (Util.estaVazio(formFicha)) {
			formFicha = "FORM,FICHA";
		}
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("aplicar_largura_abrir_arquivo_objeto", aplicarLarguraAoAbrirArquivoObjeto);
		pref.putBoolean("aplicar_altura_abrir_arquivo_objeto", aplicarAlturaAoAbrirArquivoObjeto);
		pref.putBoolean("habilitado_inner_joins_objeto", habilitadoInnerJoinsObjeto);
		pref.putBoolean("area_trans_tabela_registros", areaTransTabelaRegistros);
		pref.putInt("tipo_container_pesquisa_auto", tipoContainerPesquisaAuto);
		pref.putInt("cor_antes_total_recente", corAntesTotalRecente.getRGB());
		pref.putBoolean("fechar_com_ESC_formulario", fecharComESCFormulario);
		pref.putInt("porc_horizontal_local_form", porcHorizontalLocalForm);
		pref.putBoolean("fechar_com_ESC_internal", fecharComESCInternal);
		pref.putInt("porc_vertical_local_form", porcVerticalLocalForm);
		pref.putBoolean("fechar_com_ESC_dialogo", fecharComESCDialogo);
		pref.putInt("intervalo_pesquisa_auto", intervaloPesquisaAuto);
		pref.putBoolean("monitor_preferencial", monitorPreferencial);
		pref.putBoolean("abrir_auto_destacado", abrirAutoDestacado);
		pref.putBoolean("fichario_com_rolagem", ficharioComRolagem);
		pref.putInt("intervalo_comparacao", intervaloComparacao);
		pref.putInt("posicao_aba_fichario", posicaoAbaFichario);
		pref.putInt("cor_total_atual", corTotalAtual.getRGB());
		pref.putInt("cor_compara_rec", corComparaRec.getRGB());
		pref.put("form_ficha_dialogo", formFichaDialogo);
		pref.putBoolean("titulo_aba_min", tituloAbaMin);
		pref.putInt("layout_abertura", layoutAbertura);
		pref.putBoolean("abrir_auto", abrirAuto);
		pref.put("form_dialogo", formDialogo);
		pref.put("form_ficha", formFicha);
	}

	public static void inicializar() {
		String can = Mensagens.getString("label.cancelar");
		String sim = Mensagens.getString("label.sim");
		String nao = Mensagens.getString("label.nao");
		UIManager.put("OptionPane.cancelButtonText", can);
		UIManager.put("OptionPane.yesButtonText", sim);
		UIManager.put("OptionPane.noButtonText", nao);
	}

	public static void exportar() throws IOException, BackingStoreException {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.exportSubtree(new FileOutputStream(ARQ_PREF));
	}

	public static void importar() throws IOException, InvalidPreferencesFormatException {
		Preferences.importPreferences(new FileInputStream(ARQ_PREF));
	}

	public static boolean getBoolean(String chave) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		return pref.getBoolean(chave, false);
	}

	public static String getString(String chave) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		return pref.get(chave, "");
	}

	public static void setBoolean(String chave, boolean valor) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean(chave, valor);
	}

	public static void setString(String chave, String valor) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.put(chave, valor == null ? "" : valor);
	}

	public static boolean isAreaTransTabelaRegistros() {
		return areaTransTabelaRegistros;
	}

	public static void setAreaTransTabelaRegistros(boolean areaTransTabelaRegistros) {
		Preferencias.areaTransTabelaRegistros = areaTransTabelaRegistros;
	}

	public static boolean isFicharioComRolagem() {
		return ficharioComRolagem;
	}

	public static void setFicharioComRolagem(boolean ficharioComRolagem) {
		Preferencias.ficharioComRolagem = ficharioComRolagem;
	}

	public static boolean isAbrirAutoDestacado() {
		return abrirAutoDestacado;
	}

	public static void setAbrirAutoDestacado(boolean abrirAutoDestacado) {
		Preferencias.abrirAutoDestacado = abrirAutoDestacado;
	}

	public static int getPosicaoAbaFichario() {
		return posicaoAbaFichario;
	}

	public static void setPosicaoAbaFichario(int posicaoAbaFichario) {
		Preferencias.posicaoAbaFichario = posicaoAbaFichario;
	}

	public static boolean isAbrirAuto() {
		return abrirAuto;
	}

	public static void setAbrirAuto(boolean abrirAuto) {
		Preferencias.abrirAuto = abrirAuto;
	}

	public static int getIntervaloPesquisaAuto() {
		return intervaloPesquisaAuto;
	}

	public static void setIntervaloPesquisaAuto(int intervaloPesquisaAuto) {
		Preferencias.intervaloPesquisaAuto = intervaloPesquisaAuto;
	}

	public static int getTipoContainerPesquisaAuto() {
		return tipoContainerPesquisaAuto;
	}

	public static void setTipoContainerPesquisaAuto(int tipoContainerPesquisaAuto) {
		Preferencias.tipoContainerPesquisaAuto = tipoContainerPesquisaAuto;
	}

	public static boolean isFecharComESCFormulario() {
		return fecharComESCFormulario;
	}

	public static void setFecharComESCFormulario(boolean fecharComESCFormulario) {
		Preferencias.fecharComESCFormulario = fecharComESCFormulario;
	}

	public static boolean isFecharComESCInternal() {
		return fecharComESCInternal;
	}

	public static void setFecharComESCInternal(boolean fecharComESCInternal) {
		Preferencias.fecharComESCInternal = fecharComESCInternal;
	}

	public static boolean isFecharComESCDialogo() {
		return fecharComESCDialogo;
	}

	public static void setFecharComESCDialogo(boolean fecharComESCDialogo) {
		Preferencias.fecharComESCDialogo = fecharComESCDialogo;
	}

	public static String getFormFichaDialogo() {
		return formFichaDialogo;
	}

	public static void setFormFichaDialogo(String formFichaDialogo) {
		Preferencias.formFichaDialogo = formFichaDialogo;
	}

	public static String getFormDialogo() {
		return formDialogo;
	}

	public static void setFormDialogo(String formDialogo) {
		Preferencias.formDialogo = formDialogo;
	}

	public static String getFormFicha() {
		return formFicha;
	}

	public static void setFormFicha(String formFicha) {
		Preferencias.formFicha = formFicha;
	}

	public static int getLayoutAbertura() {
		return layoutAbertura;
	}

	public static void setLayoutAbertura(int layoutAbertura) {
		Preferencias.layoutAbertura = layoutAbertura;
	}

	public static int getIntervaloComparacao() {
		return intervaloComparacao;
	}

	public static void setIntervaloComparacao(int intervaloComparacao) {
		Preferencias.intervaloComparacao = intervaloComparacao;
	}

	public static Color getCorAntesTotalRecente() {
		return corAntesTotalRecente;
	}

	public static void setCorAntesTotalRecente(Color corAntesTotalRecente) {
		Preferencias.corAntesTotalRecente = corAntesTotalRecente;
	}

	public static Color getCorTotalAtual() {
		return corTotalAtual;
	}

	public static void setCorTotalAtual(Color corTotalAtual) {
		Preferencias.corTotalAtual = corTotalAtual;
	}

	public static Color getCorComparaRec() {
		return corComparaRec;
	}

	public static void setCorComparaRec(Color corComparaRec) {
		Preferencias.corComparaRec = corComparaRec;
	}

	public static boolean isTituloAbaMin() {
		return tituloAbaMin;
	}

	public static void setTituloAbaMin(boolean tituloAbaMin) {
		Preferencias.tituloAbaMin = tituloAbaMin;
	}

	public static boolean isErroCriarConnection() {
		return erroCriarConnection;
	}

	public static void setErroCriarConnection(boolean erroCriarConnection) {
		Preferencias.erroCriarConnection = erroCriarConnection;
	}

	public static boolean isExibiuMensagemConnection() {
		return exibiuMensagemConnection;
	}

	public static void setExibiuMensagemConnection(boolean exibiuMensagemConnection) {
		Preferencias.exibiuMensagemConnection = exibiuMensagemConnection;
	}

	public static int getPorcHorizontalLocalForm() {
		return porcHorizontalLocalForm;
	}

	public static void setPorcHorizontalLocalForm(int porcHorizontalLocalForm) {
		Preferencias.porcHorizontalLocalForm = porcHorizontalLocalForm;
	}

	public static int getPorcVerticalLocalForm() {
		return porcVerticalLocalForm;
	}

	public static void setPorcVerticalLocalForm(int porcVerticalLocalForm) {
		Preferencias.porcVerticalLocalForm = porcVerticalLocalForm;
	}

	public static boolean isAplicarLarguraAoAbrirArquivoObjeto() {
		return aplicarLarguraAoAbrirArquivoObjeto;
	}

	public static void setAplicarLarguraAoAbrirArquivoObjeto(boolean aplicarLarguraAoAbrirArquivoObjeto) {
		Preferencias.aplicarLarguraAoAbrirArquivoObjeto = aplicarLarguraAoAbrirArquivoObjeto;
	}

	public static boolean isAplicarAlturaAoAbrirArquivoObjeto() {
		return aplicarAlturaAoAbrirArquivoObjeto;
	}

	public static void setAplicarAlturaAoAbrirArquivoObjeto(boolean aplicarAlturaAoAbrirArquivoObjeto) {
		Preferencias.aplicarAlturaAoAbrirArquivoObjeto = aplicarAlturaAoAbrirArquivoObjeto;
	}

	public static Color getCorFonteCopiado() {
		return corFonteCopiado;
	}

	public static void setCorFonteCopiado(Color corFonteCopiado) {
		Preferencias.corFonteCopiado = corFonteCopiado;
	}

	public static Color getCorCopiado() {
		return corCopiado;
	}

	public static void setCorCopiado(Color corCopiado) {
		Preferencias.corCopiado = corCopiado;
	}

	public static boolean isDesconectado() {
		return desconectado;
	}

	public static void setDesconectado(boolean desconectado) {
		Preferencias.desconectado = desconectado;
	}

	public static String getArqPref() {
		return ARQ_PREF;
	}

	public static boolean isHabilitadoInnerJoinsObjeto() {
		return habilitadoInnerJoinsObjeto;
	}

	public static void setHabilitadoInnerJoinsObjeto(boolean habilitadoInnerJoinsObjeto) {
		Preferencias.habilitadoInnerJoinsObjeto = habilitadoInnerJoinsObjeto;
	}

	public static boolean isMonitorPreferencial() {
		return monitorPreferencial;
	}

	public static void setMonitorPreferencial(boolean monitorPreferencial) {
		Preferencias.monitorPreferencial = monitorPreferencial;
	}
}