-- Execute depois de 07_carga_passo_a_passo_relacionamentos.sql,
-- conectado ao database api_fundamentos.

-- Garantir a coluna usada pelo relacionamento, inclusive em uma tabela
-- produtos que tenha sido criada anteriormente pelo JPA.
ALTER TABLE public.produtos
    ADD COLUMN IF NOT EXISTS categoria_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_produtos_categorias'
          AND conrelid = 'public.produtos'::regclass
    ) THEN
        ALTER TABLE public.produtos
            ADD CONSTRAINT fk_produtos_categorias
            FOREIGN KEY (categoria_id)
            REFERENCES public.categorias (id)
            ON UPDATE CASCADE
            ON DELETE SET NULL;
    END IF;
END;
$$;

-- 1. Inserir categorias.
INSERT INTO public.categorias (nome, descricao)
VALUES ('Perifericos', 'Acessorios para computadores')
ON CONFLICT (nome) DO UPDATE
SET descricao = EXCLUDED.descricao
RETURNING *;

INSERT INTO public.categorias (nome, descricao)
VALUES ('Monitores', 'Telas e equipamentos de exibicao')
ON CONFLICT (nome) DO UPDATE
SET descricao = EXCLUDED.descricao
RETURNING *;

INSERT INTO public.categorias (nome, descricao)
VALUES ('Armazenamento', 'Discos e unidades de armazenamento')
ON CONFLICT (nome) DO UPDATE
SET descricao = EXCLUDED.descricao
RETURNING *;

-- 2. Inserir produtos e ligar cada um a uma categoria.
INSERT INTO public.produtos (nome, preco, ativo, categoria_id)
SELECT 'Teclado mecanico', 299.90, TRUE, id
FROM public.categorias
WHERE nome = 'Perifericos'
  AND NOT EXISTS (
      SELECT 1 FROM public.produtos WHERE nome = 'Teclado mecanico'
  )
RETURNING *;

INSERT INTO public.produtos (nome, preco, ativo, categoria_id)
SELECT 'Mouse sem fio', 149.90, TRUE, id
FROM public.categorias
WHERE nome = 'Perifericos'
  AND NOT EXISTS (
      SELECT 1 FROM public.produtos WHERE nome = 'Mouse sem fio'
  )
RETURNING *;

INSERT INTO public.produtos (nome, preco, ativo, categoria_id)
SELECT 'Monitor 27 polegadas', 1899.90, TRUE, id
FROM public.categorias
WHERE nome = 'Monitores'
  AND NOT EXISTS (
      SELECT 1 FROM public.produtos WHERE nome = 'Monitor 27 polegadas'
  )
RETURNING *;

INSERT INTO public.produtos (nome, preco, ativo, categoria_id)
SELECT 'SSD 1TB', 499.90, TRUE, id
FROM public.categorias
WHERE nome = 'Armazenamento'
  AND NOT EXISTS (
      SELECT 1 FROM public.produtos WHERE nome = 'SSD 1TB'
  )
RETURNING *;

-- 3. Mostrar o relacionamento 1:N preenchido.
SELECT
    c.id AS categoria_id,
    c.nome AS categoria,
    p.id AS produto_id,
    p.nome AS produto,
    p.preco,
    p.ativo
FROM public.categorias c
LEFT JOIN public.produtos p
    ON p.categoria_id = c.id
ORDER BY c.nome, p.nome;

-- 4. Contar quantos produtos existem em cada categoria.
SELECT
    c.nome AS categoria,
    COUNT(p.id) AS quantidade_produtos
FROM public.categorias c
LEFT JOIN public.produtos p
    ON p.categoria_id = c.id
GROUP BY c.id, c.nome
ORDER BY c.nome;



############

-- Execute depois de 07_carga_passo_a_passo_relacionamentos.sql,
-- conectado ao database api_fundamentos.

-- Garantir a FK caso ainda não exista.
SET @fk_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'produtos'
      AND CONSTRAINT_NAME = 'fk_produtos_categorias'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_exists = 0,
    'ALTER TABLE produtos
        ADD CONSTRAINT fk_produtos_categorias
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL',
    'SELECT ''Foreign key fk_produtos_categorias já existe'''
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 1. Inserir categorias (UPSERT).

INSERT INTO categorias (nome, descricao)
VALUES ('Perifericos', 'Acessorios para computadores')
ON DUPLICATE KEY UPDATE
    descricao = VALUES(descricao);

INSERT INTO categorias (nome, descricao)
VALUES ('Monitores', 'Telas e equipamentos de exibicao')
ON DUPLICATE KEY UPDATE
    descricao = VALUES(descricao);


INSERT INTO categorias (nome, descricao)
VALUES ('Armazenamento', 'Discos e unidades de armazenamento')
ON DUPLICATE KEY UPDATE
    descricao = VALUES(descricao);


-- 2. Inserir produtos e associar às categorias.

INSERT INTO produtos (nome, preco, ativo, categoria_id)
SELECT 'Teclado mecanico', 299.90, TRUE, c.id
FROM categorias c
WHERE c.nome = 'Perifericos'
  AND NOT EXISTS (
      SELECT 1
      FROM produtos p
      WHERE p.nome = 'Teclado mecanico'
  );


INSERT INTO produtos (nome, preco, ativo, categoria_id)
SELECT 'Mouse sem fio', 149.90, TRUE, c.id
FROM categorias c
WHERE c.nome = 'Perifericos'
  AND NOT EXISTS (
      SELECT 1
      FROM produtos p
      WHERE p.nome = 'Mouse sem fio'
  );

INSERT INTO produtos (nome, preco, ativo, categoria_id)
SELECT 'Monitor 27 polegadas', 1899.90, TRUE, c.id
FROM categorias c
WHERE c.nome = 'Monitores'
  AND NOT EXISTS (
      SELECT 1
      FROM produtos p
      WHERE p.nome = 'Monitor 27 polegadas'
  );


INSERT INTO produtos (nome, preco, ativo, categoria_id)
SELECT 'SSD 1TB', 499.90, TRUE, c.id
FROM categorias c
WHERE c.nome = 'Armazenamento'
  AND NOT EXISTS (
      SELECT 1
      FROM produtos p
      WHERE p.nome = 'SSD 1TB'
  );

-- 3. Mostrar o relacionamento 1:N preenchido.

SELECT
    c.id AS categoria_id,
    c.nome AS categoria,
    p.id AS produto_id,
    p.nome AS produto,
    p.preco,
    p.ativo
FROM categorias c
LEFT JOIN produtos p
    ON p.categoria_id = c.id
ORDER BY c.nome, p.nome;

-- 4. Contar quantos produtos existem em cada categoria.

SELECT
    c.nome AS categoria,
    COUNT(p.id) AS quantidade_produtos
FROM categorias c
LEFT JOIN produtos p
    ON p.categoria_id = c.id
GROUP BY c.id, c.nome
ORDER BY c.nome;