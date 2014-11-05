# Update para incluir categorias en las tareas (relación)

# --- !Ups

ALTER TABLE task ADD CONSTRAINT unique_task_id UNIQUE(id);

CREATE TABLE category_task(
   category_owner varchar(255) NOT NULL,
   category_name varchar(255) NOT NULL,
   task_id integer NOT NULL,
   FOREIGN KEY (category_owner,category_name) REFERENCES category(owner, name),
   FOREIGN KEY (task_id) REFERENCES task(id)
);



# --- !Downs

DROP TABLE category_task;

ALTER TABLE task DROP CONSTRAINT unique_task_id;