create table customer
	(customer_username		varchar(50), 
	 name			varchar(50) not null, 
	 address		varchar(100), 
	 credit_card	numeric(16,0),
     email          varchar(50),
     phone_number   numeric(10,0),
	 primary key (customer_username)
	);

create table author
	(author_id		numeric(4,0),
	 name 			varchar(50) not null,
	 primary key (author_id)
	);

create table publisher
	(publisher_name	varchar(50),
	 bank_account	numeric(12,0),
	 address		varchar(100),
	 email			varchar(50),
	 phone_number	numeric(10,0),
	 primary key (publisher_name)
	);

create table book
	(ISBN			numeric(13,0), 
	 title			varchar(50) not null, 
	 price			numeric(12,2) check (price > 0),
	 percentage_to_publisher 	numeric(4,2) check (percentage_to_publisher < 100),
     pages          numeric(5,0) check (pages > 0),
     stock		    numeric(5,0),
	 genre			varchar(50),
	 publisher_name varchar(50),
	 primary key (ISBN),
	 foreign key (publisher_name) references publisher
	 	on delete cascade
	);

create table wrote
	(author_id		numeric(4,0),
	 ISBN			numeric(13,0),
	 primary key (author_id, ISBN),
	 foreign key (author_id) references author
	 	on delete cascade,
	 foreign key (ISBN) references book
		on delete cascade
	);

create table shopping_cart
	(shopping_cart_id	numeric(12,0),
	 customer_username	varchar(50),
	 primary key (shopping_cart_id),
	 foreign key (customer_username) references customer
	 	on delete cascade
	);

create table in_cart
	(shopping_cart_id	numeric(12,0),
	 ISBN			numeric(13,0),
	 quantity		numeric(4,0),
	 primary key (shopping_cart_id,ISBN),
	 foreign key (shopping_cart_id) references shopping_cart
	 	on delete cascade,
	 foreign key (ISBN) references book
	 	on delete cascade
	);

create table purchase
	(tracking_number numeric(12,0),
	 date_time_of_purchase   		varchar(50),
	 shipping_address 	varchar(100),
	 billing_address	varchar(100),
	 credit_card	numeric(16,0),
	 amount_paid	numeric(12,2),
	 tracking_status	varchar(50),
	 shopping_cart_id	numeric(12,0),
	 customer_username	varchar(50),
	 primary key(tracking_number),
	 foreign key (shopping_cart_id) references shopping_cart
	 	on delete set null,
	 foreign key (customer_username) references customer
	 	on delete set null
	);

create table stock_order
	(date_time 			varchar(50),
	 publisher_name		varchar(50),
	 ISBN			numeric(13,0),
	 number_purchased	numeric(4,0),
	 primary key (date_time, publisher_name, ISBN),
	 foreign key (publisher_name) references publisher
	 	on delete cascade,
	 foreign key (ISBN) references book
	 	on delete set null
	);
