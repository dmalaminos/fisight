CREATE TABLE public.financial_location
(
    id integer NOT NULL,
    entity_name character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT financial_location_pkey PRIMARY KEY (id)
)