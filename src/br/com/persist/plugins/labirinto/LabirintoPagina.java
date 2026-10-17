package br.com.persist.plugins.labirinto;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.abstrato.Aba;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TabbedPane;

public class LabirintoPagina extends Panel implements Aba {
	private final TabbedPane tabbedPane = new TabbedPane(false);
	private static final long serialVersionUID = 1L;
	private final AbaViewXML abaViewXML;
	private final AbaView2D abaView2D;
	private final AbaView3D abaView3D;
	private final File file;
	private int indice;

	public LabirintoPagina(LabirintoFichario fichario, File file) {
		tabbedPane.setTabLayoutPolicy(TabbedPane.SCROLL);
		abaViewXML = new AbaViewXML(fichario, file);
		abaView2D = new AbaView2D(file);
		abaView3D = new AbaView3D(abaView2D);
		this.file = file;
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		tabbedPane.addTab("ViewXML", abaViewXML);
		tabbedPane.addTab("View2D", abaView2D);
		tabbedPane.addTab("View3D", abaView3D);
		add(BorderLayout.CENTER, tabbedPane);
	}

	public String getConteudo() {
		return abaViewXML.getConteudo();
	}

	public void setConteudo(String string) {
		abaViewXML.textEditor.setText(string);
	}

	public String getNome() {
		return file.getName();
	}

	private void abrir() {
		abaViewXML.abrir();
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(LabirintoConstantes.PAINEL_LABIRINTO, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, LabirintoMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try {
			ArquivoUtil.salvar(abaViewXML.textEditor, file);
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(LabirintoConstantes.PAINEL_LABIRINTO, ex, this);
		}
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		if (Util.contemStringEm(file, string, porParte)) {
			set.add(file.getAbsolutePath());
		}
	}

	@Override
	public void setIndice(int i) {
		indice = i;
	}

	@Override
	public int getIndice() {
		return indice;
	}

	@Override
	public File getFile() {
		return file;
	}
}