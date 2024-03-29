package br.com.persist.plugins.consulta;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;

public class ConsultaFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	private ConsultaFormulario(Formulario formulario, Conexao conexao, String conteudo) {
		super(formulario, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, formulario, conexao, conteudo);
		container.setConsultaFormulario(this);
		montarLayout();
	}

	private ConsultaFormulario(ConsultaContainer container) {
		super(container.getFormulario(), Mensagens.getString(Constantes.LABEL_CONSULTA));
		container.setConsultaFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ConsultaContainer container) {
		ConsultaFormulario form = new ConsultaFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static ConsultaFormulario criar(Formulario formulario, Conexao conexao, String conteudo) {
		ConsultaFormulario form = new ConsultaFormulario(formulario, conexao, conteudo);
		Formulario.posicionarJanela(formulario, form);
		return form;
	}

	public static ConsultaFormulario criar2(Formulario formulario, Conexao conexao, String conteudo) {
		return new ConsultaFormulario(formulario, conexao, conteudo);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setConsultaFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(this);
	}
}