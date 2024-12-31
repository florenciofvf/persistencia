package br.com.persist.assistencia;

import java.util.Objects;

public class MetaInfo {
	private final String meta;
	private final String info;

	public MetaInfo(String meta, String info) {
		this.meta = Objects.requireNonNull(meta);
		this.info = Objects.requireNonNull(info);
	}

	public String getMeta() {
		return meta;
	}

	public String getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return info;
	}
}