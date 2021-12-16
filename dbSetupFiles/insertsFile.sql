delete from customer;
delete from author;
delete from publisher;
delete from book;
delete from wrote;
delete from shopping_cart;
delete from in_cart;
delete from purchase;
delete from stock_refill_order;

insert into customer values ('jeff77', 'Jeff Jefferson', '1234 Jubilife Street, Ottawa, ON, H0H 0H0', 1234567890123456, 'jeff77@hotmail.com', 6135550001);
insert into customer values ('tim12', 'Tim Thompson', '9876 Veilstone Way, Ottawa, ON, H0H 0H0', 1111222233334444, 'timmytom@thunderbird.com', 6135550002);
insert into customer values ('yolanda45', 'Yolanda Squatpump', '5974 Canalave Street, Ottawa, ON, H0H 0H0', 1212343456567878, 'squatpumpy@protonmail.com', 6135550003);
insert into author values(1,'J.R.R. Tolkien');
insert into author values(2,'Haruki Murakami');
insert into author values(3,'Bill Clinton');
insert into author values(4,'James Patterson');
insert into author values(5,'Stephen King');
insert into author values(6,'Margret Atwood');
insert into author values(7,'Philip K. Dick');
insert into author values(8,'Andy Weir');
insert into author values(9,'Banana Yoshimoto');
insert into author values(10,'William Shakespeare');
insert into publisher values('Pseudorandom House', 123456789012, '15 Melancholy Way, Ottawa, ON, H0H 0H0', 'contact@pseudorandomhouse.ca', 6135551111);
insert into publisher values('Flightless Bird', 098765432109, '88 Snowbelle Street, Ottawa, ON, H0H 0H0', 'contact@flightlessbirdpublishing.net', 6135552222);
insert into publisher values('Post-Secondary University Press', 102938475610, '31 Rusborough Road, Ottawa, ON, H0H 0H0', 'contact-press@post-secondaryuniversty.edu', 6135552222);
insert into book values(9780261102354, 'Lord of the Rings: The Fellowship of the Ring', 10.99, 448, 13, 'Fantasy', 'Pseudorandom House');
insert into book values(9780261102361, 'Lord of the Rings: The Two Towers', 10.99, 464, 14, 'Fantasy', 'Pseudorandom House');
insert into book values(9780261102378, 'Lord of the Rings: The Return of the King', 10.99, 464, 15, 'Fantasy', 'Pseudorandom House');
insert into book values(9780261102736, 'The Silmarillion', 10.99, 480, 20, 'Fantasy', 'Pseudorandom House');
insert into book values(9780007458424, 'The Hobbit', 10.99, 368, 10, 'Fantasy', 'Pseudorandom House');

insert into book values(9781400079278, 'Kafka on the Shore', 21.27, 480, 10, 'Magical Realism', 'Flightless Bird');
insert into book values(9780375704024, 'Norwegian Wood', 23.00, 304, 10, 'Magical Realism', 'Flightless Bird');
insert into book values(9780385678025, '1Q84', 35.00, 1184, 10, 'Magical Realism', 'Flightless Bird');

insert into book values(9781538713839, 'The President is Missing', 23.49, 528, 10, 'Thriller', 'Post-Secondary University Press');
insert into book values(9780316540711, 'The President''s Daughter', 22.26, 608, 10, 'Thriller', 'Post-Secondary University Press');
insert into book values(9780316499149, 'Fear No Evil', 25.00, 400, 50, 'Thriller', 'Post-Secondary University Press');

insert into book values(9781982136079, '1922', 13.01, 144, 25, 'Horror', 'Pseudorandom House');
insert into book values(9780307743657, 'The Shining', 9.11, 688, 25, 'Horror', 'Pseudorandom House');

insert into book values(9780771008795, 'The Handmaid''s Tale', 19.95, 384, 15, 'Science Fiction', 'Flightless Bird');
insert into book values(9780771009457, 'The Testaments', 22.00, 448, 14, 'Science Fiction', 'Flightless Bird');

insert into book values(9780345404473, 'Do Androids Dream of Electric Sheep?', 12.99, 240, 11, 'Science Fiction', 'Post-Secondary University Press');
insert into book values(9780547572482, 'The Man in the High Castle', 22.55, 288, 11, 'Dystopian', 'Post-Secondary University Press');
insert into book values(9780547572178, 'A Scanner Darkly', 22.94, 304, 10, 'Science Fiction', 'Post-Secondary University Press');

insert into book values(9780593357132, 'The Martian', 12.99, 480, 17, 'Science Fiction', 'Pseudorandom House');
insert into book values(9780553448122, 'Artemis', 8.00, 320, 16, 'Science Fiction', 'Pseudorandom House');

insert into book values(9780802142443, 'Kitchen', 23.95, 160, 10, 'Drama', 'Flightless Bird');

insert into book values(9780143128540, 'Hamlet', 13.50, 208, 12, 'Classic', 'Post-Secondary University Press');
insert into book values(9780143128571, 'Romeo and Juliet', 13.50, 176, 11, 'Classic', 'Post-Secondary University Press');
insert into book values(9780143128564, 'Macbeth', 13.50, 144, 10, 'Classic', 'Post-Secondary University Press');

insert into wrote values(1, 9780261102354);
insert into wrote values(1, 9780261102361);
insert into wrote values(1, 9780261102378);
insert into wrote values(1, 9780261102736);
insert into wrote values(1, 9780007458424);

insert into wrote values(2, 9781400079278);
insert into wrote values(2, 9780375704024);
insert into wrote values(2, 9780385678025);

insert into wrote values(3, 9781538713839);
insert into wrote values(3, 9780316540711);
insert into wrote values(4, 9781538713839);
insert into wrote values(4, 9780316540711);
insert into wrote values(4, 9780316499149);

insert into wrote values(5, 9781982136079);
insert into wrote values(5, 9780307743657);

insert into wrote values(6, 9780771008795);
insert into wrote values(6, 9780771009457);

insert into wrote values(7, 9780345404473);
insert into wrote values(7, 9780547572482);
insert into wrote values(7, 9780547572178);

insert into wrote values(8, 9780593357132);
insert into wrote values(8, 9780553448122);

insert into wrote values(9, 9780802142443);

insert into wrote values(10, 9780143128540);
insert into wrote values(10, 9780143128571);
insert into wrote values(10, 9780143128564);




