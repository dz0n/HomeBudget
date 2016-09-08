drop table if exists budget_active;
drop table if exists budget_access;
drop table if exists receipt;
drop table if exists subcategory;
drop table if exists category;
drop table if exists account;
drop table if exists budget;
drop table if exists users;


CREATE TABLE users (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	username varchar(30) NOT NULL,
	password varchar(60) NOT NULL,
	email varchar(50) NOT NULL
);

CREATE TABLE budget (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	name varchar(45) NOT NULL,
	description varchar(100) DEFAULT '',
	user int(11) NOT NULL,
	FOREIGN KEY (user) REFERENCES users(id)
);

CREATE TABLE category (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	name varchar(45) NOT NULL,
	description varchar(100) DEFAULT '',
	value int(12) NOT NULL,
	budget int(11) NOT NULL,
	created timestamp DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (budget) REFERENCES budget(id)
);

CREATE TABLE subcategory (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	name varchar(45) NOT NULL,
	description varchar(100) DEFAULT '',
	category int(11) NOT NULL,
	created timestamp DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (category) REFERENCES category(id)
);

CREATE TABLE account (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	name varchar(45) NOT NULL,
	description varchar(100) DEFAULT '',
	value int(12) NOT NULL,
	budget int(11) NOT NULL,
	created timestamp DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (budget) REFERENCES budget(id)
);

CREATE TABLE receipt (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	name varchar(45) NOT NULL,
	description varchar(100) DEFAULT '',
	value int(11) NOT NULL,
	date date DEFAULT CURRENT_DATE(),
	subcategory int(11) NOT NULL,
	account int(11) NOT NULL,
	user int(11) DEFAULT NULL,
	created timestamp DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (subcategory) REFERENCES subcategory(id),
	FOREIGN KEY (account) REFERENCES account(id),
	FOREIGN KEY (user) REFERENCES users(id)
);

CREATE TABLE budget_active (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	user int(11) DEFAULT NULL,
	budget int(11) DEFAULT NULL,
	UNIQUE KEY user_UNIQUE (user),
	FOREIGN KEY (user) REFERENCES users(id),
	FOREIGN KEY (budget) REFERENCES budget(id)
);

CREATE TABLE budget_access (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	budget int(11) DEFAULT NULL,
	user int(11) DEFAULT NULL,
	level int(4) DEFAULT '0',
	UNIQUE KEY budget_UNIQUE (budget, user),
	FOREIGN KEY (user) REFERENCES users(id),
	FOREIGN KEY (budget) REFERENCES budget(id)
);

CREATE TABLE account_transfer (
	id int(11) PRIMARY KEY AUTO_INCREMENT,
	name varchar(45) DEFAULT '',
	description varchar(100) DEFAULT '',
	value int(11) NOT NULL,
	date date DEFAULT CURRENT_DATE(),
	from_account int(11) DEFAULT NULL,
	to_account int(11) DEFAULT NULL,
	user int(11) DEFAULT NULL,
	created timestamp DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (from_account) REFERENCES account(id),
	FOREIGN KEY (to_account) REFERENCES account(id),
	FOREIGN KEY (user) REFERENCES users(id)
);
