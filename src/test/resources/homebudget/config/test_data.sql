INSERT INTO users(id, username, password, email) VALUES (1,'proba','$2a$10$cx5GYfHzCDMngWepG.PXdeH6tJXZrQ9iOc3fXC5hrztRqk8mvspTG','proba@proba.pl');
INSERT INTO users(id, username, password, email) VALUES (2,'user2','$2a$10$cx5GYfHzCDMngWepG.PXdeH6tJXZrQ9iOc3fXC5hrztRqk8mvspTG','user2@user2.com');
INSERT INTO users(id, username, password, email) VALUES (3,'user3','$2a$10$cx5GYfHzCDMngWepG.PXdeH6tJXZrQ9iOc3fXC5hrztRqk8mvspTG','user2@user2.com');

INSERT INTO budget(name, description, user) VALUES ('test',' ',1);
INSERT INTO budget(name, description, user) VALUES ('abc','brak',1);
INSERT INTO budget(name, description, user) VALUES ('def','brak',2);

INSERT INTO category(name, description, value, budget) VALUES ('category #1', 'none', 10020, 1);
INSERT INTO category(name, description, value, budget) VALUES ('category #2', 'none', 20056, 2);
INSERT INTO category(name, description, value, budget) VALUES ('category #3', 'none', 30012, 2);
INSERT INTO category(name, description, value, budget) VALUES ('category #4', 'none', 43056, 2);

INSERT INTO subcategory(name, description, category) VALUES ('subcategory #1', '', 1);
INSERT INTO subcategory(name, description, category) VALUES ('subcategory #2', '', 1);
INSERT INTO subcategory(name, description, category) VALUES ('subcategory #3', '', 2);
INSERT INTO subcategory(name, description, category) VALUES ('subcategory #4', '', 2);
INSERT INTO subcategory(name, description, category) VALUES ('subcategory #5', '', 3);

INSERT INTO account(name, description, value, budget) VALUES ('account #1', '', 100000, 1);
INSERT INTO account(name, description, value, budget) VALUES ('account #2', '', 210073, 1);
INSERT INTO account(name, description, value, budget) VALUES ('account #3', '', 310076, 2);
INSERT INTO account(name, description, value, budget) VALUES ('account #4', '', 410051, 2);
INSERT INTO account(name, description, value, budget) VALUES ('account #5', '', 510021, 2);
INSERT INTO account(name, description, value, budget) VALUES ('account #6', '', 630031, 3);

INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #1', '', 100, 1, 1);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #2', '', 200, 1, 1);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #3', '', 300, 1, 1);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #4', '', 400, 1, 2);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #5', '', 500, 1, 2);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #6', '', 600, 2, 1);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #7', '', 700, 2, 2);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #8', '', 800, 2, 2);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #9', '', 900, 3, 3);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #10', '', 1000, 3, 3);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #11', '', 1100, 3, 4);
INSERT INTO receipt(name, description, value, subcategory, account) VALUES ('receipt #12', '', 1200, 5, 5);

INSERT INTO account_transfer(name, description, value, from_account, to_account) VALUES ('account transfer #1', '', 1100, 1, 2);
INSERT INTO account_transfer(name, description, value, from_account, to_account) VALUES ('account transfer #2', '', 1200, 3, 4);
INSERT INTO account_transfer(name, description, value, from_account, to_account) VALUES ('account transfer #3', '', 1300, 3, 5);
INSERT INTO account_transfer(name, description, value, from_account, to_account) VALUES ('account transfer #4', '', 1400, 4, 5);
INSERT INTO account_transfer(name, description, value, from_account, to_account) VALUES ('account transfer #5', '', 1500, 5, 3);