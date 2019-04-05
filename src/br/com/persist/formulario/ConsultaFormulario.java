package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.util.Map;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.consulta.ConsultaContainer;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConsultaFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	public ConsultaFormulario(ConexaoProvedor provedor, Conexao padrao) {
		this(Mensagens.getString("label.consulta"), provedor, padrao, null, null);
	}

	public ConsultaFormulario(String titulo, ConexaoProvedor provedor, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		container = new ConsultaContainer(this, provedor, padrao, instrucao, mapaChaveValor);
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