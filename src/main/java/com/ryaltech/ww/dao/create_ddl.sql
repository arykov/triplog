create table users(
	user_id numeric(20) primary key,
	auth_provider varchar(20),
	auth_provider_id varchar(100),
	user_name varchar(100)
);

create table rivers(
	river_id numeric(20) primary key,
	river_name varchar(100)
);

create table outings(
	outing_id numeric(20) primary key,
	user_id numeric(20) references users(user_id),
	river_id numeric(20) references rivers(river_id),
	start_timestamp timestamp,
	end_timestamp timestamp,
	notes varchar(4000)
);

create sequence orm_seq start with 1;