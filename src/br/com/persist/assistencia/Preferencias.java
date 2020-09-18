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
	private static boolean execAposCopiarColunaConcatenado;
	private static boolean execAposBaixarParaComplemento;
	private static boolean areaTransTabelaRegistros;
	private static boolean copiarNomeColunaListener;
	private static boolean exibiuMensagemConnection;
	private static String arqPref = "preferencias";
	private static int tipoContainerPesquisaAuto;
	private static int porcHorizontalLocalForm;
	private static boolean erroCriarConnection;
	private static boolean abortarFecharComESC;
	private static boolean ficharioComRolagem;
	private static Color corAntesTotalRecente;
	private static boolean abrirAutoDestacado;
	private static int intervaloPesquisaAuto;
	private static int porcVerticalLocalForm;
	private static boolean fecharAposSoltar;
	private static int intervaloComparacao;
	private static String formFichaDialogo;
	private static int posicaoAbaFichario;
	private static boolean nomearArrasto;
	private static boolean tituloAbaMin;
	private static Color corTotalAtual;
	private static Color corComparaRec;
	private static String formDialogo;
	private static int layoutAbertura;
	private static boolean abrirAuto;
	private static String formFicha;

	private Preferencias() {
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);

		tipoContainerPesquisaAuto = pref.getInt("tipo_container_pesquisa_auto", Constantes.TIPO_CONTAINER_FORMULARIO);
		aplicarLarguraAoAbrirArquivoObjeto = pref.getBoolean("aplicar_largura_abrir_arquivo_objeto", false);
		aplicarAlturaAoAbrirArquivoObjeto = pref.getBoolean("aplicar_altura_abrir_arquivo_objeto", false);
		execAposCopiarColunaConcatenado = pref.getBoolean("exec_apos_copiar_coluna_concatenado", false);
		corAntesTotalRecente = new Color(pref.getInt("cor_antes_total_recente", Color.BLACK.getRGB()));
		execAposBaixarParaComplemento = pref.getBoolean("exec_apos_baixar_para_complemento", false);
		corTotalAtual = new Color(pref.getInt("cor_total_atual", Color.ORANGE.getRGB()));
		areaTransTabelaRegistros = pref.getBoolean("area_trans_tabela_registros", false);
		copiarNomeColunaListener = pref.getBoolean("copiar_nome_coluna_listener", false);
		corComparaRec = new Color(pref.getInt("cor_compara_rec", Color.CYAN.getRGB()));
		porcHorizontalLocalForm = pref.getInt("porc_horizontal_local_form", 70);
		formFichaDialogo = pref.get("form_ficha_dialogo", "FORM,FICHA,DIALOG");
		abortarFecharComESC = pref.getBoolean("abortar_fechar_com_ESC", false);
		intervaloPesquisaAuto = pref.getInt("intervalo_pesquisa_auto", 5000);
		porcVerticalLocalForm = pref.getInt("porc_vertical_local_form", 70);
		abrirAutoDestacado = pref.getBoolean("abrir_auto_destacado", false);
		ficharioComRolagem = pref.getBoolean("fichario_com_rolagem", true);
		fecharAposSoltar = pref.getBoolean("fechar_apos_soltar", true);
		intervaloComparacao = pref.getInt("intervalo_comparacao", 5);
		posicaoAbaFichario = pref.getInt("posicao_aba_fichario", 1);
		nomearArrasto = pref.getBoolean("nomear_arrasto", false);
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
		pref.putBoolean("exec_apos_copiar_coluna_concatenado", execAposCopiarColunaConcatenado);
		pref.putBoolean("exec_apos_baixar_para_complemento", execAposBaixarParaComplemento);
		pref.putBoolean("area_trans_tabela_registros", areaTransTabelaRegistros);
		pref.putBoolean("copiar_nome_coluna_listener", copiarNomeColunaListener);
		pref.putInt("tipo_container_pesquisa_auto", tipoContainerPesquisaAuto);
		pref.putInt("cor_antes_total_recente", corAntesTotalRecente.getRGB());
		pref.putInt("porc_horizontal_local_form", porcHorizontalLocalForm);
		pref.putInt("porc_vertical_local_form", porcVerticalLocalForm);
		pref.putBoolean("abortar_fechar_com_ESC", abortarFecharComESC);
		pref.putInt("intervalo_pesquisa_auto", intervaloPesquisaAuto);
		pref.putBoolean("abrir_auto_destacado", abrirAutoDestacado);
		pref.putBoolean("fichario_com_rolagem", ficharioComRolagem);
		pref.putInt("intervalo_comparacao", intervaloComparacao);
		pref.putBoolean("fechar_apos_soltar", fecharAposSoltar);
		pref.putInt("posicao_aba_fichario", posicaoAbaFichario);
		pref.putInt("cor_total_atual", corTotalAtual.getRGB());
		pref.putInt("cor_compara_rec", corComparaRec.getRGB());
		pref.putBoolean("nomear_arrasto", nomearArrasto);
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
		pref.exportSubtree(new FileOutputStream(arqPref));
	}

	public static void importar() throws IOException, InvalidPreferencesFormatException {
		Preferences.importPreferences(new FileInputStream(arqPref));
	}

	public static boolean getBoolean(String chave) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		return pref.getBoolean(chave, false);
	}

	public static void setBoolean(String chave, boolean valor) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean(chave, valor);
	}

	public static boolean isAreaTransTabelaRegistros() {
		return areaTransTabelaRegistros;
	}

	public static void setAreaTransTabelaRegistros(boolean areaTransTabelaRegistros) {
		Preferencias.areaTransTabelaRegistros = areaTransTabelaRegistros;
	}

	public static boolean isCopiarNomeColunaListener() {
		return copiarNomeColunaListener;
	}

	public static void setCopiarNomeColunaListener(boolean copiarNomeColunaListener) {
		Preferencias.copiarNomeColunaListener = copiarNomeColunaListener;
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

	public static boolean isFecharAposSoltar() {
		return fecharAposSoltar;
	}

	public static void setFecharAposSoltar(boolean fecharAposSoltar) {
		Preferencias.fecharAposSoltar = fecharAposSoltar;
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

	public static boolean isAbortarFecharComESC() {
		return abortarFecharComESC;
	}

	public static void setAbortarFecharComESC(boolean abortarFecharComESC) {
		Preferencias.abortarFecharComESC = abortarFecharComESC;
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

	public static boolean isNomearArrasto() {
		return nomearArrasto;
	}

	public static void setNomearArrasto(boolean nomearArrasto) {
		Preferencias.nomearArrasto = nomearArrasto;
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

	public static boolean isExecAposCopiarColunaConcatenado() {
		return execAposCopiarColunaConcatenado;
	}

	public static void setExecAposCopiarColunaConcatenado(boolean execAposCopiarColunaConcatenado) {
		Preferencias.execAposCopiarColunaConcatenado = execAposCopiarColunaConcatenado;
	}

	public static boolean isExecAposBaixarParaComplemento() {
		return execAposBaixarParaComplemento;
	}

	public static void setExecAposBaixarParaComplemento(boolean execAposBaixarParaComplemento) {
		Preferencias.execAposBaixarParaComplemento = execAposBaixarParaComplemento;
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
}