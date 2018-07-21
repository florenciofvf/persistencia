package br.com.persist.formulario;

import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.com.persist.banco.Conexao;
import br.com.persist.comp.Menu;
import br.com.persist.util.Sistema;
import br.com.persist.util.Util;

public class FormularioUtil {
	private FormularioUtil() {
	}

	public static void configMAC(Formulario formulario) {
		if (Sistema.getInstancia().isMac()) {
			try {
				Class<?> classe = Class.forName("com.apple.eawt.FullScreenUtilities");
				Method method = classe.getMethod("setWindowCanFullScreen", Window.class, Boolean.TYPE);
				method.invoke(classe, formulario, true);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(formulario.getClass().getName() + ".setWindowCanFullScreen()", ex,
						formulario);
			}
		}
	}

	public static void fechar(Formulario formulario) {
		try {
			Conexao.fecharConexoes();
		} catch (Exception ex) {
			Util.stackTraceAndMessage(formulario.getClass().getName() + ".fechar()", ex, formulario);
		}
	}

	public static void menuAparencia(Formulario formulario, Menu menu) {
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		ButtonGroup grupo = new ButtonGroup();

		for (LookAndFeelInfo info : installedLookAndFeels) {
			ItemLAF item = new ItemLAF(formulario, info);
			grupo.add(item);
			menu.add(item);
		}
	}

	public static void aparenciaPadrao(Menu menu, String titulo) {
		int total = menu.getItemCount();

		for (int i = 0; i < total; i++) {
			JMenuItem item = menu.getItem(i);

			if (titulo.equals(item.getText())) {
				item.doClick();
			}
		}
	}
}