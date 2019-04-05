package br.com.persist.listener;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.banco.Conexao;
import br.com.persist.util.BuscaAuto.Grupo;

public interface ObjetoContainerListener {
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado);

	public List<Conexao> getConexoes();

	public void setTitle(String tit);

	public Dimension getDimensoes();

	public Frame getFrame();

	public void dispose();
}