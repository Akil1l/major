package com.example.demo.mapper;

import java.util.Map;

import javax.annotation.Resource;

import com.example.demo.handler.MyResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * 用于session查询
 *
 * @author jiangliuhong
 */
@Repository
public class SessionMapper extends SqlSessionDaoSupport {

    @Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDataSet() {
        MyResultHandler handler = new MyResultHandler();
        //namespace : XxxMapper.xml 中配置的地址（XxxMapper.xml的qualified name）
        //.selectXxxxNum : XxxMapper.xml 中配置的方法名称
        //this.getSqlSession().select(namespace+".selectXxxxNum", handler);
        this.getSqlSession().select(CmGuoMapper.class.getName() + ".getDataSet", handler);
        Map<String, Object> map = handler.getMappedResults();
        return map;
    }
}