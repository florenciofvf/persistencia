package br.com.persist.painel;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.banco.Conexao;
import br.com.persist.util.BuscaAuto.Grupo;

public interface PainelObjetoListener {
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado);

	public Vector<Conexao> getConexoes();

	public void setTitle(String tit);

	public Dimension getDimensoes();

	public Frame getFrame();

	public void dispose();
}