# Update para incluir usuarios

# --- !Ups

CREATE TABLE people(
   nick varchar(255) NOT NULL UNIQUE
);

INSERT INTO people (nick) VALUES ('tasks');
INSERT INTO people (nick) VALUES ('edgar');
INSERT INTO people (nick) VALUES ('domingo');
INSERT INTO people (nick) VALUES ('pasqual');
INSERT INTO people (nick) VALUES ('francisco');
INSERT INTO people (nick) VALUES ('matriculadehonor');


ALTER TABLE task ADD taskowner varchar(255) NOT NULL DEFAULT('tasks');
ALTER TABLE task ADD CONSTRAINT fk_task_to_owner FOREIGN KEY (taskowner) REFERENCES people(nick);

# --- !Downs

ALTER TABLE task DROP CONSTRAINT fk_task_to_owner;
ALTER TABLE task DROP taskowner;

DROP TABLE people;