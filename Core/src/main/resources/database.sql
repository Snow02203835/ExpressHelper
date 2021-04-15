CREATE DATABASE IF NOT EXISTS express DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

CREATE USER 'expressAdmin'@'localhost' IDENTIFIED BY '123456';
CREATE USER 'expressAdmin'@'%' IDENTIFIED BY '123456';

GRANT ALL ON express.* TO 'expressAdmin'@'localhost';
GRANT ALL ON express.* TO 'expressAdmin'@'%';

FLUSH PRIVILEGES;