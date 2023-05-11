--
-- PostgreSQL database dump
--

-- Dumped from database version 14.5
-- Dumped by pg_dump version 14.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: article; Type: TABLE; Schema: public; Owner: db_creator
--

CREATE TABLE public.article (
    id bigint NOT NULL,
    added_at timestamp(6) without time zone,
    content character varying(255),
    headline character varying(255),
    slug character varying(255),
    title character varying(255),
    author_id bigint
);


ALTER TABLE public.article OWNER TO db_creator;

--
-- Name: article_seq; Type: SEQUENCE; Schema: public; Owner: db_creator
--

CREATE SEQUENCE public.article_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.article_seq OWNER TO db_creator;

--
-- Name: user; Type: TABLE; Schema: public; Owner: db_creator
--

CREATE TABLE public."user" (
    id bigint NOT NULL,
    description character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    login character varying(255)
);


ALTER TABLE public."user" OWNER TO db_creator;

--
-- Name: user_seq; Type: SEQUENCE; Schema: public; Owner: db_creator
--

CREATE SEQUENCE public.user_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_seq OWNER TO db_creator;

--
-- Data for Name: article; Type: TABLE DATA; Schema: public; Owner: db_creator
--

COPY public.article (id, added_at, content, headline, slug, title, author_id) FROM stdin;
1	2023-05-09 18:56:18.18165	dolor sit amet	Lorem	lorem	Lorem	1
2	2023-05-09 18:56:18.189343	dolor sit amet	Ipsum	ipsum	Ipsum	1
102	2023-05-09 19:42:29.663244	It was created by Postman.	You won't believe how this article was created!	postman-article	Postman article	1
202	2023-05-09 22:30:03.702372	It was created by Postman.	To be updated...	postman-article-2	Postman article 2	1
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: db_creator
--

COPY public."user" (id, description, firstname, lastname, login) FROM stdin;
1	\N	John	Doe	johnDoe
2	\N	Terry	Smith	terrySmith
752	\N	Docker	Composer	dockerComposer
\.


--
-- Name: article_seq; Type: SEQUENCE SET; Schema: public; Owner: db_creator
--

SELECT pg_catalog.setval('public.article_seq', 851, true);


--
-- Name: user_seq; Type: SEQUENCE SET; Schema: public; Owner: db_creator
--

SELECT pg_catalog.setval('public.user_seq', 951, true);


--
-- Name: article article_pkey; Type: CONSTRAINT; Schema: public; Owner: db_creator
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT article_pkey PRIMARY KEY (id);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: db_creator
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: article FKfhk3yc24nq2uawud4m6pd89q2; Type: FK CONSTRAINT; Schema: public; Owner: db_creator
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT "FKfhk3yc24nq2uawud4m6pd89q2" FOREIGN KEY (author_id) REFERENCES public."user"(id);


--
-- PostgreSQL database dump complete
--

