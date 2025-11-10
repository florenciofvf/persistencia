package br.com.persist.assistencia;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import br.com.persist.componente.TextEditor;
import br.com.persist.formulario.Formulario;

public class Preferencias {
	private static final List<Class<?>> outrasPreferencias = new ArrayList<>();
	private static final String DIMENSAO_MENSAGEM = "dimensao_mensagem";
	private static final String EDITOR_FONT_STYLE = "editor_font_style";
	private static final String EDITOR_FONT_NAME = "editor_font_name";
	private static final String EDITOR_FONT_SIZE = "editor_font_size";
	private static boolean aplicarLarguraAoAbrirArquivoObjeto;
	private static boolean aplicarAlturaAoAbrirArquivoObjeto;
	private static final String ARQ_PREF = "preferencias";
	private static final Logger LOG = Logger.getGlobal();
	private static final String LA_500_300 = "500,300";
	private static boolean exibirTotalBytesClonados;
	private static boolean exibiuMensagemConnection;
	private static boolean desenharEspacoRetornoTab;
	private static boolean fecharComESCFormulario;
	private static boolean abrirFormularioDireita;
	private static boolean abrirFormularioAbaixo;
	private static boolean fecharComESCInternal;
	private static int porcHorizontalLocalForm;
	private static boolean erroCriarConnection;
	private static boolean fecharComESCDialogo;
	private static boolean monitorPreferencial;
	private static boolean ficharioComRolagem;
	private static int porcVerticalLocalForm;
	private static String formFichaDialogo;
	private static String dimensaoMensagem;
	private static String getObjetosBanco;
	private static int posicaoAbaFichario;
	private static Color corFonteCopiado;
	private static boolean tituloAbaMin;
	private static boolean desconectado;
	private static int totalConfirmacao;
	private static String formDialogo;
	private static Color corCopiado;

	private Preferencias() {
	}

	public static void addOutraPreferencia(Class<?> classe) {
		if (classe != null) {
			outrasPreferencias.add(classe);
		}
	}

	public static void abrir() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		aplicarLarguraAoAbrirArquivoObjeto = pref.getBoolean("aplicar_largura_abrir_arquivo_objeto", false);
		aplicarAlturaAoAbrirArquivoObjeto = pref.getBoolean("aplicar_altura_abrir_arquivo_objeto", false);
		desenharEspacoRetornoTab = pref.getBoolean("desenhar_espaco_retorno_tab", false);
		TextEditor.setPaintERT(desenharEspacoRetornoTab);
		fecharComESCFormulario = pref.getBoolean("fechar_com_ESC_formulario", false);
		posicaoAbaFichario = pref.getInt("posicao_aba_fichario", SwingConstants.TOP);
		abrirFormularioDireita = pref.getBoolean("abrir_formulario_direita", false);
		abrirFormularioAbaixo = pref.getBoolean("abrir_formulario_abaixo", false);
		fecharComESCInternal = pref.getBoolean("fechar_com_ESC_internal", false);
		porcHorizontalLocalForm = pref.getInt("porc_horizontal_local_form", 70);
		formFichaDialogo = pref.get("form_ficha_dialogo", "FORM,FICHA,DIALOG");
		fecharComESCDialogo = pref.getBoolean("fechar_com_ESC_dialogo", false);
		monitorPreferencial = pref.getBoolean("monitor_preferencial", false);
		porcVerticalLocalForm = pref.getInt("porc_vertical_local_form", 70);
		ficharioComRolagem = pref.getBoolean("fichario_com_rolagem", true);
		dimensaoMensagem = pref.get(DIMENSAO_MENSAGEM, LA_500_300);
		getObjetosBanco = pref.get("get_objetos_banco", "TABLE");
		tituloAbaMin = pref.getBoolean("titulo_aba_min", false);
		totalConfirmacao = pref.getInt("total_confirmacao", 1);
		formDialogo = pref.get("form_dialogo", "FORM,DIALOG");
		if (Util.isEmpty(formFichaDialogo)) {
			formFichaDialogo = "FORM,FICHA,DIALOG";
		}
		if (Util.isEmpty(dimensaoMensagem)) {
			dimensaoMensagem = LA_500_300;
		}
		if (Util.isEmpty(getObjetosBanco)) {
			getObjetosBanco = "TABLE";
		}
		if (Util.isEmpty(formDialogo)) {
			formDialogo = "FORM,DIALOG";
		}
		abrirOutras();
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

