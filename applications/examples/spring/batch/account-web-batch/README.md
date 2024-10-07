


View Jobs in Database


```sql
select ji.job_name , je.*  
from batch_job_execution je, batch_job_instance ji
where je.job_instance_id = ji.job_instance_id 
```


```sql
select * from taccounts.accounts;
 drop table taccounts.accounts;
```