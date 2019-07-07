package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.container.MetadadosContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class MetadadoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MetadadosContainer container;

	public MetadadoFormulario(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		super(Mensagens.getString(Constantes.LABEL_METADADOS));
		container = new MetadadosContainer(this, formulario, provedor, padrao);
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