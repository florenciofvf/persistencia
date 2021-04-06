package br.com.persist.assistencia;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import br.com.persist.formulario.Formulario;

public class Preferencias {
	private static final List<Class<?>> outrasPreferencias = new ArrayList<>();
	private static boolean aplicarLarguraAoAbrirArquivoObjeto;
	private static boolean aplicarAlturaAoAbrirArquivoObjeto;
	private static final String ARQ_PREF = "preferencias";
	private static final Logger LOG = Logger.getGlobal();
	private static boolean exibiuMensagemConnection;
	private static boolean fecharComESCFormulario;
	private static boolean fecharComESCInternal;
	private static int porcHorizontalLocalForm;
	private static boolean erroCriarConnection;
	private static boolean fecharComESCDialogo;
	private static boolean monitorPreferencial;
	private static boolean ficharioComRolagem;
	private static int porcVerticalLocalForm;
	private static String formFichaDialogo;
	private static int posicaoAbaFichario;
	private static Color corFonteCopiado;
	private static boolean tituloAbaMin;
	private static boolean desconectado;
	private static String formDialogo;
	private static String formFicha;
	private static Color corCopiado;

	private Preferencias() {
	}

	public static void addOutraPreferencia(Class<?> classe) {
		if (classe != null) {
			outrasPreferencias.add(classe);
		}
	}

	private static void abrirOutras() {
		try {
			for (Class<?> klass : outrasPreferencias) {
				Method method = klass.getDeclaredMethod("abrir");
				method.invoke(null);
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, Constantes.ERRO, ex);
		}
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		aplicarLarguraAoAbrirArquivoObjeto = pref.getBoolean("aplicar_largura_abrir_arquivo_objeto", false);
		aplicarAlturaAoAbrirArquivoObjeto = pref.getBoolean("aplicar_altura_abrir_arquivo_objeto", false);
		fecharComESCFormulario = pref.getBoolean("fechar_com_ESC_formulario", false);
		fecharComESCInternal = pref.getBoolean("fechar_com_ESC_internal", false);
		porcHorizontalLocalForm = pref.getInt("porc_horizontal_local_form", 70);
		formFichaDialogo = pref.get("form_ficha_dialogo", "FORM,FICHA,DIALOG");
		fecharComESCDialogo = pref.getBoolean("fechar_com_ESC_dialogo", false);
		monitorPreferencial = pref.getBoolean("monitor_preferencial", false);
		porcVerticalLocalForm = pref.getInt("porc_vertical_local_form", 70);
		ficharioComRolagem = pref.getBoolean("fichario_com_rolagem", true);
		posicaoAbaFichario = pref.getInt("posicao_aba_fichario", 1);
		tituloAbaMin = pref.getBoolean("titulo_aba_min", false);
		formDialogo = pref.get("form_dialogo", "FORM,DIALOG");
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
		abrirOutras();
	}

	private static void salvarOutras() {
		try {
			for (Class<?> klass : outrasPreferencias) {
				Method method = klass.getDeclaredMethod("salvar");
				method.invoke(null);
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, Constantes.ERRO, ex);
		}
	}

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("aplicar_largura_abrir_arquivo_objeto", aplicarLarguraAoAbrirArquivoObjeto);
		pref.putBoolean("aplicar_altura_abrir_arquivo_objeto", aplicarAlturaAoAbrirArquivoObjeto);
		pref.putBoolean("fechar_com_ESC_formulario", fecharComESCFormulario);
		pref.putInt("porc_horizontal_local_form", porcHorizontalLocalForm);
		pref.putBoolean("fechar_com_ESC_internal", fecharComESCInternal);
		pref.putInt("porc_vertical_local_form", porcVerticalLocalForm);
		pref.putBoolean("fechar_com_ESC_dialogo", fecharComESCDialogo);
		pref.putBoolean("monitor_preferencial", monitorPreferencial);
		pref.putBoolean("fichario_com_rolagem", ficharioComRolagem);
		pref.putInt("posicao_aba_fichario", posicaoAbaFichario);
		pref.put("form_ficha_dialogo", formFichaDialogo);
		pref.putBoolean("titulo_aba_min", tituloAbaMin);
		pref.put("form_dialogo", formDialogo);
		pref.put("form_ficha", formFicha);
		salvarOutras();
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

	public static boolean isFicharioComRolagem() {
		return ficharioComRolagem;
	}

	public static void setFicharioComRolagem(boolean ficharioComRolagem) {
		Preferencias.ficharioComRolagem = ficharioComRolagem;
	}

	public static int getPosicaoAbaFichario() {
		return posicaoAbaFichario;
	}

	public static void setPosicaoAbaFichario(int posicaoAbaFichario) {
		Preferencias.posicaoAbaFichario = posicaoAbaFichario;
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

	public static boolean isMonitorPreferencial() {
		return monitorPreferencial;
	}

	public static void setMonitorPreferencial(boolean monitorPreferencial) {
		Preferencias.monitorPreferencial = monitorPreferencial;
	}
}