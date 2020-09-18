package br.com.persist.plugins.consulta;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;

public class ConsultaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	private ConsultaDialogo(Frame frame, Formulario formulario, Conexao conexao, String conteudo) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, conexao, conteudo);
		container.setConsultaDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ConsultaDialogo criar(Formulario formulario, Conexao conexao, String conteudo) {
		ConsultaDialogo form = new ConsultaDialogo(formulario, formulario, conexao, conteudo);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		return form;
	}

	public static ConsultaDialogo criar2(Formulario formulario, Conexao conexao, String conteudo) {
		return new ConsultaDialogo(formulario, formulario, conexao, conteudo);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setConsultaDialogo(null);
		fechar();
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.dialogoVisivel();
	}
}