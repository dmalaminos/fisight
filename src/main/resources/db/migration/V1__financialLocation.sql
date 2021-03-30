CREATE TABLE public.location
(
    id integer NOT NULL,
    entity_name character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT location_pkey PRIMARY KEY (id)
)