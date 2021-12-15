create table customer
	(username		varchar(50), 
	 name			varchar(50) not null, 
	 address		varchar(100), 
	 credit_card	varchar(50),
     email          varchar(50),
     phone_number   varchar(50),
	 primary key (username)
	);

create table author
	(ID				varchar(50),
	 name 			varchar(50),
	 primary key (ID)
	);

create table publisher
	(name 			varchar(50),
	 address		varchar(100),
	 email			varchar(50),
	 phone_number	varchar(50),
	 primary key (name)
	);

create table book
	(ISBN			varchar(50), 
	 title			varchar(50) not null, 
	 price			numeric(12,2) check (price > 0),
     pages          numeric(5,0) check (pages > 0),
     stock		    numeric(5,0),
	 publisher_name varchar(50),
	 primary key (ISBN),
	 foreign key (publisher_name) references publisher
	 	on delete cascade
	);

create table wrote
	(author_id		varchar(50),
	 ISBN			varchar(50),
	 primary key (author_id, ISBN),
	 foreign key (author_id) references author
	 	on delete cascade,
	 foreign key (ISBN) references book
		on delete cascade
	);

create table shopping_cart
	(ID				numeric(12,0),
	 customer_user	varchar(50),
	 checked_out	bit,
	 primary key (cart_id),
	 foreign key (customer_user) references customer
	 	on delete cascade
	);

create table in_cart
	(cart_id		numeric(12,0),
	 ISBN			varchar(50),
	 quantity		numeric(4,0),
	 primary key (cart_id,ISBN),
	 foreign key (cart_id) references shopping_cart
	 	on delete cascade,
	 foreign key (ISBN) references book
	 	on delete cascade
	);

create table purchase
	(ID				numeric(12,0),
	 date   		varchar(10),
	 customer_user	varchar(50),
	 shopping_cart_id	numeric(12,0),
	 shipping_address 	varchar(100),
	 billing_address	varchar(100),
	 credit_card	varchar(50),
	 package_tracking	varchar(50),
	 primary key (ID),
	 foreign key (customer_user) references customer_user
	 	on delete set null,
	 foreign key (shopping_cart_id) references shopping_cart
	 	on delete cascade
	);

create table order
	(date 			varchar(10),
	 publisher_name		varchar(50),
	 ISBN			varchar(50),
	 received		bit,
	 primary key (date, publisher, ISBN),
	 foreign key (publisher) references publisher
	 	on delete cascade,
	 foreign key (ISBN) references book
	 	on delete cascade
	);
