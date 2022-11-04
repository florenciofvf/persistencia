package br.com.persist.plugins.requisicao.visualizador;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.data.Tipo;

public class RequisicaoVisualizadorPDF extends AbstratoRequisicaoVisualizador {
	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			Class<?> klass = Class.forName("com.qoppa.pdfViewer.PDFViewerBean");
			Object objeto = klass.newInstance();
			JComponent comp = (JComponent) objeto;
			load(klass, objeto, bytes);
			SwingUtilities.invokeLater(() -> comp.scrollRectToVisible(new Rectangle()));
			return comp;
		} catch (Exception e) {
			Util.mensagem(parent, e.getMessage());
			return null;
		}
	}

	private void load(Class<?> klass, Object objeto, byte[] bytes)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = klass.getDeclaredMethod("loadPDF", InputStream.class);
		method.invoke(objeto, new ByteArrayInputStream(bytes));
	}

	@Override
	public String toString() {
		return "PDF";
	}

	@Override
	public Icon getIcone() {
		return Icones.PDF;
	}
}