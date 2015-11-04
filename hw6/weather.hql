drop table w1;

create external table w1 (data1 string, year int, data2 string, temperature int, quality tinyint, data3 string)
     row format delimited fields terminated by ','
     location '/user/ns3184/hiveInput/';
show tables;
describe w1;

select * from w1;

select * from w1 limit 2;

select year from w1;      

select * from w1 where year > 1949;

select * from w1 where year >= 1949;

select distinct year from w1;

-- select * from w1 group by year;

-- select w.year, w.temp from (select year, max(temperature) as temp from w1 group by year) w; 

create external table if not exists w2 (data1 string, year int, data2 string, temperature int, quality tinyint, nines int)
          row format delimited fields terminated by ','
          location '/user/ns3184/hiveInput/';

describe w2;

select * from w2;    -- Is this what you expected to see? Enter your answer on NYU Classes.

create external table if not exists w3 (data1 string, year int, data2 string, temperature int, quality tinyint, nines bigint)
          row format delimited fields terminated by ','
          location '/user/ns3184/hiveInput/';

select * from w3;    -- Is this what you expected to see?


select * from w2;    --Should fail because this table (metadata) was dropped.
select * from w3;    --We dropped table w2, but data and table w3.s metadata are still available.
select * from w1;    