	public static void salvar() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean("aplicar_largura_abrir_arquivo_objeto", aplicarLarguraAoAbrirArquivoObjeto);
		pref.putBoolean("aplicar_altura_abrir_arquivo_objeto", aplicarAlturaAoAbrirArquivoObjeto);
		pref.putBoolean("desenhar_espaco_retorno_tab", desenharEspacoRetornoTab);
		pref.putBoolean("fechar_com_ESC_formulario", fecharComESCFormulario);
		pref.putBoolean("abrir_formulario_direita", abrirFormularioDireita);
		pref.putInt("porc_horizontal_local_form", porcHorizontalLocalForm);
		pref.putBoolean("abrir_formulario_abaixo", abrirFormularioAbaixo);
		pref.putBoolean("fechar_com_ESC_internal", fecharComESCInternal);
		pref.putInt("porc_vertical_local_form", porcVerticalLocalForm);
		pref.putBoolean("fechar_com_ESC_dialogo", fecharComESCDialogo);
		pref.putBoolean("monitor_preferencial", monitorPreferencial);
		pref.putBoolean("fichario_com_rolagem", ficharioComRolagem);
		pref.putInt("posicao_aba_fichario", posicaoAbaFichario);
		pref.putInt("total_confirmacao", totalConfirmacao);
		pref.put("form_ficha_dialogo", formFichaDialogo);
		pref.putBoolean("titulo_aba_min", tituloAbaMin);
		pref.put("get_objetos_banco", getObjetosBanco);
		pref.put(DIMENSAO_MENSAGEM, dimensaoMensagem);
		pref.put("form_dialogo", formDialogo);
		salvarOutras();
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

	public static int getInt(String chave) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		return pref.getInt(chave, 0);
	}

	public static void setBoolean(String chave, boolean valor) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putBoolean(chave, valor);
	}

	public static void setString(String chave, String valor) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.put(chave, valor == null ? "" : valor);
	}

	public static void setInt(String chave, int valor) {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt(chave, valor);
	}

	public static boolean isFicharioComRolagem() {
		return ficharioComRolagem;
	}

	public static void setFicharioComRolagem(boolean ficharioComRolagem) {
		Preferencias.ficharioComRolagem = ficharioComRolagem;
	}

	public static int getPosicaoAbaFichario() {
		if (posicaoAbaFichario == 0) {
			posicaoAbaFichario = 1;
		}
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

	public static String getGetObjetosBanco() {
		return getObjetosBanco;
	}

	public static void setGetObjetosBanco(String getObjetosBanco) {
		Preferencias.getObjetosBanco = getObjetosBanco;
	}

	public static String getFormDialogo() {
		return formDialogo;
	}

	public static void setFormDialogo(String formDialogo) {
		Preferencias.formDialogo = formDialogo;
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

	public static int getTotalConfirmacao() {
		return totalConfirmacao;
	}

	public static void setTotalConfirmacao(int totalConfirmacao) {
		Preferencias.totalConfirmacao = totalConfirmacao;
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

	public static boolean isAbrirFormularioDireita() {
		return abrirFormularioDireita;
	}

	public static void setAbrirFormularioDireita(boolean abrirFormularioDireita) {
		Preferencias.abrirFormularioDireita = abrirFormularioDireita;
	}

	public static boolean isAbrirFormularioAbaixo() {
		return abrirFormularioAbaixo;
	}

	public static void setAbrirFormularioAbaixo(boolean abrirFormularioAbaixo) {
		Preferencias.abrirFormularioAbaixo = abrirFormularioAbaixo;
	}

	public static boolean isDesenharEspacoRetornoTab() {
		return desenharEspacoRetornoTab;
	}

	public static void setDesenharEspacoRetornoTab(boolean desenharEspacoRetornoTab) {
		Preferencias.desenharEspacoRetornoTab = desenharEspacoRetornoTab;
		TextEditor.setPaintERT(desenharEspacoRetornoTab);
	}

	public static boolean isExibirTotalBytesClonados() {
		return exibirTotalBytesClonados;
	}

	public static void setExibirTotalBytesClonados(boolean exibirTotalBytesClonados) {
		Preferencias.exibirTotalBytesClonados = exibirTotalBytesClonados;
	}

	public static void setFontPreferencia(Font font) {
		if (font == null) {
			return;
		}
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		pref.putInt(EDITOR_FONT_STYLE, font.getStyle());
		pref.putInt(EDITOR_FONT_SIZE, font.getSize());
		pref.put(EDITOR_FONT_NAME, font.getName());
	}

	public static Font getFontPreferencia() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		String name = pref.get(EDITOR_FONT_NAME, null);
		if (name == null) {
			return null;
		}
		return new Font(name, pref.getInt(EDITOR_FONT_STYLE, 0), pref.getInt(EDITOR_FONT_SIZE, 12));
	}

	public static String getDimensaoMensagem() {
		return dimensaoMensagem;
	}

	public static void setDimensaoMensagem(String dimensaoMensagem) {
		Preferencias.dimensaoMensagem = dimensaoMensagem;
	}

	public static Dimension getDimensionMensagem() {
		Preferences pref = Preferences.userNodeForPackage(Formulario.class);
		String dm = pref.get(DIMENSAO_MENSAGEM, LA_500_300);
		int largura = 500;
		int altura = 300;
		Dimension d = new Dimension(largura, altura);
		if (Util.isEmpty(dm)) {
			return d;
		}
		String[] array = dm.split(",");
		if (array.length != 2) {
			return d;
		}
		try {
			largura = Integer.parseInt(array[0].trim());
		} catch (Exception e) {
			//
		}
		try {
			altura = Integer.parseInt(array[1].trim());
		} catch (Exception e) {
			//
		}
		return new Dimension(largura, altura);
	}
}