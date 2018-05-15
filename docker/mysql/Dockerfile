FROM mysql:5.7

MAINTAINER jason_stedman@hms.harvard.edu

ADD 1-schema.sql /docker-entrypoint-initdb.d
ADD 2-views_and_procs.sql /docker-entrypoint-initdb.d
ADD 3-users.sql /docker-entrypoint-initdb.d

#RUN echo "lower_case_table_names=1" >> /etc/mysql/conf.d/mysql.cnf 

EXPOSE 3306
