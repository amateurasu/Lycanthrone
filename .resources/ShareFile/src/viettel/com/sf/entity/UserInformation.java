package viettel.com.sf.entity;

import lombok.*;
import org.hibernate.annotations.Where;
import viettel.com.sf.common.Constant;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Account_Info")
public class UserInformation {

	@Id
	@Column(name = "login_name")
	private String loginName;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "phone")
	private String phoneNumber;

	@Column(name = "departement")
	private String departement;

	@Column(name = "login_fail_counter")
	private int loginFailCounter;

	@Column(name = "login_fail_time")
	private Timestamp loginFailTime;

	@Column(name = "salt")
	private String salt;

	@Column(name = "role")
	private String role;

	@Column(name = "created_time")
	private Timestamp createdTime;

	@Column(name = "last_update_time")
	private Timestamp lastUpdateTime;

	@Column(name = "permission")
	private String permission = Constant.USER_PERMISSIONS.get(0);

	@Column(name = "available")
	private String available = Constant.AVAILABLE;

	@ManyToOne
	@JoinColumn(name = "create_by", referencedColumnName = "login_name")
	private UserInformation createBy;

	@OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Where(clause = "(available='AVAILABLE' OR available is NULL)")
	@OrderBy("upTime DESC")
	private List<FileInfomation> listFileUploaded = new ArrayList<>();

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Organization> listManager = new ArrayList<>();

	@OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Organization> listEmployee = new ArrayList<>();
}
