create table t_wxcms_account
(
	id int not null primary key,
	name varchar(100) not null comment '中文名',
  email varchar(255) not null comment '登录邮箱',
  password varchar(32) not null comment '密码MD5',
	appid varchar(100) null,
	appsecret varchar(100) null,
  verify varchar(31) null comment '微信公众号MP_verify域名验证',
	url varchar(255) null,
	token varchar(255) null,
	createTime datetime null comment '创建时间',
  wxPayOn int default '0' null comment '是否开启微信支付',
	mchKey varchar(255) null comment '微信支付商户秘钥',
	mchId varchar(255) null comment '微信支付商户号',
  zfbPayOn int default '0' null comment '是否开启支付宝支付',
  zfbAppId varchar(31) null comment '支付宝应用ID',
  zfbPrivKey varchar(2047) null comment '支付宝应用私钥',
  zfbPubKey varchar(511) null comment '支付宝公钥'
)
;