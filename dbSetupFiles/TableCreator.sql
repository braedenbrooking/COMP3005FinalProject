create table customer
	(username		varchar(50), 
	 name			varchar(50) not null, 
	 address		varchar(100), 
	 credit_card	varchar(50),
     email          varchar(50),
     phone_number   varchar(50),
	 primary key (username)
	);