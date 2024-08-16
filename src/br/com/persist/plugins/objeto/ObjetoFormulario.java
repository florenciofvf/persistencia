package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoException;
import br.com.persist.plugins.objeto.internal.InternalConfig;

public class ObjetoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

	private ObjetoFormulario(Formulario formulario, File file) {
		super(formulario, file.getName());
		container = new ObjetoContainer(this, formulario);
		container.setObjetoFormulario(this);
		montarLayout();
	}

	private ObjetoFormulario(ObjetoContainer container) {
		super(container.getFormulario(), container.getFileName());
		container.setObjetoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ObjetoFormulario criar(Formulario formulario, ObjetoContainer container) {
		ObjetoFormulario form = new ObjetoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
		return form;
	}

	public static ObjetoFormulario criar(Formulario formulario, File file) {
		ObjetoFormulario form = new ObjetoFormulario(formulario, file);
		Formulario.posicionarJanela(formulario, form);
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
		container.setObjetoFormulario(null);
		fechar();
	}

	@Override
	public void windowActivatedHandler(Window window) {
		container.windowActivatedHandler(window);
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}

	@Override
	public void windowClosingHandler(Window window) {
		container.formularioFechado();
	}
}