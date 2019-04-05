package br.com.persist.update;

import java.awt.BorderLayout;
import java.util.Map;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class UpdateFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final UpdateContainer container;

	public UpdateFormulario(ConexaoProvedor provedor, Conexao padrao) {
		this(Mensagens.getString("label.atualizacao"), provedor, padrao, null, null);
	}

	public UpdateFormulario(String titulo, ConexaoProvedor provedor, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		container = new UpdateContainer(this, provedor, padrao, instrucao, mapaChaveValor);
		montarLayout();
	}

	public UpdateFormulario(String titulo, ConexaoProvedor provedor, Conexao padrao, String instrucao) {
		super(titulo);
		container = new UpdateContainer(this, provedor, padrao, instrucao);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}
}