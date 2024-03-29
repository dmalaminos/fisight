CREATE TABLE public.transfer
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    amount_amount numeric(16,8),
    amount_currency varchar(5) COLLATE pg_catalog."default",
    fee_amount numeric(16,8),
    fee_currency varchar(5) COLLATE pg_catalog."default",
    date_transferred timestamp without time zone,
    source_id integer,
    target_id integer,
    CONSTRAINT transfer_pkey PRIMARY KEY (id),
    CONSTRAINT fk3acqovo686xni8eangctw22yc FOREIGN KEY (target_id)
        REFERENCES public.location (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkgam8qi0eb2e3glgnvm075esmm FOREIGN KEY (source_id)
        REFERENCES public.location (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)