package viettel.com.sf.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ip_table")
public class IpTable {
	@Id
	@SequenceGenerator(name = "ip_table_seq", sequenceName = "ip_table_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ip_table_seq")
	@Column
	private long id;

	@Column(name = "ip_addr")
	private String ipAddr;
}
