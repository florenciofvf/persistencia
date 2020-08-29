package br.com.persist.consulta;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Map;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class ConsultaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	private ConsultaDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, provedor, null, null, null, true);
		montarLayout();
	}

	private ConsultaDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, provedor, padrao, null, null, true);
		montarLayout();
	}

	private ConsultaDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, provedor, padrao, instrucao, mapaChaveValor, abrirArquivo);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ConsultaDialogo criar(Frame frame, Formulario formulario, ConexaoProvedor provedor) {
		return new ConsultaDialogo(frame, formulario, provedor);
	}

	public static ConsultaDialogo criar(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		ConsultaDialogo form = new ConsultaDialogo(frame, formulario, provedor, padrao);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}

	public static ConsultaDialogo criar(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		return new ConsultaDialogo(frame, formulario, provedor, padrao, instrucao, mapaChaveValor, abrirArquivo);
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		ConsultaDialogo form = criar(formulario, formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}