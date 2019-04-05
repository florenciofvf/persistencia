package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JInternalFrame;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.desktop.Desktop;
import br.com.persist.listener.PainelObjetoListener;
import br.com.persist.principal.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.BuscaAuto.Tabela;

public class InternoFormulario extends JInternalFrame implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer objetoPainel;
	private final Formulario formulario;
	private String apelido;

	public InternoFormulario(Formulario formulario, Objeto objeto, Graphics g, Conexao padrao, boolean buscaAuto) {
		super(objeto.getId(), true, true, true, true);
		this.formulario = formulario;
		objetoPainel = new ObjetoContainer(this, objeto, g, padrao, buscaAuto);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 600);
		montarLayout();
		setVisible(true);
	}

	public boolean ehTabela(Tabela tabela) {
		return getApelido().equalsIgnoreCase(tabela.getApelido())
				&& objetoPainel.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public ObjetoContainer getObjetoPainel() {
		return objetoPainel;
	}

	public void buscaAutomatica(String campo, String argumentos) {
		objetoPainel.buscaAutomatica(campo, argumentos);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, objetoPainel);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado) {
		Container parent = getParent();
		Desktop desktop = null;

		while (parent != null) {
			if (parent instanceof Desktop) {
				desktop = (Desktop) parent;
				break;
			}

			parent = getParent();
		}

		if (desktop != null) {
			desktop.buscaAutomatica(grupo, argumentos, objetoPainel, processado);
		}
	}

	@Override
	public List<Conexao> getConexoes() {
		return formulario.getConexoes();
	}

	@Override
	public Dimension getDimensoes() {
		return getSize();
	}

	@Override
	public Frame getFrame() {
		return null;
	}

	public void atualizarFormulario() {
		objetoPainel.atualizarFormulario();
	}

	public String getApelido() {
		if (apelido == null) {
			apelido = "";
		}

		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}
}