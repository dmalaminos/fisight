CREATE TABLE public.currency_trade
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    base_currency varchar(5),
    date_traded timestamp without time zone,
    fee_amount numeric(16,8),
    fee_currency varchar(5),
    price_per_base_unit_amount numeric(16,8),
    price_per_base_unit_currency varchar(5),
    quantity double precision NOT NULL,
    quote_currency varchar(5),
    trade_type varchar(5),
    location_id integer,
    CONSTRAINT currency_trade_pkey PRIMARY KEY (id),
    CONSTRAINT fktrjw8yehaa995k84xyry1rhtr FOREIGN KEY (location_id)
        REFERENCES public.location (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)