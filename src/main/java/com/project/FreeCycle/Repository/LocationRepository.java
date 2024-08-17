package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Location save(Location location);

    void deleteByUser_id(long user_id);
}
