package viettel.com.sf.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Action_History")
public class ActionHistory {

	@Id
	@SequenceGenerator(name = "action_history_id_seq", sequenceName = "action_history_id_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "action_history_id_seq")
	@Column
	private long id;

	@Column(name = "action")
	private char action;

	@Column(name = "action_time")
	private Timestamp actionTime;

	@Column(name = "ip_addr")
	private String ipAddr;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "login_name", referencedColumnName = "login_name")
	private UserInformation user;

	@OneToOne
	@JoinColumn(name = "file_id", referencedColumnName = "file_id")
	private FileInfomation file;
}
