create database PathLab;
use PathLab;
create table dept(
deptid int not null auto_increment,
deptname varchar(50) not null,
primary key(deptid)
);
create table app(
appid int not null auto_increment,
appdate date not null,
pid int not null,
deptid int not null,
primary key(appid),
foreign key(deptid) references dept(deptid)
);
create table patient(
pid int not null auto_increment,
pname varchar(50) not null,
pemail varchar(50) not null,
ppassword varchar(50) not null,
pbloodgroup varchar(50) not null,
psex varchar(50) not null,
pages int not null,
primary key(pid)
);
create table doctor(
did int not null auto_increment,
dname varchar(50) not null,
dphone varchar(50) not null,
demail varchar(50) not null,
dpassword varchar(50) not null,
deptid int not null,
primary key(did),
foreign key(deptid) references dept(deptid)
);

create table feedback(
fid int not null auto_increment,
fname varchar(50) not null,
ftext varchar(250) not null,
primary key(fid)
);
create table admintable(
adminid int not null auto_increment,
adminuser varchar(50) not null,
adminpass varchar(50) not null,
primary key(adminid)
);
alter table patient
add column appid int;

insert into admintable(adminuser, adminpass) values("admin","admin");

create table role(
`id` int not null auto_increment,
`name`varchar(50) not null,
	primary key(`id`)
);
INSERT INTO `role`(`id`,`name`)
VALUES(1,"admin"),
(2,"user");
/***********************************/