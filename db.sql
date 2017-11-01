#MySQL
CREATE TABLE awp_account
(
	id int not null primary key,
	name varchar(100) not null comment '中文名',
  email varchar(255) not null comment '登录邮箱',
  password varchar(32) not null comment '密码MD5',
  role int default '1' not null comment '0=管理员 1=普通',
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
);

CREATE TABLE awp_order
(
    eid INT NOT NULL,
    type INT NOT NULL COMMENT '0=微信支付，1=支付宝支付',
    orderId VARCHAR(255) NOT NULL COMMENT '本地订单ID',
    platOrderId VARCHAR(255) COMMENT '支付平台订单ID',
    callback VARCHAR(2047) NOT NULL COMMENT '回调地址',
    createTime DATETIME NOT NULL,
    payTime DATETIME
);

#PostgreSQL
create table awp_account
(
	id serial not null
		constraint awp_account_pkey
			primary key,
	name varchar(100) not null,
	email varchar(255) not null,
	password varchar(32) not null,
	role integer default 1 not null,
	appid varchar(100),
	appsecret varchar(100),
	verify varchar(31),
	url varchar(255),
	token varchar(255),
	createtime timestamp,
	wxpayon integer default 0,
	mchkey varchar(255),
	mchid varchar(255),
	zfbpayon integer default 0,
	zfbappid varchar(31),
	zfbprivkey varchar(2047),
	zfbpubkey varchar(511)
);

create unique index awp_account_email_uindex
	on awp_account (email);

comment on column awp_account.name is '中文名';
comment on column awp_account.email is '登录邮箱';
comment on column awp_account.password is '密码MD5';
comment on column awp_account.role is '0=管理员 1=普通';
comment on column awp_account.verify is '微信公众号MP_verify域名验证';
comment on column awp_account.createtime is '创建时间';
comment on column awp_account.wxpayon is '是否开启微信支付';
comment on column awp_account.mchkey is '微信支付商户秘钥';
comment on column awp_account.mchid is '微信支付商户号';
comment on column awp_account.zfbpayon is '是否开启支付宝支付';
comment on column awp_account.zfbappid is '支付宝应用ID';
comment on column awp_account.zfbprivkey is '支付宝应用私钥';
comment on column awp_account.zfbpubkey is '支付宝公钥';

create table awp_order
(
	eid integer not null,
	type integer not null,
	orderid varchar(255) not null,
	platorderid varchar(255),
	callback varchar(2047) not null,
	createtime timestamp not null,
	paytime timestamp
);

comment on column awp_order.type is '0=微信支付，1=支付宝支付';
comment on column awp_order.orderid is '本地订单ID';
comment on column awp_order.platorderid is '支付平台订单ID';
comment on column awp_order.callback is '回调地址';