

CREATE TABLE organizations (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    currency    VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);
CREATE TABLE positions (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    organization_id INTEGER NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

CREATE TABLE users (
	id bigserial NOT NULL,
	email text NOT NULL,
	email_verified boolean NOT NULL,
	image_url text NULL,
	"name" text NOT NULL,
	"password" text NULL,
	provider text NOT NULL,
	provider_id text NULL,
	organization_id integer,
	position_id integer,
	FOREIGN KEY (organization_id) REFERENCES organizations(id),
    FOREIGN KEY (position_id) REFERENCES positions(id), 
	CONSTRAINT uk_users_email UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE role (
	name text NOT NULL,
	description text,
	CONSTRAINT role_pkey PRIMARY KEY (name)
);

CREATE TABLE user_role (
	id serial,
	role_name text not NULL,
	user_id bigint not NULL,
	CONSTRAINT user_role_pkey PRIMARY KEY (id),
	CONSTRAINT uk_user_role UNIQUE (role_name, user_id),
	CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_user_role_role_name FOREIGN KEY (role_name) REFERENCES role(name)
);
