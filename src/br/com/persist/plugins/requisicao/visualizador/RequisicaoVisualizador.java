package br.com.persist.plugins.requisicao.visualizador;

import java.awt.Component;
import java.io.PrintWriter;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JTextPane;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.data.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoRota;

public interface RequisicaoVisualizador {
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros);

	public void adicionarMime(String mime);

	public boolean contemMime(String mime);

	public void excluirMime(String mime);

	public void salvar(PrintWriter pw);

	public Set<String> getMimes();

	public String getTitulo();

	public Icon getIcone();

	public void limpar();

	public void setRequisicaoVisualizadorListener(RequisicaoVisualizadorListener listener);

	public RequisicaoVisualizadorListener getRequisicaoVisualizadorListener();

	public void setRequisicaoRota(RequisicaoRota rota);

	public RequisicaoRota getRequisicaoRota();

	public default BarraButton criarToolbarPesquisa(JTextPane textPane, TextEditor textEditor) {
		ToolbarPesquisa toolbarPesquisa = new ToolbarPesquisa(textPane);
		if (textEditor != null) {
			textEditor.setListener(TextEditor.newTextEditorAdapter(toolbarPesquisa::focusInputPesquisar));
		}
		return toolbarPesquisa;
	}
}