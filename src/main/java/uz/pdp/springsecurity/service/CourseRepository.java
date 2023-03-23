package uz.pdp.springsecurity.service;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Course;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
}
