package viettel.com.sf.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "config_table")
public class ConfigTable {
	@Id
	@Column
	private long id;

	@Column(name = "category")
	private String category;

	@Column(name = "parameter")
	private String parameter;

	@Column(name = "value")
	private String value;

	@Column(name = "description")
	private String description;

	@Column(name = "created_time")
	private Timestamp createdTime;

	@Column(name = "last_update_time")
	private Timestamp lastUpdateTime;
}
