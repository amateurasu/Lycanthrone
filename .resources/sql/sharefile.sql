CREATE TABLE IF NOT EXISTS account_info ( -- account table
    login_name         VARCHAR(255) NOT NULL PRIMARY KEY,
    created_time       TIMESTAMP,         -- created time
    department         VARCHAR(255),      -- Department of username
    email              VARCHAR(255),      -- @vttek.vn email
    full_name          VARCHAR(255),      -- user's full name
    login_fail_counter INTEGER,           -- number of continuous log in fail. if is 5, user cannot log in for a long time
    login_fail_time    TIMESTAMP,         -- last time user log in fail
    last_update_time   TIMESTAMP,
    password           VARCHAR(255),
    phone              VARCHAR(255),
    role               VARCHAR(255),      -- role of user (ADM or NRM)
    salt               VARCHAR(255),      -- string for encrypt password
    create_by          VARCHAR(255) REFERENCES account_info,
    available          VARCHAR(255),
    permission         VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS config_table (
    id               BIGINT NOT NULL PRIMARY KEY,
    category         VARCHAR(255),
    created_time     TIMESTAMP,
    description      VARCHAR(255),
    last_update_time TIMESTAMP,
    value            VARCHAR(255),
    parameter        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS file_info (                       -- information of file upload
    file_id            INTEGER PRIMARY KEY AUTOINCREMENT,
    file_name          VARCHAR(255),
    file_size          DOUBLE PRECISION,
    file_type          VARCHAR(255),                         -- type of file, only rar, zip, docx, pdf, pptx, xisx
    file_path          VARCHAR(255),                         -- path to save file in server
    upload_duration    DOUBLE PRECISION,                     -- time need for upload process (millisecond)
    upload_time        TIMESTAMP,                            -- time action upload performed
    login_name         VARCHAR(255) REFERENCES account_info, -- uploader
    down_count         INTEGER,
    last_download_time TIMESTAMP,
    available          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS action_history (           -- action of all user, need for trace
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    action      CHAR,                                 -- login (success, fail), logout, upload, download, search
    action_time TIMESTAMP,                            -- time user did action
    ip_addr     VARCHAR(255),                         -- IP address of computer request action
    login_name  VARCHAR(255) REFERENCES account_info, -- user did action
    file_id     INTEGER REFERENCES file_info,         -- file impacted
    description VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS ip_table (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    ip_addr VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_online (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    ip_addr    VARCHAR(255),
    status     CHAR,
    login_name VARCHAR(255) REFERENCES account_info
);

CREATE TABLE IF NOT EXISTS organization (
    organization_id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee        VARCHAR(255) REFERENCES account_info,
    manager         VARCHAR(255) REFERENCES account_info
);

------------------------------------------------------------------------------------------------------------------------

INSERT INTO account_info (login_name, created_time, department, email, full_name, last_update_time, login_fail_counter, login_fail_time, password, phone, role, salt, create_by, available, permission)
VALUES ('admin', '2019-04-18 13:56:06.520000', 'OCS', 'admin@vttek.vn', 'ADMIN', '2019-12-18 12:29:28.314000', 0, '2019-12-04 15:26:28.952000', '0R8H1en6ARzNWbU9uOW8amXOIjabaUNPSnSsqaZgHlc=', '0975123456', 'ADM', 'H4EokNur0ZkwbEnEHDx6DVHmR4dsmc3aSHKuRb2WxzI=', NULL, 'AVAILABLE', 'TWO_WAY'),
       ('duclm22', '2019-12-18 14:02:15.370000', 'VHT', 'duclm22@vttek.vn', 'Le Minh Duc', NULL, 2, '2019-12-18 14:14:38.936000', '0R8H1en6ARzNWbU9uOW8amXOIjabaUNPSnSsqaZgHlc=', '0123456789', 'NRM', 'H4EokNur0ZkwbEnEHDx6DVHmR4dsmc3aSHKuRb2WxzI=', 'admin', 'AVAILABLE', 'ONE_WAY');

INSERT INTO action_history (id, action, action_time, description, ip_addr, login_name, file_id)
VALUES (5539, '1', '2019-12-18 14:11:53.677000', NULL, '0:0:0:0:0:0:0:1', 'duclm22', NULL),
       (5540, '1', '2019-12-18 14:14:10.353000', NULL, '127.0.0.1', 'duclm22', NULL),
       (5541, '1', '2019-12-18 14:14:38.942000', NULL, '0:0:0:0:0:0:0:1', 'duclm22', NULL),
       (5542, '0', '2019-12-18 14:16:00.085000', NULL, '0:0:0:0:0:0:0:1', 'admin', NULL),
       (5543, '5', '2019-12-18 14:16:19.029000', 'Search Info: file name = a; user = null; from = Tue Jan 01 14:16:00 ICT 2019; to = Wed Dec 18 14:16:00 ICT 2019', '0:0:0:0:0:0:0:1', 'admin', NULL);

INSERT INTO config_table (id, category, created_time, description, last_update_time, value, parameter)
VALUES (6, 'permit', NULL, 'permit', NULL, 'ALL', 'permit'),
       (7, 'fileserver', NULL, 'file server address', NULL, 'AH1L+8TvI16ifXLBacZqWA==', 'file_server_addr'),
       (1, 'email', NULL, 'ip email host', '2019-06-03 10:22:23.564000', 'o/1p5QBxYif+TxbWAmMpMQ==', 'email_host'),
       (2, 'email', NULL, 'port', '2019-06-03 10:22:23.564000', '465', 'email_port'),
       (3, 'email', NULL, 'email address', '2019-06-03 10:22:23.564000', 'tuanvd10@vttek.vn', 'email_addr'),
       (4, 'email', NULL, 'email password', '2019-06-03 10:22:23.564000', '+0g38B2o7Gh6tliaCcBijismlT/nryQCdwNBdZYJ7EU=', 'email_pass'),
       (5, 'email', NULL, NULL, '2019-06-03 10:22:23.564000', 'false', 'tls_enable'),
       (10, 'PC', NULL, NULL, NULL, 'GGW1ZXESR4A8E4FQ2scTiA==', 'username'),
       (11, 'PC', NULL, NULL, NULL, '5Bz1QvvJ85RiDcJNWqUpGQ==', 'password'),
       (12, 'PC', NULL, NULL, NULL, '22', 'port'),
       (14, 'VDI', NULL, NULL, NULL, 'GGW1ZXESR4A8E4FQ2scTiA==', 'username'),
       (15, 'VDI', NULL, NULL, NULL, '5Bz1QvvJ85RiDcJNWqUpGQ==', 'password'),
       (16, 'VDI', NULL, NULL, NULL, '22', 'port'),
       (8, 'fileserver', NULL, 'filse server root path', NULL, 'Ow2pfrhwoaVxxGoaCOwR1q/kaFlfud5V93QbYE7of8A=', 'file_server_root_path'),
       (17, 'auth', NULL, 'doimain of ldapt authenticate', NULL, 'h1G5cXK1v4tSfM/MHv8N2A==', 'domain'),
       (18, 'auth', NULL, 'host of ldap authenticate', NULL, 'RrlXcT16S67fsCHeIsc5mp2GNYjCzQJfout2K0tEXIk=', 'ldapHost'),
       (19, 'auth', NULL, 'BaseDN of ldap authenticate', NULL, 'loRdFC+ibmsQbNeYtjAKtVkL1dnb7x8Op7ByksRQ+lY=', 'searchBase'),
       (13, 'VDI', NULL, 'PC: ip address of remote server (PC server)', NULL, 'CxbW6L8LmveLEeTa3lpecw==', 'host'),
       (9, 'PC', NULL, 'VDI: ip address of  remote server (VDI-server)', NULL, 'x4MQ4WLKFZL69OwTHIG4+A==', 'host'),
       (20, 'PC', NULL, 'max data in 1 session download (MB) from VDI', NULL, '10', 'max_transfer'),
       (21, 'VDI', NULL, 'max data in 1 session download (MB) from PC', NULL, '100', 'max_transfer');

INSERT INTO user_online (id, ip_addr, status, login_name)
VALUES (150, '10.61.248.98', '0', 'admin');

INSERT INTO organization (organization_id, employee, manager)
VALUES (1, 'duclm22', 'dungtx');
