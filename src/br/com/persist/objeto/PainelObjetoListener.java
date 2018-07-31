package br.com.persist.objeto;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.Vector;

import br.com.persist.banco.Conexao;

public interface PainelObjetoListener {
	public Vector<Conexao> getConexoes();

	public void setTitle(String tit);

	public Dimension getDimensoes();

	public Frame getFrame();

	public void dispose();
}