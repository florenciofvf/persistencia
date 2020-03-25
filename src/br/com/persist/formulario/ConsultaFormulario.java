package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.util.Map;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.container.ConsultaContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConsultaFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	public ConsultaFormulario(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		this(formulario, Mensagens.getString(Constantes.LABEL_CONSULTA), provedor, padrao, null, null, true);
	}

	public ConsultaFormulario(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		super(titulo);
		container = new ConsultaContainer(this, formulario, provedor, padrao, instrucao, mapaChaveValor, abrirArquivo);
		montarLayout();
	}

	public ConsultaFormulario(ConsultaContainer container) {
		super(Mensagens.getString(Constantes.LABEL_CONSULTA));
		container.setConsultaFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public void setConteudo(String conteudo) {
		container.setConteudo(conteudo);
	}

	public static void criar(Formulario formulario, ConsultaContainer container) {
		ConsultaFormulario form = new ConsultaFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao, String conteudo) {
		ConsultaFormulario form = new ConsultaFormulario(formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setConteudo(conteudo);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		Formulario formulario = container.getFormulario();

		if (formulario != null) {
			remove(container);
			container.setJanela(null);
			container.setConsultaFormulario(null);
			formulario.getFichario().getConsulta().retornoAoFichario(formulario, container);
			dispose();
		}
	}
}