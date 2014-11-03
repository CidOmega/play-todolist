# Update para incluir categorias en las tareas

# --- !Ups

CREATE TABLE category(
   owner varchar(255) NOT NULL,
   name varchar(255) NOT NULL,
   UNIQUE(owner, name),
   FOREIGN KEY (owner) REFERENCES people(nick)
);


# --- !Downs

DROP TABLE category;