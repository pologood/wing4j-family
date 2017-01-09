```global params
--@dialect wing4j
--@namespace org.wing4j.orm
```
[��ѯ�û���Ϣ](selectDemo)
================================
```params
--@flushCacheRequired=true
--@useCache=false
--@fetchSize=1
--@timeout=1000
--@comment=����ע��
```
```sql
select * 
from tb_demo_wing4j_inf t
/*#def 'A'*/
where t.col1=$col2:VRACHAR$
/*#if col2 is not null*/
/*#def 'A'*/
and col2=$col2:VRACHAR$
/*#fi*/
/*#if col3 is not null*/
/*#def 'A'*/
and col3=$col3:NUMBER$
/*#fi*/
```


[�����û�ID��������](updateById)
================================
```sql
update table t
/*#def 'A'*/
set t.col1 = $col2:VRACHAR$
where t.col1='col1'
/*#if col2 is not null*/
/*#def 'A'*/
and col2=$col2:VRACHAR$
/*#fi*/
/*#if col3 is not null*/
/*#def 'A'*/
and col3=$col3:NUMBER$
/*#fi*/
```
