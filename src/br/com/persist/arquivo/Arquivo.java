package br.com.persist.arquivo;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Util;

public class Arquivo {
	private static final Logger LOG = Logger.getGlobal();
	private final List<String> ignorados;
	private final List<Arquivo> filhos;
	private boolean arquivoAberto;
	private boolean processado;
	private BigInteger tag;
	private Arquivo pai;
	private File file;

	public Arquivo(File file, List<String> ignorados) {
		this.ignorados = Objects.requireNonNull(ignorados);
		this.file = Objects.requireNonNull(file);
		filhos = new ArrayList<>();
	}

	public List<String> getIgnorados() {
		return ignorados;
	}

	public void reiniciar() {
		processado = true;
		filhos.clear();
		inflar();
	}

	public void inflar() {
		getFilhos();
		for (Arquivo a : filhos) {
			a.inflar();
		}
	}

	public void excluir() {
		for (Arquivo a : filhos) {
			a.excluir();
		}
		if (isFile() || isDirectory()) {
			excluir(file);
		}
	}

	private void excluir(File file) {
		Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
		try {
			Files.delete(path);
		} catch (IOException e) {
			LOG.log(Level.FINEST, "EXCLUIR ARQUIVO");
		}
	}

	public boolean renomear(String nome) {
		try {
			File destino = new File(file.getParent(), nome);
			boolean resp = file.renameTo(destino);
			if (resp) {
				file = destino;
			}
			return resp;
		} catch (Exception e) {
			return false;
		}
	}

	public void listar(List<Arquivo> lista) {
		if (isFile()) {
			lista.add(this);
		}
		for (Arquivo a : filhos) {
			a.listar(lista);
		}
	}

	public List<Arquivo> getFilhos() {
		if (!processado) {
			processar();
		}
		return filhos;
	}

	private void processar() {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File f = files[i];
					if (".DS_Store".equalsIgnoreCase(f.getName())) {
						excluir(f);
					} else if (!ignorar(f.getName())) {
						adicionar(f);
					}
				}
			}
		}
		processado = true;
		ordenar();
	}

	private boolean ignorar(String string) {
		if (string != null) {
			for (String s : ignorados) {
				if (string.endsWith(s)) {
					return true;
				}
			}
		}
		return false;
	}

	public Arquivo adicionar(File file) {
		if (file != null) {
			Arquivo arquivo = new Arquivo(file, ignorados);
			if (!contem(arquivo)) {
				filhos.add(arquivo);
				arquivo.pai = this;
				return arquivo;
			}
		}
		return null;
	}

	public boolean isPai() {
		return getName().endsWith("_parent");
	}

	public boolean isAuto() {
		return getName().endsWith("_auto");
	}

	public boolean isPessoa() {
		return getName().endsWith("_pessoa");
	}

	public boolean isFile2() {
		return getName().endsWith("_file");
	}

	public boolean isURL() {
		return getName().endsWith("_url");
	}

	public boolean isExec() {
		return getName().endsWith("_exec");
	}

	public boolean isTimer() {
		return getName().endsWith("_timer");
	}

	public boolean isEmpty() {
		return getName().endsWith("_empty");
	}

	public boolean isAnexo() {
		return getName().endsWith("_anexo");
	}

	public boolean isInfo() {
		return getName().endsWith("_info");
	}

	public boolean isSeta() {
		return getName().endsWith("_seta");
	}

	public boolean isCheck() {
		return getName().endsWith("_check");
	}

	public boolean isDescricao() {
		return getName().endsWith("_desc");
	}

	public boolean isException() {
		return getName().endsWith("_exception");
	}

	public boolean isRefresh() {
		return getName().endsWith("_refresh");
	}

	public boolean isService() {
		return getName().endsWith("_service");
	}

	public boolean isSinc() {
		return getName().endsWith("_sinc");
	}

	public boolean isDown() {
		return getName().endsWith("_down");
	}

	public boolean isUp() {
		return getName().endsWith("_up");
	}

	public boolean isJS() {
		return getName().endsWith("_js");
	}

	public void ordenar() {
		ArquivoUtil.ordenar(filhos);
	}

	public boolean excluir(Arquivo arquivo) {
		return filhos.remove(arquivo);
	}

	public boolean contem(Arquivo arquivo) {
		return getIndice(arquivo) >= 0;
	}

	public int getIndice(Arquivo arquivo) {
		return filhos.indexOf(arquivo);
	}

	public int getTotal() {
		return filhos.size();
	}

	public boolean estaVazio() {
		return filhos.isEmpty();
	}

	public Arquivo getArquivo(int index) {
		return filhos.get(index);
	}

	public Arquivo getArquivo(File file) {
		if (this.file.equals(file)) {
			return this;
		}
		for (Arquivo m : filhos) {
			Arquivo a = m.getArquivo(file);
			if (a != null) {
				return a;
			}
		}
		return null;
	}

	public Arquivo getArquivo(String descricao, boolean porParte) {
		for (Arquivo item : filhos) {
			if (Util.existeEm(item.file.getName(), descricao, porParte)) {
				return item;
			}
		}
		for (Arquivo item : filhos) {
			Arquivo resp = item.getArquivo(descricao, porParte);
			if (resp != null) {
				return resp;
			}
		}
		return null;
	}

	public void preencher(List<Arquivo> lista, String descricao, boolean porParte) {
		if (Util.existeEm(file.getName(), descricao, porParte)) {
			lista.add(this);
		}
		for (Arquivo item : filhos) {
			item.preencher(lista, descricao, porParte);
		}
	}

	public void pesquisar(String nome, AtomicInteger contador, boolean recursivo) {
		if (file.getName().endsWith(nome)) {
			contador.incrementAndGet();
		}
		if (recursivo) {
			for (Arquivo item : filhos) {
				item.pesquisar(nome, contador, recursivo);
			}
		} else {
			for (Arquivo item : filhos) {
				if (item.getName().endsWith(nome)) {
					contador.incrementAndGet();
				}
			}
		}
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		if (Util.contemStringEm(file, string, porParte)) {
			set.add(file.getAbsolutePath());
		}
		for (Arquivo a : filhos) {
			a.contemConteudo(set, string, porParte);
		}
	}

	public boolean pathValido() {
		return pathValido(file);
	}

	public static boolean pathValido(File file) {
		if (file == null) {
			return false;
		}
		String path = file.getAbsolutePath();
		for (char c : path.toCharArray()) {
			if (c <= ' ') {
				return false;
			}
		}
		return true;
	}

	public boolean isFile() {
		return file.isFile();
	}

	public boolean isDirectory() {
		return file.isDirectory();
	}

	public File getFile() {
		return file;
	}

	public Arquivo getPai() {
		return pai;
	}

	public String getName() {
		return file.getName();
	}

	@Override
	public String toString() {
		return file.getName();
	}

	public boolean isArquivoAberto() {
		return arquivoAberto;
	}

	public void setArquivoAberto(boolean arquivoAberto) {
		this.arquivoAberto = arquivoAberto;
	}

	public BigInteger getTag() {
		return tag;
	}

	public void setTag(BigInteger tag) {
		this.tag = tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arquivo other = (Arquivo) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file)) {
			return false;
		}
		return true;
	}
}