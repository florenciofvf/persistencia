package br.com.persist.update;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Map;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class UpdateDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final UpdateContainer container;

	public UpdateDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor) {
		super(frame, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		container = new UpdateContainer(this, formulario, provedor, null, null, null);
		montarLayout();
	}

	public UpdateDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao conexao) {
		super(frame, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		container = new UpdateContainer(this, formulario, provedor, conexao, null, null);
		montarLayout();
	}

	public UpdateDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao conexao,
			String instrucao) {
		super(frame, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		container = new UpdateContainer(this, formulario, provedor, conexao, instrucao);
		montarLayout();
	}

	public UpdateDialogo(Frame frame, Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao conexao,
			String instrucao, Map<String, String> mapaChaveValor) {
		super(frame, titulo);
		container = new UpdateContainer(this, formulario, provedor, conexao, instrucao, mapaChaveValor);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		UpdateDialogo form = new UpdateDialogo(formulario, formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}