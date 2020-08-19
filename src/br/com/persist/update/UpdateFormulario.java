package br.com.persist.update;

import java.awt.BorderLayout;
import java.util.Map;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class UpdateFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final UpdateContainer container;

	private UpdateFormulario(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		this(formulario, Mensagens.getString(Constantes.LABEL_UPDATE), provedor, padrao, null, null);
	}

	private UpdateFormulario(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao) {
		super(titulo);
		container = new UpdateContainer(this, formulario, provedor, padrao, instrucao);
		container.setUpdateFormulario(this);
		montarLayout();
	}

	private UpdateFormulario(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor) {
		super(titulo);
		container = new UpdateContainer(this, formulario, provedor, padrao, instrucao, mapaChaveValor);
		container.setUpdateFormulario(this);
		montarLayout();
	}

	private UpdateFormulario(UpdateContainer container) {
		super(Mensagens.getString(Constantes.LABEL_UPDATE));
		container.setUpdateFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public void setConteudo(String conteudo) {
		container.setConteudo(conteudo);
	}

	public static UpdateFormulario criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		return new UpdateFormulario(formulario, provedor, padrao);
	}

	public static UpdateFormulario criar(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao) {
		return new UpdateFormulario(formulario, titulo, provedor, padrao, instrucao);
	}

	public static UpdateFormulario criar(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor) {
		return new UpdateFormulario(formulario, titulo, provedor, padrao, instrucao, mapaChaveValor);
	}

	public static UpdateFormulario criar(UpdateContainer container) {
		return new UpdateFormulario(container);
	}

	public static void criar(Formulario formulario, UpdateContainer container) {
		UpdateFormulario form = criar(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao, String conteudo) {
		UpdateFormulario form = criar(formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setConteudo(conteudo);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		Formulario formulario = container.getFormulario();

		if (formulario != null) {
			remove(container);
			container.setJanela(null);
			container.setUpdateFormulario(null);
			formulario.getFichario().getUpdate().retornoAoFichario(formulario, container);
			dispose();
		}
	}
}