package viettel.com.sf.entity;

import org.zkoss.util.media.Media;
import viettel.com.sf.common.Constant;

import javax.persistence.*;
import java.sql.Timestamp;

@lombok.Data
@Entity
@Table(name = "file_info")
public class FileInfomation {

	@Id
	@SequenceGenerator(name = "file_seq", sequenceName = "file_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "file_seq")
	@Column(name = "file_id")
	private int fileId;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "file_size")
	private double fileSize;

	@Column(name = "file_path")
	private String path;

	@Column(name = "upload_time")
	private Timestamp upTime;

	@Column(name = "upload_duration")
	private double upDuration;

	@Column(name = "down_count")
	private int downloadCounter = 0;

	@Column(name = "last_download_time")
	private Timestamp lastDownTime;

	@Column(name = "available")
	private String available = Constant.AVAILABLE;

	@ManyToOne
	@JoinColumn(name = "login_name", referencedColumnName = "login_name")
	private UserInformation uploader;

	@Transient
	private Media media;

	@Transient
	private boolean selected;

	public String getSourceIcon() {
		return "/image/icon/" + this.fileType + ".png";
	}
}
