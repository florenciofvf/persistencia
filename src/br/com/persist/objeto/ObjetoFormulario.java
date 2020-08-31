package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.metadado.Metadado;
import br.com.persist.principal.Formulario;
import br.com.persist.util.ConfigArquivo;

public class ObjetoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;
	private boolean ativo = true;

	private ObjetoFormulario(Formulario formulario, ObjetoContainer container, File file) {
		super(file.getName());
		container.setObjetoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private ObjetoFormulario(Formulario formulario, File file) {
		super(file.getName());
		container = new ObjetoContainer(formulario, this);
		container.setObjetoFormulario(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void executarAoAbrirForm() {
		container.estadoSelecao();
	}

	@Override
	public void executarAoFecharForm() {
		if (ativo) {
			container.excluido();
		}
	}

	public void abrir(File file, ObjetoColetor coletor, Graphics g, ConfigArquivo config) {
		container.abrir(file, coletor, g, config);
	}

	public void abrirExportacaoImportacaoMetadado(Metadado metadado, boolean exportacao, boolean circular) {
		container.abrirExportacaoImportacaoMetadado(metadado, exportacao, circular);
	}

	public void exportarMetadadoRaiz(Metadado metadado) {
		container.exportarMetadadoRaiz(metadado);
	}

	public static void criar(Formulario formulario, ObjetoContainer container, File file) {
		ObjetoFormulario form = new ObjetoFormulario(formulario, container, file);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static ObjetoFormulario criar(Formulario formulario, File file) {
		ObjetoFormulario form = new ObjetoFormulario(formulario, file);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		return form;
	}

	public void retornoAoFichario() {
		Formulario formulario = container.getFormulario();

		if (formulario != null) {
			remove(container);
			ativo = false;
			container.setJanela(null);
			container.setObjetoFormulario(null);
			fechar();
		}
	}
}