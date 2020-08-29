package br.com.persist.update;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Map;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class UpdateDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final UpdateContainer container;

	private UpdateDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor) {
		super(frame, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		container = new UpdateContainer(this, formulario, provedor, null, null, null);
		montarLayout();
	}

	private UpdateDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao conexao) {
		super(frame, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		container = new UpdateContainer(this, formulario, provedor, conexao, null, null);
		montarLayout();
	}

	private UpdateDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao conexao,
			String instrucao) {
		super(frame, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		container = new UpdateContainer(this, formulario, provedor, conexao, instrucao);
		montarLayout();
	}

	private UpdateDialogo(Frame frame, Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao conexao,
			String instrucao, Map<String, String> mapaChaveValor) {
		super(frame, titulo);
		container = new UpdateContainer(this, formulario, provedor, conexao, instrucao, mapaChaveValor);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static UpdateDialogo criar(Frame frame, Formulario formulario, ConexaoProvedor provedor) {
		return new UpdateDialogo(frame, formulario, provedor);
	}

	public static UpdateDialogo criar(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao conexao) {
		UpdateDialogo form = new UpdateDialogo(frame, formulario, provedor, conexao);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}

	public static UpdateDialogo criar(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao conexao,
			String instrucao) {
		return new UpdateDialogo(frame, formulario, provedor, conexao, instrucao);
	}

	public static UpdateDialogo criar(Frame frame, Formulario formulario, String titulo, ConexaoProvedor provedor,
			Conexao conexao, String instrucao, Map<String, String> mapaChaveValor) {
		return new UpdateDialogo(frame, formulario, titulo, provedor, conexao, instrucao, mapaChaveValor);
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		UpdateDialogo form = criar(formulario, formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}