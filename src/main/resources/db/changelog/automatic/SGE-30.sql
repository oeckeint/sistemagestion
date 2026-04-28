-- liquibase formatted sql

-- changeset jesus:SGE-30
CREATE INDEX idx_cliente_cups ON sge.cliente (cups);

-- rollback DROP INDEX idx_cliente_cups ON sge.cliente;
