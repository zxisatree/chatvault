--
-- PostgreSQL database dump
--

-- Dumped from database version 14.5
-- Dumped by pg_dump version 14.5

SET
statement_timeout = 0;
SET
lock_timeout = 0;
SET
idle_in_transaction_session_timeout = 0;
SET
client_encoding = ''UTF8'';
SET
standard_conforming_strings = on;
SELECT pg_catalog.set_config(''search_path'', '''', false);
SET
check_function_bodies = false;
SET
xmloption = content;
SET
client_min_messages = warning;
SET
row_security = off;

SET
default_tablespace = '''';

SET
default_table_access_method = heap;

--
-- Name: article; Type: TABLE; Schema: public; Owner: compose_or_else
--

CREATE TABLE public.article
(
    id        bigint NOT NULL,
    added_at  timestamp(6) without time zone,
    content   character varying(255),
    headline  character varying(255),
    slug      character varying(255),
    title     character varying(255),
    author_id bigint
);


ALTER TABLE public.article OWNER TO compose_or_else;

--
-- Name: article_seq; Type: SEQUENCE; Schema: public; Owner: compose_or_else
--

CREATE SEQUENCE public.article_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.article_seq OWNER TO compose_or_else;

--
-- Name: db_user; Type: TABLE; Schema: public; Owner: compose_or_else
--

CREATE TABLE public.db_user
(
    id       bigint NOT NULL,
    login    character varying(255),
    password character varying(255)
);


ALTER TABLE public.db_user OWNER TO compose_or_else;

--
-- Name: db_user_seq; Type: SEQUENCE; Schema: public; Owner: compose_or_else
--

CREATE SEQUENCE public.db_user_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.db_user_seq OWNER TO compose_or_else;

--
-- Data for Name: article; Type: TABLE DATA; Schema: public; Owner: compose_or_else
--

COPY public.article (id, added_at, content, headline, slug, title, author_id) FROM stdin;
1	2023-05-12 21:18:27.871862	dolor sit amet	Lorem	lorem	Lorem	1
2	2023-05-12 21:18:27.879881	dolor sit amet	Ipsum	ipsum	Ipsum	1
\.


--
-- Data for Name: db_user; Type: TABLE DATA; Schema: public; Owner: compose_or_else
--

COPY public.db_user (id, login, password) FROM stdin;
1	johnDoe	password
\.


--
-- Name: article_seq; Type: SEQUENCE SET; Schema: public; Owner: compose_or_else
--

SELECT pg_catalog.setval(''public.article_seq'', 301, true);


--
-- Name: db_user_seq; Type: SEQUENCE SET; Schema: public; Owner: compose_or_else
--

SELECT pg_catalog.setval(''public.db_user_seq'', 251, true);


--
-- Name: article article_pkey; Type: CONSTRAINT; Schema: public; Owner: compose_or_else
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT article_pkey PRIMARY KEY (id);


--
-- Name: db_user db_user_pkey; Type: CONSTRAINT; Schema: public; Owner: compose_or_else
--

ALTER TABLE ONLY public.db_user
    ADD CONSTRAINT db_user_pkey PRIMARY KEY (id);


--
-- Name: article FK1ltag0qcdodojepd6jqsxijht; Type: FK CONSTRAINT; Schema: public; Owner: compose_or_else
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT "FK1ltag0qcdodojepd6jqsxijht" FOREIGN KEY (author_id) REFERENCES public.db_user(id);


--
-- PostgreSQL database dump complete
--

