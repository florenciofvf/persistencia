package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.metadado.Metadado;
import br.com.persist.principal.Formulario;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.xml.XMLColetor;

public class ContainerFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final Container container;
	private boolean ativo = true;

	private ContainerFormulario(Formulario formulario, Container container, File file) {
		super(file.getName());
		container.setContainerFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private ContainerFormulario(Formulario formulario, File file) {
		super(file.getName());
		container = new Container(formulario, this);
		container.setContainerFormulario(this);
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

	public void abrir(File file, XMLColetor coletor, Graphics g, ConfigArquivo config) {
		container.abrir(file, coletor, g, config);
	}

	public void abrirExportacaoImportacaoMetadado(Metadado metadado, boolean exportacao, boolean circular) {
		container.abrirExportacaoImportacaoMetadado(metadado, exportacao, circular);
	}

	public void exportarMetadadoRaiz(Metadado metadado) {
		container.exportarMetadadoRaiz(metadado);
	}

	public static void criar(Formulario formulario, Container container, File file) {
		ContainerFormulario form = new ContainerFormulario(formulario, container, file);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static ContainerFormulario criar(Formulario formulario, File file) {
		ContainerFormulario form = new ContainerFormulario(formulario, file);
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
			container.setContainerFormulario(null);
			formulario.getFichario().getConteiner().retornoAoFichario(formulario, container);
			fechar();
		}
	}
}