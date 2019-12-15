package viettel.com.sf.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "orangization")
public class Organization {
	@Id
	@SequenceGenerator(name = "orangization_seq", sequenceName = "orangization_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "orangization_seq")
	@Column(name = "orangization_id")
	private int orgId;

	@ManyToOne
	@JoinColumn(name = "employee", referencedColumnName = "login_name")
	private UserInformation employee;

	@ManyToOne
	@JoinColumn(name = "manager", referencedColumnName = "login_name")
	private UserInformation manager;
}
