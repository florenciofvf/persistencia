package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.banco.Conexao;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.Mensagens;

public class FormularioSelect extends FormularioAbstrato implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelSelect painelSelect;
	private final Formulario formulario;

	public FormularioSelect(Formulario formulario, Frame frame, Conexao padrao) {
		super(Mensagens.getString("label.pesquisa"));
		this.formulario = formulario;
		painelSelect = new PainelSelect(this, padrao);
		setLocationRelativeTo(frame);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelSelect);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado) {
	}

	@Override
	public Vector<Conexao> getConexoes() {
		return formulario.getConexoes();
	}

	@Override
	public Dimension getDimensoes() {
		return getSize();
	}

	@Override
	public Frame getFrame() {
		return this;
	}
}