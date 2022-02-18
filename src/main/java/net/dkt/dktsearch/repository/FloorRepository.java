package net.dkt.dktsearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dkt.dktsearch.model.Floor;
import net.dkt.dktsearch.model.Genre;

public interface FloorRepository extends JpaRepository<Floor, Integer> {

}
