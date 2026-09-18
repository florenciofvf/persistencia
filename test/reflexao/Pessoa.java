package reflexao;

import java.util.Date;

public class Pessoa {
	private final String nome;
	private Mes mesParaFerias;
	private Date agendamento;
	private Double valor;

	public Pessoa(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public Mes getMesParaFerias() {
		return mesParaFerias;
	}

	public void setMesParaFerias(Mes mesParaFerias) {
		this.mesParaFerias = mesParaFerias;
	}

	public Date getAgendamento() {
		return agendamento;
	}

	public void setAgendamento(Date agendamento) {
		this.agendamento = agendamento;
		System.out.println("Agendamento para:" + this.agendamento);
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
		System.out.println("Valor:" + this.valor);
	}

	@Override
	public String toString() {
		return nome + " tira férias em " + mesParaFerias;
	}
}