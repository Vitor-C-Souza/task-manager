CREATE TABLE tb_categoria
(
    id         BINARY(16) NOT NULL,
    nome       VARCHAR(50) NOT NULL,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE tb_tarefa
(
    id           BINARY(16) NOT NULL,
    titulo       VARCHAR(255) NOT NULL,
    concluida    BOOLEAN      NOT NULL DEFAULT FALSE,
    categoria_id BINARY(16) NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_tarefa_categoria
        FOREIGN KEY (categoria_id) REFERENCES tb_categoria (id)
            ON DELETE CASCADE
);