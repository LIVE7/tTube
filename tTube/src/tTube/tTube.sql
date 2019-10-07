-------------------------------------

--tTubeMemo 테이블 생성

create table tTubeMemo(
	idx number(4,0),
	tname varchar2(30),
	tmemo varchar2(95),
	tdate date,
	bjname varchar2(30)
);

-------------------------------------

--ttube_seq 시퀀스 생성

create SEQUENCE ttube_seq
START WITH 1;