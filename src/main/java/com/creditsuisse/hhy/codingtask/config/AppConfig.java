package com.creditsuisse.hhy.codingtask.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server hsqlServer(@Value("classpath:/hsqldb.properties") Resource props) throws IOException, ServerAcl.AclFormatException {
        Server bean = new Server();
        bean.setProperties(new HsqlProperties(PropertiesLoaderUtils.loadProperties(props)));
        return bean;
    }

    @Bean
    @DependsOn("hsqlServer")
    public DataSource getDataSource(
            @Autowired DataSourceProperties dsProps) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(dsProps.getDriverClassName());
        dataSourceBuilder.url(dsProps.getUrl());
        dataSourceBuilder.username(dsProps.getUsername());
        dataSourceBuilder.password(dsProps.getPassword());
        return dataSourceBuilder.build();
    }
}
