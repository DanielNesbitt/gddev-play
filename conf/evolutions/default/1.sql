# Users schema

# --- !Ups

CREATE TABLE users (
  id serial PRIMARY KEY ,
  username text NOT NULL ,
  forename text NOT NULL,
  surname text NOT NULL
);

INSERT INTO users (username, forename, surname) VALUES
  ('daniel', 'Daniel', 'Nesbitt'),
  ('james', 'James', 'Cooper');

# --- !Downs

DROP TABLE users;