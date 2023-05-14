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
-- Name: article; Type: TABLE; Schema: public; Owner: windows_tester
--

CREATE TABLE public.article (
                                id bigint NOT NULL,
                                added_at timestamp(6) without time zone,
                                content character varying(255),
                                headline character varying(255),
                                slug character varying(255),
                                title character varying(255),
                                author_username character varying(255)
);


ALTER TABLE public.article OWNER TO windows_tester;

--
-- Name: article_seq; Type: SEQUENCE; Schema: public; Owner: windows_tester
--

CREATE SEQUENCE public.article_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.article_seq OWNER TO windows_tester;

--
-- Name: authorities; Type: TABLE; Schema: public; Owner: windows_tester
--

CREATE TABLE public.authorities (
                                    username character varying(50) NOT NULL,
                                    authority character varying(50) NOT NULL
);


ALTER TABLE public.authorities OWNER TO windows_tester;

--
-- Name: users; Type: TABLE; Schema: public; Owner: windows_tester
--

CREATE TABLE public.users (
                              username character varying(255) NOT NULL,
                              enabled boolean NOT NULL,
                              password character varying(255) NOT NULL
);


ALTER TABLE public.users OWNER TO windows_tester;

--
-- Data for Name: article; Type: TABLE DATA; Schema: public; Owner: windows_tester
--

COPY public.article (id, added_at, content, headline, slug, title, author_username) FROM stdin;
1	2023-05-14 10:39:13.683961	dolor sit amet	Lorem	lorem	Lorem	user
2	2023-05-14 10:39:13.730972	dolor sit amet	Ipsum	ipsum	Ipsum	user
\.


--
-- Data for Name: authorities; Type: TABLE DATA; Schema: public; Owner: windows_tester
--

COPY public.authorities (username, authority) FROM stdin;
admin	ROLE_ADMIN
admin	ROLE_USER
user	ROLE_USER
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: windows_tester
--

COPY public.users (username, enabled, password) FROM stdin;
admin	t	$2a$10$6MN7h/YNEBbrNaiRioYBre1hdZNYFbHdtrGSXFNsRlRXN3kCCv74e
user	t	$2a$10$fSWw5hjfa2HBNRaNafzAH.hgVa3YDx3ZK7TT5mbR08FxTnqIO34Mq
\.


--
-- Name: article_seq; Type: SEQUENCE SET; Schema: public; Owner: windows_tester
--

SELECT pg_catalog.setval('public.article_seq', 101, true);


--
-- Name: article article_pkey; Type: CONSTRAINT; Schema: public; Owner: windows_tester
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT article_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: windows_tester
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);


--
-- Name: ix_auth_username; Type: INDEX; Schema: public; Owner: windows_tester
--

CREATE UNIQUE INDEX ix_auth_username ON public.authorities USING btree (username, authority);


--
-- Name: authorities fk_authorities_users; Type: FK CONSTRAINT; Schema: public; Owner: windows_tester
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES public.users(username);


--
-- Name: article fkd7fuj7re5bai6rvacjtef5ghy; Type: FK CONSTRAINT; Schema: public; Owner: windows_tester
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT fkd7fuj7re5bai6rvacjtef5ghy FOREIGN KEY (author_username) REFERENCES public.users(username);


--
-- PostgreSQL database dump complete
--

