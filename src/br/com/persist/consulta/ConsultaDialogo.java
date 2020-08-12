package br.com.persist.consulta;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Map;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConsultaDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	public ConsultaDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, provedor, null, null, null, true);
		montarLayout();
	}

	public ConsultaDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, provedor, padrao, null, null, true);
		montarLayout();
	}

	public ConsultaDialogo(Frame frame, Formulario formulario, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, provedor, padrao, instrucao, mapaChaveValor, abrirArquivo);
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
		ConsultaDialogo form = new ConsultaDialogo(formulario, formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}