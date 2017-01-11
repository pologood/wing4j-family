```configure
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
--@comment=��ע
--@f
```
```sql
select * 
from tb_demo_wing4j_inf t
where t.col1=#{col2:VRACHAR}
/*#     if col2 == null                  */
and col2=#{col2:VRACHAR}
/*#     fi                               */
/*#     if col3 is not null              */
and col3=#{col2:NUMBER}
/*#     fi                               */
```


[�����û�ID��������](updateById)
================================

```sql
update table t
set t.col1 = #{col2:VRACHAR}
where t.col1='col1'
/*#     if col2 is not null              */
and col2=$col2:VRACHAR$
/*#     fi                               */
/*#     if col3 is not null              */
and col3=$col3:NUMBER$
/*#     fi                               */
```
