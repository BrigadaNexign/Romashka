package rom.brt.repository;

import org.springframework.stereotype.Repository;
import rom.brt.entity.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {
}