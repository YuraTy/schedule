package com.foxminded.dao;

import com.foxminded.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@Repository
public class CourseDaoImpl implements CourseDao {

    @Autowired
    private  JdbcTemplate jdbcTemplate;

    @Override
    public Course create(Course course) {
        String sqlInquiry = "INSERT INTO courses (name_course) VALUES (?)";
        jdbcTemplate.update(sqlInquiry, course.getNameCourse());
        return course;
    }

    @Override
    public List<Course> findAll() {
        String sqlInquiry = "SELECT name_course,id FROM courses";
        return jdbcTemplate.query(sqlInquiry, BeanPropertyRowMapper.newInstance(Course.class));
    }

    @Override
    public Course update(Course courseNew, Course courseOld) {
        String sqlInquiry = "UPDATE courses SET name_course = ? WHERE name_course = ?";
        jdbcTemplate.update(sqlInquiry, courseOld.getNameCourse(), courseNew.getNameCourse());
        return courseNew;
    }

    @Override
    public void delete(Course course) {
        String sqlInquiry = "DELETE FROM courses WHERE name_course = ?";
        jdbcTemplate.update(sqlInquiry, course.getNameCourse());
    }

    @PostConstruct
    public void creteTable() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("createTableCourses.sql"));
        resourceDatabasePopulator.execute(jdbcTemplate.getDataSource());
    }
}
