-- ==========================================
-- 1. SEQUENCES
-- ==========================================
CREATE SEQUENCE IF NOT EXISTS users_id_seq;
CREATE SEQUENCE IF NOT EXISTS conversation_id_seq;
CREATE SEQUENCE IF NOT EXISTS group_chat_id_seq;
CREATE SEQUENCE IF NOT EXISTS group_admin_id_seq;
CREATE SEQUENCE IF NOT EXISTS message_id_seq;
CREATE SEQUENCE IF NOT EXISTS message_image_id_seq;
CREATE SEQUENCE IF NOT EXISTS audit_log_id_seq;

-- ==========================================
-- 2. TABELAS INDEPENDENTES
-- ==========================================

-- Tabela: public.users
CREATE TABLE IF NOT EXISTS public.users
(
    id bigint NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    fullname character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- Tabela: public.chat_group
CREATE TABLE IF NOT EXISTS public.chat_group
(
    id bigint NOT NULL DEFAULT nextval('group_chat_id_seq'::regclass),
    name character varying(255) NOT NULL,
    CONSTRAINT chat_group_pkey PRIMARY KEY (id)
);

-- Tabela: public.audit_log
CREATE TABLE IF NOT EXISTS public.audit_log
(
    id bigint NOT NULL DEFAULT nextval('audit_log_id_seq'::regclass),
    action character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    CONSTRAINT audit_log_pkey PRIMARY KEY (id)
);

-- ==========================================
-- 3. TABELAS DEPENDENTES (CHAVES ESTRANGEIRAS)
-- ==========================================

-- Tabela: public.conversation
CREATE TABLE IF NOT EXISTS public.conversation
(
    id bigint NOT NULL DEFAULT nextval('conversation_id_seq'::regclass),
    user1_id bigint NOT NULL,
    user2_id bigint NOT NULL,
    CONSTRAINT conversation_pkey PRIMARY KEY (id),
    CONSTRAINT conversation_user1_id_fkey FOREIGN KEY (user1_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT conversation_user2_id_fkey FOREIGN KEY (user2_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Tabela: public.group_admin
CREATE TABLE IF NOT EXISTS public.group_admin
(
    id bigint NOT NULL DEFAULT nextval('group_admin_id_seq'::regclass),
    group_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT group_admin_pkey PRIMARY KEY (id),
    CONSTRAINT unique_group_user UNIQUE (group_id, user_id),
    CONSTRAINT fk_group FOREIGN KEY (group_id)
        REFERENCES public.chat_group (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);

-- Tabela: public.group_members
CREATE TABLE IF NOT EXISTS public.group_members
(
    group_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT group_members_pkey PRIMARY KEY (group_id, user_id),
    CONSTRAINT group_members_group_id_fkey FOREIGN KEY (group_id)
        REFERENCES public.chat_group (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE,
    CONSTRAINT group_members_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);

-- Tabela: public.message
CREATE TABLE IF NOT EXISTS public.message
(
    id bigint NOT NULL DEFAULT nextval('message_id_seq'::regclass),
    conversation_id bigint,
    sender_id bigint NOT NULL,
    content text NOT NULL,
    "timestamp" bigint NOT NULL,
    sender_email character varying(255) NOT NULL,
    group_id bigint,
    CONSTRAINT message_pkey PRIMARY KEY (id),
    CONSTRAINT message_conversation_id_fkey FOREIGN KEY (conversation_id)
        REFERENCES public.conversation (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT message_group_id_fkey FOREIGN KEY (group_id)
        REFERENCES public.chat_group (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE,
    CONSTRAINT message_sender_id_fkey FOREIGN KEY (sender_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Tabela: public.message_image
CREATE TABLE IF NOT EXISTS public.message_image
(
    id bigint NOT NULL DEFAULT nextval('message_image_id_seq'::regclass),
    url character varying(255) NOT NULL,
    message_id bigint,
    CONSTRAINT pk_message_image PRIMARY KEY (id),
    CONSTRAINT fk_message FOREIGN KEY (message_id)
        REFERENCES public.message (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);


-- ==========================================
-- 4. POVOAMENTO DE DADOS (POPULATE)
-- ==========================================

-- Inserindo Usuários (Senhas fictícias em texto simples/hash para teste)
-- INSERT INTO public.users (fullname, email, password) VALUES
-- ('Carlos Silva', 'carlos@linktalk.com', 'senha123'),
-- ('Amanda Souza', 'amanda@linktalk.com', 'senha456'),
-- ('Bruno Lima', 'bruno@linktalk.com', 'senha789');

-- -- Criando uma conversa privada entre Carlos (id 1) e Amanda (id 2)
-- INSERT INTO public.conversation (user1_id, user2_id) VALUES (1, 2);

-- -- Criando um Grupo de Chat
-- INSERT INTO public.chat_group (name) VALUES ('Equipe de Desenvolvimento');

-- -- Adicionando Administrador do Grupo (Carlos - id 1 no Grupo 1)
-- INSERT INTO public.group_admin (group_id, user_id) VALUES (1, 1);

-- -- Adicionando Membros ao Grupo (Carlos, Amanda e Bruno no Grupo 1)
-- INSERT INTO public.group_members (group_id, user_id) VALUES 
-- (1, 1),
-- (1, 2),
-- (1, 3);

-- -- Inserindo Mensagens de Teste
-- -- Mensagem na conversa privada (id 1) enviada por Carlos
-- INSERT INTO public.message (conversation_id, sender_id, content, "timestamp", sender_email, group_id) VALUES
-- (1, 1, 'Olá Amanda, tudo bem?', 1711111111000, 'carlos@linktalk.com', NULL);

-- -- Mensagem no grupo (id 1) enviada por Bruno
-- INSERT INTO public.message (conversation_id, sender_id, content, "timestamp", sender_email, group_id) VALUES
-- (NULL, 3, 'Pessoal, a reunião começou!', 1711111222000, 'bruno@linktalk.com', 1);

-- -- Inserindo uma imagem atrelada à mensagem do Bruno (id da mensagem será 2)
-- INSERT INTO public.message_image (url, message_id) VALUES
-- ('https://linktalk-bucket.s3.amazonaws.com/prints/reuniao.png', 2);

-- -- Inserindo log de auditoria inicial
-- INSERT INTO public.audit_log (action, username, "timestamp") VALUES
-- ('DATABASE_INITIALIZATION', 'system_initializer', CURRENT_TIMESTAMP);