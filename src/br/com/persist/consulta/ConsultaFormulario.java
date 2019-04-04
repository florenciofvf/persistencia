package br.com.persist.consulta;

import java.awt.BorderLayout;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConsultaFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	public ConsultaFormulario(ConexaoProvedor provedor, Conexao padrao) {
		super(Mensagens.getString("label.consulta"));
		container = new ConsultaContainer(this, provedor, padrao, null, null);
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