CREATE TABLE IF NOT EXISTS geschaeftsfuehrer (
                                                 id              uuid PRIMARY KEY USING INDEX TABLESPACE zuliefererspace,
                                                 name            varchar(40) NOT NULL,
                                                 email           varchar(40) NOT NULL UNIQUE,
                                                 gehalt          integer NOT NULL CHECK(gehalt BETWEEN 0 AND 10000000)
) TABLESPACE zuliefererspace;
CREATE INDEX IF NOT EXISTS geschaeftsfuehrer_email_idx ON geschaeftsfuehrer(email) TABLESPACE zuliefererspace;

CREATE TABLE IF NOT EXISTS zulieferer (
                                          id                      uuid PRIMARY KEY USING INDEX TABLESPACE zuliefererspace,
                                          version                 integer NOT NULL DEFAULT 0,
                                          name                    varchar(40) NOT NULL,
                                          email                   varchar(40) NOT NULL ,
                                          geschaeftsfuehrer_id    uuid NOT NULL UNIQUE  REFERENCES geschaeftsfuehrer,
                                          username                varchar(20) NOT NULL UNIQUE,
                                          erzeugt                 timestamp NOT NULL,
                                          aktualisiert            timestamp NOT NULL
) TABLESPACE zuliefererspace;

CREATE INDEX IF NOT EXISTS zulieferer_name_idx ON zulieferer(name) TABLESPACE zuliefererspace;

CREATE TABLE IF NOT EXISTS lieferung (
                                         id              uuid PRIMARY KEY USING INDEX TABLESPACE zuliefererspace,
                                         artikel         varchar(40) NOT NULL,
                                         zulieferer_id   uuid REFERENCES zulieferer,
                                         idx             integer NOT NULL DEFAULT 0
) TABLESPACE zuliefererspace;
CREATE INDEX IF NOT EXISTS lieferung_zulieferer_id_idx ON lieferung(zulieferer_id) TABLESPACE zuliefererspace;
