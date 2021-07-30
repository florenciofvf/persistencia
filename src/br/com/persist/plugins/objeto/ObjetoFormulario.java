package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.internal.InternalConfig;

public class ObjetoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

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
		Formulario.posicionarJanela(formulario, form);
		return form;
	}

	public static ObjetoFormulario criar(Formulario formulario, File file) {
		ObjetoFormulario form = new ObjetoFormulario(formulario, file);
		Formulario.posicionarJanela(formulario, form);
		return form;
	}

	public void abrirExportacaoImportacaoMetadado(Conexao conexao, Metadado metadado, boolean exportacao,
			boolean circular) {
		AtomicReference<String> tituloTemp = new AtomicReference<>();
		container.abrirExportacaoImportacaoMetadado(conexao, metadado, exportacao, circular, tituloTemp);
		if (!Util.estaVazio(tituloTemp.get())) {
			setTitle(tituloTemp.get());
		}
	}

	public void abrirArquivo(File file, ObjetoColetor coletor, Graphics g, InternalConfig config) {
		container.abrir(file, coletor, g, config);
	}

	public void exportarMetadadoRaiz(Metadado metadado) {
		container.exportarMetadadoRaiz(metadado);
	}

	public void abrirArquivo(File file) {
		container.abrirArquivo(file);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setObjetoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}

	@Override
	public void executarAoFecharFormulario() {
		container.formularioFechado();
	}
}