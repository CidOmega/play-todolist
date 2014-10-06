# Update para incluir fechas en las tareas

# --- !Ups

ALTER TABLE task ADD deadend timestamp;


# --- !Downs

ALTER TABLE task DROP deadend;