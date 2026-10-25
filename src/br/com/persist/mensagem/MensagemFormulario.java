package br.com.persist.mensagem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.File;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Dimensao;
import br.com.persist.assistencia.DimensaoUtil;
import br.com.persist.assistencia.Preferencias;

public class MensagemFormulario extends AbstratoFormulario implements Dimensao {
	private static final long serialVersionUID = 1L;
	private final MensagemContainer container;

	private MensagemFormulario(String titulo, String msg, File file) {
		super(null, file != null ? file.getAbsolutePath() : titulo);
		container = new MensagemContainer(this, msg, file);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static MensagemFormulario criar(String titulo, String msg, File file) {
		return new MensagemFormulario(titulo, msg, file);
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.dialogOpenedHandler();
		String string = container.getString();
		if (Preferencias.ajusteAuto(string)) {
			DimensaoUtil.ajustar(this);
		}
	}

	@Override
	public boolean contemScrollHorizontal() {
		return container.contemScrollHorizontal();
	}

	@Override
	public boolean contemScrollVertical() {
		return container.contemScrollVertical();
	}

	@Override
	public void somarALargura(int valor) {
		Dimension dimension = getSize();
		dimension.width += valor;
		setSize(dimension);
	}

	@Override
	public void setLargura(int largura) {
		Dimension dimension = getSize();
		dimension.width = largura;
		setSize(dimension);
	}

	@Override
	public void somarAAltura(int valor) {
		Dimension dimension = getSize();
		dimension.height += valor;
		setSize(dimension);
	}

	@Override
	public void setAltura(int altura) {
		Dimension dimension = getSize();
		dimension.height = altura;
		setSize(dimension);
	}

	@Override
	public int getLargura() {
		return getWidth();
	}

	@Override
	public int getAltura() {
		return getHeight();
	}

	@Override
	public void checarMaximoHorizontal() {
		Rectangle bounds = getGraphicsConfiguration().getBounds();
		if (getLargura() > bounds.width) {
			setLargura(bounds.width);
		}
	}

	@Override
	public void checarMaximoVertical() {
		Rectangle bounds = getGraphicsConfiguration().getBounds();
		if (getAltura() > bounds.height) {
			setAltura(bounds.height);
		}
	}
}