package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.metadado.Metadado;
import br.com.persist.principal.Formulario;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.IJanela;
import br.com.persist.xml.XMLColetor;

public class ContainerFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final Container container;
	private boolean ativo = true;

	public ContainerFormulario(Formulario formulario, Container container, File file) {
		super(file.getName());
		container.setContainerFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
		configurar();
	}

	public ContainerFormulario(Formulario formulario, File file) {
		super(file.getName());
		container = new Container(formulario, this);
		container.setContainerFormulario(this);
		montarLayout();
		configurar();
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

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.estadoSelecao();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (ativo) {
					container.excluido();
				}
			}
		});
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, Container container, File file) {
		ContainerFormulario form = new ContainerFormulario(formulario, container, file);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		Formulario formulario = container.getFormulario();

		if (formulario != null) {
			remove(container);
			ativo = false;
			container.setJanela(null);
			container.setContainerFormulario(null);
			formulario.getFichario().getConteiner().retornoAoFichario(formulario, container);
			dispose();
		}
	}
}