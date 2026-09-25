package br.com.persist.abstrato;

import java.io.File;

public interface Aba {
	public void setIndice(int i);

	public int getIndice();

	public File getFile();
}