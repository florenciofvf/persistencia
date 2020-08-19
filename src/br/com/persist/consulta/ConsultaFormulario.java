package br.com.persist.consulta;

import java.awt.BorderLayout;
import java.util.Map;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConsultaFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	private ConsultaFormulario(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		this(formulario, Mensagens.getString(Constantes.LABEL_CONSULTA), provedor, padrao, null, null, true);
	}

	private ConsultaFormulario(Formulario formulario, String titulo, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		super(titulo);
		container = new ConsultaContainer(this, formulario, provedor, padrao, instrucao, mapaChaveValor, abrirArquivo);
		container.setConsultaFormulario(this);
		montarLayout();
	}

	private ConsultaFormulario(ConsultaContainer container) {
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

	public static ConsultaFormulario criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		return new ConsultaFormulario(formulario, provedor, padrao);
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao, String conteudo) {
		ConsultaFormulario form = criar(formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setConteudo(conteudo);
		form.setVisible(true);
	}

	public static ConsultaFormulario criar(Formulario formulario, String titulo, ConexaoProvedor provedor,
			Conexao padrao, String instrucao, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		return new ConsultaFormulario(formulario, titulo, provedor, padrao, instrucao, mapaChaveValor, abrirArquivo);
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