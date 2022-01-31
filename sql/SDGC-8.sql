#SDGC-8
#Base de datos con usuarios y contraseñas

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  username varchar(50) NOT NULL,
  password varchar(68) NOT NULL,
  enabled tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `users` 
VALUES 
('Eduardo','{bcrypt}$2a$10$BPTat6hrW2/eJYOmuEqT9uPJd.o.Rg13l83OX1lIIMpBv3gbEe7FC',1),
('Jesus','{bcrypt}$2a$10$Q.ZYY2xXzNijXpZvKtXquug7BS2gUqhJ1IOPHDW5qosewTw5rJ2B.',1),
('Mary','{bcrypt}$2a$10$Q.ZYY2xXzNijXpZvKtXquug7BS2gUqhJ1IOPHDW5qosewTw5rJ2B.',1);

DROP TABLE IF EXISTS authorities;
CREATE TABLE authorities (
  username varchar(50) NOT NULL,
  authority varchar(50) NOT NULL,
  UNIQUE KEY authorities_idx_1 (username, authority),
  CONSTRAINT authorities_ibfk_1 FOREIGN KEY (username) REFERENCES users (username)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO `authorities` 
VALUES 
('Mary','ROLE_EMPLOYEE'),
('Jesus','ROLE_EMPLOYEE'),
('Jesus','ROLE_MANAGER'),
('Eduardo','ROLE_EMPLOYEE'),
('Eduardo','ROLE_ADMIN');

COMMIT;