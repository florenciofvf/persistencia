package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoException;
import br.com.persist.plugins.objeto.internal.InternalConfig;

public class ObjetoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

	private ObjetoDialogo(Formulario formulario, File file) {
		super((Frame) null, file.getName());
		container = new ObjetoContainer(this, formulario);
		container.setObjetoDialogo(this);
		montarLayout();
	}

	private ObjetoDialogo(ObjetoContainer container) {
		super(container.getFormulario(), container.getFileName());
		container.setObjetoDialogo(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ObjetoContainer container) {
		ObjetoDialogo form = new ObjetoDialogo(container);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public static ObjetoDialogo criar(Formulario formulario, File file) {
		ObjetoDialogo form = new ObjetoDialogo(formulario, file);
		Util.configSizeLocation(formulario, form, null);
		return form;
	}

	public void abrirExportacaoImportacaoMetadado(Conexao conexao, Metadado metadado, boolean exportacao,
			boolean circular) throws MetadadoException, ObjetoException, AssistenciaException {
		AtomicReference<String> tituloTemp = new AtomicReference<>();
		container.abrirExportacaoImportacaoMetadado(conexao, metadado, exportacao, circular, tituloTemp);
		if (!Util.isEmpty(tituloTemp.get())) {
			setTitle(tituloTemp.get());
		}
	}

	public void abrirArquivo(File file, ObjetoColetor coletor, InternalConfig config)
			throws XMLException, ObjetoException, AssistenciaException {
		container.abrir(file, coletor, config);
	}

	public void exportarMetadadoRaiz(Metadado metadado) throws AssistenciaException {
		container.exportarMetadadoRaiz(metadado);
	}

	public void abrirArquivo(File file, InternalConfig config) {
		container.abrirArquivo(file, config);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setObjetoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}