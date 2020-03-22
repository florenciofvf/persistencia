package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import br.com.persist.Metadado;
import br.com.persist.desktop.Container;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Form;
import br.com.persist.util.IJanela;

public class ContainerFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final Container container;
	private boolean ativo = true;

	public ContainerFormulario(Formulario formulario, Container container, File file) {
		super(file.getName());
		container.setSuperficieFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
		configurar();
	}

	public ContainerFormulario(Formulario formulario, File file) {
		super(file.getName());
		container = new Container(formulario, this);
		container.setSuperficieFormulario(this);
		montarLayout();
		configurar();
	}

	public void abrir(File file, List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms,
			StringBuilder sbConexao, Graphics g, Dimension d) {
		container.abrir(file, objetos, relacoes, forms, sbConexao, g, d);
	}

	public void abrirExportacaoImportacaoMetadado(Metadado metadado, boolean exportacao, boolean circular) {
		container.abrirExportacaoImportacaoMetadado(metadado, exportacao, circular);
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

	public void retornoDestacarEmFormulario() {
		remove(container);
		ativo = false;
		container.setJanela(null);
		container.setSuperficieFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().retornoDestacarEmFormulario(formulario, container);
		dispose();
	}
}