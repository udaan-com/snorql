package com.udaan.snorql.framework.job.model

import java.util.*

object QuartzProperties {
    val prop = Properties()

    init {
        //RMI configuration to make the client to connect to the Quartz server
        prop["org.quartz.scheduler.rmi.export"] = "true"
        prop["org.quartz.scheduler.rmi.createRegistry"] = "true"
        prop["org.quartz.scheduler.rmi.registryHost"] = "localhost"
        prop["org.quartz.scheduler.rmi.registryPort"] = "1099"
        prop["org.quartz.threadPool.class"] = "org.quartz.simpl.SimpleThreadPool"
        prop["org.quartz.threadPool.threadCount"] = "2"
        //Quartz Server Properties
        prop["quartz.scheduler.instanceName"] = "ServerScheduler"
        prop["org.quartz.scheduler.instanceId"] = "AUTO"
        prop["org.quartz.scheduler.skipUpdateCheck"] = "true"
        prop["org.quartz.scheduler.instanceId"] = "NON_CLUSTERED"
        prop["org.quartz.scheduler.jobFactory.class"] = "org.quartz.simpl.SimpleJobFactory"
        prop["org.quartz.jobStore.class"] = "org.quartz.impl.jdbcjobstore.JobStoreTX"
        prop["org.quartz.jobStore.driverDelegateClass"] = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate"
//        prop["org.quartz.jobStore.driverDelegateClass"] = "org.quartz.impl.jdbcjobstore.MSSQLDelegate"
        prop["org.quartz.jobStore.dataSource"] = "quartzDataSource"
        prop["org.quartz.jobStore.tablePrefix"] = "QRTZ_"
        prop["org.quartz.jobStore.isClustered"] = "false"
        //MYSQL DATABASE CONFIGURATION
        //If we do not specify this configuration, QUARTZ will use RAM(in-memory) to store jobs
        //Once we restart QUARTZ, the jobs will not be persisted
        // Configure your MySQL properties
        prop["org.quartz.dataSource.quartzDataSource.driver"] = "com.mysql.jdbc.Driver"
        prop["org.quartz.dataSource.quartzDataSource.URL"] = "jdbc:mysql://localhost:3306/quartz"
        prop["org.quartz.dataSource.quartzDataSource.user"] = "root"
        prop["org.quartz.dataSource.quartzDataSource.password"] = "mysql@0000"
        prop["org.quartz.dataSource.quartzDataSource.maxConnections"] = "2"
//        prop["org.quartz.dataSource.quartzDataSource.driver"] = "com.mysql.jdbc.Driver"
//        prop["org.quartz.dataSource.quartzDataSource.driver"] = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
//        prop["org.quartz.dataSource.quartzDataSource.URL"] = // "jdbc:sqlserver://uddevsql.database.windows.net:1433;database=db-test;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;Authentication=ActiveDirectoryIntegrated"
//             "jdbc:sqlserver:uddevsql.database.windows.net:1433;database=db-test;"
//        prop["org.quartz.dataSource.quartzDataSource.user"] = "udaan@uddevsql" // System.getenv("DB_USER").trim()
//        prop["org.quartz.dataSource.quartzDataSource.password"] = "Dev@rjun1" // System.getenv("DB_PASSWORD").trim()
//        prop["org.quartz.dataSource.quartzDataSource.maxConnections"] = "2"
    }
}