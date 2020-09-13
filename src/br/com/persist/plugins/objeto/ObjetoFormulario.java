package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.principal.Formulario;

public class ObjetoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;
	// private boolean ativo = true;

	private ObjetoFormulario(Formulario formulario, File file) {
		super(file.getName());
		container = new ObjetoContainer(this, formulario);
		container.setObjetoFormulario(this);
		montarLayout();
	}

	private ObjetoFormulario(Formulario formulario, ObjetoContainer container) {
		super(container.getFileName());
		container.setObjetoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ObjetoFormulario criar(Formulario formulario, ObjetoContainer container) {
		ObjetoFormulario form = new ObjetoFormulario(formulario, container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		return form;
	}

	public static ObjetoFormulario criar(Formulario formulario, File file) {
		ObjetoFormulario form = new ObjetoFormulario(formulario, file);
		form.setLocationRelativeTo(formulario);
		return form;
	}

	public void abrirArquivo(File file) {
		container.abrirArquivo(file);
	}

	public void abrirArquivo(File file, ObjetoColetor coletor, Graphics g, InternalConfig config) {
		container.abrir(file, coletor, g, config);
	}

	// public void abrirExportacaoImportacaoMetadado(Metadado metadado, boolean
	// exportacao, boolean circular) {
	// container.abrirExportacaoImportacaoMetadado(metadado, exportacao,
	// circular);
	// }
	//
	// public void exportarMetadadoRaiz(Metadado metadado) {
	// container.exportarMetadadoRaiz(metadado);
	// }

	// public void retornoAoFichario() {
	// Formulario formulario = container.getFormulario();
	//
	// if (formulario != null) {
	// remove(container);
	// ativo = false;
	// container.setJanela(null);
	// container.setObjetoFormulario(null);
	// fechar();
	// }
	// }

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setObjetoFormulario(null);
		fechar();
	}

	// @Override
	// public void executarAoAbrirForm() {
	// container.estadoSelecao();
	// }

	// @Override
	// public void executarAoFecharForm() {
	// if (ativo) {
	// container.excluido();
	// }
	// }

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}