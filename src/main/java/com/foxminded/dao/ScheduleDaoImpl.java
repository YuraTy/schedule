package com.foxminded.dao;

import com.foxminded.model.Classroom;
import com.foxminded.model.Course;
import com.foxminded.model.Group;
import com.foxminded.model.Schedule;
import com.foxminded.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class ScheduleDaoImpl implements ScheduleDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Schedule create(Schedule schedule) {
        String sqlInquiry = "INSERT INTO schedule (group_id,teacher_id,course_id,classroom_id,lesson_start_time,lesson_end_time) VALUES (?,?,?,?,?,?)";
        int groupId = schedule.getGroup().getGroupId();
        int teacherId = schedule.getTeacher().getTeacherId();
        int courseId = schedule.getCourse().getCourseId();
        int classroomId = schedule.getClassroom().getClassroomId();
        LocalDateTime startTime = schedule.getLessonStartTime();
        LocalDateTime endTime = schedule.getLessonEndTime();
        jdbcTemplate.update(sqlInquiry, groupId, teacherId, courseId, classroomId, startTime, endTime);
        return schedule;
    }

    @Override
    public List<Schedule> findAll() {
        String sqlInquiry = "SELECT schedule.schedule_id,classrooms.id,classrooms.number_classroom,courses.id,courses.name_course,groups.id,groups.name_group,teachers.id,teachers.first_name,teachers.last_name,schedule.lesson_start_time,schedule.lesson_end_time\n" +
                "FROM schedule schedule\n" +
                "INNER JOIN classrooms classrooms ON classrooms.id = schedule.classroom_id\n" +
                "INNER JOIN courses courses ON courses.id = schedule.course_id\n" +
                "INNER JOIN groups groups ON groups.id = schedule.group_id\n" +
                "INNER JOIN teachers teachers ON teachers.id = schedule.teacher_id";
        return jdbcTemplate.query(sqlInquiry, new RowMapper<Schedule>() {
            @Override
            public Schedule mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return Schedule.builder()
                        .group(buildGroup(resultSet))
                        .teacher(buildTeacher(resultSet))
                        .classroom(buildClassroom(resultSet))
                        .course(buildCourse(resultSet))
                        .lessonStartTime(buildStartTime(resultSet))
                        .lessonEndTime(buildEndTime(resultSet))
                        .scheduleId(buildScheduleId(resultSet))
                        .build();
            }
        });
    }

    @Override
    public List<Schedule> takeScheduleToTeacher(Teacher teacher) {
        int teacherId = teacher.getTeacherId();
        String sqlInquiry = "SELECT schedule.schedule_id,classrooms.id,classrooms.number_classroom,courses.id,courses.name_course,groups.id,groups.name_group,teachers.id,teachers.first_name,teachers.last_name,schedule.lesson_start_time,schedule.lesson_end_time\n" +
                "FROM schedule schedule\n" +
                "INNER JOIN classrooms classrooms ON classrooms.id = schedule.classroom_id\n" +
                "INNER JOIN courses courses ON courses.id = schedule.course_id\n" +
                "INNER JOIN groups groups ON groups.id = schedule.group_id\n" +
                "INNER JOIN teachers teachers ON teachers.id = schedule.teacher_id\n" +
                "WHERE schedule.teacher_id = ?";
        return jdbcTemplate.query(sqlInquiry, new RowMapper<Schedule>() {
            @Override
            public Schedule mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return Schedule.builder()
                        .group(buildGroup(resultSet))
                        .teacher(buildTeacher(resultSet))
                        .classroom(buildClassroom(resultSet))
                        .course(buildCourse(resultSet))
                        .lessonStartTime(buildStartTime(resultSet))
                        .lessonEndTime(buildEndTime(resultSet))
                        .scheduleId(buildScheduleId(resultSet))
                        .build();
            }
        }, teacherId);
    }

    @Override
    public Schedule update(Schedule scheduleNew, Schedule scheduleOld) {
        String sqlInquiry = "UPDATE schedule SET  group_id = ?,teacher_id = ?,course_id = ?,classroom_id = ?,lesson_start_time = ?,lesson_end_time = ?\n" +
                "WHERE schedule_id = ?;";
        int scheduleOldId = scheduleOld.getScheduleId();
        int groupId = scheduleNew.getGroup().getGroupId();
        int teacherId = scheduleNew.getTeacher().getTeacherId();
        int courseId = scheduleNew.getCourse().getCourseId();
        int classroomId = scheduleNew.getClassroom().getClassroomId();
        LocalDateTime startTime = scheduleNew.getLessonStartTime();
        LocalDateTime endTime = scheduleNew.getLessonEndTime();
        jdbcTemplate.update(sqlInquiry, groupId, teacherId, courseId, classroomId, startTime, endTime, scheduleOldId);
        return scheduleNew;
    }

    @Override
    public void delete(Schedule schedule) {
        int scheduleId = schedule.getScheduleId();
        String sqlInquiry = "DELETE FROM schedule WHERE schedule_id = ? ";
        jdbcTemplate.update(sqlInquiry, scheduleId);
    }

    @PostConstruct
    public void creteTable() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("createTableSchedule.sql"));
        resourceDatabasePopulator.execute(jdbcTemplate.getDataSource());
    }

    private Group buildGroup(ResultSet resultSet) throws SQLException {
        return new Group(resultSet.getString("groups.name_group"),resultSet.getInt("groups.id"));
    }

    private Teacher buildTeacher(ResultSet resultSet) throws SQLException {
        return  new Teacher(resultSet.getString("teachers.first_name"),resultSet.getString("teachers.last_name"),resultSet.getInt("teachers.id"));
    }

    private Course buildCourse(ResultSet resultSet) throws SQLException {
        return new Course(resultSet.getString("courses.name_course"),resultSet.getInt("courses.id"));
    }

    private Classroom buildClassroom(ResultSet resultSet) throws SQLException {
        return new Classroom(resultSet.getInt("classrooms.number_classroom"),resultSet.getInt("classrooms.id"));
    }

    private static final String DATA_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private LocalDateTime buildStartTime(ResultSet resultSet) throws SQLException {
        return LocalDateTime.parse(resultSet.getString("schedule.lesson_start_time"), DateTimeFormatter.ofPattern(DATA_PATTERN));
    }

    private LocalDateTime buildEndTime(ResultSet resultSet) throws SQLException {
        return LocalDateTime.parse(resultSet.getString("schedule.lesson_end_time"), DateTimeFormatter.ofPattern(DATA_PATTERN));
    }

    private int buildScheduleId(ResultSet resultSet) throws SQLException {
        return resultSet.getInt("schedule.schedule_id");
    }

}
