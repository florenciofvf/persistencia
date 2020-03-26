package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.util.Map;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.container.UpdateContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class UpdateFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final UpdateContainer container;

	public UpdateFormulario(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		this(formulario, Mensagens.getString(Constantes.LABEL_UPDATE), provedor, padrao, null, null);
	}

	public UpdateFormulario(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor) {
		super(titulo);
		container = new UpdateContainer(this, formulario, provedor, padrao, instrucao, mapaChaveValor);
		container.setUpdateFormulario(this);
		montarLayout();
	}

	public UpdateFormulario(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao) {
		super(titulo);
		container = new UpdateContainer(this, formulario, provedor, padrao, instrucao);
		container.setUpdateFormulario(this);
		montarLayout();
	}

	public UpdateFormulario(UpdateContainer container) {
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

	public static void criar(Formulario formulario, UpdateContainer container) {
		UpdateFormulario form = new UpdateFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao, String conteudo) {
		UpdateFormulario form = new UpdateFormulario(formulario, provedor, padrao);
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